package fr.dila.reponses.core.domain;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.domain.JetonDoc;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;

/**
 * 
 * @author admin
 */
public class JetonDocImpl extends fr.dila.st.core.jeton.JetonDocImpl implements JetonDoc {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructeur de JetonDocImpl.
     * 
     * @param doc Modèle de document
     */
    public JetonDocImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getMinAttribution() {
    	return PropertyUtil.getStringProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_MIN);
    }

    @Override
    public Calendar getDateAttribution() {
        return PropertyUtil.getCalendarProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_DATE);
    }

    @Override
    public String getTypeAttribution() {
    	return PropertyUtil.getStringProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_TYPE);
    }

	@Override
	public void setMinAttribution(String minAttribution) {
		PropertyUtil.setProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_MIN, minAttribution);
	}

	@Override
	public void setDateAttribution(Calendar dateAttribution) {
		PropertyUtil.setProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_DATE, dateAttribution);
	}

	@Override
	public void setTypeAttribution(String typeAttribution) {
		PropertyUtil.setProperty(getDocument(), STSchemaConstant.JETON_DOCUMENT_SCHEMA, ReponsesSchemaConstant.JETON_DOC_ATTRIBUTION_TYPE, typeAttribution);
	}
}
