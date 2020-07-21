/**
 * 
 */
package fr.dila.reponses.api.constant;

/**
 * @author jomez
 * 
 */
public class RequeteConstants {
    
    public static final String SANS_REPONSE_PUBLIEE = "SansReponse";
    
    public static final String AVEC_REPONSE_PUBLIEE = "AvecReponse";

    /** Constantes volet requete simple **/
    
    public static final String REQUETE_SIMPLE_SCHEMA = "requeteSimple";

    public static final String CRITERE_RECHERCHE = "critere_requete";
    
    public static final String CRITERE_RECHERCHE_CLAUSE =  "critere_requete_clause";
    
    /** Constantes volet requete avancé **/

    public static final String ORIGINE_QUESTION = "origineQuestion";
    
    public static final String REQUETE_COMPLEXE_SCHEMA = "requeteComplexe";

    public static final String DATE_JO_REPONSE_FIN = "dateJOReponseFin";
    
    public static final String DATE_JO_REPONSE_DEBUT = "dateJOReponseDebut";
    
    public static final String DATE_JO_QUESTION_FIN = "dateJOQuestionFin";
    
    public static final String DATE_JO_QUESTION_DEBUT = "dateJOQuestionDebut";
    
    public static final String DATE_SIGNALEMENT_FIN = "dateSignalementFin";
    
    public static final String DATE_SIGNALEMENT_DEBUT = "dateSignalementDebut";
    
    public static final String NUMERO_QUESTION_FIN = "numeroQuestionFin";
    
    public static final String NUMERO_QUESTION_DEBUT = "numeroQuestionDebut";
    
    public static final String GROUPE_POLITIQUE = "groupePolitique";
    
    public static final String NOM_AUTEUR = "nomAuteur";

    public static final String LEGISLATURE = "legislature";
    
    /**
     * Le ministère interpellé
     *
     **/
    public static final String MINISTERE_INTERPELLE = "ministereInterroge";
    
    public static final String MINISTERE_RATTACHEMENT = "ministereRattachement";
    
    public static final String DIRECTION_PILOTE = "directionPilote";
    
   
    /** Constantes volet requete index **/
    
    public static final String REQUETE_INDEX_SCHEMA = "requeteIndex";

    /** Constantes volet requete texte integral **/
    
    public static final String REQUETE_TEXTE_INTEGRAL_SCHEMA = "requeteTexteIntegral";
    
    public static final String CRITERE_RECHERCHE_TXT_INTEGRAL = "critere_requete";
    
    public static final String DANS_TEXTE_REPONSE = "dansTexteReponse";
    
    public static final String DANS_TITRE = "dansTitre";
    
    public static final String DANS_TEXTE_QUESTION = "dansTexteQuestion";
    
    public static final String APPLIQUER_RECHERCHE_EXACTE = "appliquerRechercheExacte";
    
    public static final String SUBCLAUSE = "subclause";

    /** Constantes volet requete metadonnees **/
    
    public static final String REQUETE_METADONNEES_SCHEMA = "requeteMetadonnees";

    public static final String CARACTERISTIQUE_QUESTION = "caracteristiqueQuestion";
    
    public static final String CLAUSE_CARACTERISTIQUE_QUESTION = "clauseCaracteristiques";

    public static final String TYPE_QUESTION = "typeQuestion";
    
    public static final String CLAUSE_ETAT_RETIRE_OU_NON_RETIRE = "clauseEtatRetireOuNonRetire";
    
    /** Les dossiers à l'état caduques **/
    public static final String DOSSIER_ETAT_CADUQUE = "etatCaduque";
    
    /** Les dossiers à l'état clos_autre **/
    public static final String DOSSIER_ETAT_CLOTURE_AUTRE = "etatClotureAutre";
    
    //** La liste des états de la question
    public static final String ETAT_QUESTION_LIST = "etatQuestion";
    
	/** Autres constantes **/
    public static final String DATE_FIN_ARBITRAIRE = "01/01/2050";
    
    public static final String DATE_DEBUT_ARBITRAIRE = "01/01/1950";

    public static final String FORMAT_DATE = "dd/MM/yyyy";
    
    
    /** Constante volet requête sur les étapes **/
    public static final String REQUETE_FDR_SCHEMA = "requeteFeuilleRoute";
    
    public static final String DIRECTION_ID = "directionId";
    
    public static final String POSTE_ID = "posteId";
    
    public static final String DISTRIBUTION_MAILBOX_ID = "distributionMailboxId";
    
    public static final String ETAPE_STATUT_ID = "statusStep";
    
    public static final String VALIDATION_STATUT_ID = "validationStatusId";
    
    public static final String TITRE_FDR = "titreFeuilleRoute";
    
    public static final String ID_FDR = "idFeuilleRoute";
    
    public static final String CURRENT_LIFE_CYCLE_STATE = "currentLifeCycleState";
    
    public static final String DATE_DEBUT_ETAPE_DEBUT_INTERVALLE = "rangeBeginDateStepStart";
    
    public static final String DATE_DEBUT_ETAPE_FIN_INTERVALLE = "rangeBeginDateStepEnd";
    
    public static final String DATE_FIN_ETAPE_DEBUT_INTERVALLE = "rangeEndDateStepStart";
    
    public static final String DATE_FIN_ETAPE_FIN_INTERVALLE = "rangeEndDateStepEnd";
    
    
    /** Constantes volet requete experte **/
    
    public static final String REQUETE_EXPERTE_DOCUMENT_TYPE = "RequeteExperte";
    
    /** Constantes pour les noms des query models utilisés dans la recherche **/
    
    public static final String PART_REQUETE_TEXTE_INTEGRAL = "requeteTexteIntegral";
    public static final String PART_REQUETE_FDR = "requeteFdr";
    public static final String PART_REQUETE_METADONNEE = "requeteMetadonnee";
    public static final String PART_REQUETE_INDEX_COMPL = "requeteIndexCompl";
    public static final String PART_REQUETE_INDEX_ORIGINE = "requeteIndex";
    // Query model inexistant, construit à partir des queries model PART_REQUETE_INDEX_COMPL et PART_REQUETE_INDEX_ORIGINE
    public static final String PART_REQUETE_INDEX_TOUS = "requeteIndexTous";
    public static final String PART_REQUETE_COMPLEXE = "requeteComplexe";
    public static final String PART_REQUETE_SIMPLE = "requeteSimple";
    
    /** Chemin vers les requêtes pré-paramétrées **/
    public static final String BIBLIO_REQUETES_ROOT = "/case-management/workspaces/admin/biblio-requetes-root/";
    
}
