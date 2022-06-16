package fr.dila.reponses.core.recherche.query;

import static fr.dila.reponses.api.constant.RequeteConstants.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.api.WhereClauseDefinition;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ServiceUtil.class, NXQLQueryBuilder.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesQueryModelTest {
    private static final String REQUETE_SIMPLE = "SELECT * FROM Document";
    private static final String REQUETE_COMPLEXE =
        "SELECT * FROM Document WHERE q.qu:origineQuestion = 'AN' AND ((q.qu:etatQuestion = 'repondu' AND r.rep:datePublicationJOReponse IS NOT NULL)) AND q.qu:legislatureQuestion = '14'";
    private static final String REQUETE_INDEX_ORIGINE =
        "SELECT * FROM Document WHERE q.ixa:AN_analyse = 'aide de la France' AND q.ixa:SE_rubrique = 'Motocycles'";
    private static final String REQUETE_INDEX_COMPL =
        "SELECT * FROM Document WHERE q.ixacomp:AN_analyse = 'aide de la France' AND q.ixacomp:SE_rubrique = 'Motocycles'";
    private static final String REQUETE_METADONNEE = "SELECT * FROM Document WHERE q.qu:typeQuestion = 'QE'";
    private static final String REQUETE_FDR =
        "SELECT * FROM Document WHERE e2.rtsk:type = '6' AND e2.ecm:currentLifeCycleState = 'running'";
    private static final String REQUETE_TEXTE_INTEGRAL =
        "SELECT * FROM Document WHERE q.ecm:fulltext_txtQuestion = \"${test} ${expression} ${texte} ${question}\"";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private PageProviderService pageProviderService;

    @Mock
    private DocumentModel doc;

    @Mock
    private PageProviderDefinition pageProviderDefinition;

    @Mock
    private WhereClauseDefinition whereClauseDefinition;

    @Before
    public void setUp() {
        mockStatic(ServiceUtil.class);
        mockStatic(NXQLQueryBuilder.class);

        when(ServiceUtil.getRequiredService(PageProviderService.class)).thenReturn(pageProviderService);
        when(pageProviderService.getPageProviderDefinition(anyString())).thenReturn(pageProviderDefinition);
        when(pageProviderDefinition.getWhereClause()).thenReturn(whereClauseDefinition);
        when(pageProviderDefinition.getSortInfos()).thenReturn(emptyList());
    }

    @Test
    public void getAndRequetePartsWithIndexModeTous() {
        String expectedResult =
            "(q.qu:origineQuestion = 'AN' AND ((q.qu:etatQuestion = 'repondu' AND r.rep:datePublicationJOReponse IS NOT NULL)) AND q.qu:legislatureQuestion = '14') AND ((q.ixa:AN_analyse = 'aide de la France' AND q.ixa:SE_rubrique = 'Motocycles') OR (q.ixacomp:AN_analyse = 'aide de la France' AND q.ixacomp:SE_rubrique = 'Motocycles')) AND (q.qu:typeQuestion = 'QE') AND (e2.rtsk:type = '6' AND e2.ecm:currentLifeCycleState = 'running') AND (q.ecm:fulltext_txtQuestion = \"${test} ${expression} ${texte} ${question}\")";

        List<String> modelNames = ImmutableList.of(
            PART_REQUETE_SIMPLE,
            PART_REQUETE_COMPLEXE,
            IndexModeEnum.TOUS.getQueryModelName(),
            PART_REQUETE_METADONNEE,
            PART_REQUETE_FDR,
            PART_REQUETE_TEXTE_INTEGRAL
        );

        when(NXQLQueryBuilder.getQuery(doc, whereClauseDefinition, null, new SortInfo[0]))
            .thenReturn(
                REQUETE_SIMPLE,
                REQUETE_COMPLEXE,
                REQUETE_INDEX_ORIGINE,
                REQUETE_INDEX_COMPL,
                REQUETE_METADONNEE,
                REQUETE_FDR,
                REQUETE_TEXTE_INTEGRAL
            );

        String result = ReponsesQueryModel.getAndRequeteParts(doc, toArray(modelNames));

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void getAndRequetePartsWithIndexModeTousAndEmptyRequeteParts() {
        String expectedResult =
            "(q.qu:origineQuestion = 'AN' AND ((q.qu:etatQuestion = 'repondu' AND r.rep:datePublicationJOReponse IS NOT NULL)) AND q.qu:legislatureQuestion = '14') AND (q.qu:typeQuestion = 'QE') AND (e2.rtsk:type = '6' AND e2.ecm:currentLifeCycleState = 'running') AND (q.ecm:fulltext_txtQuestion = \"${test} ${expression} ${texte} ${question}\")";

        List<String> modelNames = ImmutableList.of(
            PART_REQUETE_SIMPLE,
            PART_REQUETE_COMPLEXE,
            IndexModeEnum.TOUS.getQueryModelName(),
            PART_REQUETE_METADONNEE,
            PART_REQUETE_FDR,
            PART_REQUETE_TEXTE_INTEGRAL
        );

        when(NXQLQueryBuilder.getQuery(doc, whereClauseDefinition, null, new SortInfo[0]))
            .thenReturn(
                REQUETE_SIMPLE,
                REQUETE_COMPLEXE,
                REQUETE_SIMPLE,
                REQUETE_SIMPLE,
                REQUETE_METADONNEE,
                REQUETE_FDR,
                REQUETE_TEXTE_INTEGRAL
            );

        String result = ReponsesQueryModel.getAndRequeteParts(doc, toArray(modelNames));

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void getAndRequetePartsWithOnlyIndexModeTousAndEmptyRequeteParts() {
        List<String> modelNames = ImmutableList.of(IndexModeEnum.TOUS.getQueryModelName());

        when(NXQLQueryBuilder.getQuery(doc, whereClauseDefinition, null, new SortInfo[0]))
            .thenReturn(REQUETE_SIMPLE, REQUETE_SIMPLE);

        String result = ReponsesQueryModel.getAndRequeteParts(doc, toArray(modelNames));

        assertThat(result).isEmpty();
    }

    private static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }
}
