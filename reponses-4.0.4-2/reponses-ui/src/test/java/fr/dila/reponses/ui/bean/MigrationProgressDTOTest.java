package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.ss.ui.bean.MigrationProgressDTO;
import org.junit.Test;

public class MigrationProgressDTOTest {

    @Test
    public void testConstructor() {
        MigrationProgressDTO dto = new MigrationProgressDTO();
        assertEquals(0, dto.getNbStart());
        assertEquals(0, dto.getNbCurrent());
        assertEquals(0, dto.getNbTotal());
        assertNull(dto.getStatus());
    }

    @Test
    public void testConstructorWithParams() {
        MigrationProgressDTO dto = new MigrationProgressDTO(1, 2, 3, ReponsesConstant.EN_COURS_STATUS);
        assertEquals(1, dto.getNbStart());
        assertEquals(2, dto.getNbCurrent());
        assertEquals(3, dto.getNbTotal());
        assertEquals(ReponsesConstant.EN_COURS_STATUS, dto.getStatus());
    }

    @Test
    public void testSetter() {
        MigrationProgressDTO dto = new MigrationProgressDTO();

        dto.setNbStart(11);
        dto.setNbCurrent(22);
        dto.setNbTotal(33);
        dto.setStatus(ReponsesConstant.TERMINEE_STATUS);

        assertEquals(11, dto.getNbStart());
        assertEquals(22, dto.getNbCurrent());
        assertEquals(33, dto.getNbTotal());
        assertEquals(ReponsesConstant.TERMINEE_STATUS, dto.getStatus());
    }
}
