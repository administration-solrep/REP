package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.core.cases.flux.RenouvellementImpl;
import fr.dila.reponses.core.cases.flux.SignalementImpl;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.dossier.STDossierImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du dossier Réponses.
 *
 * @author jtremeaux
 */
public class DossierImpl extends STDossierImpl implements Dossier {
    private static final long serialVersionUID = 6160682333116646611L;

    private static final STLogger LOGGER = STLogFactory.getLog(DossierImpl.class);

    private static final String QUESTION_DOCUMENT_NAME = "Question";

    private static final String QUERY_HISTORIQUES =
        "SELECT h.ecm:uuid AS id FROM " +
        ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_TYPE +
        " as h WHERE h.ecm:parentId" +
        " = ? ORDER BY h." +
        ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOC_SCHEMA_PREFIX +
        ":" +
        ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DATE_PROPERTY;

    /**
     * Constructeur de DossierImpl.
     *
     * @param dossierDoc
     *            Document dossier
     */
    public DossierImpl(DocumentModel dossierDoc) {
        super(dossierDoc);
    }

    @Override
    public String getLastDocumentRoute() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
        );
    }

    @Override
    public void setLastDocumentRoute(String lastDocumentRoute) {
        PropertyUtil.setProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY,
            lastDocumentRoute
        );
    }

    /*
     * get the FondDeDossier item referenced in the Dossier (non-Javadoc)
     * @see fr.dila.reponses.api.cases.Dossier#getFondDeDossier(org.nuxeo.ecm.core .api.CoreSession)
     */
    @Override
    public FondDeDossier getFondDeDossier(CoreSession session) {
        try {
            String fddid = getFondDeDossierId();
            DocumentModel fddDoc = session.getDocument(new IdRef(fddid));
            return fddDoc.getAdapter(FondDeDossier.class);
        } catch (NuxeoException e) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_GET_FDD_TEC);
        }
        return null;
    }

    @Override
    public Dossier setDossierProperties(CoreSession session) {
        // On affecte un titre par défaut au dossier
        Long identifiantQuestion = getNumeroQuestion();
        String title = "question sans n° question";
        if (identifiantQuestion != null) {
            title = "Dossier n°" + identifiantQuestion;
        }
        DublincoreSchemaUtils.setTitle(document, title);

        LOGGER.debug(STLogEnumImpl.UPDATE_METADONNEE_TEC, "Les métadonnées sont mises à jour!");
        // retrieve current dossier
        return this;
    }

    @Override
    public FondDeDossier createFondDeDossier(CoreSession session) {
        try {
            final String fondDeDossierTitre = "FondDeDossier";

            DocumentModel newFondDeDossierModel = session.createDocumentModel(
                getDocument().getPath().toString(),
                fondDeDossierTitre,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_DOCUMENT_TYPE
            );

            // On affecte un titre par défaut au fond de Dossier
            Long identifiantQuestion = getNumeroQuestion();
            String title = "fondDeDossier sans n° question";
            if (identifiantQuestion != null) {
                title = "Fond De Dossier n°" + identifiantQuestion;
            }

            DublincoreSchemaUtils.setTitle(newFondDeDossierModel, title);

            // save the DocumentModel with type FondDossier
            DocumentModel fddModel = session.createDocument(newFondDeDossierModel);
            session.save();
            FondDeDossier fondDeDossier = fddModel.getAdapter(FondDeDossier.class);

            return fondDeDossier;
        } catch (NuxeoException e) {
            throw new ReponsesException("Fond De Dossier creation fail.", e);
        }
    }

    @Override
    public Long getNumeroQuestion() {
        return getLongProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_NUMERO_QUESTION);
    }

    @Override
    public void setNumeroQuestion(Long numeroQuestion) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_NUMERO_QUESTION, numeroQuestion);
    }

    @Override
    public String getIdMinistereAttributaireCourant() {
        return getStringProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
        );
    }

    @Override
    public void setIdMinistereAttributaireCourant(String idMinistere) {
        setProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT,
            idMinistere
        );
    }

    @Override
    public String getIdMinistereAttributairePrecedent() {
        return getStringProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_PRECEDENT
        );
    }

    @Override
    public void setIdMinistereAttributairePrecedent(String idMinistere) {
        setProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_PRECEDENT,
            idMinistere
        );
    }

    /**
     * SPL : access a la question depuis l'id de question
     */
    @Override
    public Question getQuestion(CoreSession session) {
        String questionId = getQuestionId();
        if (questionId != null) {
            DocumentModel questionDocumentModel = session.getDocument(new IdRef(questionId));
            if (questionDocumentModel != null) {
                return questionDocumentModel.getAdapter(Question.class);
            }
        }
        return null;
    }

    @Override
    public Reponse getReponse(CoreSession session) {
        String reponseId = getReponseId();
        if (reponseId != null) {
            DocumentModel reponseDocumentModel = session.getDocument(new IdRef(reponseId));
            if (reponseDocumentModel != null) {
                return reponseDocumentModel.getAdapter(Reponse.class);
            }
        }
        return null;
    }

    @Override
    public Boolean isArchivable(CoreSession session) {
        Calendar datePublicationJO = getQuestion(session).getDatePublicationJO();
        STParametreService paramService = STServiceLocator.getSTParametreService();
        String duaDelai = paramService.getParametreValue(session, STParametreConstant.DELAI_CONSERVATION_DONNEES);
        Calendar dua = DateUtil.removeMonthsToNow(Integer.parseInt(duaDelai));
        if (datePublicationJO != null && datePublicationJO.before(dua)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canEliminate(CoreSession session) {
        return BooleanUtils.isTrue(isArchivable(session)) && getListeElimination() == null;
    }

    @Override
    public String getDossierLot() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_NOM_DOSSIER_LOT
        );
    }

    @Override
    public void setDossierLot(String idDossierLot) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_NOM_DOSSIER_LOT, idDossierLot);
    }

    @Override
    public Boolean getEtapeRedactionAtteinte() {
        return PropertyUtil.getBooleanProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ETAPE_REDACTION_ATTEINTE
        );
    }

    @Override
    public void setEtapeRedactionAtteinte(Boolean isEtapeRedactionAtteinte) {
        setProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ETAPE_REDACTION_ATTEINTE,
            isEtapeRedactionAtteinte
        );
    }

    @Override
    public Boolean getEtapeSignatureAtteinte() {
        return PropertyUtil.getBooleanProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ETAPE_SIGNATURE_ATTEINTE
        );
    }

    @Override
    public void setEtapeSignatureAtteinte(Boolean isEtapeSignatureAtteinte) {
        setProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ETAPE_SIGNATURE_ATTEINTE,
            isEtapeSignatureAtteinte
        );
    }

    @Override
    public Long getReaffectionCount() {
        return PropertyUtil.getLongProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_REAFFECTATION_COUNT
        );
    }

    @Override
    public void setReaffectionCount(Long reaffectionCount) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_REAFFECTATION_COUNT, reaffectionCount);
    }

    @Override
    public String getIdMinistereReattribution() {
        return getStringProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_ID_MINISTERE_REATTRIBUTION);
    }

    @Override
    public void setIdMinistereReattribution(String idMinistereReattribution) {
        setProperty(
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_REATTRIBUTION,
            idMinistereReattribution
        );
    }

    @Override
    public String getQuestionId() {
        return getStringProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID);
    }

    @Override
    public void setQuestionId(String questionId) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID, questionId);
    }

    @Override
    public String getReponseId() {
        return getStringProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID);
    }

    @Override
    public void setReponseId(String reponseId) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID, reponseId);
    }

    @Override
    public String getFondDeDossierId() {
        return getStringProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_FONDDEDOSSIER_ID);
    }

    @Override
    public void setFondDeDossierId(String fddId) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_DOCUMENT_FONDDEDOSSIER_ID, fddId);
    }

    @Override
    public Boolean isActive(CoreSession session) {
        if (!hasFeuilleRoute()) {
            // Pas de feuille de route, donc pas de feuille de route running
            return false;
        }
        String routeId = this.getLastDocumentRoute();
        DocumentModel routeDoc = session.getDocument(new IdRef(routeId));
        FeuilleRoute route = routeDoc.getAdapter(FeuilleRoute.class);
        return route.isRunning();
    }

    @Override
    public Dossier getDossier(CoreSession session) {
        return this;
    }

    @Override
    public void setListeElimination(String listeEliminationId) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_LISTE_ELIMINATION, listeEliminationId);
    }

    @Override
    public String getListeElimination() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_LISTE_ELIMINATION
        );
    }

    @Override
    public void createQuestion(CoreSession session, Question questionObject, String etatQuestion) {
        // titre de la question dans le classement nuxeo
        String titreQuestion = QUESTION_DOCUMENT_NAME;

        // create document model question
        DocumentModel question = session.createDocumentModel(
            getDocument().getPathAsString(),
            titreQuestion,
            DossierConstants.QUESTION_DOCUMENT_TYPE
        );

        // path info
        LOGGER.debug(STLogEnumImpl.GET_METADONNEE_TEC, "question path" + question.getPathAsString());
        LOGGER.debug(STLogEnumImpl.GET_METADONNEE_TEC, "question name" + question.getName());

        // On affecte un titre par défaut à la question => voir si pertinent
        Long identifiantQuestion = questionObject.getNumeroQuestion();
        if (identifiantQuestion != null) {
            titreQuestion = "Question n°" + identifiantQuestion;
        }
        DublincoreSchemaUtils.setTitle(question, titreQuestion);

        // set all question properties
        Question questionDoc = question.getAdapter(Question.class);
        question = questionDoc.getQuestionMetadata(questionObject);

        // create question in session
        question = session.createDocument(question);

        // save questionId in dossier
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        String delaiQuestionSignalee = parametreService.getParametreValue(
            session,
            STParametreConstant.DELAI_QUESTION_SIGNALEE
        );

        saveQuestionId(session, question.getId());
        questionDoc = question.getAdapter(Question.class);
        questionDoc.setEtatQuestion(session, etatQuestion, new GregorianCalendar(), delaiQuestionSignalee);

        session.save();
        LOGGER.debug(STLogEnumImpl.GET_METADONNEE_TEC, "doc question id : " + question.getId());
    }

    @Override
    public DocumentModel createReponse(CoreSession session, Long numeroQuestion, Reponse reponseData) {
        String titreReponse = "Reponse";

        DocumentModel reponse = session.createDocumentModel(
            getDocument().getPath().toString(),
            titreReponse,
            DossierConstants.REPONSE_DOCUMENT_TYPE
        );

        // set reponse titre
        if (numeroQuestion != null) {
            titreReponse = "Reponse n°" + numeroQuestion;
        }

        DublincoreSchemaUtils.setTitle(reponse, titreReponse);
        Reponse reponseAdapted = reponse.getAdapter(Reponse.class);
        if (reponseData != null) {
            reponseAdapted.setDateJOreponse(reponseData.getDateJOreponse());
            reponseAdapted.setIdAuteurReponse(reponseData.getIdAuteurReponse());
            reponseAdapted.setNumeroJOreponse(reponseData.getNumeroJOreponse());
            reponseAdapted.setPageJOreponse(reponseData.getPageJOreponse());
            reponseAdapted.setTexteReponse(reponseData.getTexteReponse());

            // Update le champ hasReponseInitiee de la question, si celle-ci est non vide
            Question question = this.getQuestion(session);
            question.setHasReponseInitiee(StringUtils.isNotBlank(reponseData.getTexteReponse()));
            session.saveDocument(question.getDocument());
        }
        // create reponse in session
        reponse = session.createDocument(reponseAdapted.getDocument());

        // save questionId in dossier
        saveReponseId(session, reponse.getId());

        return reponse;
    }

    @Override
    public List<HistoriqueAttribution> getHistoriqueAttribution(CoreSession session) {
        List<HistoriqueAttribution> historiques = new ArrayList<>();
        try {
            List<DocumentModel> historiquesDocs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                "HistoriqueAttribution",
                QUERY_HISTORIQUES,
                new Object[] { this.getQuestionId() }
            );
            for (DocumentModel histoDoc : historiquesDocs) {
                historiques.add(histoDoc.getAdapter(HistoriqueAttribution.class));
            }
        } catch (NuxeoException exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_HISTORIQUE_ATTR_TEC, exc);
        }

        Collections.sort(
            historiques,
            (histo1, histo2) -> {
                Calendar date1 = histo1.getDateAttribution();
                Calendar date2 = histo2.getDateAttribution();
                return date1.compareTo(date2);
            }
        );

        return historiques;
    }

    @Override
    public void addHistoriqueAttribution(
        CoreSession session,
        String fdrTypeCreationInstanciation,
        Calendar date,
        String idMinistere
    ) {
        String titreAttribution =
            "Attribution-" + SolonDateConverter.DATE_DASH_REVERSE.format(date) + "-" + idMinistere;

        DocumentModel historiqueDoc = session.createDocumentModel(
            getDocument().getPath() + "/Question",
            titreAttribution,
            ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_TYPE
        );
        HistoriqueAttribution historique = historiqueDoc.getAdapter(HistoriqueAttribution.class);
        if (historique != null) {
            historique.setDateAttribution(date);
            historique.setMinAttribution(idMinistere);
            historique.setTypeAttribution(fdrTypeCreationInstanciation);
            session.createDocument(historique.getDocument());
        }
    }

    @Override
    public void addHistoriqueAttribution(CoreSession session, String fdrTypeCreationInstanciation, String idMinistere) {
        addHistoriqueAttribution(session, fdrTypeCreationInstanciation, Calendar.getInstance(), idMinistere);
    }

    @Override
    public String getLabelNextStep() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_LABEL_ETAPE_SUIVANTE
        );
    }

    @Override
    public void setLabelNextStep(String labelEtapeSuivante) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.DOSSIER_LABEL_ETAPE_SUIVANTE, labelEtapeSuivante);
    }

    @Override
    public Boolean isArbitrated() {
        return PropertyUtil.getBooleanProperty(
            getDocument(),
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.IS_ARBITRATED_PROPERTY
        );
    }

    @Override
    public void setIsArbitrated(Boolean isArbitrated) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.IS_ARBITRATED_PROPERTY, isArbitrated);
    }

    protected List<Signalement> getSignalementsProperty(String schema, String value) {
        ArrayList<Signalement> signalementList = new ArrayList<>();
        List<Map<String, Serializable>> signalements = PropertyUtil.getMapStringSerializableListProperty(
            document,
            schema,
            value
        );
        if (signalements != null) {
            for (Map<String, Serializable> signalement : signalements) {
                signalementList.add(new SignalementImpl(signalement));
            }
        }
        return signalementList;
    }

    protected List<Renouvellement> getRenouvellementsProperty(String schema, String value) {
        List<Renouvellement> renouvellementList = new ArrayList<>();
        List<Map<String, Serializable>> renouvellements = PropertyUtil.getMapStringSerializableListProperty(
            document,
            schema,
            value
        );
        if (renouvellements != null) {
            for (Map<String, Serializable> renouvellement : renouvellements) {
                renouvellementList.add(new RenouvellementImpl(renouvellement));
            }
        }
        return renouvellementList;
    }

    protected void saveReponseId(CoreSession session, String reponseId) {
        setReponseId(reponseId);
        save(session);
    }

    protected void saveQuestionId(CoreSession session, String questionId) {
        setQuestionId(questionId);
        save(session);
    }

    @Override
    public void setHasPJ(Boolean hasPJ) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.HAS_PJ_PROPERTY, hasPJ);
    }

    @Override
    public Boolean hasPJ() {
        return PropertyUtil.getBooleanProperty(
            getDocument(),
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.HAS_PJ_PROPERTY
        );
    }

    @Override
    public boolean isRedemarre() {
        return PropertyUtil.getBooleanProperty(
            getDocument(),
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.IS_QUESTION_REDEMARRE
        );
    }

    @Override
    public void setIsRedemarre(boolean isRedemarre) {
        setProperty(DossierConstants.DOSSIER_SCHEMA, DossierConstants.IS_QUESTION_REDEMARRE, isRedemarre);
    }
}
