package fr.dila.reponses.core.recherche.query;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import fr.dila.st.core.service.STServiceLocator;


public class TestReponsesQueryAssembler extends ReponsesRepositoryTestCase{
    
    private UFNXQLQueryAssembler assembler;
    private JointureService jointureService;
    
    public void setUp() throws Exception{
        super.setUp();
        jointureService = STServiceLocator.getJointureService();
        assembler = (UFNXQLQueryAssembler) jointureService.getDefaultQueryAssembler();
    }
    
    public void testCorrespondanceEquality() throws ClientException {
    	openSession();
        CorrespondenceDescriptor c0 = new CorrespondenceDescriptor();
        c0.setDocument("Parapheur");
        c0.setDocPrefix("p.");
        c0.setEmplacement("AFTER_WHERE");
        CorrespondenceDescriptor c1 = new CorrespondenceDescriptor();
        c1.setDocument("Parapheur");
        c1.setDocPrefix("p.");
        c1.setEmplacement("AFTER_WHERE");
        assertEquals(c0,c1);
        // Verification contains
        List<CorrespondenceDescriptor> l = new ArrayList<CorrespondenceDescriptor>();
        l.add(c0);
        assertEquals(true,l.contains(c0));
        assertEquals(true,l.contains(c1));
        closeSession();
    }
    
    public void testDontAddCorrespondenceAlreadyAdded() throws ClientException{
    	openSession();
        CorrespondenceDescriptor c1 = new CorrespondenceDescriptor();
        c1.setDocument("Dossier");
        c1.setDocPrefix("d.");
        c1.setEmplacement("AFTER_WHERE");
        String whereClause = "r.rep.txtReponse LIKE 'Hello' AND d.dos:" + 
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507'";
        assembler.setWhereClause(whereClause);
        assertEquals(5,assembler.get_useful_correspondences(whereClause).size());
        assertEquals(true,assembler.get_useful_correspondences(whereClause).contains(c1));
        closeSession();
    }
    
}
