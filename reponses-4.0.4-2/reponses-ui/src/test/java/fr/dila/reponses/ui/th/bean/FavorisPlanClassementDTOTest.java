package fr.dila.reponses.ui.th.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.ui.bean.FavorisPlanClassementDTO;
import fr.dila.st.ui.bean.TreeElementDTO;
import org.junit.Test;

public class FavorisPlanClassementDTOTest {

    @Test
    public void testContructeur() {
        FavorisPlanClassementDTO dto = new FavorisPlanClassementDTO();
        assertNull(dto.getAssemblee());
        assertNull(dto.getSenat());
    }

    @Test
    public void testSetters() {
        FavorisPlanClassementDTO dto = new FavorisPlanClassementDTO();
        assertNull(dto.getAssemblee());
        assertNull(dto.getSenat());
        TreeElementDTO assemblee = new TreeElementDTO();
        assemblee.setLabel("ASSEMBLEE NATIONALE");
        assemblee.setKey("AN");
        assemblee.setIsBold(true);
        assemblee.setIsOpen(true);
        dto.setAssemblee(assemblee);
        TreeElementDTO senat = new TreeElementDTO();
        senat.setLabel("SENAT");
        senat.setKey("SENAT");
        senat.setIsBold(true);
        senat.setIsOpen(true);
        dto.setSenat(senat);
        assertNotNull(dto.getAssemblee());
        assertNotNull(dto.getSenat());
        assertThat(dto.getAssemblee().getChilds()).isEmpty();
        assertThat(dto.getSenat().getChilds()).isEmpty();
    }
}
