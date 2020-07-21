package fr.dila.ss.core.service;

import java.security.Principal;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.SSArchiveService;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

public abstract class SSArchiveServiceImpl implements SSArchiveService {
	private static final long		serialVersionUID	= -7780914517900449689L;

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(SSArchiveServiceImpl.class);

	/**
	 * Default constructor
	 */
	protected SSArchiveServiceImpl() {

	}

	@Override
	public void supprimerDossier(final CoreSession session, final DocumentModel dossierDoc) throws Exception {
		deleteDossier(session, dossierDoc);
		logSuppressionDossier(session, dossierDoc, null);
	}
	
	@Override
	public void supprimerDossier(final CoreSession session, final DocumentModel dossierDoc, final Principal principal) throws Exception {
		deleteDossier(session, dossierDoc);
		logSuppressionDossier(session, dossierDoc, principal);
	}
	
	private void deleteDossier(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {
		// Chargement des services
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

		// suppression des DossierLink
		final List<DocumentModel> dossiersLink = findDossierLinkUnrestricted(session, dossierDoc.getId());
		for (final DocumentModel docLink : dossiersLink) {
			LOGGER.info(session, STLogEnumImpl.DEL_DL_TEC, docLink);
			session.removeDocument(docLink.getRef());
			session.save();
		}

		// suppression de la feuille de route
		final List<DocumentRoute> routes = documentRoutingService.getDocumentRoutesForAttachedDocument(session,
				dossierDoc.getId());
		if (routes != null && !routes.isEmpty()) {
			for (final DocumentRoute route : routes) {
				LOGGER.info(session, SSLogEnumImpl.DEL_FDR_TEC, route.getDocument());
				// désormais on utilise la méthode de docRouting pour supprimer de manière "soft"
				documentRoutingService.softDeleteStep(session, route.getDocument());
			}
		}

		// suppression du Dossier
		LOGGER.info(session, STLogEnumImpl.DEL_DOSSIER_TEC, dossierDoc);
		if ("false".equals(Framework.getProperty("solonepg.dossier.soft.delete", "true"))) {
			session.removeDocument(dossierDoc.getRef());
			session.save();
		}
	}
	
	protected void logSuppressionDossier(final CoreSession session, final DocumentModel dossierDoc, final Principal principal) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		if (principal == null) {
			journalService.journaliserActionAdministration(session, dossierDoc, STEventConstant.EVENT_ARCHIVAGE_DOSSIER, STEventConstant.COMMENT_ARCHIVAGE_DOSSIER);
		} else {
			journalService.journaliserActionAdministration(session, principal, dossierDoc, STEventConstant.EVENT_ARCHIVAGE_DOSSIER, STEventConstant.COMMENT_ARCHIVAGE_DOSSIER);
		}
	}

	/**
	 * Methode qui retourne les DossierLinks lié au dossier qui nécessite d'appeller le service des corbeille : cette
	 * méthode est surchargée dans les applications réponse et solonepg et est utilisé dans la méthode supprimerDossier
	 * 
	 * @return List<DocumentModel>
	 */
	protected abstract List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, String id)
			throws ClientException;

}
