package fr.dila.reponses.ui.th.bean.recherche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.api.constant.VocabularyConstants;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

public class RechercheGeneraleFormTest {

    @Test
    public void testConstructor() {
        RechercheGeneraleForm form = new RechercheGeneraleForm();
        assertNull(form.getAuteur());
        assertNull(form.getDirectionPilote());
        assertNull(form.getEtatQuestion());
        assertNull(form.getGroupePolitique());
        assertNotNull(form.getLegislature());
        assertEquals("15", form.getLegislature());
        assertNull(form.getMinistereAttributaire());
        assertNull(form.getMinistereInterroge());
        assertNull(form.getMinistereRattachement());
        assertNull(form.getNumeros());
        assertNotNull(form.getOrigineQuestion());
        assertEquals(
            Arrays.asList(VocabularyConstants.QUESTION_ORIGINE_AN, VocabularyConstants.QUESTION_ORIGINE_SENAT),
            form.getOrigineQuestion()
        );
        assertNotNull(form.getQuestions());
        assertEquals(VocabularyConstants.QUESTION_TYPE_QE, form.getQuestions());
        assertNull(form.getStatusReponse());
    }

    @Test
    public void testSetter() {
        RechercheGeneraleForm form = new RechercheGeneraleForm();

        form.setAuteur("auteur");
        assertEquals("auteur", form.getAuteur());
        form.setDirectionPilote("dir");
        assertEquals("dir", form.getDirectionPilote());
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        form.setEtatQuestion(list);
        assertEquals(list, form.getEtatQuestion());
        form.setGroupePolitique("grp");
        assertEquals("grp", form.getGroupePolitique());
        form.setLegislature("15");
        assertEquals("15", form.getLegislature());
        form.setMinistereAttributaire("minAtt");
        assertEquals("minAtt", form.getMinistereAttributaire());
        form.setMinistereInterroge("minInt");
        assertEquals("minInt", form.getMinistereInterroge());
        form.setMinistereRattachement("minRat");
        assertEquals("minRat", form.getMinistereRattachement());
        form.setNumeros("1");
        assertEquals("1", form.getNumeros());
        form.setOrigineQuestion(list);
        assertEquals(list, form.getOrigineQuestion());
        form.setQuestions("?");
        assertEquals("?", form.getQuestions());
        form.setStatusReponse(list);
        assertEquals(list, form.getStatusReponse());
    }
}
