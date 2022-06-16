package fr.dila.reponses.core.service;

import fr.dila.ss.core.flux.EcheanceCalculateur;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;

public class TestEcheanceCalculateur {

    @Test
    public void testEcheance() {
        // Echéance de 1j, début 22/11/2012
        Calendar calDebut = Calendar.getInstance();
        calDebut.set(2012, 10, 22);

        Calendar calFin = EcheanceCalculateur.getEcheanceCompteUniquementJourOuvre(calDebut, 1);
        Assert.assertEquals(23, calFin.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(10, calFin.get(Calendar.MONTH));
        Assert.assertEquals(2012, calFin.get(Calendar.YEAR));

        // Echéance de 2j, début 22/11/2012
        calDebut = Calendar.getInstance();
        calDebut.set(2012, 10, 22);

        calFin = EcheanceCalculateur.getEcheanceCompteUniquementJourOuvre(calDebut, 2);
        Assert.assertEquals(26, calFin.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(10, calFin.get(Calendar.MONTH));
        Assert.assertEquals(2012, calFin.get(Calendar.YEAR));

        // Echéance de 15j, début 30/07/2012
        calDebut = Calendar.getInstance();
        calDebut.set(2012, 6, 30);

        calFin = EcheanceCalculateur.getEcheanceCompteUniquementJourOuvre(calDebut, 15);
        Assert.assertEquals(20, calFin.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(7, calFin.get(Calendar.MONTH));
        Assert.assertEquals(2012, calFin.get(Calendar.YEAR));
    }
}
