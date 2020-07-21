package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.search.api.client.querymodel.QueryModelService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.recherche.ReponsesMinimalEscaper;
import fr.dila.reponses.core.recherche.query.ReponsesQueryModel;
import fr.dila.reponses.core.requeteur.KeywordMinistereAttributaireResolver;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.recherche.STRechercheServiceImpl;
import fr.dila.st.core.requeteur.RequeteurFunctionSolverHelper;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 * @author JGZ L'implementation du service recherche L'utilisation d'un service deprecié est rendu necessaire, car les contents view ne sont pas (encore) assez flexibles.
 */
@SuppressWarnings("deprecation")
public class RechercheServiceImpl extends STRechercheServiceImpl implements RechercheService {

    private static final String USER_REQUETE_FOLDER = "/";

    protected QueryModelService qmService;

    private static final Log log = LogFactory.getLog(RechercheServiceImpl.class);

    public RechercheServiceImpl() {
        super();
        qmService = (QueryModelService) Framework.getRuntime().getComponent(QueryModelService.NAME);
    }

    @Override
    public Requete createRequete(final CoreSession session, final String name) throws ClientException {
        final DocumentModel requeteDoc = new DocumentModelImpl(USER_REQUETE_FOLDER, name, ReponsesConstant.RECHERCHE_DOCUMENT_TYPE);
        final DocumentModel modelResult = session.createDocument(requeteDoc);
        final Requete requete = modelResult.getAdapter(Requete.class);
        return requete;
    }

    @Override
    public Requete getRequete(final CoreSession session, final String name) throws ClientException {
        final DocumentModel requeteDoc = new DocumentModelImpl(USER_REQUETE_FOLDER, name, ReponsesConstant.RECHERCHE_DOCUMENT_TYPE);
        final Requete requete = requeteDoc.getAdapter(Requete.class);
        return requete;
    }

    @Override
    public DocumentModelList query(final CoreSession session, final Requete requete) throws ClientException {
        requete.doBeforeQuery();
        final String pattern = getFullQuery(session, requete);
        final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, pattern, null);
        return session.getDocuments(refs);
    }

    @Override
    public String getFullQuery(final CoreSession session, final Requete requete, String... modelNames) throws ClientException {
        final JointureService jointureService = STServiceLocator.getJointureService();
        final QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
        // Si on ne spécifie rien, on prend les requetes models enregistrés par défaut
        if (modelNames.length == 0) {
            modelNames = this.getRequeteParts(requete);
        }
        final ReponsesQueryModel queryModel = new ReponsesQueryModel();
        final String clause = queryModel.getAndRequeteParts(qmService, requete.getDocument(), modelNames);
        assembler.setWhereClause(clause);
        String fullQuery = assembler.getFullQuery();
        fullQuery = resolveKeywords(session, fullQuery, null);
        if (log.isDebugEnabled()) {
            log.debug("Recherche Service -- Log requete");
            log.debug(fullQuery);
        }
        return fullQuery;
    }

    /**
     * Cherche les mots-clés et remplace par l'expression voulue.
     * 
     * @param query
     * @param env
     * @return la requête complête avec les post-traitements pour la résolution des mot-clés.
     */
    // TODO : Regrouper avec les solveurs du requêteur
    protected String resolveKeywords(final CoreSession session, String query, final Map<String, Object> env) {
        for (final RequeteurFunctionSolver solver : getSolvers()) {
            query = RequeteurFunctionSolverHelper.apply(solver, session, query, env);
        }
        return query;
    }

    // TODO: Regrouper avec les solveurs du requêteur
    private List<RequeteurFunctionSolver> getSolvers() {
        final List<RequeteurFunctionSolver> solvers = new ArrayList<RequeteurFunctionSolver>();
        solvers.add(new KeywordMinistereAttributaireResolver());
        return solvers;
    }

    @Override
    public Question searchQuestionBySourceNumero(final CoreSession session, final String sourcenumero) throws ClientException {
        final ReponsesMinimalEscaper escaper = new ReponsesMinimalEscaper();

        final StringBuilder sb = new StringBuilder("SELECT d.ecm:uuid as id FROM ");
        sb.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
        sb.append(" as d WHERE d.");
        sb.append(DossierConstants.QUESTION_SOURCE_NUMERO_XPATH);
        sb.append(" = ? AND d.");
        sb.append(DossierConstants.QUESTION_TYPE_QUESTION_XPATH);
        sb.append(" = ? AND d.");
        sb.append(DossierConstants.QUESTION_LEGISLATURE_XPATH);
        sb.append(" = ? ");

        final String legislatureValue = STServiceLocator.getSTParametreService().getParametreValue(session,
                ReponsesParametreConstant.LEGISLATURE_COURANTE);

        Long legislature = null;
        try {
            legislature = Long.parseLong(legislatureValue);
        } catch (final Exception e) {
            throw new ClientException("La législature courante n'est pas paramétrée correctement.", e);
        }

        final List<DocumentModel> dossiers = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, DossierConstants.QUESTION_DOCUMENT_TYPE,
                sb.toString(), new Object[] { escaper.escape(sourcenumero), "QE", legislature });

        if (dossiers.isEmpty()) {
            throw new ClientException("Aucune question trouvée pour " + sourcenumero + ",  pour la législature " + legislatureValue);
        }
        if (dossiers.size() == 1) {
            return dossiers.get(0).getAdapter(Question.class);
        } else {
            throw new ClientException("Plusieurs questions trouvées pour " + sourcenumero + ",  pour la législature " + legislatureValue);
        }

    }

    /**
     * Retourne la liste des identifiants des requêtes model en fonction du mode d'indexation de la requête.
     * 
     * @return
     */
    public String[] getRequeteParts(final Requete requete) {
        final List<String> requeteModels = new ArrayList<String>();
        requeteModels.add(RequeteConstants.PART_REQUETE_SIMPLE);
        requeteModels.add(RequeteConstants.PART_REQUETE_COMPLEXE);
        requeteModels.add(requete.getIndexationMode().getQueryModelName());
        requeteModels.add(RequeteConstants.PART_REQUETE_METADONNEE);
        requeteModels.add(RequeteConstants.PART_REQUETE_FDR);
        requeteModels.add(RequeteConstants.PART_REQUETE_TEXTE_INTEGRAL);
        final String[] requetesArray = requeteModels.toArray(new String[requeteModels.size()]);
        return requetesArray;
    }

    @Override
    public String getWhereClause(final Requete requete, final String... modelNames) throws ClientException {
        final ReponsesQueryModel queryModel = new ReponsesQueryModel();
        final String clause = queryModel.getAndRequeteParts(qmService, requete.getDocument(), modelNames);
        return clause;
    }
}
