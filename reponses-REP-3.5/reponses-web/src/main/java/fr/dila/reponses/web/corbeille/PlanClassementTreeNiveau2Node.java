package fr.dila.reponses.web.corbeille;

public class PlanClassementTreeNiveau2Node {
    private String label;
    
    private String indexation;
    
    private String niveau1Indexation;
    
    /**
     * nombre à afficher de dossier dans l'etape
     */
    private String count;
    
    public PlanClassementTreeNiveau2Node(String label, String indexation, String niveau1Indexation, String count){
        this.count = count;
        this.indexation = indexation;
        this.label = label;
        this.niveau1Indexation = niveau1Indexation;
    }

    public String getLabel() {
        return label;
    }
    
    public String getIndexation() {
        return indexation;
    }
    
    public String getNiveau1Indexation() {
        return niveau1Indexation;
    }
    
    public String getCount() {
        return count;
    }
}
