package fr.dila.reponses.core.corbeille;


import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Teste la mailbox personnelle de création des dossiers Réponses.
 *  
 * @author jtremeaux
 */
public class TestDossierOwnerMailbox extends ReponsesRepositoryTestCase  {

	public void testDossierOwner() throws Exception {
    	openSession();
    	
        // Récupère l'utilisateur qui possède les dossiers
        ConfigService configService = STServiceLocator.getConfigService();
        String dossierOwner = configService.getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);
        assertNotNull(dossierOwner);
        
        // Crée la Mailbox personnelle de l'utilisateur
        Mailbox mailbox = getPersonalMailbox(dossierOwner);
        assertNotNull(mailbox);
        final String mailboxid = mailbox.getId();
        
        // Récupère la Mailbox personnelle de l'utilisateur qui possède les dossiers
        CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        mailbox = corbeilleService.getDossierOwnerPersonalMailbox(session);
        assertNotNull(mailbox);
        assertEquals(mailboxid, mailbox.getId());
        
        closeSession();
    }
}
