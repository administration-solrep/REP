package fr.dila.reponses.api.constant;

/**
 * Liste des événements customisés de REPONSES
 * 
 * @author RCE, ARN
 * 
 */
public final class ReponsesEventConstant {

	// *************************************************************
	// Catégorie d'Événements
	// *************************************************************

	/**
	 * type d'événement lié au Interface (Flux)
	 */
	public static final String	CATEGORY_INTERFACE										= "Interface";

	// *************************************************************
	// Événements de la distribution des dossiers
	// *************************************************************

	public static final String	ABOUT_TO_CREATE_DOSSIER									= "aboutToCreateDossier";

	// *************************************************************
	// Événements de l'audit trail
	// *************************************************************

	/**
	 * Événement d'ajout d'un document au fond de dossier.
	 */
	public static final String	DOSSIER_AJOUT_FOND_DOSSIER_EVENT						= "ajoutFondDossier";

	/**
	 * Événement de suppression d'un document au fond de dossier.
	 */
	public static final String	DOSSIER_SUPP_FOND_DOSSIER_EVENT							= "supprimerFondDossier";

	/**
	 * Événement de modification d'un document du fond de dossier
	 */
	public static final String	DOSSIER_MODIF_FOND_DOSSIER_EVENT						= "modifierFondDossier";

	public static final String	COMMENT_AJOUT_FOND_DOSSIER								= "Ajout du fichier dans le fond de dossier : ";

	public static final String	COMMENT_SUPP_FOND_DOSSIER								= "Suppression du fichier dans le fond de dossier : ";

	public static final String	COMMENT_MODIF_FOND_DOSSIER								= "Modification du fichier {0} par {1} dans le fond de dossier";

	public static final String	COMMENT_DEPL_FOND_DOSSIER								= "Déplacement du fichier {0} dans le répertoire {1}";

	/**
	 * Événement de mise à jour de la réponse dans le parapheur.
	 */
	public static final String	DOSSIER_PARAPHEUR_UPDATE_EVENT							= "parapheurUpdate";

	/**
	 * commentaire : mise à jour de la réponse dans le parapheur.
	 */
	public static final String	COMMENT_PARAPHEUR_UPDATE								= "label.journal.comment.updateParapheur";

	/**
	 * Événement de l'action de casser le cachet serveur
	 */
	public static final String	DOSSIER_BRISER_SIGNATURE_EVENT							= "briserSignatureReponse";

	/**
	 * commentaire : action de casser le cachet serveur
	 */
	public static final String	COMMENT_PARAPHEUR_REMOVE_SIGNATURE						= "label.journal.comment.removeSignature";

	/**
	 * Événement de réattribution de l'étape de feuille de route du dossier
	 */
	public static final String	DOSSIER_REATTRIBUTION_EVENT								= "dossierReattribution";

	/**
	 * commentaire : réattribution de l'étape de feuille de route du dossier
	 */
	public static final String	COMMENT_DOSSIER_REATTRIBUTION							= "label.journal.comment.reattribution";

	/**
	 * Événement : arbitrage du sgg pour l'étape de feuille de route lié au dossier
	 */
	public static final String	DOSSIER_ARBITRAGE_SGG_EVENT								= "dossierArbitrageSgg";

	/**
	 * commentaire : arbitrage du sgg pour l'étape de feuille de route lié au dossier
	 */
	public static final String	COMMENT_DOSSIER_ARBITRAGE_SGG							= "label.journal.comment.arbitrageSgg";

	/**
	 * commentaire : mise en attente du dossier
	 */
	public static final String	COMMENT_DOSSIER_ATTENTE									= "label.journal.comment.enAttente";

	/**
	 * Événement de réorientation de l'étape de feuille de route du dossier
	 */
	public static final String	DOSSIER_REORIENTATION_EVENT								= "dossierReorientation";

	/**
	 * commentaire : réorientation de l'étape de feuille de route du dossier
	 */
	public static final String	COMMENT_DOSSIER_REORIENTATION							= "label.journal.comment.reorientation";

	/**
	 * commentaire : Changement d'état de la question
	 */
	public static final String	COMMENT_QUESTION_CHANGEMENT								= "label.journal.comment.changementEtatQuestion";

	/**
	 * commentaire : versioning de la réponse
	 */
	public static final String	COMMENT_REPONSE_VERSIONING								= "Nouvelle version de la réponse : ";

	public static final String	GENERATE_REPORT_EVENT									= "generateReport";

	/**
	 * événement : demande d'élimination d'un dossier
	 */
	public static final String	EVENT_DEMANDE_ELIMINATION								= "demandeElimination";

	/**
	 * commentaire : demande d'élimination d'un dossier
	 */
	public static final String	COMMENT_DEMANDE_ELIMINATION								= "label.journal.comment.demandeElimination";

	public static final String	BATCH_EVENT_UPDATE_QUESTION_CONNEXE						= "updateQuestionConnexeBatch";

	// -------------------------------------------------------------
	// param des event
	// -------------------------------------------------------------
	public static final String	DOSSIER_EVENT_PARAM										= "dossier";

	public static final String	QUESTION_EVENT_PARAM									= "question";

	/**
	 * Événement de mise à jour de la réponse.
	 */
	public static final String	DOSSIER_REPONSE_UPDATE_EVENT							= "reponseUpdate";

	/**
	 * Événement de mise à jour de la version de la réponse.
	 */
	public static final String	DOSSIER_REPONSE_VERSION_UPDATE_EVENT					= "reponseVersionUpdate";

	/**
	 * Événement lors de la creation/modification/suppression d'un lot
	 */
	public static final String	DOSSIER_REPONSE_ALLOTISSEMENT							= "dossierAllotissement";

	// Listes d'élimination
	/**
	 * Événement lors de l'élimination de données
	 */
	public static final String	AFTER_ELIMINATION_LISTE									= "afterEliminationListe";
	/**
	 * Événement lors de l'abandon d'une liste d'élimination de données
	 */
	public static final String	AFTER_ABANDON_LISTE										= "afterAbandonListe";

	// MIGRATION DE GOUVERNEMENT
	public static final String	MIGRATION_GVT_NEW_TIMBRE_MAP							= "MIGRATION_GVT_NEW_TIMBRE_MAP";
	public static final String	MIGRATION_GVT_NEXT_GVT									= "MIGRATION_GVT_NEXT_GVT";
	public static final String	MIGRATION_GVT_EVENT										= "migrationGvtEvent";
	public static final String	MIGRATION_GVT_CLOSE_EVENT								= "migrationGvtCloseEvent";
	public static final String	MIGRATION_GVT_CURRENT_GVT								= "MIGRATION_GVT_CURRENT_GVT";
	public static final String	NEW_TIMBRE_UNCHANGED_ENTITY								= "NEW_TIMBRE_UNCHANGED_ENTITY";
	public static final String	NEW_TIMBRE_DEACTIVATE_ENTITY							= "NEW_TIMBRE_DEACTIVATE_ENTITY";
	public static final String	NEW_TIMBRE_MAP											= "NEW_TIMBRE_MAP";
	public static final String	MIGRATION_GVT_CURRENT_LOGGING							= "MIGRATION_GVT_CURRENT_LOGGING";

	// Calcul des timbres
	public static final String	MIGRATION_COUNT_INFOS_TIMBRES_EVENT						= "calculTimbresEvent";

	// Contantes des batchs
	public static final String	COMPUTE_STAT_BATCH_EVENT								= "computeStatsEvent";
	public static final String	USER_DESACTIVATION_BATCH_EVENT							= "userDesactivationEvent";
	public static final String	DAILY_DISTRIBUTION_MAIL_BATCH_EVENT						= "dailyDistributionMailEvent";
	public static final String	DAILY_RETIRED_MAIL_BATCH_EVENT							= "dailyRetiredMailEvent";
	public static final String	ARCHIVAGE_MAIL_BATCH_EVENT								= "archivageMailEvent";
	public static final String	PURGE_JOURNAL_BATCH_EVENT								= "purgeJournal";
	public static final String	ERASE_FAVORIS_BATCH_EVENT								= "eraseFavorisEvent";
	public static final String	COMPUTE_PRECOMPTAGE_BATCH_EVENT							= "computePrecomptageBatch";
	public static final String	CLEAN_EXPORT_STAT_BATCH_EVENT							= "cleanExportStatDocsBatchEvent";
	public static final String	EXTRACTION_QUESTIONS_BATCH_EVENT						= "extractionQuestionsEvent";
	public static final String	CLEAN_DELETED_ALLOT_EVENT								= "cleanDeletedAllotissementEvent";
	// Constante mise a jour du precomptage
	public static final String	EVT_UPDATE_MAILBOX_STEP_COUNT							= "updateMailboxStepCount";
	public static final String	EVT_CONTEXT_MAILBOX_DOC_ID								= "mailboxDocId";
	
	// Constantes export search user
	public static final String EXPORT_USER_SEARCH_EVENT 								= "exportSearchUsers";

	/**
	 * Événement web service "chercherReponses"
	 */
	public static final String	WEBSERVICE_CHERCHER_REPONSES_EVENT						= "WSChercherReponses";

	/**
	 * Commentaire de l'événement web service "chercherReponses"
	 */
	public static final String	WEBSERVICE_CHERCHER_REPONSES_COMMENT					= "label.journal.comment.chercherReponses";

	/**
	 * Événement web service "chercherErrataReponses"
	 */
	public static final String	WEBSERVICE_CHERCHER_ERRATA_REPONSES_EVENT				= "WSChercherErrataReponses";

	/**
	 * Commentaire de l'événement web service "chercherErrataReponses"
	 */
	public static final String	WEBSERVICE_CHERCHER_ERRATA_REPONSES_COMMENT				= "label.journal.comment.chercherErrataReponses";

	/**
	 * Événement web service "envoyerReponses"
	 */
	public static final String	WEBSERVICE_ENVOYER_REPONSES_EVENT						= "WSEnvoyerReponses";

	/**
	 * Commentaire de l'événement web service "envoyerReponses"
	 */
	public static final String	WEBSERVICE_ENVOYER_REPONSES_COMMENT						= "label.journal.comment.envoyerReponses";

	/**
	 * Événement web service "envoyerReponses"
	 */
	public static final String	WEBSERVICE_ENVOYER_ERRATA_REPONSES_EVENT				= "WSEnvoyerErrataReponses";

	/**
	 * Commentaire de l'événement web service "envoyerReponses"
	 */
	public static final String	WEBSERVICE_ENVOYER_ERRATA_REPONSES_COMMENT				= "label.journal.comment.envoyerErrataReponses";

	/**
	 * Événement web service "chercherQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_QUESTION_EVENT						= "WSChercherQuestion";

	/**
	 * Commentaire de l'événement web service "chercherQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_QUESTION_COMMENT					= "label.journal.comment.chercherQuestion";

	/**
	 * Événement web service "chercherErrataQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT				= "WSChercherErrataQuestion";

	/**
	 * Commentaire de l'événement web service "chercherErrataQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_ERRATA_QUESTION_COMMENT				= "label.journal.comment.chercherErrataQuestion";

	/**
	 * Événement web service "chercherChangementEtatQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_EVENT		= "WSChangementEtatQuestion";

	/**
	 * Commentaire de l'événement web service "chercherChangementEtatQuestion"
	 */
	public static final String	WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_COMMENT	= "label.journal.comment.chercherChangementEtatQuestion";

	/**
	 * Événement web service "envoyerQuestions"
	 */
	public static final String	WEBSERVICE_ENVOYER_QUESTIONS_EVENT						= "WSEnvoyerQuestions";

	/**
	 * Commentaire de l'événement web service "envoyerQuestions"
	 */
	public static final String	WEBSERVICE_ENVOYER_QUESTIONS_COMMENT					= "label.journal.comment.envoyerQuestions";

	/**
	 * Événement web service "envoyerQuestionsErrata"
	 */
	public static final String	WEBSERVICE_ENVOYER_QUESTIONS_ERRATA_EVENT				= "WSEnvoyerQuestionsErrata";

	/**
	 * Commentaire de l'événement web service "envoyerQuestionsErrata"
	 */
	public static final String	WEBSERVICE_ENVOYER_QUESTIONS_ERRATA_COMMENT				= "label.journal.comment.envoyerQuestionsErrata";

	/**
	 * Événement web service "changerEtatQuestions"
	 */
	public static final String	WEBSERVICE_CHANGER_ETAT_QUESTION_EVENT					= "WSChangerEtatQuestions";

	/**
	 * Commentaire de l'événement web service "changerEtatQuestions"
	 */
	public static final String	WEBSERVICE_CHANGER_ETAT_QUESTION_COMMENT				= "label.journal.comment.changerEtatQuestions";

	/**
	 * Événement web service "chercherDossier"
	 */
	public static final String	WEBSERVICE_CHERCHER_DOSSIER_EVENT						= "WSChercherDossier";

	/**
	 * Commentaire de l'événement web service "chercherDossier"
	 */
	public static final String	WEBSERVICE_CHERCHER_DOSSIER_COMMENT						= "label.journal.comment.chercherDossier";

	/**
	 * Événement web service "controlePublication"
	 */
	public static final String	WEBSERVICE_CONTROLE_PUBLICATION_EVENT					= "WSControlePublication";

	/**
	 * Commentaire de l'événement web service "controlePublication"
	 */
	public static final String	WEBSERVICE_CONTROLE_PUBLICATION_COMMENT					= "label.journal.comment.controlePublication";

	/**
	 * Événement web service "chercherRetourPublication"
	 */
	public static final String	WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT			= "WSChercherRetourPublication";

	/**
	 * Commentaire de l'événement web service "chercherRetourPublication"
	 */
	public static final String	WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_COMMENT			= "label.journal.comment.chercherRetourPublication";

	/**
	 * Événement web service "chercherAttribution"
	 */
	public static final String	WEBSERVICE_CHERCHER_ATTRIBUTION_EVENT					= "WSChercherAttribution";

	/**
	 * Commentaire de l'événement web service "chercherAttribution"
	 */
	public static final String	WEBSERVICE_CHERCHER_ATTRIBUTION_COMMENT					= "label.journal.comment.chercherAttribution";

	/**
	 * Événement web service "chercherMembresGouvernement"
	 */
	public static final String	WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT			= "WSChercherMembresGouvernement";

	/**
	 * Commentaire de l'événement web service "chercherMembresGouvernement"
	 */
	public static final String	WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT		= "label.journal.comment.chercherMembresGouvernement";

	/**
	 * Événement web service "chercherLegislature"
	 */
	public static final String	WEBSERVICE_CHERCHER_LEGISLATURE_EVENT					= "WSChercherLegislature";

	/**
	 * Commentaire de l'événement web service "chercherLegislature"
	 */
	public static final String	WEBSERVICE_CHERCHER_LEGISLATURE_COMMENT					= "label.journal.comment.chercherLegislature";

	/**
	 * Événement web service "chercherAttributionsDate"
	 * 
	 */
	public static final String	WEBSERVICE_CHERCHER_ATTRIBUTION_DATE_EVENT				= "WSChercherAttributionsDate";

	/**
	 * Commentaire de l'événement web service "chercherAttributionsDate"
	 */
	public static final String	WEBSERVICE_CHERCHER_ATTRIBUTION_DATE_COMMENT			= "label.journal.comment.chercherAttributionsDate";

}
