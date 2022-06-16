package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class DossierDTOTest {

    @Test
    public void testConstructor() {
        ConsultDossierDTO dto = new ConsultDossierDTO();
        assertNull(dto.getId());
        assertNull(dto.getQuestionInfo());
        assertEquals(new ArrayList<String>(), dto.getNextStepLabel());
        assertEquals(new ArrayList<String>(), dto.getActualStepLabel());
        assertEquals(false, dto.getIsVerrouille());
        assertEquals("", dto.getLockTime());
        assertEquals("", dto.getLockOwner());
        assertEquals(false, dto.getIsDone());
        assertNull(dto.getDateLastStep());
    }

    @Test
    public void testSetter() {
        ConsultDossierDTO dto = new ConsultDossierDTO();
        assertNull(dto.getId());
        assertNull(dto.getQuestionInfo());
        assertEquals(new ArrayList<String>(), dto.getNextStepLabel());
        assertEquals(new ArrayList<String>(), dto.getActualStepLabel());
        assertEquals(false, dto.getIsVerrouille());
        assertEquals("", dto.getLockTime());
        assertEquals("", dto.getLockOwner());

        dto.setId("monId");
        assertEquals("monId", dto.getId());

        dto.setQuestionInfo(new QuestionHeaderDTO());
        assertNotNull(dto.getQuestionInfo());

        List<String> list = new ArrayList<>();
        list.add("label");
        dto.setNextStepLabel(list);
        assertEquals("label", dto.getNextStepLabel().get(0));

        List<String> actualStepLabel = Arrays.asList("actuallabel");
        dto.setActualStepLabel(actualStepLabel);
        assertEquals(actualStepLabel, dto.getActualStepLabel());

        dto.setIsVerrouille(true);
        assertEquals(true, dto.getIsVerrouille());

        dto.setLockTime("lock");
        assertEquals("lock", dto.getLockTime());

        dto.setLockOwner("owner");
        assertEquals("owner", dto.getLockOwner());

        dto.setIsDone(true);
        assertEquals(true, dto.getIsDone());

        dto.setDateLastStep(new Date(0));
        assertEquals(new Date(0), dto.getDateLastStep());
    }
}
