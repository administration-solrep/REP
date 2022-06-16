package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.favoris.FavorisIndexation.TypeIndexation.AN;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.reponses.api.service.FavorisIndexationService;
import fr.dila.reponses.core.ReponseFeature;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
public class FavorisIndexationServiceIT {
    @Inject
    private CoreSession session;

    @Inject
    private FavorisIndexationService service;

    @Inject
    private UserManager um;

    @Test
    public void testService() {
        assertThat(service).isNotNull();
    }

    @Test
    public void testAddFavoris() {
        try (
            CloseableCoreSession userSession = CoreInstance.openCoreSession(
                session.getRepositoryName(),
                um.getPrincipal("adminsgg")
            )
        ) {
            DocumentRef favorisIndexationRootRef = service.getFavorisIndexationRootRef(userSession);
            assertThat(userSession.exists(favorisIndexationRootRef)).isTrue();
            DocumentModelList favs = userSession.getChildren(favorisIndexationRootRef);
            assertThat(favs).isEmpty();

            final String cleParent = "foo";
            final String cle = "bar";
            // should add favori
            service.addFavoris(userSession, AN.name(), cle, cleParent);
            favs = userSession.getChildren(favorisIndexationRootRef);
            assertThat(favs.size()).isEqualTo(1);
            FavorisIndexation favori = favs.get(0).getAdapter(FavorisIndexation.class);
            assertThat(favori.getTypeIndexation()).isEqualTo(AN.name());
            assertThat(favori.getNiveau1()).isEqualTo(cleParent);
            assertThat(favori.getNiveau2()).isEqualTo(cle);

            // should not add favori again since it already exists
            service.addFavoris(userSession, AN.name(), cle, cleParent);
            favs = userSession.getChildren(favorisIndexationRootRef);
            assertThat(favs.size()).isEqualTo(1);
        }
    }
}
