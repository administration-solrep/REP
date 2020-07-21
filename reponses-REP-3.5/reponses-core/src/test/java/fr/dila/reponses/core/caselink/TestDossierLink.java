package fr.dila.reponses.core.caselink;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.model.PropertyException;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STDossierLinkConstant;

/**
 * @author arolin
 */
public class TestDossierLink extends ReponsesRepositoryTestCase {

    private static final Log log = LogFactory.getLog(DossierLink.class);

    private DossierLink dossierLink;

    @Override
    public void setUp() throws Exception {

        super.setUp();

        openSession();
        DocumentModel docModel = createDocument(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, "newDossierLinkTest");        
        dossierLink = docModel.getAdapter(DossierLink.class);
        session.save();
        closeSession();
    }
    

    public void testDossierLinkProperties() throws PropertyException, ClientException {
    	openSession();
    	
        // on verifie que le case link est bien créé, est de type DossierLink, possède
        // une facet "CaseLink" et possède les schémas "Question" et "caseLink"
        log.info("begin : test dossier link type ");

        DocumentModel dossierModel = dossierLink.getDocument();
        assertNotNull(dossierModel);
        assertEquals(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, dossierModel.getType());
        assertTrue(dossierModel.hasFacet(CaseLinkConstants.CASE_LINK_FACET));
        
        assertTrue(dossierModel.hasSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA));
        assertTrue(dossierModel.hasSchema(CaseLinkConstants.CASE_LINK_SCHEMA));
        
        closeSession();
    }

     public void testProperties() throws ClientException {
    	 openSession();
     log.info("begin : test dossier schema properties ");

     //dossierLink id
     String IdDossierLink = dossierLink.getId();
     
     //DossierLink properties
     String currentFeuillerouteId = "currentFeuillerouteId";
     Long numeroQuestion = 12L;
     String sortField ="sortField";
     String typeQuestion ="typeQuestion";
     String nomCompletAuteur="nomCompletAuteur";
     Calendar  datePublicationJO = GregorianCalendar.getInstance();
     String idMinistere = "idMinistere";
     //String delai ="delai";
     String sourceNumeroQuestion="sourceNumeroQuestion";
     Boolean isMailSend=true;
     
     //set the properties
     dossierLink.setRoutingTaskType(currentFeuillerouteId);
     dossierLink.setNumeroQuestion(numeroQuestion);
     dossierLink.setSortField(sortField);
     dossierLink.setTypeQuestion(typeQuestion);
     dossierLink.setNomCompletAuteur(nomCompletAuteur);
     dossierLink.setDatePublicationJO(datePublicationJO);
     dossierLink.setIdMinistereAttributaire(idMinistere);
     //String delai ="delai";
     dossierLink.setSourceNumeroQuestion(sourceNumeroQuestion);
     dossierLink.setCurrentStepIsMailSendProperty(isMailSend);
     session.saveDocument(dossierLink.getDocument());
     session.save();
     closeSession();
    
     openSession();
     DocumentModel model = session.getDocument(new IdRef(IdDossierLink));
     dossierLink = model.getAdapter(DossierLink.class);
     
     assertEquals(currentFeuillerouteId,dossierLink.getRoutingTaskType());
     assertEquals(numeroQuestion,dossierLink.getNumeroQuestion());
     assertEquals(sortField,dossierLink.getSortField());
     assertEquals(typeQuestion,dossierLink.getTypeQuestion());
     assertEquals(nomCompletAuteur,dossierLink.getNomCompletAuteur());
     assertEquals(datePublicationJO,dossierLink.getDatePublicationJO());
     assertEquals(idMinistere,dossierLink.getIdMinistereAttributaire());
     assertEquals(sourceNumeroQuestion,dossierLink.getSourceNumeroQuestion());
     assertEquals(isMailSend,dossierLink.getCurrentStepIsMailSendProperty());
     
     closeSession();
     }

}