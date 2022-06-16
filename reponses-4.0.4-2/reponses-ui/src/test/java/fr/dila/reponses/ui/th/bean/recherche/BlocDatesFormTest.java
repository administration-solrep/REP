package fr.dila.reponses.ui.th.bean.recherche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.GregorianCalendar;
import org.junit.Test;

public class BlocDatesFormTest {

    @Test
    public void testConstructor() {
        BlocDatesForm form = new BlocDatesForm();

        assertNull(form.getDateJOQuestionDebut());
        assertNull(form.getDateJOQuestionFin());
        assertNull(form.getDateJOReponsesDebut());
        assertNull(form.getDateJOReponsesFin());
        assertNull(form.getDateSignalementDebut());
        assertNull(form.getDateSignalementFin());
    }

    @Test
    public void testSetter() {
        BlocDatesForm form = new BlocDatesForm();

        form.setDateJOQuestionDebut(new GregorianCalendar(2015, 00, 01));
        assertEquals(new GregorianCalendar(2015, 00, 01), form.getDateJOQuestionDebut());
        form.setDateJOQuestionFin(new GregorianCalendar(2016, 00, 01));
        assertEquals(new GregorianCalendar(2016, 00, 01), form.getDateJOQuestionFin());
        form.setDateJOReponsesDebut(new GregorianCalendar(2017, 00, 01));
        assertEquals(new GregorianCalendar(2017, 00, 01), form.getDateJOReponsesDebut());
        form.setDateJOReponsesFin(new GregorianCalendar(2018, 00, 01));
        assertEquals(new GregorianCalendar(2018, 00, 01), form.getDateJOReponsesFin());
        form.setDateSignalementDebut(new GregorianCalendar(2019, 00, 01));
        assertEquals(new GregorianCalendar(2019, 00, 01), form.getDateSignalementDebut());
        form.setDateSignalementFin(new GregorianCalendar(2020, 00, 01));
        assertEquals(new GregorianCalendar(2020, 00, 01), form.getDateSignalementFin());
    }
}
