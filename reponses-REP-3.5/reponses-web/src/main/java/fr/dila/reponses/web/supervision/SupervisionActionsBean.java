package fr.dila.reponses.web.supervision;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.reporting.engine.BirtEngine;
import org.nuxeo.ecm.platform.reporting.report.ReportHelper;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.security.UserManagerActions;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.web.action.NavigationWebActionsBean;

/**
 * 
 * Bean d'alertes.
 * 
 * @author jbrunet
 */
@Name("supervisionActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 2)
public class SupervisionActionsBean implements Serializable {

	private static final long						serialVersionUID					= 1L;

	@In(create = true, required = true)
	protected transient UserManager					userManager;

	@In(create = true, required = true)
	protected transient UserManagerActions			userManagerActions;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true, required = true)
	protected transient CoreSession					documentManager;

	@In(create = true, required = true)
	protected transient ActionManager				actionManager;

	@In(required = true, create = true)
	protected transient SSPrincipal					ssPrincipal;

	@In(create = true)
	protected transient NavigationWebActionsBean	navigationWebActions;

	@RequestParameter("newSelectedStat")
	private String									newSelectedStat;

	@RequestParameter("selectedUserView")
	private String									selectedUserView;

	private String									selectedStat;
	private List<String>							catalogStat;

	private Date									filterNotConnected;

	@DataModel(value = "connectionUserList")
	protected DocumentModelList						userList;
	@DataModelSelection("connectionUserList")
	protected DocumentModel							selectedUser;

	private boolean									sortAscendingUsername;
	private boolean									sortAscendingFirstName;
	private boolean									sortAscendingLastName;
	private boolean									sortAscendingDate;

	private boolean									mailOpen;
	private String									mailText;
	private String									mailObject;

	private static final String						STAT_UTILISATEUR_CONNECTE_FILE		= "birtReports/r_stats_noms_utilisateurs_connectes.rptdesign";
	private static final String						STAT_UTILISATEUR_NON_CONNECTE_FILE	= "birtReports/r_stats_noms_utilisateurs_actifs.rptdesign";

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger					LOGGER								= STLogFactory
																								.getLog(SupervisionActionsBean.class);

	private static final int						STAT_UTILISATEUR_CONNECTE			= 0;
	private static final int						STAT_UTILISATEUR_NON_CONNECTE		= 1;

	/**
	 * STAT_UTILISATEUR_CONNECTE : Liste des utilisateurs connectés STAT_UTILISATEUR_NON_CONNECTE : Liste des
	 * utilisateurs ne s'étant pas connectés depuis .. La date par défaut de non connexion est celle du jour
	 * 
	 * @throws ClientException
	 */
	@PostConstruct
	public void init() throws ClientException {
		catalogStat = new ArrayList<String>();
		catalogStat.add(STAT_UTILISATEUR_CONNECTE, "Liste des utilisateurs connectés");
		catalogStat.add(STAT_UTILISATEUR_NON_CONNECTE,
				"Liste des utilisateurs ne s'étant pas connectés depuis une certaine durée (jour, mois, année)");
		selectedStat = catalogStat.get(STAT_UTILISATEUR_CONNECTE);
		userList = null;
		filterNotConnected = Calendar.getInstance().getTime();
		sortAscendingUsername = true;
		sortAscendingFirstName = true;
		sortAscendingLastName = true;
		sortAscendingDate = true;
		mailOpen = false;
	}

	/**
	 * 
	 * @return la liste des utilisateurs connectés
	 * @throws ClientException
	 */
	private DocumentModelList getAllUserConnected() throws ClientException {
		DocumentModelList dml = new DocumentModelListImpl();
		List<DocumentModel> userListInfo = SSServiceLocator.getUtilisateurConnectionMonitorService()
				.getAllInfoUtilisateurConnection(documentManager, false);
		for (DocumentModel userInfo : userListInfo) {
			SSInfoUtilisateurConnection userIuc = userInfo.getAdapter(SSInfoUtilisateurConnection.class);
			dml.add(userManager.getUserModel(userIuc.getUserName()));
		}
		return dml;
	}

	/**
	 * 
	 * @return la liste des utilisateurs non connectés depuis une certaines durée
	 * @throws ClientException
	 */
	private DocumentModelList getAllUserNotConnectedSince() throws ClientException {
		DocumentModelList dml = new DocumentModelListImpl();
		if (filterNotConnected == null) {
			if (userList == null) {
				dml = new DocumentModelListImpl();
			}
		} else {
			SSUtilisateurConnectionMonitorService utilisateurConnectionMonitorService = SSServiceLocator
					.getUtilisateurConnectionMonitorService();
			List<String> userNameList = utilisateurConnectionMonitorService
					.getListInfoUtilisateurConnectionNotConnectedSince(documentManager, filterNotConnected);
			for (String userName : userNameList) {
				if (userName != null) {
					dml.add(userManager.getUserModel(userName));
				}
			}
		}
		return dml;
	}

	@Factory(value = "connectionUserList")
	public DocumentModelList getUserList() throws ClientException {
		if (userList == null) {
			if (catalogStat.get(STAT_UTILISATEUR_CONNECTE).equals(selectedStat)) {
				userList = getAllUserConnected();
			} else if (catalogStat.get(STAT_UTILISATEUR_NON_CONNECTE).equals(selectedStat)) {
				userList = getAllUserNotConnectedSince();
			}
		}
		return userList;
	}

	private String getBirtReportFile() {
		if (catalogStat.get(STAT_UTILISATEUR_CONNECTE).equals(selectedStat)) {
			return STAT_UTILISATEUR_CONNECTE_FILE;
		} else if (catalogStat.get(STAT_UTILISATEUR_NON_CONNECTE).equals(selectedStat)) {
			return STAT_UTILISATEUR_NON_CONNECTE_FILE;
		}
		return null;
	}

	public Date getDateConnexion(String username) throws ClientException {
		SSInfoUtilisateurConnection iuc = SSServiceLocator.getUtilisateurConnectionMonitorService()
				.getInfoUtilisateurConnection(documentManager, username);
		if (iuc == null) {
			return null;
		}
		return iuc.getDateConnection().getTime();
	}

	public String viewSelectedStat() {
		if (newSelectedStat != null && !newSelectedStat.equals(selectedStat)) {
			selectedStat = newSelectedStat;
		}
		userList = null;
		if (hasFilter() && filterNotConnected == null) {
			facesMessages.add(StatusMessage.Severity.WARN, "La date doit être renseignée");
		}
		return ReponsesViewConstant.VIEW_SUPERVISION;
	}

	public String sendMailToAllConnected() throws ClientException {
		if ("".equals(mailObject)) {
			facesMessages.add(StatusMessage.Severity.WARN, "L'objet du mèl doit être renseigné");
		} else if ("".equals(mailText)) {
			facesMessages.add(StatusMessage.Severity.WARN, "Le texte du mèl doit être renseigné");
		} else {
			// Récupération des STUser correspondants
			ArrayList<STUser> recipients = new ArrayList<STUser>();
			for (DocumentModel dm : getAllUserConnected()) {
				recipients.add(dm.getAdapter(STUser.class));
			}
			try {
				STServiceLocator.getSTMailService().sendMailToUserList(documentManager, recipients, mailObject,
						mailText);
				facesMessages.add(StatusMessage.Severity.INFO, "Le mèl a été envoyé avec succès");
			} catch (ClientException e) {
				facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de l'envoi du mèl");
				LOGGER.warn(documentManager, STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
			}
		}
		return ReponsesViewConstant.VIEW_SUPERVISION;
	}

	public String getNewSelectedStat() {
		return newSelectedStat;
	}

	public void setNewSelectedStat(String newSelectedStat) {
		this.newSelectedStat = newSelectedStat;
	}

	public List<String> getCatalogStat() {
		return catalogStat;
	}

	public String getSelectedStat() {
		return selectedStat;
	}

	public void setSelectedStat(String selectedStat) {
		this.selectedStat = selectedStat;
	}

	public Date getFilterNotConnected() {
		return filterNotConnected;
	}

	public void setFilterNotConnected(Date filterNotConnected) {
		this.filterNotConnected = filterNotConnected;
	}

	public String getMailText() {
		return mailText;
	}

	public void setMailText(String mailText) {
		this.mailText = mailText;
	}

	public String getMailObject() {
		return mailObject;
	}

	public void setMailObject(String mailObject) {
		this.mailObject = mailObject;
	}

	public String goToUserView() throws ClientException {
		if (selectedUserView != null) {
			navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_utilisateur"));
			return userManagerActions.viewUser(selectedUserView);
		}
		return viewSelectedStat();
	}

	public boolean hasFilter() {
		if (catalogStat.get(STAT_UTILISATEUR_NON_CONNECTE).equals(selectedStat)) {
			return true;
		}
		return false;
	}

	public void toggleMail() {
		mailOpen = !mailOpen;
	}

	public boolean isMailOpen() {
		return mailOpen;
	}

	public void setMailOpen(boolean isMailOpen) {
		this.mailOpen = isMailOpen;
	}

	public void clearMailArea() {
		mailObject = "";
		mailText = "";
	}

	private static HttpServletResponse getHttpServletResponse() {
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

	public String formatDateParameter() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(filterNotConnected);
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
	}

	/**
	 * Report generation
	 * 
	 * @throws Exception
	 */
	public void generateReportExcel() throws Exception {
		HashMap<String, String> parameter = new HashMap<String, String>();
		if (hasFilter()) {
			if (filterNotConnected == null) {
				filterNotConnected = Calendar.getInstance().getTime();
			}
			parameter.put("NOMSUTILISATEURS_PARAM", allUserInfoConnexion(filterNotConnected));
		}
		final HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}
		response.reset();
		final OutputStream outputStream = response.getOutputStream();
		// Lecture du fichier birt .rptdesign
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getBirtReportFile());
		if (is == null) {
			is = Framework.getResourceLoader().getResourceAsStream(getBirtReportFile());
		}
		final IReportRunnable nuxeoReport = ReportHelper.getNuxeoReport(is);
		final IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

		final HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("xls");
		options.setOutputStream(outputStream);

		task.setParameterValues(parameter);
		task.setRenderOption(options);
		task.run();
		task.close();

		// prepare pdf response
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=report.xls");

		FacesContext.getCurrentInstance().responseComplete();
	}

	public void generateReportPdf() throws Exception {
		HashMap<String, String> parameter = new HashMap<String, String>();
		if (hasFilter()) {
			if (filterNotConnected == null) {
				filterNotConnected = Calendar.getInstance().getTime();
			}
			parameter.put("NOMSUTILISATEURS_PARAM", allUserInfoConnexion(filterNotConnected));
		}
		final HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}
		response.reset();
		final OutputStream outputStream = response.getOutputStream();
		// Lecture du fichier birt .rptdesign
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getBirtReportFile());
		if (is == null) {
			is = Framework.getResourceLoader().getResourceAsStream(getBirtReportFile());
		}
		final IReportRunnable nuxeoReport = ReportHelper.getNuxeoReport(is);
		final IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

		final HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
		options.setOutputStream(outputStream);

		task.setParameterValues(parameter);
		task.setRenderOption(options);
		task.run();
		task.close();

		// prepare pdf response
		response.setHeader("Content-Type", "application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=report.pdf");

		FacesContext.getCurrentInstance().responseComplete();
	}

	private String allUserInfoConnexion(Date dateRef) throws ClientException {
		String result = "";
		DocumentModelList dmlUsers = new DocumentModelListImpl();
		final SSUtilisateurConnectionMonitorService utilisateurConnectionMonitorService = SSServiceLocator
				.getUtilisateurConnectionMonitorService();
		List<String> usersList = utilisateurConnectionMonitorService.getListInfoUtilisateurConnectionNotConnectedSince(
				documentManager, dateRef);
		if (usersList != null) {
			for (String name : usersList) {
				dmlUsers.add(userManager.getUserModel(name));
			}
			result = this.usersToJSON(dmlUsers);
		}
		return result;
	}

	private String usersToJSON(DocumentModelList usersList) {

		String firstName = "";
		String lastName = "";
		String userName = "";
		String dateConnexion = "";

		StringBuilder usersBuilder = new StringBuilder("[");
		if (usersList != null) {
			for (DocumentModel user : usersList) {
				STUser sTUser = user.getAdapter(STUser.class);
				firstName = sTUser.getFirstName();
				lastName = sTUser.getLastName();
				userName = sTUser.getUsername();
				try {
					if (getDateConnexion(sTUser.getUsername()) != null) {
						dateConnexion = DateUtil.formatWithHour(getDateConnexion(sTUser.getUsername()));
					} else {
						dateConnexion = "";
					}
				} catch (ClientException e) {
					dateConnexion = "";
				}

				if (firstName == null) {
					firstName = "";
				}
				if (lastName == null) {
					lastName = "";
				}
				if (usersBuilder.length() != 1) {
					usersBuilder.append(",");
				}

				boolean skip = userName.isEmpty() && firstName.isEmpty() && lastName.isEmpty()
						&& dateConnexion.isEmpty();
				if (!skip) {
					usersBuilder.append("{\"userName\" : \"");
					usersBuilder.append(userName);
					usersBuilder.append("\", \"firstName\":\"");
					usersBuilder.append(firstName);
					usersBuilder.append("\",\"lastName\":\"");
					usersBuilder.append(lastName);
					usersBuilder.append("\",\"dateConnexion\":\"");
					usersBuilder.append(dateConnexion);
					usersBuilder.append("\"}");
				}
			}
		}
		usersBuilder.append("]");
		return usersBuilder.toString();
	}

	/**
	 * Méthodes de tri
	 * 
	 */
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
	 * Méthode de tri pour l'affichage de la recherche utilisateur : tri par date de connexion
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
							try {
								if (getDateConnexion(user1.getUsername()) != null
										&& getDateConnexion(user2.getUsername()) != null) {
									return getDateConnexion(user1.getUsername()).compareTo(
											getDateConnexion(user2.getUsername()));
								} else if (getDateConnexion(user1.getUsername()) != null) {
									return 1;
								} else if (getDateConnexion(user2.getUsername()) != null) {
									return -1;
								} else {
									return 0;
								}
							} catch (ClientException e) {
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
							try {
								if (getDateConnexion(user1.getUsername()) != null
										&& getDateConnexion(user2.getUsername()) != null) {
									return getDateConnexion(user1.getUsername()).compareTo(
											getDateConnexion(user2.getUsername()));
								} else if (getDateConnexion(user1.getUsername()) != null) {
									return -1;
								} else if (getDateConnexion(user2.getUsername()) != null) {
									return 1;
								} else {
									return 0;
								}
							} catch (ClientException e) {
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

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_SUPERVISION));
	}

	/**
	 * Reset la userlist à null
	 */
	public void resetUserList() {
		userList = null;
	}
}
