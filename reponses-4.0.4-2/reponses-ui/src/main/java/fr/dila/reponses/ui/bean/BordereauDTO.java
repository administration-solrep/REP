package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.annot.NxProp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BordereauDTO {
    @NxProp(xpath = "qu:numeroQuestion", docType = "Question")
    private Long numeroQuestion;

    @NxProp(xpath = "qu:origineQuestion", docType = "Question")
    private String origineQuestion;

    @NxProp(xpath = "qu:civiliteAuteur", docType = "Question")
    private String civiliteAuteur;

    @NxProp(xpath = "qu:nomAuteur", docType = "Question")
    private String nomAuteur;

    @NxProp(xpath = "qu:prenomAuteur", docType = "Question")
    private String prenomAuteur;

    @NxProp(xpath = "qu:groupePolitique", docType = "Question")
    private String groupePolitique;

    @NxProp(xpath = "qu:circonscriptionAuteur", docType = "Question")
    private String circonscriptionAuteur;

    @NxProp(xpath = "qu:typeQuestion", docType = "Question")
    private String typeQuestion;

    @NxProp(xpath = "dos:ministereAttributaireCourant", docType = "Dossier")
    private String ministereAttributaireCourant;

    @NxProp(xpath = "qu:datePublicationJO", docType = "Question")
    private Calendar datePublicationJO;

    @NxProp(xpath = "qu:pageJO", docType = "Question")
    private String pageJO;

    @NxProp(xpath = "qu:senatQuestionTitre", docType = "Question")
    private String senatQuestionTitre;

    @NxProp(xpath = "qu:intituleMinistere", docType = "Question")
    private String intituleMinistere;

    @NxProp(xpath = "qu:idMinistereRattachement", docType = "Question")
    private String ministereRattachement;

    @NxProp(xpath = "qu:intituleMinistereRattachement", docType = "Question")
    private String intituleMinistereRattachement;

    private Map<String, String> mapMinistereRattachement = new HashMap<>();

    @NxProp(xpath = "qu:idDirectionPilote", docType = "Question")
    private String directionPilote;

    @NxProp(xpath = "qu:intituleDirectionPilote", docType = "Question")
    private String intituleDirectionPilote;

    @NxProp(xpath = "qu:dateTransmissionAssemblees", docType = "Question")
    private Calendar dateTransmissionAssemblees;

    @NxProp(xpath = "qu:dateSignalementQuestion", docType = "Question")
    private Calendar dateSignalementQuestion;

    @NxProp(xpath = "qu:dateRetraitQuestion", docType = "Question")
    private Calendar dateRetraitQuestion;

    @NxProp(xpath = "qu:dateCaduciteQuestion", docType = "Question")
    private Calendar dateCaduciteQuestion;

    @NxProp(xpath = "qu:dateRappelQuestion", docType = "Question")
    private Calendar dateRappelQuestion;

    @NxProp(xpath = "rep:datePublicationJOReponse", docType = "Reponse")
    private Calendar datePublicationJOReponse;

    @NxProp(xpath = "rep:pageJOReponse", docType = "Reponse")
    private Long pageJOReponse;

    @NxProp(xpath = "qu:renouvellementsQuestion", docType = "Question")
    private List<Calendar> renouvellements;

    private List<HistoriqueAttributionDTO> historiquesAttributionDto;

    private Set<String> listingUnitesStruct;

    private Calendar tacheCoursDate;

    private String tacheCoursPoste;

    private Long tacheCoursDeadline;

    private Boolean tacheCoursAuto;

    private String tacheFinalePoste;

    private IndexationDTO indexationDTO;

    private IndexationDTO currentIndexationDTO;

    private Calendar dateAttendueReponseSignalement = null;

    private Boolean partMiseAJour = Boolean.FALSE;
    private Boolean partReponse = Boolean.FALSE;
    private Boolean partIndexationAN = Boolean.FALSE;
    private Boolean partIndexationSENAT = Boolean.FALSE;
    private Boolean partEditableIndexationComplementaire = Boolean.FALSE;
    private Boolean partIndexationComplementaireAN = Boolean.FALSE;
    private Boolean partIndexationComplementaireSE = Boolean.FALSE;
    private Boolean partIndexationComplementaireMotCle = Boolean.FALSE;
    private Boolean partFeuilleRoute = Boolean.FALSE;
    private Boolean partEditMinistereRattachement = Boolean.FALSE;
    private Boolean partEditDirectionPilote = Boolean.FALSE;
    private Boolean isQuestionTypeEcrite = Boolean.FALSE;
    private Boolean isEdit = Boolean.FALSE;

    public BordereauDTO() {}

    public Long getNumeroQuestion() {
        return numeroQuestion;
    }

    public void setNumeroQuestion(Long numeroQuestion) {
        this.numeroQuestion = numeroQuestion;
    }

    public String getOrigineQuestion() {
        return origineQuestion;
    }

    public void setOrigineQuestion(String origineQuestion) {
        this.origineQuestion = origineQuestion;
    }

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

    public String getPrenomAuteur() {
        return prenomAuteur;
    }

    public void setPrenomAuteur(String prenomAuteur) {
        this.prenomAuteur = prenomAuteur;
    }

    public String getGroupePolitique() {
        return groupePolitique;
    }

    public void setGroupePolitique(String groupePolitique) {
        this.groupePolitique = groupePolitique;
    }

    public String getCirconscriptionAuteur() {
        return circonscriptionAuteur;
    }

    public void setCirconscriptionAuteur(String circonscriptionAuteur) {
        this.circonscriptionAuteur = circonscriptionAuteur;
    }

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public Calendar getDatePublicationJO() {
        return datePublicationJO;
    }

    public void setDatePublicationJO(Calendar datePublicationJO) {
        this.datePublicationJO = datePublicationJO;
    }

    public String getPageJO() {
        return pageJO;
    }

    public void setPageJO(String pageJO) {
        this.pageJO = pageJO;
    }

    public String getSenatQuestionTitre() {
        return senatQuestionTitre;
    }

    public void setSenatQuestionTitre(String senatQuestionTitre) {
        this.senatQuestionTitre = senatQuestionTitre;
    }

    public String getIntituleMinistere() {
        return intituleMinistere;
    }

    public void setIntituleMinistere(String intituleMinistere) {
        this.intituleMinistere = intituleMinistere;
    }

    public String getMinistereRattachement() {
        return ministereRattachement;
    }

    public void setMinistereRattachement(String ministereRattachement) {
        this.ministereRattachement = ministereRattachement;
    }

    public String getIntituleMinistereRattachement() {
        return intituleMinistereRattachement;
    }

    public void setIntituleMinistereRattachement(String intituleMinistereRattachement) {
        this.intituleMinistereRattachement = intituleMinistereRattachement;
    }

    public String getDirectionPilote() {
        return directionPilote;
    }

    public void setDirectionPilote(String directionPilote) {
        this.directionPilote = directionPilote;
    }

    public String getIntituleDirectionPilote() {
        return intituleDirectionPilote;
    }

    public void setIntituleDirectionPilote(String intituleDirectionPilote) {
        this.intituleDirectionPilote = intituleDirectionPilote;
    }

    public String getMinistereAttributaireCourant() {
        return ministereAttributaireCourant;
    }

    public void setMinistereAttributaireCourant(String ministereAttributaireCourant) {
        this.ministereAttributaireCourant = ministereAttributaireCourant;
    }

    public String getAuteur() {
        return civiliteAuteur + " " + nomAuteur + " " + prenomAuteur;
    }

    public Calendar getDateTransmissionAssemblees() {
        return dateTransmissionAssemblees;
    }

    public Calendar getDateRetraitQuestion() {
        return dateRetraitQuestion;
    }

    public void setDateRetraitQuestion(Calendar dateRetraitQuestion) {
        this.dateRetraitQuestion = dateRetraitQuestion;
    }

    public Calendar getDateCaduciteQuestion() {
        return dateCaduciteQuestion;
    }

    public void setDateCaduciteQuestion(Calendar dateCaduciteQuestion) {
        this.dateCaduciteQuestion = dateCaduciteQuestion;
    }

    public Calendar getDateRappelQuestion() {
        return dateRappelQuestion;
    }

    public void setDateRappelQuestion(Calendar dateRappelQuestion) {
        this.dateRappelQuestion = dateRappelQuestion;
    }

    public Calendar getDatePublicationJOReponse() {
        return datePublicationJOReponse;
    }

    public void setDatePublicationJOReponse(Calendar datePublicationJOReponse) {
        this.datePublicationJOReponse = datePublicationJOReponse;
    }

    public Long getPageJOReponse() {
        return pageJOReponse;
    }

    public void setPageJOReponse(Long pageJOReponse) {
        this.pageJOReponse = pageJOReponse;
    }

    public List<Calendar> getRenouvellements() {
        return renouvellements;
    }

    public void setRenouvellements(List<Calendar> renouvellements) {
        this.renouvellements = renouvellements;
    }

    public List<HistoriqueAttributionDTO> getHistoriquesAttributionDto() {
        return historiquesAttributionDto;
    }

    public void setHistoriquesAttributionDto(List<HistoriqueAttributionDTO> historiquesAttributionDto) {
        this.historiquesAttributionDto = historiquesAttributionDto;
    }

    public Set<String> getListingUnitesStruct() {
        return listingUnitesStruct;
    }

    public void setListingUnitesStruct(Set<String> listingUnitesStruct) {
        this.listingUnitesStruct = listingUnitesStruct;
    }

    public Calendar getTacheCoursDate() {
        return tacheCoursDate;
    }

    public void setTacheCoursDate(Calendar tacheCoursDate) {
        this.tacheCoursDate = tacheCoursDate;
    }

    public String getTacheCoursPoste() {
        return tacheCoursPoste;
    }

    public void setTacheCoursPoste(String tacheCoursPoste) {
        this.tacheCoursPoste = tacheCoursPoste;
    }

    public Long getTacheCoursDeadline() {
        return tacheCoursDeadline;
    }

    public void setTacheCoursDeadline(Long tacheCoursDeadline) {
        this.tacheCoursDeadline = tacheCoursDeadline;
    }

    public Boolean getTacheCoursAuto() {
        return tacheCoursAuto;
    }

    public void setTacheCoursAuto(Boolean tacheCoursAuto) {
        this.tacheCoursAuto = tacheCoursAuto;
    }

    public String getTacheFinalePoste() {
        return tacheFinalePoste;
    }

    public void setTacheFinalePoste(String tacheFinalePoste) {
        this.tacheFinalePoste = tacheFinalePoste;
    }

    public void setDateTransmissionAssemblees(Calendar dateTransmissionAssemblees) {
        this.dateTransmissionAssemblees = dateTransmissionAssemblees;
    }

    public Calendar getDateSignalementQuestion() {
        return dateSignalementQuestion;
    }

    public void setDateSignalementQuestion(Calendar dateSignalementQuestion) {
        this.dateSignalementQuestion = dateSignalementQuestion;
    }

    public IndexationDTO getIndexationDTO() {
        return indexationDTO;
    }

    public void setIndexationDTO(IndexationDTO indexationDTO) {
        this.indexationDTO = indexationDTO;
    }

    public Calendar getDateAttendueReponseSignalement() {
        return dateAttendueReponseSignalement;
    }

    public void setDateAttendueReponseSignalement(Calendar dateAttendueSignalement) {
        this.dateAttendueReponseSignalement = dateAttendueSignalement;
    }

    public IndexationDTO getCurrentIndexationDTO() {
        return currentIndexationDTO;
    }

    public void setCurrentIndexationDTO(IndexationDTO currentIndexationDTO) {
        this.currentIndexationDTO = currentIndexationDTO;
    }

    public Boolean getPartMiseAJour() {
        return partMiseAJour;
    }

    public void setPartMiseAJour(Boolean partMiseAJour) {
        this.partMiseAJour = partMiseAJour;
    }

    public Boolean getPartReponse() {
        return partReponse;
    }

    public void setPartReponse(Boolean partReponse) {
        this.partReponse = partReponse;
    }

    public Boolean getPartIndexationAN() {
        return partIndexationAN;
    }

    public void setPartIndexationAN(Boolean partIndexationAN) {
        this.partIndexationAN = partIndexationAN;
    }

    public Boolean getPartIndexationSENAT() {
        return partIndexationSENAT;
    }

    public void setPartIndexationSENAT(Boolean partIndexationSENAT) {
        this.partIndexationSENAT = partIndexationSENAT;
    }

    public Boolean getPartEditableIndexationComplementaire() {
        return partEditableIndexationComplementaire;
    }

    public void setPartEditableIndexationComplementaire(Boolean partEditableIndexationComplementaire) {
        this.partEditableIndexationComplementaire = partEditableIndexationComplementaire;
    }

    public Boolean getPartIndexationComplementaireAN() {
        return partIndexationComplementaireAN;
    }

    public void setPartIndexationComplementaireAN(Boolean partIndexationComplementaireAN) {
        this.partIndexationComplementaireAN = partIndexationComplementaireAN;
    }

    public Boolean getPartIndexationComplementaireSE() {
        return partIndexationComplementaireSE;
    }

    public void setPartIndexationComplementaireSE(Boolean partIndexationComplementaireSE) {
        this.partIndexationComplementaireSE = partIndexationComplementaireSE;
    }

    public Boolean getPartIndexationComplementaireMotCle() {
        return partIndexationComplementaireMotCle;
    }

    public void setPartIndexationComplementaireMotCle(Boolean partIndexationComplementaireMotCle) {
        this.partIndexationComplementaireMotCle = partIndexationComplementaireMotCle;
    }

    public Boolean getPartFeuilleRoute() {
        return partFeuilleRoute;
    }

    public void setPartFeuilleRoute(Boolean partFeuilleRoute) {
        this.partFeuilleRoute = partFeuilleRoute;
    }

    public Boolean getPartEditMinistereRattachement() {
        return partEditMinistereRattachement;
    }

    public void setPartEditMinistereRattachement(Boolean partEditMinistereRattachement) {
        this.partEditMinistereRattachement = partEditMinistereRattachement;
    }

    public Boolean getPartEditDirectionPilote() {
        return partEditDirectionPilote;
    }

    public void setPartEditDirectionPilote(Boolean partEditDirectionPilote) {
        this.partEditDirectionPilote = partEditDirectionPilote;
    }

    public Boolean isQuestionTypeEcrite() {
        return isQuestionTypeEcrite;
    }

    public void setIsQuestionTypeEcrite(Boolean isQuestionTypeEcrite) {
        this.isQuestionTypeEcrite = isQuestionTypeEcrite;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public Map<String, String> getMapMinistereRattachement() {
        return mapMinistereRattachement;
    }

    public void setMapMinistereRattachement(Map<String, String> mapMinistereRattachement) {
        this.mapMinistereRattachement = mapMinistereRattachement;
    }
}
