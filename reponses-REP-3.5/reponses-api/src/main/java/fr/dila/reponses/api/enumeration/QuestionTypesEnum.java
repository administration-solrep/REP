package fr.dila.reponses.api.enumeration;

import java.util.ArrayList;
import java.util.List;

import fr.dila.reponses.api.constant.VocabularyConstants;

public enum QuestionTypesEnum {
	QE(VocabularyConstants.QUESTION_TYPE_QE),
	QOSD(VocabularyConstants.QUESTION_TYPE_QOSD),
	QOAD(VocabularyConstants.QUESTION_TYPE_QOAD),
	QOAE(VocabularyConstants.QUESTION_TYPE_QOAE),
	QG(VocabularyConstants.QUESTION_TYPE_QG),
	QC(VocabularyConstants.QUESTION_TYPE_QC);
	
	private String type;
	
	QuestionTypesEnum(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	public static boolean isValueInEnum(String value) {			
		for (QuestionTypesEnum type : values()) {
			if (type.toString().equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<String> toStrings() {
		ArrayList<String> values = new ArrayList<String>();
		for (QuestionTypesEnum type : values()) {
			values.add(type.toString());
		}
		return values;
	}
}
