package fr.dila.reponses.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ecm.platform.actions.Action;

public class FdrDTOTest {

    @Test
    public void testConstructor() {
        FdrDTO dto = new FdrDTO();
        assertNull(dto.getTable());
        assertNotNull(dto.getTabActions());
        assertThat(dto.getTabActions()).isEmpty();
    }

    @Test
    public void testSetter() {
        FdrDTO dto = new FdrDTO();
        assertNull(dto.getTable());
        assertNotNull(dto.getTabActions());
        assertThat(dto.getTabActions()).isEmpty();

        FdrTableDTO table = new FdrTableDTO();
        table.setTotalNbLevel(3);
        dto.setTable(table);
        assertNotNull(dto.getTable());
        assertEquals(new Integer(3), dto.getTable().getTotalNbLevel());

        List<Action> actions = new ArrayList<>();
        actions.add(new Action("monAction", null));
        dto.setTabActions(actions);
        assertNotNull(dto.getTabActions());
        assertEquals(1, dto.getTabActions().size());
        assertEquals("monAction", dto.getTabActions().get(0).getId());
    }
}
