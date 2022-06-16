package fr.dila.reponses.api.hibernate;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DOSSIER_REPONSE")
public class DossierReponse implements Serializable {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4166589405071673371L;

    @Id
    @Column(name = "ID", nullable = false, updatable = false, insertable = false)
    private String id;

    @Column(name = "NUMEROQUESTION")
    private Integer numeroQuestion;

    @Column(name = "IDDOCUMENTQUESTION")
    private String idDocumentQuestion;

    @Column(name = "LASTDOCUMENTROUTE")
    private String lastDocumentRoute;

    @Column(name = "LISTEELIMINATION")
    private String listeElimination;

    @Column(name = "IDDOCUMENTREPONSE")
    private String idDocumentReponse;

    @Column(name = "ETAPEREDACTIONATTEINTE")
    private Boolean etapeRedactionAtteinte;

    @Column(name = "IDDOSSIERLOT")
    private String idDossierLot;

    @Column(name = "IDDOCUMENTFDD")
    private String idDocumentFdd;

    @Column(name = "MINISTEREATTRIBUTAIRECOURANT")
    private String ministereAttributaireCourant;

    @Column(name = "REAFFECTATIONCOUNT")
    private Integer reaffectationCount;

    @Column(name = "ETAPESIGNATUREATTEINTE")
    private Boolean etapeSignatureAtteinte;

    @Column(name = "MINISTEREATTRIBUTAIREPRECEDENT")
    private String ministereAttributairePrecedent;

    @Column(name = "MINISTEREREATTRIBUTION")
    private String ministereReattribution;

    @Column(name = "LABELETAPESUIVANTE")
    private String labelEtapeSuivante;

    @Column(name = "HISTORIQUEDOSSIERTRAITE")
    private Boolean historiqueDossierTraite;

    @Column(name = "ISARBITRATED")
    private Boolean isArbitrated;

    @Column(name = "HASPJ")
    private Boolean hasPJ;

    @Column(name = "ISREDEMARRE")
    private Boolean isRedemarre;

    public DossierReponse() {
        super();
    }

    /**
     * @param id
     * @param numeroQuestion
     * @param idDocumentQuestion
     * @param lastDocumentRoute
     * @param listeElimination
     * @param idDocumentReponse
     * @param etapeRedactionAtteinte
     * @param idDossierLot
     * @param idDocumentFdd
     * @param ministereAttributaireCourant
     * @param reaffectationCount
     * @param etapeSignatureAtteinte
     * @param ministereAttributairePrecedent
     * @param ministereReattribution
     * @param labelEtapeSuivante
     * @param historiqueDossierTraite
     * @param isArbitrated
     */
    public DossierReponse(
        String id,
        Integer numeroQuestion,
        String idDocumentQuestion,
        String lastDocumentRoute,
        String listeElimination,
        String idDocumentReponse,
        Boolean etapeRedactionAtteinte,
        String idDossierLot,
        String idDocumentFdd,
        String ministereAttributaireCourant,
        Integer reaffectationCount,
        Boolean etapeSignatureAtteinte,
        String ministereAttributairePrecedent,
        String ministereReattribution,
        String labelEtapeSuivante,
        Boolean historiqueDossierTraite,
        Boolean isArbitrated,
        Boolean hasPJ
    ) {
        super();
        this.id = id;
        this.numeroQuestion = numeroQuestion;
        this.idDocumentQuestion = idDocumentQuestion;
        this.lastDocumentRoute = lastDocumentRoute;
        this.listeElimination = listeElimination;
        this.idDocumentReponse = idDocumentReponse;
        this.etapeRedactionAtteinte = etapeRedactionAtteinte;
        this.idDossierLot = idDossierLot;
        this.idDocumentFdd = idDocumentFdd;
        this.ministereAttributaireCourant = ministereAttributaireCourant;
        this.reaffectationCount = reaffectationCount;
        this.etapeSignatureAtteinte = etapeSignatureAtteinte;
        this.ministereAttributairePrecedent = ministereAttributairePrecedent;
        this.ministereReattribution = ministereReattribution;
        this.labelEtapeSuivante = labelEtapeSuivante;
        this.historiqueDossierTraite = historiqueDossierTraite;
        this.isArbitrated = isArbitrated;
        this.hasPJ = hasPJ;
    }

    /**
     * @return the numeroQuestion
     */
    public Integer getNumeroQuestion() {
        return numeroQuestion;
    }

    /**
     * @param numeroQuestion
     *            the numeroQuestion to set
     */
    public void setNumeroQuestion(Integer numeroQuestion) {
        this.numeroQuestion = numeroQuestion;
    }

    /**
     * @return the idDocumentQuestion
     */
    public String getIdDocumentQuestion() {
        return idDocumentQuestion;
    }

    /**
     * @param idDocumentQuestion
     *            the idDocumentQuestion to set
     */
    public void setIdDocumentQuestion(String idDocumentQuestion) {
        this.idDocumentQuestion = idDocumentQuestion;
    }

    /**
     * @return the lastDocumentRoute
     */
    public String getLastDocumentRoute() {
        return lastDocumentRoute;
    }

    /**
     * @param lastDocumentRoute
     *            the lastDocumentRoute to set
     */
    public void setLastDocumentRoute(String lastDocumentRoute) {
        this.lastDocumentRoute = lastDocumentRoute;
    }

    /**
     * @return the listeElimination
     */
    public String getListeElimination() {
        return listeElimination;
    }

    /**
     * @param listeElimination
     *            the listeElimination to set
     */
    public void setListeElimination(String listeElimination) {
        this.listeElimination = listeElimination;
    }

    /**
     * @return the idDocumentReponse
     */
    public String getIdDocumentReponse() {
        return idDocumentReponse;
    }

    /**
     * @param idDocumentReponse
     *            the idDocumentReponse to set
     */
    public void setIdDocumentReponse(String idDocumentReponse) {
        this.idDocumentReponse = idDocumentReponse;
    }

    /**
     * @return the etapeRedactionAtteinte
     */
    public Boolean getEtapeRedactionAtteinte() {
        return etapeRedactionAtteinte;
    }

    /**
     * @param etapeRedactionAtteinte
     *            the etapeRedactionAtteinte to set
     */
    public void setEtapeRedactionAtteinte(Boolean etapeRedactionAtteinte) {
        this.etapeRedactionAtteinte = etapeRedactionAtteinte;
    }

    /**
     * @return the idDossierLot
     */
    public String getIdDossierLot() {
        return idDossierLot;
    }

    /**
     * @param idDossierLot
     *            the idDossierLot to set
     */
    public void setIdDossierLot(String idDossierLot) {
        this.idDossierLot = idDossierLot;
    }

    /**
     * @return the idDocumentFdd
     */
    public String getIdDocumentFdd() {
        return idDocumentFdd;
    }

    /**
     * @param idDocumentFdd
     *            the idDocumentFdd to set
     */
    public void setIdDocumentFdd(String idDocumentFdd) {
        this.idDocumentFdd = idDocumentFdd;
    }

    /**
     * @return the ministereAttributaireCourant
     */
    public String getMinistereAttributaireCourant() {
        return ministereAttributaireCourant;
    }

    /**
     * @param ministereAttributaireCourant
     *            the ministereAttributaireCourant to set
     */
    public void setMinistereAttributaireCourant(String ministereAttributaireCourant) {
        this.ministereAttributaireCourant = ministereAttributaireCourant;
    }

    /**
     * @return the reaffectationCount
     */
    public Integer getReaffectationCount() {
        return reaffectationCount;
    }

    /**
     * @param reaffectationCount
     *            the reaffectationCount to set
     */
    public void setReaffectationCount(Integer reaffectationCount) {
        this.reaffectationCount = reaffectationCount;
    }

    /**
     * @return the etapeSignatureAtteinte
     */
    public Boolean getEtapeSignatureAtteinte() {
        return etapeSignatureAtteinte;
    }

    /**
     * @param etapeSignatureAtteinte
     *            the etapeSignatureAtteinte to set
     */
    public void setEtapeSignatureAtteinte(Boolean etapeSignatureAtteinte) {
        this.etapeSignatureAtteinte = etapeSignatureAtteinte;
    }

    /**
     * @return the ministereAttributairePrecedent
     */
    public String getMinistereAttributairePrecedent() {
        return ministereAttributairePrecedent;
    }

    /**
     * @param ministereAttributairePrecedent
     *            the ministereAttributairePrecedent to set
     */
    public void setMinistereAttributairePrecedent(String ministereAttributairePrecedent) {
        this.ministereAttributairePrecedent = ministereAttributairePrecedent;
    }

    /**
     * @return the ministereReattribution
     */
    public String getMinistereReattribution() {
        return ministereReattribution;
    }

    /**
     * @param ministereReattribution
     *            the ministereReattribution to set
     */
    public void setMinistereReattribution(String ministereReattribution) {
        this.ministereReattribution = ministereReattribution;
    }

    /**
     * @return the labelEtapeSuivante
     */
    public String getLabelEtapeSuivante() {
        return labelEtapeSuivante;
    }

    /**
     * @param labelEtapeSuivante
     *            the labelEtapeSuivante to set
     */
    public void setLabelEtapeSuivante(String labelEtapeSuivante) {
        this.labelEtapeSuivante = labelEtapeSuivante;
    }

    /**
     * @return the historiqueDossierTraite
     */
    public Boolean getHistoriqueDossierTraite() {
        return historiqueDossierTraite;
    }

    /**
     * @param historiqueDossierTraite
     *            the historiqueDossierTraite to set
     */
    public void setHistoriqueDossierTraite(Boolean historiqueDossierTraite) {
        this.historiqueDossierTraite = historiqueDossierTraite;
    }

    /**
     * @return the isArbitrated
     */
    public Boolean getIsArbitrated() {
        return isArbitrated;
    }

    /**
     * @param isArbitrated
     *            the isArbitrated to set
     */
    public void setIsArbitrated(Boolean isArbitrated) {
        this.isArbitrated = isArbitrated;
    }

    /**
     * return the hasPJ
     */
    public Boolean getHasPJ() {
        return hasPJ;
    }

    /**
     * @param hasPJ
     */
    public void setHasPJ(Boolean hasPJ) {
        this.hasPJ = hasPJ;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * return the isRedemarre
     */
    public Boolean getIsRedemarre() {
        return isRedemarre;
    }

    /**
     * @param isRedemarre
     */
    public void setIsRedemarre(Boolean isRedemarre) {
        this.isRedemarre = isRedemarre;
    }
}
