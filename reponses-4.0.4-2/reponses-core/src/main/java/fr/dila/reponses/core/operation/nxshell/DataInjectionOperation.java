package fr.dila.reponses.core.operation.nxshell;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.ALLOTISSEMENT_UPDATER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.ALL_CORBEILLE_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.DOSSIER_ADMIN_UNLOCKER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.ESPACE_RECHERCHE_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.ESPACE_SUIVI_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.FDR_INSTANCE_SUBSTITUTOR;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.MINISTERE_RATTACHEMENT_UPDATER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.NC_ARBITRAGE_SGG_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.NC_REATTRIBUTION_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.NC_REORIENTATION_READER;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.RECHERCHE_MODELE_FDR;
import static fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant.UTILISATEUR_DELEGATOR;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMINISTRATION_JOURNAL_READER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_READER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.FEUILLE_DE_ROUTE_INSTANCE_RESTARTER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.INTERFACE_ACCESS;
import static fr.dila.st.api.constant.STBaseFunctionConstant.JOURNAL_READER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ORGANIGRAMME_READER;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ROUTE_MANAGERS;
import static fr.dila.st.api.constant.STBaseFunctionConstant.SUPERVISEUR_SGG_GROUP_NAME;
import static fr.dila.st.api.constant.STBaseFunctionConstant.UTILISATEUR_CREATOR;
import static fr.dila.st.core.service.STServiceLocator.getCaseManagementDocumentTypeService;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

import com.google.common.collect.ImmutableMap;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.recherche.IndexationImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSBaseFunctionConstant;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.operation.livraison.SSCreateParametresOperation;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.BaseFunction;
import fr.dila.st.api.user.Profile;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.operation.version.STCreateParametresOperation;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STDefaultMailboxCreator;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Une opération pour injecter des données métiers dans la BDD et le repository,
 * on produit ainsi des dossiers et autres données sans passer par les
 * Webservices.
 *
 * @author tlombard
 */
@Operation(
    id = DataInjectionOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "CloturerDossiersLegislature",
    description = "Injecte des données dans la base de données"
)
public class DataInjectionOperation {
    public static final String USER_WS_AN = "ws_an";

    public static final String USER_WS_SENAT = "ws_senat";

    public static final String USER_WS_SWORD = "ws_sword";

    public static final String USER_CONTRIBUTEUR = "contributeur";

    public static final String USER_VIGIESGG = "vigieSgg";

    public static final String USER_SUPERVISEUR_SGG = "superviseurSgg";

    public static final String USER_ADMINMIN = "adminMin";

    public static final String USER_ADMINSGG = "adminsgg";

    private static final Log LOGGER = LogFactory.getLog(DataInjectionOperation.class);

    private static final String PASSWORD = "Solon2NG";

    private static final String POSTE_50002249 = "50002249";

    private static final String POSTE_50000656 = "50000656";

    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Injection.Donnees";

    private static final String FDR_FOLDER_PATH = "/case-management/workspaces/admin/modele-route";

    public static final String MINISTERE_PM_KEY = "Premier Ministre";
    public static final String MINISTERE_ECONOMIE_KEY = "Economie";
    public static final String MINISTERE_AGRICULTURE_KEY = "Agriculture";
    public static final String MINISTERE_JUSTICE_KEY = "Justice";
    public static final String MINISTERE_EDUCATION_KEY = "Education";
    public static final Map<String, ImmutableTriple<String, String, Long>> MEMBRES_GOUVERNEMENT = ImmutableMap.of(
        MINISTERE_PM_KEY,
        ImmutableTriple.of("50000500", MINISTERE_PM_KEY, 1L),
        MINISTERE_ECONOMIE_KEY,
        ImmutableTriple.of("50000507", "Min. Economie, industrie et emploi", 7L),
        MINISTERE_AGRICULTURE_KEY,
        ImmutableTriple.of("50000938", "Min. Agriculture et pêche", 2L),
        MINISTERE_JUSTICE_KEY,
        ImmutableTriple.of("50000972", "Min. Justice", 12L),
        MINISTERE_EDUCATION_KEY,
        ImmutableTriple.of("50000968", "Min. Education Nationale", 15L)
    );

    public static final String NEW_MINISTERE_ECONOMIE_KEY = "New " + MINISTERE_ECONOMIE_KEY;
    public static final String NEW_MINISTERE_AGRICULTURE_KEY = "New " + MINISTERE_AGRICULTURE_KEY;
    public static final String NEW_MINISTERE_JUSTICE_KEY = "New " + MINISTERE_JUSTICE_KEY;
    public static final String NEW_MINISTERE_EDUCATION_KEY = "New " + MINISTERE_EDUCATION_KEY;
    public static final Map<String, ImmutableTriple<String, String, Long>> MEMBRES_NEW_GOUVERNEMENT = ImmutableMap.of(
        NEW_MINISTERE_ECONOMIE_KEY,
        ImmutableTriple.of("50001507", "New Min. Economie, industrie et emploi", 7L),
        NEW_MINISTERE_AGRICULTURE_KEY,
        ImmutableTriple.of("50001938", "New Min. Agriculture et pêche", 2L),
        NEW_MINISTERE_JUSTICE_KEY,
        ImmutableTriple.of("50001972", "New Min. Justice", 12L),
        NEW_MINISTERE_EDUCATION_KEY,
        ImmutableTriple.of("50001968", "New Min. Education Nationale", 15L)
    );

    @Context
    protected CoreSession session;

    private DocumentRoutingService documentRoutingService;

    private MailboxPosteService mailboxPosteService;

    private MailboxCreator mailboxCreator;

    private STParametreService paramService;

    private STGouvernementService gouvernementService;

    private STMinisteresService ministereService;

    private STUsAndDirectionService usAndDirectionService;

    private STPostesService posteService;
    private ReponsesUserManager userManager;
    private STProfilUtilisateurService profilService;

    private String delaiQuestionSignalee;

    private static final Long LEGISLATURE = 14L;

    private ReponsesVocabularyService vocabularyService;

    private DossierDistributionService dossierDistributionService;

    /** Gouvernement actuel */
    private GouvernementNode gvtNode;
    /** Nouveau gouvernement */
    private GouvernementNode newGvtNode;

    private EntiteNode premierministre;
    private EntiteNode ministereEco;
    private EntiteNode ministereAgriculture;
    private EntiteNode ministereJustice;
    private EntiteNode ministereEducation;

    private EntiteNode newMinistereEco;
    private EntiteNode newMinistereAgriculture;
    private EntiteNode newMinistereJustice;
    private EntiteNode newMinistereEducation;

    private UniteStructurelleNode directionSGG;
    private UniteStructurelleNode directionEcoBdc;
    private UniteStructurelleNode directionAgriBdc;
    private UniteStructurelleNode directionJusticeBdc;
    private UniteStructurelleNode directionEducationBdc;

    private UniteStructurelleNode newDirectionEcoBdc;
    private UniteStructurelleNode newDirectionAgriBdc;
    private UniteStructurelleNode newDirectionJusticeBdc;
    private UniteStructurelleNode newDirectionEducationBdc;

    private PosteNode posteAgentBdc;
    private PosteNode posteAgentDgefp;
    private PosteNode posteDlfEco;
    private PosteNode posteAgentsSecGenEco;
    private PosteNode posteAdminSolonSgg;
    private PosteNode posteConsAffEcoPm;
    private PosteNode posteSecConsAffEcoPm;
    private PosteNode posteBdcAgri;
    private PosteNode posteBdcJustice;
    private PosteNode posteBdcEcologie;

    private void initServicesAndParams() {
        mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        paramService = STServiceLocator.getSTParametreService();
        vocabularyService = ReponsesServiceLocator.getVocabularyService();
        dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();
        mailboxCreator = STServiceLocator.getMailboxCreator();
        gouvernementService = STServiceLocator.getSTGouvernementService();
        ministereService = STServiceLocator.getSTMinisteresService();
        usAndDirectionService = STServiceLocator.getSTUsAndDirectionService();
        posteService = STServiceLocator.getSTPostesService();
        userManager = (ReponsesUserManager) STServiceLocator.getUserManager();
        profilService = STServiceLocator.getSTProfilUtilisateurService();

        delaiQuestionSignalee = paramService.getParametreValue(session, STParametreConstant.DELAI_QUESTION_SIGNALEE);
    }

    @OperationMethod
    public void run() {
        initServicesAndParams();

        if (checkNotRun() && (Framework.isDevModeSet() || Framework.isTestModeSet())) {
            LOGGER.info("Début de l'injection de données.");

            injectOrganigramme();
            injectUserRelatedData();
            injectMailboxes();
            externalOperation();
            injectFdR();
            injectDossiers();

            LOGGER.info("Fin de l'injection de données.");
        }
    }

    public void run(CoreSession session) {
        this.session = session;
        run();
    }

    private void injectUserRelatedData() {
        injectFunctions();
        injectGroups();
        injectUsers();
    }

    private void externalOperation() {
        callOperation(STCreateParametresOperation.ID);
        callOperation(SSCreateParametresOperation.ID);
        callOperation(ReponsesCreateParametresOperation.ID);
        callOperation(Reponses402CreateParametresOperation.ID);
    }

    private void callOperation(String... operationIds) {
        AutomationService automation = Framework.getService(AutomationService.class);
        Stream
            .of(operationIds)
            .forEach(
                id -> {
                    OperationContext ctx = new OperationContext(session);
                    try {
                        automation.run(ctx, id);
                    } catch (OperationException e) {
                        throw new NuxeoException(e);
                    }
                }
            );
    }

    private void injectFunctions() {
        injectOneFunction(UTILISATEUR_CREATOR, "Gestion des utilisateurs : création");
        injectOneFunction(
            ORGANIGRAMME_MINISTERE_UPDATER,
            "Organigramme : droit de mise à jour du ministère de l'utilisateur uniquement"
        );
        injectOneFunction(DOSSIER_ADMIN_UNLOCKER, "Permet de lever le verrou sur un dossier");
        injectOneFunction("EspaceSupervisionReader", "Administration : Supervision des connexions utilisateurs");
        injectOneFunction("ParamReferenceReader", "La lecture des paramêtres de référence");
        injectOneFunction("ArchivageEditor", "Archivage des dossiers");
        injectOneFunction(ALLOTISSEMENT_READER, "Accès à l'onglet Allotissement");
        injectOneFunction(
            "FDRInstanceAdminUpdater",
            "Dossier : modification d'une feuille de route par l'administrateur fonctionnel"
        );
        injectOneFunction("FondDossierRepertoireSGG", "Accès aux répertoires du SGG dans le fond de dossier");
        injectOneFunction("ProfilDeleter", "Gestion des profils : suppression");
        injectOneFunction(
            ReponsesBaseFunctionConstant.BDC_REQUETES_PREPARAM_READER,
            "Permet d'avoir accès aux requêtes réservées au cabinet BDC (questions signalées et renouvellées)"
        );
        injectOneFunction(MINISTERE_RATTACHEMENT_UPDATER, "Dossier : modification du ministère de rattachement");
        injectOneFunction(INTERFACE_ACCESS, "Accès à l'interface de l'application");
        injectOneFunction("WorkspaceReader", "Lecture des Workspace (?)");
        injectOneFunction(
            "FDRObligatoireSGGUpdater",
            "Gestion des modèles de feuilles de route : modification des étapes obligatoire SGG"
        );
        injectOneFunction(ESPACE_SUIVI_READER, "Accès à l'espace de suivi");
        injectOneFunction("DossierCreator", "  Dossier Réponses : création");
        injectOneFunction("UtilisateurMinistereUpdater", " Gestion des utilisateurs du ministère: mise à jour");
        injectOneFunction("IndexationComplementaireAdminMinistereUpdater", "   Accès à l'indexation complémentaire");
        injectOneFunction("UtilisateurDeleter", "Gestion des utilisateurs : suppression");
        injectOneFunction("DroitVisibiliteStatsSGG", "Statistique : visibilité des statistiques réservé au SGG’");
        injectOneFunction("ProfilUpdater", "Gestion des profils : mise à jour");
        injectOneFunction("DirectionPiloteUpdater", "Dossier : modification de la direction pilote");
        injectOneFunction("DroitExportMasseStats", "Statistique : téléchargement des statistiques");
        injectOneFunction("FDRModelValidator", "Gestion des modèles de feuilles de route : administrateur fonctionnel");
        injectOneFunction(
            "FondDossierAdminMinUpdater",
            "Dossier : modification du fond de dossier par l'administrateur ministériel"
        );
        injectOneFunction(
            ROUTE_MANAGERS,
            "Permet de modifier les feuilles de routes, doit être affecté à tous les profils"
        );
        injectOneFunction("IndexationComplementaireAdminUpdater", "Accès à l'indexation complémentaire");
        injectOneFunction(
            "DossierRechercheUpdater",
            "Dossier : possibilité d'agir sur un dossier sans être destinataire de la distribution"
        );
        injectOneFunction("WsChercherChangementDEtatQuestions", "Webservice - ChercherChangementDEtatQuestions");
        injectOneFunction(RECHERCHE_MODELE_FDR, "Gestion des modèles de feuilles de route : recherche");
        injectOneFunction("BatchSuiviReader", "Administration : suivi des batchs");
        injectOneFunction(NC_ARBITRAGE_SGG_READER, "Effectuer une demande d'arbitrage SGG");
        injectOneFunction("WsControlePublication", "Webservice - controlePublication");
        injectOneFunction("DossierDeleter", "Dossier Réponses : suppression");
        injectOneFunction("UtilisateurUpdater", "Gestion des utilisateurs : mise à jour");
        injectOneFunction(DOSSIER_RECHERCHE_READER, "Dossier : accès en lecture aux dossiers à partir des recherches");
        injectOneFunction("SuperviseurSGGUpdater", "Droit d'édition de la propriété Superviseur SGG sur les postes");
        injectOneFunction(
            "FeuilleRouteViewer",
            "Permet de lire la feuille de route d'un dossier n'étant pas dans sa corbeille"
        );
        injectOneFunction(
            "AdminFonctionnelEmailReceiver",
            "Email : réception des mél. destinés aux administrateurs fonctionnels"
        );
        injectOneFunction("ReattributionDirecteReader", "Dossier : réattribution directe");
        injectOneFunction("WsChercherAttributions", "Webservice - chercherAttributions");
        injectOneFunction("IndexationComplementaireUpdater", "Accès à l'indexation complémentaire");
        injectOneFunction("SenatEditor", "Webservice - Accès aux questions SENAT");
        injectOneFunction(
            "AssembleesParlementairesReader",
            "Accès en lecture à l'élement de l'organigramme \"Assemblées Parlementaires\""
        );
        injectOneFunction("WsChercherAttributionsDate", "Webservice - chercherAttributionsDate");
        injectOneFunction("EspaceCorbeilleReader", "Accès à l'espace corbeille");
        injectOneFunction("WsEnvoyerQuestionsErrata", "Webservice - envoyerQuestionsErrata");
        injectOneFunction(
            FEUILLE_DE_ROUTE_MODEL_UDPATER,
            "Gestion des modèles de feuilles de route : administrateur ministériel"
        );
        injectOneFunction(ESPACE_ADMINISTRATION_READER, "Accès à l'espace d'administration");
        injectOneFunction(
            "FondDossierAdminUpdater",
            "Dossier : modification du fond de dossier par l'administrateur fonctionnel"
        );
        injectOneFunction("RequetePublisher", "Droit de publier une requête dans l'espace des requêtes générales.");
        injectOneFunction(ADMINISTRATION_UTILISATEUR_READER, "Gestion des utilisateurs : lecture");
        injectOneFunction(ALL_CORBEILLE_READER, "Corbeille : outil de sélection d'un poste ou d'un utilisateur");
        injectOneFunction(FDR_INSTANCE_SUBSTITUTOR, "Dossier : substitution d'une feuille de route");
        injectOneFunction("WsRechercherDossier", "Webservice - rechercherDossier");
        injectOneFunction(ESPACE_RECHERCHE_READER, "Accès à l'espace de recherche avancée");
        injectOneFunction("OrganigrammeAdminUnlocker", "Permet de lever le verrou dans l'organigramme");
        injectOneFunction(NC_REORIENTATION_READER, "Effectuer une réorientation");
        injectOneFunction("ProfilReader", "Gestion des profils : lecture");
        injectOneFunction("WsChangerEtatQuestions", "Webservice - changerEtatQuestions");
        injectOneFunction("UtilisateurMinistereDeleter", "Gestion des utilisateurs du ministère : suppression");
        injectOneFunction(
            "DossierRechercheMinUpdater",
            "Dossier : possibilité d'agir sur les dossier de son ministère sans être destinataire de la distribution"
        );
        injectOneFunction("WsChercherReponses", "Webservice - chercherReponses");
        injectOneFunction(ORGANIGRAMME_READER, "Organigramme : lecture");
        injectOneFunction("WsEnvoyerReponsesErrata", "Webservice - envoyerReponseErrata");
        injectOneFunction(
            "DossierParapheurAdminMinUpdater",
            "Dossier : modification du parapheur par l'administrateur ministériel"
        );
        injectOneFunction("ReaffectationReader", "Effectuer une réaffectation");
        injectOneFunction(
            "DossierParapheurAdminUpdater",
            "Dossier : modification du parapheur par l'administrateur fonctionnel"
        );
        injectOneFunction("FeuilleRouteSetter", "Ajout d'une feuille de route à un dossier n'en n'ayant pas");
        injectOneFunction("WsChercherErrataReponses", "Webservice - chercherErrataReponses");
        injectOneFunction(NC_REATTRIBUTION_READER, "Effectuer une réattribution");
        injectOneFunction("WsEnvoyerQuestions", "Webservice - envoyerQuestions");
        injectOneFunction("AnEditor", "Webservice - Accès aux questions AN");
        injectOneFunction(ADMINISTRATION_JOURNAL_READER, "Accès à l'espace d'administration du journal");
        injectOneFunction(
            "FDRInstanceAdminMinUpdater",
            "Dossier : modification d'une feuille de route par l'administrateur ministériel"
        );
        injectOneFunction("OrganigrammeUpdater", "Organigramme : mise à jour");
        injectOneFunction("DefaultProfileTemporaryUser", "Profil(s) par défaut pour les utilisateurs temporaires");
        injectOneFunction("ReorientationRedacteurReader", "Effectuer une réorientation pour le profil rédacteur");
        injectOneFunction("WsChercherQuestions", "Webservice - chercherQuestions");
        injectOneFunction(
            FEUILLE_DE_ROUTE_INSTANCE_RESTARTER,
            "Dossier : permet de relancer une instance de feuille de route à l'état terminé"
        );
        injectOneFunction(UTILISATEUR_DELEGATOR, "Gestion des utilisateurs : Délégation des droits");
        injectOneFunction(DOSSIERS_CONNEXES_READER, "Accès à l'onglet Dossiers Connexes");
        injectOneFunction("ProfilCreator", "Gestion des profils : création");
        injectOneFunction("AllowAddPosteAllMinistere", "Autorise la sélection d'un poste d'un autre ministère");
        injectOneFunction("WsEnvoyerReponses", "Webservice - envoyerReponses");
        injectOneFunction(CORBEILLE_SGG_READER, "Corbeille SGG : affichage de tous les ministères");
        injectOneFunction("WsChercherRetourPublication", "Webservice - chercherRetourPublication");
        injectOneFunction(ALLOTISSEMENT_UPDATER, "Allotissement : lier,delier,créer lot");
        injectOneFunction("WsChercherErrataQuestions", "Webservice - chercherErrataQuestions");
        injectOneFunction(
            STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED,
            "Autorise l'accès à l'application quand l'accès restreint est actif"
        );
        injectOneFunction(JOURNAL_READER, "Dossier : affichage de l'onglet journal");
        injectOneFunction("EspaceActualitesReader", "Administration : Accès au sous menu actualités");
        injectOneFunction("AdminAccessMigration", "Administration : Accès au menu Migration");
        injectOneFunction("AdminAccessParam", "Administration : Accès au menu Paramétrages");
    }

    private void injectGroups() {
        injectOneGroup(
            STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME,
            asList(
                STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER,
                STBaseFunctionConstant.ORGANIGRAMME_READER,
                STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER,
                STBaseFunctionConstant.UTILISATEUR_MINISTERE_UPDATER,
                "OrganigrammeReader",
                "OrganigrammeMinistereUpdater",
                "ArbitrageSGGReader",
                "IndexationComplementaireAdminMinistereUpdater",
                "JournalReader",
                "UtilisateurDelegator",
                "AllCorbeilleReader",
                "ReattributionReader",
                "routeManagers",
                ReponsesBaseFunctionConstant.BDC_REQUETES_PREPARAM_READER,
                "FondDossierAdminMinUpdater",
                "OrganigrammeMinistereUpdater",
                "UtilisateurMinistereUpdater",
                "FDRInstanceRestarter",
                "ReorientationReader",
                ReponsesBaseFunctionConstant.DOSSIER_ADMIN_UNLOCKER,
                "InterfaceAccess",
                "UtilisateurReader",
                "FDRModelUpdater",
                "AdministrationJournalReader",
                "DossierRechercheMinUpdater",
                "DossiersConnexesReader",
                "DossierRechercheReader",
                "EspaceAdministrationReader",
                "MinistereRattachementUpdater",
                "RechercheModeleFDR",
                "FDRInstanceAdminMinUpdater",
                "EspaceCorbeilleReader",
                "UtilisateurCreator",
                "AllotissementReader",
                "DossierParapheurAdminMinUpdater",
                "EspaceRechercheReader",
                "DirectionPiloteUpdater",
                "OrganigrammeReader",
                "ProfilReader",
                "FDRInstanceSubstitutor",
                "EspaceSuiviReader",
                "UtilisateurMinistereDeleter",
                "AllotissementUpdater"
            )
        );
        injectOneGroup(SSBaseFunctionConstant.VIGIE_DU_SGG_GROUP_NAME, new ArrayList<>());
        injectOneGroup(
            "Contributeur ministériel",
            asList(
                STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_READER,
                ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_READER,
                ReponsesBaseFunctionConstant.UTILISATEUR_DELEGATOR
            )
        );
        injectOneGroup(
            STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME,
            asList(
                ReponsesBaseFunctionConstant.ALL_CORBEILLE_READER,
                STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER,
                ReponsesBaseFunctionConstant.FDR_INSTANCE_ADMIN_UPDATER,
                STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_READER,
                ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_READER,
                STBaseFunctionConstant.ORGANIGRAMME_UPDATER,
                STBaseFunctionConstant.ORGANIGRAMME_READER,
                STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER,
                STWebserviceConstant.CHERCHER_QUESTIONS,
                STWebserviceConstant.ENVOYER_REPONSES,
                STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION,
                STWebserviceConstant.CONTROLE_PUBLICATION,
                STWebserviceConstant.CHERCHER_ERRATA_REPONSES,
                STWebserviceConstant.ENVOYER_QUESTIONS_ERRATA,
                STWebserviceConstant.CHERCHER_ATTRIBUTIONS,
                STWebserviceConstant.ENVOYER_CHANGEMENT_ETAT,
                STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS,
                STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE,
                STWebserviceConstant.CHERCHER_REPONSES,
                STWebserviceConstant.RECHERCHER_DOSSIER,
                STWebserviceConstant.ENVOYER_REPONSES_ERRATA,
                STWebserviceConstant.ENVOYER_QUESTIONS,
                STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION,
                STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER,
                ReponsesBaseFunctionConstant.DROIT_VISIBILITE_STATS_SGG,
                STWebserviceConstant.PROFIL_AN,
                STWebserviceConstant.PROFIL_SENAT,
                STBaseFunctionConstant.UTILISATEUR_CREATOR,
                STBaseFunctionConstant.UTILISATEUR_UPDATER,
                STBaseFunctionConstant.UTILISATEUR_DELETER,
                SSBaseFunctionConstant.ADMIN_ACCESS_MIGRATION,
                STBaseFunctionConstant.ADMINISTRATION_PROFIL_READER,
                STBaseFunctionConstant.PROFIL_UPDATER,
                "ProfilCreator",
                "DossierDeleter",
                "ReattributionDirecteReader",
                "FDRInstanceSubstitutor",
                "EspaceSuiviReader",
                "DossierParapheurAdminUpdater",
                "FDRInstanceRestarter",
                "ReorientationReader",
                "AllotissementUpdater",
                "CorbeilleSGGReader",
                "EspaceActualitesReader",
                "AdminAccessMigration",
                "AdminAccessParam",
                "AdminFonctionnelEmailReceiver",
                "FDRModelValidator",
                "DossierCreator",
                "InterfaceAccess",
                "FDRObligatoireSGGUpdater",
                "EspaceSupervisionReader",
                "DroitVisibiliteStatsSGG",
                "UtilisateurReader",
                "FeuilleRouteSetter",
                ReponsesBaseFunctionConstant.BDC_REQUETES_PREPARAM_READER,
                "FondDossierAdminUpdater",
                "UtilisateurUpdater",
                "DroitExportMasseStats",
                "OrganigrammeAdminUnlocker",
                "ArbitrageSGGReader",
                "FDRModelUpdater",
                "DossierRechercheUpdater",
                "FDRInstanceAdminUpdater",
                "AdministrationJournalReader",
                "RequetePublisher",
                "SuperviseurSGGUpdater",
                "DossiersConnexesReader",
                "DossierRechercheReader",
                "EspaceAdministrationReader",
                "ParamReferenceReader",
                "ProfilUpdater",
                "JournalReader",
                "UtilisateurDelegator",
                "MinistereRattachementUpdater",
                "AllCorbeilleReader",
                "RechercheModeleFDR",
                "AssembleesParlementairesReader",
                "ArchivageEditor",
                "AllowAddPosteAllMinistere",
                "ReaffectationReader",
                "ReattributionReader",
                "IndexationComplementaireAdminUpdater",
                "ProfilDeleter",
                "UtilisateurCreator",
                "AllotissementReader",
                "routeManagers",
                "FondDossierRepertoireSGG",
                STBaseFunctionConstant.BATCH_READER,
                "OrganigrammeUpdater",
                "EspaceRechercheReader",
                STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED,
                ReponsesBaseFunctionConstant.DOSSIER_CREATOR,
                ReponsesBaseFunctionConstant.DOSSIER_ADMIN_UNLOCKER
            )
        );
        injectOneGroup(
            STBaseFunctionConstant.SUPERVISEUR_SGG_GROUP_NAME,
            asList(
                STBaseFunctionConstant.UTILISATEUR_CREATOR,
                ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER,
                ReponsesBaseFunctionConstant.FOND_DOSSIER_REPERTOIRE_SGG,
                STBaseFunctionConstant.ROUTE_MANAGERS,
                ReponsesBaseFunctionConstant.FDR_OBLIGATOIRE_SGG_UPDATER,
                STBaseFunctionConstant.INTERFACE_ACCESS,
                ReponsesBaseFunctionConstant.BDC_REQUETES_PREPARAM_READER,
                ReponsesBaseFunctionConstant.DROIT_VISIBILITE_STATS_SGG,
                STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_READER,
                ReponsesBaseFunctionConstant.ESPACE_RECHERCHE_READER,
                STBaseFunctionConstant.ORGANIGRAMME_READER,
                ReponsesBaseFunctionConstant.NC_ARBITRAGE_SGG_READER,
                ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_UPDATER,
                ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_MINISTERIEL_UPDATER,
                ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER,
                ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_READER,
                "JournalReader",
                ReponsesBaseFunctionConstant.UTILISATEUR_DELEGATOR,
                ReponsesBaseFunctionConstant.FDR_INSTANCE_SUBSTITUTOR,
                "EspaceSuiviReader",
                ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER,
                STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE,
                STBaseFunctionConstant.FEUILLE_DE_ROUTE_INSTANCE_RESTARTER,
                ReponsesBaseFunctionConstant.NC_REORIENTATION_READER,
                ReponsesBaseFunctionConstant.NC_REATTRIBUTION_READER,
                ReponsesBaseFunctionConstant.ALLOTISSEMENT_UPDATER,
                ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER,
                ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_UPDATER,
                ReponsesBaseFunctionConstant.DOSSIER_ADMIN_UNLOCKER
            )
        );
        List<String> wsFunctions = asList(
            STWebserviceConstant.CHERCHER_QUESTIONS,
            STWebserviceConstant.ENVOYER_REPONSES,
            STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION,
            STWebserviceConstant.CONTROLE_PUBLICATION,
            STWebserviceConstant.CHERCHER_ERRATA_REPONSES,
            STWebserviceConstant.ENVOYER_QUESTIONS_ERRATA,
            STWebserviceConstant.CHERCHER_ATTRIBUTIONS,
            STWebserviceConstant.ENVOYER_CHANGEMENT_ETAT,
            STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS,
            STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE,
            STWebserviceConstant.CHERCHER_REPONSES,
            STWebserviceConstant.RECHERCHER_DOSSIER,
            STWebserviceConstant.ENVOYER_REPONSES_ERRATA,
            STWebserviceConstant.ENVOYER_QUESTIONS,
            STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION,
            STWebserviceConstant.PROFIL_SENAT
        );
        List<String> wsMinsFunctions = new ArrayList<>(wsFunctions);
        wsMinsFunctions.add(STWebserviceConstant.PROFIL_AN);
        wsMinsFunctions.add(STWebserviceConstant.PROFIL_SENAT);
        wsMinsFunctions.add(ReponsesBaseFunctionConstant.DOSSIER_CREATOR);
        injectOneGroup("Webservices Ministériels", wsMinsFunctions);
        List<String> wsANFunctions = new ArrayList<>(wsFunctions);
        wsANFunctions.add(STWebserviceConstant.PROFIL_AN);
        injectOneGroup("Webservices AN", wsANFunctions);
        List<String> wsSENATFunctions = new ArrayList<>(wsFunctions);
        wsSENATFunctions.add(STWebserviceConstant.PROFIL_SENAT);
        injectOneGroup("Webservices SENAT", wsSENATFunctions);
    }

    private DocumentModel injectOneFunction(String id, String description) {
        VocabularyService functionVocabularyService = STServiceLocator.getVocabularyService();

        DocumentModel functionDoc = functionVocabularyService.getNewEntry(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR);

        BaseFunction function = functionDoc.getAdapter(BaseFunction.class);

        function.setDescription(description);
        function.setGroupname(id);

        functionVocabularyService.createDirectoryEntry(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR, functionDoc);

        return functionDoc;
    }

    private DocumentModel injectOneGroup(String groupname, List<String> functions) {
        DocumentModel groupModel = userManager.getBareGroupModel();
        Profile profile = groupModel.getAdapter(Profile.class);
        groupModel.setPropertyValue("groupname", groupname);
        profile.setBaseFunctionList(functions);
        return userManager.createGroup(groupModel);
    }

    private void injectUsers() {
        injectOneUser(
            USER_ADMINSGG,
            PASSWORD,
            asList(POSTE_50002249, POSTE_50000656),
            asList(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)
        );
        injectOneUser(
            USER_ADMINMIN,
            PASSWORD,
            asList(POSTE_50000656),
            asList(STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME)
        );
        injectOneUser(
            USER_VIGIESGG,
            PASSWORD,
            asList(POSTE_50000656),
            asList(SSBaseFunctionConstant.VIGIE_DU_SGG_GROUP_NAME)
        );
        injectOneUser(USER_SUPERVISEUR_SGG, PASSWORD, asList(POSTE_50000656), asList(SUPERVISEUR_SGG_GROUP_NAME));
        injectOneUser(USER_CONTRIBUTEUR, PASSWORD, asList(POSTE_50000656), asList("Contributeur ministériel"));
        injectOneUser(USER_WS_SWORD, PASSWORD, asList(POSTE_50002249), asList("Webservices Ministériels"));
        injectOneUser(USER_WS_AN, PASSWORD, asList(POSTE_50000656), asList("Webservices AN"));
        injectOneUser(USER_WS_SENAT, PASSWORD, asList(POSTE_50000656), asList("Webservices SENAT"));

        // users for test
        injectOneUser("john", "John", "Doe", PASSWORD, emptyList(), emptyList(), true);
        injectOneUser("jane", "Jane", "Doe", PASSWORD, emptyList(), emptyList(), false);
        injectOneUser("cornelius", "Cornelius", "Ábrányi", PASSWORD, emptyList(), emptyList(), false);
    }

    private void injectOneUser(String username, String password, List<String> postes, List<String> groups) {
        injectOneUser(username, username.toLowerCase(), username.toUpperCase(), password, postes, groups, false);
    }

    private void injectOneUser(
        String username,
        String firstName,
        String lastName,
        String password,
        List<String> postes,
        List<String> groups,
        boolean deleted
    ) {
        DocumentModel user = userManager.getBareUserModel();
        STUser stuser = user.getAdapter(STUser.class);
        stuser.setUsername(username);
        stuser.setLastName(lastName);
        stuser.setFirstName(firstName);
        stuser.setEmail(username + "@reponses2ng.com");
        stuser.setPassword(password);
        stuser.setPostes(postes);
        stuser.setGroups(groups);
        stuser.setDateDebut(Calendar.getInstance());
        stuser.setDeleted(deleted);

        userManager.createUser(user);

        // rewrite password a second time
        DocumentModel userup = userManager.getUserModel(username);
        STUser stuserup = userup.getAdapter(STUser.class);
        stuserup.setPassword(password);
        stuserup.setPwdReset(false);
        userManager.updateUser(userup);

        // mise à jour du profil utilisateur
        DocumentModel userProfileDoc = profilService.getProfilUtilisateurDoc(session, username);
        ProfilUtilisateur userProfile = userProfileDoc.getAdapter(ProfilUtilisateur.class);
        userProfile.setUserColumns(newArrayList(UserColumnEnum.LEGISLATURE.toString()));
        session.saveDocument(userProfileDoc);
    }

    /**
     * Vérification : l'opération ne doit jamais avoir été exécutée. En particulier
     * aucun gouvernement ne doit avoir été injecté.
     *
     * @return true si on ne trouve pas de gouvernement.
     */
    private boolean checkNotRun() {
        return gouvernementService.getGouvernementList().isEmpty();
    }

    private void injectOrganigramme() {
        createGouvernements();
        createMinisteres();
        createUnitesStructurelles();
        createPostes();
    }

    private void createMinisteres() {
        // Gouvernement actuel
        premierministre =
            createOneMinistere(
                MEMBRES_GOUVERNEMENT.get(MINISTERE_PM_KEY).getLeft(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_PM_KEY).getMiddle(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_PM_KEY).getRight(),
                gvtNode
            );
        ministereEco =
            createOneMinistere(
                MEMBRES_GOUVERNEMENT.get(MINISTERE_ECONOMIE_KEY).getLeft(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_ECONOMIE_KEY).getMiddle(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_ECONOMIE_KEY).getRight(),
                gvtNode
            );
        ministereAgriculture =
            createOneMinistere(
                MEMBRES_GOUVERNEMENT.get(MINISTERE_AGRICULTURE_KEY).getLeft(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_AGRICULTURE_KEY).getMiddle(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_AGRICULTURE_KEY).getRight(),
                gvtNode
            );
        ministereJustice =
            createOneMinistere(
                MEMBRES_GOUVERNEMENT.get(MINISTERE_JUSTICE_KEY).getLeft(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_JUSTICE_KEY).getMiddle(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_JUSTICE_KEY).getRight(),
                gvtNode
            );
        ministereEducation =
            createOneMinistere(
                MEMBRES_GOUVERNEMENT.get(MINISTERE_EDUCATION_KEY).getLeft(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_EDUCATION_KEY).getMiddle(),
                MEMBRES_GOUVERNEMENT.get(MINISTERE_EDUCATION_KEY).getRight(),
                gvtNode
            );

        newMinistereEco =
            createOneMinistere(
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_ECONOMIE_KEY).getLeft(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_ECONOMIE_KEY).getMiddle(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_ECONOMIE_KEY).getRight(),
                newGvtNode
            );

        newMinistereAgriculture =
            createOneMinistere(
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_AGRICULTURE_KEY).getLeft(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_AGRICULTURE_KEY).getMiddle(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_AGRICULTURE_KEY).getRight(),
                newGvtNode
            );

        newMinistereJustice =
            createOneMinistere(
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_JUSTICE_KEY).getLeft(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_JUSTICE_KEY).getMiddle(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_JUSTICE_KEY).getRight(),
                newGvtNode
            );

        newMinistereEducation =
            createOneMinistere(
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_EDUCATION_KEY).getLeft(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_EDUCATION_KEY).getMiddle(),
                MEMBRES_NEW_GOUVERNEMENT.get(NEW_MINISTERE_EDUCATION_KEY).getRight(),
                newGvtNode
            );
    }

    private void createUnitesStructurelles() {
        directionSGG = createOneDirection(premierministre, "60007663", "Secrétariat général du Gouvernement (SGG)");
        directionEcoBdc = createOneDirection(ministereEco, "50000655", "Bureau des Cabinets (Economie)");
        directionAgriBdc = createOneDirection(ministereAgriculture, "50001443", "Agents BDC Agriculture");
        directionJusticeBdc = createOneDirection(ministereJustice, "50001086", "Cabinet du garde des sceaux");
        directionEducationBdc = createOneDirection(ministereEducation, "50001257", "Bureau des Cabinets (Education)");

        newDirectionEcoBdc = createOneDirection(newMinistereEco, "50001655", "New Bureau des Cabinets (Economie)");
        newDirectionAgriBdc = createOneDirection(newMinistereAgriculture, "50002443", "New Agents BDC Agriculture");
        newDirectionJusticeBdc = createOneDirection(newMinistereJustice, "50002086", "New Cabinet du garde des sceaux");
        newDirectionEducationBdc =
            createOneDirection(newMinistereEducation, "50002257", "New Bureau des Cabinets (Education)");
    }

    private void createPostes() {
        posteAgentBdc = createOnePoste(directionEcoBdc, POSTE_50000656, "Agents BDC (Economie)", false);
        posteAgentDgefp = createOnePoste(directionEcoBdc, "50001188", "Agents DGEFP", false);
        posteDlfEco = createOnePoste(directionEcoBdc, "50001933", "DLF (Economie)", false);
        posteAgentsSecGenEco = createOnePoste(directionEcoBdc, "50001217", "Agents sec. général (Economie)", false);
        posteAdminSolonSgg = createOnePoste(directionEcoBdc, POSTE_50002249, "Administrateur S.O.L.O.N. (SGG)", false);
        posteConsAffEcoPm = createOnePoste(directionEcoBdc, "60000000", "Conseillère Aff. éco et finances PM", false);
        posteSecConsAffEcoPm =
            createOnePoste(directionEcoBdc, "50000612", "Secrétariat Conseillère Aff. éco et finances PM", false);
        posteBdcAgri = createOnePoste(directionAgriBdc, "50001454", "Agents BDC Agriculture", false);
        posteBdcJustice = createOnePoste(directionJusticeBdc, "50001097", "BDC Justice", false);
        posteBdcEcologie = createOnePoste(directionAgriBdc, "50000652", "Agents BDC Ecologie MEDAD", false);

        createOnePoste(directionEducationBdc, "50000834", "Agents BDC Education", false);
        createOnePoste(directionSGG, "50000624", "Département de l'activité normative", true);
        createOnePoste(newDirectionEcoBdc, "50001656", "New Agents BDC (Economie)", false);
        createOnePoste(newDirectionEcoBdc, "50002188", "New Agents DGEFP", false);
        createOnePoste(newDirectionEcoBdc, "50002933", "New DLF (Economie)", false);
        createOnePoste(newDirectionEcoBdc, "50002217", "New Agents sec. général (Economie)", false);
        createOnePoste(newDirectionEcoBdc, "50003249", "New Administrateur S.O.L.O.N. (SGG)", false);
        createOnePoste(newDirectionEcoBdc, "60001000", "New Conseillère Aff. éco et finances PM", false);
        createOnePoste(newDirectionEcoBdc, "50001612", "New Secrétariat Conseillère Aff. éco et finances PM", false);
        createOnePoste(newDirectionAgriBdc, "50002454", "New Agents BDC Agriculture", false);
        createOnePoste(newDirectionJusticeBdc, "50002097", "New BDC Justice", false);
        createOnePoste(newDirectionAgriBdc, "50001652", "New Agents BDC Ecologie MEDAD", false);
        createOnePoste(newDirectionEducationBdc, "50001834", "New Agents BDC Education", false);
    }

    private PosteNode createOnePoste(
        UniteStructurelleNode parentNode,
        String id,
        String label,
        boolean superviseurSGG
    ) {
        PosteNode posteNode = posteService.getBarePosteModel();

        // ajout du parent
        posteNode.setParentUniteId(parentNode.getId());
        // Création
        posteNode.setId(id);
        posteNode.setChargeMissionSGG(false);
        posteNode.setConseillerPM(false);
        posteNode.setPosteBdc(true);
        posteNode.setPosteWs(false);
        posteNode.setDateDebut(Calendar.getInstance());
        posteNode.setLabel(label);
        posteNode.setSuperviseurSGG(superviseurSGG);
        posteService.createPoste(session, posteNode);

        return posteNode;
    }

    private EntiteNode createOneMinistere(
        String id,
        String label,
        Long ordreProtocolaire,
        GouvernementNode parentNode
    ) {
        EntiteNode entiteNode = ministereService.getBareEntiteModel();
        // ajout du parent
        entiteNode.setParentGouvernement(parentNode.getId());

        String infosMinistre = "à renseigner";

        // Création
        entiteNode.setId(id);
        entiteNode.setDateDebut(Calendar.getInstance().getTime());
        entiteNode.setLabel(label);
        entiteNode.setFormule(label);
        entiteNode.setEdition(label);
        entiteNode.setMembreGouvernementCivilite(infosMinistre);
        entiteNode.setMembreGouvernementPrenom(infosMinistre);
        entiteNode.setMembreGouvernementNom(infosMinistre);
        entiteNode.setOrdre(ordreProtocolaire);
        return ministereService.createEntite(entiteNode);
    }

    private UniteStructurelleNode createOneDirection(EntiteNode parentNode, String id, String label) {
        UniteStructurelleNode ustNode = usAndDirectionService.getBareUniteStructurelleModel();

        // ajout du parent
        ustNode.setParentEntiteId(parentNode.getId());
        // Création
        ustNode.setId(id);
        ustNode.setDateDebut(Calendar.getInstance());
        ustNode.setLabel(label);
        ustNode.setType(OrganigrammeType.DIRECTION);
        return usAndDirectionService.createUniteStructurelle(ustNode);
    }

    private void createGouvernements() {
        gvtNode = STServiceLocator.getSTGouvernementService().getBareGouvernementModel();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1); // Hier
        gvtNode.setDateDebut(cal.getTime());
        gvtNode.setLabel("Gouvernement actuel");

        gouvernementService.createGouvernement(gvtNode);

        newGvtNode = STServiceLocator.getSTGouvernementService().getBareGouvernementModel();

        cal = Calendar.getInstance(); // Aujourd'hui
        newGvtNode.setDateDebut(cal.getTime());
        newGvtNode.setLabel("Nouveau gouvernement");

        gouvernementService.createGouvernement(newGvtNode);
    }

    private void injectMailboxes() {
        // Mailbox utilisateurs
        createPersonalMailbox(USER_ADMINSGG);

        // Mailbox de postes
        mailboxPosteService.createPosteMailbox(session, posteAgentBdc.getId(), posteAgentBdc.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteAgentDgefp.getId(), posteAgentDgefp.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteDlfEco.getId(), posteDlfEco.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteAgentsSecGenEco.getId(), posteAgentsSecGenEco.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteAdminSolonSgg.getId(), posteAdminSolonSgg.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteConsAffEcoPm.getId(), posteConsAffEcoPm.getLabel());
        mailboxPosteService.createPosteMailbox(session, posteSecConsAffEcoPm.getId(), posteSecConsAffEcoPm.getLabel());
    }

    private void createPersonalMailbox(String user) {
        // Create the personal mailbox for the user
        final String mailboxType = getCaseManagementDocumentTypeService().getMailboxType();
        DocumentModel mailboxModel = session.createDocumentModel(mailboxType);
        Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

        // Set mailbox properties
        mailbox.setId(mailboxCreator.getPersonalMailboxId(user));
        mailbox.setTitle(user);
        mailbox.setOwner(user);
        mailbox.setType(MailboxConstants.type.personal);

        DocumentModelList res = session.query(
            String.format("SELECT * from %s", MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE)
        );
        if (res == null || res.isEmpty()) {
            throw new NuxeoException("Cannot find any mailbox folder");
        }

        mailboxModel.setPathInfo(
            res.get(0).getPathAsString(),
            STDefaultMailboxCreator.generateMailboxName(mailbox.getTitle())
        );
        session.createDocument(mailboxModel);
        session.save();
    }

    /**
     * Injection de feuilles de route.
     */
    private void injectFdR() {
        createFdR1();
        createFdR2();
        createFdR3();
        createFdR4();
    }

    /**
     * Injection de dossiers.
     *
     * @throws Exception
     */
    private void injectDossiers() {
        injectDossier(
            1L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_AN,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (AN)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 1 (AN)",
            new ArrayList<>(),
            new ArrayList<>()
        );

        injectDossier(
            2L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_AN,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereJustice,
            "Nouveau Centre (AN)",
            "M",
            "Durant",
            "Franck",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (AN)",
            new ArrayList<>(),
            new ArrayList<>()
        );

        injectDossier(
            3L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_AN,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (AN)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 3 (AN)",
            new ArrayList<>(),
            new ArrayList<>()
        );

        injectDossier(
            4L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_AN,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (AN)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 4 (AN)",
            new ArrayList<>(),
            new ArrayList<>()
        );

        injectDossier(
            11L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_AN,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (AN)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 11 (AN)",
            new ArrayList<>(),
            new ArrayList<>()
        );

        injectDossier(
            2L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            20L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            21L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            22L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            23L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            24L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            25L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
        injectDossier(
            26L,
            QuestionTypesEnum.QE,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "",
            VocabularyConstants.ETAT_QUESTION_EN_COURS,
            ministereEco,
            "Nouveau Centre (SENAT)",
            "M",
            "Féron",
            "Hervé",
            "386399",
            "29 - Finistère",
            asList("montant"),
            "administration",
            "activités",
            "",
            new ArrayList<>(),
            "Question 2 (SENAT)",
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    private Dossier injectDossier(
        Long numeroQuestion,
        QuestionTypesEnum typeQuestion,
        String source,
        String texteJoint,
        String etatQuestion,
        EntiteNode ministere,
        String groupePolitique,
        String civiliteAuteur,
        String nomAuteur,
        String prenomAuteur,
        String idMandat,
        String circonscriptionAuteur,
        List<String> analyses,
        String rubrique,
        String rubriqueTa,
        String titreSenat,
        List<String> senatQuestionRenvois,
        String texteQuestion,
        List<String> themesSenat,
        List<String> rubriquesSenat
    ) {
        Question appQuestion = createQuestionModel();
        appQuestion.setNumeroQuestion(numeroQuestion);
        appQuestion.setTypeQuestion(typeQuestion.name());
        appQuestion.setOrigineQuestion(source);
        appQuestion.setLegislatureQuestion(LEGISLATURE);
        appQuestion.setTexteJoint(texteJoint);
        appQuestion.setPageJO(String.valueOf(numeroQuestion));

        Calendar dateReceptionQuestion = GregorianCalendar.getInstance();
        appQuestion.setDateReceptionQuestion(dateReceptionQuestion);

        appQuestion.setIdMinistereInterroge(ministere.getId());
        appQuestion.setTitreJOMinistere(ministere.getLabel());
        appQuestion.setIntituleMinistere(ministere.getEdition());

        // Auteur
        appQuestion.setCiviliteAuteur(civiliteAuteur);
        appQuestion.setNomAuteur(nomAuteur);
        appQuestion.setPrenomAuteur(prenomAuteur);
        appQuestion.setIdMandat(idMandat);
        appQuestion.setCirconscriptionAuteur(circonscriptionAuteur);

        appQuestion.setGroupePolitique(groupePolitique);

        if (!vocabularyService.hasDirectoryEntry(VocabularyConstants.GROUPE_POLITIQUE, groupePolitique)) {
            DocumentModel newGroupePol = vocabularyService.getNewEntry(VocabularyConstants.GROUPE_POLITIQUE);
            PropertyUtil.setProperty(
                newGroupePol,
                STVocabularyConstants.VOCABULARY,
                STVocabularyConstants.COLUMN_ID,
                groupePolitique
            );
            PropertyUtil.setProperty(
                newGroupePol,
                STVocabularyConstants.VOCABULARY,
                STVocabularyConstants.COLUMN_LABEL,
                groupePolitique
            );
            vocabularyService.createDirectoryEntry(VocabularyConstants.GROUPE_POLITIQUE, newGroupePol);
        }

        // gestion du choix entre les deux type de métadonnées d'indexation

        // Recuperation des informations d'indexation AN

        if (DossierConstants.ORIGINE_QUESTION_AN.equals(source)) {
            // indexationAn != null (testé par validateIndexationAN)
            if (analyses != null && !analyses.isEmpty()) {
                appQuestion.setAssNatAnalyses(WsUtils.deduplicateIndexation(analyses));
            }
            appQuestion.setAssNatRubrique(Collections.singletonList(rubrique));

            if (rubriqueTa != null) {
                appQuestion.setAssNatTeteAnalyse(Collections.singletonList(rubriqueTa));
            }
        }

        // Recuperation des informations d'indexation SENAT
        if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(source)) {
            // titreSenat != null (testé par validateIndexatioSenat)
            appQuestion.setSenatQuestionTitre(titreSenat);

            // indexationSenat.getTheme() != null (testé par
            // validateIndexatioSenat)
            appQuestion.setSenatQuestionThemes(themesSenat);

            // indexationSenat.getRubrique() != null (testé par
            // validateIndexatioSenat)
            appQuestion.setSenatQuestionRubrique(rubriquesSenat);
            appQuestion.setSenatQuestionRenvois(senatQuestionRenvois);
        }

        // Texte de la question
        appQuestion.setTexteQuestion(texteQuestion);

        Dossier savedDossier = null;
        Question savedQuestion = null;

        // Le ministère attributaire du dossier est passé en paramètre
        savedDossier =
            createAndSaveDossier(appQuestion, null, ministere.getId(), true, etatQuestion).getAdapter(Dossier.class);
        session.save();

        if (savedDossier != null) {
            savedQuestion = savedDossier.getQuestion(session);
        }
        // état en cours + autre que QE => état répondu
        if (etatQuestion != null && savedQuestion != null) {
            if (savedQuestion.getDocument().getRef() == null || !session.exists(savedQuestion.getDocument().getRef())) {
                session.createDocument(savedQuestion.getDocument());
            }
            if (
                etatQuestion.equals(VocabularyConstants.ETAT_QUESTION_EN_COURS) &&
                !VocabularyConstants.QUESTION_TYPE_QE.equals(typeQuestion.name())
            ) {
                etatQuestion = VocabularyConstants.ETAT_QUESTION_REPONDU;
            }
            savedQuestion.setEtatQuestion(session, etatQuestion, new GregorianCalendar(), delaiQuestionSignalee);
            session.saveDocument(savedQuestion.getDocument());
        }

        return savedDossier;
    }

    protected DocumentModel createAndSaveDossier(
        Question question,
        Reponse reponse,
        String idMinistereAttributaire,
        Boolean startDefaultRoute,
        String etatQuestion
    ) {
        // création du dossier
        Dossier dossier = dossierDistributionService.initDossier(session, question.getNumeroQuestion());

        // Set du ministère attributaire après la création du document
        // Dossier,
        // et avant le lancement de la feuille de
        // route
        dossier.setIdMinistereAttributaireCourant(idMinistereAttributaire);

        // creation de tous les sous elements du dossier
        dossier = dossierDistributionService.createDossier(session, dossier, question, reponse, etatQuestion);

        if (startDefaultRoute) {
            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);
        }

        return session.saveDocument(dossier.getDocument());
    }

    private void createRouteStep(
        DocumentModel parent,
        String type,
        Long deadline,
        PosteNode poste,
        boolean automaticValidation,
        boolean obligatoireMinistere,
        boolean obligatoireSGG
    ) {
        String mailboxId = posteToId(poste);
        String stepName = "etape_" + type + "_" + mailboxId;
        DocumentModel stepDoc = session.createDocumentModel(
            parent.getPathAsString(),
            stepName,
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE
        );
        DublincoreSchemaUtils.setTitle(stepDoc, stepName);
        stepDoc = session.createDocument(stepDoc);
        ReponsesRouteStep routeStep = stepDoc.getAdapter(ReponsesRouteStep.class);
        routeStep.setDeadLine(deadline);
        routeStep.setDistributionMailboxId(mailboxId);
        routeStep.setDocumentRouteId(parent.getId());
        routeStep.setMailSend(false);
        routeStep.setReaffectation(false);
        routeStep.setObligatoireMinistere(obligatoireMinistere);
        routeStep.setObligatoireSGG(obligatoireSGG);
        routeStep.setType(type);
        routeStep.setAutomaticValidation(automaticValidation);
        session.saveDocument(stepDoc);
    }

    private ReponsesFeuilleRoute initFdR(
        String fdrName,
        EntiteNode ministere,
        UniteStructurelleNode direction,
        boolean fdrDefaut
    ) {
        DocumentModel docModel = session.createDocumentModel(
            FDR_FOLDER_PATH,
            fdrName,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE
        );
        DublincoreSchemaUtils.setTitle(docModel, fdrName);
        docModel = session.createDocument(docModel);
        session.save();

        ReponsesFeuilleRoute fdr = docModel.getAdapter(ReponsesFeuilleRoute.class);
        fdr.setFeuilleRouteDefaut(fdrDefaut);
        fdr.setMinistere(ministere.getLabel());
        fdr.setIntituleDirectionPilote(direction.getLabel());

        fdr.setIdDirectionPilote(direction.getId());
        fdr.setMinistere(ministere.getId());

        session.saveDocument(docModel);

        session.save();

        fdr = session.getDocument(fdr.getDocument().getRef()).getAdapter(ReponsesFeuilleRoute.class);

        return fdr;
    }

    private String posteToId(PosteNode poste) {
        return "poste-" + poste.getId();
    }

    // Modèle par défaut
    private void createFdR1() {
        ReponsesFeuilleRoute fdr = initFdR("FDR 1", ministereEco, directionEcoBdc, true);

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION,
            3L,
            posteAgentBdc,
            false,
            false,
            true
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION,
            3L,
            posteAgentDgefp,
            false,
            false,
            false
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_REDACTION,
            3L,
            posteDlfEco,
            false,
            false,
            false
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_VISA,
            3L,
            posteAgentsSecGenEco,
            false,
            false,
            false
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE,
            3L,
            posteAgentBdc,
            false,
            false,
            false
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION,
            3L,
            posteAdminSolonSgg,
            false,
            false,
            false
        );

        DocumentModel paralleleFolder = initParalleleFolder(fdr);

        DocumentModel serieFolder = initSerieFolder(paralleleFolder, "serie1");
        createRouteStep(
            serieFolder,
            VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION,
            3L,
            posteConsAffEcoPm,
            false,
            false,
            false
        );

        serieFolder = initSerieFolder(paralleleFolder, "serie2");
        createRouteStep(
            serieFolder,
            VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM,
            3L,
            posteSecConsAffEcoPm,
            false,
            false,
            false
        );

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE,
            3L,
            posteAdminSolonSgg,
            false,
            false,
            false
        );

        documentRoutingService.lockDocumentRoute(fdr, session);
        documentRoutingService.validateRouteModel(fdr, session);
        documentRoutingService.unlockDocumentRoute(fdr, session);

        session.save();
    }

    private DocumentModel initSerieFolder(DocumentModel paralleleFolder, String name) {
        DocumentModel serieFolder = session.createDocumentModel(
            paralleleFolder.getPathAsString(),
            name,
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE
        );
        DublincoreSchemaUtils.setTitle(serieFolder, name);
        FeuilleRouteStepFolderSchemaUtil.setExecution(serieFolder, FeuilleRouteExecutionType.serial);
        serieFolder = session.createDocument(serieFolder);
        return serieFolder;
    }

    private DocumentModel initParalleleFolder(ReponsesFeuilleRoute fdr) {
        DocumentModel paralleleFolder = session.createDocumentModel(
            fdr.getDocument().getPathAsString(),
            "parallele",
            SSConstant.STEP_FOLDER_DOCUMENT_TYPE
        );
        DublincoreSchemaUtils.setTitle(paralleleFolder, "parallele");
        FeuilleRouteStepFolderSchemaUtil.setExecution(paralleleFolder, FeuilleRouteExecutionType.parallel);
        paralleleFolder = session.createDocument(paralleleFolder);
        return paralleleFolder;
    }

    private void createFdR2() {
        ReponsesFeuilleRoute fdr = initFdR("FDR 2", ministereEco, directionEcoBdc, false);

        // ajout de données d'indexation
        IndexationImpl indexation = (IndexationImpl) fdr.getDocument().getAdapter(ReponsesIndexableDocument.class);
        indexation.setAssNatAnalyses(asList("montant"));
        indexation.setAssNatRubrique(asList("administration"));
        indexation.setAssNatTeteAnalyse(asList("activités"));

        session.saveDocument(fdr.getDocument());
        session.save();

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION,
            3L,
            posteAgentBdc,
            true,
            true,
            true
        );

        DocumentModel paralleleFolder = initParalleleFolder(fdr);

        DocumentModel serieFolder = initSerieFolder(paralleleFolder, "serie1");
        createRouteStep(
            serieFolder,
            VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION,
            3L,
            posteSecConsAffEcoPm,
            false,
            false,
            false
        );

        serieFolder = initSerieFolder(paralleleFolder, "serie2");
        createRouteStep(
            serieFolder,
            VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM,
            3L,
            posteConsAffEcoPm,
            false,
            false,
            false
        );

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE,
            3L,
            posteAdminSolonSgg,
            true,
            true,
            true
        );

        documentRoutingService.lockDocumentRoute(fdr, session);
        documentRoutingService.validateRouteModel(fdr, session);
        documentRoutingService.unlockDocumentRoute(fdr, session);

        session.save();
    }

    private void createFdR3() {
        ReponsesFeuilleRoute fdr = initFdR("FDR 3", ministereAgriculture, directionAgriBdc, false);

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE,
            3L,
            posteBdcAgri,
            true,
            true,
            true
        );

        documentRoutingService.lockDocumentRoute(fdr, session);
        documentRoutingService.validateRouteModel(fdr, session);
        documentRoutingService.unlockDocumentRoute(fdr, session);

        session.save();
    }

    private void createFdR4() {
        ReponsesFeuilleRoute fdr = initFdR("FDR 4", ministereJustice, directionJusticeBdc, false);

        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_AVIS,
            3L,
            posteBdcJustice,
            true,
            true,
            true
        );
        createRouteStep(
            fdr.getDocument(),
            VocabularyConstants.ROUTING_TASK_TYPE_CORRECTION,
            3L,
            posteBdcEcologie,
            true,
            true,
            true
        );

        documentRoutingService.lockDocumentRoute(fdr, session);
        documentRoutingService.validateRouteModel(fdr, session);
        documentRoutingService.unlockDocumentRoute(fdr, session);

        session.save();
    }

    /**
     * Crée un document de type question
     *
     * @return un objet Question
     */
    private Question createQuestionModel() {
        try {
            DocumentModel document = session.createDocumentModel(DossierConstants.QUESTION_DOCUMENT_TYPE);
            return document.getAdapter(Question.class);
        } catch (NuxeoException e) {
            throw new NuxeoException("Cannot create Question", e);
        }
    }
}
