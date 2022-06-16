package fr.dila.reponses.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import fr.dila.ss.ui.bean.actions.CorbeilleActionDTO;
import org.junit.Test;

public class ReponsesCorbeilleActionDTOTest {

    @Test
    public void testConstructor() {
        CorbeilleActionDTO dto = new CorbeilleActionDTO();
        assertEquals(false, dto.getIsDossierLoadedInCorbeille());
    }

    @Test
    public void testSetter() {
        CorbeilleActionDTO dto = new CorbeilleActionDTO();
        assertEquals(false, dto.getIsDossierLoadedInCorbeille());

        dto.setIsDossierLoadedInCorbeille(true);
        assertEquals(true, dto.getIsDossierLoadedInCorbeille());
    }
}
