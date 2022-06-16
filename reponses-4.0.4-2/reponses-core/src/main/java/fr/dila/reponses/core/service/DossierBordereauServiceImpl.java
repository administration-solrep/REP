package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierBordereauService;
import fr.dila.reponses.api.service.ReponsesOrganigrammeService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du service de distribution des dossiers de Reponses.
 *
 * @author asatre
 */
public class DossierBordereauServiceImpl
    extends fr.dila.st.core.service.LogDocumentUpdateServiceImpl
    implements DossierBordereauService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public DossierBordereauServiceImpl() {
        super();
    }

    @Override
    protected Map<String, Object> getMap(DocumentModel questionDocument) {
        // on récupère toutes les propriétés liées au bordereau-
        Map<String, Object> map = new HashMap<>();
        map.putAll(questionDocument.getProperties(DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA));
        map.putAll(questionDocument.getProperties(DossierConstants.QUESTION_DOCUMENT_SCHEMA));
        return map;
    }

    @Override
    protected void fireEvent(
        final CoreSession session,
        final DocumentModel ancienDossierOrQuestion,
        final Entry<String, Object> entry,
        Object nouveauDossierValue,
        String ancienDossierValueLabel
    ) {
        final JournalService journalService = STServiceLocator.getJournalService();
        final ReponsesOrganigrammeService organigrammeService = ReponsesServiceLocator.getReponsesOrganigrammeService();
        DocumentModel ancienDossier;
        if (ancienDossierOrQuestion.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
            ancienDossier = session.getDocument(ancienDossierOrQuestion.getAdapter(Question.class).getDossierRef());
        } else {
            ancienDossier = ancienDossierOrQuestion;
        }

        String idValue = entry.getKey().substring(entry.getKey().indexOf(":") + 1);

        if (DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION.equals(idValue)) {
            if (nouveauDossierValue != null) {
                nouveauDossierValue =
                    organigrammeService.getOrganigrammeNodeById((String) nouveauDossierValue).getLabel();
            }
            if (ancienDossierValueLabel != null) {
                ancienDossierValueLabel =
                    organigrammeService.getOrganigrammeNodeById(ancienDossierValueLabel).getLabel();
            }
        }

        // journalisation de l'action dans les logs
        ReponsesVocabularyService vocService = ReponsesServiceLocator.getVocabularyService();
        String bordereauLabel = vocService.getLabelFromId(
            STVocabularyConstants.BORDEREAU_LABEL,
            idValue,
            STVocabularyConstants.COLUMN_LABEL
        );
        String comment =
            bordereauLabel +
            " : '" +
            (nouveauDossierValue != null ? nouveauDossierValue : "") +
            "' remplace '" +
            (ancienDossierValueLabel != null ? ancienDossierValueLabel : "") +
            "'";
        journalService.journaliserActionBordereau(session, ancienDossier, STEventConstant.BORDEREAU_UPDATE, comment);
    }

    @Override
    protected Set<String> getUnLoggableEntry() {
        return new HashSet<>();
    }
}
