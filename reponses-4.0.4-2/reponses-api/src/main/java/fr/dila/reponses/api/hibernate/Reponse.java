package fr.dila.reponses.api.hibernate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "REPONSE")
public class Reponse implements Serializable {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4296664425257688746L;

    @Id
    @Column(name = "ID", nullable = false, updatable = false, insertable = false)
    private String id;

    @Column(name = "PAGEJOREPONSE")
    private Integer pageJoReponse;

    @Column(name = "IDENTIFIANT")
    private Integer identifiant;

    @Column(name = "NUMEROJOREPONSE")
    private Integer numeroJoReponse;

    @Column(name = "AN_RUBRIQUE")
    private String anRubrique;

    @Column(name = "ISSIGNATUREVALIDE")
    private Boolean isSignatureValide;

    @Lob
    @Column(name = "SIGNATURE")
    private String signature;

    @Column(name = "IDAUTEURREPONSE")
    private String idAuteurReponse;

    @Column(name = "IDAUTEURREMOVESIGNATURE")
    private String idAuteurRemoveSignature;

    @Column(name = "VERROU")
    private String verrou;

    @Column(name = "DATEPUBLICATIONJOREPONSE")
    private Date datePublicationJoReponse;

    @Column(name = "DATEVALIDATION")
    private Date dateValidation;

    @Lob
    @Column(name = "ERRATUM")
    private String erratum;

    public Reponse() {
        super();
    }

    /**
     * @param id
     * @param pageJoReponse
     * @param identifiant
     * @param numeroJoReponse
     * @param anRubrique
     * @param isSignatureValide
     * @param signature
     * @param idAuteurReponse
     * @param idAuteurRemoveSignature
     * @param verrou
     * @param datePublicationJoReponse
     * @param dateValidation
     * @param erratum
     */
    public Reponse(
        String id,
        Integer pageJoReponse,
        Integer identifiant,
        Integer numeroJoReponse,
        String anRubrique,
        Boolean isSignatureValide,
        String signature,
        String idAuteurReponse,
        String idAuteurRemoveSignature,
        String verrou,
        Date datePublicationJoReponse,
        Date dateValidation,
        String erratum
    ) {
        super();
        this.id = id;
        this.pageJoReponse = pageJoReponse;
        this.identifiant = identifiant;
        this.numeroJoReponse = numeroJoReponse;
        this.anRubrique = anRubrique;
        this.isSignatureValide = isSignatureValide;
        this.signature = signature;
        this.idAuteurReponse = idAuteurReponse;
        this.idAuteurRemoveSignature = idAuteurRemoveSignature;
        this.verrou = verrou;
        this.datePublicationJoReponse = datePublicationJoReponse;
        this.dateValidation = dateValidation;
        this.erratum = erratum;
    }

    /**
     * @return the pageJoReponse
     */
    public Integer getPageJoReponse() {
        return pageJoReponse;
    }

    /**
     * @param pageJoReponse the pageJoReponse to set
     */
    public void setPageJoReponse(Integer pageJoReponse) {
        this.pageJoReponse = pageJoReponse;
    }

    /**
     * @return the identifiant
     */
    public Integer getIdentifiant() {
        return identifiant;
    }

    /**
     * @param identifiant the identifiant to set
     */
    public void setIdentifiant(Integer identifiant) {
        this.identifiant = identifiant;
    }

    /**
     * @return the numeroJoReponse
     */
    public Integer getNumeroJoReponse() {
        return numeroJoReponse;
    }

    /**
     * @param numeroJoReponse the numeroJoReponse to set
     */
    public void setNumeroJoReponse(Integer numeroJoReponse) {
        this.numeroJoReponse = numeroJoReponse;
    }

    /**
     * @return the anRubrique
     */
    public String getAnRubrique() {
        return anRubrique;
    }

    /**
     * @param anRubrique the anRubrique to set
     */
    public void setAnRubrique(String anRubrique) {
        this.anRubrique = anRubrique;
    }

    /**
     * @return the isSignatureValide
     */
    public Boolean getIsSignatureValide() {
        return isSignatureValide;
    }

    /**
     * @param isSignatureValide the isSignatureValide to set
     */
    public void setIsSignatureValide(Boolean isSignatureValide) {
        this.isSignatureValide = isSignatureValide;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the idAuteurReponse
     */
    public String getIdAuteurReponse() {
        return idAuteurReponse;
    }

    /**
     * @param idAuteurReponse the idAuteurReponse to set
     */
    public void setIdAuteurReponse(String idAuteurReponse) {
        this.idAuteurReponse = idAuteurReponse;
    }

    /**
     * @return the idAuteurRemoveSignature
     */
    public String getIdAuteurRemoveSignature() {
        return idAuteurRemoveSignature;
    }

    /**
     * @param idAuteurRemoveSignature the idAuteurRemoveSignature to set
     */
    public void setIdAuteurRemoveSignature(String idAuteurRemoveSignature) {
        this.idAuteurRemoveSignature = idAuteurRemoveSignature;
    }

    /**
     * @return the verrou
     */
    public String getVerrou() {
        return verrou;
    }

    /**
     * @param verrou the verrou to set
     */
    public void setVerrou(String verrou) {
        this.verrou = verrou;
    }

    /**
     * @return the datePublicationJoReponse
     */
    public Date getDatePublicationJoReponse() {
        return datePublicationJoReponse;
    }

    /**
     * @param datePublicationJoReponse the datePublicationJoReponse to set
     */
    public void setDatePublicationJoReponse(Date datePublicationJoReponse) {
        this.datePublicationJoReponse = datePublicationJoReponse;
    }

    /**
     * @return the dateValidation
     */
    public Date getDateValidation() {
        return dateValidation;
    }

    /**
     * @param dateValidation the dateValidation to set
     */
    public void setDateValidation(Date dateValidation) {
        this.dateValidation = dateValidation;
    }

    /**
     * @return the erratum
     */
    public String getErratum() {
        return erratum;
    }

    /**
     * @param erratum the erratum to set
     */
    public void setErratum(String erratum) {
        this.erratum = erratum;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
