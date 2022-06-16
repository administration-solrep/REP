package fr.dila.reponses.core.constant;

public class DelegationConstant {

    private DelegationConstant() {
        // Constant class
    }

    /**
     * Type de document racine des délégations.
     */
    public static final String DELEGATION_ROOT_DOCUMENT_TYPE = "DelegationRoot";

    /**
     * Type de document délégation.
     */
    public static final String DELEGATION_DOCUMENT_TYPE = "Delegation";

    /**
     * Nom du schéma délégation.
     */
    public static final String DELEGATION_SCHEMA = "delegation";

    /**
     * Préfixe du schéma délégation.
     */
    public static final String DELEGATION_SCHEMA_PREFIX = "del";

    /**
     * Propriété du schéma délégation : date de début.
     */
    public static final String DELEGATION_DATE_DEBUT_PROPERTY_NAME = "dateDebut";

    /**
     * Propriété du schéma délégation : date de fin.
     */
    public static final String DELEGATION_DATE_FIN_PROPERTY_NAME = "dateFin";

    /**
     * Propriété du schéma délégation : utilisateur qui donne les droits de délégation.
     */
    public static final String DELEGATION_SOURCE_ID_PROPERTY_NAME = "sourceId";

    /**
     * Propriété du schéma délégation : utilisateur qui reçoit les droits de délégation.
     */
    public static final String DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME = "destinataireId";

    /**
     * Propriété du schéma délégation : liste des profils.
     */
    public static final String DELEGATION_PROFIL_LIST_PROPERTY_NAME = "profilList";

    public static final String DELEGATION_XPATH_DESTINATAIRE_ID =
        DELEGATION_SCHEMA + ":" + DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME;

    public static final String DELEGATION_XPATH_SOURCE_ID =
        DELEGATION_SCHEMA + ":" + DELEGATION_SOURCE_ID_PROPERTY_NAME;

    public static final String DELEGATION_XPATH_DATE_DEBUT =
        DELEGATION_SCHEMA + ":" + DELEGATION_DATE_DEBUT_PROPERTY_NAME;

    public static final String DELEGATION_XPATH_DATE_FIN = DELEGATION_SCHEMA + ":" + DELEGATION_DATE_FIN_PROPERTY_NAME;

    public static final String DELEGATION_XPATH_PROFIL_LIST =
        DELEGATION_SCHEMA + ":" + DELEGATION_PROFIL_LIST_PROPERTY_NAME;
}
