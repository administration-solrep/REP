package fr.dila.reponses.api.constant;

import fr.dila.st.api.constant.STBaseFunctionConstant;

/**
 * Liste des fonctions unitaires de l'application Réponses. Ces fonctions déterminent la possibilité de cliquer sur un
 * bouton, afficher un menu, accéder à un document où à une vue.
 *
 * @author jtremeaux
 */
public class ReponsesBaseFunctionConstant extends STBaseFunctionConstant {
    /**
     * Accès aux fonctions documentaires et de recherche avancée.
     */
    public static final String ESPACE_RECHERCHE_READER = "EspaceRechercheReader";

    /**
     * Accès à l'espace de suivi
     */
    public static final String ESPACE_SUIVI_READER = "EspaceSuiviReader";

    /**
     * Gestion des modèles de feuilles de route : recherche
     */
    public static final String RECHERCHE_MODELE_FDR = "RechercheModeleFDR";

    /**
     * Fonction unitaire permettant de créer un dossier.
     */
    public static final String DOSSIER_CREATOR = "DossierCreator";

    /**
     * Droit d'accès posé sur le dossier à sa création, donne accès à la lecture du dossier.
     */
    public static final String DOSSIER_RECHERCHE_READER = "DossierRechercheReader";

    /**
     * Dossier : possibilité d'agir sur un dossier sans être destinataire de la distribution.
     */
    public static final String DOSSIER_RECHERCHE_UPDATER = "DossierRechercheUpdater";

    /**
     * Dossier : possibilité d'agir sur les dossier de son ministère sans être destinataire de la distribution.
     */
    public static final String DOSSIER_RECHERCHE_MIN_UPDATER = "DossierRechercheMinUpdater";

    /**
     * Dossier : modification du parapheur par l'administrateur fonctionnel.
     */
    public static final String DOSSIER_PARAPHEUR_ADMIN_UPDATER = "DossierParapheurAdminUpdater";

    /**
     * Dossier : unlock .
     */
    public static final String DOSSIER_ADMIN_UNLOCKER = "DossierAdminUnlocker";

    /**
     * Dossier : modification du parapheur par l'administrateur ministériel.
     */
    public static final String DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER = "DossierParapheurAdminMinUpdater";

    /**
     * Dossier : modification du fond de dossier par l'administrateur fonctionnel.
     */
    public static final String FOND_DOSSIER_ADMIN_UPDATER = "FondDossierAdminUpdater";

    /**
     * Dossier : modification du fond de dossier par l'administrateur ministériel.
     */
    public static final String FOND_DOSSIER_ADMIN_MIN_UPDATER = "FondDossierAdminMinUpdater";

    /**
     * Dossier : modification d'une feuille de route par l'administrateur fonctionnel.
     */
    public static final String FDR_INSTANCE_ADMIN_UPDATER = "FDRInstanceAdminUpdater";

    /**
     * Dossier : modification d'une feuille de route par l'administrateur ministériel.
     */
    public static final String FDR_INSTANCE_ADMIN_MIN_UPDATER = "FDRInstanceAdminMinUpdater";

    /**
     * Dossier : substitution d'une feuille de route.
     */
    public static final String FDR_INSTANCE_SUBSTITUTOR = "FDRInstanceSubstitutor";

    /**
     * Dossier : redémarrage d'une feuille de route terminée.
     */
    public static final String FDR_INSTANCE_RESTARTER = "FDRInstanceRestarter";

    /**
     * Gestion des corbeilles : lecture SGG
     */
    public static final String CORBEILLE_SGG_READER = "CorbeilleSGGReader";

    /**
     * Gestion des corbeilles : selection du poste
     */
    public static final String ALL_CORBEILLE_READER = "AllCorbeilleReader";

    /**
     * Gestion du non concerné : réorientation
     */
    public static final String NC_REORIENTATION_READER = "ReorientationReader";

    /**
     * Gestion du non concerné : réorientation rédacteur
     */
    public static final String NC_REORIENTATION_REDACTEUR_READER = "ReorientationRedacteurReader";

    /**
     * Gestion du non concerné : réattribution
     */
    public static final String NC_REATTRIBUTION_READER = "ReattributionReader";

    /**
     * Gestion du non concerné : arbitrage SGG
     */
    public static final String NC_ARBITRAGE_SGG_READER = "ArbitrageSGGReader";

    /**
     * Accès l'indexation complémentaire.
     */
    public static final String INDEXATION_COMPLEMENTAIRE_UPDATER = "IndexationComplementaireUpdater";

    /**
     * Accès l'indexation complémentaire. (Admin)
     */
    public static final String INDEXATION_COMPLEMENTAIRE_ADMIN_UPDATER = "IndexationComplementaireAdminUpdater";

    /**
     * Accès l'indexation complémentaire. (Admin ministeriel)
     */
    public static final String INDEXATION_COMPLEMENTAIRE_ADMIN_MINISTERIEL_UPDATER =
        "IndexationComplementaireAdminMinistereUpdater";

    /**
     * droit de modification de Ministere de Rattachement
     */
    public static final String MINISTERE_RATTACHEMENT_UPDATER = "MinistereRattachementUpdater";

    /**
     * droit de modification de Direction Pilote
     */
    public static final String DIRECTION_PILOTE_UPDATER = "DirectionPiloteUpdater";

    /**
     * Dossier : lecture et mise à jour du répertoire réservé au SGG dans le fond de dossier.
     */
    public static final String FOND_DOSSIER_REPERTOIRE_SGG = "FondDossierRepertoireSGG";

    /**
     * Accès l'onglet Dossiers Connexes.
     */
    public static final String DOSSIERS_CONNEXES_READER = "DossiersConnexesReader";

    /**
     * Accès à la réattribution direct
     */
    public static final String REATTRIBUTION_DIRECT_READER = "ReattributionDirecteReader";

    /**
     * Accès l'onglet Allotissement.
     */
    public static final String ALLOTISSEMENT_READER = "AllotissementReader";

    /**
     * Accès à la gestion de l'allotissement
     */
    public static final String ALLOTISSEMENT_UPDATER = "AllotissementUpdater";

    /**
     * Administration : délégation des droits utilisateur.
     */
    public static final String UTILISATEUR_DELEGATOR = "UtilisateurDelegator";

    /**
     * Droit à la publication dans l'espace des requêtes générales.
     */
    public static final String REQUETE_PUBLISHER = "RequetePublisher";

    /**
     * Droit d'utilisation de l'archivage
     */
    public static final String ARCHIVAGE_EDITOR = "ArchivageEditor";

    /**
     * Ajout d'une feuille de route à un dossier n'en n'ayant pas
     */
    public static final String FEUILLE_ROUTE_SETTER = "FeuilleRouteSetter";

    /**
     * Profil(s) par défaut pour les utilisateurs temporaires
     */
    public static final String DEFAULT_PROFILE_TEMP_USER = "DefaultProfileTemporaryUser";

    /**
     * Autorise l'accès en lecture a la feuille de route
     */
    public static final String FEUILLE_ROUTE_VIEWER = "FeuilleRouteViewer";

    /**
     * Gestion des feuilles de route : modification des etape obligatoire SGG
     */
    public static final String FDR_OBLIGATOIRE_SGG_UPDATER = "FDRObligatoireSGGUpdater";

    /**
     * Statistique : visibilité des statistiques réservé au SGG
     */
    public static final String DROIT_VISIBILITE_STATS_SGG = "DroitVisibiliteStatsSGG";

    /**
     * Statistique : export de masse des statistiques
     */
    public static final String DROIT_MASS_EXPORT_STATS = "DroitExportMasseStats";

    /**
     * Droit pour l'action de réattribution directe
     */
    public static final String DROIT_REATTRIBUTION_DIRECTE = "DroitReattributionDirecte";

    /**
     * Droit pour l'action de réaffectation dans les actions de masse
     */
    public static final String DROIT_REAFFECTATION = "ReaffectationReader";

    /**
     * Consultation des requêtes réservées au cabinet BDC : reader
     */
    public static final String BDC_REQUETES_PREPARAM_READER = "BdcRequetesPreparamReader";
}
