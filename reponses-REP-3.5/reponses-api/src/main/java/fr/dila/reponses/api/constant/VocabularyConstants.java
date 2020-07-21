package fr.dila.reponses.api.constant;

import java.util.HashMap;

/**
 * Constantes des vocabulaires de Réponses.
 * 
 * @author jgomez
 * @author jtremeaux
 */
public class VocabularyConstants {

    // *************************************************************
    // Vocabulaires
    // *************************************************************
    
	public static final String AN_ANALYSE = "AN_analyse";
	public static final String TA_RUBRIQUE = "TA_rubrique";
	public static final String AN_RUBRIQUE = "AN_rubrique";
	public static final String SE_RUBRIQUE = "SE_rubrique";
	public static final String SE_RENVOI = "SE_renvoi";
	public static final String SE_THEME = "SE_theme";
	public static final String MOTSCLEF_MINISTERE = "motclef_ministere";
	public static final String GROUPE_POLITIQUE = "groupe_politique";
	public static final String TYPE_QUESTION = "type_question";
	public static final String ETAT_QUESTION = "etat_question";
	public static final String ORIGINE_QUESTION = "origine_question";
	public static final String CARACTERISTIQUE_QUESTION = "caracteristique_question";
	public static final String VERROU = "verrou";
	public static final String NIVEAU_VISIBILITE = "niveau_visibilite";
	public static final String LEGISLATURE = "legislature";
    public static final String INDEXATION_ZONE_AN = "AN";
    public static final String INDEXATION_ZONE_SENAT = "Senat";
    public static final String INDEXATION_ZONE_MINISTERE = "Ministere";
    
    // *************************************************************
    // Etat/Delai Question
    // *************************************************************
    public static final String ETAT_DOSSIER_SIGNALE = "signalé";
    public static final String ETAT_DOSSIER_RENOUVELE = "renouvelé";
    public static final String ETAT_DOSSIER_RAPPELE = "rappelé";
    public static final String DELAI_DOSSIER_REPONDUE = "reponse";
    public static final String DELAI_DOSSIER_RETIRE = "Retirée";
    public static final String DELAI_DOSSIER_CADUQUE = "caduque";
    public static final String DELAI_DOSSIER_CLOS = "clos";
    
    // *************************************************************
    // Origine Question : id Origine
    // *************************************************************
    public static final String QUESTION_ORIGINE_SENAT = "SENAT";
    
    public static final String QUESTION_ORIGINE_AN = "AN";
        
        
    // *************************************************************
    // Etat Question : type D'etat
    // *************************************************************    

    public static final String ETAT_QUESTION_SIGNALEE = "signalee";
        
    public static final String ETAT_QUESTION_RENOUVELEE = "renouvelee";

    public static final String ETAT_QUESTION_RETIREE = "retiree";
    
    public static final String ETAT_QUESTION_CADUQUE = "caduque";
    
    public static final String ETAT_QUESTION_REATTRIBUEEE = "reattribuee";
    
    public static final String ETAT_QUESTION_EN_COURS = "en cours";
    
    public static final String ETAT_QUESTION_REPONDU = "repondu";
    
    public static final String ETAT_QUESTION_CLOTURE_AUTRE = "cloture_autre";
    
    public static final String ETAT_QUESTION_RAPPELE = "rappele";
    
    // *************************************************************
    //Type Question
    // *************************************************************    
    
    public static final String QUESTION_TYPE_QE = "QE";
    
    public static final String QUESTION_TYPE_QOSD = "QOSD";
    
    public static final String QUESTION_TYPE_QOAD = "QOAD";
    
    public static final String QUESTION_TYPE_QOAE = "QOAE";
    
    public static final String QUESTION_TYPE_QG = "QG";
    
    public static final String QUESTION_TYPE_QC = "QC";
        
    // *************************************************************
    // Feuilles de route : types d'étapes
    // *************************************************************
    /**
     * Type d'étape de feuille de route "Pour rédaction".
     */
    public static final String ROUTING_TASK_TYPE_REDACTION = "1";

    /**
     * Type d'étape de feuille de route "Pour attribution".
     */
    public static final String ROUTING_TASK_TYPE_ATTRIBUTION = "2";

    /**
     * Type d'étape de feuille de route "Pour VISA".
     */
    public static final String ROUTING_TASK_TYPE_VISA = "3";

    /**
     * Type d'étape de feuille de route "Pour signature".
     */
    public static final String ROUTING_TASK_TYPE_SIGNATURE = "4";

    /**
     * Type d'étape de feuille de route "Pour information".
     */
    public static final String ROUTING_TASK_TYPE_INFORMATION = "5";

    /**
     * Type d'étape de feuille de route "Pour avis".
     */
    public static final String ROUTING_TASK_TYPE_AVIS = "6";

    /**
     * Type d'étape de feuille de route "Pour validation PM".
     */
    public static final String ROUTING_TASK_TYPE_VALIDATION_PM = "7";

    /**
     * Type d'étape de feuille de route "Pour réattribution".
     */
    public static final String ROUTING_TASK_TYPE_REATTRIBUTION = "8";

    /**
     * Type d'étape de feuille de route "Pour réorientation".
     */
    public static final String ROUTING_TASK_TYPE_REORIENTATION = "9";

    /**
     * Type d'étape de feuille de route "Pour impression".
     */
    public static final String ROUTING_TASK_TYPE_IMPRESSION = "10";

    /**
     * Type d'étape de feuille de route "Pour transmission assemblée".
     */
    public static final String ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE = "11";

    /**
     * Type d'étape de feuille de route "Pour arbitrage".
     */
    public static final String ROUTING_TASK_TYPE_ARBITRAGE = "12";

    /**
     * Type d'étape de feuille de route "Pour rédaction interfacée".
     */
    public static final String ROUTING_TASK_TYPE_REDACTION_INTERFACEE = "13";

    /**
     * Type d'étape de feuille de route "Pour actualisation".
     */
    public static final String ROUTING_TASK_TYPE_ACTUALISATION = "14";
    
    /**
     * Type d'étape de feuille de route "Pour correction".
     */
    public static final String ROUTING_TASK_TYPE_CORRECTION = "15";
    
    /**
     * Type d'étape de feuille de route "Pour attente".
     */
    public static final String ROUTING_TASK_TYPE_ATTENTE = "16";
    
    /**
     * Type d'étape de feuille de route "Pour retour".
     */
    public static final String ROUTING_TASK_TYPE_RETOUR = "17";
    
    /**
     * Type d'étape de feuille de route "Pour retour validation PM".
     */
    public static final String ROUTING_TASK_TYPE_RETOUR_VALIDATION_PM = "18";
    
    // *************************************************************
    // Feuilles de route : libellé d'étapes
    // *************************************************************
    
    /**
     * libellé d'étape de feuille de route "Pour rédaction".
     */
    public static final String LIBELLE_ROUTING_TASK_REDACTION = "Pour rédaction";
    
    /**
     * libellé d'étape de feuille de route "Pour attribution".
     */
    public static final String LIBELLE_ROUTING_TASK_ATTRIBUTION = "Pour attribution";
    
    /**
     * libellé d'étape de feuille de route "Pour VISA".
     */
    public static final String LIBELLE_ROUTING_TASK_VISA = "Pour VISA";
    
    /**
     * libellé d'étape de feuille de route "Pour signature".
     */
    public static final String LIBELLE_ROUTING_TASK_SIGNATURE = "Pour signature";
    
    /**
     * libellé d'étape de feuille de route "Pour information".
     */
    public static final String LIBELLE_ROUTING_TASK_INFORMATION = "Pour information";
    
    /**
     * libellé d'étape de feuille de route "Pour avis".
     */
    public static final String LIBELLE_ROUTING_TASK_AVIS = "Pour avis";
    
    /**
     * libellé d'étape de feuille de route "Pour validation PM".
     */
    public static final String LIBELLE_ROUTING_TASK_VALIDATION_PM = "Pour validation PM";
    
    /**
     * libellé d'étape de feuille de route "Pour réattribution".
     */
    public static final String LIBELLE_ROUTING_TASK_REATTRIBUTION = "Pour réattribution";
    
    /**
     * libellé d'étape de feuille de route "Pour réorientation".
     */
    public static final String LIBELLE_ROUTING_TASK_REORIENTATION = "Pour réorientation";
    
    /**
     * libellé d'étape de feuille de route "Pour impression".
     */
    public static final String LIBELLE_ROUTING_TASK_IMPRESSION = "Pour impression";
    
    /**
     * libellé d'étape de feuille de route "Pour transmission assemblée".
     */
    public static final String LIBELLE_ROUTING_TASK_TRANSMISSION_ASSEMBLEE = "Pour transmission assemblée";
    
    /**
     * libellé d'étape de feuille de route "Pour arbitrage".
     */
    public static final String LIBELLE_ROUTING_TASK_ARBITRAGE = "Pour arbitrage";
    
    /**
     * libellé d'étape de feuille de route "Pour rédaction interfacée".
     */
    public static final String LIBELLE_ROUTING_TASK_REDACTION_INTERFACEE = "Pour rédaction interfacée";
    
    /**
     * libellé d'étape de feuille de route "Pour actualisation".
     */
    public static final String LIBELLE_ROUTING_TASK_ACTUALISATION = "Pour actualisation";
    
    /**
     * libellé d'étape de feuille de route "Pour correction".
     */
    public static final String LIBELLE_ROUTING_TASK_CORRECTION = "Pour correction";
    
    /**
     * libellé d'étape de feuille de route "Pour attente".
     */
    public static final String LIBELLE_ROUTING_TASK_ATTENTE = "Pour attente";
    
    /**
     * libellé d'étape de feuille de route "Pour retour".
     */
    public static final String LIBELLE_ROUTING_TASK_RETOUR = "Pour retour";
    
    /**
     * libellé d'étape de feuille de route "Pour retour validation PM".
     */
    public static final String LIBELLE_ROUTING_TASK_RETOUR_VALIDATION_PM = "Pour retour validation PM";
    
    /**
	 * List des libellé des étape de feuille de route
	 */
	public static final HashMap<String, String>	LIST_LIBELLE_ROUTING_TASK_PAR_ID	= new HashMap<String, String>();
	static {
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_REDACTION, LIBELLE_ROUTING_TASK_REDACTION); 							// statut 1
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_ATTRIBUTION, LIBELLE_ROUTING_TASK_ATTRIBUTION);						// statut 2
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_VISA, LIBELLE_ROUTING_TASK_VISA);									// statut 3
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_SIGNATURE, LIBELLE_ROUTING_TASK_SIGNATURE);							// statut 4
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_INFORMATION, LIBELLE_ROUTING_TASK_INFORMATION);						// statut 5
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_AVIS, LIBELLE_ROUTING_TASK_AVIS);									// statut 6
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_VALIDATION_PM, LIBELLE_ROUTING_TASK_VALIDATION_PM);					// statut 7
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_REATTRIBUTION, LIBELLE_ROUTING_TASK_REATTRIBUTION);					// statut 8
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_REORIENTATION, LIBELLE_ROUTING_TASK_REORIENTATION);					// statut 9
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_IMPRESSION, LIBELLE_ROUTING_TASK_IMPRESSION);						// statut 10
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE, LIBELLE_ROUTING_TASK_TRANSMISSION_ASSEMBLEE);// statut 11
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_ARBITRAGE, LIBELLE_ROUTING_TASK_ARBITRAGE);							// statut 12
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_REDACTION_INTERFACEE, LIBELLE_ROUTING_TASK_REDACTION_INTERFACEE);	// statut 13
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_ACTUALISATION, LIBELLE_ROUTING_TASK_ACTUALISATION);					// statut 14
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_CORRECTION, LIBELLE_ROUTING_TASK_CORRECTION);						// statut 15
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_ATTENTE, LIBELLE_ROUTING_TASK_ATTENTE);								// statut 16
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_RETOUR, LIBELLE_ROUTING_TASK_RETOUR);								// statut 17
		LIST_LIBELLE_ROUTING_TASK_PAR_ID.put(ROUTING_TASK_TYPE_RETOUR_VALIDATION_PM, LIBELLE_ROUTING_TASK_RETOUR_VALIDATION_PM);	// statut 18
	}
    
    // *************************************************************
    // Vocabulaire booléen
    // *************************************************************
    /**
     * Vocabulaire booléen.
     */
    public static final String BOOLEAN_VOCABULARY = "boolean_voc";

    /**
     * Vocabulaire booléen : valeur vraie.
     */
    public static final String BOOLEAN_TRUE = "TRUE";

    /**
     * Vocabulaire booléen : valeur fausse.
     */
    public static final String BOOLEAN_FALSE = "FALSE";
}
