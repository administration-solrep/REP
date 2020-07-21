package  fr.dila.reponses.core.recherche;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.search.api.client.querymodel.descriptor.QueryModelDescriptor;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
/**
 * 
 * @author jgomez
 * 
 */

// Briser la classe de test en 4 morceaux : requete simple , avancee, index et metadonnées
public class RechercheTestCase  extends ReponsesRepositoryTestCase   {

    public static final String BASE_QUERY = "ufnxql:SELECT DISTINCT q.ecm:uuid AS id FROM Question AS q";
    
    private static final String MY_REQUETE = "myRequete";
    private static final Log LOG = LogFactory.getLog(RechercheTestCase.class);
    protected RechercheService rs;
    protected QueryModelDescriptor requeteDescriptor;
    protected Dossier dossier1;
    protected Question question1;
    protected Reponse reponse1;
   
    
    @Override
    public void setUp() throws Exception {
    	LOG.debug("ENTER SETUP");
        super.setUp();
        openSession();
        rs = ReponsesServiceLocator.getRechercheService();
        assertNotNull(rs);
        EnvReponseFixture fixture = new EnvReponseFixture();
        fixture.setUpEnv(session);
        dossier1 =  fixture.getDossier1();
        question1 =  dossier1.getQuestion(session);
        reponse1 =  fixture.getReponse1();        
        closeSession();
        LOG.debug("EXIT SETUP");
    }
    
    public Requete createRequete(){
        Requete requete =null;
        try {
            requete = rs.createRequete(session, MY_REQUETE);
        } catch (ClientException e) {
            LOG.error("Incapable de créer la requête de test");
            LOG.error(e);
        }
        return requete;
    }
}