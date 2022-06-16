package fr.dila.reponses.api.extraction;

import fr.dila.reponses.api.hibernate.Reponse;
import fr.dila.st.api.hibernate.Hierarchy;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "QUESTION")
public class Question implements Serializable {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4571224177158659136L;

    @Id
    @Column(name = "ID", nullable = false, updatable = false, insertable = false)
    private String id;

    @Column(name = "ETATQUESTION")
    private String etatQuestion;

    @Column(name = "INTITULEMINISTERE")
    private String intituleMinistere;

    @Column(name = "ETATRENOUVELE")
    private Boolean etatRenouvelé;

    @Column(name = "LEGISLATUREQUESTION")
    private Integer legislatureQuestion;

    @Column(name = "IDMANDAT")
    private String idMandat;

    //	@Column(name = "HASHCONNEXITETEXTE")
    //	private String hashConnexiteTexte;

    @Column(name = "DATECADUCITEQUESTION")
    private Date dateCaduciteQuestion;

    @Column(name = "ETATSIGNALE")
    private Boolean etatSignale;

    @Column(name = "NOMCOMPLETAUTEUR")
    private String nomCompletAuteur;

    @Column(name = "ORIGINEQUESTION")
    private String origineQuestion;

    @Column(name = "ETATRAPPELE")
    private Boolean etatRappele;

    @Column(name = "SOURCENUMEROQUESTION")
    private String sourceNumeroQuestion;

    @Column(name = "DATEPUBLICATIONJO")
    private Date datePublicationJO;

    @Column(name = "ETATNONRETIRE")
    private Boolean etatNonRetire;

    @Column(name = "ETATSQUESTION")
    private String etatsQuestion;

    @Column(name = "PAGEJO")
    private String pageJO;

    @Column(name = "ETATRETIRE")
    private Boolean etatRetire;

    @Column(name = "DATETRANSMISSIONASSEMBLEES")
    private Date dateTransmissionAssemblees;

    @Column(name = "DATERETRAITQUESTION")
    private Date dateRetraitQuestion;

    //	@Column(name = "TITREJOMINISTERE")
    //	private String titreJOMinistere;
    //
    //	@Column(name = "TEXTE_JOINT")
    //	private String texteJoint;

    @Column(name = "DATERENOUVELLEMENTQUESTION")
    private Date dateRenouvellementQuestion;

    @Column(name = "SENATQUESTIONTITRE")
    private String senatQuestionTitre;

    @Column(name = "IDMINISTEREINTERROGE")
    private String idMinistereInterroge;

    @Column(name = "DATERECEPTIONQUESTION")
    private Date dateReceptionQuestion;

    @Column(name = "CIVILITEAUTEUR")
    private String civiliteAuteur;

    @Column(name = "NOMAUTEUR")
    private String nomAuteur;

    @Column(name = "ETATREATTRIBUE")
    private Boolean etatReattribue;

    @Column(name = "PRENOMAUTEUR")
    private String prenomAuteur;

    @Column(name = "TYPEQUESTION")
    private String typeQuestion;

    @Column(name = "NUMEROQUESTION")
    private Integer numeroQuestion;

    @Column(name = "IDMINISTEREATTRIBUTAIRE")
    private String idMinistereAttributaire;

    @Column(name = "INTITULEMINISTEREATTRIBUTAIRE")
    private String intituleMinistereAttributaire;

    @Column(name = "DATESIGNALEMENTQUESTION")
    private Date dateSignalementQuestion;

    @Column(name = "DATECLOTUREQUESTION")
    private Date dateClotureQuestion;

    @Column(name = "DATERAPPELQUESTION")
    private Date dateRappelQuestion;

    @Column(name = "IDMINISTERERATTACHEMENT")
    private String idMinistereRattachement;

    @Column(name = "INTITULEMINISTERERATTACHEMENT")
    private String intituleMinistereRattachement;

    @Column(name = "IDDIRECTIONPILOTE")
    private String idDirectionPilote;

    @Column(name = "INTITULEDIRECTIONPILOTE")
    private String intituleDirectionPilote;

    @Transient
    private Boolean hasErratum = null;

    @Transient
    private Boolean hasRepErratum = null;

    public Question() {
        super();
    }

    public Question(
        String id,
        String etatQuestion,
        String intituleMinistere,
        Boolean etatRenouvelé,
        Integer legislatureQuestion,
        String idMandat,
        String hashConnexiteTexte,
        Date dateCaduciteQuestion,
        Boolean etatSignale,
        String nomCompletAuteur,
        String origineQuestion,
        Boolean etatRappele,
        String sourceNumeroQuestion,
        Date datePublicationJO,
        Boolean etatNonRetire,
        String etatsQuestion,
        String pageJO,
        Boolean etatRetire,
        Date dateTransmissionAssemblees,
        Date dateRetraitQuestion,
        String titreJOMinistere,
        String texteJoint,
        Date dateRenouvellementQuestion,
        String senatQuestionTitre,
        String idMinistereInterroge,
        String hashConnexiteAN,
        String circonscriptionAuteur,
        String groupePolitique,
        Boolean hasReponseInitiee,
        Date dateReceptionQuestion,
        String hashConnexiteSE,
        String texteQuestion,
        String motsCles,
        String civiliteAuteur,
        String nomAuteur,
        String hashConnexiteTitre,
        Boolean etatReattribue,
        String prenomAuteur,
        String typeQuestion,
        String caracteristiqueQuestion,
        Integer numeroQuestion,
        String idMinistereReattributaire,
        String intituleMinistereReattributaire,
        Date dateSignalementQuestion,
        Date dateClotureQuestion,
        Date dateRappelQuestion,
        String idMinistereRattachement,
        String intituleMinistereRattachement,
        String idDirectionPilote,
        String intituleDirectionPilote
    ) {
        super();
        this.id = id;
        this.etatQuestion = etatQuestion;
        this.intituleMinistere = intituleMinistere;
        this.etatRenouvelé = etatRenouvelé;
        this.legislatureQuestion = legislatureQuestion;
        this.idMandat = idMandat;
        //		this.hashConnexiteTexte = hashConnexiteTexte;
        this.dateCaduciteQuestion = dateCaduciteQuestion;
        this.etatSignale = etatSignale;
        this.nomCompletAuteur = nomCompletAuteur;
        this.origineQuestion = origineQuestion;
        this.etatRappele = etatRappele;
        this.sourceNumeroQuestion = sourceNumeroQuestion;
        this.datePublicationJO = datePublicationJO;
        this.etatNonRetire = etatNonRetire;
        this.etatsQuestion = etatsQuestion;
        this.pageJO = pageJO;
        this.etatRetire = etatRetire;
        this.dateTransmissionAssemblees = dateTransmissionAssemblees;
        this.dateRetraitQuestion = dateRetraitQuestion;
        //		this.titreJOMinistere = titreJOMinistere;
        //		this.texteJoint = texteJoint;
        this.dateRenouvellementQuestion = dateRenouvellementQuestion;
        this.senatQuestionTitre = senatQuestionTitre;
        this.idMinistereInterroge = idMinistereInterroge;
        //		this.hashConnexiteAN = hashConnexiteAN;
        //		this.circonscriptionAuteur = circonscriptionAuteur;
        //		this.groupePolitique = groupePolitique;
        //		this.hasReponseInitiee = hasReponseInitiee;
        this.dateReceptionQuestion = dateReceptionQuestion;
        //		this.hashConnexiteSE = hashConnexiteSE;
        //		this.texteQuestion = texteQuestion;
        //		this.motsCles = motsCles;
        this.civiliteAuteur = civiliteAuteur;
        this.nomAuteur = nomAuteur;
        //		this.hashConnexiteTitre = hashConnexiteTitre;
        this.etatReattribue = etatReattribue;
        this.prenomAuteur = prenomAuteur;
        this.typeQuestion = typeQuestion;
        //		this.caracteristiqueQuestion = caracteristiqueQuestion;
        this.numeroQuestion = numeroQuestion;
        this.idMinistereAttributaire = idMinistereReattributaire;
        this.intituleMinistereAttributaire = intituleMinistereReattributaire;
        this.dateSignalementQuestion = dateSignalementQuestion;
        this.dateClotureQuestion = dateClotureQuestion;
        this.dateRappelQuestion = dateRappelQuestion;
        this.idMinistereRattachement = idMinistereRattachement;
        this.intituleMinistereRattachement = intituleMinistereRattachement;
        this.idDirectionPilote = idDirectionPilote;
        this.intituleDirectionPilote = intituleDirectionPilote;
    }

    public Question(Question quest, Hierarchy hierarchy) {
        super();
        this.id = quest.id;
        this.etatQuestion = quest.etatQuestion;
        this.intituleMinistere = quest.intituleMinistere;
        this.etatRenouvelé = quest.etatRenouvelé;
        this.legislatureQuestion = quest.legislatureQuestion;
        this.idMandat = quest.idMandat;
        //		this.hashConnexiteTexte = quest.hashConnexiteTexte;
        this.dateCaduciteQuestion = quest.dateCaduciteQuestion;
        this.etatSignale = quest.etatSignale;
        this.nomCompletAuteur = quest.nomCompletAuteur;
        this.origineQuestion = quest.origineQuestion;
        this.etatRappele = quest.etatRappele;
        this.sourceNumeroQuestion = quest.sourceNumeroQuestion;
        this.datePublicationJO = quest.datePublicationJO;
        this.etatNonRetire = quest.etatNonRetire;
        this.etatsQuestion = quest.etatsQuestion;
        this.pageJO = quest.pageJO;
        this.etatRetire = quest.etatRetire;
        this.dateTransmissionAssemblees = quest.dateTransmissionAssemblees;
        this.dateRetraitQuestion = quest.dateRetraitQuestion;
        //		this.titreJOMinistere = quest.titreJOMinistere;
        //		this.texteJoint = quest.texteJoint;
        this.dateRenouvellementQuestion = quest.dateRenouvellementQuestion;
        this.senatQuestionTitre = quest.senatQuestionTitre;
        this.idMinistereInterroge = quest.idMinistereInterroge;
        //		this.hashConnexiteAN = quest.hashConnexiteAN;
        //		this.circonscriptionAuteur = quest.circonscriptionAuteur;
        //		this.groupePolitique = quest.groupePolitique;
        //		this.hasReponseInitiee = quest.hasReponseInitiee;
        this.dateReceptionQuestion = quest.dateReceptionQuestion;
        //		this.hashConnexiteSE = quest.hashConnexiteSE;
        //		this.texteQuestion = quest.texteQuestion;
        //		this.motsCles = quest.motsCles;
        this.civiliteAuteur = quest.civiliteAuteur;
        this.nomAuteur = quest.nomAuteur;
        //		this.hashConnexiteTitre = quest.hashConnexiteTitre;
        this.etatReattribue = quest.etatReattribue;
        this.prenomAuteur = quest.prenomAuteur;
        this.typeQuestion = quest.typeQuestion;
        //		this.caracteristiqueQuestion = quest.caracteristiqueQuestion;
        this.numeroQuestion = quest.numeroQuestion;
        this.idMinistereAttributaire = quest.idMinistereAttributaire;
        this.intituleMinistereAttributaire = quest.intituleMinistereAttributaire;
        this.dateSignalementQuestion = quest.dateSignalementQuestion;
        this.dateClotureQuestion = quest.dateClotureQuestion;
        this.dateRappelQuestion = quest.dateRappelQuestion;
        this.idMinistereRattachement = quest.idMinistereRattachement;
        this.intituleMinistereRattachement = quest.intituleMinistereRattachement;
        this.idDirectionPilote = quest.idDirectionPilote;
        this.intituleDirectionPilote = quest.intituleDirectionPilote;

        if (hierarchy.getId() == null) {
            this.hasErratum = false;
        } else {
            this.hasErratum = true;
        }
    }

    public Question(Question quest, Reponse reponse) {
        super();
        this.id = quest.id;
        this.etatQuestion = quest.etatQuestion;
        this.intituleMinistere = quest.intituleMinistere;
        this.etatRenouvelé = quest.etatRenouvelé;
        this.legislatureQuestion = quest.legislatureQuestion;
        this.idMandat = quest.idMandat;
        //		this.hashConnexiteTexte = quest.hashConnexiteTexte;
        this.dateCaduciteQuestion = quest.dateCaduciteQuestion;
        this.etatSignale = quest.etatSignale;
        this.nomCompletAuteur = quest.nomCompletAuteur;
        this.origineQuestion = quest.origineQuestion;
        this.etatRappele = quest.etatRappele;
        this.sourceNumeroQuestion = quest.sourceNumeroQuestion;
        this.datePublicationJO = quest.datePublicationJO;
        this.etatNonRetire = quest.etatNonRetire;
        this.etatsQuestion = quest.etatsQuestion;
        this.pageJO = quest.pageJO;
        this.etatRetire = quest.etatRetire;
        this.dateTransmissionAssemblees = quest.dateTransmissionAssemblees;
        this.dateRetraitQuestion = quest.dateRetraitQuestion;
        //		this.titreJOMinistere = quest.titreJOMinistere;
        //		this.texteJoint = quest.texteJoint;
        this.dateRenouvellementQuestion = quest.dateRenouvellementQuestion;
        this.senatQuestionTitre = quest.senatQuestionTitre;
        this.idMinistereInterroge = quest.idMinistereInterroge;
        //		this.hashConnexiteAN = quest.hashConnexiteAN;
        //		this.circonscriptionAuteur = quest.circonscriptionAuteur;
        //		this.groupePolitique = quest.groupePolitique;
        //		this.hasReponseInitiee = quest.hasReponseInitiee;
        this.dateReceptionQuestion = quest.dateReceptionQuestion;
        //		this.hashConnexiteSE = quest.hashConnexiteSE;
        //		this.texteQuestion = quest.texteQuestion;
        //		this.motsCles = quest.motsCles;
        this.civiliteAuteur = quest.civiliteAuteur;
        this.nomAuteur = quest.nomAuteur;
        //		this.hashConnexiteTitre = quest.hashConnexiteTitre;
        this.etatReattribue = quest.etatReattribue;
        this.prenomAuteur = quest.prenomAuteur;
        this.typeQuestion = quest.typeQuestion;
        //		this.caracteristiqueQuestion = quest.caracteristiqueQuestion;
        this.numeroQuestion = quest.numeroQuestion;
        this.idMinistereAttributaire = quest.idMinistereAttributaire;
        this.intituleMinistereAttributaire = quest.intituleMinistereAttributaire;
        this.dateSignalementQuestion = quest.dateSignalementQuestion;
        this.dateClotureQuestion = quest.dateClotureQuestion;
        this.dateRappelQuestion = quest.dateRappelQuestion;
        this.idMinistereRattachement = quest.idMinistereRattachement;
        this.intituleMinistereRattachement = quest.intituleMinistereRattachement;
        this.idDirectionPilote = quest.idDirectionPilote;
        this.intituleDirectionPilote = quest.intituleDirectionPilote;

        if (StringUtils.isBlank(reponse.getErratum())) {
            this.hasRepErratum = false;
        } else {
            this.hasRepErratum = true;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtatQuestion() {
        return etatQuestion;
    }

    public void setEtatQuestion(String etatQuestion) {
        this.etatQuestion = etatQuestion;
    }

    public String getIntituleMinistere() {
        return intituleMinistere;
    }

    public void setIntituleMinistere(String intituleMinistere) {
        this.intituleMinistere = intituleMinistere;
    }

    public Boolean isEtatRenouvelé() {
        return etatRenouvelé;
    }

    public void setEtatRenouvelé(Boolean etatRenouvelé) {
        this.etatRenouvelé = etatRenouvelé;
    }

    public Integer getLegislatureQuestion() {
        return legislatureQuestion;
    }

    public void setLegislatureQuestion(Integer legislatureQuestion) {
        this.legislatureQuestion = legislatureQuestion;
    }

    public String getIdMandat() {
        return idMandat;
    }

    public void setIdMandat(String idMandat) {
        this.idMandat = idMandat;
    }

    //	public String getHashConnexiteTexte() {
    //		return hashConnexiteTexte;
    //	}
    //
    //	public void setHashConnexiteTexte(String hashConnexiteTexte) {
    //		this.hashConnexiteTexte = hashConnexiteTexte;
    //	}

    public Date getDateCaduciteQuestion() {
        return dateCaduciteQuestion;
    }

    public void setDateCaduciteQuestion(Date dateCaduciteQuestion) {
        this.dateCaduciteQuestion = dateCaduciteQuestion;
    }

    public Boolean isEtatSignale() {
        return etatSignale;
    }

    public void setEtatSignale(Boolean etatSignale) {
        this.etatSignale = etatSignale;
    }

    public String getNomCompletAuteur() {
        return nomCompletAuteur;
    }

    public void setNomCompletAuteur(String nomCompletAuteur) {
        this.nomCompletAuteur = nomCompletAuteur;
    }

    public String getOrigineQuestion() {
        return origineQuestion;
    }

    public void setOrigineQuestion(String origineQuestion) {
        this.origineQuestion = origineQuestion;
    }

    public Boolean isEtatRappele() {
        return etatRappele;
    }

    public void setEtatRappele(Boolean etatRappele) {
        this.etatRappele = etatRappele;
    }

    public String getSourceNumeroQuestion() {
        return sourceNumeroQuestion;
    }

    public void setSourceNumeroQuestion(String sourceNumeroQuestion) {
        this.sourceNumeroQuestion = sourceNumeroQuestion;
    }

    public Date getDatePublicationJO() {
        return datePublicationJO;
    }

    public void setDatePublicationJO(Date datePublicationJO) {
        this.datePublicationJO = datePublicationJO;
    }

    public Boolean isEtatNonRetire() {
        return etatNonRetire;
    }

    public void setEtatNonRetire(Boolean etatNonRetire) {
        this.etatNonRetire = etatNonRetire;
    }

    public String getEtatsQuestion() {
        return etatsQuestion;
    }

    public void setEtatsQuestion(String etatsQuestion) {
        this.etatsQuestion = etatsQuestion;
    }

    public String getPageJO() {
        return pageJO;
    }

    public void setPageJO(String pageJO) {
        this.pageJO = pageJO;
    }

    public Boolean isEtatRetire() {
        return etatRetire;
    }

    public void setEtatRetire(Boolean etatRetire) {
        this.etatRetire = etatRetire;
    }

    public Date getDateTransmissionAssemblees() {
        return dateTransmissionAssemblees;
    }

    public void setDateTransmissionAssemblees(Date dateTransmissionAssemblees) {
        this.dateTransmissionAssemblees = dateTransmissionAssemblees;
    }

    public Date getDateRetraitQuestion() {
        return dateRetraitQuestion;
    }

    public void setDateRetraitQuestion(Date dateRetraitQuestion) {
        this.dateRetraitQuestion = dateRetraitQuestion;
    }

    //	public String getTitreJOMinistere() {
    //		return titreJOMinistere;
    //	}
    //
    //	public void setTitreJOMinistere(String titreJOMinistere) {
    //		this.titreJOMinistere = titreJOMinistere;
    //	}
    //
    //	public String getTexteJoint() {
    //		return texteJoint;
    //	}
    //
    //	public void setTexteJoint(String texteJoint) {
    //		this.texteJoint = texteJoint;
    //	}

    public Date getDateRenouvellementQuestion() {
        return dateRenouvellementQuestion;
    }

    public void setDateRenouvellementQuestion(Date dateRenouvellementQuestion) {
        this.dateRenouvellementQuestion = dateRenouvellementQuestion;
    }

    public String getSenatQuestionTitre() {
        return senatQuestionTitre;
    }

    public void setSenatQuestionTitre(String senatQuestionTitre) {
        this.senatQuestionTitre = senatQuestionTitre;
    }

    public String getIdMinistereInterroge() {
        return idMinistereInterroge;
    }

    public void setIdMinistereInterroge(String idMinistereInterroge) {
        this.idMinistereInterroge = idMinistereInterroge;
    }

    //	public String getHashConnexiteAN() {
    //		return hashConnexiteAN;
    //	}
    //
    //	public void setHashConnexiteAN(String hashConnexiteAN) {
    //		this.hashConnexiteAN = hashConnexiteAN;
    //	}
    //
    //	public String getCirconscriptionAuteur() {
    //		return circonscriptionAuteur;
    //	}
    //
    //	public void setCirconscriptionAuteur(String circonscriptionAuteur) {
    //		this.circonscriptionAuteur = circonscriptionAuteur;
    //	}
    //
    //	public String getGroupePolitique() {
    //		return groupePolitique;
    //	}
    //
    //	public void setGroupePolitique(String groupePolitique) {
    //		this.groupePolitique = groupePolitique;
    //	}
    //
    //	public Boolean isHasReponseInitiee() {
    //		return hasReponseInitiee;
    //	}
    //
    //	public void setHasReponseInitiee(Boolean hasReponseInitiee) {
    //		this.hasReponseInitiee = hasReponseInitiee;
    //	}

    public Date getDateReceptionQuestion() {
        return dateReceptionQuestion;
    }

    public void setDateReceptionQuestion(Date dateReceptionQuestion) {
        this.dateReceptionQuestion = dateReceptionQuestion;
    }

    //
    //	public String getHashConnexiteSE() {
    //		return hashConnexiteSE;
    //	}
    //
    //	public void setHashConnexiteSE(String hashConnexiteSE) {
    //		this.hashConnexiteSE = hashConnexiteSE;
    //	}

    //	public String getTexteQuestion() {
    //		return texteQuestion;
    //	}
    //
    //	public void setTexteQuestion(String texteQuestion) {
    //		this.texteQuestion = texteQuestion;
    //	}

    //	public String getMotsCles() {
    //		return motsCles;
    //	}
    //
    //	public void setMotsCles(String motsCles) {
    //		this.motsCles = motsCles;
    //	}

    public String getCiviliteAuteur() {
        return civiliteAuteur;
    }

    public void setCiviliteAuteur(String civiliteAuteur) {
        this.civiliteAuteur = civiliteAuteur;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }

    //	public String getHashConnexiteTitre() {
    //		return hashConnexiteTitre;
    //	}
    //
    //	public void setHashConnexiteTitre(String hashConnexiteTitre) {
    //		this.hashConnexiteTitre = hashConnexiteTitre;
    //	}

    public Boolean isEtatReattribue() {
        return etatReattribue;
    }

    public void setEtatReattribue(Boolean etatReattribue) {
        this.etatReattribue = etatReattribue;
    }

    public String getPrenomAuteur() {
        return prenomAuteur;
    }

    public void setPrenomAuteur(String prenomAuteur) {
        this.prenomAuteur = prenomAuteur;
    }

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    //	public String getCaracteristiqueQuestion() {
    //		return caracteristiqueQuestion;
    //	}
    //
    //	public void setCaracteristiqueQuestion(String caracteristiqueQuestion) {
    //		this.caracteristiqueQuestion = caracteristiqueQuestion;
    //	}

    public Integer getNumeroQuestion() {
        return numeroQuestion;
    }

    public void setNumeroQuestion(Integer numeroQuestion) {
        this.numeroQuestion = numeroQuestion;
    }

    public String getIdMinistereAttributaire() {
        return idMinistereAttributaire;
    }

    public void setIdMinistereAttributaire(String idMinistereAttributaire) {
        this.idMinistereAttributaire = idMinistereAttributaire;
    }

    public String getIntituleMinistereAttributaire() {
        return intituleMinistereAttributaire;
    }

    public void setIntituleMinistereAttributaire(String intituleMinistereAttributaire) {
        this.intituleMinistereAttributaire = intituleMinistereAttributaire;
    }

    public Date getDateSignalementQuestion() {
        return dateSignalementQuestion;
    }

    public void setDateSignalementQuestion(Date dateSignalementQuestion) {
        this.dateSignalementQuestion = dateSignalementQuestion;
    }

    public Date getDateClotureQuestion() {
        return dateClotureQuestion;
    }

    public void setDateClotureQuestion(Date dateClotureQuestion) {
        this.dateClotureQuestion = dateClotureQuestion;
    }

    public Date getDateRappelQuestion() {
        return dateRappelQuestion;
    }

    public void setDateRappelQuestion(Date dateRappelQuestion) {
        this.dateRappelQuestion = dateRappelQuestion;
    }

    public String getIdMinistereRattachement() {
        return idMinistereRattachement;
    }

    public void setIdMinistereRattachement(String idMinistereRattachement) {
        this.idMinistereRattachement = idMinistereRattachement;
    }

    public String getIntituleMinistereRattachement() {
        return intituleMinistereRattachement;
    }

    public void setIntituleMinistereRattachement(String intituleMinistereRattachement) {
        this.intituleMinistereRattachement = intituleMinistereRattachement;
    }

    public String getIdDirectionPilote() {
        return idDirectionPilote;
    }

    public void setIdDirectionPilote(String idDirectionPilote) {
        this.idDirectionPilote = idDirectionPilote;
    }

    public String getIntituleDirectionPilote() {
        return intituleDirectionPilote;
    }

    public void setIntituleDirectionPilote(String intituleDirectionPilote) {
        this.intituleDirectionPilote = intituleDirectionPilote;
    }

    public Boolean hasErratum() {
        return this.hasErratum;
    }

    public void setHasErratum(Boolean hasErratum) {
        this.hasErratum = hasErratum;
    }

    public Boolean hasRepErratum() {
        return this.hasRepErratum;
    }

    public void setHasRepErratum(Boolean hasRepErratum) {
        this.hasRepErratum = hasRepErratum;
    }
}
