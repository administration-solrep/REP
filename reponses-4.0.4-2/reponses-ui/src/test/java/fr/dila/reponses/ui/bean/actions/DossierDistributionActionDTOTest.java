package fr.dila.reponses.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DossierDistributionActionDTOTest {

    @Test
    public void testConstructor() {
        DossierDistributionActionDTO dto = new DossierDistributionActionDTO();
        assertEquals(false, dto.getIsDossierLinkLoaded());
    }

    @Test
    public void testSetter() {
        DossierDistributionActionDTO dto = new DossierDistributionActionDTO();
        assertEquals(false, dto.getIsDossierLinkLoaded());

        dto.setIsDossierLinkLoaded(true);
        assertEquals(true, dto.getIsDossierLinkLoaded());
    }
}
