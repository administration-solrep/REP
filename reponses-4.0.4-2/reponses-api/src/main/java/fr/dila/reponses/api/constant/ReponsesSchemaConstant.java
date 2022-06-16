package fr.dila.reponses.api.constant;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.favoris.FavoriDossier;
import fr.dila.st.api.constant.STDossierLinkConstant;
import java.util.HashMap;
import java.util.Map;

/**
 * Constantes des schémas de l'application Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesSchemaConstant {
    /**
     * Etape de feuille de route
     */
    public static final String ROUTING_TASK_REAFFECTATION_PROPERTY = "isReaffectation";

    /**
     * JetonDoc
     */
    public static final String JETON_DOC_ATTRIBUTION = "jetonDoc_attribution";
    public static final String JETON_DOC_ATTRIBUTION_MIN = "min_attribution";
    public static final String JETON_DOC_ATTRIBUTION_DATE = "date_attribution";
    public static final String JETON_DOC_ATTRIBUTION_TYPE = "type_attribution";

    /**
     * Favoris d'indexation
     */
    public static final String INDEXATION_ROOT_TYPE = "FavorisIndexationRoot";
    public static final String INDEXATION_DOCUMENT_TYPE = "FavorisIndexation";
    public static final String INDEXATION_SCHEMA = "favorisIndexation";
    public static final String INDEXATION_TYPE = "typeIndexation";
    public static final String INDEXATION_NIVEAU1 = "niveau1";
    public static final String INDEXATION_NIVEAU2 = "niveau2";
    public static final String INDEXATION_ROOT_FOLDER = "favoris-indexation";

    public static final String FAVORI_DOSSIER_SCHEMA = "favorisDossier";

    /**
     * Notifications
     */
    public static final String NOTIFICATION_TYPE = "WsNotification";
    public static final String NOTIFICATION_FOLDER_TYPE = "WsNotificationRoot";
    public static final String NOTIFICATION_SCHEMA = "wsNotification";
    public static final String NOTIFICATION_POSTE_ID_PROPERTY = "posteId";
    public static final String NOTIFICATION_WEBSERVICE_PROPERTY = "webservice";
    public static final String NOTIFICATION_NB_ESSAIS_PROPERTY = "nbEssais";
    public static final String NOTIFICATION_ID_QUESTION_PROPERTY = "idQuestion";

    /**
     * Liste d'élimination
     */
    public static final String LISTE_ELIMINATION_TYPE = "ListeElimination";
    public static final String LISTE_ELIMINATION_SCHEMA = "listeElimination";
    public static final String LISTE_ELIMINATION_EN_COURS_PROPERTY = "en_cours";
    public static final String LISTE_ELIMINATION_SUPPRESSION_EN_COURS_PROPERTY = "suppression_en_cours";
    public static final String LISTE_ELIMINATION_ABANDON_EN_COURS_PROPERTY = "abandon_en_cours";

    /**
     * Statistiques
     */
    public static final String EXPORT_STAT_DOCUMENT_TYPE = "ExportStat";

    /**
     * Historique attribution
     */
    public static final String HISTORIQUE_ATTRIBUTION_DOCUMENT_TYPE = "HistoriqueAttribution";
    public static final String HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA = "historiqueAttribution";
    public static final String HISTORIQUE_ATTRIBUTION_MIN_PROPERTY = "minAttribution";
    public static final String HISTORIQUE_ATTRIBUTION_DATE_PROPERTY = "dateAttribution";
    public static final String HISTORIQUE_ATTRIBUTION_TYPE_PROPERTY = "typeAttribution";
    public static final String HISTORIQUE_ATTRIBUTION_DOC_SCHEMA_PREFIX = "his";

    /**
     * Historique attribution
     */
    public static final String ETAT_QUESTION_DOCUMENT_TYPE = "EtatQuestion";
    public static final String ETAT_QUESTION_DOCUMENT_SCHEMA = "etatQuestion";
    public static final String ETAT_QUESTION_LABEL_PROPERTY = "etatQuestion";
    public static final String ETAT_QUESTION_DATE_PROPERTY = "date_changement_etat";
    public static final String ETAT_QUESTION_DOC_SCHEMA_PREFIX = "etatq";
    /**
     * Correspondance entre les type de documents et les objets métier
     */
    public static final Map<String, Class<? extends DossierCommon>> documentTypeMap;

    static {
        documentTypeMap = new HashMap<>();
        documentTypeMap.put(DossierConstants.QUESTION_DOCUMENT_TYPE, Question.class);
        documentTypeMap.put(DossierConstants.DOSSIER_DOCUMENT_TYPE, Dossier.class);
        documentTypeMap.put(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, DossierLink.class);
        documentTypeMap.put(FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE, FavoriDossier.class);
    }
}
