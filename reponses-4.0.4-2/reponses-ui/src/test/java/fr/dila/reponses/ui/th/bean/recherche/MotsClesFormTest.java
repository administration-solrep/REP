package fr.dila.reponses.ui.th.bean.recherche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import org.junit.Test;

public class MotsClesFormTest {

    @Test
    public void testConstructor() {
        MotsClesForm form = new MotsClesForm();

        assertNull(form.getIndexAn());
        assertNull(form.getIndexMinistere());
        assertNull(form.getIndexSenat());
        assertNull(form.getRechercheSur());
    }

    @Test
    public void testSetter() {
        MotsClesForm form = new MotsClesForm();

        ArrayList<String> list = new ArrayList<>();

        list.add("a");
        form.setIndexAn(list);
        assertEquals(list, form.getIndexAn());
        list.add("b");
        form.setIndexMinistere(list);
        assertEquals(list, form.getIndexMinistere());
        list.add("c");
        form.setIndexSenat(list);
        assertEquals(list, form.getIndexSenat());
        form.setRechercheSur("tout");
        assertEquals("tout", form.getRechercheSur());
    }
}
