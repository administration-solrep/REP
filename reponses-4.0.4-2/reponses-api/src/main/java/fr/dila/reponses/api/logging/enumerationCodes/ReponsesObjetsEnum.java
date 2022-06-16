package fr.dila.reponses.api.logging.enumerationCodes;

import fr.dila.st.api.logging.enumerationCodes.STCodes;

/**
 * Énumération de l'objet des actions <br />
 * Décompte sur 3 chiffres, le premier (4) indique qu'il s'agit d'un objet de Réponses <br />
 *
 * 400 : Défaut <br />
 * 401 : Allotissement (ALLOT) <br />
 * 402 : Favoris dossiers <br />
 * 403 : WSReponse <br />
 * 404 : Réponse <br />
 * 405 : Question <br />
 * 406 : Errata <br />
 * 407 : Paramètre validation de réponse <br />
 * 408 : WSAttribution <br />
 * 409 : WSQuestion <br />
 * 410 : Indexation Analyse <br />
 * 411 : Attribut question <br />
 * 412 : WSNotification <br />
 * 413 : WSControle <br />
 * 414 : Liste d'élimination <br />
 * 415 : Ministère attributaire <br />
 * 416 : Ministère réattribution <br />
 * 417 : Ministère rattachement <br />
 * 418 : Timbre <br />
 * 419 : Export stat document <br />
 * 420 : Historique attribution <br />
 * 421 : Etape "pour arbitrage <br />
 * 422 : Opération d'extraction des questions <br />
 * 423 : Opération d'élimination des dossiers <br />
 * 424 : Opération de versement des dossiers pour élimination <br />
 * 426 : Paramètre délai traitement question <br />
 * 427 : Opération export liste dossier <br />
 * 428 : Operation dossierDoneToRunning <br />
 *
 * 487 : batch d'extraction des questions <br />
 * 488 : batch de nettoyage des documents export stat <br />
 * 489 : batch de désactivation des utilisateurs <br />
 * 490 : batch de suppression des utilisateurs <br />
 * 491 : batch de purge de l'audit trail <br />
 * 492 : batch de suppression des favoris <br />
 * 493 : batch d'envoi mails journaliers (questions retirées) <br />
 * 494 : batch d'envoi mails journaliers (questions distribuées) <br />
 * 495 : batch de calcul des statistiques <br />
 * 496 : batch de recalcul du précomptage <br />
 * 498 : batch denormalisation automatique <br />
 * 499 : batch archivage mail <br />
 */
public enum ReponsesObjetsEnum implements STCodes {
    /**
     * 400 défaut
     */
    DEFAULT(400, "Objet défaut"),
    /**
     * 401 : Allotissement (ALLOT)
     */
    ALLOT(401, "Allotissement"),
    /**
     * 402 : Favoris dossiers
     */
    FAV_DOSSIERS(402, "Favoris dossiers"),
    /**
     * 403 : WSReponse
     */
    WSREPONSE(403, "WSReponse"),
    /**
     * 404 : Réponse
     */
    REPONSE(404, "Réponse"),
    /**
     * 405 : Question
     */
    QUESTION(405, "Question"),
    /**
     * 406 : Errata
     */
    ERRATA(406, "Errata"),
    /**
     * 407 : Paramètre validation Réponse
     */
    PARAMETER_VALIDATE_REPONSE(407, "Paramètre validation de réponse"),
    /**
     * 408 : WSAttribution
     */
    WSATTRIBUTION(408, "WSAttribution"),
    /**
     * 409 : WSQuestion
     */
    WSQUESTION(409, "WSQuestion"),
    /**
     * 410 : Indexation Analyse (AN ou SENAT)
     */
    INDEX_ANALYSE(410, "Indexation Analyse (AN ou SENAT)"),
    /**
     * 411 : Attribut question
     */
    ATTR_QUESTION(411, "Attribut question"),
    /**
     * 412 : WSNotification
     */
    WSNOTIFICATION(412, "WSNotification"),
    /**
     * 413 : WSControle
     */
    WSCONTROLE(413, "WSControle"),
    /**
     * 414 : Liste d'élimination
     */
    LISTE_ELIMINATION(414, "Liste d'élimination"),
    /**
     * 415 : Ministère attributaire
     */
    MIN_ATTRIBUTAIRE(415, "Ministère attributaire"),
    /**
     * 416 : Ministère réattribution
     */
    MIN_REATTRIBUTION(416, "Ministère réattribution"),
    /**
     * 417 : Ministère rattachement
     */
    MIN_RATTACHEMENT(417, "Ministère rattachement"),
    /**
     * 418 : Timbre
     */
    TIMBRE(418, "Timbre"),
    /**
     * 419 : export stat document
     */
    EXP_STAT_DOC(419, "Export stat document"),
    /**
     * 420 : historique attribution
     */
    HISTORIQUE_ATTRIBUTION(420, "Historique attribution"),
    /**
     * 421 : Etape "pour arbitrage"
     */
    STEP_ARBITRAGE(421, "Etape pour arbitrage"),
    /**
     * 422 : Opération d'extraction des questions
     */
    OPERATION_EXTR_QUEST(422, "Opération d'extraction des questions"),
    /**
     * 423 : Elimination des dossiers
     */
    OPERATION_ELIMINER_DOSSIER(423, "Elimination des dossiers"),
    /**
     * 424 : Versement des dossiers pour élimination
     */
    OPERATION_VERSER_DOSSIER(424, "Versmeent des dossiers pour élimination"),
    /**
     * 425 : Cloture des dossiers d'une législature
     */
    OPERATION_CLOTURE_DOSSIER_LEGISLATURE(425, "Cloture des dossiers de la législature"),
    /**
     * 426 : Paramètre délai traitement question
     */
    PARAM_DELAI_QUESTION(426, "Paramètre délai traitement question"),
    /**
     * 427 : Opération export liste dossier
     */
    OPERATION_EXPORT_LIST_DOSSIER(427, "Opération export liste dossier"),
    /*
     * 428 : Operation dossierDoneToRunning
     */
    OPERATION_DOSSIER_DONE_TO_RUNNING(428, "operation dossierDoneToRunning"),
    /**
     * /*** 486 : Batch de mise à jour des questions connexes
     */
    BATCH_QUESTION_CONNEXE(486, "Mise à jour des questions connexes"),
    /**
     * 487 : Batch d'extraction des questions
     */
    BATCH_EXTRACTION_QUESTIONS(487, "Extraction des questions"),
    /**
     * 488 : batch de suppression d'export statistiques
     */
    BATCH_DEL_EXPORT_STAT(488, "Suppression d'export statistiques"),
    /**
     * 490 : batch de suppression des utilisateurs
     */
    BATCH_DEL_USERS(490, "Suppression utilisateurs"),
    /**
     * 491 : batch de purge de l'audit trail
     */
    BATCH_PURGE_AUDIT(491, "Purge audit trail"),
    /**
     * 492 : batch de suppression des favoris
     */
    BATCH_DEL_FAVORIS(492, "Suppression favoris"),
    /**
     * 493 : batch d'envoi journalier de mail questions retirées
     */
    BATCH_DAILYMAIL_RETIRED(493, "Envoi mail journalier (questions retirées)"),
    /**
     * 494 : batch d'envoi journalier de mail questions distribuées
     */
    BATCH_DAILYMAIL_DISTRIBUTED(494, "Envoi mail journalier (questions distribuées)"),
    /**
     * 495 : batch précomptage
     */
    BATCH_STATS(495, "Recalcul statistiques"),
    /**
     * 496 : batch précomptage
     */
    BATCH_PRECOMPTAGE(496, "Recalcul précomptage"),
    /**
     * 498 : batch denormalisation automatique
     */
    BATCH_DENORM_AUTO(498, "Dénormalisation automatique"),
    /**
     * 499 : batch archivage mail
     */
    BATCH_ARCHIV_MAIL(499, "Archivage mail");

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    ReponsesObjetsEnum(int codeNumber, String codeText) {
        this.codeNumber = codeNumber;
        this.codeText = codeText;
    }

    @Override
    public int getCodeNumber() {
        return this.codeNumber;
    }

    @Override
    public String getCodeText() {
        return this.codeText;
    }

    @Override
    public String getCodeNumberStr() {
        return String.valueOf(codeNumber);
    }
}
