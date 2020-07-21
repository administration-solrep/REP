package fr.dila.reponses.api.mailbox;

import java.io.Serializable;
import java.util.Map;

/**
 * Manipulation du type complexe precomptage utilisé dans le schema
 * ReponsesMailbox.
 * 
 * @author spesnel
 *
 */
public interface PreComptage {

    String getMinistereId();
    
    String getRoutingTaskType();
    
    Long getCount();
    
    /**
     * decremente la valeur de comptage
     * 
     * @return la nouvelle valeur
     */
    Long decrCount();
    
    /**
     * incremente la valeur de comptage
     * 
     * @return la nouvelle valeur
     */
    Long incrCount();
       
    Map<String, Serializable> getPrecomptageMap();

    void setPrecomptageMap(Map<String, Serializable> precomptageMap);
    
}
