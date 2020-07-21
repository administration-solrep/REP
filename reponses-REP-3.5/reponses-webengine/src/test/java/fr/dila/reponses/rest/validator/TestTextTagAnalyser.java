package fr.dila.reponses.rest.validator;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class TestTextTagAnalyser extends TestCase{

    public void testTagExtraction(){
        
        final String[] testStrings = new String[] { 
                "A b c <d> e f", 
                "A b c <d/> e f", 
                "A b c < d/> e f", 
                "<d value=\"une valeur\"> e f", 
                "A b c <d value=\"une valeur\"/> e f",
                "A b c </d> f ",
                "A b c < d e f > f ",
                "A b c <d value=\"une valeur\" > e </d>f"
        };
        
        TextTagAnalyser tta = new TextTagAnalyser();
        
        for(int i = 0; i < testStrings.length; ++i){
            Set<String> found = tta.extractAllTag(testStrings[i]);
            assertEquals("Unexpected result for " + testStrings[i], 1, found.size());
            assertTrue("Unexpected result for " + testStrings[i], found.contains("d"));
        }
        
        final String testString2 = "A b c <d value=\"une valeur\" > e </a>f"; 
        Set<String> found = tta.extractAllTag(testString2);
        assertEquals(2, found.size());
        assertTrue(found.contains("a"));
        assertTrue(found.contains("d"));
        
        tta.startAnalyse(testString2);
        String tag = tta.nextTag();
        assertEquals("d", tag);
        tag = tta.nextTag();
        assertEquals("a", tag);        
    }
    
    public void testCheckTag(){
        Set<String> okTags = new HashSet<String>();
        okTags.add("eb");
        okTags.add("ec");
        okTags.add("ed");
        
        final String testString1 = "a b c d";
        final String testString2 = "a b c d <eb>";
        final String testString3 = "a b c d <ed eg>";        
        final String testString4 = "a b c d <ee eg> <eb> </ee>";
        
        TextTagAnalyser tta = new TextTagAnalyser();
        assertTrue(tta.checkTags(testString1, okTags));
        assertTrue(tta.checkTags(testString2, okTags));
        assertTrue(tta.checkTags(testString3, okTags));
        
        assertFalse(tta.checkTags(testString4, okTags));
        assertEquals("ee", tta.getNoConformTag());
        
        
    }
    
}
