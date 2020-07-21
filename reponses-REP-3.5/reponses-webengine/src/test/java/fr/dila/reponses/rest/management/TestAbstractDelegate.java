package fr.dila.reponses.rest.management;

import junit.framework.TestCase;

public class TestAbstractDelegate extends TestCase {

    public void testStripCDATA() {
        
        final String nullStr = null;
        assertEquals(nullStr, AbstractDelegate.stripCDATA(nullStr));
        
        final String emptyStr = "";
        assertEquals(emptyStr, AbstractDelegate.stripCDATA(emptyStr));

        final String content = "mon contenu";
        final String testStr[] = new String[]{
                content,
                "<![CDATA[" + content + "]]&gt;",
                "   <![CDATA[" + content + "]]&gt;",
                "   <![CDATA[" + content + "]]&gt;   ",
                "<![CDATA[" + content + "]]&gt;   ",
                "<![CDATA[" + content + "]]&gt;   autre données",        
                "<![CDATA[" + content + "]]&gt;  ]]&gt; autre données"
        };
        
        for(String str : testStr){
            assertEquals(content, AbstractDelegate.stripCDATA(str));
        }
       
        final String errorEnd = "<![CDATA[" + content + "]]";
        
        try { 
            AbstractDelegate.stripCDATA(errorEnd);
            fail();
        } catch(IllegalStateException e){
            // expected exception
        }
        
    }
    
}
