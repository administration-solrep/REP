package fr.dila.reponses.core.flux;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.util.DateUtil;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

public class DelaiCalculateurTest {
    private static final int DELAI_PAR_DEFAULT_EN_MOIS = 1;

    @Test
    public void testDelaiPositif() {
        testDelaiEcart(-5, "J-");
    }

    @Test
    public void testDelaiNegatif() {
        testDelaiEcart(-32, "J+");
    }

    /**
     * On calcule une date de publication égale à la date du mois
     */
    @Test
    public void testDelaiExact() {
        GregorianCalendar dateJour = new GregorianCalendar();
        //on définit la date e publication à la date du mois
        GregorianCalendar datePublication = new GregorianCalendar();
        datePublication.add(Calendar.MONTH, -DELAI_PAR_DEFAULT_EN_MOIS);
        //on ajuste la date du jour
        int day = datePublication.get(Calendar.DAY_OF_MONTH);
        dateJour.set(Calendar.DAY_OF_MONTH, day);
        LocalDate dateJourAjustee = DateUtil.gregorianCalendarToLocalDate(dateJour);
        //on récupère le délai
        String delai1 = DelaiCalculateur.getDelai(datePublication, DELAI_PAR_DEFAULT_EN_MOIS, dateJourAjustee);
        assertThat(delai1).isEqualTo("J");
    }

    private void testDelaiEcart(int dayToAdd, String delaiPositifOuNegatif) {
        GregorianCalendar datePublication = new GregorianCalendar();
        //on fixe la date du jour au 25ème jour du mois
        datePublication.set(Calendar.DAY_OF_MONTH, 25);
        LocalDate dateJourAjustee = DateUtil.gregorianCalendarToLocalDate(datePublication);
        //on ajoute x jours à la date de publication
        datePublication.add(Calendar.DAY_OF_YEAR, dayToAdd);
        // On calcule le nombre de jour maximum du mois (31,30, 29 ou 28).
        int nbJourMois = datePublication.getActualMaximum(Calendar.DAY_OF_MONTH);
        //le délai attendu est égal au nombre de jour maximum du mois + le nombre de jour ajouté
        int delaiAttendu = Math.abs(nbJourMois + dayToAdd);
        //calcul du délai
        String delai1 = DelaiCalculateur.getDelai(datePublication, DELAI_PAR_DEFAULT_EN_MOIS, dateJourAjustee);
        assertThat(delai1).isEqualTo(delaiPositifOuNegatif + delaiAttendu);
    }
}
