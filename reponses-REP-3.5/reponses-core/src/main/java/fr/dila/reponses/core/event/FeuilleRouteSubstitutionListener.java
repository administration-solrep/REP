package fr.dila.reponses.core.event;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.core.event.RollbackEventListener;

/**
 * Listener permettant d'effectuer des traitements après la substitution d'une feuille de route.
 * 
 * @author jtremeaux
 */
public class FeuilleRouteSubstitutionListener extends RollbackEventListener {

	/**
	 * Recupere pour un ensemble de dossier des metadonnées du dossier et de la question dans une unique session
	 * unrestricted Stocke les resultat dans une map
	 * 
	 * @author spesnel
	 * 
	 */
	private static class UnrestrictedDossierDataRead extends UnrestrictedSessionRunner {

		private List<String>	dossierIds;

		public UnrestrictedDossierDataRead(CoreSession session, List<String> dossierIds) {
			super(session);
			this.dossierIds = dossierIds;
		}

		@Override
		public void run() throws ClientException {
			for (String id : dossierIds) {
				DocumentModel dossierDoc = session.getDocument(new IdRef(id));
				Dossier dossier = dossierDoc.getAdapter(Dossier.class);
				Question question = dossier.getQuestion(session);

				final JetonService jetonService = ReponsesServiceLocator.getJetonService();

				jetonService.addDocumentInBasket(session, STWebserviceConstant.CHERCHER_ATTRIBUTIONS,
						question.getOrigineQuestion(), dossierDoc, dossier.getNumeroQuestion().toString(), null, null);

				jetonService.addDocumentInBasket(session, STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE,
						question.getOrigineQuestion(), dossierDoc, dossier.getNumeroQuestion().toString(), null, null);
			}
		}

	}

	@Override
	public void handleInlineEvent(Event event, InlineEventContext ctx) throws ClientException {
		// Traite uniquement les évènements de substitution de feuille de route
		if (!(event.getName().equals(STEventConstant.AFTER_SUBSTITUTION_FEUILLE_ROUTE))) {
			return;
		}
		final CoreSession session = ctx.getCoreSession();

		final DocumentModel oldFeuilleRouteDoc = (DocumentModel) ctx
				.getProperty(STEventConstant.DOSSIER_DISTRIBUTION_OLD_ROUTE_EVENT_PARAM);
		final DocumentModel newFeuilleRouteDoc = (DocumentModel) ctx
				.getProperty(STEventConstant.DOSSIER_DISTRIBUTION_NEW_ROUTE_EVENT_PARAM);
		String typeCreation = (String) ctx
				.getProperty(STEventConstant.DOSSIER_DISTRIBUTION_SUBSTITUTION_ROUTE_TYPE_EVENT_PARAM);

		// Démarre la nouvelle instance de feuille de route
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();
		dossierDistributionService.startRouteAfterSubstitution(session, oldFeuilleRouteDoc, newFeuilleRouteDoc,
				typeCreation);

		// Notifie les webservices
		if (STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REATTRIBUTION.equals(typeCreation)
				|| STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION.equals(typeCreation)) {
			final List<String> attachedDocuments = newFeuilleRouteDoc.getAdapter(STFeuilleRoute.class)
					.getAttachedDocuments();

			UnrestrictedDossierDataRead dataRead = new UnrestrictedDossierDataRead(session, attachedDocuments);
			dataRead.runUnrestricted();

		}
	}
}
