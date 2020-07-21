package fr.dila.reponses.web.archive;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.administration.AdministrationActionsBean;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierDistributionActionsBean;
import fr.dila.reponses.web.dossier.DossierListingActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.web.administration.utilisateur.MailSuggestionActionsBean;

/**
 * Bean pour les méthodes d'archivage (export zip)
 * 
 */
@Name("archiveActions")
@Scope(CONVERSATION)
public class ArchiveActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long						serialVersionUID			= -1699435653759401973L;

	private static final String						ERROR_DOSSIER_MAIL			= "feedback.reponses.dossier.error.mail";
	private static final String						OK_DOSSIER_MAIL				= "feedback.reponses.dossier.mail.ok";
	private static final String						HEADER_ATTACHEMENT_FILENAME	= "attachment; filename=\"";
	private static final String						HEADER_CONTENT_DISPOSITION	= "Content-Disposition";
	private static final String						HEADER_CONTENT_TYPE			= "Content-Type";
	private static final String						HEADER_CONTENT_TYPE_ZIP		= "application/zip";
	private static final String						HEADER_CONTENT_TYPE_PDF		= "application/pdf";

	private static final STLogger					LOGGER						= STLogFactory
																						.getLog(ArchiveActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession					documentManager;

	@In(required = true, create = true)
	protected transient SSPrincipal					ssPrincipal;

	@In(create = true, required = false)
	protected transient NavigationContext			navigationContext;

	@In(create = true)
	protected transient DocumentsListsManager		documentsListsManager;

	@In(create = true, required = false)
	protected transient ContentViewActions			contentViewActions;

	@In(create = true, required = false)
	protected transient AdministrationActionsBean	administrationActions;

	@In(create = true, required = false)
	protected transient DossierListingActionsBean	dossierListingActions;

	@In(create = true, required = false)
	protected transient MailSuggestionActionsBean	mailSuggestionActions;

	@In(create = true)
	protected transient CorbeilleActionsBean		corbeilleActions;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor			resourcesAccessor;

	private Boolean									formCopieMail;
	private String									formObjetMail;
	private String									formTexteMail;
	private List<String>							formListMail;

	private static HttpServletResponse getHttpServletResponse() {
		ServletResponse response = null;
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			response = (ServletResponse) facesContext.getExternalContext().getResponse();
		}

		if (response instanceof HttpServletResponse) {
			return (HttpServletResponse) response;
		}
		return null;
	}

	/**
	 * Export zip d'un dossier avec son fond de dossier
	 * 
	 * @throws Exception
	 */
	public void export() throws Exception {
		// Export ZIP
		final HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();
		final OutputStream outputStream = response.getOutputStream();

		// Chargement des services
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		final JournalService journalService = STServiceLocator.getJournalService();

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		final Question question = dossier.getQuestion(documentManager);

		final List<DocumentModel> fondDossierDocs = fondDeDossierService.getFddDocuments(documentManager, dossier,
				ssPrincipal);

		archiveService.writeZipStream(fondDossierDocs, outputStream, dossierDoc, documentManager);

		final String nomFichier = "Dossier_" + question.getTypeQuestion() + "_" + question.getOrigineQuestion() + "_"
				+ question.getNumeroQuestion() + ".zip";
		response.setHeader(HEADER_CONTENT_DISPOSITION, HEADER_ATTACHEMENT_FILENAME + nomFichier + "\";");
		response.setHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_ZIP);
		FacesContext.getCurrentInstance().responseComplete();

		// log de l'export
		journalService.journaliserActionParapheur(documentManager, dossierDoc,
				STEventConstant.EVENT_EXPORT_ZIP_DOSSIER, STEventConstant.COMMENT_EXPORT_ZIP_DOSSIER);
	}

	/**
	 * Export ZIP en masse d'une selection de dossier
	 * 
	 * @throws Exception
	 */
	public void masseExport() throws Exception {
		// Chargement des services
		final JournalService journalService = STServiceLocator.getJournalService();
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

		// Choix de la liste de selection
		final String selectionList = DossierDistributionActionsBean.retrieveSelectionListAccordingView(corbeilleActions
				.getCurrentView());
		final List<DocumentModel> selection = documentsListsManager.getWorkingList(selectionList);
		final List<DocumentModel> files = new ArrayList<DocumentModel>();
		final List<DocumentModel> dossiers = new ArrayList<DocumentModel>();

		// Pour chaque dossier, ajout au flux du fichier ZIP
		for (final DocumentModel doc : selection) {
			final DossierCommon common = doc.getAdapter(DossierCommon.class);
			final Dossier dossier = common.getDossier(documentManager);
			files.addAll(fondDeDossierService.getFddDocuments(documentManager, dossier, ssPrincipal));
			dossiers.add(dossier.getDocument());
		}

		// Export ZIP
		final HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();

		archiveService.writeZipStream(files, response.getOutputStream(), dossiers, documentManager);

		final String filename = "export_reponses_" + DateUtil.formatDDMMYYYY(new GregorianCalendar()) + ".zip";
		response.setHeader(HEADER_CONTENT_DISPOSITION, HEADER_ATTACHEMENT_FILENAME + filename + "\";");
		response.setHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_ZIP);
		FacesContext.getCurrentInstance().responseComplete();

		// Journalise l'action
		journalService.journaliserActionAdministration(documentManager, STEventConstant.EVENT_EXPORT_ZIP_DOSSIER,
				STEventConstant.COMMENT_EXPORT_ZIP_DOSSIER);

		documentsListsManager.resetWorkingList(selectionList);
	}

	public String masseEnvoyerMailDossier() throws Exception {
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

		// Choix de la liste de selection
		final String selectionList = DossierDistributionActionsBean.retrieveSelectionListAccordingView(corbeilleActions
				.getCurrentView());
		final List<DocumentModel> selection = documentsListsManager.getWorkingList(selectionList);

		final List<DocumentModel> files = new ArrayList<DocumentModel>();
		final List<DocumentModel> dossiers = new ArrayList<DocumentModel>();

		// Pour chaque dossier, ajout au flux du fichier ZIP
		for (final DocumentModel doc : selection) {
			final DossierCommon common = doc.getAdapter(DossierCommon.class);
			final Dossier dossier = common.getDossier(documentManager);
			files.addAll(fondDeDossierService.getFddDocuments(documentManager, dossier, ssPrincipal));
			dossiers.add(dossier.getDocument());
		}

		if (formListMail == null || formListMail.isEmpty()) {
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_DOSSIER_MAIL));
			return null;
		}

		final List<String> listMail = new ArrayList<String>();
		for (final String destId : formListMail) {
			final String destMail = (String) mailSuggestionActions.getMailInfo(destId).get(
					MailSuggestionActionsBean.ENTRY_KEY_NAME);
			listMail.add(destMail);
		}

		try {
			archiveService.sendMail(documentManager, files, listMail, formCopieMail, formObjetMail, formTexteMail,
					dossiers);
			facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(OK_DOSSIER_MAIL));
		} catch (final Exception exc) {
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_DOSSIER_MAIL));
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_SEND_MAIL_FONC, exc);
		}

		documentsListsManager.resetWorkingList(selectionList);

		resetMailDossier();
		return null;
	}

	/**
	 * Envoi des mails
	 * 
	 * @throws Exception
	 */
	public String envoyerMailDossier() throws Exception {
		if (formListMail == null || formListMail.isEmpty()) {
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_DOSSIER_MAIL));
			return null;
		}

		final List<String> listMail = new ArrayList<String>();
		for (final String destId : formListMail) {
			final String destMail = (String) mailSuggestionActions.getMailInfo(destId).get(
					MailSuggestionActionsBean.ENTRY_KEY_NAME);
			listMail.add(destMail);
		}

		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		final List<DocumentModel> fondDossierDocs = fondDeDossierService.getFddDocuments(documentManager, dossier,
				ssPrincipal);

		try {
			archiveService.sendMail(documentManager, fondDossierDocs, listMail, formCopieMail, formObjetMail,
					formTexteMail, Collections.singletonList(dossierDoc));
			facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(OK_DOSSIER_MAIL));
		} catch (final Exception exc) {
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_DOSSIER_MAIL));
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_SEND_MAIL_FONC, exc);
		}

		resetMailDossier();
		return null;
	}

	/**
	 * Annuler l'envoi des mails
	 */
	public String annulerMailDossier() {
		resetMailDossier();
		return null;
	}

	private void resetMailDossier() {
		formCopieMail = true;
		formObjetMail = null;
		formTexteMail = null;
		formListMail = null;
	}

	/**
	 * Ajout des dossiers selectionnés à la liste d'élimination courante
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String demanderElimination() throws ClientException {
		final List<DocumentModel> docs = dossierListingActions.getDossiersFromSelection();
		final List<Dossier> dossiersErreur = new ArrayList<Dossier>();
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

		new UnrestrictedSessionRunner(documentManager) {
			@Override
			public void run() throws ClientException {
				dossiersErreur.addAll(archiveService.ajouterDossiersListeElimination(session, docs));
			}
		}.runUnrestricted();

		// Message de compte rendu en cas d'erreur
		FacesMessages.afterPhase();
		facesMessages.clear();
		if (!dossiersErreur.isEmpty()) {
			final List<String> messages = new ArrayList<String>();
			for (final Dossier dossier : dossiersErreur) {
				messages.add(dossier.getQuestion(documentManager).getSourceNumeroQuestion());
			}
			facesMessages
					.add(StatusMessage.Severity.WARN,
							"Dossiers dont le délai de conservation des données n'est pas atteint ou faisant déjà partis d'une liste d'élimination : "
									+ StringUtils.join(messages, ", "));

			return null;
		}

		// redirection vers l'administration
		return administrationActions.navigateToArchivage();
	}

	/**
	 * Navigue vers une liste d'élimination
	 * 
	 * @param listeDoc
	 * @return
	 * @throws ClientException
	 */
	public String navigateToListeElimination(final DocumentModel listeDoc) throws ClientException {
		navigationContext.setCurrentDocument(listeDoc);
		return "liste_elimination_view";
	}

	/**
	 * Suppression des dossiers de la liste d'élimination courante
	 * 
	 * @throws Exception
	 */
	public String eliminerListe(final DocumentModel listeDoc) throws ClientException {
		if (!ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ARCHIVAGE_EDITOR)) {
			throw new ClientException("Archivage des dossiers non autorisé.");
		}
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
		final List<DocumentModel> docs = archiveService.getDossiersFromListeElimination(documentManager, listeDoc);
		documentsListsManager.removeFromAllLists(docs);

		// Précise que la liste est en cours de suppression
		final ListeElimination listeElim = listeDoc.getAdapter(ListeElimination.class);
		listeElim.setSuppressionEnCours(true);
		listeElim.save(documentManager);
		documentManager.save();

		final EventProducer eventProducer = STServiceLocator.getEventProducer();
		final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(ReponsesEventConstant.DOSSIER_EVENT_PARAM, listeDoc);
		final InlineEventContext inlineEventContext = new InlineEventContext(documentManager,
				documentManager.getPrincipal(), eventProperties);
		eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.AFTER_ELIMINATION_LISTE));

		// Vide le cache des content views, pour ré-executer les requêtes
		contentViewActions.reset(ReponsesContentView.ADMIN_LISTE_ELIMINATION_CONTENT_VIEW);
		contentViewActions.reset(ReponsesContentView.ADMIN_LISTES_ELIMINATION_CONTENT_VIEW);

		return administrationActions.navigateToArchivage();
	}

	/**
	 * Edite le bordereau d'archivage et enlève l'état en cours d'une liste d'élimination
	 * 
	 * @throws Exception
	 */
	public void editerBordereau(final DocumentModel listeDoc) throws Exception {
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

		// Suppression de l'état en cours de la liste d'élimination
		final ListeElimination liste = listeDoc.getAdapter(ListeElimination.class);
		liste.setEnCours(false);
		liste.save(documentManager);
		documentManager.save();

		// Export PDF du bordereau d'archivage
		final HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();
		final OutputStream outputStream = response.getOutputStream();
		archiveService.generateListeEliminationPdf(documentManager, outputStream, listeDoc.getId());

		// prepare pdf response
		response.setHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_PDF);
		response.setHeader(HEADER_CONTENT_DISPOSITION, "inline; filename=" + listeDoc.getId() + ".pdf");

		FacesContext.getCurrentInstance().responseComplete();
	}

	/**
	 * Retire une liste de dossiers d'une liste d'élimination
	 * 
	 * @throws Exception
	 */
	public void retirerDossierListeElimination() throws Exception {
		final List<DocumentModel> docs = documentsListsManager
				.getWorkingList(ReponsesConstant.LISTE_ELIMINATION_SELECTION);

		new UnrestrictedSessionRunner(documentManager) {
			@Override
			public void run() throws ClientException {
				for (final DocumentModel doc : docs) {
					try {
						doc.getAdapter(Dossier.class).setListeElimination(null);
						session.saveDocument(doc);
					} catch (final Exception e) {
						throw new ClientException(e);
					}
				}
			}
		}.runUnrestricted();

		documentManager.save();

		documentsListsManager.removeFromWorkingList(ReponsesConstant.LISTE_ELIMINATION_SELECTION,
				new ArrayList<DocumentModel>(docs));

		// Vide le cache du contentView admin_liste_elimination, pour ré-executer la requête
		contentViewActions.reset(ReponsesContentView.ADMIN_LISTE_ELIMINATION_CONTENT_VIEW);
	}

	/**
	 * Vide une liste d'élimination
	 * 
	 * @throws Exception
	 */
	public void viderListeElimination(final DocumentModel listeDoc) throws ClientException {
		final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
		final List<DocumentModel> docs = archiveService.getDossiersFromListeElimination(documentManager, listeDoc);
		new UnrestrictedSessionRunner(documentManager) {
			@Override
			public void run() throws ClientException {
				for (final DocumentModel doc : docs) {
					try {
						doc.getAdapter(Dossier.class).setListeElimination(null);
						session.saveDocument(doc);
					} catch (final Exception e) {
						throw new ClientException(e);
					}
				}
			}
		}.runUnrestricted();

		documentManager.save();

		// Vide le cache du contentView admin_liste_elimination, pour ré-executer la requête
		contentViewActions.reset(ReponsesContentView.ADMIN_LISTE_ELIMINATION_CONTENT_VIEW);
	}

	/**
	 * Abandonnne une liste d'élimination
	 * 
	 * @return
	 * @throws Exception
	 */
	public String abandonListeElimination(final DocumentModel listeDoc) throws ClientException {
		viderListeElimination(listeDoc);
		documentsListsManager.removeFromAllLists(Arrays.asList(listeDoc));
		navigationContext.setCurrentDocument(null);

		// Précise que la liste est en cours d'abandon
		final ListeElimination liste = listeDoc.getAdapter(ListeElimination.class);
		liste.setAbandonEnCours(true);
		liste.save(documentManager);
		documentManager.save();

		final EventProducer eventProducer = STServiceLocator.getEventProducer();
		final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(ReponsesEventConstant.DOSSIER_EVENT_PARAM, listeDoc);
		final InlineEventContext inlineEventContext = new InlineEventContext(documentManager,
				documentManager.getPrincipal(), eventProperties);
		eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.AFTER_ABANDON_LISTE));

		contentViewActions.reset(ReponsesContentView.ADMIN_LISTES_ELIMINATION_CONTENT_VIEW);

		return administrationActions.navigateToArchivage();
	}

	/**
	 * Retourne true si le document courant est une liste d'élimination en cours
	 * 
	 * @return
	 */
	public boolean isCurrentListeEliminationEnCours() {
		if (navigationContext.getCurrentDocument() != null
				&& navigationContext.getCurrentDocument().hasSchema(ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA)) {
			return navigationContext.getCurrentDocument().getAdapter(ListeElimination.class).isEnCours();
		}
		return false;
	}

	public Boolean getFormCopieMail() {
		return formCopieMail;
	}

	public void setFormCopieMail(final Boolean formCopieMail) {
		this.formCopieMail = formCopieMail;
	}

	public String getFormObjetMail() throws ClientException {
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		formObjetMail = parametreService.getParametreValue(documentManager, STParametreConstant.DOSSIER_MAIL_OBJET);

		return formObjetMail;
	}

	public void setFormObjetMail(final String formObjetMail) {
		this.formObjetMail = formObjetMail;
	}

	public String getFormTexteMail() throws ClientException {
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		formTexteMail = parametreService.getParametreValue(documentManager, STParametreConstant.DOSSIER_MAIL_TEXT);
		return formTexteMail;
	}

	public void setFormTexteMail(final String formTexteMail) {
		this.formTexteMail = formTexteMail;
	}

	public List<String> getFormListMail() {
		return formListMail;
	}

	public void setFormListMail(final List<String> formListMail) {
		this.formListMail = formListMail;
	}
}
