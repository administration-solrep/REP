package fr.dila.reponses.core.recherche.query;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestReponsesQueryAssembler {
    @Inject
    private CoreFeature coreFeature;

    private UFNXQLQueryAssembler assembler;

    private JointureService jointureService;

    @Before
    public void setUp() {
        jointureService = STServiceLocator.getJointureService();
        assembler = (UFNXQLQueryAssembler) jointureService.getDefaultQueryAssembler();
    }

    @Test
    public void testCorrespondanceEquality() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            CorrespondenceDescriptor c0 = new CorrespondenceDescriptor();
            c0.setDocument("Parapheur");
            c0.setDocPrefix("p.");
            c0.setEmplacement("AFTER_WHERE");
            CorrespondenceDescriptor c1 = new CorrespondenceDescriptor();
            c1.setDocument("Parapheur");
            c1.setDocPrefix("p.");
            c1.setEmplacement("AFTER_WHERE");
            Assert.assertEquals(c0, c1);
            // Verification contains
            List<CorrespondenceDescriptor> l = new ArrayList<>();
            l.add(c0);
            Assert.assertEquals(true, l.contains(c0));
            Assert.assertEquals(true, l.contains(c1));
        }
    }

    @Test
    public void testDontAddCorrespondenceAlreadyAdded() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            CorrespondenceDescriptor c1 = new CorrespondenceDescriptor();
            c1.setDocument("Dossier");
            c1.setDocPrefix("d.");
            c1.setEmplacement("AFTER_WHERE");
            String whereClause =
                "r.rep.txtReponse LIKE 'Hello' AND d.dos:" +
                DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
                " = '50000507'";
            assembler.setWhereClause(whereClause);
            Assert.assertEquals(5, assembler.get_useful_correspondences(whereClause).size());
            Assert.assertEquals(true, assembler.get_useful_correspondences(whereClause).contains(c1));
        }
    }
}
