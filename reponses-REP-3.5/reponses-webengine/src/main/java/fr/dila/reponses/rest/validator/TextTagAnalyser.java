package fr.dila.reponses.rest.validator;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTagAnalyser {

    private Pattern pattern = Pattern.compile("<[/]?[ ]*([^ ^>^/]*)");
    private Matcher matcher;
    
    private String noConformTag;
    
    public void startAnalyse(String str){
        matcher = pattern.matcher(str);
    }
    
    public String nextTag(){
        if(matcher.find()){
            return matcher.group(1);
        } else {
            return null;
        }
    }
    
    public Set<String> extractAllTag(String str){
        Set<String> result = new HashSet<String>();
        
        startAnalyse(str);
        
        String tag = nextTag();
        while(tag != null){
            result.add(tag);
            tag = nextTag();
        }
        return result;
    }
    
    /**
     * verifie si les tags présent dans une chaine sont definis dans l'ensemble donnée en parametre
     * sinon retourne faux et positionne la valeur noConformTag
     * @param str
     * @param okTag
     * @return
     */
    public boolean checkTags(String str, Set<String> okTag){
        noConformTag = null;
        
        startAnalyse(str);
        
        String tag = nextTag();
        while(tag != null){
            if(!okTag.contains(tag)){
                noConformTag = tag;
                return false;
            }
            tag = nextTag();
        }
        
        return true;
    }

    /**
     * valeur de tag ayant impliqué un return false dans checkTags
     * @return
     * @see checkTags
     */
    public String getNoConformTag() {
        return noConformTag;
    }
    
    
}
