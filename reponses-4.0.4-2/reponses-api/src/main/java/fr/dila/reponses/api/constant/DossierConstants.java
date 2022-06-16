package fr.dila.reponses.api.constant;

import fr.dila.st.api.constant.STSchemaConstant;

/**
 * @author Antoine ROLIN
 *
 */
public final class DossierConstants {
    // *************************************************************
    // Dossier
    // *************************************************************

    /**
     * Schéma du CaseLink du dossier Réponses.
     */
    public static final String DOSSIER_REPONSES_LINK_SCHEMA = "dossier_reponses_link";

    /**
     * Préfixe du schéma du CaseLink du dossier Réponses.
     */
    public static final String DOSSIER_REPONSES_LINK_PREFIX = "drl";

    public static final String DOSSIER_SCHEMA = "dossier_reponse";

    public static final String DOSSIER_SCHEMA_PREFIX = "dos";

    public static final String QUESTION_DOCUMENT_SCHEMA = "question";

    public static final String QUESTION_DOCUMENT_SCHEMA_PREFIX = "qu";

    public static final String REPONSE_DOCUMENT_SCHEMA = "reponse";

    public static final String FOND_DE_DOSSIER_DOCUMENT_SCHEMA = "fondDossier";

    public static final String HAS_PJ_PROPERTY = "hasPJ";

    public static final String FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA = "elementFondDossier";

    public static final String INDEXATION_DOCUMENT_SCHEMA = "indexation";

    public static final String INDEXATION_DOCUMENT_SCHEMA_PREFIX = "ixa";

    public static final String INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA = "indexation_comp";

    public static final String INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA_PREFIX = "ixacomp";

    public static final String DOSSIER_DOCUMENT_TYPE = "Dossier";

    public static final String QUESTION_DOCUMENT_TYPE = "Question";

    public static final String REPONSE_DOCUMENT_TYPE = "Reponse";

    public static final String REPONSE_DOCUMENT_SCHEMA_PREFIX = "rep";

    /**
     * Nom des propriétés associées au dossier link.
     */

    public static final String DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY = "sourceNumeroQuestion";
    public static final String DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_SORT_FIELD_PROPERTY = "sortField";

    public static final String DOSSIER_REPONSES_LINK_NUMERO_QUESTION_PROPERTY = "numeroQuestion";
    public static final String DOSSIER_REPONSES_LINK_NUMERO_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_NUMERO_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY = "typeQuestion";
    public static final String DOSSIER_REPONSES_LINK_TYPE_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_PROPERTY = "nomCompletAuteur";
    public static final String DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_PROPERTY = "datePublicationJO";
    public static final String DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY = "idMinistereAttributaire";
    public static final String DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_INTITULE_MINISTERE_QUESTION_PROPERTY = "intituleMinistere";
    public static final String DOSSIER_REPONSES_LINK_INTITULE_MINISTERE_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_INTITULE_MINISTERE_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_PROPERTY = "dateSignalementQuestion";
    public static final String DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_PROPERTY;

    public static final String DOSSIER_REPONSES_LINK_MOTS_CLES_PROPERTY = "motscles";
    public static final String DOSSIER_REPONSES_LINK_MOTS_CLES_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_LINK_MOTS_CLES_PROPERTY;

    /**
     * Denormalisation sur la Question et le DossierLink
     */
    public static final String DOSSIER_REPONSES_MOTS_CLES_PROPERTY = "motscles";
    public static final String DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY = "etatsQuestion";
    public static final String DOSSIER_REPONSES_ETATS_QUESTION_XPATH =
        DOSSIER_REPONSES_LINK_PREFIX + ":" + DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY;

    /**
     * Nom des propriétés associées à la question.
     */
    public static final String DOSSIER_DOCUMENT_QUESTION_ID = "idDocumentQuestion";

    public static final String DOSSIER_DOCUMENT_REPONSE_ID = "idDocumentReponse";

    public static final String DOSSIER_DOCUMENT_FONDDEDOSSIER_ID = "idDocumentFDD";

    public static final String DOSSIER_NUMERO_QUESTION = "numeroQuestion";

    public static final String DOSSIER_LISTE_ELIMINATION = "listeElimination";

    public static final String DOSSIER_ORIGINE_QUESTION = "origineQuestion";

    public static final String DOSSIER_TYPE_QUESTION = "typeQuestion";

    public static final String DOSSIER_TEXTE_JOINT = "texte_joint";

    public static final String DOSSIER_LEGISLATURE_QUESTION = "legislatureQuestion";

    public static final String DOSSIER_DATE_RECEPTION_QUESTION = "dateReceptionQuestion";

    public static final String DOSSIER_GROUPE_POLITIQUE_QUESTION = "groupePolitique";

    public static final String DOSSIER_ID_MANDAT_QUESTION = "idMandat";

    public static final String DOSSIER_NOM_AUTEUR_QUESTION = "nomAuteur";

    public static final String DOSSIER_PRENOM_AUTEUR_QUESTION = "prenomAuteur";

    public static final String DOSSIER_CIVILITE_AUTEUR_QUESTION = "civiliteAuteur";

    public static final String DOSSIER_NOM_COMPLET_AUTEUR_QUESTION = "nomCompletAuteur";

    public static final String DOSSIER_CIRCONSCRIPTION_AUTEUR_QUESTION = "circonscriptionAuteur";

    public static final String DOSSIER_PAGE_JO_QUESTION = "pageJO";

    public static final String DOSSIER_DATE_PUBLICATION_JO_QUESTION = "datePublicationJO";

    public static final String DOSSIER_ID_MINISTERE_INTERROGE_QUESTION = "idMinistereInterroge";

    public static final String DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT = "ministereAttributaireCourant";

    public static final String DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_PRECEDENT = "ministereAttributairePrecedent";

    public static final String DOSSIER_ID_MINISTERE_REATTRIBUTION = "ministereReattribution";

    public static final String DOSSIER_TITRE_JO_MINISTERE_QUESTION = "titreJOMinistere";

    public static final String DOSSIER_INTITULE_MINISTERE_QUESTION = "intituleMinistere";

    public static final String DOSSIER_MOTS_CLEF_MINISTERE = "motclef_ministere";

    public static final String DOSSIER_ASSEMBLE_NATIONALE_ANALYSES_QUESTION = "AN_analyse";

    public static final String DOSSIER_ASSEMBLE_NATIONALE_RUBRIQUE_QUESTION = "AN_rubrique";

    public static final String DOSSIER_ASSEMBLE_NATIONALE_TETE_ANALYSE_QUESTION = "TA_rubrique";

    public static final String DOSSIER_SENAT_ID_QUESTION = "senatQuestionId";

    public static final String DOSSIER_SENAT_TITRE_QUESTION = "senatQuestionTitre";

    public static final String DOSSIER_SENAT_THEMES_QUESTION = "SE_theme";

    public static final String DOSSIER_SENAT_RUBRIQUE_QUESTION = "SE_rubrique";

    public static final String DOSSIER_SENAT_RENVOIS_QUESTION = "SE_renvoi";

    public static final String DOSSIER_DATE_RENOUVELLEMENT_QUESTION = "dateRenouvellementQuestion";

    public static final String DOSSIER_DATE_SIGNALEMENT_QUESTION = "dateSignalementQuestion";

    public static final String DOSSIER_DATE_RETRAIT_QUESTION = "dateRetraitQuestion";

    public static final String DOSSIER_DATE_CLOTURE_QUESTION = "dateClotureQuestion";

    public static final String DOSSIER_DATE_RAPPEL_QUESTION = "dateRappelQuestion";

    public static final String DOSSIER_TEXTE_QUESTION = "texteQuestion";

    public static final String DOSSIER_ETAT_QUESTION = "etatQuestionList";

    public static final String DOSSIER_ETAT_QUESTION_ITEM_PROPERTY = "etatQuestion";

    public static final String DOSSIER_ETAT_QUESTION_VALUE_PROPERTY = "etatQuestion";

    public static final String DOSSIER_ETAT_QUESTION_DATE_PROPERTY = "date_changement_etat";

    public static final String DOSSIER_CARACTERISTIQUE_QUESTION = "caracteristiqueQuestion";

    public static final String DOSSIER_NOM_DOSSIER_LOT = "idDossierLot";
    public static final String DOSSIER_NOM_DOSSIER_LOT_XPATH = DOSSIER_SCHEMA_PREFIX + ":" + DOSSIER_NOM_DOSSIER_LOT;

    public static final String DOSSIER_ETAT_RETIRE = "etatRetire";

    public static final String DOSSIER_ETAT_NON_RETIRE = "etatNonRetire";

    public static final String DOSSIER_ETAT_REATTRIBUE = "etatReattribue";

    public static final String DOSSIER_ETAT_RENOUVELE = "etatRenouvele";

    public static final String DOSSIER_ETAT_SIGNALE = "etatSignale";

    public static final String DOSSIER_ETAT_RAPPELE = "etatRappele";

    public static final String DOSSIER_ETAPE_REDACTION_ATTEINTE = "etapeRedactionAtteinte";

    public static final String DOSSIER_ETAPE_SIGNATURE_ATTEINTE = "etapeSignatureAtteinte";

    public static final String DOSSIER_REAFFECTATION_COUNT = "reaffectationCount";

    public static final String DOSSIER_DATE_TRANSMISSION_ASSEMBLEES = "dateTransmissionAssemblees";

    public static final String DOSSIER_DATE_CADUCITE = "dateCaduciteQuestion";

    public static final String DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_QUESTION = "idMinistereAttributaire";

    public static final String DOSSIER_INTITULE_MINISTERE_ATTRIBUTAIRE_QUESTION = "intituleMinistereAttributaire";

    public static final String DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION = "idMinistereRattachement";

    public static final String DOSSIER_INTITULE_MINISTERE_RATTACHEMENT_QUESTION = "intituleMinistereRattachement";

    public static final String DOSSIER_ID_DIRECTION_PILOTE_QUESTION = "idDirectionPilote";

    public static final String DOSSIER_INTITULE_DIRECTION_PILOTE_QUESTION = "intituleDirectionPilote";

    public static final String DOSSIER_LABEL_ETAPE_SUIVANTE = "labelEtapeSuivante";

    public static final String DOSSIER_DATE_DEBUT_MINISTERE_INTERROGE_QUESTION = "DATE_DEBUT";

    public static final String DOSSIER_DATE_FIN_MINISTERE_INTERROGE_QUESTION = "DATE_FIN";

    /**
     * Xpath sur le schema question
     */
    public static final String QUESTION_NUMERO_XPATH = QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_NUMERO_QUESTION;

    public static final String QUESTION_LEGISLATURE_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_LEGISLATURE_QUESTION;

    public static final String QUESTION_TYPE_QUESTION_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_TYPE_QUESTION;

    public static final String QUESTION_SOURCE_NUMERO_PROP = "sourceNumeroQuestion";

    public static final String QUESTION_SOURCE_NUMERO_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + QUESTION_SOURCE_NUMERO_PROP;

    public static final String QUESTION_INDEX_AN_RUBRIQUE_XPATH =
        INDEXATION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ASSEMBLE_NATIONALE_RUBRIQUE_QUESTION;

    public static final String QUESTION_INDEX_AN_TA_XPATH =
        INDEXATION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ASSEMBLE_NATIONALE_TETE_ANALYSE_QUESTION;

    public static final String QUESTION_IXCOMP_AN_RUBRIQUE_XPATH =
        INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ASSEMBLE_NATIONALE_RUBRIQUE_QUESTION;

    public static final String QUESTION_IXCOMP_AN_TA_XPATH =
        INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ASSEMBLE_NATIONALE_TETE_ANALYSE_QUESTION;

    public static final String QUESTION_AUTEUR_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_NOM_COMPLET_AUTEUR_QUESTION;

    public static final String QUESTION_DATE_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_PROPERTY;

    public static final String QUESTION_ORIGINE_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ORIGINE_QUESTION;

    public static final String QUESTION_DATE_SIGNAL_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_PROPERTY;

    public static final String QUESTION_MININT_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_INTITULE_MINISTERE_QUESTION;

    public static final String QUESTION_MINATTR_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_INTITULE_MINISTERE_ATTRIBUTAIRE_QUESTION;
    public static final String QUESTION_ETAT_XPATH = QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_ETAT_QUESTION;

    public static final String QUESTION_MOTCLE_XPATH =
        QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DOSSIER_REPONSES_MOTS_CLES_PROPERTY;

    /**
     * Nom des propriétés associées à la réponse.
     */
    public static final String DOSSIER_TEXTE_REPONSE = STSchemaConstant.NOTE_NOTE_PROPERTY;
    public static final String DOSSIER_ID_AUTEUR_REPONSE = "idAuteurReponse";
    public static final String DOSSIER_NUMERO_JO_REPONSE = "numeroJOReponse";
    public static final String DOSSIER_PAGE_JO_REPONSE = "pageJOReponse";
    public static final String DOSSIER_DATE_JO_REPONSE = "datePublicationJOReponse";
    public static final String DOSSIER_SIGNATURE_REPONSE = "signature";
    public static final String DOSSIER_SIGNATURE_VALIDE_REPONSE = "isSignatureValide";
    public static final String REPONSE_REMOVE_SIGNATURE_AUTHOR = "idAuteurRemoveSignature";
    public static final String REPONSE_CURRENT_ERRATUM = "erratum";

    /**
     * Nom des doublons schéma-propriétés associées à la question.
     */
    public static final String NUMERO_QUESTION_PROPERTY_NAME = "question:numeroQuestion";

    public static final String ORIGINE_QUESTION_PROPERTY_NAME = "question:origineQuestion";

    public static final String TYPE_QUESTION_PROPERTY_NAME = "question:typeQuestion";

    public static final String DATE_RECEPTION_QUESTION_PROPERTY_NAME = "question:dateReceptionQuestion";

    public static final String GROUPE_POLITIQUE_QUESTION_PROPERTY_NAME = "question:groupePolitique";

    public static final String DOSSIER_TEXTE_QUESTION_PROPERTY_NAME = "question:texteQuestion";

    public static final String ORIGINE_QUESTION_AN = "AN";

    public static final String ORIGINE_QUESTION_SENAT = "SENAT";

    /**
     * dossier signalement.
     */
    public static final String DOSSIER_SIGNALEMENTS = "signalementsQuestion";

    public static final String DOSSIER_SIGNALEMENT_DATEDEFFET = "dateDEffet";

    public static final String DOSSIER_SIGNALEMENT_DATEATTENDUE = "dateAttendue";

    /**
     * dossier renouvellement.
     */
    public static final String DOSSIER_RENOUVELLEMENTS = "renouvellementsQuestion";

    public static final String DOSSIER_RENOUVELLEMENT_DATEDEFFET = "dateDEffet";

    public static final String DOSSIER_HAS_REPONSE_INITIEE = "hasReponseInitiee";

    /**
     * dossier errata
     */
    public static final String DOSSIER_ERRATA_PROPERTY = "errata";

    public static final String DOSSIER_ERRATUM_PROPERTY = "erratum";

    public static final String DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY = "datePublication";

    public static final String DOSSIER_ERRATUM_PAGE_JO_PROPERTY = "pageJO";

    public static final String DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY = "texte_erratum";

    public static final String DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY = "texte_consolide";

    /**
     * Question connexe
     */
    public static final String DOSSIER_HASH_TITRE = "hashConnexiteTitre";
    public static final String DOSSIER_HASH_TEXTE = "hashConnexiteTexte";
    public static final String DOSSIER_HASH_SENAT = "hashConnexiteSE";
    public static final String DOSSIER_HASH_AN = "hashConnexiteAN";

    /**
     * Allotissement
     */
    public static final String ALLOTISSEMENT_DOCUMENT_TYPE = "Allotissement";
    public static final String ALLOTISSEMENT_SCHEMA = "allotissement";
    public static final String ALLOTISSEMENT_FOLDER = "allotissements-root";
    public static final String ALLOTISSEMENT_IDDOSSIERS_PROPERTY = "idDossiers";
    public static final String ALLOTISSEMENT_NOM_PROPERTY = "nom";

    public static final String INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME =
        "initial_action_internal_participant_mailboxes";
    public static final String ALL_ACTION_PARTICIPANTS_PROPERTY_NAME = "all_action_participant_mailboxes";

    /**
     * Arbitrage
     */
    public static final String IS_ARBITRATED_PROPERTY = "isArbitrated";

    public static final String DOSSIER_CONNEXITE = "connexite";
    public static final String IS_QUESTION_REDEMARRE = "isRedemarre";

    // Constant utility class
    private DossierConstants() {}
}
