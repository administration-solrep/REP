package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.recherche.ReponsesComplIndexableDocument;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class IndexationImpl implements ReponsesIndexableDocument, ReponsesComplIndexableDocument {
    protected ReponsesVocabularyService vocService;

    protected DocumentModel document;

    /**
     * Le schéma sur lequel on prend et on cherche les données
     * ossierConstants.INDEXATION_DOCUMENT_SCHEMA ou INDEXATION_COMPL_DOCUMENT_SCHEMA
     *
     */
    private String schema;

    public IndexationImpl(DocumentModel doc) {
        this(doc, DossierConstants.INDEXATION_DOCUMENT_SCHEMA);
    }

    public IndexationImpl(DocumentModel doc, String schema) {
        super();
        document = doc;
        vocService = ReponsesServiceLocator.getVocabularyService();
        this.schema = schema;
    }

    @Override
    public void addVocEntry(String voc, String label) {
        try {
            List<String> indexList = getVocEntries(voc);
            indexList.add(label);
            setVocEntries(voc, indexList);
        } catch (Exception e) {
            throw new NuxeoException("Erreur dans l'ajout d'une valeur de vocabulaire");
        }
    }

    @Override
    public void setVocEntries(String voc, List<String> indexList) {
        PropertyUtil.setProperty(document, schema, voc, indexList);
    }

    @Override
    public List<String> getVocEntries(String voc) {
        return PropertyUtil.getStringListProperty(document, schema, voc);
    }

    @Override
    public void removeVocEntry(String voc, String label) {
        List<String> indexList = getVocEntries(voc);
        indexList.remove(label);
        setVocEntries(voc, indexList);
    }

    @Override
    public List<String[]> getListIndexByZone(String zone) {
        if (vocService == null) {
            return new ArrayList<String[]>();
        }
        List<String[]> libelleResultList = new ArrayList<String[]>();
        Map<String, List<String>> vocabularies = vocService.getMapVocabularyToZone();
        for (String vocabulary : vocabularies.get(zone)) {
            List<String> indexList = getVocEntries(vocabulary);
            for (String libelle : indexList) {
                String[] libelleArray = new String[3];
                libelleArray[0] = vocabulary;
                libelleArray[1] = libelle;
                libelleArray[2] = "label.vocabulary." + vocabulary;
                libelleResultList.add(libelleArray);
            }
        }
        //  Collections.sort(libelleResultList
        return libelleResultList;
    }

    @Override
    public String getMotsClef() {
        StringBuffer bf = new StringBuffer();
        Map<String, List<String>> vocabularies = vocService.getMapVocabularyToZone();
        for (Entry<String, List<String>> entry : vocabularies.entrySet()) {
            for (String vocabularyName : entry.getValue()) {
                List<String> indexationDatas = getVocEntries(vocabularyName);
                for (String indexationData : indexationDatas) {
                    bf.append(indexationData + " ");
                }
            }
        }
        return bf.toString();
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public List<String> getAssNatAnalyses() {
        return getVocEntries(AN_ANALYSE.getValue());
    }

    @Override
    public void setAssNatAnalyses(List<String> analyseAssNat) {
        setVocEntries(AN_ANALYSE.getValue(), analyseAssNat);
    }

    @Override
    public List<String> getAssNatRubrique() {
        return getVocEntries(AN_RUBRIQUE.getValue());
    }

    @Override
    public void setAssNatRubrique(List<String> rubriqueAssNat) {
        setVocEntries(AN_RUBRIQUE.getValue(), rubriqueAssNat);
    }

    @Override
    public List<String> getAssNatTeteAnalyse() {
        return getVocEntries(TA_RUBRIQUE.getValue());
    }

    @Override
    public void setAssNatTeteAnalyse(List<String> teteAnalyseAssNat) {
        setVocEntries(TA_RUBRIQUE.getValue(), teteAnalyseAssNat);
    }

    @Override
    public List<String> getSenatQuestionThemes() {
        return getVocEntries(SE_THEME.getValue());
    }

    @Override
    public void setSenatQuestionThemes(List<String> senatQuestionTheme) {
        setVocEntries(SE_THEME.getValue(), senatQuestionTheme);
    }

    @Override
    public List<String> getSenatQuestionRubrique() {
        return getVocEntries(SE_RUBRIQUE.getValue());
    }

    @Override
    public void setSenatQuestionRubrique(List<String> senatRubrique) {
        setVocEntries(SE_RUBRIQUE.getValue(), senatRubrique);
    }

    @Override
    public List<String> getSenatQuestionRenvois() {
        return getVocEntries(SE_RENVOI.getValue());
    }

    @Override
    public void setSenatQuestionRenvois(List<String> senatRenvois) {
        setVocEntries(SE_RENVOI.getValue(), senatRenvois);
    }

    @Override
    public List<String> getMotsClefMinistere() {
        return getVocEntries(MOTSCLEF_MINISTERE.getValue());
    }

    @Override
    public void setMotsClefMinistere(List<String> motClefsMinistere) {
        setVocEntries(MOTSCLEF_MINISTERE.getValue(), motClefsMinistere);
    }
}
