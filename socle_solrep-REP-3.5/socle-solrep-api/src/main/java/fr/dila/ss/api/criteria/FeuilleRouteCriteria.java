package fr.dila.ss.api.criteria;

import java.util.Calendar;

import fr.dila.st.api.dao.criteria.BaseCriteria;

/**
 * Critère de recherche des feuilles de routes.
 * 
 * @author jtremeaux
 */
public class FeuilleRouteCriteria extends BaseCriteria {
    // *************************************************************
    // Critères de recherche sur les feuilles de route
    // *************************************************************
    /**
     * Vérifie la permission de voir les feuilles de route.
     */
    private boolean checkReadPermission;
    
    /**
     * Identifiant technique de l'utilisateur de création.
     */
    private String creationUtilisateur;
    
    /**
     * Date de création min.
     */
    private Calendar creationDateMin;
    
    /**
     * Date de création min.
     */
    private Calendar creationDateMax;
    
    /**
     * Feuille de route par défaut.
     */
    private boolean feuilleRouteDefaut;
    
    /**
     * Ministère.
     */
    private String ministere;
    
    /**
     * Ministère non défini.
     */
    private boolean ministereNull;
    
    /**
     * Direction.
     */
    private String direction;

    /**
     * Direction non définie.
     */
    private boolean directionNull;
    
    /**
     * Intitulé.
     */
    private String intitule;

    /**
     * Intitulé (like).
     */
    private String intituleLike;

    /**
     * Intitulé non défini.
     */
    private boolean intituleNull;

    // *************************************************************
    // Critères de recherche sur les étapes de feuilles de route
    // *************************************************************
    /**
     * Identifiant technique du type d'étape.
     */
    private String routingTaskType;

    /**
     * Identifiant technique de la Mailbox de distribution.
     */
    private String distributionMailboxId;

    /**
     * Date d'échéance.
     */
    private Long deadline;

    /**
     * Validation automatique de l'étape.
     */
    private Boolean automaticValidation;
    
    /**
     * Étape obligatoire SGG.
     */
    private Boolean obligatoireSGG;
    
    /**
     * Étape obligatoire ministère.
     */
    private Boolean obligatoireMinistere;

    /**
     * Default constructor
     */
    public FeuilleRouteCriteria(){
    	super();
    }
    
    /**
     * Getter de checkReadPermission.
     *
     * @return checkReadPermission
     */
    public boolean isCheckReadPermission() {
        return checkReadPermission;
    }

    /**
     * Setter de checkReadPermission.
     *
     * @param checkReadPermission checkReadPermission
     */
    public void setCheckReadPermission(boolean checkReadPermission) {
        this.checkReadPermission = checkReadPermission;
    }

    /**
     * Getter de creationUtilisateur.
     *
     * @return creationUtilisateur
     */
    public String getCreationUtilisateur() {
        return creationUtilisateur;
    }

    /**
     * Setter de creationUtilisateur.
     *
     * @param creationUtilisateur creationUtilisateur
     */
    public void setCreationUtilisateur(String creationUtilisateur) {
        this.creationUtilisateur = creationUtilisateur;
    }

    /**
     * Getter de creationDateMin.
     *
     * @return creationDateMin
     */
    public Calendar getCreationDateMin() {
        return creationDateMin;
    }

    /**
     * Setter de creationDateMin.
     *
     * @param creationDateMin creationDateMin
     */
    public void setCreationDateMin(Calendar creationDateMin) {
        this.creationDateMin = creationDateMin;
    }

    /**
     * Getter de creationDateMax.
     *
     * @return creationDateMax
     */
    public Calendar getCreationDateMax() {
        return creationDateMax;
    }

    /**
     * Setter de creationDateMax.
     *
     * @param creationDateMax creationDateMax
     */
    public void setCreationDateMax(Calendar creationDateMax) {
        this.creationDateMax = creationDateMax;
    }

    /**
     * Getter de feuilleRouteDefaut.
     *
     * @return feuilleRouteDefaut
     */
    public boolean isFeuilleRouteDefaut() {
        return feuilleRouteDefaut;
    }

    /**
     * Setter de feuilleRouteDefaut.
     *
     * @param feuilleRouteDefaut feuilleRouteDefaut
     */
    public void setFeuilleRouteDefaut(boolean feuilleRouteDefaut) {
        this.feuilleRouteDefaut = feuilleRouteDefaut;
    }

    /**
     * Getter de ministere.
     *
     * @return ministere
     */
    public String getMinistere() {
        return ministere;
    }

    /**
     * Setter de ministere.
     *
     * @param ministere ministere
     */
    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    /**
     * Getter de ministereNull.
     *
     * @return ministereNull
     */
    public boolean getMinistereNull() {
        return ministereNull;
    }

    /**
     * Setter de ministereNull.
     *
     * @param ministereNull ministereNull
     */
    public void setMinistereNull(boolean ministereNull) {
        this.ministereNull = ministereNull;
    }

    /**
     * Getter de direction.
     *
     * @return direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Setter de direction.
     *
     * @param direction direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Getter de directionNull.
     *
     * @return directionNull
     */
    public boolean getDirectionNull() {
        return directionNull;
    }

    /**
     * Setter de directionNull.
     *
     * @param directionNull directionNull
     */
    public void setDirectionNull(boolean directionNull) {
        this.directionNull = directionNull;
    }

    /**
     * Getter de intitule.
     *
     * @return intitule
     */
    public String getIntitule() {
        return intitule;
    }

    /**
     * Setter de intitule.
     *
     * @param intitule intitule
     */
    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    /**
     * Getter de intituleLike.
     *
     * @return intituleLike
     */
    public String getIntituleLike() {
        return intituleLike;
    }

    /**
     * Setter de intituleLike.
     *
     * @param intituleLike intituleLike
     */
    public void setIntituleLike(String intituleLike) {
        this.intituleLike = intituleLike;
    }

    /**
     * Getter de intituleNull.
     *
     * @return intituleNull
     */
    public boolean getIntituleNull() {
        return intituleNull;
    }

    /**
     * Setter de intituleNull.
     *
     * @param intituleNull intituleNull
     */
    public void setIntituleNull(boolean intituleNull) {
        this.intituleNull = intituleNull;
    }

    /**
     * Getter de routingTaskType.
     *
     * @return routingTaskType
     */
    public String getRoutingTaskType() {
        return routingTaskType;
    }

    /**
     * Setter de routingTaskType.
     *
     * @param routingTaskType routingTaskType
     */
    public void setRoutingTaskType(String routingTaskType) {
        this.routingTaskType = routingTaskType;
    }

    /**
     * Getter de distributionMailboxId.
     *
     * @return distributionMailboxId
     */
    public String getDistributionMailboxId() {
        return distributionMailboxId;
    }

    /**
     * Setter de distributionMailboxId.
     *
     * @param distributionMailboxId distributionMailboxId
     */
    public void setDistributionMailboxId(String distributionMailboxId) {
        this.distributionMailboxId = distributionMailboxId;
    }

    /**
     * Getter de deadline.
     *
     * @return deadline
     */
    public Long getDeadline() {
        return deadline;
    }

    /**
     * Setter de deadline.
     *
     * @param deadline deadline
     */
    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    /**
     * Getter de automaticValidation.
     *
     * @return automaticValidation
     */
    public Boolean getAutomaticValidation() {
        return automaticValidation;
    }

    /**
     * Setter de automaticValidation.
     *
     * @param automaticValidation automaticValidation
     */
    public void setAutomaticValidation(Boolean automaticValidation) {
        this.automaticValidation = automaticValidation;
    }

    /**
     * Getter de obligatoireSGG.
     *
     * @return obligatoireSGG
     */
    public Boolean getObligatoireSGG() {
        return obligatoireSGG;
    }

    /**
     * Setter de obligatoireSGG.
     *
     * @param obligatoireSGG obligatoireSGG
     */
    public void setObligatoireSGG(Boolean obligatoireSGG) {
        this.obligatoireSGG = obligatoireSGG;
    }

    /**
     * Getter de obligatoireMinistere.
     *
     * @return obligatoireMinistere
     */
    public Boolean getObligatoireMinistere() {
        return obligatoireMinistere;
    }

    /**
     * Setter de obligatoireMinistere.
     *
     * @param obligatoireMinistere obligatoireMinistere
     */
    public void setObligatoireMinistere(Boolean obligatoireMinistere) {
        this.obligatoireMinistere = obligatoireMinistere;
    }
}
