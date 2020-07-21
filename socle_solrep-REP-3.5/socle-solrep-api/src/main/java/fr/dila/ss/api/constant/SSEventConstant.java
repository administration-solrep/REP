package fr.dila.ss.api.constant;

public final class SSEventConstant {

	public static final String	CLEAN_DELETED_STEP_EVENT			= "cleanDeletedStep";

	/**
	 * Evenement pour lancer la vérification de blocage de dossier
	 */
	public static final String	BLOCKED_ROUTES_ALERT_EVENT			= "blockedRoutesAlertEvent";

	public static final String	INCOHERENT_DOSSIER_LINK_EVENT		= "dossierLinkIncoherentBatchEvent";

	public static final String	VALIDATE_CASELINK_EVENT				= "validateCaseLink";

	/**
	 * Event de suppression physique des fichiers du fond de dossiers deleted
	 */
	public static final String	CLEAN_DELETED_REQUETE_EVENT			= "cleanDeletedRequeteEvent";

	/**
	 * Event de suppression physique des fichiers du fond de dossiers deleted
	 */
	public static final String	CLEAN_DELETED_FDD_FILE_EVENT		= "cleanDeletedFddFileEvent";

	public static final String	USER_DELETION_BATCH_EVENT			= "userDeletionBatch";

	public static final String	SEND_ALERT_BATCH_EVENT				= "sendAlertEvent";

	public static final String	UNLOCK_ORGANIGRAMME_BATCH_EVENT		= "unlockOrganigrammeBatchEvent";

	public static final String	CLEAN_DELETED_DL_EVENT				= "cleanDeletedDossierLinkEvent";

	public static final String	CLEAN_DELETED_DOS_EVENT				= "cleanDeletedDossierEvent";

	public static final String	CLEAN_DELETED_ALERT_EVENT			= "cleanDeletedAlertEvent";

	public static final String	CLEAN_DELETED_PARAPHEUR_FILE_EVENT	= "cleanDeletedParapheurFileEvent";

	public static final String	BATCH_EVENT_CLOSE_USERS_CONNECTIONS	= "closeUsersConnectionsEvent";

	/**
	 * utility class
	 */
	private SSEventConstant() {
		// do nothing
	}

}
