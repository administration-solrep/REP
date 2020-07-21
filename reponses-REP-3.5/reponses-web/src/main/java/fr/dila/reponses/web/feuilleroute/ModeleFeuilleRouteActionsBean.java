package fr.dila.reponses.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.core.recherche.ReponsesMinimalEscaper;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STContentViewConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Actions permettant de gérer un modèle de feuille de route dans Réponses.
 * 
 * @author bgamard
 */
@Name("modeleFeuilleRouteActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class ModeleFeuilleRouteActionsBean extends fr.dila.ss.web.feuilleroute.ModeleFeuilleRouteActionsBean implements
		Serializable {

	private static final long						serialVersionUID	= 1L;

	@In(create = true, required = false)
	protected transient ContentViewActions			contentViewActions;
	// Liste des feuilles de route sélectionnées
	protected transient DocumentModelList			fdrSelected;
	@In(create = true, required = false)
	protected transient DocumentsListsManagerBean	documentsListsManager;
	@In(create = true, required = false)
	protected transient DocumentRoutingActionsBean	docRoutingActionBean;

	/**
	 * Champ pour la recherche rapide par titre.
	 */
	private String									searchTitle;
	/**
	 * Champ pour la suppression par type d'étape
	 */
	private String									etapeType;
	/**
	 * Champ pour la suppression par poste de l'étape
	 */
	private String									etapePoste;
	/**
	 * Champ pour la suppression - sauvegarde du content view
	 */
	private ContentView								contentViewAvantSuppression;
	/**
	 * Nombre de fdr total
	 */
	private Integer									totalMassDocument;
	/**
	 * Nombre de fdr déjà traitées
	 */
	private Integer									doneMassDocument;

	@Override
	public String getContentViewCriteria() throws ClientException {
		StringBuilder sb = new StringBuilder(super.getContentViewCriteria());

		// Ajout du SQL pour la recherche rapide par titre
		if (StringUtils.isNotBlank(searchTitle)) {
			ReponsesMinimalEscaper escaper = new ReponsesMinimalEscaper();
			sb.append(" AND ").append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX).append(":")
					.append(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY)
					.append(" ILIKE \"%" + escaper.escape(searchTitle) + "%\"");
		}

		return sb.toString();
	}

	/**
	 * Lancement de la recherche rapide.
	 * 
	 * @return
	 */
	public String quickSearch() {
		contentViewActions.reset(STContentViewConstant.FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW);
		return null;
	}

	/**
	 * Réinitialisation de la recherche rapide.
	 * 
	 * @return
	 */
	public String resetQuickSearch() {
		searchTitle = null;
		contentViewActions.reset(STContentViewConstant.FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW);
		return null;
	}

	/**
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * @param searchTitle
	 *            the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * return le type de l'étape à supprimer dans la FDR
	 * 
	 * @return
	 */
	public String getEtapeType() {
		return etapeType;
	}

	/**
	 * set the etape type
	 * 
	 * @param etapeType
	 */
	public void setEtapeType(String etapeType) {
		this.etapeType = etapeType;
	}

	/**
	 * 
	 * @return le poste des étapes à supprimer dans les FDR
	 */
	public String getEtapePoste() {
		return etapePoste;
	}

	/**
	 * Set le type de poste pour les étapes à supprimer dans les FDR
	 * 
	 * @param etapePoste
	 */
	public void setEtapePoste(String etapePoste) {
		this.etapePoste = etapePoste;
	}

	/**
	 * Set le contentView avant suppression d'étapes dans les modèles de feuille de route
	 */
	public void setContentViewAvantSuppression(ContentView contentView) {
		this.contentViewAvantSuppression = contentView;
	}

	/**
	 * getter pour le contentView avant suppression d'étapes dans les modèles de feuille de route
	 */
	public ContentView getContentViewAvantSuppression() {
		return contentViewAvantSuppression;
	}

	/**
	 * Prépare la suppression d'une étape de plusieurs feuilles de route
	 * 
	 * @return
	 */
	public String prepareDeleteMultipleStepsFromRoute() throws ClientException {
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		// Sauvegarde du contentview pour le récupérer une fois qu'on aura fini la suppression des étapes
		contentViewAvantSuppression = contentViewActions.getCurrentContentView();
		// Ajout des feuilles de route à la liste de feuilles de route sélectionnées
		fdrSelected = new DocumentModelListImpl(getModeleFDRFromSelection(selectionListName));
		totalMassDocument = fdrSelected.size();
		doneMassDocument = 0;
		return "prepare_delete_multiple_steps_from_route";
	}

	/**
	 * Effectue la suppression des étapes pour une unique FDR (identifiée par son index dans la liste)
	 * 
	 * @param index
	 * @throws ClientException
	 */
	@WebRemote
	public void deleteStepsFromSingleRoute(final int index) throws ClientException {
		DocumentModel docModel = fdrSelected.get(index);
		// De là on va essayer de récupérer la feuille de route correspondante
		DocumentRouteElement relatedRouteElement = docModel.getAdapter(DocumentRouteElement.class);
		DocumentRoute documentRoute = relatedRouteElement.getDocumentRoute(documentManager);
		// Ensuite on récupère les étapes de la feuille de route
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		List<DocumentModel> allElementsFDR = new ArrayList<DocumentModel>();
		// Nouvelle méthode pour rechercher toutes les étapes du modèle de FDR
		final STFeuilleRoute feuilleRoute = docModel.getAdapter(STFeuilleRoute.class);
		final List<DocumentRouteTableElement> listRouteTableElement = documentRoutingService.getFeuilleRouteElements(
				feuilleRoute, documentManager);
		// Construction du tableau qui contient les étapes de FDR
		for (final DocumentRouteTableElement routeTableElement : listRouteTableElement) {
			if (!routeTableElement.getDocument().isFolder()) {
				STRouteStep step = routeTableElement.getRouteStep();
				allElementsFDR.add(step.getDocument());
			}
		}

		List<DocumentModel> listStepASupprimer = new ArrayList<DocumentModel>();
		for (final DocumentModel stepModel : allElementsFDR) {
			// On vérifie avant tout que l'étape de FDR correspond bien à la FDR courante
			// Permet de corriger des instabilités lors de la recherche d'étapes de la FDR
			ReponsesRouteStep step = null;
			step = stepModel.getAdapter(ReponsesRouteStep.class);
			if (step != null
					&& step.getDocumentRoute(documentManager).getDocument().getId().toString().equals(docModel.getId())) {
				String posteId = mailboxPosteService.getPosteIdFromMailboxId(step.getDistributionMailboxId());
				if (posteId.equals(etapePoste) && step.getType().equals(etapeType)) {
					// Ajout de l'étape concernée à la liste de suppression
					listStepASupprimer.add(stepModel);
				}
			}
		}
		if (!listStepASupprimer.isEmpty()) {
			try {
				// Dans un premier temps on essaie de délocker la feuille de route (au cas où elle aurait été lockée
				// avant)
				final STLockService stLockService = STServiceLocator.getSTLockService();
				try {
					documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
				} catch (DocumentRouteNotLockedException exe) {
					// Do nothing
					if (stLockActions.isDocumentLockedByAnotherUser(docModel)) {
						// Là on va essayer de lever le verrou posé par un autre utilisateur
						try {
							stLockService.unlockDocUnrestricted(documentManager, docModel);
						} catch (ClientException e) {
							// Pas possible de dévrouiller la feuille de route. On passe à la suivante
							final String message = "Impossible de dévrouiller la feuille de route " + docModel.getId();
							facesMessages.add(StatusMessage.Severity.ERROR, message);
						}
					}
				} catch (ClientException exe) {
					// Do nothing
				}
				// là on va essayer de locker la feuille de route
				stLockService.lockDoc(documentManager, docModel);
			} catch (DocumentRouteNotLockedException e) {
				// continue
			} catch (DocumentRouteAlredayLockedException ex) {
				// Do nothing
			} catch (ClientException ce) {
				// là on met une erreur dans les logs et on passe à la fdr suivante
				final String message = "Impossible de verrouiller la feuille de route " + docModel.getId();
				facesMessages.add(StatusMessage.Severity.ERROR, message);
			}
			for (DocumentModel stepDelete : listStepASupprimer) {
				try {
					// Suppression de chaque étape listée plus tôt dans la feuille de route courante
					documentRoutingService.removeRouteElement(stepDelete.getAdapter(DocumentRouteElement.class),
							documentManager);
					// Analyse de si l'étape fait partie d'une branche ou pas
					DocumentModel parent = new UnrestrictedGetDocumentRunner(documentManager).getByRef(stepDelete
							.getParentRef());
					if (parent.getType().equals("StepFolder")) {
						// Si on passe ici, c'est qu'on est dans une branche
						Boolean doDelete = true;
						// Suppression des parents si on ne trouve plus que des branches vides
						while (doDelete) {
							// Actualisation des infos pour remonter un cran au dessus
							DocumentModel parentStepFolderDoc = new UnrestrictedGetDocumentRunner(documentManager)
									.getByRef(parent.getParentRef());
							documentRoutingService.removeRouteElement(parent.getAdapter(DocumentRouteElement.class),
									documentManager);
							// Si on est plus dans une étape // ou si on trouve une branche on arrête de supprimer
							// le parent
							if (!parentStepFolderDoc.getType().equals("StepFolder")) {
								doDelete = false;
							} else {
								// Le type est bien StepFolder. On peut donc récupérer le doc avec l'adapter
								StepFolder parentStepFolder = parentStepFolderDoc.getAdapter(StepFolder.class);
								if (parentStepFolder.isParallel()) {
									// Si on a une branche pas vide, alors on ne supprime pas la suite
									// On regarde s'il a encore des enfants
									final List<DocumentModel> children = documentManager
											.getChildren(parentStepFolderDoc.getRef());
									if (children.size() == 0) {
										parent = parentStepFolderDoc;
										doDelete = true;
									} else {
										doDelete = false;
									}
								} else {
									// le nouveau parent est l'ancien ParentStepFolderDoc
									parent = parentStepFolderDoc;
									doDelete = true;
								}

							}

						}
					}

				} catch (LocalizedClientException e) {
					String message = resourcesAccessor.getMessages().get(e.getMessage());
					facesMessages.add(StatusMessage.Severity.WARN, message);
					try {
						documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
					} catch (DocumentRouteNotLockedException ex) {
						break;
					} catch (ClientException ex) {
						break;
					}
					throw new LocalizedClientException(message);
				} catch (DocumentRouteNotLockedException e) {
					final String message = resourcesAccessor.getMessages().get(
							"feedback.casemanagement.document.route.not.locked");
					facesMessages.add(StatusMessage.Severity.WARN, message);
					throw new DocumentRouteNotLockedException(message);
				}
			}
			// Passage de la feuille de route modifiée à l'état brouillon
			try {
				if (documentRoute.isValidated()) {
					documentRoutingService.invalidateRouteModel(documentRoute, documentManager);
				}
			} catch (DocumentRouteNotLockedException e) {
				String errorMessage = resourcesAccessor.getMessages().get(
						"feedback.casemanagement.document.route.not.locked");
				facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
			}
			try {
				documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
			} catch (DocumentRouteNotLockedException e) {
				// Do nothing
			} catch (ClientException ex) {
				// Do nothing
			}
		}
		doneMassDocument = doneMassDocument + 1;
	}

	/**
	 * Effectue la suppression des étapes dans les feuilles de route sélectionnées
	 */
	public String deleteMultipleStepsFromRoute() throws ClientException {

		for (DocumentModel docModel : fdrSelected) {
			// De là on va essayer de récupérer la feuille de route correspondante
			DocumentRouteElement relatedRouteElement = docModel.getAdapter(DocumentRouteElement.class);
			DocumentRoute documentRoute = relatedRouteElement.getDocumentRoute(documentManager);
			// Ensuite on récupère les étapes de la feuille de route
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
			List<DocumentModel> allElementsFDR = new ArrayList<DocumentModel>();
			// Nouvelle méthode pour rechercher toutes les étapes du modèle de FDR
			final STFeuilleRoute feuilleRoute = docModel.getAdapter(STFeuilleRoute.class);
			final List<DocumentRouteTableElement> listRouteTableElement = documentRoutingService
					.getFeuilleRouteElements(feuilleRoute, documentManager);
			// Construction du tableau qui contient les étapes de FDR
			for (final DocumentRouteTableElement routeTableElement : listRouteTableElement) {
				if (!routeTableElement.getDocument().isFolder()) {
					STRouteStep step = routeTableElement.getRouteStep();
					allElementsFDR.add(step.getDocument());
				}
			}

			List<DocumentModel> listStepASupprimer = new ArrayList<DocumentModel>();
			for (final DocumentModel stepModel : allElementsFDR) {
				// On vérifie avant tout que l'étape de FDR correspond bien à la FDR courante
				// Permet de corriger des instabilités lors de la recherche d'étapes de la FDR
				ReponsesRouteStep step = null;
				step = stepModel.getAdapter(ReponsesRouteStep.class);
				if (step != null
						&& step.getDocumentRoute(documentManager).getDocument().getId().toString()
								.equals(docModel.getId())) {
					String posteId = mailboxPosteService.getPosteIdFromMailboxId(step.getDistributionMailboxId());
					if (posteId.equals(etapePoste) && step.getType().equals(etapeType)) {
						// Ajout de l'étape concernée à la liste de suppression
						listStepASupprimer.add(stepModel);
					}
				}
			}
			if (!listStepASupprimer.isEmpty()) {
				try {
					// Dans un premier temps on essaie de délocker la feuille de route (au cas où elle aurait été lockée
					// avant)
					final STLockService stLockService = STServiceLocator.getSTLockService();
					try {
						documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
					} catch (DocumentRouteNotLockedException exe) {
						// Do nothing
						if (stLockActions.isDocumentLockedByAnotherUser(docModel)) {
							// Là on va essayer de lever le verrou posé par un autre utilisateur
							try {
								stLockService.unlockDocUnrestricted(documentManager, docModel);
							} catch (ClientException e) {
								// Pas possible de dévrouiller la feuille de route. On passe à la suivante
								final String message = "Impossible de dévrouiller la feuille de route "
										+ docModel.getId();
								facesMessages.add(StatusMessage.Severity.ERROR, message);
								break;
							}
						}
					} catch (ClientException exe) {
						// Do nothing
					}
					// là on va essayer de locker la feuille de route
					stLockService.lockDoc(documentManager, docModel);
				} catch (DocumentRouteNotLockedException e) {
					// continue
				} catch (DocumentRouteAlredayLockedException ex) {
					// Do nothing
				} catch (ClientException ce) {
					// là on met une erreur dans les logs et on passe à la fdr suivante
					final String message = "Impossible de verrouiller la feuille de route " + docModel.getId();
					facesMessages.add(StatusMessage.Severity.ERROR, message);
					break;
				}
				for (DocumentModel stepDelete : listStepASupprimer) {
					try {
						// Suppression de chaque étape listée plus tôt dans la feuille de route courante
						documentRoutingService.removeRouteElement(stepDelete.getAdapter(DocumentRouteElement.class),
								documentManager);
						// Analyse de si l'étape fait partie d'une branche ou pas
						DocumentModel parent = new UnrestrictedGetDocumentRunner(documentManager).getByRef(stepDelete
								.getParentRef());
						if (parent.getType().equals("StepFolder")) {
							// Si on passe ici, c'est qu'on est dans une branche
							Boolean doDelete = true;
							// Suppression des parents si on ne trouve plus que des branches vides
							while (doDelete) {
								// Actualisation des infos pour remonter un cran au dessus
								DocumentModel parentStepFolderDoc = new UnrestrictedGetDocumentRunner(documentManager)
										.getByRef(parent.getParentRef());
								documentRoutingService.removeRouteElement(
										parent.getAdapter(DocumentRouteElement.class), documentManager);
								// Si on est plus dans une étape // ou si on trouve une branche on arrête de supprimer
								// le parent
								if (!parentStepFolderDoc.getType().equals("StepFolder")) {
									doDelete = false;
								} else {
									// Le type est bien StepFolder. On peut donc récupérer le doc avec l'adapter
									StepFolder parentStepFolder = parentStepFolderDoc.getAdapter(StepFolder.class);
									if (parentStepFolder.isParallel()) {
										// Si on a une branche pas vide, alors on ne supprime pas la suite
										// On regarde s'il a encore des enfants
										final List<DocumentModel> children = documentManager
												.getChildren(parentStepFolderDoc.getRef());
										if (children.size() == 0) {
											parent = parentStepFolderDoc;
											doDelete = true;
										} else {
											doDelete = false;
										}
									} else {
										// le nouveau parent est l'ancien ParentStepFolderDoc
										parent = parentStepFolderDoc;
										doDelete = true;
									}

								}

							}
						}

					} catch (LocalizedClientException e) {
						String message = resourcesAccessor.getMessages().get(e.getMessage());
						facesMessages.add(StatusMessage.Severity.WARN, message);
						try {
							documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
						} catch (DocumentRouteNotLockedException ex) {
							break;
						} catch (ClientException ex) {
							break;
						}
						throw new LocalizedClientException(message);
					} catch (DocumentRouteNotLockedException e) {
						final String message = resourcesAccessor.getMessages().get(
								"feedback.casemanagement.document.route.not.locked");
						facesMessages.add(StatusMessage.Severity.WARN, message);
						throw new DocumentRouteNotLockedException(message);
					}
				}
				// Passage de la feuille de route modifiée à l'état brouillon
				try {
					if (documentRoute.isValidated()) {
						documentRoutingService.invalidateRouteModel(documentRoute, documentManager);
					}
				} catch (DocumentRouteNotLockedException e) {
					String errorMessage = resourcesAccessor.getMessages().get(
							"feedback.casemanagement.document.route.not.locked");
					facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
					break;
				}
				try {
					documentRoutingService.unlockDocumentRoute(documentRoute, documentManager);
				} catch (DocumentRouteNotLockedException e) {
					break;
				} catch (ClientException ex) {
					break;
				}
			}
		}

		// On fait une remise à zéro avant de retourner à la liste des modèles de feuille de route
		etapeType = null;
		etapePoste = null;
		fdrSelected = null;
		if (contentViewAvantSuppression.getName().equals(STContentViewConstant.FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW)) {
			return "retour_gestion_modeles_feuille_de_route";
		} else {
			return "recherche_fdr_resultats";
		}
	}

	/***
	 * Retour à la gestion des modèles de feuille de route sans effectuer de suppression
	 */
	public String retourGestionModeleFeuilleDeRouteSansSuppression() {
		// Remise à zéro des critères de suppression
		etapeType = null;
		etapePoste = null;
		fdrSelected = null;
		if (contentViewAvantSuppression.getName().equals(STContentViewConstant.FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW)) {
			return "retour_gestion_modeles_feuille_de_route";
		} else {
			return "recherche_fdr_resultats";
		}
	}

	public DocumentModelList getModeleFDRFromSelection(String selectionName) throws ClientException {
		DocumentModelList dossiers = new DocumentModelListImpl();
		for (DocumentModel docModel : documentsListsManager.getWorkingList(selectionName)) {
			dossiers.add(docModel);
		}
		return dossiers;
	}

	@WebRemote
	public int getProgressPercent() {
		return (int) ((float) doneMassDocument / (float) totalMassDocument * 100L);
	}

	@WebRemote
	public boolean hasNext() {
		return !doneMassDocument.equals(totalMassDocument);
	}
}
