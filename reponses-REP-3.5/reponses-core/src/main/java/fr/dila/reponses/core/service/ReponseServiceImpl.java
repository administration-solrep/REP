package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.cases.flux.RErratumImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;

public class ReponseServiceImpl implements ReponseService {

	private static final Log	LOG	= LogFactory.getLog(ReponseServiceImpl.class);

	/**
	 * Default constructor
	 */
	public ReponseServiceImpl() {
		// do nothing
	}

	@Override
	public DocumentModel getReponseOldVersionDocument(CoreSession session, DocumentModel reponse, int versionNumber)
			throws ClientException {
		Long searchedVersion = Long.valueOf(versionNumber);
		// get all reponse version
		List<DocumentModel> versionList = getReponseVersionDocumentList(session, reponse);
		for (DocumentModel dm : versionList) {

			if (searchedVersion.equals(dm.getProperty(STSchemaConstant.UID_SCHEMA,
					STSchemaConstant.UID_MAJOR_VERSION_PROPERTY))) {
				return dm;
			}
		}
		throw new ReponsesException("Reponse version not found (" + searchedVersion + ")");
	}

	@Override
	public List<DocumentModel> getReponseVersionDocumentList(CoreSession session, DocumentModel reponse)
			throws ClientException {
		DocumentRef docRef = reponse.getRef();
		// get all reponse version
		List<DocumentModel> dml = session.getVersions(docRef);

		if (LOG.isDebugEnabled()) {
			for (DocumentModel dm : dml) {
				LOG.debug("vers : "
						+ dm.getProperty(STSchemaConstant.UID_SCHEMA, STSchemaConstant.UID_MAJOR_VERSION_PROPERTY)
						+ " content= "
						+ dm.getProperty(STSchemaConstant.NOTE_SCHEMA, STSchemaConstant.NOTE_NOTE_PROPERTY));
			}
		}

		return dml;
	}

	@Override
	public DocumentModel saveReponse(CoreSession session, final DocumentModel inputReponseDoc, DocumentModel dossierDoc)
			throws ClientException {
		// Chargement des services
		final JournalService journalService = STServiceLocator.getJournalService();
		final EventProducer producer = STServiceLocator.getEventProducer();
		final STParametreService parametreService = STServiceLocator.getSTParametreService();

		String reponseAuthor = session.getPrincipal().getName();
		DocumentModel reponseDoc = inputReponseDoc;
		Reponse reponse = reponseDoc.getAdapter(Reponse.class);
		reponse.setAuteur(reponseAuthor);
		reponse.setTexteReponse(reponse.getTexteReponse());
		reponseDoc = session.saveDocument(reponseDoc);
		Dossier dossier = null;
		if (dossierDoc != null) {
			dossier = dossierDoc.getAdapter(Dossier.class);
		}
		Question question = null;
		if (dossier != null) {
			question = dossier.getQuestion(session);
		}

		if (question != null) {
			// On met à jour la dénormalisation, sur la question
			question.setHasReponseInitiee(!StringUtils.isBlank(reponse.getTexteReponse()));
		}

		session.save();

		if (dossier != null) {
			journalService.journaliserActionParapheur(session, dossierDoc,
					ReponsesEventConstant.DOSSIER_PARAPHEUR_UPDATE_EVENT,
					ReponsesEventConstant.COMMENT_PARAPHEUR_UPDATE);

			final DocumentEventContext journalEventContextReponse = new DocumentEventContext(session,
					session.getPrincipal(), reponseDoc);
			producer.fireEvent(journalEventContextReponse.newEvent(ReponsesEventConstant.DOSSIER_REPONSE_UPDATE_EVENT));
		}
		return reponseDoc;
	}

	@Override
	public DocumentModel saveReponseAndErratum(CoreSession documentManager, DocumentModel reponseDoc,
			DocumentModel currentDocument) throws ClientException {
		Reponse reponse = reponseDoc.getAdapter(Reponse.class);
		// Cas de l'errata de réponse : on ajoute l'erratum
		if (reponse.isPublished()) {
			RErratum currentErratum = new RErratumImpl();
			currentErratum.setPageJo(0);
			currentErratum.setTexteErratum(reponse.getCurrentErratum());
			currentErratum.setTexteConsolide(reponse.getTexteReponse());
			List<RErratum> erratums = reponse.getErrata();
			erratums.add(currentErratum);
			reponse.setErrata(erratums);
		}
		return saveReponse(documentManager, reponseDoc, currentDocument);
	}

	@Override
	public DocumentModel briserSignatureReponse(CoreSession session, Reponse reponse, DocumentModel dossier)
			throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		String reponseAuthor = session.getPrincipal().getName();

		reponse.setSignature(null);
		reponse.setIsSignatureValide(false);
		reponse.setAuthorRemoveSignature(reponseAuthor);
		DocumentModel reponseDoc = session.saveDocument(reponse.getDocument());
		session.save();

		// Journalise la modification de la réponse
		journalService.journaliserActionParapheur(session, dossier,
				ReponsesEventConstant.DOSSIER_BRISER_SIGNATURE_EVENT,
				ReponsesEventConstant.COMMENT_PARAPHEUR_REMOVE_SIGNATURE);

		// Gestion des allotissements
		Dossier dossierObject = dossier.getAdapter(Dossier.class);
		if (dossierObject != null) {
			if (allotissementService.isAllotit(dossierObject, session)) {
				allotissementService.updateSignatureLinkedReponses(session, reponse);
			}
		}

		return reponseDoc;
	}

	@Override
	public DocumentModel saveReponseFromMinistere(CoreSession session, DocumentModel reponse) throws ClientException {
		// increment major version number
		reponse.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		DocumentModel newReponse = session.saveDocument(reponse);
		session.save();

		EventProducer producer = STServiceLocator.getEventProducer();
		final DocumentEventContext journalEventContextReponse = new DocumentEventContext(session,
				session.getPrincipal(), newReponse);
		producer.fireEvent(journalEventContextReponse
				.newEvent(ReponsesEventConstant.DOSSIER_REPONSE_VERSION_UPDATE_EVENT));

		return newReponse;
	}

	/**
	 * 
	 * @see fr.dila.reponses.api.service.DossierDistributionService#getDossierFromReponse(org.nuxeo.ecm.core.api.CoreSession,
	 *      org.nuxeo.ecm.core.api.DocumentModel)
	 */
	@Override
	public DocumentModel getDossierFromReponse(CoreSession session, DocumentModel reponse) throws ClientException {
		DocumentRef dossierRef = reponse.getParentRef();
		return session.getDocument(dossierRef);
	}

	/**
	 * 
	 * @param session
	 * @param reponse
	 * @return liste des contributeurs de chaque version, par ordre de version
	 * @throws PropertyException
	 * @throws ClientException
	 */
	@Override
	public List<String> getVersionsContributorsFromReponse(CoreSession session, DocumentModel reponse)
			throws ClientException {
		List<String> versionContributors = new ArrayList<String>();
		for (DocumentModel doc : getReponseVersionDocumentList(session, reponse)) {
			versionContributors.add((String) doc.getProperty(DossierConstants.REPONSE_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_ID_AUTEUR_REPONSE));
		}
		return versionContributors;
	}

	@Override
	public DocumentModel incrementReponseVersion(CoreSession session, DocumentModel reponse) throws ClientException {
		// Increment major version number
		reponse.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);

		// Sauvegarde
		final DocumentModel doc = session.saveDocument(reponse);

		final EventProducer producer = STServiceLocator.getEventProducer();
		final DocumentEventContext journalEventContextReponse = new DocumentEventContext(session,
				session.getPrincipal(), doc);
		producer.fireEvent(journalEventContextReponse
				.newEvent(ReponsesEventConstant.DOSSIER_REPONSE_VERSION_UPDATE_EVENT));

		return doc;
	}

	@Override
	public int getReponseMajorVersionNumber(CoreSession session, DocumentModel reponse) throws ClientException {
		final Long majorVersion = (Long) reponse.getProperty(STSchemaConstant.UID_SCHEMA,
				STSchemaConstant.UID_MAJOR_VERSION_PROPERTY);
		return majorVersion.intValue();
	}

	/**
	 * Find Reponse from Dossier
	 */
	@Override
	public DocumentModel getReponseFromDossier(CoreSession session, DocumentModel dossier) {
		if (dossier != null) {
			Dossier dossierObject = dossier.getAdapter(Dossier.class);
			if (dossierObject != null) {
				Reponse reponse = dossierObject.getReponse(session);
				if (reponse != null) {
					return reponse.getDocument();
				}
			}
		}
		return null;
	}

	@Override
	public boolean isReponseSignee(final CoreSession session, final DocumentModel dossierDoc) {
		if (dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
			final DocumentModel reponseDoc = getReponseFromDossier(session, dossierDoc);
			if (reponseDoc != null) {
				final Reponse reponse = reponseDoc.getAdapter(Reponse.class);
				if (reponse != null) {
					return reponse.isSignee();
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isReponsePublished(final CoreSession session, final DocumentModel dossierDoc) {
		if (dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
			ReponseService reponseService = ReponsesServiceLocator.getReponseService();
			DocumentModel reponseDoc = reponseService.getReponseFromDossier(session, dossierDoc);
			if (reponseDoc != null) {
				Reponse reponse = reponseDoc.getAdapter(Reponse.class);
				if (reponse != null) {
					return reponse.isPublished();
				}
			}
		}
		return false;
	}

}
