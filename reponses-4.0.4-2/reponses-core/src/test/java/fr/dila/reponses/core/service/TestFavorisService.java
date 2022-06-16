/**
 *
 */
package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.recherche.EnvReponseFixture;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.naming.NameAlreadyBoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author jgomez
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
public class TestFavorisService {
    private static final String FAVORIS_DOSSIER_ROOT = "FavorisDossierRoot";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private FavorisDossierService fvs;

    private EnvReponseFixture fixture;

    @Before
    public void setUp() throws Exception {
        STServiceLocator.getSTParametreService().clearCache();
        if (fixture == null) {
            try (CloseableCoreSession session = coreFeature.openCoreSession()) {
                fixture = new EnvReponseFixture();
                fixture.setUpEnv(session);
                createDummyRootFolder(session);
                session.save();
            }
        }
    }

    @Test
    public void testCreateFavorisDossier() throws NameAlreadyBoundException {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            //Test création du favoris
            Calendar dateValidite = Calendar.getInstance();
            fvs.createFavorisRepertoire(session, "dossier_securite", dateValidite);
            Dossier dossier1 = fixture.getDossier1(session);
            Assert.assertNotNull(dossier1.getQuestion(session));
            DocumentModel favori = fvs.createFavorisDossier(session, dossier1.getDocument(), "dossier_securite");
            Assert.assertNotNull(favori);

            //Test création du répertoire favoris
            List<DocumentModel> favorisRepertoires = fvs.getFavorisRepertoires(session);
            Assert.assertNotNull(favorisRepertoires);
            Assert.assertEquals(1, favorisRepertoires.size());
        }
    }

    /**
     *
     * Test l'ajout des dossiers aux favoris
     *
     *
     * @throws NameAlreadyBoundException
     */
    @Test
    public void testAdd() throws NameAlreadyBoundException {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList docs = new DocumentModelListImpl();
            docs.add(fixture.getDossier1(session).getDocument());
            Calendar dateValidite = Calendar.getInstance();
            DocumentModel parent = fvs.createFavorisRepertoire(session, "dossier_securite", dateValidite);
            fvs.add(session, docs, "dossier_securite");
            List<DocumentModel> returnedDocs = fvs.getFavoris(session, parent.getId());
            Assert.assertEquals(1, returnedDocs.size());
            DocumentModel favoris = returnedDocs.get(0);
            Assert.assertEquals(
                fixture.getDossier1(session).getQuestion(session).getDocument().getId(),
                favoris.getPropertyValue("fvd:targetDocument")
            );
        }
    }

    /**
     *
     * Test la suppression des favoris
     *
     *
     */
    @Test
    public void testDelete() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList docsToAdd = new DocumentModelListImpl();
            docsToAdd.add(fixture.getDossier1(session).getDocument());
            Calendar dateValidite = Calendar.getInstance();
            DocumentModel parent = fvs.createFavorisRepertoire(session, "dossier_securite", dateValidite);
            fvs.add(session, docsToAdd, "dossier_securite");
            DocumentModelList docsToDelete = new DocumentModelListImpl();
            docsToDelete.add(fixture.getDossier1(session).getDocument());
            fvs.delete(session, parent.getId(), docsToDelete);
            List<DocumentModel> returnedDocs = fvs.getFavoris(session, parent.getId());
            Assert.assertEquals(0, returnedDocs.size());
        }
    }

    /**
     * Cree le dossier qui contient les favoris, içi à la racine de la session, sera crée par le service content-template dans le userworkspace.
     *
     */
    private void createDummyRootFolder(CoreSession session) {
        DocumentModel root = session.getRootDocument();
        DocumentModel favorisRoot = session.createDocumentModel(
            root.getPathAsString(),
            "favoris",
            FAVORIS_DOSSIER_ROOT
        );
        session.createDocument(favorisRoot);
        session.saveDocument(favorisRoot);
        session.save();
    }
}
