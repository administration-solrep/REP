package fr.dila.reponses.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.st.web.converter.AbstractConverter;

/**
 * Converter JSF
 */
public class QuestionsUpdatedTimbreConverter extends AbstractConverter {

	/**
	 * Constructeur
	 * 
	 * @param directoryName
	 */
	public QuestionsUpdatedTimbreConverter() {

	}

	@Override
	public String getAsString(FacesContext context, UIComponent arg1, Object object) {
		if (object instanceof Long) {
			Long value = (Long) object;
			if (Long.valueOf(ReponsesLoggingLine.DASH_COUNT).equals(value)) {
				return "-";
			}
			return value.toString();
		}
		return null;
	}

}
