package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getArchiveUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getBordereauUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getDossierListUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getDossierMassUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getDossierUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getFondDeDossierUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getIndexAssembleeSuggestionProviderService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getIndexMinistereSuggestionProviderService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getMinistereSuggestionProviderService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getNomAuteurSuggestionProviderService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getParapheurUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getPlanClassementComponentService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getRechercheUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getReponsesFeuilleRouteUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getReponsesModeleFdrListUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getReponsesUtilisateursUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getRequeteUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getRouteStepNoteUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getSelectValueUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getStatistiquesUIService;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.reponses.ui.ReponseUIFeature;
import fr.dila.reponses.ui.services.ArchiveUIService;
import fr.dila.reponses.ui.services.BordereauUIService;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.reponses.ui.services.RechercheUIService;
import fr.dila.reponses.ui.services.ReponsesFeuilleRouteUIService;
import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.ReponsesUtilisateursUIService;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.reponses.ui.services.actions.suggestion.indexassemblee.IndexAssembleeSuggestionProviderService;
import fr.dila.reponses.ui.services.actions.suggestion.indexministere.IndexMinistereSuggestionProviderService;
import fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderService;
import fr.dila.reponses.ui.services.comment.RouteStepNoteUIService;
import fr.dila.reponses.ui.services.dossier.DossierMassUIService;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationGouvernementUIService;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseUIFeature.class)
public class ReponsesUIServiceLocatorIT {

    /**
     * Vérifie que les services UI sont bien instanciés/accessibles
     */
    @Test
    @Ignore
    public void testServices() {
        assertThat(getArchiveUIService()).isInstanceOf(ArchiveUIService.class);
        assertThat(getDossierListUIService()).isInstanceOf(DossierListUIService.class);
        assertThat(getPlanClassementComponentService()).isInstanceOf(PlanClassementComponentService.class);
        assertThat(getDossierUIService()).isInstanceOf(SSDossierUIService.class);
        assertThat(getStatistiquesUIService()).isInstanceOf(StatistiquesUIService.class);
        assertThat(getBordereauUIService()).isInstanceOf(BordereauUIService.class);
        assertThat(getParapheurUIService()).isInstanceOf(ParapheurUIService.class);
        assertThat(getRechercheUIService()).isInstanceOf(RechercheUIService.class);
        assertThat(getSelectValueUIService()).isInstanceOf(ReponsesSelectValueUIService.class);
        assertThat(getMinistereSuggestionProviderService()).isInstanceOf(MinistereSuggestionProviderService.class);
        assertThat(getIndexMinistereSuggestionProviderService())
            .isInstanceOf(IndexMinistereSuggestionProviderService.class);
        assertThat(getIndexAssembleeSuggestionProviderService())
            .isInstanceOf(IndexAssembleeSuggestionProviderService.class);
        assertThat(getNomAuteurSuggestionProviderService()).isInstanceOf(NomAuteurSuggestionProviderService.class);
        assertThat(getReponsesModeleFdrListUIService()).isInstanceOf(ReponsesModeleFdrListUIService.class);
        assertThat(getRequeteUIService()).isInstanceOf(RequeteUIService.class);
        assertThat(getFondDeDossierUIService()).isInstanceOf(ReponsesFondDeDossierUIService.class);
        assertThat(getRouteStepNoteUIService()).isInstanceOf(RouteStepNoteUIService.class);
        assertThat(getReponsesMigrationGouvernementUIService())
            .isInstanceOf(ReponsesMigrationGouvernementUIService.class);
        assertThat(getReponsesUtilisateursUIService()).isInstanceOf(ReponsesUtilisateursUIService.class);
        assertThat(getReponsesFeuilleRouteUIService()).isInstanceOf(ReponsesFeuilleRouteUIService.class);
        assertThat(getDossierMassUIService()).isInstanceOf(DossierMassUIService.class);
    }
}
