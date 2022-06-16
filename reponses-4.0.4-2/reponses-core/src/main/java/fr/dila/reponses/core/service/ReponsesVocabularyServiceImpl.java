/**
 *
 */
package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.reponses.api.vocabulary.VocabularyGroupConnector;
import fr.dila.reponses.core.vocabulary.VocabularyConnectorGroupImpl;
import fr.dila.reponses.core.vocabulary.VocabularyConnectorImpl;
import fr.dila.st.core.service.VocabularyServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 * Le service de vocabulaire de Réponse. Il apporte des fonctionnalités sur les vocabulaires spécificique à l'application Réponse.
 *
 * @author jgomez
 *
 */
public class ReponsesVocabularyServiceImpl extends VocabularyServiceImpl implements ReponsesVocabularyService {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ReponsesVocabularyService.class);

    /**
     * Crée une map liant les vocabulaires aux zones d'indexation
     */
    @Override
    public Map<String, List<String>> getMapVocabularyToZone() {
        Map<String, List<String>> mapVocabularyToZone;
        mapVocabularyToZone = new HashMap<>();
        List<String> senatVocabularyList = new ArrayList<>();
        senatVocabularyList.add(SE_THEME.getValue());
        senatVocabularyList.add(SE_RUBRIQUE.getValue());
        senatVocabularyList.add(SE_RENVOI.getValue());
        mapVocabularyToZone.put(VocabularyConstants.INDEXATION_ZONE_SENAT, senatVocabularyList);
        // Création de la zone d'indexation de l'Assemblée nationale
        List<String> anVocabularyList = new ArrayList<>();

        anVocabularyList.add(AN_RUBRIQUE.getValue());
        anVocabularyList.add(TA_RUBRIQUE.getValue());
        anVocabularyList.add(AN_ANALYSE.getValue());
        mapVocabularyToZone.put(VocabularyConstants.INDEXATION_ZONE_AN, anVocabularyList);
        // Création de la zone d'indexation des ministères
        List<String> minVocabularyList = new ArrayList<>();

        minVocabularyList.add(MOTSCLEF_MINISTERE.getValue());
        mapVocabularyToZone.put(VocabularyConstants.INDEXATION_ZONE_MINISTERE, minVocabularyList);
        return mapVocabularyToZone;
    }

    @Override
    public Set<String> getZones() {
        return getMapVocabularyToZone().keySet();
    }

    @Override
    public VocabularyConnector getVocabularyConnector(String directoryName) {
        VocabularyConnectorImpl elt = null;
        try {
            elt = new VocabularyConnectorImpl(directoryName, this);
        } catch (NuxeoException e) {
            log.error("Le service a été incapable de créer l'élement d'indexation " + directoryName, e);
        }
        return elt;
    }

    protected VocabularyGroupConnector getIndexationGroup() {
        VocabularyGroupConnector elt = null;
        try {
            elt = new VocabularyConnectorGroupImpl(this);
        } catch (NuxeoException e) {
            log.error("Le service a été incapable de créer le groupe d'indexation", e);
        }
        return elt;
    }

    @Override
    public VocabularyGroupConnector getVocabularyConnectorGroup(String... directoryName) {
        VocabularyGroupConnector group = null;
        group = getIndexationGroup();
        for (String voc : directoryName) {
            group.add(getVocabularyConnector(voc));
        }
        return group;
    }

    @Override
    public VocabularyConnector getIndexationAN_Rubrique() {
        return getVocabularyConnector(AN_RUBRIQUE.getValue());
    }

    @Override
    public VocabularyConnector getIndexationAN_TARubrique() {
        return getVocabularyConnector(TA_RUBRIQUE.getValue());
    }

    @Override
    public VocabularyConnector getIndexation_ANAnalyse() {
        return getVocabularyConnector(AN_ANALYSE.getValue());
    }

    @Override
    public VocabularyConnector getIndexation_AN() {
        VocabularyGroupConnector an = getIndexationGroup();
        an.add(getIndexationAN_Rubrique());
        an.add(getIndexationAN_TARubrique());
        an.add(getIndexation_ANAnalyse());
        return an;
    }

    @Override
    public VocabularyConnector getIndexationSE_Theme() {
        return getVocabularyConnector(SE_THEME.getValue());
    }

    @Override
    public VocabularyConnector getIndexation_SE() {
        VocabularyGroupConnector se = getIndexationGroup();
        se.add(getIndexationSE_Rubrique());
        se.add(getIndexationSE_Renvoi());
        se.add(getIndexationMinistere());
        return se;
    }

    @Override
    public VocabularyConnector getIndexationSE_Rubrique() {
        return getVocabularyConnector(SE_RUBRIQUE.getValue());
    }

    @Override
    public VocabularyConnector getIndexationSE_Renvoi() {
        return getVocabularyConnector(SE_RENVOI.getValue());
    }

    @Override
    public VocabularyConnector getIndexationMinistere() {
        return getVocabularyConnector(MOTSCLEF_MINISTERE.getValue());
    }

    @Override
    public List<String> getVocabularyList() {
        List<String> vocList = new ArrayList<>();
        for (String zone : getMapVocabularyToZone().keySet()) {
            vocList.addAll(getMapVocabularyToZone().get(zone));
        }
        return vocList;
    }
}
