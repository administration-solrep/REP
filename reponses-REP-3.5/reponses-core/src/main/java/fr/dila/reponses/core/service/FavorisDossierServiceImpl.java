package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.naming.NameAlreadyBoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.FavoriDossierConstant;
import fr.dila.reponses.api.favoris.FavoriDossier;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.CleanupServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

public class FavorisDossierServiceImpl implements FavorisDossierService {

    private static final String FAVORIS_DOSSIER_REPERTOIRE_DOCUMENT_TYPE = "FavorisDossierRepertoire";

    private static final String FAVORIS_ROOT = "favoris-dossier";

    /**
     * Le chemin de favoris par défault
     */
    private static final String DEFAULT_FAVORIS_ROOT_PATH = "/favoris";

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(FavorisDossierServiceImpl.class);
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CleanupServiceImpl.class);

    /**
     *  "SELECT *  FROM FavorisDossier WHERE fvd:targetDocument IN (%s)"
     */
    private static final String QUERY_SELECT_FAVORIS_DOSSIER_FMT = "SELECT * FROM " + FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE + " WHERE "
            + "fvd:targetDocument IN (%s) AND ecm:isProxy = 0";

    private static final String QUERY_REPERTOIRE_BY_NAME_FMT = "SELECT * FROM " + FAVORIS_DOSSIER_REPERTOIRE_DOCUMENT_TYPE
            + " WHERE dc:title='%s' AND ecm:isProxy = 0";

    protected DossierDistributionService dossierDistributionService;

    protected UserWorkspaceService userWorkspaceService;

    public FavorisDossierServiceImpl() {
        super();
    }

    @Override
    public void add(final CoreSession session, final DocumentModelList docs, final String repertoireName) throws ClientException {
        if (log.isDebugEnabled()) {
            log.debug("Ajout des dossiers aux favoris" + docs.size());
        }
        // Pour empêcher l'utilisateur d'ajouter plusieurs fois le même dossier dans le même favori
        // On compare les favoris entrés précedemment aux nouveaux favoris potentiels
        // Récupération des favoris du dossier dans lequel on essaye d'insérer les nouveaux favoris
        final List<DocumentModel> favorisCurrentRepertoire = getFavoris(session, getCurrentRepertoireDocument(session, repertoireName).getId());
        final List<String> favorisIdCurrentRepertoire = new ArrayList<String>();
        for (final DocumentModel docfav : favorisCurrentRepertoire) {
            favorisIdCurrentRepertoire.add(docfav.getAdapter(FavoriDossier.class).getTargetDocumentId());
        }

        // Si le dossier est déjà dans les favoris, il n'est pas ajouté
        for (final DocumentModel doc : docs) {
            if (favorisIdCurrentRepertoire.contains(doc.getAdapter(Dossier.class).getQuestionId())) {
                if (log.isDebugEnabled()) {
                    log.debug(doc.getName() + " est déjà présent dans les favoris : pas d'ajout");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Ajout de " + doc.getName() + "aux favoris");
                }
                createFavorisDossier(session, doc, repertoireName);
            }
        }
    }

    @Override
    public DocumentModel createFavorisRepertoire(final CoreSession session, final String repertoireName, final Calendar dateValidite)
            throws ClientException, NameAlreadyBoundException {
        final String rootFolder = getFavorisRoot(session);
        if (log.isDebugEnabled()) {
            log.debug("Favori Root Folder = " + rootFolder);
        }
        if (!isValidName(session, repertoireName)) {
            throw new NameAlreadyBoundException();
        }
        final DocumentModel favorisRepertoire = new DocumentModelImpl(rootFolder, repertoireName, FAVORIS_DOSSIER_REPERTOIRE_DOCUMENT_TYPE);
        DublincoreSchemaUtils.setTitle(favorisRepertoire, repertoireName);
        DublincoreSchemaUtils.setValidDate(favorisRepertoire, dateValidite);
        session.createDocument(favorisRepertoire);
        final DocumentModel repertoireCreated = session.saveDocument(favorisRepertoire);
        session.save();
        return repertoireCreated;
    }

    /**
     * Retourne vrai si le nom du dossier n'est pas déjà utilisé.
     * @param session
     * @param repertoireName
     * @return
     * @throws ClientException
     */
    private boolean isValidName(final CoreSession session, final String repertoireName) throws ClientException {
        final String query = String.format(QUERY_REPERTOIRE_BY_NAME_FMT, repertoireName);
        final Long nameCount = QueryUtils.doCountQuery(session, query);
        return nameCount == 0;
    }

    @Override
    public DocumentModel getCurrentRepertoireDocument(final CoreSession session, final String currentRepertoire) throws ClientException {
        final String repertoirePath = getFavorisRoot(session) + "/" + currentRepertoire;
        return session.getDocument(new PathRef(repertoirePath));
    }

    @Override
    public DocumentModel createFavorisDossier(final CoreSession session, final DocumentModel dossierSourceDoc, final String currentRepertoire)
            throws ClientException {
        final Dossier dossierSource = dossierSourceDoc.getAdapter(Dossier.class);
        final String repertoirePath = getFavorisRoot(session) + "/" + currentRepertoire;
        if (log.isDebugEnabled()) {
            log.debug("Favoris créé à partir du dossier ID = " + dossierSource.getNumeroQuestion() + " dans le répertoire : " + currentRepertoire);
            log.debug("Favoris Path Folder = " + repertoirePath);
        }
        DocumentModel favoriDoc = new DocumentModelImpl(repertoirePath, "favoris_" + dossierSource.getNumeroQuestion(),
                FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE);
        // Pour l'instant on copie le contenu de la question dans le favoris,
        // d'autres alternatives sont à l'étude : créér un proxy sur dossier, faire une requête plus compliquée.
        favoriDoc = session.createDocument(favoriDoc);
        final FavoriDossier favori = favoriDoc.getAdapter(FavoriDossier.class);
        final String questionSourceId = dossierSource.getQuestionId();

        favori.setTitle(dossierSourceDoc.getTitle());
        favori.setTargetDocumentId(questionSourceId);
        favori.save(session);
        session.save();
        log.debug("TARGET = " + favori.getTargetDocumentId());
        return favori.getDocument();
    }

    @Override
    public void delete(final CoreSession session, final DocumentModelList docs) throws ClientException {
        if (docs != null && !docs.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Suppression " + docs.size() + "aux favoris");
            }

            final List<String> referenceIds = new ArrayList<String>();
            for (final DocumentModel dossierDoc : docs) {
                final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
                final String questionId = dossier.getQuestionId();
                referenceIds.add(questionId);
            }

            final String getIdFavorisQuery = String.format(QUERY_SELECT_FAVORIS_DOSSIER_FMT, StringUtil.join(referenceIds, ",", "'"));

            final DocumentModelList favorisDeleteList = session.query(getIdFavorisQuery);
            for (final DocumentModel favoris : favorisDeleteList) {
                LOGGER.info(session, STLogEnumImpl.DEL_FAVORIS_TEC, favoris);
                session.removeDocument(favoris.getRef());
            }
            session.save();
        }
    }

    @Override
    public List<DocumentModel> getFavoris(final CoreSession session, final String favorisRepertoireId) throws ClientException {
        return session.query(String.format("SELECT * FROM FavorisDossier WHERE ecm:parentId = '%s' AND ecm:isProxy = 0", favorisRepertoireId));
    }

    /**
     * 
     * Retourne la liste des répertoires de favoris.
     * 
     * @throws ClientException
     * 
     */
    @Override
    public List<DocumentModel> getFavorisRepertoires(final CoreSession session) throws ClientException {
        if (session == null) {
            return new ArrayList<DocumentModel>();
        }
        final DocumentModel favorisRoot = getRootFavorisDossier(session);
        final String currentDate = DateLiteral.dateFormatter.print(Calendar.getInstance().getTimeInMillis());
        return session.query("SELECT * FROM FavorisDossierRepertoire WHERE ecm:parentId = '" + favorisRoot.getId() + "' AND (dc:valid >= DATE '"
                + currentDate + "' OR dc:valid IS NULL) AND ecm:isProxy = 0");
    }

    /**
     * Marque l'emplacement du dossier favoris root
     * @throws ClientException 
     */
    @Override
    public DocumentModel getRootFavorisDossier(final CoreSession session) throws ClientException {
        final PathRef ref = new PathRef(getFavorisRoot(session));
        return session.getDocument(ref);
    }

    @Override
    public void removeOldFavoris(final CoreSession session, final Calendar currentDate) throws ClientException {
        final String literalDate = DateLiteral.dateFormatter.print(currentDate.getTimeInMillis());

        //on récupère tous les favoris dont la date de validité est inferieure à la date courante
        final String query = "SELECT * FROM FavorisDossierRepertoire where dc:valid < DATE '" + literalDate + "' AND ecm:isProxy = 0";
        final DocumentModelList resultList = session.query(query);
        for (final DocumentModel favorisDossier : resultList) {
            LOGGER.info(session, STLogEnumImpl.DEL_FAVORIS_TEC, favorisDossier);
            session.removeDocument(favorisDossier.getRef());
        }
    }

    private UserWorkspaceService getUserWorkspaceService() {
        if (userWorkspaceService == null) {
            userWorkspaceService = STServiceLocator.getUserWorkspaceService();
        }
        return userWorkspaceService;
    }

    /**
     * Retourne le dossier favoris root de l'utilisateur
     * 
     * @param session
     * @return Le chemin de favoris root
     * @throws ClientException
     */
    private String getFavorisRoot(final CoreSession session) throws ClientException {
        // Si le document root existe, on renvoie son chemin
        String userworkspacePath = "";
        try {
            userworkspacePath = getUserSpace(session);
        } catch (final Exception e) {
            return DEFAULT_FAVORIS_ROOT_PATH;
        }
        final PathRef favorisRootRef = new PathRef(userworkspacePath + "/" + FAVORIS_ROOT);
        if (favorisRootRef != null && session.exists(favorisRootRef)) {
            return favorisRootRef.toString();
        } else {
            return DEFAULT_FAVORIS_ROOT_PATH;
        }
    }

    /**
     * Initialise l'espace utilisateur et retourne l'emplacement de cet espace
     * 
     * @param session
     * @throws ClientException
     */
    private String getUserSpace(final CoreSession session) throws ClientException {
        final DocumentModel uw = getUserWorkspaceService().getCurrentUserPersonalWorkspace(session, null);
        return uw.getPathAsString();
    }
}
