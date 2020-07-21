package fr.dila.reponses.webtest.tests.webdriver10;

import org.junit.Assert;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;
import fr.sword.xsd.reponses.EnFonction;

/**
 * Test sur les webservices SOLON
 * @author jgomez
 *
 */
public class Test24Webservice extends AbstractWebTest{

	@WebTest( description = "Test du service chercher membres du gouvernement", useDriver = false, order =10)
	@TestDocumentation(categories={"WS", "Organigramme"})
	public void chercherMembreGouvernement() throws Exception{
		getFlog().startAction("Test du webservice chercher membres du goouvernement");
		WSAttribution wsAttribution = WsUtils.getWsAttribution(WsUtils.WS_AN_USERNAME, WsUtils.WS_AN_USERNAME);
		ChercherMembresGouvernementRequest membreGouvernementRequest = new ChercherMembresGouvernementRequest();
		membreGouvernementRequest.setEnFonction(EnFonction.TRUE);
		ChercherMembresGouvernementResponse chercherMembresGouvernementRep = wsAttribution.chercherMembresGouvernement(membreGouvernementRequest);
		Assert.assertNotNull( "La réponse est null", chercherMembresGouvernementRep );
		Assert.assertNotNull( "Le statut est nul", chercherMembresGouvernementRep.getStatut() );
		Assert.assertEquals( "Le traitement est incorrect", TraitementStatut.OK.toString(), chercherMembresGouvernementRep.getStatut().toString().toString());
		Assert.assertTrue( "Le nombre de membre de gouvernement doit être égal à 17, trouvé : " + chercherMembresGouvernementRep.getMembreGouvernement().size(), chercherMembresGouvernementRep.getMembreGouvernement().size() == ConstantesOrga.NOMBRE_MEMBRE_GOUVERNEMENT);
		getFlog().endAction();
	}
	
	
	//TODO : A réactiver
//	@WebTest( description = "Test du service chercher législature", useDriver = false)
//	@Ignore
//	public void chercherLegislature() throws Exception{
//		getFlog().startAction("Test du webservice chercher législature");
//		WSAttribution wsAttribution = WsUtils.getWsAttribution(WsUtils.WS_AN_USERNAME, WsUtils.WS_AN_USERNAME);
//		ChercherLegislaturesRequest request = new ChercherLegislaturesRequest();
//		ChercherLegislaturesResponse chercherLegislatureRep = wsAttribution.chercherLegislatures(request);
//		Assert.assertNotNull( "La réponse est null", chercherLegislatureRep );
//		Assert.assertNotNull( "Le statut est nul", chercherLegislatureRep.getStatut() );
//		Assert.assertEquals( "Le traitement est incorrect", TraitementStatut.OK.toString(), chercherLegislatureRep.getStatut().toString().toString());
//		Assert.assertNotNull( "Le bloc législature doit être présent", chercherLegislatureRep.getLegislatures());
//		Assert.assertTrue( "Le nombre de législature doit être égal à 1", chercherLegislatureRep.getLegislatures().size() == 1);
//		getFlog().endAction();
//	}
	
}
