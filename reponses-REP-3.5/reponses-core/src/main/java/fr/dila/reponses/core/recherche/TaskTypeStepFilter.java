package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Un filtre sur les feuilles de route.
 * Accepte les étapes qui correspondent à certains critères. 
 * @author jgomez
 *
 */
public class TaskTypeStepFilter implements Filter{
    
    private static final long serialVersionUID = 1L;
    protected String tskTypeAccepted;
    
    public TaskTypeStepFilter(String acceptedType){
        this.tskTypeAccepted = acceptedType;
    }
    
    /**
     * Accepte ou non une étape de feuille de route.
     * @param step L'étape de feuille de route.
     * @throws ClientException 
     * @returns Vrai si l'étape est acceptée
     */
    public boolean accept(DocumentModel step){
        return tskTypeAccepted.equals(getTaskType(step));
    }

    private String getTaskType(DocumentModel step) {
        try {
            return (String) step.getProperty(STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_TYPE_PROPERTY);
        } catch (ClientException e) {
            return  "";
        }
    }

}
