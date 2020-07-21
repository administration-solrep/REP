package fr.dila.reponses.core.recherche.traitement;

import junit.framework.TestCase;


public class TestNumeroQuestionTraitement extends TestCase {
	
    private NumeroQuestionTraitement traitement;
    
    @Override
    public void setUp() throws Exception{
        traitement = new NumeroQuestionTraitement();
    }
    
    public void testEmpty(){
        String chaineNumero = "";
        assertEquals("",traitement.parse(chaineNumero));
        chaineNumero = null;
        assertEquals(traitement.parse(chaineNumero),null);
    }

    public void testParsingIntervalle(){
        String chaineNumero = "1546-1559";
        assertEquals(traitement.parse(chaineNumero),"(q.qu:numeroQuestion BETWEEN 1546 AND 1559)");
    }
    
    public void testNumero_index(){
        String chaineNumero = "1546";
        assertEquals(traitement.parse(chaineNumero),"(q.ecm:fulltext_idQuestion LIKE '1546')");
    }
    
    public void testNumeros(){
        String chaineNumero = "50;1546";
        assertEquals(traitement.parse(chaineNumero),"(q.ecm:fulltext_idQuestion LIKE '50') OR (q.qu:numeroQuestion = 1546)");
    }

    public void testParsingNumero(){
        String chaineNumero = "1546-1559;508; 559; 805-906";
        assertEquals(traitement.parse(chaineNumero),"(q.qu:numeroQuestion BETWEEN 1546 AND 1559) OR (q.qu:numeroQuestion = 508) OR (q.qu:numeroQuestion = 559) OR (q.qu:numeroQuestion BETWEEN 805 AND 906)");
    }

}
