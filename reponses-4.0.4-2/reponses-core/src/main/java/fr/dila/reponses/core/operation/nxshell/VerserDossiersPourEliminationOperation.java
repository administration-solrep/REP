package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.transaction.Status;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Une opération pour éliminer les dossiers.
 */
@Operation(
    id = VerserDossiersPourEliminationOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "VerserDossiersPourElimination",
    description = "Verse les dossiers dans une liste d'élimination"
)
public class VerserDossiersPourEliminationOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Verser.Dossiers";

    /**
     * Logger.
     */
    private static final STLogger LOGGER = STLogFactory.getLog(VerserDossiersPourEliminationOperation.class);

    private static final String QUERY_DOSSIER_CANDIDAT =
        "select dossier_reponse.id as id from DOSSIER_REPONSE where IDDOCUMENTQUESTION in (select question.id from QUESTION where DATEPUBLICATIONJO < ?) and LISTEELIMINATION is null";

    private static final String QUERY_COUNT_DOSSIER_CANDIDAT =
        "select count(dossier_reponse.id) from DOSSIER_REPONSE where IDDOCUMENTQUESTION in (select question.id from QUESTION where DATEPUBLICATIONJO < ?) and LISTEELIMINATION is null";

    @Context
    protected CoreSession session;

    @Param(name = "nbDossiers", required = true)
    protected int nbDossiers;

    @OperationMethod
    public void run() {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_OPERATION_VERSER_DOSSIER_TEC, "Début opération " + ID);

        if (!checkParameters(nbDossiers)) {
            LOGGER.warn(
                session,
                ReponsesLogEnumImpl.FAIL_OPERATION_VERSER_DOSSIER_TEC,
                "Les paramètres reçus ne sont pas valides"
            );
            return;
        }

        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String duaDelai = paramService.getParametreValue(session, STParametreConstant.DELAI_CONSERVATION_DONNEES);
        final Calendar dua = DateUtil.removeMonthsToNow(Integer.parseInt(duaDelai));

        Object[] params = { dua };

        int countDossiers = getNbDossiersCandidat(params);
        if (nbDossiers > countDossiers) {
            nbDossiers = countDossiers;
        }
        LOGGER.info(
            session,
            ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
            countDossiers + " dossiers candidats trouvés"
        );
        if (countDossiers > 0) {
            LOGGER.info(
                session,
                ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
                "Nombre de dossiers qui seront traités : " + nbDossiers
            );
            verserDossiers(params);
        }

        LOGGER.info(session, ReponsesLogEnumImpl.END_OPERATION_VERSER_DOSSIER_TEC, "Fin de l'opération" + ID);
        return;
    }

    private boolean checkParameters(int nbDossiers) {
        if (nbDossiers <= 0) {
            LOGGER.error(
                session,
                ReponsesLogEnumImpl.FAIL_OPERATION_VERSER_DOSSIER_TEC,
                "Le nombre de dossiers à verser ne peut pas être négatif ou nul"
            );
            return false;
        }

        return true;
    }

    private int getNbDossiersCandidat(Object[] params) {
        String[] colsCount = { FlexibleQueryMaker.COL_COUNT };
        IterableQueryResult resCount = QueryUtils.doSqlQuery(session, colsCount, QUERY_COUNT_DOSSIER_CANDIDAT, params);
        int countDossiers = 0;
        if (resCount != null) {
            Iterator<Map<String, Serializable>> iterator = resCount.iterator();
            if (iterator.hasNext()) {
                Map<String, Serializable> row = iterator.next();
                countDossiers = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
            }
            resCount.close();
        }

        return countDossiers;
    }

    private void verserDossiers(Object[] params) {
        final String[] colsQuery = { FlexibleQueryMaker.COL_ID };
        long limit = nbDossiers < 100 ? nbDossiers : 100;
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

        List<DocumentModel> dossierDocs = new ArrayList<>();
        List<Dossier> dossierErreurs = new ArrayList<>();
        String dossierId = null;
        Long borne = 0L;
        IterableQueryResult res = null;
        Iterator<Map<String, Serializable>> iterator = null;
        Map<String, Serializable> row = null;

        for (Long offset = (long) 0; offset < nbDossiers; offset += limit) {
            dossierDocs.clear();
            borne = (nbDossiers < offset + limit ? nbDossiers : offset + limit);
            LOGGER.info(
                session,
                ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
                "Récupération dossiers de " + offset + " à " + borne
            );
            res = QueryUtils.doSqlQuery(session, colsQuery, QUERY_DOSSIER_CANDIDAT, params, limit, 0);
            if (res != null) {
                iterator = res.iterator();
                while (iterator.hasNext()) {
                    row = iterator.next();
                    dossierId = (String) row.get(FlexibleQueryMaker.COL_ID);
                    IdRef dossierRef = new IdRef(dossierId);
                    if (session.exists(dossierRef)) {
                        dossierDocs.add(session.getDocument(dossierRef));
                    }
                }
                iterator = null;
                dossierErreurs.addAll(archiveService.ajouterDossiersListeElimination(session, dossierDocs));
                session.save();
                res.close();
                TransactionHelper.commitOrRollbackTransaction();
                if (!isTransactionAlive()) {
                    TransactionHelper.startTransaction();
                }
            }

            LOGGER.info(
                session,
                ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
                "Fin traitement dossiers de " + offset + " à " + borne
            );
        }

        if (!dossierErreurs.isEmpty()) {
            LOGGER.warn(
                session,
                ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
                "Dossiers n'ayant pu être ajoutés à la liste d'élimination : " + dossierErreurs.size()
            );
            for (Dossier dos : dossierErreurs) {
                Question question = dos.getQuestion(session);
                LOGGER.warn(
                    session,
                    ReponsesLogEnumImpl.PROCESS_OPERATION_VERSER_DOSSIER_TEC,
                    "Dossier " +
                    question.getSourceNumeroQuestion() +
                    " - législature : " +
                    question.getLegislatureQuestion()
                );
            }
        }
    }

    private boolean isTransactionAlive() {
        try {
            return TransactionHelper.lookupUserTransaction().getStatus() != Status.STATUS_NO_TRANSACTION;
        } catch (Exception e) {
            return false;
        }
    }
}
