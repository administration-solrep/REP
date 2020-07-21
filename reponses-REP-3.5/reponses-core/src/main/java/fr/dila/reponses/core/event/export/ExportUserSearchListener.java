package fr.dila.reponses.core.event.export;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import com.google.common.base.Joiner;

import fr.dila.reponses.api.constant.RechercheExportEventConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SessionUtil;

public class ExportUserSearchListener implements PostCommitEventListener {

	private static final STLogger LOGGER = STLogFactory.getLog(ExportUserSearchListener.class);
	
	@Override
	public void handleEvent(EventBundle events) throws ClientException {
		if (!events.containsEventName(ReponsesEventConstant.EXPORT_USER_SEARCH_EVENT)) {
			return;
		}
		LOGGER.debug(STLogEnumImpl.DEFAULT, "Traitement de la demande d'export des utilisateurs prise en compte");
		for (final Event event : events) {
			if (ReponsesEventConstant.EXPORT_USER_SEARCH_EVENT.equals(event.getName())) {
				LOGGER.debug(STLogEnumImpl.DEFAULT, "On traite l'export de la recherche utilisateurs");
				handleExportEvent(event);
			}
		}
	}
	
	private void handleExportEvent(Event event) {

		CoreSession session = null;
		final EventContext eventCtx = event.getContext();
		final Map<String, Serializable> eventProperties = eventCtx.getProperties();
		@SuppressWarnings("unchecked")
		final List<DocumentModel> listDocToExport = (List<DocumentModel>) eventProperties.get(RechercheExportEventConstants.PARAM_DOCUMENT_MODEL_LIST);
		final SSPrincipal principal = (SSPrincipal) eventCtx.getPrincipal();
		
		try {
			session = SessionUtil.getCoreSession();
			List<String> headerToExport = new ArrayList<String>();
			headerToExport.add("Utilisateur");
			headerToExport.add("Prénom");
			headerToExport.add("Nom");
			headerToExport.add("Mél.");
			headerToExport.add("Date Début");
			headerToExport.add("Ministère");
			headerToExport.add("Direction");
			headerToExport.add("Adresse");
			headerToExport.add("Code Postal");
			headerToExport.add("Ville");
			headerToExport.add("Téléphone");
			headerToExport.add("Poste Utilisateur");
			headerToExport.add("Date de dernière connexion");

			List<String[]> contentToExport = buildDataToExport(session, principal, listDocToExport, true);

			DataSource fichier = ExcelUtil.createExcelFile(session, "User Export",
					headerToExport.toArray(new String[0]), contentToExport);
			
			final STMailService mailService = STServiceLocator.getSTMailService();
			String objet = "[REPONSES] Votre demande d'export de la recherche utilisateur ";
			String corpsTemplate = "Bonjour, l'export de la recherche utilisateur est terminé.";
			mailService.sendMailWithAttachement(Collections.singletonList(principal.getEmail()), objet, corpsTemplate, "export_recherche_utilisateur.xls", fichier);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, e);
		} finally {
			if (session != null) {
				SessionUtil.close(session);
			}
		}
	}
	
	private List<String[]> buildDataToExport(final CoreSession session, final SSPrincipal principal, List<DocumentModel> listDocToExport, boolean excel) throws ClientException {
		final List<String[]> contentToExport	= new ArrayList<String[]>();

		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
		final STUsAndDirectionService uniteandDirectionService = STServiceLocator.getSTUsAndDirectionService();
		final STMinisteresService ministereService = STServiceLocator.getSTMinisteresService();
		final STPostesService posteService = STServiceLocator.getSTPostesService();
		final SSUtilisateurConnectionMonitorService utilisateurConnectionMonitorService = SSServiceLocator
				.getUtilisateurConnectionMonitorService();

		boolean isAdmin = principal.isAdministrator() || principal.isMemberOf(ReponsesBaseFunctionConstant.ORGANIGRAMME_UPDATER);
		boolean isAdminMinisteriel = principal.isMemberOf(ReponsesBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER);
		Set<String> currentUserMininisteres = principal.getMinistereIdSet();
		
		for (DocumentModel doc : listDocToExport) {
			STUser user = doc.getAdapter(STUser.class);
			List<String> content = new ArrayList<String>();
			content.add(user.getUsername());
			content.add(user.getFirstName());
			content.add(user.getLastName());
			content.add(user.getEmail());
			content.add(dateFormat.format(user.getDateDebut().getTime()));
			
			if (excel) {

				SSInfoUtilisateurConnection infoUser = utilisateurConnectionMonitorService.getInfoUtilisateurConnection(session, user.getUsername());
				Calendar dateDerniereConnexion = null;
				if (infoUser != null) {
					dateDerniereConnexion = infoUser.getDateDerniereConnexion();
				}
				
				// List<String> allMinisteres = ministereService.getAllMinistereNameForUser(user.getUsername());
				List<String> allUnits = uniteandDirectionService.getDirectionNameParentsFromUser(user.getUsername());
				List<String> allPostes = posteService.getAllPosteNameForUser(user.getUsername());
				List<String> allPostesId = posteService.getAllPosteIdsForUser(user.getUsername());
				Set<String> allPostesSet = new HashSet<String>(allPostesId);
				List<EntiteNode> allMinisteresEntiteNode = ministereService.getMinistereParentFromPostes(allPostesSet);
				Joiner joiner = Joiner.on(";").skipNulls();
				List<String> allMinisteres = new ArrayList<String>();
				
				// init du check pour lecture date dernière connexion : vrai si admin, faux sinon
				boolean canReadLastConnectionDate = false || isAdmin;
				for (EntiteNode ministere : allMinisteresEntiteNode) {
					allMinisteres.add(ministere.getLabel());
					// maj du check s'il n'est pas déjà vrai, et qu'on a un admin ministeriel et que le ministere de l'user cherché est dans celui du current user
					if (!canReadLastConnectionDate && isAdminMinisteriel && currentUserMininisteres.contains(ministere.getId())) {
						canReadLastConnectionDate = true;
					}
				}

				// suppression des doublons.
				allMinisteres = new ArrayList<String>(new LinkedHashSet<String>(allMinisteres));

				content.add(joiner.join(allMinisteres));
				content.add(joiner.join(allUnits));

				content.add(user.getPostalAddress());
				content.add(user.getPostalCode());
				content.add(user.getLocality());
				content.add(user.getTelephoneNumber());
				content.add(joiner.join(allPostes));
				
				// Check les droits de l'utilisateur pour savoir s'il peut voir l'info de dernière connexion
				if (canReadLastConnectionDate && dateDerniereConnexion != null) {
					content.add(dateFormat.format(dateDerniereConnexion.getTime()));
				} else {
					content.add("");
				}
			}
			contentToExport.add(content.toArray(new String[0]));
		}
		
		return contentToExport;
	}

}
