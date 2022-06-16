package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author asatre, spl
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestAllotissement {
    private static final Log LOG = LogFactory.getLog(TestAllotissement.class);

    @Inject
    private CoreFeature coreFeature;

    @Test
    public void testFieldEcriture() {
        LOG.info("Start testFieldEcriture");

        DocumentRef docRef;
        final int NB_ID = 5;
        final String unNom = "un nom";
        List<String> idDossiers = new ArrayList<>();
        for (int i = 0; i < NB_ID; ++i) {
            idDossiers.add("id n " + i);
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel docModel = ReponseFeature.createDocument(
                session,
                DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE,
                "newAllotTest"
            );
            Assert.assertNotNull(docModel);

            Allotissement allotissement = docModel.getAdapter(Allotissement.class);
            Assert.assertNotNull(allotissement);
            Assert.assertEquals(docModel, allotissement.getDocument());

            docRef = docModel.getRef();

            allotissement.setIdDossiers(idDossiers);
            allotissement.setNom(unNom);
            session.saveDocument(allotissement.getDocument());
            session.save();
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel docModel = session.getDocument(docRef);
            Allotissement allotissement = docModel.getAdapter(Allotissement.class);

            Assert.assertEquals(unNom, allotissement.getNom());
            List<String> readIdDossiers = allotissement.getIdDossiers();
            Assert.assertEquals(NB_ID, readIdDossiers.size());

            for (int i = 0; i < NB_ID; ++i) {
                Assert.assertEquals(idDossiers.get(i), readIdDossiers.get(i));
            }
        }
    }
}
