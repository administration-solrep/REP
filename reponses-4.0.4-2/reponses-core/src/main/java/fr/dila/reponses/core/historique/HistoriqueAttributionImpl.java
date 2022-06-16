package fr.dila.reponses.core.historique;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de la réprésentation des historiques attribution
 *
 */
public class HistoriqueAttributionImpl implements HistoriqueAttribution {
    /**
     *
     */
    private static final long serialVersionUID = 5842979004437091555L;
    private DocumentModel document;

    public HistoriqueAttributionImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public String getMinAttribution() {
        return PropertyUtil.getStringProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_MIN_PROPERTY
        );
    }

    @Override
    public Calendar getDateAttribution() {
        return PropertyUtil.getCalendarProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DATE_PROPERTY
        );
    }

    @Override
    public String getTypeAttribution() {
        return PropertyUtil.getStringProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_TYPE_PROPERTY
        );
    }

    @Override
    public void setMinAttribution(String minAttribution) {
        PropertyUtil.setProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_MIN_PROPERTY,
            minAttribution
        );
    }

    @Override
    public void setDateAttribution(Calendar dateAttribution) {
        PropertyUtil.setProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DATE_PROPERTY,
            dateAttribution
        );
    }

    @Override
    public void setTypeAttribution(String typeAttribution) {
        PropertyUtil.setProperty(
            getDocument(),
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_TYPE_PROPERTY,
            typeAttribution
        );
    }
}
