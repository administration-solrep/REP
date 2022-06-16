package fr.dila.reponses.api.constant;

/**
 * Nom des paramètres de configuration (nuxeo.conf) de l'application Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesConfigConstant {
    /**
     * Nom de l'utilisateur qui injecte les dossiers Réponses.
     */
    public static final String REPONSES_DOSSIER_OWNER = "reponses.dossier.owner";

    /**
     * Répertoire qui va contenir le fichier généré par birt
     */
    public static final String REPONSES_GENERATED_REPORT_DIRECTORY = "reponses.birt.generated.report.dir";

    /**
     * Constante du service de signature
     */

    public static final String D2S_SERVICE_REQUEST_ID = "REQUEST_ID";

    public static final String D2S_SERVICE = "reponses.d2s.service.url";
    public static final String D2S_TRANSACTION_ID = "reponses.d2s.transaction.id";
    public static final String D2S_KEY_ALIAS = "reponses.d2s.key.alias";

    public static final String DVS_SERVICE = "reponses.dvs.service.url";
    public static final String DVS_TRANSACTION_ID = "reponses.dvs.transaction.id";
    public static final String DVS_KEY_ALIAS = "reponses.dvs.key.alias";

    public static final String DICTAO_USE_STUB = "reponses.dictao.useStub";

    /**
     * Test ou non du contenu de la reponses
     */
    public static final String VALIDATE_REPONSE_CONTENT_PARAMETER_NAME = "reponses.ws.validate.reponse.content";
    /**
     * tag HTML autorise dans le corps de la reponse soumise par webservice
     */
    public static final String VALIDATE_REPONSE_AUTHORIZED_TAGS_PARAMETER_NAME =
        "reponses.ws.validate.reponse.authorized.tags";

    /**
     * mail notification redémarrage feuille de route
     */
    public static final String SEND_MAIL_REDEMARRAGE_FDR_AN = "reponses.notification.mail.redemarrage.fdr.an";
    public static final String SEND_MAIL_REDEMARRAGE_FDR_SENAT = "reponses.notification.mail.redemarrage.fdr.senat";
}
