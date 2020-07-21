package fr.dila.reponses.web.dossier;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.Exception.CopieStepException;
import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.Exception.SignatureException;
import fr.dila.reponses.api.Exception.StepValidationException;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.feuilleroute.DocumentRoutingActionsBean;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.exception.PosteNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.dossier.DossierLockActionsBean;

/**
 * Bean Seam permettant de gérer la distribution des dossiers.
 * 
 * @author ARN
 */
@Name("dossierDistributionActions")
@Scope(ScopeType.CONVERSATION)
public class DossierDistributionActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long							serialVersionUID	= -6359405896496460937L;

	/**
	 * Logger.
	 */
	private static final STLogger						LOGGER				= STLogFactory
																					.getLog(DossierDistributionActionsBean.class);
	private static final String							ERROR_ADD_STEP		= "feedback.reponses.dossier.error.cannotAddStepToRoute";

	@In(create = true, required = true)
	protected transient CoreSession						documentManager;

	@In(required = true, create = true)
	protected SSPrincipal								ssPrincipal;

	@In(create = true, required = false)
	protected transient NavigationContextBean			navigationContext;

	@In(create = true, required = false)
	protected transient ReponseActionsBean				reponseActions;

	@In(create = true, required = false)
	protected transient FacesMessages					facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor				resourcesAccessor;

	@In(create = true)
	protected transient CorbeilleActionsBean			corbeilleActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingActionsBean		routingActions;

	@In(create = true, required = false)
	protected transient DossierLockActionsBean			dossierLockActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingWebActionsBean	routingWebActions;

	@In(create = true, required = false)
	protected transient DossierListingActionsBean		dossierListingActions;

	@In(create = true, required = false)
	protected transient DossierActionsBean				dossierActions;

	private DocumentModel								question;
	private String										selectedMinForReattribution;
	private String										reattributionObservations;
	private String										reattributionMinistere;

	// Services
	private ReponsesArbitrageService					arbitrageService;
	private DossierDistributionService					dossierDistributionService;

	/**
	 * Get the current Question from Dossier.
	 * 
	 * @throws ClientException
	 */
	@Factory(value = "question", scope = EVENT)
	public DocumentModel getQuestion() throws ClientException {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
			Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			final DocumentModel question = dossier.getQuestion(documentManager).getDocument();
			if (this.question == null || !this.question.getId().equals(question.getId())) {
				this.question = question;
			}
			return this.question;
		}

		return null;
	}

	/**
	 * Get the current Dossier from Reponse.
	 * 
	 */
	@Factory(value = "dossier", scope = EVENT)
	public DocumentModel getDossier() {
		return navigationContext.getCurrentDocument();
	}

	/**
	 * Retourne l'étape "validation PM" de l'instance de la feuille de route associé au dossier.
	 * 
	 * @return Etape validation premier ministre
	 */
	public DocumentModel getValidationPMStep() throws ClientException {
		// Récupère le Dossier de la session
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
		final String feuilleRouteInstanceId = dossier.getLastDocumentRoute();

		return ReponsesServiceLocator.getFeuilleRouteService().getValidationPMStep(documentManager,
				feuilleRouteInstanceId);
	}

	/**
	 * Donne un avis favorable sur le DossierLink en cours.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String donnerAvisFavorable() throws ClientException {

		checkAndUpdateReponse();

		// Récupération du dossier link
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();

		// Récupération du dossier
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		String validationFormat = null;
		try {
			reponseActions.resetReponse();
			getDossierDistributionService().validerEtape(documentManager, dossierDoc, dossierLink.getDocument());

			if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(dossierLink.getRoutingTaskType())) {
				validationFormat = resourcesAccessor.getMessages().get(
						"label.reponses.feuilleRoute.message.etape.validation.reattribution");
				displayMessageInfo(validationFormat, dossier.getNumeroQuestion(),
						getLabelEntite(dossier.getIdMinistereAttributaireCourant()));
			} else {
				validationFormat = resourcesAccessor.getMessages().get(
						"label.reponses.feuilleRoute.message.etape.validation");
				displayMessageInfo(validationFormat, dossier.getNumeroQuestion(), dossier.getLabelNextStep());
			}

			if (!VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION.equals(dossierLink.getRoutingTaskType())) {
				// Là on indique qu'on a déjà cliqué une fois sur valider l'étape
				dossierLink.setDateDebutValidation(Calendar.getInstance());
				dossierLink.save(documentManager);
			}

		} catch (final SignatureException se) {
			// le dossier n'a pas été signé
			LOGGER.warn(documentManager, ReponsesLogEnumImpl.FAIL_SIGN_DOSSIER_FONC, se);
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(se.getMessage()));
			return null;
		} catch (final StepValidationException sve) {
			if (StepValidationException.CAUSEEXC.SIGNATURE_INVALID.equals(sve.getCauseExc())) {
				try {
					manageInvalidTransmissionAssemblees(dossierLink, dossierDoc);
				} catch (CopieStepException e) {
					facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(e.getMessage()));
					return null;
				}
			} else {
				displayInternalError(sve);
				return null;
			}
		} catch(final ReponsesException re) {
			LOGGER.warn(documentManager, SSLogEnumImpl.FAIL_UPDATE_FDR_TEC, re.getMessage());
			if (LOGGER.isDebugEnable()) {
				LOGGER.warn(documentManager, SSLogEnumImpl.FAIL_UPDATE_FDR_TEC, re);
			}
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(re.getMessage()));
			return null;
		} catch (final ClientException ce) {
			displayInternalError(ce);
			return null;
		}

		return unloadDossierAndDossierLink(true);
	}

	private void displayInternalError(ClientException ce) {
		LOGGER.warn(documentManager, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, ce);
		facesMessages.add(StatusMessage.Severity.ERROR,
				resourcesAccessor.getMessages().get("label.reponses.feuilleRoute.validation.internal.error"));
	}

	private void manageInvalidTransmissionAssemblees(DossierLink dossierLink, DocumentModel dossierDoc) throws ClientException {
		final FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		try {
			DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(documentManager).getById(dossierLink
					.getRoutingTaskId());
			final ReponsesRouteStep etapeCourante = etapeDoc.getAdapter(ReponsesRouteStep.class);
			feuilleRouteService.addStepsSignatureAndTransmissionAssemblees(documentManager, dossierDoc,
					etapeCourante);
			getDossierDistributionService().validerEtapeRefus(documentManager, dossierDoc, dossierLink.getDocument());
			String validationMessage = MessageFormat.format(resourcesAccessor.getMessages().get("label.reponses.feuilleRoute.validation.signature.invalid"),
					PropertyUtil.getStringProperty(dossierDoc, DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_LABEL_ETAPE_SUIVANTE));
			facesMessages.add(StatusMessage.Severity.WARN, validationMessage);
		} catch (ReponsesException ce) {
			displayInternalError(ce);
		}
	}

	/**
	 * Donne un avis défavorable sur le DossierLink en cours. Sans ajout d'étape.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String donnerAvisDefavorableEtPoursuivre() throws ClientException {
		String view = donnerAvisDefavorable(Boolean.FALSE);
		if (view != null) {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.refusEtContinue"));
		}
		return view;
	}

	/**
	 * Donne un avis défavorable sur le DossierLink en cours. Avec ajout d'étape.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String donnerAvisDefavorableEtInsererTaches() throws ClientException {
		String view = donnerAvisDefavorable(Boolean.TRUE);
		return view;
	}

	/**
	 * Donne un avis défavorable sur le DossierLink en cours. Et retourne le dossier au ministère attributaire dans une
	 * étape "pour attribution
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String donnerAvisDefavorableEtRetourBdcAttributaire() throws ClientException {
		String view = donnerAvisDefavorable(Boolean.FALSE);
		if (view != null) {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.refusEtRetourBDC"));
		}
		return view;
	}

	/**
	 * Donne un avis défavorable sur le DossierLink en cours.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	private String donnerAvisDefavorable(final Boolean ajoutTache) throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		// Récupération du dossier en session
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		boolean isStepAjoute = false;
		checkAndUpdateReponse();

		try {
			reponseActions.resetReponse();
			dossierLockActions.lockCurrentDossier();
			FacesMessages.afterPhase();
			facesMessages.clear();
			// update old etape field and save
			if (Boolean.TRUE.equals(ajoutTache)) {
				isStepAjoute = ReponsesServiceLocator.getFeuilleRouteService().addStepAfterReject(documentManager,
						dossierLink.getRoutingTaskId(), dossier);
			}
			getDossierDistributionService().rejeterDossierLink(documentManager, dossierDoc, dossierLink.getDocument());

			// Là on indique qu'on a déjà cliqué une fois sur valider l'étape
			dossierLink.setDateDebutValidation(Calendar.getInstance());
			dossierLink.save(documentManager);

		} catch (final CopieStepException e){
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(e.getMessage()));
			return null;
		} catch (final ClientException ce) {
			if (ce instanceof ReponsesException || ce instanceof SSException) {
				LOGGER.warn(documentManager, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, ce.getMessage());
			} else {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, ce);
			}
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ce.getMessage()));
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_ADD_STEP));
			return null;
		} finally {
			dossierLockActions.unlockCurrentDossier();
		}

		// Si message non nul : Cas où les étape on été créés manuellement
		// le message affiché est donc spécifique
		if (ajoutTache && isStepAjoute) {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.refusEtRetour"));
		} else if (ajoutTache) {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.refusEtRetourAjoutManuelle"));
		}
		return unloadDossierAndDossierLink(true);
	}

	/**
	 * Demande un arbitrage du SGG sur le DossierLink en cours.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean demandeArbitrageSGG() {
		final DocumentModel dossierLinkDoc = corbeilleActions.getCurrentDossierLink().getDocument();
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		// Lock du dossier et de la route
		if (!dossierActions.canDemandeArbitrageSGG()) {
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_ADD_STEP));
			return false;
		}

		try {
			dossierLockActions.lockCurrentDossier();
			try {
				getReponsesArbitrageService().addStepArbitrageSGG(documentManager, dossierDoc, dossierLinkDoc);
				facesMessages.add(StatusMessage.Severity.INFO,
						resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.etapePourArbitrage"));
				// Là on indique qu'on a déjà cliqué une fois sur le bouton
				dossierLink.setDateDebutValidation(Calendar.getInstance());
				dossierLink.save(documentManager);
			} catch (ClientException exc) {
				LOGGER.error(documentManager, ReponsesLogEnumImpl.FAIL_ADD_STEP_ARBITRAGE_FONC, exc);
				facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_ADD_STEP));
				return false;
			} finally {
				dossierLockActions.unlockCurrentDossier();
			}
		} catch (ClientException exc) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_UNLOCK_DOC_FONC, exc);
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.traitement"));
			return false;
		}

		return true;
	}

	/**
	 * Met le dossier courant en attente.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String mettreEnAttente() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();

		// Lock du dossier et de la route
		dossierLockActions.lockCurrentDossier();

		try {
			final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(documentManager).getById(dossierLink
					.getRoutingTaskId());
			ReponsesServiceLocator.getFeuilleRouteService().addStepAttente(documentManager, etapeDoc);
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.etapePourAttente"));
		} finally {
			dossierLockActions.unlockCurrentDossier();
		}

		return null;
	}

	/**
	 * ajoute les étapes 'Pour retour', 'Pour signature' et 'pour validation retour Premier ministre' au dossier
	 * courant.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 *             ClientException
	 */
	public String validationRetourPM() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		Dossier dossier = dossierLink.getDossier(documentManager);

		// Lock du dossier et de la route
		dossierLockActions.lockCurrentDossier();

		try {
			final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(documentManager).getById(dossierLink
					.getRoutingTaskId());
			ReponsesServiceLocator.getFeuilleRouteService().addStepValidationRetourPM(documentManager, etapeDoc,
					dossier.getDocument());
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.etapePourValidationPM"));
		} catch (ReponsesException exc) {
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_ADD_STEP));
		} finally {

			dossierLockActions.unlockCurrentDossier();
		}

		return null;
	}

	/**
	 * Effectue la fonctionnalité d'attribution après arbitrage
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String attributionApresArbitrage() throws ClientException {

		if (!checkObligatoryStringParameters(reattributionMinistere)) {
			return null;
		}

		checkAndUpdateReponse();

		// Récupération du dossier link
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		// Récupération du dossier
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		try {
			getReponsesArbitrageService().attributionAfterArbitrage(documentManager, dossierLink, dossierDoc,
					reattributionMinistere, reattributionObservations);
		} catch (final ClientException exc) {
			LOGGER.error(documentManager, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, exc);
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.noRouteFound"));
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("label.reponses.feuilleRoute.validation.internal.error"));
			return null;
		}

		String validationFormat = null;
		// si le dossier a été réattribué on recharge le label de(s) étape(s)
		validationFormat = resourcesAccessor.getMessages().get(
				"label.reponses.feuilleRoute.message.etape.validation.reattribution");
		displayMessageInfo(validationFormat, dossier.getNumeroQuestion(),
				getLabelEntite(dossier.getIdMinistereAttributaireCourant()));
		resetAttributionApresArbitrageSelection();
		return unloadDossierAndDossierLink(true);

	}

	/**
	 * Récupère le label d'une entite par son id
	 * 
	 * @param idMinistere
	 * @return
	 * @throws ClientException
	 */
	private String getLabelEntite(String idMinistere) throws ClientException {
		final OrganigrammeNode node = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistere);
		String labelMin = "";
		if (node != null) {
			labelMin = node.getLabel();
		}
		return labelMin;
	}

	/**
	 * Affiche un message à l'utilisateur en mode info
	 * 
	 * @param validationFormat
	 * @param arguments
	 */
	private void displayMessageInfo(String validationFormat, Object... arguments) {
		String validationMessage = MessageFormat.format(validationFormat, arguments);
		facesMessages.add(StatusMessage.Severity.INFO, validationMessage);
	}

	/**
	 * Retourne la vue après dechargerDossierLink
	 * 
	 * @param keepCorbeille garde-t-on la corbeille courante ?
	 * @return
	 * @throws ClientException
	 */
	private String unloadDossierAndDossierLink(boolean keepCorbeille) throws ClientException {
		// Décharge le dossier
		navigationContext.resetCurrentDocument();
		// Décharge le DossierLink et rafraichit la corbeille
		return corbeilleActions.dechargerDossierLink(keepCorbeille);
	}
	
	private String unloadDossierAndDossierLink() throws ClientException {
		return unloadDossierAndDossierLink(false);
	}

	/**
	 * Vérifie si la réponse a été modifiée, et incrémente la version au besoin
	 * 
	 * @throws ClientException
	 */
	private void checkAndUpdateReponse() throws ClientException {
		// Mise à jour de la réponse
		if (reponseActions.reponseHasChanged()) {
			ReponsesServiceLocator.getReponseService().incrementReponseVersion(documentManager,
					reponseActions.getReponse());
		}
	}

	/**
	 * Vérifie les paramètres String obligatoires (l'id ministère par exemple)
	 * 
	 * @return
	 */
	private boolean checkObligatoryStringParameters(String... parameters) {
		for (String parameter : parameters) {
			if (StringUtil.isEmpty(parameter)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("feedback.reponses.error.missing.parameter"));
				return false;
			}
		}
		return true;
	}

	/**
	 * Annule la sélection du ministère et observations Utilisé dans attribution_apres_arbitrage_panel
	 */
	public void resetAttributionApresArbitrageSelection() {
		this.reattributionObservations = null;
		this.reattributionMinistere = null;
	}

	public String reattributionDirecte() throws ClientException {
		if (!getDossier().getAdapter(Dossier.class).isArbitrated()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.cannotReattributionDirecte"));
			return corbeilleActions.getCurrentView();
		}

		if (!checkObligatoryStringParameters(reattributionMinistere)) {
			return null;
		}

		reponseActions.resetReponse();

		// Lock du dossier et de la route
		dossierLockActions.lockCurrentDossier();

		// Récupération du dossier
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		try {
			getReponsesArbitrageService().reattributionDirecte(documentManager,
					corbeilleActions.getCurrentDossierLink(), dossierDoc, reattributionMinistere,
					reattributionObservations);
			// Là on indique qu'on a déjà cliqué une fois sur le bouton
			final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
			dossierLink.setDateDebutValidation(Calendar.getInstance());
			dossierLink.save(documentManager);
		} catch (final ClientException exc) {
			LOGGER.error(documentManager, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, exc);
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.noRouteFound"));
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("label.reponses.feuilleRoute.validation.internal.error"));
			return null;
		} finally {
			dossierLockActions.unlockCurrentDossier();
			resetReattributionDirecteSelection();
			unloadDossierAndDossierLink(true);
		}

		String validationFormat = null;
		// si le dossier a été réattribué on recharge le label de(s) étape(s)
		validationFormat = resourcesAccessor.getMessages().get(
				"label.reponses.feuilleRoute.message.etape.validation.reattribution");
		displayMessageInfo(validationFormat, dossier.getNumeroQuestion(),
				getLabelEntite(dossier.getIdMinistereAttributaireCourant()));

		return corbeilleActions.getCurrentView();
	}

	/**
	 * Annule la sélection du ministère et observations Utilisé dans reattribution_directe_panel
	 */
	public void resetReattributionDirecteSelection() {
		resetAttributionApresArbitrageSelection();
	}

	/**
	 * Cas de l'étape non concerné sans passage par la popup isNextStepReorientationOrReattribution == true
	 *
	 * @return null
	 * @throws ClientException
	 */
	public String nonConcerne() throws ClientException {
		reponseActions.resetReponse();

		// Là on indique qu'on a déjà cliqué une fois sur le bouton
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		dossierLink.setDateDebutValidation(Calendar.getInstance());
		dossierLink.save(documentManager);

		// Valide l'étape "non concerné"
		getDossierDistributionService().validerEtapeNonConcerne(documentManager,
				navigationContext.getCurrentDocument(), corbeilleActions.getCurrentDossierLink().getDocument());
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.nonConcerne"));

		return unloadDossierAndDossierLink(true);
	}

	/**
	 * Réattribution à un autre ministère
	 * 
	 * @return vrai si la reattribution a pu se faire
	 * @throws ClientException
	 */
	public boolean nonConcerneReattribution() throws ClientException {
		if (!checkObligatoryStringParameters(selectedMinForReattribution)) {
			return false;
		}

		if (getDossier().getAdapter(Dossier.class).isArbitrated()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.cannotReattribution"));
			return false;
		}

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(documentManager).getById(dossierLink
				.getRoutingTaskId());
		final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);
		final DocumentModel feuilleRouteDoc = documentManager.getDocument(new IdRef(dossier.getLastDocumentRoute()));
		final ReponsesFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);

		// Là on indique qu'on a déjà cliqué une fois sur le bouton
		dossierLink.setDateDebutValidation(Calendar.getInstance());
		dossierLink.save(documentManager);

		reponseActions.resetReponse();

		// Lock du dossier et de la route
		dossierLockActions.lockCurrentDossier();

		// Récupère le poste BDC du ministère, ajoute l'étape réattribution et
		// stocke le ministère choisi dans le dossier
		try {
			final OrganigrammeNode posteBdc = STServiceLocator.getSTPostesService().getPosteBdcInEntite(
					selectedMinForReattribution);
			if (posteBdc == null) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("feedback.reponses.dossier.error.reattributionNoBdc"));
				return false;
			}
			final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
			final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
			mailboxPosteService.getOrCreateMailboxPoste(documentManager,
					mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
			ReponsesServiceLocator.getFeuilleRouteService().addStepAfterReattribution(documentManager, etapeDoc,
					mailboxId);

			// Changement du ministère courant des dossiers liés à la feuille de
			// route
			for (final String dossierId : feuilleRoute.getAttachedDocuments()) {
				final DocumentModel dossierAttachDoc = documentManager.getDocument(new IdRef(dossierId));
				final Dossier dossierAttach = dossierAttachDoc.getAdapter(Dossier.class);
				dossierAttach.setIdMinistereReattribution(selectedMinForReattribution);
				documentManager.saveDocument(dossierAttachDoc);
			}
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.etapePourReattribution"));
		} finally {
			dossierLockActions.unlockCurrentDossier();
			resetMinSelection();
		}

		// Journalise l'action
		STServiceLocator.getJournalService().journaliserActionEtapeFDR(documentManager, etape, dossierDoc,
				ReponsesEventConstant.DOSSIER_REATTRIBUTION_EVENT, ReponsesEventConstant.COMMENT_DOSSIER_REATTRIBUTION);

		return true;
	}

	/**
	 * Réattribution à un autre ministère
	 * 
	 * @return vrai si la reattribution a pu se faire
	 * @throws ClientException
	 */
	public boolean nonConcerneReattribution(String minReattribution) throws ClientException {
		selectedMinForReattribution = minReattribution;
		return nonConcerneReattribution();
	}

	/**
	 * Réorientation au sein d'un même ministère
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String nonConcerneReorientation() throws ClientException {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(documentManager).getById(dossierLink
				.getRoutingTaskId());
		final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);

		reponseActions.resetReponse();

		// Lock du dossier et de la route
		dossierLockActions.lockCurrentDossier();

		try {
			ReponsesServiceLocator.getFeuilleRouteService().addStepAfterReorientation(documentManager, etapeDoc);
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.etapePourReorientation"));
			// Là on indique qu'on a déjà cliqué une fois sur le bouton
			dossierLink.setDateDebutValidation(Calendar.getInstance());
			dossierLink.save(documentManager);
		} catch (final PosteNotFoundException e) {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("feedback.reponses.dossier.poste.reorientation.error"));
			LOGGER.warn(documentManager, STLogEnumImpl.FAIL_GET_POSTE_FONC);
		} finally {
			dossierLockActions.unlockCurrentDossier();
		}

		// Journalise l'action
		STServiceLocator.getJournalService().journaliserActionEtapeFDR(documentManager, etape, dossierDoc,
				ReponsesEventConstant.DOSSIER_REORIENTATION_EVENT, ReponsesEventConstant.COMMENT_DOSSIER_REORIENTATION);

		return null;
	}

	/**
	 * retourne la liste de selection en fonction de la vue
	 * 
	 * @param view
	 * @return
	 */
	public static String retrieveSelectionListAccordingView(final String view) {
		String selectionList = null;
		if (ReponsesViewConstant.ESPACE_CORBEILLE_VIEW.equals(view)) {
			selectionList = ReponsesConstant.CORBEILLE_SELECTION;
		} else if (ReponsesViewConstant.PLAN_CLASSEMENT_VIEW.equals(view)) {
			selectionList = ReponsesConstant.PLAN_CLASSEMENT_SELECTION;
		} else if (ReponsesViewConstant.VIEW_REQUETE_RESULTS.equals(view)) {
			selectionList = ReponsesConstant.RECHERCHE_SELECTION;
		} else if (ReponsesViewConstant.ESPACE_SUIVI.equals(view)) {
			selectionList = ReponsesConstant.RECHERCHE_SELECTION;
		}
		return selectionList;
	}

	/**
	 * Annule la sélection du ministère. Utilisé dans unconcerned_task_panel
	 */
	public void resetMinSelection() {
		selectedMinForReattribution = null;
	}

	/**
	 * Substitution de la feuille de route en cours : - Annule la feuille de route ; - Démarre une nouvelle feuille de
	 * route.
	 * 
	 * @return Vue de la liste des dossiers
	 * @throws ClientException
	 */
	public String substituerRoute() throws ClientException {
		// Récupère la nouvelle feuille de route sélectionnée par l'utilisateur
		final String routeId = routingActions.getRelatedRouteModelDocumentId();
		DocumentModel newRouteDoc = null;
		if (routeId != null && !"".equals(routeId)) {
			newRouteDoc = documentManager.getDocument(new IdRef(routeId));
		}
		if (newRouteDoc == null) {
			// Aucun modèle n'est sélectionné
			final String message = resourcesAccessor.getMessages().get(
					"feedback.reponses.document.route.no.valid.route");
			facesMessages.add(StatusMessage.Severity.WARN, message);

			return null;
		}

		// Déverrouille le dossier si verrouille
		if (dossierLockActions.getCanUnlockCurrentDossier()) {
			dossierLockActions.unlockCurrentDossier();
		}

		// Récupère l'ancienne instance de feuille de route
		final STFeuilleRoute oldRoute = routingActions.getRelatedRoute();
		DocumentModel oldRouteDoc = null;
		if (oldRoute != null) {
			oldRouteDoc = oldRoute.getDocument();
		}

		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		// Substitue la feuille de route
		getDossierDistributionService().substituerFeuilleRoute(documentManager, dossierDoc, oldRouteDoc, newRouteDoc,
				STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_SUBSTITUTION);

		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		final Question question = dossier.getQuestion(documentManager);
		if (question.isQuestionTypeEcrite()) {
			final ReponsesFeuilleRoute newFeuilleRoute = newRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
			getDossierDistributionService().setDirectionPilote(documentManager, question,
					newFeuilleRoute.getIdDirectionPilote());
			documentManager.saveDocument(question.getDocument());
		}

		// Réinitialise la sélection de la feuille de route
		routingActions.resetRelatedRouteDocumentId();

		// Journalise de l'évenement
		STServiceLocator.getJournalService().journaliserActionFDR(documentManager, dossierDoc,
				STEventConstant.DOSSIER_SUBSTITUER_FEUILLE_ROUTE, STEventConstant.COMMENT_SUBSTITUER_FEUILLE_ROUTE);

		// envoi message information utilisateur
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("st.dossierDistribution.action.substituer.success"));

		// Recharge le dossierLink
		final String dossierId = dossierDoc.getId();
		// Décharge le dossier
		navigationContext.resetCurrentDocument();
		// Décharge le DossierLink et rafraichit la corbeille
		corbeilleActions.dechargerDossierLink();

		dossierListingActions.navigateToDossier(documentManager.getDocument(new IdRef(dossierId)));

		final String view = routingWebActions.getFeuilleRouteView();
		if (corbeilleActions.getCurrentDossierLink() == null && corbeilleActions.getCurrentView().equals(view)) {
			// on est dans la corbeille mais le dossier link est plus chez nous
			// Décharge le dossier
			navigationContext.resetCurrentDocument();
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("reponses.dossierLink.notreadable"));
		}

		return view;
	}

	/**
	 * Redémarre un dossier dont la feuille de route a été terminée.
	 * 
	 * @return Vue
	 * @throws ClientException
	 *             ClientException
	 */
	public String redemarrerDossier() throws ClientException {
		// Déverrouille le dossier si il est verrouillé
		if (dossierLockActions.getCanUnlockCurrentDossier()) {
			dossierLockActions.unlockCurrentDossier();
		}

		// Redémarre le dossier
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		getDossierDistributionService().restartDossier(documentManager, dossierDoc);

		// Affiche un message d'information
		String message = resourcesAccessor.getMessages().get("reponses.distribution.action.restart.success");
		facesMessages.add(StatusMessage.Severity.INFO, message);

		// Affiche un message si la feuille de route est redémarré pour effectuer un errata
		if (reponseActions.isReponsePublished()) {
			message = resourcesAccessor.getMessages().get("reponses.distribution.action.restart.for.errata");
			facesMessages.add(StatusMessage.Severity.INFO, message);
		}

		// Décharge le dossier
		navigationContext.resetCurrentDocument();

		// Décharge le DossierLink et rafraichit la corbeille
		return corbeilleActions.dechargerDossierLink();
	}

	/**
	 * Retourne vrai si un DossierLink a été chargé, c'est à dire que l'utilisateur peut agir sur l'étape en cours en
	 * tant que destinataire ou administrateur.
	 * 
	 * @return Condition
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean isDossierLinkLoaded() throws ClientException {
		try {
			DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
			if (dossierLink == null) {
				return false;
			} else {
				// Vérifie la lecture sur le dossier link
				String routingTaskType = dossierLink.getRoutingTaskType();
				return routingTaskType != null;
			}
		} catch (Exception exc) {
			LOGGER.debug(documentManager, STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, exc);
			return false;
		}
	}

	@Factory(value = "listeDocumentPublicReponse", scope = EVENT)
	public List<FondDeDossierFile> getListeDocumentPublicReponse() throws ClientException {
		List<FondDeDossierFile> listeDocumentPublicReponse = new ArrayList<FondDeDossierFile>();
		final FondDeDossierService fddService = ReponsesServiceLocator.getFondDeDossierService();
		final DocumentModel fddDocument = fddService.getFondDeDossierFromDossier(documentManager, getDossier());
		if (fddDocument != null) {
			listeDocumentPublicReponse = fddService.getFondDeDossierPublicDocument(documentManager, fddDocument);
		}
		return listeDocumentPublicReponse;
	}

	@Observer(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_TREE_CHANGED_EVENT)
	public void reloadListDocumentPublicReponse() throws ClientException {
		getListeDocumentPublicReponse();
	}

	/**
	 * @return the selectedMinForReattribution
	 */
	public String getSelectedMinForReattribution() {
		return selectedMinForReattribution;
	}

	/**
	 * @param selectedMinForReattribution
	 *            the selectedMinForReattribution to set
	 */
	public void setSelectedMinForReattribution(final String selectedMinForReattribution) {
		this.selectedMinForReattribution = selectedMinForReattribution;
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut éditer le minsitere racttachement
	 * 
	 * @return Condition
	 */
	public Boolean getCanEditMinistereRattachement() {
		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.MINISTERE_RATTACHEMENT_UPDATER)) {
			return true;
		}
		return false;
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut éditer direction pilote
	 * 
	 * @return Condition
	 */
	public Boolean getCanEditDirectionPilote() {
		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DIRECTION_PILOTE_UPDATER)) {
			return true;
		}
		return false;
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut éditer les dossiers connexes.
	 * 
	 * @return Condition
	 */
	public Boolean canReadDossierConnexe() {
		return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER);
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut lire les dossiers allotis.
	 * 
	 * @return Condition
	 */
	public Boolean canReadAllotissement() {
		return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER);
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut modifier les dossiers allotis.
	 * 
	 * @return Condition
	 */
	public Boolean canUpdateAllotissement() {
		return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_UPDATER);
	}

	/**
	 * reset l'id utilisé pour la selection de la feuille de route
	 * 
	 * @return la vue pour selectionner la feuille de route
	 */
	public String navigateToFdrModelSelection() {
		routingActions.resetRelatedRouteDocumentId();
		return "view_modeles_feuille_route_for_selection";
	}

	public String getReattributionObservations() {
		return reattributionObservations;
	}

	public void setReattributionObservations(String reattributionObservations) {
		this.reattributionObservations = reattributionObservations;
	}

	public String getReattributionMinistere() {
		return reattributionMinistere;
	}

	public void setReattributionMinistere(String reattributionMinistere) {
		this.reattributionMinistere = reattributionMinistere;
	}

	private ReponsesArbitrageService getReponsesArbitrageService() {
		if (arbitrageService == null) {
			arbitrageService = ReponsesServiceLocator.getReponsesArbitrageService();
		}
		return arbitrageService;
	}

	private DossierDistributionService getDossierDistributionService() {
		if (dossierDistributionService == null) {
			dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
		}
		return dossierDistributionService;
	}

	/**
	 * Retourne vrai si l'étape est en cours et que l'utilisateur a déjà lancé l'étape et faux si l'étape a bien été
	 * terminée. Le but est d'éviter les dossiers link incohérents
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isEtapeEnCoursValidation() throws ClientException {
		try {
			DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
			if (dossierLink == null) {
				return false;
			} else {
				DateTime now = new DateTime();
				if (dossierLink.getDateDebutValidation() == null) {
					return false;
				} else {
					// On ajoute 4 minutes à la date de validation
					DateTime dateValidationEtape = new DateTime(dossierLink.getDateDebutValidation());
					dateValidationEtape = dateValidationEtape.plusMinutes(4);
					if (now.compareTo(dateValidationEtape) >= 1) {
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (Exception exc) {
			LOGGER.debug(documentManager, STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, exc);
			return false;
		}
	}

	/**
	 * Retourne faux si l'étape est en cours et que l'utilisateur a déjà lancé l'étape et vrai si l'étape a bien été
	 * terminée. Le but est d'éviter les dossiers link incohérents
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isNotEtapeEnCoursValidation() throws ClientException {
		try {
			DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
			if (dossierLink == null) {
				return true;
			} else {
				DateTime now = new DateTime();

				if (dossierLink.getDateDebutValidation() == null) {
					return true;
				} else {
					// On ajoute 4 minutes à la date de validation
					DateTime dateValidationEtape = new DateTime(dossierLink.getDateDebutValidation());
					dateValidationEtape = dateValidationEtape.plusMinutes(4);
					if (now.compareTo(dateValidationEtape) >= 1) {
						return true;
					} else {
						return false;
					}
				}
			}
		} catch (Exception exc) {
			LOGGER.debug(documentManager, STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, exc);
			return false;
		}
	}
}
