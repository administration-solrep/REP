package fr.dila.reponses.api.recherche;

import java.io.Serializable;

public class IdLabel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7118708193977203078L;
    
    private String id;
    private String label;
    
    public IdLabel(String id, String label){
        this.id = id;
        this.label = label;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    
}
