package fr.dila.reponses.web.corbeille;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlanClassementTreeNiveau1Node {
    /**
     * le label du noeud
     */
    private String label;
    
    private String count;
    
    /**
     * l'indexation de ce noeud
     */
    private String indexation;
        
    /**
     * les noeuds de niveau 2
     * @return
     */
    private List<PlanClassementTreeNiveau2Node> niveaux2;
    
    /**
     * Les noeuds des lettres
     */
    private Collection<PlanClassementTreeNiveau1Node> lettres;
    
    public Boolean opened;
    
    private Boolean loaded;
    
    public PlanClassementTreeNiveau1Node(String label, String indexation, String count) {
        this.label = label;
        this.count = count;
        this.indexation = indexation;
        this.niveaux2 = new ArrayList<PlanClassementTreeNiveau2Node>();
        //Ajout par defaut d'un item pour permettre l'affichage du "+"
        if(count != null && !count.isEmpty() && Integer.valueOf(count)>0){
        	this.niveaux2.add(new PlanClassementTreeNiveau2Node("fakeItem", null, null, null));
        }
        opened = Boolean.FALSE;
        loaded = Boolean.FALSE;
    }
    
    public void addNiveau2(String n2Indexation, String n2Count) {
        addNiveau2(new PlanClassementTreeNiveau2Node(n2Indexation, n2Indexation, indexation, n2Count));
    }
    
    public void addNiveau2(PlanClassementTreeNiveau2Node niveau2) {
        niveaux2.add(niveau2);
    }
    
    public Boolean isOpened() {
        return opened;
    }
    
    public void setOpened(Boolean isOpened) {
        opened = isOpened;
    }
    
    public Boolean isLoaded() {
        return loaded;
    }
    
    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }
    
    // READ ACCESSORS
    
    public String getLabel() {
        return label;
    }
    
    public List<PlanClassementTreeNiveau2Node> getNiveaux2() {
        if(!"Rubrique".equals(indexation)) {
            return niveaux2;
        }
        return null;
    }
    
    public Collection<PlanClassementTreeNiveau1Node> getLettres() {
        if("Rubrique".equals(indexation)) {
            if(lettres == null) {
                Map<String, PlanClassementTreeNiveau1Node> map = new TreeMap<String, PlanClassementTreeNiveau1Node>();
                for(PlanClassementTreeNiveau2Node niveau2 : niveaux2) {
                    String lettre = niveau2.getLabel().substring(0, 1);
                    lettre = Normalizer.normalize(lettre, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                    if(map.containsKey(lettre)) {
                        PlanClassementTreeNiveau1Node node1 = map.get(lettre);
                        node1.addNiveau2(niveau2);
                    } else {
                        PlanClassementTreeNiveau1Node node1 = new PlanClassementTreeNiveau1Node(lettre, null, null);
                        node1.setLoaded(true);
                        node1.addNiveau2(niveau2);
                        map.put(lettre, node1);
                    }
                }
                lettres = map.values();
            }
            return lettres;
        }
        return null;
    }
    
    public String getIndexation() {
        return indexation;
    }
    
    public String getCount() {
        return count;
    }
    
    public void clearNiveaux2() {
    	this.niveaux2 = new ArrayList<PlanClassementTreeNiveau2Node>();
    	lettres = null;
    }
}
