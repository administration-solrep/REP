package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.core.recherche.TexteIntegralEscaper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.search.api.client.querymodel.Escaper;

public class TestTexteIntegralEscaper {

    @Test
    public void testProtectionSingleQuote() {
        Escaper escaper = new TexteIntegralEscaper();
        String mot_test = "lutte contre l'exclusion";
        Assert.assertEquals("lutte contre l\\\\'exclusion", escaper.escape(mot_test));

        mot_test = "mot-clés";
        Assert.assertEquals("mot-clés", escaper.escape(mot_test));
    }
}
