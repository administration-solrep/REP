package fr.dila.reponses.ui.th.bean.recherche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.VocabularyConstants;
import java.util.GregorianCalendar;
import org.junit.Test;

public class FeuilleRouteFormTest {

    @Test
    public void testConstructor() {
        FeuilleRouteForm form = new FeuilleRouteForm();

        assertNull(form.getDateDebutEtapeDebut());
        assertNull(form.getDateDebutEtapeFin());
        assertNull(form.getDateFinEtapeDebut());
        assertNull(form.getDateFinEtapeFin());
        assertNull(form.getDirection());
        assertNull(form.getEtapeObligatoire());
        assertNull(form.getIntituleFdr());
        assertNull(form.getPoste());
        assertNull(form.getRafraichissementAuto());
        assertEquals(VocabularyConstants.STATUS_ETAPE_DEFAULT_VALUE, form.getStatut());
        assertNull(form.getTypeEtape());
    }

    @Test
    public void testSetter() {
        FeuilleRouteForm form = new FeuilleRouteForm();

        form.setDateDebutEtapeDebut(new GregorianCalendar(2017, 00, 01));
        assertEquals(new GregorianCalendar(2017, 00, 01), form.getDateDebutEtapeDebut());
        form.setDateDebutEtapeFin(new GregorianCalendar(2018, 00, 01));
        assertEquals(new GregorianCalendar(2018, 00, 01), form.getDateDebutEtapeFin());
        form.setDateFinEtapeDebut(new GregorianCalendar(2019, 00, 01));
        assertEquals(new GregorianCalendar(2019, 00, 01), form.getDateFinEtapeDebut());
        form.setDateFinEtapeFin(new GregorianCalendar(2020, 00, 01));
        assertEquals(new GregorianCalendar(2020, 00, 01), form.getDateFinEtapeFin());
        form.setDirection("dir");
        assertEquals("dir", form.getDirection());
        form.setEtapeObligatoire(true);
        assertEquals(true, form.getEtapeObligatoire());
        form.setIntituleFdr("intitulé");
        assertEquals("intitulé", form.getIntituleFdr());
        form.setPoste("poste");
        assertEquals("poste", form.getPoste());
        form.setRafraichissementAuto(false);
        assertEquals(false, form.getRafraichissementAuto());
        form.setStatut("statut");
        assertEquals("statut", form.getStatut());
        form.setTypeEtape("type");
        assertEquals("type", form.getTypeEtape());
    }
}
