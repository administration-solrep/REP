package fr.dila.reponses.api.recherche;

import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.st.api.recherche.RequeteTraitement;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author JGZ
 * Requete : une requête est l'objet qui contient les données pour effectuer une recherche.
 */

public interface Requete extends Serializable {
    DocumentModel getDocument();

    void setDocument(DocumentModel doc);

    /**
     * Ajoute une entrée à une des listes gérant les vocabulaires associés à cette requête
     *
     * @param voc
     * @param label
     */
    void addVocEntry(String voc, String label);

    /**
     * enlève une entrée à une des listes gérant les vocabulaires associés à cette requête
     *
     * @param voc
     * @param label
     */
    void removeVocEntry(String voc, String label);

    List<String[]> getListIndexByZone(String zone);

    /** Getter/setter **/

    Calendar getDateQuestionDebut();

    Calendar getDateQuestionFin();

    Calendar getDateSignalementDebut();

    Calendar getDateSignalementFin();

    void setNumeroRange(int debut, int fin);

    void setDateRange(Calendar dateDebut, Calendar dateFin);

    void setDateRangeReponse(Calendar debut, Calendar fin);

    void setGroupePolitique(String groupePolitique);

    String getGroupePolitique();

    void setChampRequeteSimple(String critereRecherche);

    void setOrigineQuestion(List<String> stringList);

    void setCaracteristiqueQuestion(List<String> caracteristiqueList);

    void setTypeQuestion(List<String> types);

    void setDansTexteQuestion(Boolean value);

    void setDansTitre(Boolean value);

    void setDansTexteReponse(Boolean value);

    void setCritereRechercheIntegral(String critere);

    String getSubClause();

    void setLegislature(String legislature);

    String getLegislature();

    String getNomAuteur();

    void setNomAuteur(String nomAuteur);

    void setDateQuestionDebut(Calendar dateDebut);

    void setDateQuestionFin(Calendar dateFin);

    void setDateSignalementDebut(Calendar dateDebut);

    void setDateSignalementFin(Calendar dateFin);

    Calendar getDateReponseDebut();

    void setDateReponseDebut(Calendar dateDebut);

    void setDateReponseFin(Calendar dateFin);

    void setNumeroQuestionDebut(int debut);

    void setNumeroQuestionFin(int fin);

    void setSubClause(String clause);

    String getCritereRechercheIntegral();

    Boolean getDansTexteReponse();

    Boolean getDansTitre();

    Boolean getDansTexteQuestion();

    /**
     * Mettre à vrai pour effectuer une recherche de manière exact
     *
     * @param rechercheExacte
     */
    void setAppliquerRechercheExacte(Boolean rechercheExacte);

    /**
     * Retourne vrai si la recherche exacte est appliquée,
     * faux sinon
     *
     * @return
     */
    Boolean getAppliquerRechercheExacte();

    String getQueryType();

    void setQueryType(String type);

    void setEtat(String propertyEtat, Boolean value);

    Boolean getEtat(String propertyEtat);

    String getChampRequeteSimple();

    String getClauseChampRequeteSimple();

    void setClauseChampRequeteSimple(String clause);

    /**
     * Donne la valeur du mode d'indexation pour cette requête :
     * - INDEX_ORIG pour l'indexation d'origine
     * - INDEX_COMPL pour l'indexation complémentaire
     * - TOUS pour les 2 modes d'indexation
     *
     * @return le mode d'indexation
     */
    IndexModeEnum getIndexationMode();

    /**
     * Positionne la valeur du mode d'indexation
     *
     * @return
     */
    void setIndexationMode(IndexModeEnum indexationMode);

    void setMinistereInterroge(String ministereInterroge);

    String getMinistereInterroge();

    void setMinistereRattachement(String ministereRattachement);

    String getMinistereRattachement();

    void setDirectionPilote(String directionPilote);

    String getDirectionPilote();

    /**
     * Retourne vrai si une des recherches du texte intégral est selectionnée
     *
     * @return
     */
    Boolean hasTextRechercheSelected();

    /**
     * Met en place le fragment OR pour l'état retiré ou non retiré
     *
     * @param clause
     */
    void setClauseEtatRetireOuNonRetire(String clause);

    /**
     * Retourne le la clause qui donne la conjonction des états rétirés et non retirés
     *
     * @return
     */
    String getClauseEtatRetireOuNonRetire();

    RequeteTraitement<Requete> getTraitement();

    void setTraitement(RequeteTraitement<Requete> traitement);

    void init();

    void doBeforeQuery();

    /**
     * Met en place la recherche des dossiers caduques
     *
     * @param etatCaduque
     */
    void setEtatCaduque(Boolean etatCaduque);

    /**
     * Retourne vrai si la recherche des dossiers clos est en place
     */
    Boolean getEtatCaduque();

    /**
     * Met en place la recherche des dossiers en état cloture autre
     *
     * @param etatClotureAutre
     */
    void setEtatClotureAutre(Boolean etatClotureAutre);

    /**
     * Retourne vrai si la recherche des dossiers cloture autre est en place
     */
    Boolean getEtatClotureAutre();

    /**
     * Met en place la liste des états de la question sur lesquelle la recherche porte
     *
     * @param etats
     */
    void setEtatQuestionList(List<String> etats);

    /***
     * Retourne la liste des états de la question sur lesquelle la recherche porte
     * @return
     */
    List<String> getEtatQuestionList();

    /**
     * Retourne la liste des valeurs cochées par l'utilisateur concernant les réponses publiées ou non.
     *
     * @return
     */
    List<String> getCaracteristiqueQuestion();

    /**
     * Retourne la clause concernant les réponses publiées ou non
     *
     * @return
     */
    String getClauseCaracteristiques();

    /**
     * Met en place le paramêtre clause comme clause des réponses publiées ou non.
     *
     * @param clause
     * @return
     */
    void setClauseCaracteristiques(String clause);

    /**
     * Place l'identifiant de la mailbox à rechercher sur les étapes
     *
     * @param mailboxIdFromPoste
     */
    void setEtapeDistributionMailboxId(String mailboxIdFromPoste);

    /**
     * Retourne l'identificant de la direction à rechercher sur les étapes/
     *
     * @return
     */
    String getEtapeIdDirection();

    /**
     * Retourne l'identificant de poste à rechercher sur les étapes/
     *
     * @return
     */
    String getEtapeIdPoste();

    /**
     * Retourne le statut d'une étape.
     * Attention ! Il s'agit du statut de recherche, qui regroupe des informations relatives à l'identifiant de validation de l'étape et le cycle de vie d'une étape.
     *
     * @return
     */
    String getEtapeIdStatut();

    /**
     * Recherche sur le cycle de vie d'une étape (en cours, à venir, terminé)
     *
     * @param state
     */
    void setEtapeCurrentCycleState(String state);

    /**
     * Recherche sur le statut de validation d'une étape. (pour le cycle de vie à validated : savoir si la sortie est non concerné, validé automatiquement, etc ...)
     *
     * @param validationStatutId
     */
    void setValidationStatutId(String validationStatutId);

    /**
     * Positionne le titre de la feuille de route pour effectuer une recherche sur ce champ sur les instances de feuille de route.
     *
     * @param fdrTitre
     */
    void setTitreFeuilleRoute(String fdrTitre);

    /**
     * Recherche sur la date de début d'étape - intervalle de départ
     */
    void setEtapeDateActivation(Calendar date);

    /**
     * Retourne la date de début d'étape - intervalle de départ
     */
    Calendar getEtapeDateActivation();

    /**
     * Recherche sur la date de début d'étape - intervalle de fin
     */
    void setEtapeDateActivation_2(Calendar date);

    /**
     * Retourne la date de début d'étape - intervalle de fin
     */
    Calendar getEtapeDateActivation_2();

    /**
     * Recherche sur la date de fin d'étape - intervalle de départ
     */
    void setEtapeDateValidation(Calendar date);

    /**
     * Retourne la date de fin d'étape - intervalle de départ
     */
    Calendar getEtapeDateValidation();

    /**
     * Recherche sur la date de fin d'étape - intervalle de fin
     */
    void setEtapeDateValidation_2(Calendar date);

    /**
     * Retourne la date de fin d'étape - intervalle de fin
     */
    Calendar getEtapeDateValidation_2();
}
