package fr.dila.reponses.web.recherche;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


/**
 * Bean de gestion des panneaux de la recherche, en particulier
 * la conservation de l'état de chaqu'un des panneaux
 * @author jgomez
 *
 */
//TODO : Factoriser les fonctionnalités  dans ST.
@Name("recherchePanneauxActions")
@Scope(ScopeType.CONVERSATION)
public class RecherchePanneauxActionsBean implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private Map<String,Boolean> panneauxFoldedStateMap;
    
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(RecherchePanneauxActionsBean.class);
    
    @PostConstruct
    public void initialize(){
        clearPanneaux();
    }

    /**
     * Place tous les panneaux déroulable en mode fermé.
     */
    public void clearPanneaux() {
        panneauxFoldedStateMap = new HashMap<String, Boolean>();
        panneauxFoldedStateMap.put("RequeteTexteIntegral",false);
        panneauxFoldedStateMap.put("RequeteIndex",false);
        panneauxFoldedStateMap.put("RequeteFeuilleRoute",false);
    }
    
    /**
     * Passe un panneau de ouvert à fermé ou vice-versa et renvoie son état.
     * @param panneauName
     * @return 
     * @throws Exception
     */
    public boolean toggle(String panneauName) throws Exception{
        boolean state = panneauxFoldedStateMap.get(panneauName);
        panneauxFoldedStateMap.put(panneauName,!panneauxFoldedStateMap.get(panneauName));
        return state;
    }
    
    public Map<String,Boolean> getPanneauxMap(){
        return panneauxFoldedStateMap;
    }
    
    

}
