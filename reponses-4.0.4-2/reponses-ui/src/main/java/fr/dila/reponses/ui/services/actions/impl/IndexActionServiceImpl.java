package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.api.constant.DossierConstants.INDEXATION_DOCUMENT_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_AN;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_MINISTERE;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_SENAT;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.bean.IndexationItemDTO;
import fr.dila.reponses.ui.bean.VocSugUI;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class IndexActionServiceImpl implements IndexActionService {

    /**
     * @return une table qui contient les termes d'indexation relatifs à une ou plusieurs boites de suggestion.
     */
    public Map<String, VocSugUI> getVocMap() {
        return ReponsesServiceLocator
            .getVocabularyService()
            .getVocabularyList()
            .stream()
            .collect(Collectors.toMap(Function.identity(), VocSugUI::new));
    }

    public List<String[]> getListIndexByZone(DocumentModel indexableDocument, String indexationzoneName) {
        List<String[]> listIndexInitiale = getDocumentAdapted(indexableDocument).getListIndexByZone(indexationzoneName);

        if (INDEXATION_ZONE_AN.equals(indexationzoneName)) {
            return listIndexInitiale
                .stream()
                // On l'ajoute à la liste finale uniquement dans ce cas -> FEV 504
                .filter(
                    indexSousListe -> !indexSousListe[0].equals(TA_RUBRIQUE.getValue()) || !indexSousListe[1].isEmpty()
                )
                .collect(Collectors.toList());
        }

        return listIndexInitiale;
    }

    @Override
    public Map<String, IndexationItemDTO> getListIndexByZoneInMap(
        DocumentModel indexableDocument,
        String indexationzoneName
    ) {
        Map<String, IndexationItemDTO> returnMap = new HashMap<>();
        getListIndexByZone(indexableDocument, indexationzoneName)
            .forEach(
                tab -> {
                    IndexationItemDTO indexItem = new IndexationItemDTO();
                    if (returnMap.get(tab[0]) != null) {
                        indexItem = returnMap.get(tab[0]);
                    } else {
                        returnMap.put(tab[0], indexItem);
                    }
                    indexItem.getAllValues().add(tab[1]);
                    indexItem.getLabels().put(tab[1], tab[2]);
                }
            );
        return returnMap;
    }

    /**
     * Retourne un document adapté en ReponseIndexableDocument
     *
     * @param doc
     * @return Le document adapté
     */
    public ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc) {
        return doc.getAdapter(ReponsesIndexableDocument.class);
    }

    /**
     * Retourne la liste des vocabulaires correspondant à une zone d'indexation
     *
     * @param indexationZone
     * @return
     */
    public List<String> getDirectoriesByZone(String indexationZone) {
        return ReponsesServiceLocator.getVocabularyService().getMapVocabularyToZone().get(indexationZone);
    }

    public DocumentModel getCurrentIndexation(DocumentModel questionDoc, CoreSession session) {
        if (questionDoc != null) {
            return getIndexationComplementaire(questionDoc, session);
        }
        return session.createDocumentModel(DossierConstants.QUESTION_DOCUMENT_TYPE);
    }

    private DocumentModel getIndexationComplementaire(DocumentModel questionDoc, CoreSession session) {
        DocumentModel indexationComp = session.createDocumentModel(
            "/",
            INDEXATION_DOCUMENT_SCHEMA,
            RECHERCHE_DOCUMENT_TYPE
        );

        Question question = questionDoc.getAdapter(Question.class);

        ReponsesIndexableDocument indexationObj = indexationComp.getAdapter(ReponsesIndexableDocument.class);
        indexationObj.setSenatQuestionThemes(question.getIndexationComplSenatQuestionThemes());
        indexationObj.setSenatQuestionRubrique(question.getIndexationComplSenatQuestionRubrique());
        indexationObj.setSenatQuestionRenvois(question.getIndexationComplSenatQuestionRenvois());
        indexationObj.setAssNatRubrique(question.getIndexationComplAssNatRubrique());
        indexationObj.setAssNatTeteAnalyse(question.getIndexationComplAssNatTeteAnalyse());
        indexationObj.setAssNatAnalyses(question.getIndexationComplAssNatAnalyses());
        indexationObj.setMotsClefMinistere(question.getIndexationComplMotsClefMinistere());

        return indexationComp;
    }

    public void initIndexationDtoDirectories(IndexationDTO indexationDTO) {
        indexationDTO.setDirectoriesAN(getDirectoriesByZone(INDEXATION_ZONE_AN));
        indexationDTO.setDirectoriesSENAT(getDirectoriesByZone(INDEXATION_ZONE_SENAT));
        indexationDTO.setDirectoriesMinistere(getDirectoriesByZone(INDEXATION_ZONE_MINISTERE));
    }
}
