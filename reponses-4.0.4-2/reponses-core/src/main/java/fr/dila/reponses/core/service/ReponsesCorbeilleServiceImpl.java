package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_ETATS_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_XPATH;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doUFNXQLQueryAndMapping;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import com.google.common.collect.Lists;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STCorbeilleServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Implémentation du service Corbeille de l'application Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesCorbeilleServiceImpl extends STCorbeilleServiceImpl implements ReponsesCorbeilleService {
    private static final String COL_LABEL = "label";
    private static final String COL_COUNT = "count";

    /**
     * UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    @Override
    public Mailbox getDossierOwnerPersonalMailbox(final CoreSession session) {
        // Détermine le nom du propriétaire des dossiers
        final ConfigService configService = STServiceLocator.getConfigService();
        final String dossierOwner = configService.getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);

        // Récupère la mailbox personnelle du propriétaire
        final MailboxService mailboxService = ReponsesServiceLocator.getMailboxService();
        final Mailbox mailbox = mailboxService.getUserPersonalMailbox(session, dossierOwner);

        return mailbox;
    }

    /**
     * Liste les etapes existantes dans une mailbox pour un ministere donné, accompagnées du nombre de dossier présent
     * dans chaque étape
     *
     * @param mailboxIdsForQuery
     *            contient uneliste d'id separé par des virgule (destine a etre inclu dans un IN)
     */
    @Override
    public Map<String, Integer> listNotEmptyEtape(
        final CoreSession session,
        final Set<String> mailboxIds,
        final String ministereId
    ) {
        if (mailboxIds == null || mailboxIds.isEmpty()) {
            return Collections.emptyMap();
        }

        final StringBuilder query = new StringBuilder("SELECT l.drl:routingTaskType as ")
            .append(COL_LABEL)
            .append(", count() as ")
            .append(COL_COUNT)
            .append(" FROM ")
            .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
            .append(" AS l WHERE l.")
            .append(DossierConstants.DOSSIER_REPONSES_LINK_PREFIX)
            .append(":")
            .append(DossierConstants.DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY)
            .append(" = ? AND l.")
            .append(LIFECYCLE_EQUAL_TODO)
            .append(" AND l.cmdist:initial_action_internal_participant_mailboxes IN (")
            .append(StringHelper.getQuestionMark(mailboxIds.size()))
            .append(") GROUP BY l.drl:routingTaskType");

        // // Requete en SQL pour des raisons de performances
        // StringBuilder sb = new StringBuilder("SELECT l_D_1.ROUTINGTASKTYPE AS label, count(*) AS count ")
        // .append("FROM DOSSIER_REPONSES_LINK l_D_1 ")
        // .append("WHERE l_D_1.IDMINISTEREATTRIBUTAIRE = ? AND ")
        // .append("(EXISTS (SELECT 1 FROM \"CMDIST_INITIAL_ACTION_4CD43708\" dist WHERE ID = l_D_1.ID AND ")
        // .append("(dist.ITEM IN ( ")
        // .append(StringUtil.getQuestionMark(mailboxIds.size()))
        // .append(" )))) GROUP BY l_D_1.ROUTINGTASKTYPE");

        final Object[] params = new Object[1 + mailboxIds.size()];
        int index = 0;
        params[index++] = ministereId;
        for (final String mailboxId : mailboxIds) {
            params[index++] = mailboxId;
        }

        // Map<String, Integer> etapes = buildResultMapFromSQLQueryResult(session, sb.toString(),
        // new String[]{"drl:routingTaskType", FlexibleQueryMaker.COL_COUNT}, params, null);
        final Map<String, Integer> etapes = buildResultMapFromUFNXQLQueryResult(
            session,
            query.toString(),
            params,
            null
        );
        return etapes;
    }

    /**
     * Rempli une map avec les retour de la requete, les champs attendu sont label et count (qui contient le nombre
     * d'occurence de label) Cree un map ou en complete une. dans ce dernier cas le nombre d'occurence est ajouté au
     * nombre courant se trouvant dans la map "initMap"
     *
     * @param query
     *            UFNQL query
     * @param initMap
     * @return
     */
    private Map<String, Integer> buildResultMapFromUFNXQLQueryResult(
        final CoreSession session,
        final String query,
        final Object[] params,
        final Map<String, Integer> initMap
    ) {
        try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params)) {
            final Map<String, Integer> map = initMap == null ? new TreeMap<>(Collator.getInstance()) : initMap;
            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            while (iterator.hasNext()) {
                final Map<String, Serializable> row = iterator.next();
                final String label = (String) row.get(COL_LABEL);
                final Long count = (Long) row.get(COL_COUNT);

                final Integer existingCount = map.get(label);
                map.put(label, count.intValue() + (existingCount == null ? 0 : existingCount));
            }
            return map;
        }
    }

    @Override
    public List<DocumentModel> findUpdatableDossierLinkForDossier(
        final CoreSession session,
        final DocumentModel dossierDoc
    ) {
        return findUpdatableDossierLinkForDossiers(session, Collections.singletonList(dossierDoc.getId()));
    }

    @Override
    public List<DocumentModel> findUpdatableDossierLinkForDossier(
        final CoreSession session,
        final SSPrincipal principal,
        final DocumentModel dossierDoc
    ) {
        return findUpdatableDossierLinkForDossiers(session, principal, Collections.singletonList(dossierDoc.getId()));
    }

    @Override
    public List<DocumentModel> findUpdatableDossierLinkForDossiers(
        final CoreSession session,
        final SSPrincipal principal,
        final List<String> dossiersDocsIds
    ) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final Set<String> mailboxIdSet = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(
            principal.getPosteIdSet()
        );
        final Set<DocumentModel> dossierLinkList = new HashSet<>();
        // Si l'utilisateur est un administrateur, il peut actionner le DossierLink à la place de l'utilisateur
        if (principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_UPDATER)) {
            dossierLinkList.addAll(findDossierLinkUnrestrictedWithPrefetch(session, dossiersDocsIds));
        } else if (principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_MIN_UPDATER)) {
            // Récuépration des dossier link ministère si l'utilisateur à le droit
            dossierLinkList.addAll(
                findDossierLinkInMinistereOrMailbox(
                    session,
                    dossiersDocsIds,
                    principal.getMinistereIdSet(),
                    mailboxIdSet
                )
            );
        } else {
            // Recherche des DossierLink distribués directement à l'utilisateur
            dossierLinkList.addAll(findDossierLinkInMailbox(session, dossiersDocsIds, mailboxIdSet));
        }
        return new ArrayList<>(dossierLinkList);
    }

    @Override
    public List<DocumentModel> findUpdatableDossierLinkForDossiers(
        final CoreSession session,
        final List<String> dossiersDocsIds
    ) {
        final SSPrincipal principal = (SSPrincipal) session.getPrincipal();
        return findUpdatableDossierLinkForDossiers(session, principal, dossiersDocsIds);
    }

    @Override
    protected List<DocumentModel> queryDocs(
        final CoreSession session,
        final String query,
        final Collection<String> paramList
    ) {
        PrefetchInfo prefetchInfo = new PrefetchInfo(
            StringUtils.join(
                Lists.newArrayList(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA, DossierConstants.DOSSIER_SCHEMA),
                ","
            )
        );
        return QueryHelper.doUFNXQLQueryAndFetchForDocuments(
            session,
            query,
            paramList.toArray(new String[paramList.size()]),
            0,
            0,
            prefetchInfo
        );
    }

    private List<DocumentModel> findDossierLinkUnrestrictedWithPrefetch(
        CoreSession session,
        List<String> dossiersDocsIds
    ) {
        final StringBuilder stringBuilder = new StringBuilder("SELECT l.ecm:uuid AS id FROM ");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder
            .append(" AS l WHERE l.cslk:caseDocumentId IN (")
            .append(StringUtil.genMarksSuite(dossiersDocsIds.size()))
            .append(") AND l.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);

        PrefetchInfo prefetch = new PrefetchInfo(
            StringUtils.join(
                Lists.newArrayList(
                    DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
                    CaseLinkConstants.CASE_LINK_SCHEMA,
                    CaseDistribConstants.DISTRIBUTION_SCHEMA
                ),
                ","
            )
        );

        return QueryHelper.doUFNXQLQueryAndFetchForDocuments(
            session,
            stringBuilder.toString(),
            dossiersDocsIds.toArray(new Object[dossiersDocsIds.size()]),
            0,
            0,
            prefetch
        );
    }

    @Override
    public List<DocumentModel> findDossierInMinistere(final CoreSession session, final String ministereId) {
        final String query =
            "SELECT d." +
            STSchemaConstant.ECM_UUID_XPATH +
            " AS id FROM Dossier AS d WHERE d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = ?";

        final List<String> paramList = new ArrayList<>();
        paramList.add(ministereId);
        return queryDocs(session, query, paramList);
    }

    @Override
    public List<DocumentModel> findDossierFromMinistereReattribution(
        final CoreSession session,
        final String ministereReattribution
    ) {
        final String query =
            "SELECT d." +
            STSchemaConstant.ECM_UUID_XPATH +
            " AS id FROM Dossier AS d WHERE d.dos:ministereReattribution = ? and d.dos:ministereReattribution != d.dos:ministereAttributaireCourant";

        final List<String> paramList = new ArrayList<>();
        paramList.add(ministereReattribution);

        return queryDocs(session, query, paramList);
    }

    @Override
    public List<DocumentModel> findDossierLinkInMinistereOrMailbox(
        final CoreSession session,
        final List<String> dossiersDocsIds,
        final Collection<String> ministereIdList,
        final Collection<String> mailboxIdList
    ) {
        final StringBuilder query = new StringBuilder("SELECT l.")
            .append(STSchemaConstant.ECM_UUID_XPATH)
            .append(" AS id FROM ")
            .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
            .append(" AS l ")
            .append(" WHERE l.cslk:caseDocumentId IN (")
            .append(StringHelper.getQuestionMark(dossiersDocsIds.size()))
            .append(") AND l.")
            .append(LIFECYCLE_EQUAL_TODO)
            .append(" AND (l.drl:idMinistereAttributaire IN (")
            .append(StringHelper.getQuestionMark(ministereIdList.size()))
            .append(") OR l.cmdist:initial_action_internal_participant_mailboxes IN (")
            .append(StringHelper.getQuestionMark(mailboxIdList.size()))
            .append("))");

        final List<String> paramList = new ArrayList<>();
        paramList.addAll(dossiersDocsIds);
        paramList.addAll(ministereIdList);
        paramList.addAll(mailboxIdList);

        return queryDocs(session, query.toString(), paramList);
    }

    @Override
    public int countDossierLinksSignalesForPostes(CoreSession session, Collection<String> postesId) {
        return countDossierLinksForPostes(session, postesId, getRegexpForSignale());
    }

    @Override
    public int countDossierLinksNonSignalesForPostes(CoreSession session, Collection<String> postesId) {
        return countDossierLinksForPostes(session, postesId, getRegexpForNonSignale());
    }

    private String getRegexpForSignale() {
        return getRegexpFunc("^.t.$");
    }

    private String getRegexpForNonSignale() {
        return getRegexpFunc("^.[^t].$");
    }

    private String getRegexpFunc(String regexp) {
        return format("regexpLike(dl.%s, '%s') = 1", DOSSIER_REPONSES_ETATS_QUESTION_XPATH, regexp);
    }

    @Override
    public Map<String, Integer> mapCountDossierLinksSignalesToMinisteres(
        CoreSession session,
        Collection<String> postesId
    ) {
        return doMapCountDossierLinksToMinisteres(session, postesId, getRegexpForSignale());
    }

    @Override
    public Map<String, Integer> mapCountDossierLinksNonSignalesToMinisteres(
        CoreSession session,
        Collection<String> postesId
    ) {
        return doMapCountDossierLinksToMinisteres(session, postesId, getRegexpForNonSignale());
    }

    private Map<String, Integer> doMapCountDossierLinksToMinisteres(
        CoreSession session,
        Collection<String> postesId,
        String funcSignale
    ) {
        Collection<String> mailboxs = getMailboxPosteIds(postesId);

        final StringBuilder stringBuilder = new StringBuilder("SELECT dl.");
        stringBuilder.append(DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_XPATH);
        stringBuilder.append(" AS min FROM ");
        stringBuilder.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
        stringBuilder.append(" AS dl WHERE dl.cmdist:initial_action_internal_participant_mailboxes IN (");
        stringBuilder.append(StringHelper.getQuestionMark(mailboxs.size())).append(") AND dl.");
        stringBuilder.append(LIFECYCLE_EQUAL_TODO);
        stringBuilder.append(" AND ");
        stringBuilder.append(funcSignale);

        List<String> list = doUFNXQLQueryAndMapping(
            session,
            stringBuilder.toString(),
            mailboxs.toArray(new String[mailboxs.size()]),
            (Map<String, Serializable> rowData) -> (String) rowData.get("min")
        );
        return list.stream().collect(groupingBy(identity(), summingInt(s -> 1)));
    }

    @Override
    protected List<DocumentModel> queryDocsUnrestricted(
        final CoreSession session,
        final String query,
        final Collection<String> paramList
    ) {
        PrefetchInfo prefetchInfo = new PrefetchInfo(
            StringUtils.join(
                Lists.newArrayList(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA, DossierConstants.DOSSIER_SCHEMA),
                ","
            )
        );
        return QueryHelper.doUnrestrictedUFNXQLQueryAndFetchForDocuments(
            session,
            query,
            paramList.toArray(new String[paramList.size()]),
            0,
            0,
            prefetchInfo
        );
    }
}
