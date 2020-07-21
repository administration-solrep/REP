package fr.dila.reponses.core.flux;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.joda.time.DateTime;


public class TestDelaiCalculateur extends TestCase {
    
    public static int delaiParDefaultEnMois = 1; 
    
    public void testDelaiPositif(){
          testDelaiEcart(-5,"J-");
    }

    public void testDelaiNegatif(){
        testDelaiEcart(-32,"J+");
    }
    
    /**
     * On calcule une date de publication égale à la date du mois 
     */
    public void testDelaiExact(){
        GregorianCalendar dateJour = new GregorianCalendar();
        //on définit la date e publication à la date du mois 
        GregorianCalendar datePublication = new GregorianCalendar();
        datePublication.add(Calendar.MONTH,-delaiParDefaultEnMois);
        //on ajuste la date du jour 
        int day = datePublication.get(Calendar.DAY_OF_MONTH);
        dateJour.set(Calendar.DAY_OF_MONTH, day);
        DateTime dateJourAjustee  =  new DateTime(dateJour.getTime());
        //on récupère le délai
        String delai1 = DelaiCalculateur.getDelai(datePublication,delaiParDefaultEnMois,dateJourAjustee);
        assertEquals("J",delai1);
    }
    
    protected void testDelaiEcart(int dayToAdd, String delaiPositifOuNegatif){
        GregorianCalendar datePublication = new GregorianCalendar();
        //on fixe la date du jour au 25ème jour du mois
        datePublication.set(Calendar.DAY_OF_MONTH, 25);
        DateTime dateJourAjustee  =  new DateTime(datePublication.getTime());
        //on ajoute x jours à la date de publication
        datePublication.add(Calendar.DAY_OF_YEAR, dayToAdd);
        // On calcule le nombre de jour maximum du mois (31,30, 29 ou 28).
        int nbJourMois = datePublication.getActualMaximum(Calendar.DAY_OF_MONTH);
        //le délai attendu est égal au nombre de jour maximum du mois + le nombre de jour ajouté
        int delaiAttendu = Math.abs(nbJourMois + dayToAdd);
        //calcul du délai
        String delai1 = DelaiCalculateur.getDelai(datePublication,delaiParDefaultEnMois,dateJourAjustee);
        assertEquals(delaiPositifOuNegatif + delaiAttendu,delai1);
    }
}
