package fr.dila.reponses.ui.th.bean.recherche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.RequeteConstants;
import java.util.ArrayList;
import org.junit.Test;

public class TexteIntegralFormTest {

    @Test
    public void testConstructor() {
        TexteIntegralForm form = new TexteIntegralForm();

        assertNull(form.getExpression());
        assertNull(form.getRechercheExacte());
        assertNotNull(form.getRechercherDans());
        assertEquals(1, form.getRechercherDans().size());
        assertEquals(RequeteConstants.DANS_TEXTE_QUESTION, form.getRechercherDans().get(0));
    }

    @Test
    public void testSetter() {
        TexteIntegralForm form = new TexteIntegralForm();

        form.setExpression("expression");
        assertEquals("expression", form.getExpression());

        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        form.setRechercheExacte(list);
        assertEquals(list, form.getRechercheExacte());

        list.add("c");
        form.setRechercherDans(list);
        assertEquals(list, form.getRechercherDans());
    }
}
