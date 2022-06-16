package fr.dila.reponses.core.event.work;

import static fr.dila.st.api.constant.STSchemaConstant.ROUTING_TASK_SCHEMA;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.work.AbstractBatchProcessorStep;
import fr.dila.st.core.event.work.StepContext;
import fr.dila.st.core.event.work.StepDTO;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class QuestionReminderEventStep extends AbstractBatchProcessorStep<StepDTO> {
    private static final long serialVersionUID = -6456372233864739940L;

    private static final String OBJET = "REPONSES : QE en attente de traitement";
    private static final String CONTENT =
        "La QE n°%s du %s est en attente de traitement dans votre corbeille depuis %d jours,";

    private static final String CONTENT_FINAL = "Merci de traiter ces questions dans les meilleurs délais";

    public QuestionReminderEventStep(StepContext stepContext, List<StepDTO> items) {
        super(QuestionReminderEventStep.class, stepContext, items);
    }

    @Override
    protected void processItem(CoreSession session, StepDTO poste) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        LocalDate now = LocalDate.now();
        LocalDate delaiTraitementEtape = now.minusDays(
            Integer.parseInt(
                paramService.getParametreValue(session, ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_NAME)
            )
        );

        MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        PosteNode posteNode = STServiceLocator
            .getSTPostesService()
            .getPoste(mailboxPosteService.getPosteIdFromMailboxId(poste.getPosteId()));
        Set<STUser> uniqueUsers = new TreeSet<>(posteNode.getUserList());
        StringBuilder sb = new StringBuilder();
        boolean isQEnSouffrancesExiste = false;

        DocumentModelList docs = QueryHelper.getDocuments(session, poste.getStepIds(), ROUTING_TASK_SCHEMA);
        for (DocumentModel doc : docs) {
            SSRouteStep stStep = doc.getAdapter(SSRouteStep.class);

            LocalDate stStepAsLocalDate = DateUtil.gregorianCalendarToLocalDate(stStep.getDateDebutEtape());

            if (stStep.isRunning() && stStepAsLocalDate.compareTo(delaiTraitementEtape) <= 0) {
                long since = ChronoUnit.DAYS.between(stStepAsLocalDate, now);

                Dossier dossier = SSServiceLocator
                    .getDossierDistributionService()
                    .getDossierFromDocumentRouteId(session, stStep.getDocumentRouteId())
                    .getAdapter(Dossier.class);

                Question question = ReponsesServiceLocator
                    .getDossierService()
                    .getDossierDirecteur(session, dossier)
                    .orElse(dossier)
                    .getQuestion(session);

                sb
                    .append(String.format(CONTENT, question.getNumeroQuestion(), question.getOrigineQuestion(), since))
                    .append("<br>");

                uniqueUsers.addAll(
                    STServiceLocator
                        .getSTMinisteresService()
                        .getAdminsMinisterielsFromMinistere(stStep.getMinistereId())
                );

                isQEnSouffrancesExiste = true;
            }
        }
        if (isQEnSouffrancesExiste) {
            sb.append("<br>").append(CONTENT_FINAL);
            STServiceLocator.getSTMailService().sendMailToUserList(new ArrayList<>(uniqueUsers), OBJET, sb.toString());
        }
    }

    @Override
    protected String getBatchResultStepMessage() {
        return "Traitement des questions";
    }

    @Override
    protected String getBatchResultJobMessage() {
        return "Lanceur du traitement des questions en souffrance";
    }
}
