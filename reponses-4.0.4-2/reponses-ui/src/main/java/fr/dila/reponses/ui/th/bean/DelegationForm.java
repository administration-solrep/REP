package fr.dila.reponses.ui.th.bean;

import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.mapper.MapDoc2BeanPrincipalProcess;
import fr.dila.st.ui.mapper.MapDoc2BeanUserFullNameProcess;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class DelegationForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @FormParam("id")
    @NxProp(docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE, xpath = STSchemaConstant.ECM_UUID_XPATH)
    private String id;

    @FormParam("delegue-key")
    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_DESTINATAIRE_ID
    )
    private String delegue;

    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_DESTINATAIRE_ID,
        process = MapDoc2BeanUserFullNameProcess.class,
        way = NxProp.Way.DOC_TO_BEAN
    )
    private String delegueName;

    private Map<String, String> mapDelegue = new HashMap<>();

    @FormParam("dateDebut")
    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_DATE_DEBUT
    )
    private Calendar dateDebut;

    @FormParam("dateFin")
    @NxProp(docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE, xpath = DelegationConstant.DELEGATION_XPATH_DATE_FIN)
    private Calendar dateFin;

    @FormParam("profils")
    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_PROFIL_LIST
    )
    private ArrayList<String> profils = new ArrayList<>();

    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_SOURCE_ID,
        process = MapDoc2BeanPrincipalProcess.class
    )
    private String sourceId;

    @NxProp(
        docType = DelegationConstant.DELEGATION_DOCUMENT_TYPE,
        xpath = DelegationConstant.DELEGATION_XPATH_SOURCE_ID,
        process = MapDoc2BeanUserFullNameProcess.class,
        way = NxProp.Way.DOC_TO_BEAN
    )
    private String sourceName;

    public DelegationForm() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDelegue() {
        return delegue;
    }

    public void setDelegue(String delegue) {
        this.delegue = delegue;
    }

    public String getDelegueName() {
        return delegueName;
    }

    public void setDelegueName(String delegueName) {
        this.delegueName = delegueName;
    }

    public Calendar getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Calendar dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Calendar getDateFin() {
        return dateFin;
    }

    public void setDateFin(Calendar dateFin) {
        this.dateFin = dateFin;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public List<String> getProfils() {
        return profils;
    }

    public void setProfils(ArrayList<String> profils) {
        this.profils = profils;
    }

    public Map<String, String> getMapDelegue() {
        return mapDelegue;
    }

    public void setMapDelegue(Map<String, String> mapDelegue) {
        this.mapDelegue = mapDelegue;
    }
}
