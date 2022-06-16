package fr.dila.reponses.ui.helper;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_LABEL;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static fr.dila.reponses.api.constant.VocabularyConstants.LEGISLATURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(ReponsesServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class ReponsesProviderHelperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private DocumentModel legislatureDoc1;

    @Mock
    private DocumentModel legislatureDoc2;

    @Mock
    private ReponsesVocabularyService reponsesVocabularyService;

    @Test
    public void getLegislatureNames() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getVocabularyService()).thenReturn(reponsesVocabularyService);

        DocumentModelList legislatureDocs = new DocumentModelListImpl(newArrayList(legislatureDoc1, legislatureDoc2));
        when(reponsesVocabularyService.getAllEntry(LEGISLATURE)).thenReturn(legislatureDocs);

        String legislature1Id = "14";
        String legislature1Label = "14eme législature";
        when(legislatureDoc1.getId()).thenReturn(legislature1Id);
        when(legislatureDoc1.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_LABEL)).thenReturn(legislature1Label);

        String legislature2Id = "15";
        String legislature2Label = "15eme législature";
        when(legislatureDoc2.getId()).thenReturn(legislature2Id);
        when(legislatureDoc2.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_LABEL)).thenReturn(legislature2Label);

        Map<String, String> results = ReponsesProviderHelper.getLegislatureNames();

        assertThat(results)
            .containsExactly(entry(legislature1Id, legislature1Label), entry(legislature2Id, legislature2Label));
    }
}
