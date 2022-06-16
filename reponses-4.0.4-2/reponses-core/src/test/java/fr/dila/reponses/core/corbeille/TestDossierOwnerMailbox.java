package fr.dila.reponses.core.corbeille;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Teste la mailbox personnelle de création des dossiers Réponses.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestDossierOwnerMailbox {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Test
    public void testDossierOwner() throws Exception {
        String mailboxid;

        // Récupère l'utilisateur qui possède les dossiers
        ConfigService configService = STServiceLocator.getConfigService();
        String dossierOwner = configService.getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);
        Assert.assertNotNull(dossierOwner);

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la Mailbox personnelle de l'utilisateur
            Mailbox mailbox = reponseFeature.getPersonalMailbox(session, dossierOwner);
            Assert.assertNotNull(mailbox);
            Assert.assertEquals("adminsggf adminsggl", mailbox.getTitle());
            mailboxid = mailbox.getId();
            Assert.assertNotNull(mailboxid);
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession("adminsgg")) {
            ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
            Mailbox mailbox = corbeilleService.getDossierOwnerPersonalMailbox(session);
            Assert.assertNotNull(mailbox);
            Assert.assertEquals(mailboxid, mailbox.getId());
        }
    }
}
