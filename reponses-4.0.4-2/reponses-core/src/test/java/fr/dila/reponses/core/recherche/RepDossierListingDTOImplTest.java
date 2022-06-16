package fr.dila.reponses.core.recherche;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class RepDossierListingDTOImplTest {
    private RepDossierListingDTO dto;

    @Before
    public void setUp() {
        dto = new RepDossierListingDTO();
    }

    @Test
    public void getEtatWithAllValues() {
        dto.setRenouvelle(true);
        dto.setSignale(true);
        dto.setUrgent(true);

        List<String> etat = dto.getEtat();

        assertThat(etat).containsExactly("renouvellé", "signalé", "rappelé");
    }

    @Test
    public void getEtatWithOneValue() {
        dto.setRenouvelle(false);
        dto.setSignale(true);
        dto.setUrgent(false);

        List<String> etat = dto.getEtat();

        assertThat(etat).containsExactly("signalé");
    }

    @Test
    public void getEtatWithoutValue() {
        dto.setRenouvelle(false);
        dto.setSignale(false);
        dto.setUrgent(false);

        List<String> etat = dto.getEtat();

        assertTrue(etat.isEmpty());
    }
}
