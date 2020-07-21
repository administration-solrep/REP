package fr.dila.reponses.core.recherche.query;

import fr.dila.reponses.api.constant.DossierConstants;


/**
 * Utiliser pour extraire l'origine et le numero de question ou l'expression recherché
 * 
 * 
 * @author spesnel
 *
 */
public class SimpleSearchQueryParser {
    
    /**
     * retourne l'orgine de la question recherché
     * AN si le debut est an/AN
     * SENAT sil edebut est SENAT ou SE ou sénat en minuscule/majuscule
     * @return
     */
    public static String getOrigineQuestionToSearch(String expr){
        if(expr != null){
            String exprlc = expr.trim().toLowerCase();
            if(exprlc.startsWith("an")){
                return DossierConstants.ORIGINE_QUESTION_AN;
            } else if(exprlc.startsWith("se")){
                return DossierConstants.ORIGINE_QUESTION_SENAT;
            } else if(exprlc.startsWith("sénat")){
                return DossierConstants.ORIGINE_QUESTION_SENAT;
            }
        }
        return "%";
    }
    
    /**
     * retourne la fin de l'expresion apres le debut qui est an ou SENAT
     * @return
     */
    public static String getNumberQuestionToSearch(String expr){
        if(expr != null){
            String exprlc = expr.trim().toLowerCase();
            String res = exprlc;
            if(exprlc.startsWith("an")){
                res = exprlc.substring(2);
            } else if(exprlc.startsWith("senat") || exprlc.startsWith("sénat")){
                res = exprlc.substring(5);
            } else if(exprlc.startsWith("se")){
                res = exprlc.substring(2);                
            }
            return res.trim().replace('*', '%');
            
        }
        return "";
    }
}
