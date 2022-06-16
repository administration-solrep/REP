package fr.dila.reponses.core.domain.user;

import fr.dila.reponses.api.domain.user.Delegation;
import fr.dila.reponses.core.constant.DelegationConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier délégation.
 *
 * @author jtremeaux
 */
public class DelegationImpl implements Delegation {
    protected DocumentModel document;

    /**
     * Constructeur de DelegationImpl.
     *
     * @param document
     *            Document
     */
    public DelegationImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public void setDocument(DocumentModel document) {
        this.document = document;
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DATE_FIN_PROPERTY_NAME
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DATE_FIN_PROPERTY_NAME,
            dateFin
        );
    }

    @Override
    public String getSourceId() {
        return PropertyUtil.getStringProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME
        );
    }

    @Override
    public void setSourceId(String sourceId) {
        PropertyUtil.setProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME,
            sourceId
        );
    }

    @Override
    public String getDestinataireId() {
        return PropertyUtil.getStringProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME
        );
    }

    @Override
    public void setDestinataireId(String destinataireId) {
        PropertyUtil.setProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME,
            destinataireId
        );
    }

    @Override
    public List<String> getProfilListId() {
        return PropertyUtil.getStringListProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_PROFIL_LIST_PROPERTY_NAME
        );
    }

    @Override
    public void setProfilListId(List<String> profilListId) {
        PropertyUtil.setProperty(
            document,
            DelegationConstant.DELEGATION_SCHEMA,
            DelegationConstant.DELEGATION_PROFIL_LIST_PROPERTY_NAME,
            profilListId
        );
    }
}
