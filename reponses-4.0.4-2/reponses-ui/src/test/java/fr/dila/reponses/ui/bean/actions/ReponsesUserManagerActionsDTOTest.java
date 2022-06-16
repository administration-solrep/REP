package fr.dila.reponses.ui.bean.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import org.junit.Test;

public class ReponsesUserManagerActionsDTOTest {

    @Test
    public void testConstructor() {
        STUserManagerActionsDTO dto = new STUserManagerActionsDTO();
        assertFalse(dto.getIsCurrentUserPermanent());
    }

    @Test
    public void testSetter() {
        STUserManagerActionsDTO dto = new STUserManagerActionsDTO();
        assertFalse(dto.getIsCurrentUserPermanent());

        dto.setIsCurrentUserPermanent(true);
        assertEquals(true, dto.getIsCurrentUserPermanent());
    }
}
