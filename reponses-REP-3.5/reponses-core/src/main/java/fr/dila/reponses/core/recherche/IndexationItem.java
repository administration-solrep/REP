package fr.dila.reponses.core.recherche;

import java.util.HashMap;
import java.util.Map;

import fr.dila.reponses.api.constant.VocabularyConstants;

public class IndexationItem {
	private String						vocabulary, voc_prefix, label;

	private static Map<String, String>	VOC_NAME_TO_PREFIX	= new HashMap<String, String>();
	static {
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.SE_RUBRIQUE, "Ru");
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.SE_THEME, "Th");
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.SE_RENVOI, "Re");
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.AN_RUBRIQUE, "Ru");
		// Anciennement avec le préfix An (FEV 504) - Analyse est devenu Titre
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.AN_ANALYSE, "Ti");
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.TA_RUBRIQUE, "TA");
		VOC_NAME_TO_PREFIX.put(VocabularyConstants.MOTSCLEF_MINISTERE, "");
	}

	public IndexationItem(String voc, String label) {
		this.vocabulary = voc;
		this.label = label;
		this.voc_prefix = VOC_NAME_TO_PREFIX.get(voc);

	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getVocabulary() {
		return vocabulary;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setVoc_prefix(String voc_prefix) {
		this.voc_prefix = voc_prefix;
	}

	public String getVoc_prefix() {
		return voc_prefix;
	}

}
