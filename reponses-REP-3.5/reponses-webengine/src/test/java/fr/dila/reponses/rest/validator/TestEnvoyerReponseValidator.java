package fr.dila.reponses.rest.validator;

import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

public class TestEnvoyerReponseValidator extends SQLRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        deployContrib("fr.dila.st.core", "OSGI-INF/service/config-framework.xml");
        
        deployContrib("fr.dila.reponses.webengine.test", "OSGI-INF/test-envoyerreponsevalidator-config-contrib.xml");         
 
    }
    
    public void testValidateReponseContent(){
        
        final String reponseValid1 = "ma reponse sans tag html";
        final String reponseValid2 = "ma reponse <table><th><td></td></th><tr><td/></tr><table> <strong>";
        final String reponseInvalid1 = "ma reponses <body>";
        final String reponseInvalid2 = "ma reponses <caption>";
        final String reponseInvalid3 = "ma reponses <img value=\"a\" >";
        final String reponseInvalid4 = "ma reponses <a value=\"a\" >";
        
        ValidatorResult result = null;
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseValid1);
        assertTrue(result.isValid());
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseValid2);
        assertTrue(result.isValid());
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid1);
        assertFalse(result.isValid());
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid2);
        assertFalse(result.isValid());
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid3);
        assertFalse(result.isValid());
        
        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid4);
        assertFalse(result.isValid());
        
    }
    
}
