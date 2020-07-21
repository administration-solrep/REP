package fr.dila.reponses.web.corbeille;

/**
 * Noeud pour une etape
 * 
 * @author spesnel
 *
 */
public class CorbeilleTreeEtapeNode{

    /**
     * clé identifiant l'etape
     */
    private String feuilleRouteId;
    
    private String ministereId;
    
    /**
     * nombre à afficher de dossier dans l'etape
     */
    private long count;
    
    public CorbeilleTreeEtapeNode(String feuilleRouteId, String ministereId, long count){
        this.count = count;
        this.feuilleRouteId = feuilleRouteId;
        this.ministereId = ministereId;
    }

    public String getFeuilleRouteId(){
        return feuilleRouteId;
    }
    
    public String getMinistereId(){
        return ministereId;
    }
    
    public long getCount(){
        return count;
    }
    
    public void addCount(long valueToAdd){
        count = count + valueToAdd;
    }

	void setFeuilleRouteId(String feuilleRouteId) {
		this.feuilleRouteId = feuilleRouteId;
	}
}
