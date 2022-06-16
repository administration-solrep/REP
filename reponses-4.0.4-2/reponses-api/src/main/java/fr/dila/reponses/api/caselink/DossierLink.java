package fr.dila.reponses.api.caselink;

import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.st.api.caselink.STDossierLink;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * DossierLink Interface (herit CaseLink Interface)
 *
 * @author arolin
 */
public interface DossierLink extends STDossierLink, Serializable, DossierCommon {
    /**
     *
     * Gets the Question number (9536).
     */
    Long getNumeroQuestion();

    void setNumeroQuestion(Long numeroQuestion);

    /**
     * Gets the source numero question field (AN-9536).
     */
    String getSourceNumeroQuestion();

    void setSourceNumeroQuestion(String sortField);

    /**
     * Gets the sort field.
     */
    String getSortField();

    void setSortField(String sortField);

    /**
     * Gets the Question Type (QE,QO).
     */
    String getTypeQuestion();

    void setTypeQuestion(String typeQuestion);

    /**
     * Gets the Question date publication JO.
     */
    Calendar getDatePublicationJO();

    void setDatePublicationJO(Calendar datePublicationJO);

    /**
     * Gets the Question nom complet auteur.
     */
    String getNomCompletAuteur();

    void setNomCompletAuteur(String nomCompletAuteur);

    ///////////////////
    // Ministere info
    //////////////////

    /**
     * Gets the Question id Ministere Attributaire.
     */
    String getIdMinistereAttributaire();

    void setIdMinistereAttributaire(String idMinistereAttributaire);

    /**
     * Gets the Question intitule Ministere.
     */
    String getIntituleMinistere();

    void setIntituleMinistere(String intituleMinistere);

    // *************************************************************
    // Étapes de feuille de route
    // *************************************************************
    /**
     * Retourne l'identifiant technique de l'étape en cours.
     *
     * @return Identifiant technique de l'étape en cours
     */
    @Override
    String getRoutingTaskId();

    /**
     * Renseigne l'identifiant technique de l'étape en cours.
     *
     * @param Identifiant technique de l'étape en cours
     */
    @Override
    void setRoutingTaskId(String routingTaskId);

    /**
     * Retourne le type d'étape en cours.
     *
     * @return Type d'étape en cours
     */
    @Override
    String getRoutingTaskType();

    /**
     * Renseigne le type d'étape en cours.
     *
     * @param routingTaskType  Type d'étape en cours
     */
    @Override
    void setRoutingTaskType(String routingTaskType);

    /**
     * Retourne le libellé de l'étape en cours.
     *
     * @return Libellé de l'étape en cours
     */
    @Override
    String getRoutingTaskLabel();

    /**
     * Renseigne le libellé de l'étape en cours.
     *
     * @param Libellé de l'étape en cours
     */
    @Override
    void setRoutingTaskLabel(String routingTaskLabel);

    /**
     * Retourne le libellé de la Mailbox de distribution (champ dénormalisé).
     *
     * @return Libellé de la Mailbox de distribution
     */
    @Override
    String getRoutingTaskMailboxLabel();

    /**
     * Renseigne le libellé de la Mailbox de distribution (champ dénormalisé).
     *
     * @param Libellé de la Mailbox de distribution
     */
    @Override
    void setRoutingTaskMailboxLabel(String routingTaskMailboxLabel);

    ///////////////////
    // Etape Courante Feuille de route
    //////////////////
    String getEtatsQuestion();
    void setEtatsQuestion(String etatsQuestion);
    void setEtatsQuestion(Boolean isUrgent, Boolean isSignale, Boolean isRenouvelle);

    String getMotsCles();
    void setMotsCles(String motsCles);

    Calendar getDateSignalementQuestion();
    void setDateSignalementQuestion(Calendar dateSignalementQuestion);

    void setEtatQuestionSimple(String etatQuestion);

    String getEtatQuestionSimple();

    /**
     * test si le dossier est a l'etat urgent
     * Pioche l'info dans etatQuestion
     * @return
     */
    Boolean isUrgent();

    /**
     * test si le dossier est a l'etat signale
     * Pioche l'info dans etatQuestion
     * @return
     */
    Boolean isSignale();

    /**
     * test si le dossier est a l'etat renouvelle
     * Pioche l'info dans etatQuestion
     * @return
     */
    Boolean isRenouvelle();

    List<String> getInitialActionInternalParticipant();

    void setInitialActionInternalParticipant(List<String> initialActionInternamParticipant);

    List<String> getAllActionParticipant();

    void setAllActionParticipant(List<String> allActionParticipant);

    public static DossierLink adapt(DocumentModel doc) {
        return doc.getAdapter(DossierLink.class);
    }
}
