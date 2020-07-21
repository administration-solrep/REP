package fr.dila.reponses.web.recherche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.reponses.api.constant.RechercheExportEventConstants;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.recherche.LDAPElement;
import fr.dila.st.core.recherche.UserRequeteur;
import fr.dila.st.core.service.STServiceLocator;

@Name("rechercheUtilisateurActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class RechercheUtilisateurActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long			serialVersionUID		= 1L;

	private static final STLogger		LOGGER					= STLogFactory
																		.getLog(RechercheUtilisateurActionsBean.class);
	private String						username;
	private String						firstName;
	private String						lastName;
	private String						email;
	private String						telephoneNumber;
	private List<String>				ministeres;
	private List<String>				postes;
	private List<String>				groups;
	private List<String>				postUser;
	private List<String>				directions;
	private Date						dateDebut;
	private Date						dateFin;
	private Date						dateDebutMAX;
	private Date						dateFinMAX;
	private String						postalAddress;
	private String						postalCode;
	private String						locality;

	@DataModel(value = "userList")
	protected DocumentModelList			userList;

	protected DocumentModel				selectedUser;

	@In(create = true)
	protected transient UserManager		userManager;

	@In(create = true, required = false)
	protected transient CoreSession		documentManager;

	@In(required = true, create = false)
	protected SSPrincipal				ssPrincipal;

	@In(create = true, required = true)
	protected ContentViewActions		contentViewActions;

	private String						contentViewName			= "RechercheUser";

	@In(create = true, required = false)
	protected transient FacesMessages	facesMessages;

	private Boolean						isChecked				= false;

	private Map<DocumentModel, Boolean>	mapQuestionChecked;

	private boolean						sortAscendingUsername	= true;
	private boolean						sortAscendingFirstName	= true;
	private boolean						sortAscendingLastName	= true;
	private boolean						sortAscendingUserMail	= true;
	private boolean						sortAscendingDate		= true;

	private UserRequeteur				userRequeteur;

	protected String					formObjetMail			= "";

	protected String					formTexteMail			= "";

	// link="#{pdfPrintActions.doRenderViewList('view_pdf_liste_resultats.faces')}"
	public String searchUser() throws ClientException {

		contentViewActions.reset(ReponsesContentView.RECHERCHE_AVANCEE_CONTENT_VIEW);
		setContentViewName(ReponsesContentView.RECHERCHE_AVANCEE_CONTENT_VIEW);
		userRequeteur = new UserRequeteur();

		if (StringUtils.isNotBlank(username)) {
			userRequeteur
					.setFilterValue(new LDAPElement(UserRequeteur.ID_USERNAME, UserRequeteur.QUERY_EQUAL, username));
		}
		if (StringUtils.isNotBlank(firstName)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_FIRST_NAME, UserRequeteur.QUERY_EQUAL,
					firstName));
		}
		if (StringUtils.isNotBlank(lastName)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_LAST_NAME, UserRequeteur.QUERY_EQUAL,
					lastName));
		}
		if (StringUtils.isNotBlank(email)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_EMAIL, UserRequeteur.QUERY_EQUAL, email));
		}
		if (StringUtils.isNotBlank(postalAddress)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_POSTAL_ADDRESS, UserRequeteur.QUERY_EQUAL,
					postalAddress));
		}
		if (StringUtils.isNotBlank(postalCode)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_POSTAL_CODE, UserRequeteur.QUERY_EQUAL,
					postalCode));
		}
		if (StringUtils.isNotBlank(locality)) {
			userRequeteur
					.setFilterValue(new LDAPElement(UserRequeteur.ID_LOCALITY, UserRequeteur.QUERY_EQUAL, locality));
		}
		if (StringUtils.isNotBlank(telephoneNumber)) {
			userRequeteur.setFilterValue(new LDAPElement(UserRequeteur.ID_TELEPHONE, UserRequeteur.QUERY_EQUAL,
					telephoneNumber));
		}
		if (dateDebut != null) {
			userRequeteur.setFilterValue(UserRequeteur.ID_DATE_DEBUT_MIN, UserRequeteur.QUERY_SUP, dateDebut);
		}
		if (dateDebutMAX != null) {
			userRequeteur.setFilterValue(UserRequeteur.ID_DATE_DEBUT_MAX, UserRequeteur.QUERY_INF, dateDebutMAX);
		}
		if (dateFin != null) {
			userRequeteur.setFilterValue(UserRequeteur.ID_DATE_FIN_MIN, UserRequeteur.QUERY_SUP, dateFin);
		}
		if (dateFinMAX != null) {
			userRequeteur.setFilterValue(UserRequeteur.ID_DATE_FIN_MAX, UserRequeteur.QUERY_INF, dateFinMAX);
		}
		if (!groups.isEmpty()) {
			userRequeteur.setUserProfil(groups);
		}
		if (!ministeres.isEmpty()) {
			userRequeteur.setUserMinistere(ministeres);
		}
		if (!directions.isEmpty()) {
			userRequeteur.setUserDirection(directions);
		}
		if (!postes.isEmpty()) {
			userRequeteur.setUserPoste(postes);
		}

		setUsersList(userRequeteur.searchUsers());
		mapQuestionChecked = new HashMap<DocumentModel, Boolean>();
		if (userList != null && !userList.isEmpty()) {
			for (DocumentModel doc : userList) {

				mapQuestionChecked.put(doc, false);
			}
		}
		return null;
	}

	protected static HttpServletResponse getHttpServletResponse() {
		ServletResponse response = null;
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			response = (ServletResponse) facesContext.getExternalContext().getResponse();
		}
		if (response != null && response instanceof HttpServletResponse) {
			return (HttpServletResponse) response;
		}
		return null;
	}

	public void setContentViewName(String contentViewName) {
		this.contentViewName = contentViewName;
	}

	public String getContentViewName() {
		return contentViewName;
	}

	public ContentView getContentView() throws ClientException {
		return contentViewActions.getContentViewWithProvider(contentViewName);
	}

	private boolean isUserSelected() {
		if (mapQuestionChecked != null) {
			for (boolean value : mapQuestionChecked.values()) {
				if (value) {
					return value;
				}
			}
		}
		return false;
	}

	private DocumentModelList getSelectedDocument() {
		DocumentModelList listSelectedDoc = new DocumentModelListImpl();
		for (Map.Entry<DocumentModel, Boolean> value : mapQuestionChecked.entrySet()) {
			if (value.getValue()) {
				listSelectedDoc.add(value.getKey());
			}
		}
		return listSelectedDoc;

	}

	public void createExportExcel() throws ClientException {

		DocumentModelList listDocToExport = getUserSelectionOrFullList();

		try {
			if (listDocToExport != null && !listDocToExport.isEmpty()) {
				final EventProducer eventProducer = STServiceLocator.getEventProducer();
				final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();

				eventProperties.put(RechercheExportEventConstants.PARAM_DOCUMENT_MODEL_LIST, listDocToExport);

				final InlineEventContext eventContext = new InlineEventContext(documentManager, ssPrincipal,
						eventProperties);
				eventProducer.fireEvent(eventContext.newEvent(ReponsesEventConstant.EXPORT_USER_SEARCH_EVENT));
				facesMessages
						.add(StatusMessage.Severity.INFO,
								"La demande d'export a été prise en compte. Il vous sera transmis par courrier électronique dès qu'il sera disponible.");
			}
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, e);
			facesMessages.add(StatusMessage.Severity.ERROR,
					"Un souci est survenu lors de la génération du fichier excel");
		}
	}

	/**
	 * @return the dateDebutMAX
	 */
	public Date getDateDebutMAX() {
		return dateDebutMAX;
	}

	/**
	 * @param dateDebutMAX
	 *            the dateDebutMAX to set
	 */
	public void setDateDebutMAX(Date dateDebutMAX) {
		this.dateDebutMAX = dateDebutMAX;
	}

	/**
	 * @return the dateFinMAX
	 */
	public Date getDateFinMAX() {
		return dateFinMAX;
	}

	/**
	 * @param dateFinMAX
	 *            the dateFinMAX to set
	 */
	public void setDateFinMAX(Date dateFinMAX) {
		this.dateFinMAX = dateFinMAX;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the telephoneNumber
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * @param telephoneNumber
	 *            the telephoneNumber to set
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * @return the postUser
	 */
	public List<String> getPostUser() {
		return postUser;
	}

	/**
	 * @param postUser
	 *            the postUser to set
	 */
	public void setPostUser(List<String> postUser) {
		this.postUser = postUser;
	}

	/**
	 * @return the dateFin
	 */
	public Date getDateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin
	 *            the dateFin to set
	 */
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	/**
	 * @return the postalAddress
	 */
	public String getPostalAddress() {
		return postalAddress;
	}

	/**
	 * @param postalAddress
	 *            the postalAddress to set
	 */
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality
	 *            the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the postes
	 */
	public List<String> getPostes() {
		return postes;
	}

	/**
	 * @param postes
	 *            the postes to set
	 */
	public void setPostes(List<String> postes) {
		this.postes = postes;
	}

	public String resetSearch() {
		username = null;
		firstName = null;
		lastName = null;
		email = null;
		telephoneNumber = null;
		ministeres = null;
		postes = null;
		groups = null;
		postUser = null;
		directions = null;
		dateDebut = null;
		dateFin = null;
		dateDebutMAX = null;
		dateFinMAX = null;
		postalAddress = null;
		postalCode = null;
		locality = null;
		userList = new DocumentModelListImpl();
		return null;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the ministeres
	 */
	public List<String> getMinistere() {
		return ministeres;
	}

	/**
	 * @param ministeres
	 *            the ministeres to set
	 */
	public void setMinistere(List<String> ministere) {
		this.ministeres = ministere;
	}

	/**
	 * @return the directions
	 */
	public List<String> getDirection() {
		return directions;
	}

	/**
	 * @param directions
	 *            the directions to set
	 */
	public void setDirection(List<String> direction) {
		this.directions = direction;
	}

	/**
	 * @return the dateDebut
	 */
	public Date getDateDebut() {
		return dateDebut;
	}

	/**
	 * @param dateDebut
	 *            the dateDebut to set
	 */
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	/**
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par username
	 * 
	 */
	public void sortByUsername() {
		if (sortAscendingUsername) {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getUsername() != null && user2.getUsername() != null) {
								return user1.getUsername().compareTo(user2.getUsername());
							} else if (user1.getUsername() != null) {
								return 1;
							} else if (user2.getUsername() != null) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return 1;
					} else if (doc2 != null) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingUsername = false;
		} else {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getUsername() != null && user2.getUsername() != null) {
								return user2.getUsername().compareTo(user1.getUsername());
							} else if (user1.getUsername() != null) {
								return -1;
							} else if (user2.getUsername() != null) {
								return 1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return -1;
					} else if (doc2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingUsername = true;
		}
	}

	/**
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par first name
	 * 
	 */
	public void sortByFirstName() {
		if (sortAscendingFirstName) {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getFirstName() != null && user2.getFirstName() != null) {
								return user1.getFirstName().compareTo(user2.getFirstName());
							} else if (user1.getFirstName() != null) {
								return 1;
							} else if (user2.getFirstName() != null) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return 1;
					} else if (doc2 != null) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingFirstName = false;
		} else {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getFirstName() != null && user2.getFirstName() != null) {
								return user2.getFirstName().compareTo(user1.getFirstName());
							} else if (user1.getFirstName() != null) {
								return -1;
							} else if (user2.getFirstName() != null) {
								return 1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return -1;
					} else if (doc2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingFirstName = true;
		}
	}

	/**
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par last name
	 */
	public void sortByLastName() {
		if (sortAscendingLastName) {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getLastName() != null && user2.getLastName() != null) {
								return user1.getLastName().compareTo(user2.getLastName());
							} else if (user1.getLastName() != null) {
								return 1;
							} else if (user2.getLastName() != null) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return 1;
					} else if (doc2 != null) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingLastName = false;
		} else {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getLastName() != null && user2.getLastName() != null) {
								return user2.getLastName().compareTo(user1.getLastName());
							} else if (user1.getLastName() != null) {
								return -1;
							} else if (user2.getLastName() != null) {
								return 1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return -1;
					} else if (doc2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingLastName = true;
		}
	}

	/**
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par mail
	 */
	public void sortByMail() {
		if (sortAscendingUserMail) {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getEmail() != null && user2.getEmail() != null) {
								return user1.getEmail().compareTo(user2.getEmail());
							} else if (user1.getEmail() != null) {
								return 1;
							} else if (user2.getEmail() != null) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return 1;
					} else if (doc2 != null) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingUserMail = false;
		} else {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getEmail() != null && user2.getEmail() != null) {
								return user2.getEmail().compareTo(user1.getEmail());
							} else if (user1.getEmail() != null) {
								return -1;
							} else if (user2.getEmail() != null) {
								return 1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return -1;
					} else if (doc2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingUserMail = true;
		}
	}

	/**
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par date de début
	 */
	public void sortByDate() {
		if (sortAscendingDate) {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getDateDebut() != null && user2.getDateDebut() != null) {
								return user1.getDateDebut().compareTo(user2.getDateDebut());
							} else if (user1.getDateDebut() != null) {
								return 1;
							} else if (user2.getDateDebut() != null) {
								return -1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return 1;
					} else if (doc2 != null) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingDate = false;
		} else {
			Collections.sort(this.userList, new Comparator<DocumentModel>() {
				@Override
				public int compare(DocumentModel doc1, DocumentModel doc2) {
					if (doc1 != null && doc2 != null) {
						STUser user1 = doc1.getAdapter(STUser.class);
						STUser user2 = doc2.getAdapter(STUser.class);
						if (user1 != null && user2 != null) {
							if (user1.getDateDebut() != null && user2.getDateDebut() != null) {
								return user2.getDateDebut().compareTo(user1.getDateDebut());
							} else if (user1.getDateDebut() != null) {
								return -1;
							} else if (user2.getDateDebut() != null) {
								return 1;
							} else {
								return 0;
							}
						} else {
							return 0;
						}
					} else if (doc1 != null) {
						return -1;
					} else if (doc2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			sortAscendingDate = true;
		}
	}

	public void checkAllBox() {
		isChecked = true;
		for (DocumentModel doc : mapQuestionChecked.keySet()) {
			mapQuestionChecked.put(doc, isChecked);
		}
	}

	public void checkBox(String uuid) throws ClientException {
		mapQuestionChecked.keySet();

	}

	public void checkBox(DocumentModel uuid) throws ClientException {
		if (mapQuestionChecked.get(uuid)) {
			mapQuestionChecked.put(uuid, false);
		} else {
			mapQuestionChecked.put(uuid, true);
		}
	}

	public void deleteSelectedUsers() throws ClientException {
		if (mapQuestionChecked != null && !mapQuestionChecked.isEmpty()) {
			if (mapQuestionChecked.containsValue(true)) {
				String usersSupprimes = "";
				String usersNonSupprimes = "";
				for (DocumentModel doc : mapQuestionChecked.keySet()) {
					if (mapQuestionChecked.get(doc)) {
						STUser user = doc.getAdapter(STUser.class);
						// Ajout vérification de droit
						// Si administrateur ministériel on vérifie que user est dans son ministère
						// Sinon affichage message erreur
						// récupération des ministères de l'utilisateur courrant
						Boolean canDeleteUser = true;
						List<String> listGroupUtilisateur = ssPrincipal.getAllGroups();
						if (!listGroupUtilisateur.contains("Administrateur fonctionnel")
								&& listGroupUtilisateur.contains("Administrateur ministériel")) {
							canDeleteUser = false;
							// vérification que l'utilisateur est du même ministère que le user qu'il souhaite supprimer
							final Set<String> ministereIdSet = ssPrincipal.getMinistereIdSet();
							STUserService stUserService = STServiceLocator.getSTUserService();
							List<String> userASupprMinId = stUserService.getAllUserMinisteresId(user.getUsername());
							canDeleteUser = !Collections.disjoint(userASupprMinId, ministereIdSet);

						}
						if (canDeleteUser) {
							userManager.deleteUser(doc);
							usersSupprimes += usersSupprimes.isEmpty() ? "" + user.getUsername() : ", "
									+ user.getUsername();
						} else {
							usersNonSupprimes += usersNonSupprimes.isEmpty() ? "" + user.getUsername() : ", "
									+ user.getUsername();
						}
					}
				}
				searchUser();
				String messageInfoSuppression = "";
				if (!usersSupprimes.isEmpty()) {
					messageInfoSuppression = "Les utilisateurs suivants ont été supprimés : " + usersSupprimes;
				}
				if (!usersNonSupprimes.isEmpty()) {
					messageInfoSuppression += " Vous n'avez pas les droits pour supprimer les utilisateurs "
							+ usersNonSupprimes;
				}
				facesMessages.add(StatusMessage.Severity.INFO, messageInfoSuppression);
			} else {
				facesMessages.add(StatusMessage.Severity.WARN, "Vous n'avez pas selectionné d'utilisateur.");
			}
		}
	}

	private DocumentModelList getUserSelectionOrFullList() {
		DocumentModelList listDocToExport;

		if (isUserSelected()) {
			listDocToExport = getSelectedDocument();
		} else {
			listDocToExport = userList;
		}

		return listDocToExport;
	}

	public void envoyerMail() throws ClientException {

		DocumentModelList listDocToExport = getUserSelectionOrFullList();

		List<STUser> recipients = new ArrayList<STUser>();
		for (DocumentModel doc : listDocToExport) {
			if (mapQuestionChecked.get(doc)) {
				recipients.add(doc.getAdapter(STUser.class));
			}
		}
		if (recipients.size() > 0) {
			ReponsesServiceLocator.getReponsesMailService().sendMailToUserListAsBCC(documentManager, recipients,
					formObjetMail, formTexteMail);
		}
		formObjetMail = "";
		formTexteMail = "";
	}

	public void annulerMail() {
		formObjetMail = "";
		formTexteMail = "";
	}

	public String getFormObjetMail() {
		return formObjetMail;
	}

	public void setFormObjetMail(String formObjetMail) {
		this.formObjetMail = formObjetMail;
	}

	public String getFormTexteMail() {
		return formTexteMail;
	}

	public void setFormTexteMail(String formTextMail) {
		this.formTexteMail = formTextMail;
	}

	public void setUsersList(List<DocumentModel> userListPr) {
		userList = new DocumentModelListImpl();
		for (DocumentModel documentModel : userListPr) {
			userList.add(documentModel);
		}
	}

}
