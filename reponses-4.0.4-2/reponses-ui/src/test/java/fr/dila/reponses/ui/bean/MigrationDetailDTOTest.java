package fr.dila.reponses.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.ss.ui.bean.MigrationDetailDTO;
import fr.dila.st.api.organigramme.OrganigrammeType;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class MigrationDetailDTOTest {
    private static final String OLD_MIN_ID = "oldMinID";
    private static final String NEW_MIN_ID = "newMinID";
    private static final String OLD_MIN = "oldMin";
    private static final String NEW_MIN = "newMin";
    private static final String OLD_ID = "oldID";
    private static final String NEW_ID = "newID";
    private static final String OLD = "old";
    private static final String NEW = "new";

    @Test
    public void testConstructor() {
        MigrationDetailDTO dto = new MigrationDetailDTO();
        assertFalse(dto.getDeleteOld());
        assertNull(dto.getStatus());
        assertNull(dto.getErrorMessage());
        assertNull(dto.getMigrationId());
        assertNull(dto.getMigrationType());
        assertEquals(StringUtils.EMPTY, dto.getNewElement());
        assertEquals(StringUtils.EMPTY, dto.getOldElement());
        assertEquals(StringUtils.EMPTY, dto.getNewElementId());
        assertEquals(StringUtils.EMPTY, dto.getOldElementId());
        assertEquals(StringUtils.EMPTY, dto.getNewMinistere());
        assertEquals(StringUtils.EMPTY, dto.getOldMinistere());
        assertEquals(StringUtils.EMPTY, dto.getNewMinistereId());
        assertEquals(StringUtils.EMPTY, dto.getOldMinistereId());
        assertEquals(new HashMap<>(), dto.getMapNewElement());
        assertEquals(new HashMap<>(), dto.getMapOldElement());
        assertEquals(new HashMap<>(), dto.getMapNewMinistere());
        assertEquals(new HashMap<>(), dto.getMapOldMinistere());
    }

    @Test
    public void testSetter() {
        MigrationDetailDTO dto = new MigrationDetailDTO();

        dto.setDeleteOld(true);
        dto.setStatus(ReponsesConstant.EN_COURS_STATUS);
        dto.setErrorMessage("erreur");
        dto.setMigrationId("1");
        dto.setMigrationType(OrganigrammeType.DIRECTION.getValue());
        dto.setNewElement(NEW);
        dto.setOldElement(OLD);
        dto.setNewElementId(NEW_ID);
        dto.setOldElementId(OLD_ID);
        dto.setNewMinistere(NEW_MIN);
        dto.setOldMinistere(OLD_MIN);
        dto.setNewMinistereId(NEW_MIN_ID);
        dto.setOldMinistereId(OLD_MIN_ID);
        dto.setMapNewElement(Collections.singletonMap(NEW_ID, NEW));
        dto.setMapOldElement(Collections.singletonMap(OLD_ID, OLD));
        dto.setMapNewMinistere(Collections.singletonMap(NEW_MIN_ID, NEW_MIN));
        dto.setMapOldMinistere(Collections.singletonMap(OLD_MIN_ID, OLD_MIN));

        assertTrue(dto.getDeleteOld());
        assertEquals(ReponsesConstant.EN_COURS_STATUS, dto.getStatus());
        assertEquals("erreur", dto.getErrorMessage());
        assertEquals("1", dto.getMigrationId());
        assertEquals(OrganigrammeType.DIRECTION.getValue(), dto.getMigrationType());
        assertEquals(NEW, dto.getNewElement());
        assertEquals(OLD, dto.getOldElement());
        assertEquals(NEW_ID, dto.getNewElementId());
        assertEquals(OLD_ID, dto.getOldElementId());
        assertEquals(NEW_MIN, dto.getNewMinistere());
        assertEquals(OLD_MIN, dto.getOldMinistere());
        assertEquals(NEW_MIN_ID, dto.getNewMinistereId());
        assertEquals(OLD_MIN_ID, dto.getOldMinistereId());
        assertThat(dto.getMapNewElement()).containsExactly(entry(NEW_ID, NEW));
        assertThat(dto.getMapOldElement()).containsExactly(entry(OLD_ID, OLD));
        assertThat(dto.getMapNewMinistere()).containsExactly(entry(NEW_MIN_ID, NEW_MIN));
        assertThat(dto.getMapOldMinistere()).containsExactly(entry(OLD_MIN_ID, OLD_MIN));
    }
}
