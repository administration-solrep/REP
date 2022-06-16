package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_SCHEMA;
import static fr.dila.st.core.service.STServiceLocator.getUserWorkspaceService;
import static fr.sword.naiad.nuxeo.commons.core.util.StringUtil.join;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.retrieveDocuments;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.FavoriDossierConstant;
import fr.dila.reponses.api.favoris.FavoriDossier;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

public class FavorisDossierServiceImpl implements FavorisDossierService {
    private static final String FAVORIS_DOSSIER_REPERTOIRE_DOC_TYPE = "FavorisDossierRepertoire";

    private static final String FAVORIS_ROOT = "favoris-dossier";

    private static final long serialVersionUID = 1L;

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOG = STLogFactory.getLog(FavorisDossierServiceImpl.class);

    /**
     *  "SELECT *  FROM FavorisDossier WHERE fvd:targetDocument IN (%s)"
     */
    private static final String QUERY_SELECT_FAVORIS_DOSSIER_FMT =
        "SELECT f.ecm:uuid AS id FROM " +
        FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE +
        " AS f WHERE " +
        "f.fvd:targetDocument IN (%s) AND f.ecm:parentId = '%s'";

    private static final String QUERY_REPERTOIRE_BY_NAME_FMT =
        "SELECT * FROM " + FAVORIS_DOSSIER_REPERTOIRE_DOC_TYPE + " WHERE dc:title='%s'";

    protected DossierDistributionService dossierDistributionService;

    public FavorisDossierServiceImpl() {
        super();
    }

    @Override
    public void add(final CoreSession session, final Collection<String> ids, final String repertoireName) {
        DocumentModelList dossierDocs = retrieveDocuments(session, DOSSIER_DOCUMENT_TYPE, ids);
        add(session, dossierDocs, repertoireName);
    }

    @Override
    public void add(final CoreSession session, final DocumentModelList docs, final String repertoireName) {
        LOG.debug(session, STLogEnumImpl.DEFAULT, "Ajout des dossiers aux favoris" + docs.size());
        // Pour empêcher l'utilisateur d'ajouter plusieurs fois le même dossier dans le même favori
        // On compare les favoris entrés précedemment aux nouveaux favoris potentiels
        // Récupération des favoris du dossier dans lequel on essaye d'insérer les nouveaux favoris
        final List<DocumentModel> favorisCurrentRepertoire = getFavoris(
            session,
            getCurrentRepertoireDocument(session, repertoireName).getId()
        );
        final List<String> favorisIdCurrentRepertoire = new ArrayList<>();
        for (final DocumentModel docfav : favorisCurrentRepertoire) {
            favorisIdCurrentRepertoire.add(docfav.getAdapter(FavoriDossier.class).getTargetDocumentId());
        }

        // Si le dossier est déjà dans les favoris, il n'est pas ajouté
        for (final DocumentModel doc : docs) {
            if (favorisIdCurrentRepertoire.contains(doc.getAdapter(Dossier.class).getQuestionId())) {
                LOG.debug(
                    session,
                    STLogEnumImpl.DEFAULT,
                    doc.getName() + " est déjà présent dans les favoris : pas d'ajout"
                );
            } else {
                LOG.debug(session, STLogEnumImpl.DEFAULT, "Ajout de " + doc.getName() + "aux favoris");
                createFavorisDossier(session, doc, repertoireName);
            }
        }
    }

    @Override
    public DocumentModel createFavorisRepertoire(
        final CoreSession session,
        final String repertoireName,
        final Calendar dateValidite
    ) {
        final String rootFolder = getFavorisRoot(session);
        LOG.debug(session, STLogEnumImpl.DEFAULT, "Favori Root Folder = " + rootFolder);
        final DocumentModel favorisRepertoire = session.createDocumentModel(
            rootFolder,
            repertoireName,
            FAVORIS_DOSSIER_REPERTOIRE_DOC_TYPE
        );
        DublincoreSchemaUtils.setTitle(favorisRepertoire, repertoireName);
        DublincoreSchemaUtils.setValidDate(favorisRepertoire, dateValidite);
        session.createDocument(favorisRepertoire);
        final DocumentModel repertoireCreated = session.saveDocument(favorisRepertoire);
        session.save();
        return repertoireCreated;
    }

    @Override
    public boolean isNomFavoriLibre(final CoreSession session, final String repertoireName) {
        final String query = String.format(QUERY_REPERTOIRE_BY_NAME_FMT, repertoireName);
        return QueryUtils.doQueryForIds(session, query, 1, 0).isEmpty();
    }

    @Override
    public DocumentModel getCurrentRepertoireDocument(final CoreSession session, final String currentRepertoire) {
        final String repertoirePath = getFavorisRoot(session) + "/" + currentRepertoire;
        return session.getDocument(new PathRef(repertoirePath));
    }

    @Override
    public DocumentModel createFavorisDossier(
        final CoreSession session,
        final DocumentModel dossierSourceDoc,
        final String currentRepertoire
    ) {
        final Dossier dossierSource = dossierSourceDoc.getAdapter(Dossier.class);
        final String repertoirePath = getFavorisRoot(session) + "/" + currentRepertoire;
        LOG.debug(
            session,
            STLogEnumImpl.DEFAULT,
            "Favoris créé à partir du dossier ID = " +
            dossierSource.getNumeroQuestion() +
            " dans le répertoire : " +
            currentRepertoire
        );
        LOG.debug(session, STLogEnumImpl.DEFAULT, "Favoris Path Folder = " + repertoirePath);
        DocumentModel favoriDoc = session.createDocumentModel(
            repertoirePath,
            "favoris_" + dossierSource.getNumeroQuestion(),
            FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE
        );
        // Pour l'instant on copie le contenu de la question dans le favoris,
        // d'autres alternatives sont à l'étude : créér un proxy sur dossier, faire une requête plus compliquée.
        favoriDoc = session.createDocument(favoriDoc);
        final FavoriDossier favori = favoriDoc.getAdapter(FavoriDossier.class);
        final String questionSourceId = dossierSource.getQuestionId();

        favori.setTitle(dossierSourceDoc.getTitle());
        favori.setTargetDocumentId(questionSourceId);
        favori.save(session);
        session.save();
        LOG.debug(session, STLogEnumImpl.DEFAULT, "TARGET = " + favori.getTargetDocumentId());
        return favori.getDocument();
    }

    @Override
    public void delete(final CoreSession session, final String favoriId, final Collection<String> dossierIds) {
        delete(session, favoriId, session.getDocuments(dossierIds, new PrefetchInfo(DOSSIER_SCHEMA)));
    }

    @Override
    public void delete(final CoreSession session, final String favoriId, final DocumentModelList docs) {
        LOG.debug(session, STLogEnumImpl.DEFAULT, format("Suppression de %d favoris", docs.size()));

        Collection<String> questionIds = docs
            .stream()
            .map(d -> d.getAdapter(Dossier.class))
            .map(Dossier::getQuestionId)
            .collect(toList());

        final String getIdFavorisQuery = format(
            QUERY_SELECT_FAVORIS_DOSSIER_FMT,
            join(questionIds, ",", "'"),
            favoriId
        );

        QueryUtils
            .doUFNXQLQueryAndFetchForDocuments(session, getIdFavorisQuery, new Object[] {})
            .stream()
            .map(DocumentModel::getRef)
            .forEach(
                ref -> {
                    LOG.info(session, STLogEnumImpl.DEL_FAVORIS_TEC, ref.reference());
                    session.removeDocument(ref);
                }
            );
    }

    @Override
    public List<DocumentModel> getFavoris(final CoreSession session, final String favorisRepertoireId) {
        String query = String.format(
            "SELECT f.ecm:uuid AS id FROM FavorisDossier AS f WHERE f.ecm:parentId = '%s'",
            favorisRepertoireId
        );
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query, new Object[] {});
    }

    @Override
    public List<String> getFavorisRepertoiresId(final CoreSession session) {
        Objects.requireNonNull(session, "null CoreSession");
        final DocumentModel favorisRoot = getRootFavorisDossier(session);
        final String currentDate = DateLiteral.dateFormatter.print(Calendar.getInstance().getTimeInMillis());
        String query = format(
            "Select f.ecm:uuid as id From %s as f Where f.ecm:parentId = '%s' AND (f.dc:valid >= DATE '%s' OR f.dc:valid IS NULL)",
            FAVORIS_DOSSIER_REPERTOIRE_DOC_TYPE,
            favorisRoot.getId(),
            currentDate
        );
        return QueryUtils.doUFNXQLQueryForIdsList(session, query, new Object[] {});
    }

    @Override
    public List<DocumentModel> getFavorisRepertoires(final CoreSession session) {
        return session.getDocuments(getFavorisRepertoiresId(session), null);
    }

    @Override
    public void removeFavorisRepertoires(final CoreSession session) {
        session.removeDocuments(getFavorisRepertoiresId(session).stream().map(IdRef::new).toArray(DocumentRef[]::new));
    }

    /**
     * Marque l'emplacement du dossier favoris root
     *
     */
    @Override
    public DocumentModel getRootFavorisDossier(final CoreSession session) {
        final PathRef ref = new PathRef(getFavorisRoot(session));
        return session.getDocument(ref);
    }

    @Override
    public void removeOldFavoris(final CoreSession session, final Calendar currentDate) {
        final String literalDate = DateLiteral.dateFormatter.print(currentDate.getTimeInMillis());

        //on récupère tous les favoris dont la date de validité est inferieure à la date courante
        final String query =
            "SELECT * FROM FavorisDossierRepertoire where dc:valid < DATE '" + literalDate + "' AND ecm:isProxy = 0";
        final DocumentModelList resultList = session.query(query);
        for (final DocumentModel favorisDossier : resultList) {
            LOG.info(session, STLogEnumImpl.DEL_FAVORIS_TEC, favorisDossier);
            session.removeDocument(favorisDossier.getRef());
        }
    }

    private String getFavorisRoot(final CoreSession session) {
        return (
            getUserWorkspaceService().getCurrentUserPersonalWorkspace(session).getPathAsString() + "/" + FAVORIS_ROOT
        );
    }
}
