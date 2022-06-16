package fr.dila.reponses.core.operation.nxshell;

import com.google.common.collect.Lists;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.QuestionConnexeService.HashTarget;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.ui.web.auth.NuxeoAuthenticationFilter;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

@Operation(id = FEV541HomologationSha512Operation.ID, label = "FEV541HomologationSha512")
public class FEV541HomologationSha512Operation {
    public static final String ID = "Reponses.Update.FEV541HomologationSha512";

    protected static final Log LOG = LogFactory.getLog(FEV541HomologationSha512Operation.class);

    @OperationMethod
    public void run() {
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info("Début opération " + ID);

        TransactionHelper.commitOrRollbackTransaction();

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            doUpdate(session);
        }

        LOG.info("Fin opération " + ID);
    }

    public void doUpdate(CoreSession session) {
        final QuestionConnexeService questionConnexeService;
        final ConfigService configService;
        try {
            questionConnexeService = Framework.getService(QuestionConnexeService.class);
            configService = Framework.getService(ConfigService.class);
        } catch (Exception e) {
            LOG.error("Erreur de récupération du service de calcul de connexité ; questions non mise à jour");
            return;
        }
        Thread counterThread = null;
        try {
            final AtomicInteger counter = new AtomicInteger();
            LOG.info("Récupération des questions à mettre à jour");
            List<String> questionsIdsList = QueryUtils.doQueryForIds(
                session,
                String.format(
                    "SELECT * FROM %s ORDER BY %s",
                    DossierConstants.QUESTION_DOCUMENT_TYPE,
                    "qu:dateReceptionQuestion"
                )
            );
            LOG.info(String.format("%d questions à mettre à jour", questionsIdsList.size()));
            final Integer total = questionsIdsList.size();
            counterThread =
                new Thread(
                    new Runnable() {

                        @Override
                        public void run() {
                            long start = System.currentTimeMillis();
                            while (!Thread.currentThread().isInterrupted()) {
                                long current = System.currentTimeMillis();
                                int currentCounter = counter.get();
                                float speed = (float) currentCounter / (current - start) * 1000;
                                LOG.info(
                                    String.format(
                                        "Mise à jour SHA-512: %d/%d questions traitées (%.2f/s.)",
                                        currentCounter,
                                        total,
                                        speed
                                    )
                                );
                                try {
                                    Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                        }
                    }
                );
            counterThread.start();
            List<List<String>> lots = Lists.partition(questionsIdsList, 10);
            BlockingQueue<Runnable> runnables = new LinkedBlockingQueue<Runnable>();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 10000, TimeUnit.MILLISECONDS, runnables);
            for (final List<String> lot : lots) {
                // on traite les maj dans un thread séparé pour éviter les problèmes de session
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            doUpdateLot(lot, counter, configService, questionConnexeService);
                        } catch (Throwable e) {
                            LOG.error("Erreur de traitement d'un lot", e);
                        }
                    }
                };
                runnables.add(runnable);
            }
            executor.prestartAllCoreThreads();
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.HOURS);
        } catch (InterruptedException ie) {
            LOG.error("Interruption de la mise à jour des questions", ie);
        } finally {
            if (counterThread != null && counterThread.isAlive()) {
                counterThread.interrupt();
            }
        }
    }

    public void doUpdateLot(
        List<String> questionsIds,
        AtomicInteger counter,
        ConfigService configService,
        QuestionConnexeService questionConnexeService
    ) {
        LoginContext loginContext = null;
        String batchLogin = configService.getValue(STConfigConstants.NUXEO_BATCH_USER);

        try (CloseableCoreSession lotSession = SessionUtil.openSession()) {
            Exception initError = null;
            DocumentModelList questionsDocumentModelList = null;
            try {
                loginContext = NuxeoAuthenticationFilter.loginAs(batchLogin);
                TransactionHelper.startTransaction();

                List<DocumentRef> questionsRefs = new ArrayList<>();
                for (String questionId : questionsIds) {
                    questionsRefs.add(new IdRef(questionId));
                }

                questionsDocumentModelList = lotSession.getDocuments(questionsRefs.toArray(new DocumentRef[0]));
            } catch (LoginException e) {
                initError = e;
            }

            // interruption et mise à jour des statistiques si erreur d'initialisation
            if (initError != null) {
                throw new IllegalStateException(
                    String.format(
                        "Erreur de préparation d'un thread d'indexation; %d questions non traitées",
                        questionsIds.size()
                    ),
                    initError
                );
            }
            for (DocumentModel questionDocumentModel : questionsDocumentModelList) {
                if (doUpdateQuestion(lotSession, questionConnexeService, questionDocumentModel)) {
                    counter.incrementAndGet();
                }
            }
        } finally {
            TransactionHelper.commitOrRollbackTransaction();
            if (loginContext != null) {
                try {
                    loginContext.logout();
                } catch (LoginException e) {
                    throw new IllegalStateException("Erreur de logout d'un thread d'indexation", e);
                }
            }
        }
    }

    public boolean doUpdateQuestion(
        CoreSession session,
        QuestionConnexeService service,
        DocumentModel questionDocumentModel
    ) {
        Question question = questionDocumentModel.getAdapter(Question.class);
        try {
            String hashTitre = service.getHash(question, HashTarget.TITLE, session);
            String hashTexte = service.getHash(question, HashTarget.TEXTE, session);
            String hashIndexationAn = service.getHash(question, HashTarget.INDEXATION_AN, session);
            String hashIndexationSe = service.getHash(question, HashTarget.INDEXATION_SE, session);
            question.setHashConnexiteTitre(hashTitre);
            question.setHashConnexiteTexte(hashTexte);
            question.setHashConnexiteAN(hashIndexationAn);
            question.setHashConnexiteSE(hashIndexationSe);
            session.saveDocument(questionDocumentModel);
            return true;
        } catch (NuxeoException ce) {
            LOG.error(String.format("Erreur de calcul de la connexité de la question %s", question.getId()), ce);
            TransactionHelper.setTransactionRollbackOnly();
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Mécanisme de calcul SHA-512 absent", e);
        }
    }
}
