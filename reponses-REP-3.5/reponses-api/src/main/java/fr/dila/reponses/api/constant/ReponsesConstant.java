package fr.dila.reponses.api.constant;

/**
 * Constantes de l'application Réponses.
 * 
 * @author jtremeaux
 */
public class ReponsesConstant {
    // *************************************************************
    // Listes de selection
    // *************************************************************
    /**
     * Liste de selection des corbeilles
     */
    public static final String CORBEILLE_SELECTION = "CORBEILLE_SELECTION";
    
    /**
     * Liste de selection du plan de classement
     */
    public static final String PLAN_CLASSEMENT_SELECTION = "PLAN_CLASSEMENT_SELECTION";
    
    /**
     * Liste de selection d'une liste d'élimination
     */
    public static final String LISTE_ELIMINATION_SELECTION = "LISTE_ELIMINATION_SELECTION";
    
    /**
     * Liste de selection de la recherche
     */
    public static final String RECHERCHE_SELECTION = "CURRENT_SELECTION";
    
    // *************************************************************
    // Corbeilles
    // *************************************************************

    /**
     * menu corbeille
     */
    public static final String LEFT_MENU_CORBEILLE_ACTION = "LEFT_MENU_CORBEILLE";
    
    /**
     * menh plan de classement
     */
    public static final String LEFT_MENU_PLAN_CLASSEMENT_ACTION = "LEFT_MENU_PLAN_CLASSEMENT";
    
    // *************************************************************
    // Feuilles de route
    // *************************************************************
    /**
     * Propriété du schéma feuille de route : Titre de la question.
     */
    public static final String REPONSES_FEUILLE_ROUTE_TITRE_QUESTION_PROPERTY_NAME = "titreQuestion";
    
    // *************************************************************
    // Feuilles de route
    // *************************************************************
    /**
     * Propriété du schéma feuille de route : id Direction Pilote.
     */
    public static final String REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME = "idDirectionPilote";
    
    // *************************************************************
    // Feuilles de route
    // *************************************************************
    /**
     * Propriété du schéma feuille de route : intitule Direction Pilote.
     */
    public static final String REPONSES_FEUILLE_ROUTE_INTITULE_DIRECTION_PILOTE_PROPERTY_NAME = "intituleDirectionPilote";

    // *************************************************************
    // Recherche
    // *************************************************************
    /**
     * Type d'une recherche
     */
    public static final String RECHERCHE_DOCUMENT_TYPE = "RequeteComposite";
    public static final String RECHERCHE_CONTENT_VIEW = "requeteComposite";

    // *************************************************************
    // Fond de Dossier
    // *************************************************************
    /**
     * Nombre maximum de fichier uploadable dans la popup d'ajout de document dans le fond de dossier
     */
    public static final int MAX_FILE_UPLOAD_FOND_DOSSIER = Integer.MAX_VALUE;
    
   
    //TODO utiliser fichier de conf pour permettre e changer dynamiqement ce paramètre
    /**
     * Type de fichier autorisé pour le téléchargement dans le fond de dossier.
     */
    public static final String ALLOWED_UPLOAD_FILE_TYPE = "jpg, png, odt, pdf, rtf, ods, odp, vsd, doc, xls, ppt, docx, xlsx, pptx, zip";

    /**
     * Taille maximum autorisée pour le numéro de la question.
     */
    public static final int DOSSIER_QUESTION_NUMBER_MAX_LENGTH = 15;

    // *************************************************************
    // Journal
    // *************************************************************
    /**
     * Filtre sur les docType
     */
    public static final String FILTER_DOCTYPE = "docType";
    /**
     * Filtre sur les actions
     */
    public static final String FILTER_ACTIONS = "eventId";
    /**
     * Filtre sur le nom des utilisateurs
     */
    public static final String FILTER_USER = "principalName";
    
    // *************************************************************
    // Reponses Mailbox
    // *************************************************************
    /**
     * Type de document Mailbox Réponses.
     */
    public static final String REPONSES_MAILBOX_TYPE = "ReponsesMailbox";
    
    /**
     * Schéma Mailbox Réponses.
     */
    public static final String REPONSES_MAILBOX_SCHEMA = "reponsesMailbox";
    
    /**
     * Proprieté preComptageListe du schema reponsesMailbox
     */
    public static final String REPONSES_MAILBOX_PRECOMPTAGES_PROPERTY = "preComptageListe";
    
    /**
     * id du ministere
     */
    public static final String REPONSES_MAILBOX_PRECOMPTAGE_MINISTERE_PROPERTY = "ministereId";

    /**
     * type d'etapes
     */
    public static final String REPONSES_MAILBOX_PRECOMPTAGE_ROUTINGTASK_PROPERTY = "routingTaskType";
    
    /**
     * nombre de dossier link dans la mailbox associé à un ministere et a un type d'etape
     */
    public static final String REPONSES_MAILBOX_PRECOMPTAGE_COUNT_PROPERTY = "count";

    
    
    // *************************************************************
    // Recherche
    // *************************************************************
    
    /**
     * menu favoris
     */
    public static final String LEFT_MENU_FAVORIS_DOSSIER_ACTION = "LEFT_MENU_FAVORIS_DOSSIER";
    
    /**
     * Menu de gauche recherche
     */
    public static final String LEFT_MENU_RECHERCHE_CATEGORY = "LEFT_MENU_RECHERCHE";
    
    // *************************************************************
    // User
    // *************************************************************
    
    /**
     * Nom du schema de l'objet User
     */
    public static final String TAB_SELECTED_DEFAULT= "espace_corbeille";
    
    // *************************************************************
    // Legislature
    // *************************************************************
    
    /**
     * Nom du schema legislature
     */
    public static final String LEGISLATURE_SCHEMA= "vocabularyLegislature";
    
    /**
     * Nom de la propriété id du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_ID= "id";
    
    /**
     * Nom de la propriété label du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_LABEL= "label";
    
    /**
     * Nom de la propriété obsolete du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_OBSOLETE= "obsolete";
    
    /**
     * Nom de la propriété ordering du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_ORDERING= "ordering";
    /**
     * Nom de la propriété dateDebut du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_DATEDEBUT= "dateDebut";
    
    /**
     * Nom de la propriété dateFin du schéma vocabulaireLegislature
     */
    public static final String LEGISLATURE_DATEFIN= "dateFin";
    
    
    /**
     * Nom du repertoire qui contient les rapports BIRT
     */
    public static final String BIRT_REPORT_DIR = "birtReports";

    public static final String BIRT_REPORT_FICHE_DOSSIER = "birtReports/fiche_dossier.rptdesign";

    public static final String BIRT_REPORT_TABLEAU_BORD = "birtReports/tableau_bord.rptdesign";
    
    public static final String BIRT_REPORT_LISTE_ELIMINATION = "birtReports/elimination.rptdesign";
    
    public static final String BIRT_REPORT_FICHE_DOSSIER_PARAM_ID = "idQuestion";
    
    public static final String BIRT_REPORT_EXPORT_REPONSE_PARAM_ID = "exportReponse";
}
