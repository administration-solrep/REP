package fr.dila.reponses.rest.jaxb;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;
import fr.dila.st.rest.client.helper.CdataCharacterEscapeHandler;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.Reponse;
import fr.sword.xsd.reponses.ReponseQuestion;

/**
 * Ce test permet de vérifier l'échappement des cdata avec la version de JAXB intégrée dans WebEngine.
 * 
 * @author jtremeaux
 */
public class TestCustomJaxbMarshaller extends TestCase {
    
    public void testCharacterEscaper() throws Exception {
        ChercherReponsesResponse rep = new ChercherReponsesResponse();
        Reponse reponse = new Reponse();
        reponse.setTexteReponse("Le texte de la r&eacute;ponse");
        ReponseQuestion reponseQuestion = new ReponseQuestion();
        reponseQuestion.setReponse(reponse);
        rep.getReponses().add(reponseQuestion);
        
        JAXBContext context = JAXBContext.newInstance(ChercherReponsesResponse.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Marshaller m = context.createMarshaller();
        m.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", new CdataCharacterEscapeHandler());
//        m.setProperty("com.sun.xml.internal.bind.CharacterEscapeHandler", new CdataCharacterEscapeHandler());
        m.marshal(rep, os);
//        System.out.println(os.toString());
//        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><chercherReponsesResponse xmlns=\"http://www.dila.premier-ministre.gouv.fr/solrep/reponses/WSreponse\" xmlns:ns2=\"http://www.dila.premier-ministre.gouv.fr/solrep/reponses/commons\" xmlns:ns3=\"http://www.dila.premier-ministre.gouv.fr/solrep/reponses/reponses\"><dernier_renvoi>false</dernier_renvoi><reponses><ns3:reponse><ns3:texte_reponse><![CDATA[Le texte de la r&eacute;ponse]]></ns3:texte_reponse></ns3:reponse></reponses></chercherReponsesResponse>", os.toString());
    }
}
