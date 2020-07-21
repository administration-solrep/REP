package fr.dila.reponses.web.recherche;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Ce bean seam permet d'assurer le support pour le widget indexation_widget.
 * 
 */
@Name("indexActions")
@Scope(CONVERSATION)
public class IndexActionsBean implements Serializable {

	private static final long				serialVersionUID	= -7882073590849165234L;

	@In(create = true, required = true)
	protected transient CoreSession			documentManager;

	@In(create = true, required = false)
	protected transient NavigationContext	navigationContext;

	@In(create = true, required = false)
	protected WebActions					webActions;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	private static final Log				log					= LogFactory.getLog(IndexActionsBean.class);

	// private static final String EDIT_SAISIE_NON_LIBRE = "edTF";

	/**
	 * 
	 * @return vocSuggMap La table qui contient les termes d'indexation relatifs à une ou plusieurs boites de
	 *         suggestion.
	 * @throws ClientException
	 * 
	 */

	@Factory(value = "vocSuggMap")
	public Map<String, VocSugUI> getVocMap() {
		Map<String, VocSugUI> vocSuggMap = new HashMap<String, VocSugUI>();
		ReponsesVocabularyService vs = ReponsesServiceLocator.getVocabularyService();
		for (String vocabulary : vs.getVocabularyList()) {
			vocSuggMap.put(vocabulary, new VocSugUI(vocabulary));
		}
		return vocSuggMap;
	}

	public String addIndex(DocumentModel indexableDocument, VocSugUI voc) throws ClientException {
		if (StringUtils.isBlank(voc.getIndexLabel())) {
			String messageKey = "message.add.new.index.not.empty";
			facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(messageKey),
					voc.getIndexLabel());
			return null;
		}
		if (!DossierConstants.DOSSIER_MOTS_CLEF_MINISTERE.equals(voc.getVocabularyLabel())) {
			List<String> listCompletion = voc.getSuggestions(voc.getIndexLabel());
			if (listCompletion == null || !listCompletion.contains(voc.getIndexLabel())) {
				String messageKey = "message.add.new.index.not.vocabulary";
				facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(messageKey),
						voc.getIndexLabel());
				return null;
			}
		}
		getDocumentAdapted(indexableDocument).addVocEntry(voc.getVocabularyLabel(), voc.getIndexLabel());
		voc.reset();
		return null;
	}

	public void removeIndex(DocumentModel indexableDocument, String[] item) throws ClientException {
		String vocabulary = item[0].trim();
		String label = item[1].trim();
		getDocumentAdapted(indexableDocument).removeVocEntry(vocabulary, label);
	}

	public List<String[]> getListIndexByZone(DocumentModel indexableDocument, String indexationzoneName)
			throws ClientException {
		List<String[]> listIndexInitiale = new ArrayList<String[]>();
		List<String[]> listIndexFinale = new ArrayList<String[]>();
		listIndexInitiale = getDocumentAdapted(indexableDocument).getListIndexByZone(indexationzoneName);
		if (indexationzoneName.equals("AN")) {
			for (String[] indexSousListe : listIndexInitiale) {
				if (indexSousListe[0].equals("TA_rubrique") && indexSousListe[1].isEmpty()) {
					// On ne l'ajoute pas à la liste finale -> FEV 504
				} else {
					// On l'ajoute à la liste finale
					listIndexFinale.add(indexSousListe);
				}
			}
		} else {
			return listIndexInitiale;
		}
		return listIndexFinale;
	}

	/**
	 * Retourne un document adapté en ReponseIndexableDocument
	 * 
	 * @param doc
	 * @return Le document adapté
	 * @throws ClientException
	 */
	public ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc) throws ClientException {
		return doc.getAdapter(ReponsesIndexableDocument.class);
	}

	/**
	 * Retourne l'ensemble des mots-clef, en mode unrestricted
	 * 
	 * @param doc
	 * @return Une liste de mots-clef
	 * @throws ClientException
	 */
	public String getMotsClefUnrestricted(DocumentModel doc) throws ClientException {
		if (doc == null) {
			log.warn("Document is null in getMotsClefUnrestricted");
			return "";
		}
		ReponsesIndexableDocument indexation = doc.getAdapter(ReponsesIndexableDocument.class);
		if (indexation == null) {
			log.error("L'adaptation en ReponseIndexableDocument n'a pas réussi avec le document de type "
					+ doc.getType());
			throw new ClientException("Ne peut pas donner les mots-clef du document");
		}
		return indexation.getMotsClef();
	}

	/**
	 * Retourne un document adapté à partir de la Question d'un DossierLink en ReponseIndexableDocument
	 */
	public ReponsesIndexableDocument getDocumentAdaptedFromDossierLink(DocumentModel dossierLinkDoc) {
		if (dossierLinkDoc == null) {
			return null;
		}

		DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
		DocumentModel dossierDoc = dossierLink.getCase(documentManager).getDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		Question question = dossier.getQuestion(documentManager);
		return question.getDocument().getAdapter(ReponsesIndexableDocument.class);
	}

	public String getMotCleAdaptedFromDossierLink(DocumentModel dossierLinkDoc) {
		if (dossierLinkDoc == null) {
			return null;
		}

		DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
		DocumentModel dossierDoc = dossierLink.getCase(documentManager).getDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		Question question = dossier.getQuestion(documentManager);
		return question.getMotsCles();
	}

	/**
	 * Retourne la liste des vocabulaires correspondant à une zone d'indexation
	 * 
	 * @param indexationZone
	 * @return
	 */
	public List<String> getDirectoriesByZone(String indexationZone) {
		final ReponsesVocabularyService vocService = ReponsesServiceLocator.getVocabularyService();
		return vocService.getMapVocabularyToZone().get(indexationZone);
	}

}