package fr.dila.st.api.constant;

import fr.dila.cm.caselink.CaseLinkConstants;

/**
 * Constantes des schémas du socle transverse.
 * 
 * @author jtremeaux
 */
public final class STSchemaConstant {
	// *************************************************************
	// Schéma Dublin Core (Nuxeo)
	// *************************************************************
	/**
	 * Préfixe du schéma dublin core.
	 */
	public static final String	DUBLINCORE_SCHEMA											= "dublincore";

	/**
	 * Préfixe du schéma dublin core.
	 */
	public static final String	DUBLINCORE_SCHEMA_PREFIX									= "dc";

	/**
	 * Propriété du schéma dublin core : Titre du document.
	 */
	public static final String	DUBLINCORE_TITLE_PROPERTY									= "title";

	public static final String	DUBLINCORE_TITLE_XPATH										= DUBLINCORE_SCHEMA_PREFIX
																									+ ":"
																									+ DUBLINCORE_TITLE_PROPERTY;

	/**
	 * Propriété du schéma dublin core : Utilisateur créateur du document.
	 */
	public static final String	DUBLINCORE_CREATOR_PROPERTY									= "creator";

	public static final String	DUBLINCORE_CREATOR_XPATH									= DUBLINCORE_SCHEMA_PREFIX
																									+ ":"
																									+ DUBLINCORE_CREATOR_PROPERTY;

	/**
	 * Propriété du schéma dublin core : nature du document.
	 */
	public static final String	DUBLINCORE_NATURE											= "nature";

	public static final String	DUBLINCORE_NATURE_XPATH										= DUBLINCORE_SCHEMA_PREFIX
																									+ ":"
																									+ DUBLINCORE_NATURE;

	/**
	 * Propriété du schéma dublin core : Date de création du document
	 */
	public static final String	DUBLINCORE_CREATED_PROPERTY									= "created";

	public static final String	DUBLINCORE_CREATED_XPATH									= DUBLINCORE_SCHEMA_PREFIX
																									+ ":"
																									+ DUBLINCORE_CREATED_PROPERTY;

	/**
	 * Propriété du schéma dublin core : source.
	 */
	public static final String	DUBLINCORE_SOURCE_PROPERTY									= "source";

	/**
	 * Propriété du schéma dublin core : issued.
	 */
	public static final String	DUBLINCORE_ISSUED_PROPERTY									= "issued";

	/**
	 * Propriété du schéma dublin core : Description.
	 */
	public static final String	DUBLINCORE_DESCRIPTION_PROPERTY								= "description";

	/**
	 * Propriété du schéma dublin core : Date de dernière modification.
	 */
	public static final String	DUBLINCORE_MODIFIED_PROPERTY								= "modified";

	/**
	 * Propriété du schéma dublin core : Dernier contributeur.
	 */
	public static final String	DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY						= "lastContributor";

	/**
	 * Propriété du schéma dublin core : date de validité
	 */
	public static final String	DUBLINCORE_VALID_PROPERTY									= "valid";

	// *************************************************************
	// Schéma ECM (Nuxeo)
	// *************************************************************

	/**
	 * Préfixe du schéma ECM.
	 */
	public static final String	ECM_SCHEMA_PREFIX											= "ecm";

	/**
	 * Propriété du schéma ECM : UUID.
	 */
	public static final String	ECM_UUID_PROPERTY											= "uuid";

	/**
	 * xpath de la propriete UUID
	 */
	public static final String	ECM_UUID_XPATH												= ECM_SCHEMA_PREFIX + ":"
																									+ ECM_UUID_PROPERTY;

	/**
	 * Propriété du schéma ECM : document parent.
	 */
	public static final String	ECM_PARENT_ID_PROPERTY										= "parentId";

	/**
	 * xpath de la propriété parentId
	 */
	public static final String	ECM_PARENT_ID_XPATH											= ECM_SCHEMA_PREFIX
																									+ ":"
																									+ ECM_PARENT_ID_PROPERTY;

	/**
	 * Propriété du schéma ECM : état du cycle de vie en cours.
	 */
	public static final String	ECM_CURRENT_LIFE_CYCLE_STATE								= "currentLifeCycleState";

	/**
	 * xpath de la propriete currentLifeCycleState
	 */
	public static final String	ECM_CURRENT_LIFE_CYCLE_STATE_XPATH							= ECM_SCHEMA_PREFIX
																									+ ":"
																									+ ECM_CURRENT_LIFE_CYCLE_STATE;

	/**
	 * Propriété du schema ECM : lockCreated
	 */
	public static final String	ECM_LOCK_CREATED_PROPERTY									= "lockCreated";

	/**
	 * Propriété du schéma ECM : nom du document
	 */
	public static final String	ECM_NAME_PROPERTY											= "name";

	/**
	 * xpath de la propiété nom
	 */
	public static final String	ECM_NAME_XPATH												= ECM_SCHEMA_PREFIX + ":"
																									+ ECM_NAME_PROPERTY;

	/**
	 * Propriété isProxy
	 */
	public static final String	ECM_ISPROXY_PROPERTY										= "isProxy";

	/**
	 * xpath de la propriété isProxy
	 */
	public static final String	ECM_ISPROXY_XPATH											= ECM_SCHEMA_PREFIX
																									+ ":"
																									+ ECM_ISPROXY_PROPERTY;

	/**
	 * Propriété du schéma ECM : champ plein texte
	 */
	public static final String	ECM_FULLTEXT_PROPERTY										= "fulltext";

	// *************************************************************
	// Schéma fichier (Nuxeo)
	// *************************************************************

	/**
	 * Schéma fichier.
	 */
	public static final String	FILE_SCHEMA													= "file";

	/**
	 * Propriété du schéma fichier : nom du fichier.
	 */
	public static final String	FILE_FILENAME_PROPERTY										= "filename";

	/**
	 * Propriété du schéma fichier : contenu du fichier.
	 */
	public static final String	FILE_CONTENT_PROPERTY										= "content";

	/**
	 * Propriété du schéma fichier:content : type MIME du fichier.
	 */
	public static final String	FILE_CONTENT_MIME_TYPE_PROPERTY								= "content/mime-type";

	/**
	 * Propriété du schéma fichier:content : codage du fichier.
	 */
	public static final String	FILE_CONTENT_ENCODING_PROPERTY								= "content/encoding";

	/**
	 * Propriété du schéma fichier:content : digest du fichier sha512
	 */
	public static final String	FILE_CONTENT_DIGEST_SHA512_PROPERTY							= "content/digestsha512";

	/**
	 * Propriété du schéma fichier:content : taille du fichier.
	 */
	public static final String	FILE_CONTENT_LENGTH_PROPERTY								= "content/length";

	/**
	 * Propriété du schéma fichier:content : données du fichier.
	 */
	public static final String	FILE_CONTENT_DATA_PROPERTY									= "content/data";

	// *************************************************************
	// Schéma uid (Nuxeo)
	// *************************************************************

	/**
	 * Schéma UID.
	 */
	public static final String	UID_SCHEMA													= "uid";

	/**
	 * Propriété du schéma UID : numéro de version majeur.
	 */
	public static final String	UID_MAJOR_VERSION_PROPERTY									= "major_version";

	/**
	 * Propriété du schéma UID : numéro de version mineur.
	 */
	public static final String	UID_MINOR_VERSION_PROPERTY									= "minor_version";

	// *************************************************************
	// Schéma utilisateur
	// *************************************************************
	/**
	 * Schéma utilisateur.
	 */
	public static final String	USER_SCHEMA													= "user";

	/**
	 * Propriété du schéma utilisateur : username (login).
	 */
	public static final String	USER_USERNAME												= "username";

	/**
	 * Propriété du schéma utilisateur : prénom.
	 */
	public static final String	USER_FIRST_NAME												= "firstName";

	/**
	 * Propriété du schéma utilisateur : nom.
	 */
	public static final String	USER_LAST_NAME												= "lastName";

	/**
	 * Propriété du schéma utilisateur : titre (civilite).
	 */
	public static final String	USER_TITLE													= "title";

	/**
	 * Propriété du schéma utilisateur : employeeType (fonction).
	 */
	public static final String	USER_EMPLOYEE_TYPE											= "employeeType";

	/**
	 * Propriété du schéma utilisateur : postes.
	 */
	public static final String	USER_POSTES													= "postes";

	/**
	 * Propriété du schéma utilisateur : adresse postale.
	 */
	public static final String	USER_POSTAL_ADRESS											= "postalAddress";

	/**
	 * Propriété du schéma utilisateur : adresse postale.
	 */
	public static final String	USER_POSTAL_CODE											= "postalCode";

	/**
	 * Propriété du schéma utilisateur : adresse email.
	 */
	public static final String	USER_EMAIL													= "email";
	// *************************************************************
	// Schéma groupes
	// *************************************************************
	/**
	 * Schéma des groupes.
	 */
	public static final String	GROUP_SCHEMA												= "group";

	/**
	 * Propriété du schéma groupes : description.
	 */
	public static final String	DIRECTORY_GROUP_DESCRIPTION_PROPERTY						= "description";

	/**
	 * Propriété du schéma groupes : sous-groupes.
	 */
	public static final String	DIRECTORY_GROUP_SUBGROUPS_PROPERTY							= "subGroups";

	// *************************************************************
	// Schéma base fonction
	// *************************************************************
	/**
	 * Schéma des fonctions.
	 */
	public static final String	BASE_FUNCTION_SCHEMA										= "base_function";

	/**
	 * Propriété du schéma fonctions : description.
	 */
	public static final String	BASE_FUNCTION_DESCRIPTION_PROPERTY							= "description";

	/**
	 * Propriété du schéma fonctions : groupes parents.
	 */
	public static final String	BASE_FUNCTION_PARENTGROUPS_PROPERTY							= "parentGroups";

	// *************************************************************
	// Schéma délégation
	// *************************************************************
	/**
	 * Nom du schéma délégation.
	 */
	public static final String	DELEGATION_SCHEMA											= "delegation";

	/**
	 * Préfixe du schéma délégation.
	 */
	public static final String	DELEGATION_SCHEMA_PREFIX									= "del";

	/**
	 * Propriété du schéma délégation : date de début.
	 */
	public static final String	DELEGATION_DATE_DEBUT_PROPERTY_NAME							= "dateDebut";

	/**
	 * Propriété du schéma délégation : date de fin.
	 */
	public static final String	DELEGATION_DATE_FIN_PROPERTY_NAME							= "dateFin";

	/**
	 * Propriété du schéma délégation : utilisateur qui donne les droits de délégation.
	 */
	public static final String	DELEGATION_SOURCE_ID_PROPERTY_NAME							= "sourceId";

	/**
	 * Propriété du schéma délégation : utilisateur qui reçoit les droits de délégation.
	 */
	public static final String	DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME					= "destinataireId";

	/**
	 * Propriété du schéma délégation : liste des profils.
	 */
	public static final String	DELEGATION_PROFIL_LIST_PROPERTY_NAME						= "profilList";

	// *************************************************************
	// Schéma du Jeton lié au document (jeton_doc)
	// *************************************************************
	/**
	 * Schéma jeton_doc
	 */
	public static final String	JETON_DOCUMENT_SCHEMA										= "jeton_doc";

	/**
	 * Préfixe du schéma jeton_doc.
	 */
	public static final String	JETON_DOCUMENT_SCHEMA_PREFIX								= "jtd";

	/**
	 * Propriété du schéma jeton_doc : id_doc
	 */
	public static final String	JETON_DOCUMENT_ID_DOC										= "id_doc";

	/**
	 * Propriété du schéma jeton_doc : id_jeton_doc
	 */
	public static final String	JETON_DOCUMENT_ID_JETON_DOC									= "id_jeton";

	/**
	 * Propriété du schéma jeton_doc : id_jeton_doc
	 */
	public static final String	JETON_DOCUMENT_ID_OWNER										= "id_owner";

	/**
	 * Propriété du schéma jeton_doc : type_webservice
	 */
	public static final String	JETON_DOCUMENT_WEBSERVICE									= "type_webservice";

	/**
	 * Propriété du schéma jeton_doc : Date de création.
	 */
	public static final String	JETON_DOC_CREATED_PROPERTY									= "created";

	/**
	 * Propriété du schéma jeton_doc : type_modification
	 */
	public static final String	JETON_DOCUMENT_TYPE_MODIFICATION							= "type_modification";

	/**
	 * Propriété du schéma jeton_doc : ids_complementaires
	 */
	public static final String	JETON_DOCUMENT_IDS_COMPLEMENTAIRES							= "ids_complementaires";

	// *************************************************************
	// Schéma du Jeton Maitre
	// *************************************************************
	/**
	 * Schéma
	 */
	public static final String	JETON_MAITRE_SCHEMA											= "jeton_maitre";

	/**
	 * Propriété du schéma jeton_maitre: date_creation
	 */
	public static final String	JETON_MAITRE_WEBSERVICE										= "type_webservice";

	/**
	 * Propriété du schéma jeton_maitre: numero_jeton
	 */
	public static final String	JETON_MAITRE_NUMERO											= "numero_jeton";

	/**
	 * Propriété du schéma jeton_maitre: id_ministere
	 */
	public static final String	JETON_MAITRE_ID_PROPRIETAIRE								= "id_proprietaire";

	// *************************************************************
	// Schéma du lock Jeton Maitre
	// *************************************************************
	/**
	 * Schéma
	 */
	public static final String	LOCK_JETON_MAITRE_SCHEMA									= "lock_jeton_maitre";

	/**
	 * Propriété du schéma lock_jeton_maitre: type webservice
	 */
	public static final String	LOCK_JETON_MAITRE_WEBSERVICE								= "type_webservice";

	/**
	 * Propriété du schéma lock_jeton_maitre: numero_jeton
	 */
	public static final String	LOCK_JETON_MAITRE_NUMERO									= "numero_jeton";

	/**
	 * Propriété du schéma lock_jeton_maitre: id_ministere
	 */
	public static final String	LOCK_JETON_MAITRE_ID_PROPRIETAIRE							= "id_proprietaire";

	/**
	 * Propriété du schéma lock_jeton_maitre: id_jeton_maitre
	 */
	public static final String	LOCK_JETON_MAITRE_ID										= "id_jeton_maitre";

	/**
	 * préfixe du schéma lock_jeton_maitre: ljtm
	 */
	public static final String	LOCK_JETON_MAITRE_PREFIX									= "ljtm";

	// *************************************************************
	// Propriétés du schéma LDAP DILA
	// *************************************************************
	/**
	 * Propriété du schéma LDAP DILA : propriétaire du verrou.
	 */
	public static final String	ORGANIGRAMME_LOCK_USER_NAME_PROPERTY						= "lockUserName";

	/**
	 * Propriété du schéma LDAP DILA : date du verrou.
	 */
	public static final String	ORGANIGRAMME_LOCK_DATE_PROPERTY								= "lockDate";

	// *************************************************************
	// Schéma organigramme gouvernement
	// *************************************************************
	/**
	 * Schéma des gouvernements d'organigramme.
	 */
	public static final String	ORGANIGRAMME_GOUVERNEMENT_SCHEMA							= "organigramme-gouvernement";

	// *************************************************************
	// Schéma organigramme utilisateur
	// *************************************************************
	/**
	 * Schéma des entités d'organigramme.
	 */
	public static final String	ORGANIGRAMME_ENTITE_SCHEMA									= "organigramme-entite";

	// *************************************************************
	// Schéma organigramme unité structurelle
	// *************************************************************
	/**
	 * Schéma des unités structurelles d'organigramme.
	 */
	public static final String	ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA						= "organigramme-unite-structurelle";

	// *************************************************************
	// Schéma organigramme poste
	// *************************************************************
	/**
	 * Schéma des postes d'organigramme.
	 */
	public static final String	ORGANIGRAMME_POSTE_SCHEMA									= "organigramme-poste";

	// *************************************************************
	// Schéma organigramme user
	// *************************************************************
	/**
	 * Schéma des gouvernements d'organigramme.
	 */
	public static final String	ORGANIGRAMME_USER_SCHEMA									= "user";

	// *************************************************************
	// Schéma Preferences
	// *************************************************************
	/**
	 * Préfixe du schéma Preferences.
	 */
	public static final String	PREFERENCES_NOTIFICATION_URL								= "notificationUrl";

	// *************************************************************
	// Schéma dossier
	// *************************************************************
	/**
	 * Préfixe du schéma dossier.
	 */
	public static final String	DOSSIER_SCHEMA_PREFIX										= "dos";

	/**
	 * Propriété du schéma dossier : Dernière feuille de route exécutée sur le dossier.
	 */
	public static final String	DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY						= "lastDocumentRoute";

	// *************************************************************
	// Organigramme
	// *************************************************************
	/**
	 * OrganigrammeType de la racine d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_BASE										= "BASE";

	/**
	 * OrganigrammeType des unités structurelles d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_UNITE_STRUCTURELLE						= "UST";

	/**
	 * OrganigrammeType des postes d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_POSTE										= "PST";

	/**
	 * OrganigrammeType des ministeres d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_MINISTERE									= "MIN";

	/**
	 * OrganigrammeType des institution d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_INSTITUTION								= "INS";

	/**
	 * OrganigrammeType des gouvernements d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_GOUVERNEMENT								= "GVT";

	/**
	 * OrganigrammeType des directions d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_DIRECTION									= "DIR";

	/**
	 * OrganigrammeType des users d'organigramme.
	 */
	public static final String	ORGANIGRAMME_TYPE_USER										= "USR";

	/**
	 * OrganigrammeType other.
	 */
	public static final String	ORGANIGRAMME_TYPE_OTHER										= "OTHER";

	/**
	 * Schéma organigramme-poste : chargeMissionSGG.
	 */
	public static final String	ORGANIGRAMME_POSTE_CHARGE_MISSION_SGG						= "chargeMissionSGG";

	/**
	 * Schéma organigramme-poste : superviseurSGG.
	 */
	public static final String	ORGANIGRAMME_POSTE_SUPERVISEUR_SGG							= "superviseurSGG";

	/**
	 * Schéma organigramme-poste : posteBdc.
	 */
	public static final String	ORGANIGRAMME_POSTE_BDC										= "posteBdc";

	/**
	 * Schéma organigramme-poste : posteWs.
	 */
	public static final String	ORGANIGRAMME_POSTE_WS										= "posteWs";

	// *************************************************************
	// Schéma feuille de route
	// *************************************************************
	/**
	 * Schéma feuille de route.
	 */
	public static final String	FEUILLE_ROUTE_SCHEMA										= "feuille_route";

	/**
	 * Préfixe du schéma feuille de route.
	 */
	public static final String	FEUILLE_ROUTE_SCHEMA_PREFIX									= "fdr";

	/**
	 * Propriété du schéma feuille de route : Feuille de route par défaut.
	 */
	public static final String	FEUILLE_ROUTE_DEFAUT_PROPERTY								= "feuilleRouteDefaut";

	public static final String	FEUILLE_ROUTE_DEFAUT_XPATH									= FEUILLE_ROUTE_SCHEMA_PREFIX
																									+ ":"
																									+ FEUILLE_ROUTE_DEFAUT_PROPERTY;

	/**
	 * Propriété du schéma feuille de route : Ministère.
	 */
	public static final String	FEUILLE_ROUTE_MINISTERE_PROPERTY							= "ministere";

	public static final String	FEUILLE_ROUTE_MINISTERE_XPATH								= FEUILLE_ROUTE_SCHEMA_PREFIX
																									+ ":"
																									+ FEUILLE_ROUTE_MINISTERE_PROPERTY;

	/**
	 * Propriété du schéma feuille de route : État de la demande de validation
	 */
	public static final String	FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY					= "demandeValidation";

	/**
	 * Propriété du schéma feuille de route : Evenement à l'origine de la creation
	 */
	public static final String	FEUILLE_ROUTE_TYPE_CREATION_PROPERTY						= "typeCreation";

	// *************************************************************
	// Schéma étape de feuille de route
	// *************************************************************
	/**
	 * Schéma routing task.
	 */
	public static final String	ROUTING_TASK_SCHEMA											= "routing_task";

	/**
	 * Préfixe du schéma routing task.
	 */
	public static final String	ROUTING_TASK_SCHEMA_PREFIX									= "rtsk";

	/**
	 * Propriété du schéma routing task : type d'action.
	 */
	public static final String	ROUTING_TASK_TYPE_PROPERTY									= "type";

	/**
	 * Propriété du schéma routing task : Identifiant technique de la Mailbox de distribution.
	 */
	public static final String	ROUTING_TASK_MAILBOX_ID_PROPERTY							= "distributionMailboxId";

	public static final String	ROUTING_TASK_MAILBOX_ID_XPATH								= ROUTING_TASK_SCHEMA_PREFIX
																									+ ":"
																									+ ROUTING_TASK_MAILBOX_ID_PROPERTY;

	/**
	 * Propriété du schéma routing task : UUID de la feuille de route (champ dénormalisé).
	 */
	public static final String	ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY						= "documentRouteId";

	/**
	 * Propriété du schéma routing task : validation automatique.
	 */
	public static final String	ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY					= "automaticValidation";

	/**
	 * Propriété du schéma routing task : échéance.
	 */
	public static final String	ROUTING_TASK_DUE_DATE_PROPERTY								= "dueDate";

	/**
	 * Propriété du schéma routing task : échéance.
	 */
	public static final String	ROUTING_TASK_NUM_VERSION									= "numeroVersion";

	/**
	 * Propriété du schéma routing_task : Libellé des ministères après validation de l'étape (dénormalisation).
	 */
	public static final String	ROUTING_TASK_MINISTERE_LABEL_PROPERTY						= "ministereLabel";

	/**
	 * Propriété du schéma routing_task : ID des ministères après validation de l'étape (dénormalisation).
	 */
	public static final String	ROUTING_TASK_MINISTERE_ID_PROPERTY							= "ministereId";

	/**
	 * Propriété du schéma routing_task : Libellé de la direction après validation de l'étape (dénormalisation).
	 */
	public static final String	ROUTING_TASK_DIRECTION_LABEL_PROPERTY						= "directionLabel";

	/**
	 * Propriété du schéma routing_task : Identifiant de la direction après validation de l'étape (dénormalisation).
	 */
	public static final String	ROUTING_TASK_DIRECTION_ID_PROPERTY							= "directionId";

	/**
	 * Propriété du schéma routing_task : Libellé du poste de distribution après validation de l'étape
	 * (dénormalisation).
	 */
	public static final String	ROUTING_TASK_POSTE_LABEL_PROPERTY							= "posteLabel";

	/**
	 * Propriété du schéma routing_task : Nom de l'utilisateur qui a validé l'étape après validation de l'étape
	 * (dénormalisation).
	 */
	public static final String	ROUTING_TASK_VALIDATION_USER_LABEL_PROPERTY					= "validationUserLabel";

	/**
	 * Propriété du schéma routing_task : Identifiant du poste de distribution après validation de l'étape
	 * (dénormalisation).
	 */
	public static final String	ROUTING_TASK_POSTE_ID_PROPERTY								= "posteId";

	/**
	 * Propriété du schéma routing_task : Identifiant de l'utilisateur qui a validé l'étape après validation de l'étape
	 * (dénormalisation).
	 */
	public static final String	ROUTING_TASK_VALIDATION_USER_ID_PROPERTY					= "validationUserId";

	/**
	 * Propriété du schéma routing task : actionnable (pas utilisé).
	 */
	public static final String	ROUTING_TASK_ACTIONNABLE_PROPERTY							= "actionnable";

	/**
	 * Propriété du schéma routing task : état de validation.
	 */
	public static final String	ROUTING_TASK_VALIDATION_STATUS_PROPERTY						= "validationStatus";

	/**
	 * Valeur de la propriété du schéma routing task : État de validation "avis favorable".
	 */
	public static final String	ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE			= "1";

	/**
	 * Valeur de la propriété du schéma routing task : État de validation "avis défavorable".
	 */
	public static final String	ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE		= "2";

	/**
	 * Valeur de la propriété du schéma routing task : État de validation "validé automatiquement".
	 */
	public static final String	ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE	= "3";

	/**
	 * Valeur de la propriété du schéma routing task : État de validation "non concerné".
	 */
	public static final String	ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE			= "4";

	/**
	 * Propriété du schéma routing task : date de début de l'étape.
	 */
	public static final String	ROUTING_TASK_DATE_DEBUT_ETAPE_PROPERTY						= "dateDebutEtape";

	/**
	 * Propriété du schéma routing task : date de fin de l'étape.
	 */
	public static final String	ROUTING_TASK_DATE_FIN_ETAPE_PROPERTY						= "dateFinEtape";

	/**
	 * Propriété du schéma routing task : alreadyValidated.
	 */
	public static final String	ROUTING_TASK_ALREADY_VALIDATED_PROPERTY						= "alreadyValidated";

	/**
	 * Propriété du schéma routing task : automaticValidated.
	 */
	public static final String	ROUTING_TASK_AUTOMATIC_VALIDATED_PROPERTY					= "automaticValidated";

	/**
	 * Propriété du schéma routing task : isSendMail.
	 */
	public static final String	ROUTING_TASK_IS_MAIL_SEND_PROPERTY							= "isMailSend";

	/**
	 * Propriété du schéma routing task : deadline (durée de traitement indicatif d'une étape en jours).
	 */
	public static final String	ROUTING_TASK_DEADLINE_PROPERTY								= "deadline";

	/**
	 * Propriété du schéma routing task : étape déjà dupliquée.
	 */
	public static final String	ROUTING_TASK_ALREADY_DUPLICATED_PROPERTY					= "alreadyDuplicated";

	/**
	 * Propriété du schéma routing task : étape définie comme obligatoire par le SGG.
	 */
	public static final String	ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY						= "obligatoireSGG";

	/**
	 * Propriété du schéma routing task : étape définie comme obligatoire par le ministère.
	 */
	public static final String	ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY					= "obligatoireMinistere";

	/**
	 * Schema info_comments
	 */
	public static final String	INFO_COMMENTS_SCHEMA										= "info_comments";

	/**
	 * Préfixe du schéma info comments.
	 */
	public static final String	INFO_COMMENTS_SCHEMA_PREFIX									= "infocom";

	/**
	 * Property number of comments
	 */
	public static final String	INFO_COMMMENTS_NUMBER_OF_COMMENTS_PROPERTY					= "numberOfComments";

	// *************************************************************
	// Schéma conteneur d'étapes
	// *************************************************************
	/**
	 * Schéma step folder (nuxeo).
	 */
	public static final String	STEP_FOLDER_SCHEMA											= "step_folder";

	/**
	 * Préfix du Schéma step folder (nuxeo).
	 */
	public static final String	STEP_FOLDER_SCHEMA_PREFIX									= "stepf";

	/**
	 * Propriété du schéma routing task : execution ( serial ou parallel).
	 */
	public static final String	STEP_FOLDER_EXECUTION_PROPERTY								= "execution";

	/**
	 * Valeur de la propriété "execution "du schéma routing task : parallel.
	 */
	public static final String	STEP_FOLDER_EXECUTION_PARALLEL_VALUE						= "parallel";

	/**
	 * Valeur de la propriété "execution "du schéma routing task : serial.
	 */
	public static final String	STEP_FOLDER_EXECUTION_SERIAL_VALUE							= "serial";

	// *************************************************************
	// Paramètres
	// *************************************************************
	/**
	 * Schéma paramètre
	 */
	public static final String	PARAMETRE_SCHEMA											= "parametre";

	/**
	 * Propriété du schéma parametre : unité
	 */
	public static final String	PARAMETRE_UNIT												= "unit";

	/**
	 * Propriété du schéma parametre : valeur
	 */
	public static final String	PARAMETRE_VALUE												= "value";

	// ******************************************************************
	// Note
	// ******************************************************************
	public static final String	NOTE_SCHEMA													= "note";

	public static final String	NOTE_NOTE_PROPERTY											= "note";

	// ******************************************************************
	// CaseLink
	// ******************************************************************

	/**
	 * Préfixe du schéma case_link.
	 */
	public static final String	CASE_LINK_SCHEMA_PREFIX										= "cslk";

	/**
	 * Schéma case_link.
	 */
	public static final String	CASE_LINK_SCHEMA											= CaseLinkConstants.CASE_LINK_SCHEMA;

	/**
	 * Propriété du schéma case_link : isActionable.
	 */
	public static final String	CASE_LINK_IS_ACTIONABLE_PROPERTY							= "isActionable";

	/**
	 * Propriété du schéma case_link : isEnCoursValidation.
	 */
	public static final String	CASE_LINK_IS_ENCOURS_VALIDATION								= "isEnCoursValidation";
	/**
	 * Propriété du schéma case_link : caseDocumentId.
	 */
	public static final String	CASE_LINK_CASE_DOCUMENT_ID_PROPERTY							= "caseDocumentId";

	public static final String	CASE_LINK_CASE_DOCUMENT_ID_XPATH							= CASE_LINK_SCHEMA_PREFIX
																									+ ":"
																									+ CASE_LINK_CASE_DOCUMENT_ID_PROPERTY;

	/**
	 * Propriété du schéma case_link : caseRepositoryName.
	 */
	public static final String	CASE_LINK_CASE_REPOSITORY_NAME_PROPERTY						= "caseRepositoryName";

	/**
	 * Propriété du schéma case_link : date.
	 */
	public static final String	CASE_LINK_DATE_PROPERTY										= "date";

	/**
	 * Propriété du schéma case_link : draft.
	 */
	public static final String	CASE_LINK_DRAFT_PROPERTY									= "draft";

	/**
	 * Propriété du schéma case_link : isSent.
	 */
	public static final String	CASE_LINK_IS_SENT_PROPERTY									= "isSent";

	/**
	 * Propriété du schéma case_link : sender.
	 */
	public static final String	CASE_LINK_SENDER_PROPERTY									= "sender";

	/**
	 * Propriété du schéma case_link : senderMailboxId.
	 */
	public static final String	CASE_LINK_SENDER_MAILBOX_ID_PROPERTY						= "senderMailboxId";

	/**
	 * Propriété du schéma case_link : isRead.
	 */
	public static final String	CASE_LINK_IS_READ_PROPERTY									= "isRead";

	// ******************************************************************
	// Actionable CaseLink
	// ******************************************************************

	/**
	 * Préfixe du schéma actionable_case_link.
	 */
	public static final String	ACTIONABLE_CASE_LINK_SCHEMA_PREFIX							= "acslk";

	public static final String	ACTIONABLE_CASE_LINK_AUTOMATIC_VALIDATION_PROPERTY			= "automaticValidation";

	public static final String	ACTIONABLE_CASE_LINK_DUE_DATE_PROPERTY						= "dueDate";

	public static final String	ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_PROPERTY				= "stepDocumentId";

	public static final String	ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_XPATH					= ACTIONABLE_CASE_LINK_SCHEMA_PREFIX
																									+ ":"
																									+ ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_PROPERTY;

	// *************************************************************
	// Mailbox
	// *************************************************************
	/**
	 * Schéma mailbox.
	 */
	public static final String	MAILBOX_SCHEMA												= "mailbox";

	/**
	 * Propriété du schéma mailbox : identifiant technique de la Mailbox.
	 */
	public static final String	MAILBOX_ID_PROPRETY											= "mailbox_id";

	/**
	 * log (fausse table)
	 */
	public static final String	LOG_SCHEMA													= "log";

	// *************************************************************
	// Comment
	// *************************************************************
	/**
	 * Schéma comment.
	 */
	public static final String	COMMENT_SCHEMA												= "comment";

	/**
	 * Préfixe du schéma comment.
	 */
	public static final String	COMMENT_PREFIX												= "comment";

	/**
	 * commented document id
	 */
	public static final String	COMMENT_COMMENTED_DOC_ID_PROPERTY							= "commentedDocId";

	public static final String	COMMENT_AUTHOR_PROPERTY										= "author";

	public static final String	COMMENT_TEXT_PROPERTY										= "text";

	public static final String	COMMENT_CREATION_DATE_PROPERTY								= "creationDate";

	// *************************************************************
	// Etat Application
	// *************************************************************
	/**
	 * Schéma Etat Application.
	 */
	public static final String	ETAT_APPLICATION_SCHEMA										= "etat_application";

	/**
	 * restriction acces
	 */
	public static final String	ETAT_APPLICATION_RESTRICTION_ACCES_PROPERTY					= "restriction_acces";

	/**
	 * description restriction
	 */
	public static final String	ETAT_APPLICATION_DESCRIPTION_RESTRICTION_PROPERTY			= "description_restriction";

	// *************************************************************
	// Banniere d'informations page d'accueil
	// *************************************************************
	/**
	 * Schéma Banniere.
	 */
	public static final String	BANNIERE_SCHEMA												= "etat_application";										// oui
																																						// cela
																																						// fait
																																						// finalement
																																						// parti
																																						// du
																																						// même
																																						// document

	/**
	 * affichage ou non du message
	 */
	public static final String	BANNIERE_AFFICHAGE_PROPERTY									= "affichage";

	/**
	 * message de la bannière
	 */
	public static final String	BANNIERE_MESSAGE_PROPERTY									= "message";

	// *************************************************************
	// Notifications de suivi des batchs
	// *************************************************************
	/**
	 * Schéma Notifications de suivi des batchs.
	 */
	public static final String	NOTIFICATIONS_SUIVI_BATCHS_SCHEMA							= "notifications_suivi_batchs";

	/**
	 * activation des notifications
	 */
	public static final String	NOTIFICATIONS_SUIVI_BATCHS_ETAT_NOTIFICATION_PROPERTY		= "etat_notification";

	/**
	 * liste des receveurs de mail
	 */
	public static final String	NOTIFICATIONS_SUIVI_BATCHS_LIST_USERNAME_PROPERTY			= "receiverMailList";

	// *************************************************************
	// Blob
	// *************************************************************

	/**
	 * Schéma blob.
	 */
	public static final String	BLOB_SCHEMA													= "content";

	/**
	 * Nom de la table de liaison routing_task-comment
	 */
	public static final String ROUTING_TASK_COMMENTS_PROPERTY = "comments";

	/**
	 * Id du commentaire parent
	 */
	public static final String COMMENT_PARENT_COMMENT_ID_PROPERTY = "parentCommentId";

	/**
	 * utility class
	 */
	private STSchemaConstant() {
		// do nothing
	}
}
