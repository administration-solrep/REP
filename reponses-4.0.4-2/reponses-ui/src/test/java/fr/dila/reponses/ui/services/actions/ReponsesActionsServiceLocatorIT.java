package fr.dila.reponses.ui.services.actions;

import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getComparateurActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getDossierActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getDossierConnexeActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getIndexActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getParapheurActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponseActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesBordereauActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesModeleFeuilleRouteActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesOrganigrammeInjectionActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesSmartNXQLQueryActionService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getRequeteurActionService;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.reponses.ui.ReponseUIFeature;
import fr.dila.reponses.ui.services.actions.nxql.ReponsesSmartNXQLQueryActionService;
import fr.dila.reponses.ui.services.actions.organigramme.ReponsesOrganigrammeInjectionActionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseUIFeature.class)
public class ReponsesActionsServiceLocatorIT {

    /**
     * Vérifie que les services Actions sont bien instanciés/accessibles
     */
    @Test
    public void testServices() {
        assertThat(getReponsesDocumentRoutingActionService()).isInstanceOf(ReponsesDocumentRoutingActionService.class);
        assertThat(getDossierActionService()).isInstanceOf(DossierActionService.class);
        assertThat(getReponseActionService()).isInstanceOf(ReponseActionService.class);
        assertThat(getReponsesDossierDistributionActionService())
            .isInstanceOf(ReponsesDossierDistributionActionService.class);
        assertThat(getReponsesBordereauActionService()).isInstanceOf(ReponsesBordereauActionService.class);
        assertThat(getRequeteurActionService()).isInstanceOf(RequeteurActionService.class);
        assertThat(getReponsesSmartNXQLQueryActionService()).isInstanceOf(ReponsesSmartNXQLQueryActionService.class);
        assertThat(getIndexActionService()).isInstanceOf(IndexActionService.class);
        assertThat(getParapheurActionService()).isInstanceOf(ParapheurActionService.class);
        assertThat(getReponsesOrganigrammeInjectionActionService())
            .isInstanceOf(ReponsesOrganigrammeInjectionActionService.class);
        assertThat(getComparateurActionService()).isInstanceOf(ComparateurActionService.class);
        assertThat(getReponsesModeleFeuilleRouteActionService())
            .isInstanceOf(ReponsesModeleFeuilleRouteActionService.class);
        assertThat(getDossierConnexeActionService()).isInstanceOf(DossierConnexeActionService.class);
    }
}
