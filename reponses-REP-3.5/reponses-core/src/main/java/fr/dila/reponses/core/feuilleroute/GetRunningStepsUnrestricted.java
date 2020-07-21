package fr.dila.reponses.core.feuilleroute;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;

public class GetRunningStepsUnrestricted extends UnrestrictedSessionRunner {
    
    /**
     * Identifiant technique de l'instance de feuille de route.
     * */
    protected final String routeInstanceDocId;
    
    /**
     * Le service de feuille de route.
     * 
     */
    protected final FeuilleRouteService  fdrService;
    
    protected DocumentModel runningStep;
    protected String[] labelRunningStep;
    
    public GetRunningStepsUnrestricted(String repoName, String routeInstanceDocId, FeuilleRouteService fdrService) {
        super(repoName);
        this.fdrService = fdrService;
        this.routeInstanceDocId = routeInstanceDocId;
    }

    @Override
    public void run() throws ClientException {
        //TODO sly : à changer parce que getRunningSteps renvoit plusieurs steps
        runningStep = fdrService.getRunningSteps(session, routeInstanceDocId).get(0);
        
        if (runningStep != null){
            STRouteStep routeStep = runningStep.getAdapter(STRouteStep.class);
            String mailboxId = routeStep.getDistributionMailboxId();
            String routingTaskType = routeStep.getType();
            VocabularyService vocService = STServiceLocator.getVocabularyService();
            String routingTaskKey = vocService.getEntryLabel(STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY, routingTaskType);
            final MailboxService mailboxService = STServiceLocator.getMailboxService();
            String mailboxTitleLabel = mailboxService.getMailboxTitle(session,mailboxId);
            labelRunningStep = new String[2];
            labelRunningStep[0] = routingTaskKey;
            labelRunningStep[1] = mailboxTitleLabel;
        } else{
            labelRunningStep = new String[2];
            labelRunningStep[0] = "label.no.step";
            labelRunningStep[1] = "";
        }
    }
    

    /**
     * Retourne l'étape courante ayant pour dossier docId.
     * @return Etape courante
     */
    public DocumentModel getRunningSteps() {
        return runningStep;
    }
    
    /**
     * Retourne le label de l'étape courante
     * @return Un tableau qui permet de recontruire le nom d'une étape
     */
    public String[] getNameRunningSteps() {
        return labelRunningStep;
    }


}
