package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.CASE_LINK_SCHEMA;
import static fr.sword.xsd.reponses.Civilite.M;
import static fr.sword.xsd.reponses.Civilite.MLLE;
import static fr.sword.xsd.reponses.Civilite.MME;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultString;

import com.google.common.collect.ImmutableMap;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.Civilite;
import fr.sword.xsd.reponses.Fichier;
import fr.sword.xsd.reponses.IndexationAn;
import fr.sword.xsd.reponses.IndexationSenat;
import fr.sword.xsd.reponses.IndexationSenat.Renvois;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.ResultatTraitement;
import fr.sword.xsd.reponses.TraitementStatut;
import fr.sword.xsd.reponses.Visibilite;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Status;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * @author sly
 *
 */
public class AbstractDelegate {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractDelegate.class);
    private static final Map<String, Civilite> CIVILITE_FROM_STRING = ImmutableMap.of("MLLE", MLLE, "MME", MME, "M", M);

    protected CoreSession session;
    protected VocabularyService vocabularyService = null;

    // *** Messages d'erreurs renvoyés à l'utilisateur
    protected static final String INFO_ACCES_APPLI_KO =
        "Impossible de récupérer les informations d'accès à l'application";
    protected static final String ACCES_APPLI_RESTREINT = "L'accès a l'application est restreint : ";
    protected static final String JETON_NON_RECONNU = "Erreur : Jeton non-reconnu.";
    protected static final String ORIGINE_UTILISATEUR_INCONNUE = "Origine de l'utilisateur du webservice introuvable.";
    protected static final String JETON_NON_ADEQUAT =
        "Erreur : Impossible de récupérer des documents. Veuillez saisir un jeton adéquat.";
    protected static final String JETON_NON_ADEQUAT_DERNIER_JETON_FOURNI =
        JETON_NON_ADEQUAT + " Dernier jeton fourni : %d";
    protected static final String USER_NON_AUTORISE = "Utilisateur non autorisé à utiliser ce service.";
    protected static final String RECUPERATION_INFO_KO = "Impossible de récupérer cette information.";
    protected static final String USER_ISNT_IN_MINISTERE =
        "Vous n'êtes pas autorisé à modifier une question qui n'appartient pas à votre ministere.";

    // *** Messages d'erreurs pour les logs
    protected static final String META_CHERCHER_DOC_ERROR = "Erreur metaChercherDocument";
    protected static final String RECUPERATION_DOS_ERROR = "Erreur de récupération Dossier from Question";
    protected static final String RECUPERATION_QUEST_ERROR = "Erreur de récupération Question";
    protected static final String RECUPERATION_DL_ERROR = "Erreur de récupération Dossier Link";
    protected static final String RECUPERATION_ENTITE_ERROR = "Erreur de récupération noeud entité";
    protected static final String ERRATUM_ERROR = "Erreur d'erratum";

    // *** Constantes pour la map de vérification de webservice
    protected static final String MAP_TRAITEMENT_STATUT = "TRAITEMENT";
    protected static final String MAP_MESSAGE_ERREUR = "MESSAGE_ERREUR";

    /**
     *
     */
    protected AbstractDelegate(CoreSession documentManager) {
        this.session = documentManager;
        vocabularyService = ReponsesServiceLocator.getVocabularyService();
    }

    /**
     * cree un document de type question
     *
     * @return
     *
     */
    protected Question createQuestionModel() {
        try {
            DocumentModel document = session.createDocumentModel(DossierConstants.QUESTION_DOCUMENT_TYPE);
            return document.getAdapter(Question.class);
        } catch (NuxeoException e) {
            throw new NuxeoException("Cannot create Question", e);
        }
    }

    protected Reponse createReponseModel() {
        try {
            DocumentModel document = session.createDocumentModel(DossierConstants.REPONSE_DOCUMENT_TYPE);
            return document.getAdapter(Reponse.class);
        } catch (NuxeoException e) {
            throw new NuxeoException("Cannot create Reponse", e);
        }
    }

    protected DocumentModel createAndSaveDossier(
        Question question,
        Reponse reponse,
        String idMinistereAttributaire,
        Boolean startDefaultRoute,
        String etatQuestion
    )
        throws Exception {
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        // création du dossier
        Dossier dossier = dossierDistributionService.initDossier(session, question.getNumeroQuestion());

        // Set du ministère attributaire après la création du document Dossier, et avant le lancement de la feuille de
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

    /**
     * remove "blank*<![CDATA[" and "]]&gt; .*" on the input string
     *
     * @param cdataStr
     * @return
     * @throws IllegalStateException
     *             if the end part of the CDATA is missing
     */
    protected static String stripCDATA(String cdataStr) {
        if (cdataStr != null) {
            cdataStr = cdataStr.trim();
            if (cdataStr.startsWith("<![CDATA[")) {
                cdataStr = cdataStr.substring(9);
                int i = cdataStr.indexOf("]]&gt;");
                if (i == -1) {
                    throw new IllegalStateException("argument starts with <![CDATA[ but cannot find pairing ]]&gt;");
                }
                cdataStr = cdataStr.substring(0, i);
            }
        }
        return cdataStr;
    }

    /**
     * Retourne true si les questions ont le même quadruplet législature/numéro/source/type.
     *
     * @param qu1
     * @param qu2
     * @return
     */
    protected boolean idQuestionsAreEquals(QuestionId qu1, QuestionId qu2) {
        return (
            qu1.getLegislature() == qu2.getLegislature() &&
            qu1.getNumeroQuestion() == qu2.getNumeroQuestion() &&
            qu1.getSource().equals(qu2.getSource()) &&
            qu1.getType().equals(qu2.getType())
        );
    }

    /**
     * Extrait un {@link Auteur} d'une {@link Question}.
     *
     * @param appQuestion
     * @return
     */
    protected static Auteur getAuteurFromQuestion(Question appQuestion) {
        Auteur auteur = new Auteur();

        auteur.setGrpPol(defaultString(appQuestion.getGroupePolitique()));
        auteur.setCivilite(CIVILITE_FROM_STRING.getOrDefault(appQuestion.getCiviliteAuteur(), M));
        auteur.setIdMandat(defaultString(appQuestion.getIdMandat()));
        auteur.setNom(defaultString(appQuestion.getNomAuteur()));
        auteur.setPrenom(defaultString(appQuestion.getPrenomAuteur()));
        auteur.setCirconscription(defaultString(appQuestion.getCirconscriptionAuteur()));

        return auteur;
    }

    protected IndexationAn getIndexationAnFromQuestion(Question appQuestion) {
        // IndexationAn
        // ///////////////////
        IndexationAn indexationAn = new IndexationAn();
        List<String> rubriqueList = appQuestion.getAssNatRubrique();
        if (rubriqueList != null && rubriqueList.size() > 0) {
            indexationAn.setRubrique(rubriqueList.get(0));
        }
        List<String> taList = appQuestion.getAssNatTeteAnalyse();
        if (taList != null && taList.size() > 0) {
            indexationAn.setRubriqueTa(taList.get(0));
        } else {
            // Correction problème rubrique_ta pour agriculture. Si c'est null on rajoute quand même quelque chose de
            // blanc
            indexationAn.setRubriqueTa("");
        }
        List<String> analyseList = appQuestion.getAssNatAnalyses();
        if (analyseList != null) {
            indexationAn.getAnalyse().addAll(analyseList);
        }
        return indexationAn;
    }

    protected IndexationSenat getIndexationSenatFromQuestion(Question appQuestion) {
        IndexationSenat indexationSenat = new IndexationSenat();

        List<String> renvoisSenat = appQuestion.getSenatQuestionRenvois();
        if (renvoisSenat != null && !renvoisSenat.isEmpty()) {
            Renvois renvois = new Renvois();
            renvois.getRenvoi().addAll(renvoisSenat);
            indexationSenat.setRenvois(renvois);
        }

        List<String> senatQuestionRubrique = appQuestion.getSenatQuestionRubrique();
        if (senatQuestionRubrique != null) {
            indexationSenat.getRubrique().addAll(senatQuestionRubrique);
        }

        List<String> senatQuestionThemes = appQuestion.getSenatQuestionThemes();
        if (senatQuestionThemes != null) {
            indexationSenat.getTheme().addAll(senatQuestionThemes);
        }

        return indexationSenat;
    }

    protected Ministre getMinistereDepotFromQuestion(Question appQuestion) {
        Ministre ministreDepot = new Ministre();
        String idMinistereDepot = appQuestion.getIdMinistereInterroge();
        if (idMinistereDepot != null) {
            ministreDepot.setId(Integer.parseInt(idMinistereDepot));
        }
        ministreDepot.setIntituleMinistere(appQuestion.getIntituleMinistere());
        ministreDepot.setTitreJo(appQuestion.getTitreJOMinistere());
        return ministreDepot;
    }

    protected Ministre getMinistereAttributaireFromDossier(Dossier dossier) {
        Ministre ministereAttributaire = new Ministre();
        String idMinistereAttributaire = dossier.getIdMinistereAttributaireCourant();
        if (idMinistereAttributaire != null) {
            ministereAttributaire.setId(Integer.parseInt(idMinistereAttributaire));

            EntiteNode entiteNode = null;
            try {
                entiteNode = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistereAttributaire);
            } catch (NuxeoException e) {
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC,
                    "ID : " + idMinistereAttributaire + " / " + e.getMessage()
                );
                LOGGER.debug(session, STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC, idMinistereAttributaire, e);
            }
            if (entiteNode != null) {
                ministereAttributaire.setIntituleMinistere(entiteNode.getLabel());
                ministereAttributaire.setTitreJo(entiteNode.getEdition());
            }
        }

        return ministereAttributaire;
    }

    protected fr.dila.reponses.api.cases.Question getQuestionFromQuestionId(QuestionId questionId) {
        return Optional
            .of(questionId)
            .filter(AbstractDelegate::isValidIdQuestion)
            .map(
                validQuestionId ->
                    ReponsesServiceLocator
                        .getDossierDistributionService()
                        .retrieveDocumentQuestionId(
                            session,
                            validQuestionId.getNumeroQuestion(),
                            validQuestionId.getType().name(),
                            validQuestionId.getSource().name(),
                            validQuestionId.getLegislature()
                        )
            )
            .filter(Objects::nonNull)
            .map(docQuestionId -> session.getDocument(new IdRef(docQuestionId)))
            .map(questionDoc -> questionDoc.getAdapter(fr.dila.reponses.api.cases.Question.class))
            .orElseThrow(
                () ->
                    new NuxeoException(
                        "Le dossier de la question " +
                        questionId.getNumeroQuestion() +
                        " de type " +
                        questionId.getType() +
                        " provenant de " +
                        questionId.getSource() +
                        " à la legislature " +
                        questionId.getLegislature() +
                        ", est introuvable"
                    )
            );
    }

    protected Dossier getDossierFromQuestionId(QuestionId questionid) {
        fr.dila.reponses.api.cases.Question appQuestion = getQuestionFromQuestionId(questionid);
        return appQuestion.getDossier(session);
    }

    protected static Set<String> getMinisteresIdSetFromLogin(CoreSession session) {
        return Optional
            .ofNullable(session.getPrincipal())
            .map(SSPrincipal.class::cast)
            .map(SSPrincipal::getMinistereIdSet)
            .orElseGet(Collections::emptySet);
    }

    protected static String getFirstMinistereLoginFromSession(CoreSession session) {
        return getMinisteresIdSetFromLogin(session).stream().findFirst().orElse(null);
    }

    /**
     * check if an IdQuestion is valid
     *
     * @param idQuestion
     *            the object to test
     * @return true is the id is valid
     */
    protected static Boolean isValidIdQuestion(QuestionId idQuestion) {
        return (
            idQuestion != null &&
            idQuestion.getNumeroQuestion() > 0 &&
            idQuestion.getSource() != null &&
            idQuestion.getType() != null
        );
    }

    /**
     * Si on n'a pas de groupe ou bien si dans nos groupes on n'a pas le webservice pas le droit d'accéder.
     */
    protected static boolean hasRight(CoreSession session, String webservice) {
        List<String> userGroupList = session.getPrincipal().getGroups();
        return userGroupList != null && userGroupList.contains(webservice);
    }

    protected boolean hasRightAndOrigineUtilisateur(CoreSession session, String webservice, QuestionId questionId) {
        List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
        // si on n'a pas de groupe ou bien si dans nos groupes on n'a pas le webservice pas le droit d'accéder
        if (userGroupList == null || !userGroupList.contains(webservice)) {
            return false;
        }
        if (questionId == null) {
            return true;
        }
        if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)) {
            questionId.setSource(QuestionSource.AN);
        } else if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
            questionId.setSource(QuestionSource.SENAT);
        }
        return true;
    }

    protected DossierLink getDossierLinkFromDossier(Dossier dossier, String routingTask) {
        QuestionId idQuestion = WsUtils.getQuestionIdFromQuestion(dossier.getQuestion(session));

        // Récupération des DossierLink du dossier
        StringBuilder query = new StringBuilder("SELECT * FROM ")
            .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
            .append(" WHERE ")
            .append(CaseLinkConstants.CASE_DOCUMENT_ID_FIELD + "=\"" + dossier.getDocument().getId() + "\"")
            .append(" AND ")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
            .append(" = 'todo' ")
            .append(" AND drl:routingTaskType =\"" + routingTask + "\"");

        DocumentModelList dossierLinkModelList = null;
        try {
            dossierLinkModelList =
                createUnrestrictedQueryRunner(
                    query.toString(),
                    DOSSIER_REPONSES_LINK_SCHEMA,
                    CASE_LINK_SCHEMA,
                    ACTIONABLE_CASE_LINK_SCHEMA
                )
                    .findAll();
        } catch (NuxeoException e) {
            throw new NuxeoException(
                "Impossible de trouver l'étape 'pour rédaction interfacée' pour la question " +
                idQuestion.getNumeroQuestion(),
                e
            );
        }

        if (dossierLinkModelList == null || dossierLinkModelList.isEmpty()) {
            return null;
        }

        return dossierLinkModelList.get(0).getAdapter(DossierLink.class);
    }

    protected UnrestrictedQueryRunner createUnrestrictedQueryRunner(String query, String... schemas) {
        return new UnrestrictedQueryRunner(session, query, schemas);
    }

    protected UnrestrictedGetDocumentRunner createUnrestrictedGetDocumentRunner() {
        return new UnrestrictedGetDocumentRunner(session);
    }

    protected UnrestrictedGetDocumentRunner createUnrestrictedGetDocumentRunner(String schemas) {
        return new UnrestrictedGetDocumentRunner(session, schemas);
    }

    protected JetonServiceDto metaChercherDocument(
        String login,
        String jetonRecu,
        String webservice,
        List<QuestionId> questionIds
    )
        throws NumberFormatException {
        if (isNotEmpty(questionIds)) {
            if (jetonRecu != null) {
                throw new NuxeoException(
                    "Paramètres de requêtes incorrects : présence simultanée du jeton et des identifiants de questions."
                );
            }

            JetonServiceDto dto = new JetonServiceDto();
            dto.setDocumentList(
                questionIds.stream().map(this::getQuestionFromQuestionId).map(Question::getDocument).collect(toList())
            );
            return dto;
        }

        try {
            Long jeton = Long.parseLong(jetonRecu);

            return ReponsesServiceLocator.getJetonService().getDocuments(session, login, jeton, webservice);
        } catch (NuxeoException e) {
            throw new NuxeoException("Erreur lors du retrait des derniers documents", e);
        }
    }

    protected ResultatTraitement createFichiers(List<Fichier> fichiers, Dossier dossier) {
        ResultatTraitement resultatTraitement = new ResultatTraitement();
        resultatTraitement.setStatut(TraitementStatut.OK);

        DocumentModel fddModel = dossier.getFondDeDossier(session).getDocument();

        for (Fichier fichier : fichiers) {
            if (!fichier.isADuContenu()) {
                resultatTraitement.setStatut(TraitementStatut.KO);
                resultatTraitement.setMessageErreur(
                    "Impossible de répondre à une question n'étant pas à l'étape 'rédaction interfacée'"
                );
                return resultatTraitement;
            }

            String fileName = fichier.getNom();
            Visibilite visibilite = fichier.getVisibilite();

            // On vérifie si le nom du fichier est bien formé
            int pos = fileName.lastIndexOf('.');

            // Extraction du prefix&& suffixe
            String prefix = fileName.substring(0, pos - 1);
            String suffix = fileName.substring(pos + 1, fileName.length());
            if (suffix == null || suffix.length() == 0) {
                resultatTraitement.setStatut(TraitementStatut.KO);
                resultatTraitement.setMessageErreur(
                    "Nom de fichier joint incorrect : extension non trouvée dans l'intitulé du fichier."
                );
                return resultatTraitement;
            }

            File tempFile = null;
            FileOutputStream fos = null;
            DataOutputStream dos = null;
            try {
                // Création d'un fichier temporaire
                tempFile = File.createTempFile(prefix, suffix);
                fos = new FileOutputStream(tempFile);
                dos = new DataOutputStream(fos);
                dos.write(fichier.getContenu());
                Blob blob = BlobUtils.createSerializableBlob(new FileInputStream(tempFile), fileName, null);
                if (blob == null || fileName.length() == 0) {
                    resultatTraitement.setStatut(TraitementStatut.KO);
                    resultatTraitement.setMessageErreur("Fichier vide");
                    return resultatTraitement;
                }

                // Détermination de la visibilité
                String visibiliteLevel = null;
                if (Visibilite.MINISTERE.equals(visibilite)) {
                    visibiliteLevel = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL;
                } else if (Visibilite.MINISTERE_SGG.equals(visibilite)) {
                    visibiliteLevel = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL;
                } else if (Visibilite.PARLEMENT.equals(visibilite) || Visibilite.ALL.equals(visibilite)) {
                    visibiliteLevel = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL;
                } else {
                    resultatTraitement.setStatut(TraitementStatut.KO);
                    resultatTraitement.setMessageErreur("Visibilite incorrecte.");
                    return resultatTraitement;
                }

                // Creation d'un fond de dossier element
                FondDeDossierService fdds = ReponsesServiceLocator.getFondDeDossierService();
                fdds.createFondDeDossierFile(session, fddModel, fileName, visibiliteLevel, blob);
            } catch (FileNotFoundException e) {
                resultatTraitement.setStatut(TraitementStatut.KO);
                resultatTraitement.setMessageErreur("Fichier introuvable.");
                return resultatTraitement;
            } catch (IOException e) {
                resultatTraitement.setStatut(TraitementStatut.KO);
                resultatTraitement.setMessageErreur("L'ajout de fichier joint a rencontré une erreur.");
                return resultatTraitement;
            } catch (NuxeoException e) {
                resultatTraitement.setStatut(TraitementStatut.KO);
                resultatTraitement.setMessageErreur("L'ajout de fichier joint a rencontré une erreur.");
                return resultatTraitement;
            } finally {
                if (tempFile != null) {
                    tempFile.delete();
                }
                try {
                    if (dos != null) {
                        dos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    // NOP
                }
            }
        }
        return null;
    }

    /**
     * Checks if the current User Transaction is alive.
     */
    protected static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Logge l'action effectue par webservice dans le journal d'administration.
     *
     *
     */
    protected void logWebServiceAction(String name, String comment) {
        // log du webservice dans le journal d'administration
        try {
            STServiceLocator.getJournalService().journaliserActionAdministration(session, name, comment);
        } catch (Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_EVENT_TEC,
                "Erreur lors de l'envoi d'un log pour le journal technique. Nom de l'event" +
                name +
                " / " +
                e.getMessage()
            );
            LOGGER.debug(
                session,
                STLogEnumImpl.FAIL_SEND_EVENT_TEC,
                "Erreur lors de l'envoi d'un log pour le journal technique. Nom de l'event" + name,
                e
            );
        }
    }

    /**
     * Log l'action effectuée par webservice dans le journal d'administration en lien avec le dossier concerné
     *
     * @param name
     *            nom de l'évènement de log
     * @param comment
     *            commentaire qui sera rédigé dans le log
     * @param dossier
     *            dossier auquel on ajoute le log
     */
    protected void logWebServiceActionDossier(String name, String comment, Dossier dossier) {
        // log du webservice dans le journal du dossier
        try {
            STServiceLocator
                .getJournalService()
                .journaliserActionAdministration(session, dossier.getDocument(), name, comment);
        } catch (Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_EVENT_TEC,
                "Erreur lors de l'envoi d'un log pour le journal technique. Nom de l'event" +
                name +
                " / " +
                e.getMessage()
            );
            LOGGER.debug(
                session,
                STLogEnumImpl.FAIL_SEND_EVENT_TEC,
                "Erreur lors de l'envoi d'un log pour le journal technique. Nom de l'event" + name,
                e
            );
        }
    }

    /**
     * Vérifie si l'application est en accès restreint.
     *
     * @return une map vide si tout est OK, sinon deux éléments : Statut et Message d'erreur. La clé du statut est la
     *         constante MAP_TRAITEMENT_STATUT La clé du message d'erreur est la constante MAP_MESSAGE_ERREUR
     *
     */
    protected Map<String, Object> checkWebserviceAccess() {
        Map<String, Object> results = new HashMap<String, Object>();

        try {
            SSPrincipal principal = (SSPrincipal) session.getPrincipal();
            EtatApplication etatApplication = STServiceLocator
                .getEtatApplicationService()
                .getEtatApplicationDocument(session);
            if (
                etatApplication.getRestrictionAcces() &&
                !principal.isMemberOf(STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)
            ) {
                results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);
                results.put(MAP_MESSAGE_ERREUR, ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
                return results;
            }
        } catch (NuxeoException e) {
            results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);
            results.put(MAP_MESSAGE_ERREUR, INFO_ACCES_APPLI_KO);
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, INFO_ACCES_APPLI_KO + " / " + e.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, INFO_ACCES_APPLI_KO, e);
            return results;
        }

        return results;
    }

    /**
     * Récupère le JetonServiceDto. Si une exception est levée, le statut et message d'erreur sont ajouté à la map
     * results
     *
     * @param idProfilUtilisateur
     * @param jetonRecu
     * @param webservice
     * @param questionsIds
     * @param results
     *            la map qui contient le resultat du traitement. Elle est nettoyée en début de traitement. En fin de
     *            traitement, elle est vide si tout est OK, sinon deux éléments : Statut et Message d'erreur. La clé du
     *            statut est la constante MAP_TRAITEMENT_STATUT La clé du message d'erreur est la constante
     *            MAP_MESSAGE_ERREUR
     * @return
     */
    protected JetonServiceDto getJetonDTOForWebservice(
        String idProfilUtilisateur,
        String jetonRecu,
        String webservice,
        List<QuestionId> questionsIds,
        Map<String, Object> results
    ) {
        results.clear();
        JetonServiceDto dto = null;
        try {
            dto = metaChercherDocument(idProfilUtilisateur, jetonRecu, webservice, questionsIds);
        } catch (NumberFormatException e) {
            results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);
            results.put(MAP_MESSAGE_ERREUR, JETON_NON_RECONNU);
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, JETON_NON_RECONNU + " / " + e.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_JETON_TEC, JETON_NON_RECONNU, e);
        } catch (IllegalArgumentException e) {
            results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);
            results.put(MAP_MESSAGE_ERREUR, e.getMessage());
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, META_CHERCHER_DOC_ERROR + " / " + e.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_JETON_TEC, META_CHERCHER_DOC_ERROR, e);
        } catch (NuxeoException e) {
            results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);
            results.put(MAP_MESSAGE_ERREUR, e.getMessage());
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, META_CHERCHER_DOC_ERROR + " / " + e.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_JETON_TEC, META_CHERCHER_DOC_ERROR, e);
        }
        if (dto == null && results.isEmpty()) {
            results.put(MAP_TRAITEMENT_STATUT, TraitementStatut.KO);

            JetonService jetonService = STServiceLocator.getJetonService();
            Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, idProfilUtilisateur, webservice);

            if (numeroJetonSuivant < 0) {
                numeroJetonSuivant = 0L;
            }
            results.put(MAP_MESSAGE_ERREUR, String.format(JETON_NON_ADEQUAT_DERNIER_JETON_FOURNI, numeroJetonSuivant));
        }
        return dto;
    }
}
