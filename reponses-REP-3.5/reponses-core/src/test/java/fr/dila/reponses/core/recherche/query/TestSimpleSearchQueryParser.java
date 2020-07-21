package fr.dila.reponses.core.recherche.query;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.constant.DossierConstants;

public class TestSimpleSearchQueryParser extends TestCase {

    private static final Log log = LogFactory.getLog(TestSimpleSearchQueryParser.class);

 
    public void testExtract(){
        String testStr[][] = {
        { "an 1", DossierConstants.ORIGINE_QUESTION_AN, "1" },
        {"AN 12", DossierConstants.ORIGINE_QUESTION_AN, "12" },
        {"SE 13 ", DossierConstants.ORIGINE_QUESTION_SENAT, "13" },
        {"SENAT 1", DossierConstants.ORIGINE_QUESTION_SENAT, "1" },
        {"SE  *", DossierConstants.ORIGINE_QUESTION_SENAT, "%" },
        {"SE  *", DossierConstants.ORIGINE_QUESTION_SENAT, "%" },
        {"SE  *", DossierConstants.ORIGINE_QUESTION_SENAT, "%" },
        {"SE 1*", DossierConstants.ORIGINE_QUESTION_SENAT, "1%" },
        {"SE 1*50", DossierConstants.ORIGINE_QUESTION_SENAT, "1%50" },
        {"AN *50", DossierConstants.ORIGINE_QUESTION_AN, "%50" },
        {"AN 5*6", DossierConstants.ORIGINE_QUESTION_AN, "5%6" },
        {"*50", "%", "%50"},
        {" *50", "%", "%50"},
        {" *50 ", "%", "%50"},
        {"1", "%", "1"},
        {"1 ", "%", "1"},        
        {"sénat  56 ", DossierConstants.ORIGINE_QUESTION_SENAT, "56" },
        {" SÉNAT 5*6", DossierConstants.ORIGINE_QUESTION_SENAT, "5%6" },
        };
        
        
        for(int i = 0; i < testStr.length; ++i){
            log.debug("process ["+testStr[i][0]+"]");
            String origine = SimpleSearchQueryParser.getOrigineQuestionToSearch(testStr[i][0]);
            String nbexpr = SimpleSearchQueryParser.getNumberQuestionToSearch(testStr[i][0]);
            
            Assert.assertEquals(testStr[i][1], origine);    
            Assert.assertEquals(testStr[i][2], nbexpr);
        }
        
        
    }
    
}
