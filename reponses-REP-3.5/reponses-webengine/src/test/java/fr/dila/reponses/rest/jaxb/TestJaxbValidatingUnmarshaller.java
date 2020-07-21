package fr.dila.reponses.rest.jaxb;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import junit.framework.TestCase;
import fr.dila.st.rest.client.helper.STSchemaFactory;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.Reponse;
import fr.sword.xsd.reponses.ReponseQuestion;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;

/**
 * Ce test permet de vérifier l'échappement des cdata avec la version de JAXB intégrée dans WebEngine.
 * 
 * @author jtremeaux
 */
public class TestJaxbValidatingUnmarshaller extends TestCase {
    
    public void testUnmarshallerValidation() throws Exception {
        ChercherReponsesResponse rep = new ChercherReponsesResponse();
        Reponse reponse = new Reponse();
        reponse.setTexteReponse("Le texte de la r&eacute;ponse");
        ReponseQuestion reponseQuestion = new ReponseQuestion();
        reponseQuestion.setReponse(reponse);
        rep.getReponses().add(reponseQuestion);
        
        JAXBContext context = JAXBContext.newInstance(NotifierTransitionRequest.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream is = getClass().getResourceAsStream("/unmarshaller/WSepp_notifierTransitionRequestOK.xml");
        unmarshaller.unmarshal(is);

        context = JAXBContext.newInstance(NotifierTransitionRequest.class);
        unmarshaller = context.createUnmarshaller();
        STSchemaFactory stSchemaFactory = new STSchemaFactory();
        Schema schema = stSchemaFactory.getWsEppSchema();
        unmarshaller.setSchema(schema);
        is = getClass().getResourceAsStream("/unmarshaller/WSepp_notifierTransitionRequestKO.xml");
        try {
            unmarshaller.unmarshal(is);
        } catch (UnmarshalException e) {
            assertTrue(e.getCause().getMessage().contains("id_evenement"));
            return;
        }
        fail("La validation devrait être KO");
    }
}
