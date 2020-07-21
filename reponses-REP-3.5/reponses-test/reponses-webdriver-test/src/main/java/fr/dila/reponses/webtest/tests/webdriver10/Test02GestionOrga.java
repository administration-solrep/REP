package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.IOException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import junit.framework.Assert;

import org.junit.Ignore;

import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.constant.ConstantesProfils;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.helper.ReponsesImapHelper;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.GestionOrganigrammePage;
import fr.dila.reponses.webtest.pages.administration.GestionUtilisateurPage;
import fr.dila.reponses.webtest.pages.administration.organigramme.CreerPostePage;
import fr.dila.reponses.webtest.pages.administration.organigramme.CreerUniteStructurellePage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.CreerUtilisateurPage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.UtilisateurPage;
import fr.dila.reponses.webtest.pages.espaces.EspaceAdministration;
import fr.dila.reponses.webtest.pages.login.ChangePasswordPage;
import fr.dila.reponses.webtest.pages.login.LoginPage;
import fr.dila.reponses.webtest.pages.widget.organigramme.OrganigrammeContextMenu;
import fr.dila.reponses.webtest.tests.webtestassert.WebTestAssert;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.helper.NameShortener;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;

/**
 * Tests de gestion de l'organigramme.
 * 
 * 
 * @author JGO
 */
public class Test02GestionOrga extends AbstractWebTest {

	private static final String	NOM_NOUVEAU_POSTE					= "Dir Gen - RH OGM (AGR)";
	private static final String	NOM_NOUVELLE_DIRECTION				= ConstantesOrga.DIR_OGM;
	public static final String	ID_NOUVEL_UTILISATEUR_NUM_UN		= "Tomato68";
	private String				ID_NOUVEL_UTILISATEUR				= "Tomato69";
	private static final String	ID_NOUVEL_UTILISATEUR_TROP_COURT	= "Tomato8";
	public static String		NOUVEL_UTILISATEUR					= "Tom Atour";

	@WebTest(description = "Vérification technique des liens du bandeau du haut", order = 10)
	@TestDocumentation(categories = { "Navigation" })
	public void testVerificationLienBandeauHaut() {
		CorbeillePage corbeillePage = loginAsAdmin();
		corbeillePage.getBandeauMenu().goToAdministration();
		corbeillePage.getBandeauMenu().goToRecherche();
		corbeillePage.getBandeauMenu().goToSuivi();
		corbeillePage.getBandeauMenu().goToStatistiques();
		corbeillePage.getBandeauMenu().goToEspaceTravail();
		corbeillePage.getBandeauMenu().goToPlanClassement();
		logout();
	}

	@WebTest(description = "1.1.2 Gestion organigramme", order = 20)
	@TestDocumentation(categories = { "Organigramme", "Administration", "Utilisateur" })
	public void testGestionOrganigramme() throws InterruptedException, MessagingException, IOException {
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
		GestionOrganigrammePage organigrammePage = espaceAdministration.getMenuVoletGauche().goToGestionOrganigramme();

		getFlog().startAction("Ajout d'une unité structurelle");
		OrganigrammeContextMenu orgaContextMenu1 = organigrammePage.rightClickOn(ConstantesOrga.MIN_AGRI);
		CreerUniteStructurellePage newUniteStructurelle = orgaContextMenu1.goToCreerUniteStructurelle();
		newUniteStructurelle.setTypeDirection();
		String nomNouvelleDirection = NOM_NOUVELLE_DIRECTION;
		newUniteStructurelle.setLibelle(nomNouvelleDirection);
		newUniteStructurelle.enregistrer();
		getFlog().endAction();

		getFlog().startAction("Ajout d'un poste");
		organigrammePage.unfold(ConstantesOrga.MIN_AGRI);
		organigrammePage.refresh();
		OrganigrammeContextMenu orgaContextMenu2 = organigrammePage.rightClickOn(nomNouvelleDirection);
		CreerPostePage newPoste = orgaContextMenu2.goToCreerPoste();
		newPoste.setLibelle(NOM_NOUVEAU_POSTE);
		newPoste.enregistrer();
		getFlog().endAction();

		/** Ajout d'un utilisateur **/

		getFlog().startAction("Ajout d'un utilisateur ne respectant pas les critères");
		espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
		GestionUtilisateurPage gestionUtilisateurPage = espaceAdministration.getMenuVoletGauche()
				.goToGestionDesUtilisateurs();
		CreerUtilisateurPage nouvelUtilisateurEchec = gestionUtilisateurPage.ajouterUnUtilisateur();
		String nouvelUtilisateurIdentifiant1 = ID_NOUVEL_UTILISATEUR_TROP_COURT;
		nouvelUtilisateurEchec.setIdentifiant(nouvelUtilisateurIdentifiant1);
		String[] tabUtilisateur1 = NOUVEL_UTILISATEUR.split(" ");
		nouvelUtilisateurEchec.setPrenom(tabUtilisateur1[0]);
		nouvelUtilisateurEchec.setNom(tabUtilisateur1[1]);
		nouvelUtilisateurEchec.setCiviliteMonsieur();
		nouvelUtilisateurEchec.setUtilisateurTemporaireOui();
		nouvelUtilisateurEchec.setAdresse("221B Baker Street");
		nouvelUtilisateurEchec.setVille("Lyon");
		nouvelUtilisateurEchec.setTelephone("0689565352");
		nouvelUtilisateurEchec.setMel(ReponsesImapHelper.MAILBOX_USER_DEFAULT);
		NameShortener profilShortener1 = new NameShortener(ConstantesProfils.CONTRIBUTEUR_MIN);
		nouvelUtilisateurEchec.setProfil(profilShortener1);
		NameShortener posteShortener1 = new NameShortener(NOM_NOUVEAU_POSTE);
		nouvelUtilisateurEchec.setPoste(posteShortener1);
		nouvelUtilisateurEchec.enregistrer();
		WebTestAssert.assertTrue(getFlog(), ID_NOUVEL_UTILISATEUR_TROP_COURT + " : identifiant trop court",
				ID_NOUVEL_UTILISATEUR_TROP_COURT
						+ " : identifiant accepté alors qu'il ne respecte pas les critères de taille",
				organigrammePage.verifierRejetIdentifiant());
		getFlog().endAction();

		getFlog().startAction("Ajout d'un premier utilisateur");
		espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
		gestionUtilisateurPage = espaceAdministration.getMenuVoletGauche().goToGestionDesUtilisateurs();
		CreerUtilisateurPage nouvelUtilisateur = gestionUtilisateurPage.ajouterUnUtilisateur();
		nouvelUtilisateur.setIdentifiant(ID_NOUVEL_UTILISATEUR_NUM_UN);
		String[] tabUtilisateur = NOUVEL_UTILISATEUR.split(" ");
		nouvelUtilisateur.setPrenom(tabUtilisateur[0]);
		nouvelUtilisateur.setNom(tabUtilisateur[1]);
		nouvelUtilisateur.setCiviliteMonsieur();
		nouvelUtilisateur.setUtilisateurTemporaireOui();
		nouvelUtilisateur.setAdresse("221B Baker Street");
		nouvelUtilisateur.setVille("Lyon");
		nouvelUtilisateur.setTelephone("0689565352");
		nouvelUtilisateur.setMel(ReponsesImapHelper.MAILBOX_USER_DEFAULT);
		NameShortener profilShortener = new NameShortener(ConstantesProfils.CONTRIBUTEUR_MIN);
		nouvelUtilisateur.setProfil(profilShortener);
		NameShortener posteShortener = new NameShortener(NOM_NOUVEAU_POSTE);
		nouvelUtilisateur.setPoste(posteShortener);
		nouvelUtilisateur.enregistrer();
		getFlog().endAction();

		getFlog().startAction("Nettoyage de la boîte mail");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		mailHelper.clearInbox(imap);

		getFlog().endAction();

		getFlog().startAction("Ajout d'un deuxième utilisateur");
		espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
		gestionUtilisateurPage = espaceAdministration.getMenuVoletGauche().goToGestionDesUtilisateurs();
		nouvelUtilisateur = gestionUtilisateurPage.ajouterUnUtilisateur();
		Date date = new Date();
		ID_NOUVEL_UTILISATEUR = ID_NOUVEL_UTILISATEUR + date.getDay() + date.getMonth() + date.getHours()
				+ date.getMinutes();
		String nouvelUtilisateurIdentifiant = ID_NOUVEL_UTILISATEUR;
		nouvelUtilisateur.setIdentifiant(nouvelUtilisateurIdentifiant);
		tabUtilisateur = NOUVEL_UTILISATEUR.split(" ");
		nouvelUtilisateur.setPrenom(tabUtilisateur[0]);
		nouvelUtilisateur.setNom(tabUtilisateur[1]);
		nouvelUtilisateur.setCiviliteMonsieur();
		nouvelUtilisateur.setUtilisateurTemporaireOui();
		nouvelUtilisateur.setAdresse("221B Baker Street");
		nouvelUtilisateur.setVille("Lyon");
		nouvelUtilisateur.setTelephone("0689565352");
		nouvelUtilisateur.setMel(ReponsesImapHelper.MAILBOX_USER_DEFAULT);
		profilShortener = new NameShortener(ConstantesProfils.CONTRIBUTEUR_MIN);
		nouvelUtilisateur.setProfil(profilShortener);
		posteShortener = new NameShortener(NOM_NOUVEAU_POSTE);
		nouvelUtilisateur.setPoste(posteShortener);
		nouvelUtilisateur.enregistrer();
		getFlog().endAction();

		getFlog().startAction("Obtention du mot de passe du nouvel utilisateur par lecture du mail de bienvenue");
		Message[] messages = mailHelper.waitForWelcomingMessages(imap);
		Assert.assertEquals("Il y avoir un message et un seul dans la boite mail", 1, messages.length);
		Message welcomeMessage = messages[0];
		String password = mailHelper.getPassword(welcomeMessage);
		getFlog().action("Le password de " + ID_NOUVEL_UTILISATEUR + "a été récupéré : " + password);
		getFlog().endAction();
		logout();
		getFlog().startAction(
				"Connexion en utilisant les identifiants du nouvel utilisateur " + nouvelUtilisateurIdentifiant + " : "
						+ password);
		imap.close();
		ChangePasswordPage changePasswordPage = login(nouvelUtilisateurIdentifiant, password, ChangePasswordPage.class);
		getFlog().endAction();
		getFlog().startAction("Changement du mot de passe de l'utilisateur : mot de passe trop court");
		// On ajoute d'abord un mot de passe ne respectant pas les critères puis le bon
		String passwordTooShort = "J!3see";
		String passwordNotStrongEnough = "Je1suis1long";
		String newPassword = "Toto1!1Toto";
		// Test avec mot de passe trop court
		changePasswordPage = changePasswordPage.changePasswordAndSubmit(passwordTooShort, ChangePasswordPage.class);
		WebTestAssert.assertTrue(getFlog(), passwordTooShort + " : mot de passe trop court", passwordTooShort
				+ " : mot de passe accepté alors qu'il ne respecte pas les critères de taille",
				changePasswordPage.verifierRejetMotDePasse());
		getFlog().endAction();
		// Test avec mot de passe pas assez complexe
		getFlog().startAction("Changement du mot de passe de l'utilisateur : mot de passe pas assez complexe");
		changePasswordPage = changePasswordPage.changePasswordAndSubmit(passwordNotStrongEnough,
				ChangePasswordPage.class);
		WebTestAssert.assertTrue(getFlog(), passwordNotStrongEnough + " : mot de passe pas assez complexe",
				passwordNotStrongEnough
						+ " : mot de passe accepté alors qu'il ne respecte pas les critères de complexité",
				changePasswordPage.verifierRejetMotDePasse());
		getFlog().endAction();
		// Mot de passe définitif
		getFlog().startAction("Changement du mot de passe de l'utilisateur : mot de passe accepté");
		changePasswordPage.changePasswordAndSubmit(newPassword, LoginPage.class);
		getFlog().endAction();
		logout();
	}

	// Test de débug pour lire les mails de la boîte mail
	@WebTest(description = "Lecture de la boîte mail", useDriver = false, order = 30)
	@Ignore
	@TestDocumentation(categories = "Test ignoré")
	public void readMail() throws MessagingException, IOException {
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		mailHelper.outputMessages(imap.getInboxMessage());
		// Message[] messages = mailHelper.waitForWelcomingMessages(imap);
		imap.close();
	}

	@WebTest(description = "1.1.3 Gestion organigramme", order = 21)
	@TestDocumentation(categories = { "Organigramme", "Administration", "Utilisateur" })
	public void testSuppressionUtilisateur() throws InterruptedException, MessagingException, IOException {
		getFlog().startAction("Suppression utilisateur créé précédemment");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionUtilisateurPage gestionUtilisateurPage = corbeillePage.getBandeauMenu().goToAdministration()
				.getMenuVoletGauche().goToGestionDesUtilisateurs();
		UtilisateurPage utilisateurPage = gestionUtilisateurPage.rechercherUtilisateur(ID_NOUVEL_UTILISATEUR);
		utilisateurPage.supprimer();
		logout();
	}

}
