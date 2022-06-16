package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.bean.MigrationDetailDTO;
import java.util.ArrayList;
import org.junit.Test;

public class MigrationDTOTest {

    @Test
    public void testConstructor() {
        MigrationDTO dto = new MigrationDTO();
        assertEquals(new ArrayList<MigrationDetailDTO>(), dto.getDetails());
        assertNull(dto.getStatus());
    }

    @Test
    public void testSetter() {
        MigrationDTO dto = new MigrationDTO();
        ArrayList<MigrationDetailDTO> details = new ArrayList<>();
        details.add(new MigrationDetailDTO());
        dto.setDetails(details);
        dto.setStatus(ReponsesConstant.EN_COURS_STATUS);
        assertEquals(details, dto.getDetails());
        assertEquals(ReponsesConstant.EN_COURS_STATUS, dto.getStatus());
    }
}
