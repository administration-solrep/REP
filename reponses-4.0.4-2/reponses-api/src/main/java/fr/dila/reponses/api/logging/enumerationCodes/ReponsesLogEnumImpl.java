package fr.dila.reponses.api.logging.enumerationCodes;

import fr.dila.ss.api.logging.enumerationCodes.SSObjetsEnum;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.enumerationCodes.STCodes;
import fr.dila.st.api.logging.enumerationCodes.STObjetsEnum;
import fr.dila.st.api.logging.enumerationCodes.STPorteesEnum;
import fr.dila.st.api.logging.enumerationCodes.STTypesEnum;

/**
 * Enumération des log info codifiés Code : 000_000_000 <br />
 * <=> [{@link STTypesEnum}]_[{@link STPorteesEnum}]_[{@link ReponsesObjetsEnum}]
 *
 */
public enum ReponsesLogEnumImpl implements STLogEnum {
    // ****************************************************SUPPRESSION
    // DOCUMENTS**********************************************************
    /**
     * DELETE ALLOT : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#ALLOT}
     */
    DEL_ALLOT_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, ReponsesObjetsEnum.ALLOT, "Suppression allotissement"),
    /**
     * Suppression d'une signature de réponse : {@link ReponsesTypesEnum#DELETE_SIGN}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#REPONSE}
     */
    DEL_SIGN_REPONSE_TEC(
        ReponsesTypesEnum.DELETE_SIGN,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.REPONSE,
        "Suppression d'une signature de réponse"
    ),

    // ****************************************************ECHEC
    // D'ACTIONS*****************************************************************
    /**
     * FAIL ACCESS WSREPONSE : {@link STTypesEnum#FAIL_ACCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#WSREPONSE}
     */
    FAIL_ACCESS_WSREPONSE_TEC(
        STTypesEnum.FAIL_ACCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.WSREPONSE,
        "Echec d'accès au webservice WSReponse"
    ),
    /**
     * FAIL ACCESS WSATTRIBUTION : {@link STTypesEnum#FAIL_ACCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#WSATTRIBUTION}
     */
    FAIL_ACCESS_WSATTRIBUTION_TEC(
        STTypesEnum.FAIL_ACCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.WSATTRIBUTION,
        "Echec d'accès au webservice WSAttribution"
    ),
    /**
     * FAIL ACCESS WSQUESTION : {@link STTypesEnum#FAIL_ACCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#WSQUESTION}
     */
    FAIL_ACCESS_WSQUESTION_TEC(
        STTypesEnum.FAIL_ACCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.WSQUESTION,
        "Echec d'accès au webservice WSQuestion"
    ),
    /**
     * FAIL ACCESS WSCONTROLE : {@link STTypesEnum#FAIL_ACCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#WSCONTROLE}
     */
    FAIL_ACCESS_WSCONTROLE_TEC(
        STTypesEnum.FAIL_ACCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.WSCONTROLE,
        "Echec d'accès au webservice WSControle"
    ),
    /**
     * FAIL NOTIFICATION WSNOTIFICATION : {@link STTypesEnum#FAIL_ACCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#WSQUESTION}
     */
    FAIL_NOTIFICATION_WSNOTIFICATION_TEC(
        STTypesEnum.FAIL_NOTIFICATION,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.WSNOTIFICATION,
        "Echec de notification de webservice"
    ),
    /**
     * FAIL GET QUESTION : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_GET_QUESTION_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Echec de récupération de la question"
    ),
    /**
     * FAIL SAVE QUESTION : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_SAVE_QUESTION_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Echec d'enregistrement de la question"
    ),
    /**
     * FAIL SAVE REPONSE : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#REPONSE}
     */
    FAIL_SAVE_REPONSE_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.REPONSE,
        "Echec d'enregistrement de la réponse"
    ),
    /**
     * FAIL GET ERRATA : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#ERRATA}
     */
    FAIL_GET_ERRATA_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.ERRATA,
        "Echec de récupération de l'errata"
    ),
    /**
     * FAIL SAVE ERRATA : {@link STTypesEnum#FAIL_SAVE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#ERRATA}
     */
    FAIL_SAVE_ERRATA_TEC(
        STTypesEnum.FAIL_SAVE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.ERRATA,
        "Echec d'enregistrement de l'errata"
    ),
    /**
     * FAIL UPDATE QUESTION : {@link STTypesEnum#FAIL_UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_UPDATE_QUESTION_TEC(
        STTypesEnum.FAIL_UPDATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Echec de mise à jour de la question"
    ),
    /**
     * FAIL GET PARAMETER VALIDATE REPONSE : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#PARAMETER_VALIDATE_REPONSE}
     */
    FAIL_GET_PARAM_VAL_REP_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.PARAMETER_VALIDATE_REPONSE,
        "Echec de récupération du paramètre de validation de réponse"
    ),
    /**
     * FAIL GET PARAMETER DELAI TRAITEMENT QUESTION : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#PARAM_DELAI_QUESTION}
     */
    FAIL_GET_PARAM_VAL_DELAI_TRAIT_QUESTION(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.PARAM_DELAI_QUESTION,
        "Echec de récupération du paramètre de validation de réponse"
    ),
    /**
     * FAIL_CREATE_INDEX_ANALYSE_TEC : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#INDEX_ANALYSE}
     */
    FAIL_CREATE_INDEX_ANALYSE_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.INDEX_ANALYSE,
        "Echec de création de l'indexation d'analyse"
    ),
    /**
     * FAIL_PROCESS_INDEX_ANALYSE_TEC : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#INDEX_ANALYSE}
     */
    FAIL_PROCESS_INDEX_ANALYSE_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.INDEX_ANALYSE,
        "Echec de traitement de l'indexation d'analyse"
    ),
    /**
     * FAIL_GET_ALLOT_TEC : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#ALLOT}
     */
    FAIL_GET_ALLOT_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.ALLOT,
        "Echec de récupération de l'allotissement"
    ),
    /**
     * FAIL_CREATE_ALLOT_TEC : {@link STTypesEnum#FAIL_CREATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#ALLOT}
     */
    FAIL_CREATE_ALLOT_TEC(
        STTypesEnum.FAIL_CREATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.ALLOT,
        "Echec de création de l'allotissement"
    ),
    /**
     * FAIL_VALIDATE_QUESTION_TEC : {@link STTypesEnum#FAIL_VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_VALIDATE_QUESTION_TEC(
        STTypesEnum.FAIL_VALIDATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Echec de vérification de la question"
    ),
    /**
     * Erreur lors de la publication d'une question : {@link STTypesEnum#FAIL_PUBLISH}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_PUBLISH_QUESTION_TEC(
        STTypesEnum.FAIL_PUBLISH,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Erreur lors de la publication d'une question"
    ),
    /**
     * Erreur lors de la suppression d'une liste d'élimination : {@link STTypesEnum#FAIL_DEL}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#LISTE_ELIMINATION}
     */
    FAIL_DEL_LISTE_ELIMINATION_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.LISTE_ELIMINATION,
        "Erreur lors de la suppression d'une liste d'élimination"
    ),
    /**
     * Echec de suppression du document d'export stat : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#EXP_STAT_DOC}
     */
    FAIL_DEL_EXP_STAT_TEC(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.EXP_STAT_DOC,
        "Echec de suppression du document d'export stat"
    ),
    /**
     * Echec de récupération de l'historique d'attribution : {@link STTypesEnum#FAIL_GET}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#HISTORIQUE_ATTRIBUTION}
     */
    FAIL_GET_HISTORIQUE_ATTR_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.HISTORIQUE_ATTRIBUTION,
        "Echec de récupération de l'historique d'attribution"
    ),
    /**
     * Echec d'ajout d'une étape pour arbitrage : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link ReponsesObjetsEnum#STEP_ARBITRAGE}
     */
    FAIL_ADD_STEP_ARBITRAGE_FONC(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.FONCTIONNELLE,
        ReponsesObjetsEnum.STEP_ARBITRAGE,
        "Echec fonctionnel de l'ajout d'une étape pour arbitrage"
    ),
    /**
     * Echec d'ajout d'une étape pour arbitrage : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#STEP_ARBITRAGE}
     */
    FAIL_ADD_STEP_ARBITRAGE_TEC(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.STEP_ARBITRAGE,
        "Echec technique de l'ajout d'une étape pour arbitrage"
    ),
    /**
     * Echec de signature (fonc) : {@link ReponsesTypesEnum#FAIL_SIGN}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link STObjetsEnum#DOSSIER}
     */
    FAIL_SIGN_DOSSIER_FONC(
        ReponsesTypesEnum.FAIL_SIGN,
        STPorteesEnum.FONCTIONNELLE,
        STObjetsEnum.DOSSIER,
        "Echec de signature du dossier"
    ),
    /**
     * Echec d'extraction de question : {@link ReponsesTypesEnum#FAIL_EXTRACT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#QUESTION}
     */
    FAIL_EXTRACT_QUESTION_TEC(
        ReponsesTypesEnum.FAIL_EXTRACT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.QUESTION,
        "Echec d'extraction de question"
    ),
    /**
     * Echec de récupération de la réponse : {@link ReponsesTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#REPONSE}
     */
    FAIL_GET_REPONSE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.REPONSE,
        "Echec de récupération de la réponse"
    ),
    /**
     * Echec de suppression de la signature d'une réponse : {@link ReponsesTypesEnum#FAIL_DEL_SIGN}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#REPONSE}
     */
    FAIL_DEL_SIGN_REPONSE_TEC(
        ReponsesTypesEnum.FAIL_DEL_SIGN,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.REPONSE,
        "Echec de suppression de la signature d'une réponse"
    ),

    // ****************************************************INFO
    // RECUPERATION*****************************************************************
    /**
     * GET_ATTR_QUEST_TEC : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#ATTR_QUESTION}
     */
    GET_ATTR_QUEST_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.ATTR_QUESTION,
        "Récupération d'attribut d'une question"
    ),
    /**
     * GET_ATTR_QUEST_TEC : {@link STTypesEnum#GET}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link ReponsesObjetsEnum#ATTR_QUESTION}
     */
    GET_ATTR_QUEST_FONC(
        STTypesEnum.GET,
        STPorteesEnum.FONCTIONNELLE,
        ReponsesObjetsEnum.ATTR_QUESTION,
        "Récupération d'attribut d'une question (fonc)"
    ),

    // ****************************************************BATCH*****************************************************************
    /**
     * Début du batch de dénormalisation automatique : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DENORM_AUTO}
     */
    INIT_B_DENORM_AUTO_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DENORM_AUTO,
        "Début du batch denormalisation automatique"
    ),
    /**
     * Fin du batch de dénormalisation automatique : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DENORM_AUTO}
     */
    END_B_DENORM_AUTO_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DENORM_AUTO,
        "Fin du batch denormalisation automatique"
    ),
    /**
     * Début du batch d'archivage mails : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_ARCHIV_MAIL}
     */
    INIT_B_ARCH_MAIL_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_ARCHIV_MAIL,
        "Début batch archivage mail"
    ),
    /**
     * Fin du batch d'archivage mails : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_ARCHIV_MAIL}
     */
    END_B_ARCH_MAIL_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_ARCHIV_MAIL,
        "Fin du batch archivage mail"
    ),
    /**
     * Annulation du batch d'archivage mails : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_ARCHIV_MAIL}
     */
    CANCEL_B_ARCH_MAIL_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_ARCHIV_MAIL,
        "Annulation du batch archivage mail"
    ),
    /**
     * Début du batch de recalcul du précomptage : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PRECOMPTAGE}
     */
    INIT_B_PRECOMPTAGE_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PRECOMPTAGE,
        "Début du batch de recalcul du précomptage"
    ),
    /**
     * Exécution du batch de recalcul du précomptage : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PRECOMPTAGE}
     */
    PROCESS_B_PRECOMPTAGE_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PRECOMPTAGE,
        "Exécution du batch de recalcul du précomptage"
    ),
    /**
     * Echec dans l'exécution du batch de recalcul du précomptage : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_PRECOMPTAGE}
     */
    FAIL_PROCESS_B_PRECOMPTAGE_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PRECOMPTAGE,
        "Echec dans l'exécution du batch de recalcul du précomptage"
    ),
    /**
     * Fin du batch de recalcul du précomptage : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PRECOMPTAGE}
     */
    END_B_PRECOMPTAGE_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PRECOMPTAGE,
        "Fin du batch de recalcul du précomptage"
    ),
    /**
     * Annulation du batch de recalcul du précomptage : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PRECOMPTAGE}
     */
    CANCEL_B_PRECOMPTAGE_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PRECOMPTAGE,
        "Annulation du batch de recalcul du précomptage"
    ),
    /**
     * Début du batch de calcul des stats : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_STATS}
     */
    INIT_B_STATS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_STATS,
        "Début du batch de calcul des statistiques"
    ),
    /**
     * Echec dans l'exécution du batch de calcul des stats : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_STATS}
     */
    FAIL_PROCESS_B_STATS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_STATS,
        "Echec dans l'exécution du batch de calcul des statistiques"
    ),
    /**
     * Fin du batch de calcul des stats : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_STATS}
     */
    END_B_STATS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_STATS,
        "Fin du batch de calcul des statistiques"
    ),
    /**
     * Annulation du batch de calcul des stats : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_STATS}
     */
    CANCEL_B_STATS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_STATS,
        "Annulation du batch de calcul des statistiques"
    ),
    /**
     * Début du batch d'envoi mails journaliers (questions distribuées) : {@link STTypesEnum#INIT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_DISTRIBUTED}
     */
    INIT_B_DAILYMAIL_DIS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_DISTRIBUTED,
        "Début du batch d'envoi des mails journaliers (questions distribuées)"
    ),
    /**
     * Echec dans l'exécution du batch d'envoi mails journaliers (questions distribuées) :
     * {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DAILYMAIL_DISTRIBUTED}
     */
    FAIL_PROCESS_B_DAILYMAIL_DIS_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_DISTRIBUTED,
        "Echec dans l'exécution du batch d'envoi des mails journaliers (questions distribuées)"
    ),
    /**
     * Fin du batch d'envoi mails journaliers (questions distribuées) : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_DISTRIBUTED}
     */
    END_B_DAILYMAIL_DIS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_DISTRIBUTED,
        "Fin du batch d'envoi des mails journaliers (questions distribuées)"
    ),
    /**
     * Annulation du batch d'envoi mails journaliers (questions distribuées) : {@link STTypesEnum#CANCEL}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_DISTRIBUTED}
     */
    CANCEL_B_DAILYMAIL_DIS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_DISTRIBUTED,
        "Annulation du batch d'envoi des mails journaliers (questions distribuées)"
    ),
    /**
     * Début du batch d'envoi mails journaliers (questions retirées) : {@link STTypesEnum#INIT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_RETIRED}
     */
    INIT_B_DAILYMAIL_RET_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_RETIRED,
        "Début du batch d'envoi des mails journaliers (questions retirées)"
    ),
    /**
     * Echec dans l'exécution du batch d'envoi mails journaliers (questions retirées) : {@link STTypesEnum#FAIL_PROCESS}
     * _{@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_RETIRED}
     */
    FAIL_PROCESS_B_DAILYMAIL_RET_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_RETIRED,
        "Echec dans l'exécution du batch d'envoi des mails journaliers (questions retirées)"
    ),
    /**
     * Fin du batch d'envoi mails journaliers (questions retirées) : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_RETIRED}
     */
    END_B_DAILYMAIL_RET_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_RETIRED,
        "Fin du batch d'envoi des mails journaliers (questions retirées)"
    ),
    /**
     * Annulation du batch d'envoi mails journaliers (questions retirées) : {@link STTypesEnum#CANCEL}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DAILYMAIL_RETIRED}
     */
    CANCEL_B_DAILYMAIL_RET_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DAILYMAIL_RETIRED,
        "Annulation du batch d'envoi des mails journaliers (questions retirées)"
    ),
    /**
     * Début du batch de suppression des favoris : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEL_FAVORIS}
     */
    INIT_B_DEL_FAV_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_FAVORIS,
        "Début du batch de suppression des favoris"
    ),
    /**
     * Fin du batch de suppression des favoris : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEL_FAVORIS}
     */
    END_B_DEL_FAV_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_FAVORIS,
        "Fin du batch de suppression des favoris"
    ),
    /**
     * Annulation du batch de suppression des favoris : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEL_FAVORIS}
     */
    CANCEL_B_DEL_FAV_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_FAVORIS,
        "Annulation du batch de suppression des favoris"
    ),
    /**
     * Début du batch de purge de l'audit trail : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PURGE_AUDIT}
     */
    INIT_B_PURGE_AUDIT_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PURGE_AUDIT,
        "Début du batch de purge de l'audit trail"
    ),
    /**
     * Fin du batch de purge de l'audit trail : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PURGE_AUDIT}
     */
    END_B_PURGE_AUDIT_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PURGE_AUDIT,
        "Fin du batch de purge de l'audit trail"
    ),
    /**
     * Annulation du batch de purge de l'audit trail : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_PURGE_AUDIT}
     */
    CANCEL_B_PURGE_AUDIT_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_PURGE_AUDIT,
        "Annulation du batch de purge de l'audit trail"
    ),
    /**
     * Annulation du batch de désactivation des utilisateurs : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_DEACTIVATE_USERS}
     */
    CANCEL_B_DEACTIVATE_USERS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        SSObjetsEnum.BATCH_DEACTIVATE_USERS,
        "Annulation du batch de désactivation des utilisateurs"
    ),
    /**
     * Début du batch de suppression des documents d'export statistiques : {@link STTypesEnum#INIT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DEL_EXPORT_STAT}
     */
    INIT_B_DEL_EXP_STAT_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_EXPORT_STAT,
        "Début du batch de suppression des documents d'export statistiques"
    ),
    /**
     * Fin du batch de suppression des documents d'export statistiques : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DEL_EXPORT_STAT}
     */
    END_B_DEL_EXP_STAT_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_EXPORT_STAT,
        "Fin du batch de suppression des documents d'export statistiques"
    ),
    /**
     * Annulation du batch de suppression des documents d'export statistiques : {@link STTypesEnum#CANCEL}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_DEL_EXPORT_STAT}
     */
    CANCEL_B_DEL_EXP_STAT_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_DEL_EXPORT_STAT,
        "Annulation du batch de suppression des documents d'export statistiques"
    ),
    // **********************************************************OPERATIONS****************************************************
    /**
     * Début de l'opération d'extraction des questions : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_EXTR_QUEST}
     */
    INIT_OPERATION_EXTR_QUEST_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_EXTR_QUEST,
        "Début de l'opération d'extraction des questions"
    ),
    /**
     * Echec de l'opération d'extraction des questions : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_EXTR_QUEST}
     */
    FAIL_PROCESS_OPERATION_EXTR_QUEST_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_EXTR_QUEST,
        "Echec de l'opération d'extraction des questions"
    ),
    /**
     * Fin de l'opération d'extraction des questions : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_EXTR_QUEST}
     */
    END_OPERATION_EXTR_QUEST_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_EXTR_QUEST,
        "Fin de l'opération d'extraction des questions"
    ),
    /**
     * Début de l'opération d'élimination des dossiers : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_ELIMINER_DOSSIER}
     */
    INIT_OPERATION_ELIMINER_DOSSIER_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_ELIMINER_DOSSIER,
        "Début de l'opération d'élimination des dossiers"
    ),
    /**
     * Echec de l'opération d'élimination des dossiers : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_ELIMINER_DOSSIER}
     */
    FAIL_OPERATION_ELIMINER_DOSSIER_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_ELIMINER_DOSSIER,
        "Echec de l'opération d'élimination des dossiers"
    ),
    /**
     * Exécution de la suppression des dossiers : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_ELIMINER_DOSSIER}
     */
    PROCESS_OPERATION_ELIMINER_DOSSIER_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_ELIMINER_DOSSIER,
        "Exécution de la suppression des dossiers"
    ),
    /**
     * Fin de l'opération d'élimination des dossiers : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_ELIMINER_DOSSIER}
     */
    END_OPERATION_ELIMINER_DOSSIER_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_ELIMINER_DOSSIER,
        "Fin de l'opération d'élimination des dossiers"
    ),

    /**
     * Début de l'opération de cloture des dossiers de la législature : {@link STTypesEnum#INIT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_CLOTURE_DOSSIER_LEGISLATURE}
     */
    INIT_OPERATION_CLOTURER_DOSSIER__LEGISLATURE_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_CLOTURE_DOSSIER_LEGISLATURE,
        "Début de l'opération de cloture des dossiers de la législature"
    ),
    /**
     * Echec de l'opération de cloture des dossiers de la législature : {@link STTypesEnum#FAIL_PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_CLOTURE_DOSSIER_LEGISLATURE}
     */
    FAIL_OPERATION_CLOTURE_DOSSIER_LEGISLATURE_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_CLOTURE_DOSSIER_LEGISLATURE,
        "Echec de l'opération de cloture des dossiers de la législature"
    ),
    /**
     * Exécution de la cloture des dossiers de la législature : {@link STTypesEnum#PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_CLOTURE_DOSSIER_LEGISLATURE}
     */
    PROCESS_OPERATION_CLOTURE_DOSSIER_LEGISLATURE_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_CLOTURE_DOSSIER_LEGISLATURE,
        "Exécution de la cloture des dossiers de la législature"
    ),
    /**
     * Fin de l'opération d'élimination de cloture des dossiers de la législature : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_CLOTURE_DOSSIER_LEGISLATURE}
     */
    END_OPERATION_CLOTURE_DOSSIER_LEGISLATURE_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_CLOTURE_DOSSIER_LEGISLATURE,
        "Fin de l'opération de cloture des dossiers de la législature"
    ),
    /**
     * Début de l'opération de versement des dossiers : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_VERSER_DOSSIER}
     */
    INIT_OPERATION_VERSER_DOSSIER_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_VERSER_DOSSIER,
        "Début de l'opération de versement des dossiers pour élimination"
    ),
    /**
     * Echec de l'opération de versement des dossiers : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}
     * _{@link ReponsesObjetsEnum#OPERATION_VERSER_DOSSIER}
     */
    FAIL_OPERATION_VERSER_DOSSIER_TEC(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_VERSER_DOSSIER,
        "Echec de l'opération de versement des dossiers pour élimination"
    ),
    /**
     * Exécution du versement des dossiers : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_VERSER_DOSSIER}
     */
    PROCESS_OPERATION_VERSER_DOSSIER_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_VERSER_DOSSIER,
        "Exécution du versement des dossiers pour élimination"
    ),
    /**
     * Fin de l'opération de versement des dossiers : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#OPERATION_VERSER_DOSSIER}
     */
    END_OPERATION_VERSER_DOSSIER_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_VERSER_DOSSIER,
        "Fin de l'opération de versement des dossiers pour élimination"
    ),
    /**
     * Début opération SolonEpg.Dossier.DoneToRunning : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_DOSSIER_DONE_TO_RUNNING}
     */
    INIT_OPERATION_DOSSIER_DONE_TO_RUNNING(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_DOSSIER_DONE_TO_RUNNING,
        "Début opération SolonEpg.Dossier.DoneToRunning"
    ),
    /**
     * Fin opération SolonEpg.Dossier.DoneToRunning : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_DOSSIER_DONE_TO_RUNNING}
     */
    END_OPERATION_DOSSIER_DONE_TO_RUNNING(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_DOSSIER_DONE_TO_RUNNING,
        "Fin opération SolonEpg.Dossier.DoneToRunning"
    ),
    /**
     * Opération SolonEpg.Dossier.DoneToRunning en cours : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_DOSSIER_DONE_TO_RUNNING}
     */
    PROCESS_OPERATION_DOSSIER_DONE_TO_RUNNING(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_DOSSIER_DONE_TO_RUNNING,
        "Opération SolonEpg.Dossier.DoneToRunning en cours"
    ),
    /**
     * Erreur lors du traitement SolonEpg.Dossier.DoneToRunning : {@link STTypesEnum#FAIL_PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link STObjetsEnum#OPERATION_DOSSIER_DONE_TO_RUNNING}
     */
    FAIL_OPERATION_DOSSIER_DONE_TO_RUNNING(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_DOSSIER_DONE_TO_RUNNING,
        "Erreur lors du traitement SolonEpg.Dossier.DoneToRunning"
    ),

    // **********************************************************UPDATE****************************************************
    /**
     * Mise à jour ministère attributaire : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#MIN_ATTRIBUTAIRE}
     */
    UPDATE_MIN_ATTRI_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.MIN_ATTRIBUTAIRE,
        "Mise à jour ministère attributaire"
    ),
    /**
     * Mise à jour ministère réattribution : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#MIN_REATTRIBUTION}
     */
    UPDATE_MIN_REAT_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.MIN_REATTRIBUTION,
        "Mise à jour ministère réattribution"
    ),

    // ********************************************************MIGRATION
    // **************************************************
    /**
     * Migration ministère rattachement : {@link STTypesEnum#MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#MIN_RATTACHEMENT}
     */
    MIGRATE_MIN_RATT_TEC(
        STTypesEnum.MIGRATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.MIN_RATTACHEMENT,
        "Migration métadonnée ministère rattachement"
    ),
    /**
     * Migration timbre : {@link STTypesEnum#MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#TIMBRE}
     */
    MIGRATE_TIMBRE_TEC(
        STTypesEnum.MIGRATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.TIMBRE,
        "Migration des timbres"
    ),
    /**
     * Echec de migration de timbre : {@link STTypesEnum#FAIL_MIGRATE}_{@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#TIMBRE}
     */
    FAIL_MIGRATE_TIMBRE_TEC(
        STTypesEnum.FAIL_MIGRATE,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.TIMBRE,
        "Echec de migration des timbres"
    ),
    /**
     * Echec du comptage des timbres : {@link STTypesEnum#FAIL_COUNT}_{@link STPorteesEnum#FONCTIONNELLE}_
     * {@link ReponsesObjetsEnum#TIMBRE}
     */
    FAIL_COUNT_TIMBRES_FONC(
        STTypesEnum.FAIL_COUNT,
        STPorteesEnum.FONCTIONNELLE,
        ReponsesObjetsEnum.TIMBRE,
        "Echec du calcul timbres"
    ),
    /**
     * Début du batch d'extraction des questions : {@link STTypesEnum#INIT}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_EXTRACTION_QUESTIONS}
     */
    INIT_B_EXTRACTION_QUESTIONS_TEC(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_EXTRACTION_QUESTIONS,
        "Début du batch d'extraction des questions"
    ),
    /**
     * Exécution du batch d'extraction des questions : {@link STTypesEnum#PROCESS}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_EXTRACTION_QUESTIONS}
     */
    PROCESS_B_EXTRACTION_QUESTIONS_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_EXTRACTION_QUESTIONS,
        "Exécution du batch d'extraction des questions"
    ),
    /**
     * Fin du batch d'extraction des questions : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_EXTRACTION_QUESTIONS}
     */
    END_B_EXTRACTION_QUESTIONS_TEC(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_EXTRACTION_QUESTIONS,
        "Fin du batch d'extraction des questions"
    ),
    /**
     * Annulation du batch d'extraction des questions : {@link STTypesEnum#CANCEL}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_EXTRACTION_QUESTIONS}
     */
    CANCEL_B_EXTRACTION_QUESTIONS_TEC(
        STTypesEnum.CANCEL,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_EXTRACTION_QUESTIONS,
        "Annulation du batch d'extraction des questions"
    ),
    /**
     * Initialisation du batch de mise à jour des questions connexes : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_QUESTION_CONNEXE}
     */
    INIT_B_UPDATE_Q_CONNEXE(
        STTypesEnum.INIT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_QUESTION_CONNEXE,
        "Début du batch mise à jour des questions connexes"
    ),
    /**
     * Fin du batch de mise à jour des questions connexes : {@link STTypesEnum#END}_{@link STPorteesEnum#TECHNIQUE}_
     * {@link ReponsesObjetsEnum#BATCH_QUESTION_CONNEXE}
     */
    END_B_UPDATE_Q_CONNEXE(
        STTypesEnum.END,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_QUESTION_CONNEXE,
        "Fin du batch mise à jour des questions connexes"
    ),
    /**
     * Echec dans l'exécution du batch de mise à jour des questions connexes : {@link STTypesEnum#END}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_QUESTION_CONNEXE}
     */
    FAIL_PROCESS_B_UPDATE_Q_CONNEXE(
        STTypesEnum.FAIL_PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_QUESTION_CONNEXE,
        "Echec lors de l'exécution du batch mise à jour des questions connexes"
    ),
    /**
     * Exécution du batch de calcul de la connexite des questions : {@link STTypesEnum#PROCESS}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#BATCH_QUESTION_CONNEXE}
     */
    PROCESS_B_UPDATE_Q_CONNEXITE_TEC(
        STTypesEnum.PROCESS,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.BATCH_QUESTION_CONNEXE,
        "Exécution du batch de calcul de la connexité des questions"
    ),
    /**
     * Echec de l'export de dossier en masse : {@link STTypesEnum#FAIL_EXPORT}_
     * {@link STPorteesEnum#TECHNIQUE}_{@link ReponsesObjetsEnum#OPERATION_EXPORT_LIST_DOSSIER}
     */
    FAIL_EXPORT_DOSSIER_MASS(
        STTypesEnum.FAIL_EXPORT,
        STPorteesEnum.TECHNIQUE,
        ReponsesObjetsEnum.OPERATION_EXPORT_LIST_DOSSIER,
        "Echec de l'exécution de l'export d'une liste de dossier"
    );

    private STCodes type;
    private STCodes portee;
    private STCodes objet;
    private String text;

    ReponsesLogEnumImpl(STCodes type, STCodes portee, STCodes objet, String text) {
        this.type = type;
        this.portee = portee;
        this.objet = objet;
        this.text = text;
    }

    @Override
    public String getCode() {
        StringBuilder code = new StringBuilder();
        code
            .append(type.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(portee.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(objet.getCodeNumberStr());
        return code.toString();
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        StringBuilder loggable = new StringBuilder();
        loggable.append("[CODE:").append(getCode()).append("] ").append(this.text);
        return loggable.toString();
    }
}
