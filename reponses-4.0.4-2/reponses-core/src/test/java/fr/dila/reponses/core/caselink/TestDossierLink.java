package fr.dila.reponses.core.caselink;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.constant.STDossierLinkConstant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestDossierLink {
    private static final Log log = LogFactory.getLog(TestDossierLink.class);

    @Inject
    private CoreFeature coreFeature;

    //	@Inject
    //	private ReponseFeature reponseFeature;

    private DocumentRef dossierLinkRef;

    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel docModel = ReponseFeature.createDocument(
                session,
                STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE,
                "newDossierLinkTest"
            );
            dossierLinkRef = docModel.getRef();
            session.save();
        }
    }

    @Test
    public void testDossierLinkProperties() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que le case link est bien créé, est de type
            // DossierLink, possède
            // une facet "CaseLink" et possède les schémas "Question" et
            // "caseLink"
            log.info("begin : test dossier link type ");

            DocumentModel dossierModel = session.getDocument(dossierLinkRef);
            Assert.assertNotNull(dossierModel);
            Assert.assertEquals(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, dossierModel.getType());
            Assert.assertTrue(dossierModel.hasFacet(CaseLinkConstants.CASE_LINK_FACET));

            Assert.assertNotNull(
                dossierModel.getDocumentType().getSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA)
            );

            Assert.assertTrue(dossierModel.hasSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA));
            Assert.assertTrue(dossierModel.hasSchema(CaseLinkConstants.CASE_LINK_SCHEMA));
        }
    }

    @Test
    public void testProperties() {
        // DossierLink properties
        String currentFeuillerouteId = "currentFeuillerouteId";
        Long numeroQuestion = 12L;
        String sortField = "sortField";
        String typeQuestion = "typeQuestion";
        String nomCompletAuteur = "nomCompletAuteur";
        Calendar datePublicationJO = GregorianCalendar.getInstance();
        String idMinistere = "idMinistere";
        // String delai ="delai";
        String sourceNumeroQuestion = "sourceNumeroQuestion";
        Boolean isMailSend = true;

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier schema properties ");

            DossierLink dossierLink = session.getDocument(dossierLinkRef).getAdapter(DossierLink.class);
            Assert.assertNotNull(dossierLink);

            // set the properties
            dossierLink.setRoutingTaskType(currentFeuillerouteId);
            dossierLink.setNumeroQuestion(numeroQuestion);
            dossierLink.setSortField(sortField);
            dossierLink.setTypeQuestion(typeQuestion);
            dossierLink.setNomCompletAuteur(nomCompletAuteur);
            dossierLink.setDatePublicationJO(datePublicationJO);
            dossierLink.setIdMinistereAttributaire(idMinistere);
            // String delai ="delai";
            dossierLink.setSourceNumeroQuestion(sourceNumeroQuestion);
            dossierLink.setCurrentStepIsMailSendProperty(isMailSend);
            session.saveDocument(dossierLink.getDocument());
            session.save();
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DossierLink dossierLink = session.getDocument(dossierLinkRef).getAdapter(DossierLink.class);

            Assert.assertEquals(currentFeuillerouteId, dossierLink.getRoutingTaskType());
            Assert.assertEquals(numeroQuestion, dossierLink.getNumeroQuestion());
            Assert.assertEquals(sortField, dossierLink.getSortField());
            Assert.assertEquals(typeQuestion, dossierLink.getTypeQuestion());
            Assert.assertEquals(nomCompletAuteur, dossierLink.getNomCompletAuteur());
            Assert.assertEquals(datePublicationJO, dossierLink.getDatePublicationJO());
            Assert.assertEquals(idMinistere, dossierLink.getIdMinistereAttributaire());
            Assert.assertEquals(sourceNumeroQuestion, dossierLink.getSourceNumeroQuestion());
            Assert.assertEquals(isMailSend, dossierLink.getCurrentStepIsMailSendProperty());
        }
    }
}
