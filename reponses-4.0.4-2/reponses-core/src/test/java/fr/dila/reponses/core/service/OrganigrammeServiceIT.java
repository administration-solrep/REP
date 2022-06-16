package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class, SolonMockitoFeature.class })
@Deploy("org.nuxeo.ecm.core.persistence")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-datasource.xml")
@Deploy("fr.dila.reponses.core:OSGI-INF/service/organigramme-framework.xml")
public class OrganigrammeServiceIT {
    @Mock
    @RuntimeService
    private JournalService journalService;

    @Inject
    private OrganigrammeService organigrammeService;

    @Inject
    private CoreSession session;

    @Test
    public void testCreate() {
        GouvernementNodeImpl gvtnode = new GouvernementNodeImpl();
        gvtnode.setId("gvt1");

        OrganigrammeNode node = organigrammeService.createNode(gvtnode);
        assertThat(node).isInstanceOf(GouvernementNode.class);
        assertEquals("gvt1", node.getId());
    }

    @Test
    public void testLock() {
        UniteStructurelleNode unitenode = new UniteStructurelleNodeImpl();
        unitenode.setId("unite1");

        OrganigrammeNode node = organigrammeService.createNode(unitenode);
        assertThat(node).isInstanceOf(UniteStructurelleNode.class);
        assertEquals("unite1", node.getId());

        assertTrue(organigrammeService.lockOrganigrammeNode(session, node));

        assertEquals(1, organigrammeService.getLockedNodes().size());
        Assertions
            .assertThat(organigrammeService.getLockedNodes())
            .extracting(OrganigrammeNode::getId)
            .containsExactly("unite1");

        assertTrue(organigrammeService.unlockOrganigrammeNode(node));
        Assertions.assertThat(organigrammeService.getLockedNodes()).isEmpty();
    }

    @Test
    public void testGetOrganigrameLikeLabel() {
        EntiteNode entiteNode = new EntiteNodeImpl();
        entiteNode.setId("12345678");
        entiteNode.setLabel("Ministère de l'éducation nationale");
        OrganigrammeNode node = organigrammeService.createNode(entiteNode);

        EntiteNode entiteNode2 = new EntiteNodeImpl();
        entiteNode2.setId("12345679");
        entiteNode2.setLabel("Ministère de l'intérieur");
        organigrammeService.createNode(entiteNode2);

        List<OrganigrammeNode> foundNodes = organigrammeService.getOrganigrameLikeLabels(
            "édu",
            Collections.singletonList(OrganigrammeType.MINISTERE)
        );
        Assertions.assertThat(foundNodes).containsExactly(node);
    }
}
