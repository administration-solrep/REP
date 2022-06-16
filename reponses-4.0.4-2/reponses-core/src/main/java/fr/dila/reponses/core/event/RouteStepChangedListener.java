package fr.dila.reponses.core.event;

import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener mettant à jour le label de prochaine étape dans le document dossier
 *
 *
 */
public class RouteStepChangedListener implements PostCommitEventListener {
    private static final Log log = LogFactory.getLog(RouteStepChangedListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        Set<DocumentModel> listeDocModifies = new HashSet<>();
        if (
            (events.containsEventName(STEventConstant.AFTER_SUBSTITUTION_FEUILLE_ROUTE)) ||
            (events.containsEventName(ReponsesEventConstant.DOSSIER_ARBITRAGE_SGG_EVENT)) ||
            (events.containsEventName(ReponsesEventConstant.DOSSIER_REATTRIBUTION_EVENT)) ||
            (events.containsEventName(ReponsesEventConstant.DOSSIER_REORIENTATION_EVENT)) ||
            (events.containsEventName(STEventConstant.DOSSIER_AVIS_DEFAVORABLE)) ||
            (events.containsEventName(STEventConstant.DOSSIER_AVIS_FAVORABLE)) ||
            (events.containsEventName(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE)) ||
            (events.containsEventName(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_DELETE)) ||
            (events.containsEventName(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_MOVE)) ||
            (events.containsEventName(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE)) ||
            (events.containsEventName(STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_EVENT)) ||
            (events.containsEventName(EventNames.afterCaseSentEvent.name())) ||
            (events.containsEventName(ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT))
        ) {
            Event event = null;
            for (Event evt : events) {
                if (
                    (evt.getName().equals(STEventConstant.AFTER_SUBSTITUTION_FEUILLE_ROUTE)) ||
                    (evt.getName().equals(ReponsesEventConstant.DOSSIER_ARBITRAGE_SGG_EVENT)) ||
                    (evt.getName().equals(ReponsesEventConstant.DOSSIER_REATTRIBUTION_EVENT)) ||
                    (evt.getName().equals(ReponsesEventConstant.DOSSIER_REORIENTATION_EVENT)) ||
                    (evt.getName().equals(STEventConstant.DOSSIER_AVIS_DEFAVORABLE)) ||
                    (evt.getName().equals(STEventConstant.DOSSIER_AVIS_FAVORABLE)) ||
                    (evt.getName().equals(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE)) ||
                    (evt.getName().equals(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_DELETE)) ||
                    (evt.getName().equals(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_MOVE)) ||
                    (evt.getName().equals(STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE)) ||
                    (evt.getName().equals(STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_EVENT)) ||
                    (evt.getName().equals(EventNames.afterCaseSentEvent.name())) ||
                    (evt.getName().equals(ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT))
                ) {
                    event = evt;
                    log.info("Mise à jour du label de prochaine étape : " + evt.getName());
                    final ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();

                    EventContext ctx = event.getContext();
                    if (ctx instanceof DocumentEventContext) {
                        DocumentEventContext context = (DocumentEventContext) ctx;
                        CoreSession session = ctx.getCoreSession();

                        // Traite uniquement les documents de type Dossier ou feuille de route
                        DocumentModel modelDoc = context.getSourceDocument();
                        String docType = modelDoc.getType();
                        if (listeDocModifies.add(modelDoc)) {
                            ReponsesFeuilleRoute fdr = null;
                            DocumentModel fdrDoc = null;
                            // Si le document modifié correspond à un dossier, on a juste besoin de modifier le label de
                            // prochaine étape de ce dernier
                            if (DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
                                Dossier dossier = modelDoc.getAdapter(Dossier.class);
                                fdrDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
                                fdr = fdrDoc.getAdapter(ReponsesFeuilleRoute.class);
                            } else if (SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(docType)) {
                                // Si le document modifié est une feuille de route, il faut modifier le label pour tous
                                // les dossiers rattachés à cette fdr
                                fdr = modelDoc.getAdapter(ReponsesFeuilleRoute.class);
                                fdrDoc = fdr.getDocument();
                            }

                            // vu que fdrDoc est initialisé à null et que les blocs de conditions précédentes ne
                            // regroupent pas tous les cas, on vérifie
                            // que fdrDoc a bien été initialisé
                            if (fdrDoc != null) {
                                // Récupération de l'étape ou des étapes courante(s)
                                List<DocumentModel> runningSteps = feuilleRouteService.getRunningSteps(
                                    session,
                                    fdrDoc.getId()
                                );
                                // Récupération du label des étapes suivantes
                                String label = getLabelFromRunningSteps(
                                    runningSteps,
                                    feuilleRouteService,
                                    session,
                                    fdrDoc.getId()
                                );
                                List<String> idsDossiersAttaches = fdr.getAttachedDocuments();
                                for (String id : idsDossiersAttaches) {
                                    Dossier dos = null;
                                    DocumentModel dosDoc = session.getDocument(new IdRef(id));
                                    dos = dosDoc.getAdapter(Dossier.class);
                                    if (dos != null) {
                                        log.info("MAJ dos : " + dos.getNumeroQuestion() + " label : " + label);
                                        dos.setLabelNextStep(label);
                                        dos.save(session);
                                        listeDocModifies.add(dosDoc);
                                    }
                                }
                            }
                            session.save();
                        }
                    }
                }
            }
        }
    }

    private String getLabelFromRunningSteps(
        List<DocumentModel> runningSteps,
        ReponseFeuilleRouteService feuilleRouteService,
        CoreSession session,
        String idFdr
    ) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        Set<String> labels = new HashSet<>();
        List<String> postesId = new ArrayList<>();
        List<DocumentModel> nextSteps = new ArrayList<>();

        if (runningSteps != null) {
            // Pour chaque étape en cours, on récupère l'étape qui suit
            for (DocumentModel runningStep : runningSteps) {
                nextSteps.addAll(feuilleRouteService.findNextSteps(session, idFdr, runningStep, null));
            }
            // Pour chaque étape qui suit, on récupère le label du poste associé
            if (nextSteps != null) {
                for (DocumentModel nextStepDoc : nextSteps) {
                    if (!nextStepDoc.isFolder()) {
                        SSRouteStep nextStep = nextStepDoc.getAdapter(SSRouteStep.class);
                        postesId.add(mailboxPosteService.getPosteIdFromMailboxId(nextStep.getDistributionMailboxId()));
                    }
                }
            }
        }
        // Récupération des postes de l'organigramme en fonction de leurs id
        List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(postesId);

        // On prend le label des noeuds
        for (OrganigrammeNode node : listNode) {
            labels.add(node.getLabel());
        }

        String label;
        if (labels.isEmpty()) {
            label = "-";
        } else {
            label = StringUtils.join(labels, ", ");
        }

        return label;
    }
}
