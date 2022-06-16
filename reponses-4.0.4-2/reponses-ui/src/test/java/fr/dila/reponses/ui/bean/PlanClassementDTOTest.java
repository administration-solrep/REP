package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.ui.bean.TreeElementDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class PlanClassementDTOTest {

    @Test
    public void testConstructor() {
        PlanClassementDTO dto = new PlanClassementDTO();
        assertNotNull(dto);
        assertNotNull(dto.getChilds());
        assertEquals(0, dto.getChilds().size());
        assertEquals(DossierConstants.ORIGINE_QUESTION_AN, dto.getAssemblee());
    }

    @Test
    public void testSetter() {
        PlanClassementDTO dto = new PlanClassementDTO();
        assertNotNull(dto);
        assertNotNull(dto.getChilds());
        assertEquals(0, dto.getChilds().size());
        assertEquals(DossierConstants.ORIGINE_QUESTION_AN, dto.getAssemblee());

        dto.setChilds(null);
        assertNull(dto.getChilds());

        List<TreeElementDTO> liste = new ArrayList<>();
        liste.add(new TreeElementDTO());
        liste.add(new TreeElementDTO());
        dto.setChilds(liste);
        assertNotNull(dto.getChilds());
        assertEquals(2, dto.getChilds().size());

        dto.setAssemblee("assembleeTest");
        assertEquals("assembleeTest", dto.getAssemblee());
    }
}
