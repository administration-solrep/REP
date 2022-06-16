package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.api.favoris.FavorisIndexation.TypeIndexation.AN;
import static fr.dila.reponses.api.favoris.FavorisIndexation.TypeIndexation.SENAT;
import static fr.dila.reponses.core.operation.nxshell.DataInjectionOperation.USER_ADMINSGG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ecm.core.api.CoreInstance.openCoreSession;

import fr.dila.reponses.api.service.FavorisIndexationService;
import fr.dila.reponses.ui.ReponseUIFeature;
import fr.dila.reponses.ui.bean.FavorisPlanClassementDTO;
import fr.dila.reponses.ui.services.FavorisUIService;
import fr.dila.st.ui.bean.TreeElementDTO;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ ReponseUIFeature.class })
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
@Deploy("fr.dila.st.ui:OSGI-INF/st-actions-contrib.xml")
public class FavorisUIServiceIT {
    private static final String AN_NAME = AN.name();
    private static final String SENAT_NAME = SENAT.name();

    private static final String THEME = "Thème";
    private static final String RUBRIQUE = "Rubrique";
    private static final String BAUX = "baux";
    private static final String ADMINISTRATION = "administration";

    @Inject
    private CoreSession session;

    @Inject
    private UserManager um;

    private NuxeoPrincipal adminsgg;

    @Inject
    private FavorisIndexationService favIndService;

    @Inject
    private FavorisUIService service;

    @Before
    public void setUp() {
        adminsgg = um.getPrincipal(USER_ADMINSGG);
        try (CloseableCoreSession userSession = openCoreSession(session.getRepositoryName(), adminsgg)) {
            // AN
            favIndService.addFavoris(userSession, AN_NAME, "accès aux documents administratifs", ADMINISTRATION);
            favIndService.addFavoris(userSession, AN_NAME, "baux à construction", BAUX);
            favIndService.addFavoris(userSession, AN_NAME, "loyers", BAUX);

            // SENAT
            favIndService.addFavoris(userSession, SENAT_NAME, "Cadastre", RUBRIQUE);
            favIndService.addFavoris(userSession, SENAT_NAME, "Actionnariat", RUBRIQUE);
            favIndService.addFavoris(userSession, SENAT_NAME, "Famille", THEME);
            favIndService.addFavoris(userSession, SENAT_NAME, "Logement et urbanisme", THEME);
        }
    }

    @Test
    @Ignore
    public void testService() {
        assertThat(service).isNotNull();
    }

    @Test
    @Ignore
    public void testGetFavorisDTO() {
        try (CloseableCoreSession userSession = openCoreSession(session.getRepositoryName(), adminsgg)) {
            FavorisPlanClassementDTO favorisDTO = service.getFavorisDTO(userSession);

            // AN
            TreeElementDTO anDTO = favorisDTO.getAssemblee();
            assertThat(anDTO).isNotNull();
            List<? extends TreeElementDTO> children = anDTO.getChilds();
            assertThat(children.size()).isEqualTo(2);

            // administration
            TreeElementDTO administration = children.get(0);
            assertThat(administration.getLabel()).isEqualTo(ADMINISTRATION);
            List<? extends TreeElementDTO> adminChildren = administration.getChilds();
            assertThat(adminChildren.size()).isEqualTo(1);
            assertThat(adminChildren.get(0).getLabel()).isEqualTo("accès aux documents administratifs");
            assertThat(adminChildren.get(0).getLink()).isNotEmpty();

            // baux
            TreeElementDTO baux = children.get(1);
            assertThat(baux.getLabel()).isEqualTo(BAUX);
            List<? extends TreeElementDTO> bauxChildren = baux.getChilds();
            assertThat(bauxChildren.size()).isEqualTo(2);
            assertThat(bauxChildren.get(0).getLabel()).isEqualTo("baux à construction");
            assertThat(bauxChildren.get(1).getLabel()).isEqualTo("loyers");

            // SENAT
            TreeElementDTO senatDTO = favorisDTO.getSenat();
            children = senatDTO.getChilds();
            assertThat(children.size()).isEqualTo(2);

            // Rubrique
            TreeElementDTO rubrique = children.get(0);
            assertThat(rubrique.getLabel()).isEqualTo(RUBRIQUE);
            List<? extends TreeElementDTO> rubriqueChildren = rubrique.getChilds();
            assertThat(rubriqueChildren.size()).isEqualTo(2);
            assertThat(rubriqueChildren.get(0).getLabel()).isEqualTo("Actionnariat");
            assertThat(rubriqueChildren.get(1).getLabel()).isEqualTo("Cadastre");

            // Thème
            TreeElementDTO theme = children.get(1);
            assertThat(theme.getLabel()).isEqualTo(THEME);
            List<? extends TreeElementDTO> themeChildren = theme.getChilds();
            assertThat(themeChildren.size()).isEqualTo(2);
            assertThat(themeChildren.get(0).getLabel()).isEqualTo("Famille");
            assertThat(themeChildren.get(1).getLabel()).isEqualTo("Logement et urbanisme");
        }
    }
}
