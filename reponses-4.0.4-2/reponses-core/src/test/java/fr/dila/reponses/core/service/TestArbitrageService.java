package fr.dila.reponses.core.service;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Tests du service d'arbitrage
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestArbitrageService {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponsesArbitrageService arbitrageService;

    /**
     * test si on se trouve sur une étape "Pour Arbitrage"
     * @throws Exception
     */
    @Test
    public void testIsStepPourArbitrage() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierLinkDoc = ReponseFeature.createDocument(
                session,
                STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
                "dossierLink"
            );
            DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
            dossierLink.setRoutingTaskType(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE);
            Assert.assertTrue(arbitrageService.isStepPourArbitrage(dossierLink));
        }
    }

    /**
     * test le dossier n'est pas bloqué pour arbitrage
     * @throws Exception
     */
    @Test
    public void testCanAddStepArbitrageSGG() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierDoc = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "dossier"
            );
            DocumentModel dossierLinkDoc = ReponseFeature.createDocument(
                session,
                STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
                "dossierLink"
            );

            // Test avec  doc autre que dossier :
            Assert.assertFalse(arbitrageService.canAddStepArbitrageSGG(dossierLinkDoc));

            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            dossier.setIsArbitrated(false);
            // Si le dossier n'a pas été arbitré, on peut ajouter une etape pour arbitrage
            Assert.assertTrue(arbitrageService.canAddStepArbitrageSGG(dossierDoc));

            dossier.setIsArbitrated(true);
            // Si le dossier a été arbitré, on ne peut pas ajouter une etape pour arbitrage
            Assert.assertFalse(arbitrageService.canAddStepArbitrageSGG(dossierDoc));
        }
    }

    /**
     * test de la mise à jour du dossier après un arbitrage
     * @throws Exception
     */
    @Test
    public void testUpdateDossierAfterArbitrage() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierDoc = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "dossier"
            );
            DocumentModel dossierLinkDoc = ReponseFeature.createDocument(
                session,
                STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
                "dossierLink"
            );
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);

            // On test avec un document qui n'est pas un dossier
            try {
                arbitrageService.updateDossierAfterArbitrage(session, dossierLinkDoc);
            } catch (ReponsesException exc) {
                Assert.assertEquals(STLogEnumImpl.FAIL_GET_DOSSIER_TEC.getText(), exc.getMessage());
            }

            Assert.assertFalse(dossier.isArbitrated());
            // On test le cas nominal :
            arbitrageService.updateDossierAfterArbitrage(session, dossierDoc);
            Assert.assertTrue(dossier.isArbitrated());
        }
    }
}
