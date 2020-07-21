package fr.dila.reponses.web.corbeille;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Contient les informations sur les noeuds ministere dans l'arbre des corbeilles
 * 
 * @author spesnel
 */
public class CorbeilleTreeMinistereNode {
    /**
     * le label du noeud
     */
    private String label;

    /**
     * l'id du ministere : utilisé pour récuperer les infos sur les étapes
     */
    private String ministereId;

    /**
     * les etapes pour le ministere
     * 
     * @return
     */
    private Map<String, CorbeilleTreeEtapeNode> etapes;

    public Boolean opened;
    
    private Boolean isBold;

    private Boolean loaded;

    /**
     * nombre à afficher de dossier dans l'etape
     */
    private Long count;

    private CorbeilleTreeMinistereNode(String ministereId, String label) {
        this.label = label;
        this.ministereId = ministereId;
        this.etapes = new HashMap<String, CorbeilleTreeEtapeNode>();
        opened = Boolean.FALSE;
        loaded = Boolean.FALSE;
        isBold = Boolean.TRUE;
    }

    public CorbeilleTreeMinistereNode(String id, String label, Long preComptage) {
        this(id, label);
        this.count = preComptage;
    }

    public void addOrMergedEtape(String etapeKey, long etapeCount) {
        CorbeilleTreeEtapeNode node = retrieveNodeForEtape(etapeKey);
        if (node == null) {
            addEtape(etapeKey, etapeCount);
        } else {
            node.addCount(etapeCount);
        }
    }

    private void addEtape(String etapeKey, long etapeCount) {
        addEtape(new CorbeilleTreeEtapeNode(etapeKey, ministereId, etapeCount));
    }

    private CorbeilleTreeEtapeNode retrieveNodeForEtape(String etapeKey) {
        return etapes.get(etapeKey);
    }

    private void addEtape(CorbeilleTreeEtapeNode etape) {
        etapes.put(etape.getFeuilleRouteId(), etape);
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

    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label =label;
    }

    public Collection<CorbeilleTreeEtapeNode> getEtapes() {
        return etapes.values();
    }

    public CorbeilleTreeEtapeNode getEtape(String key) {
        return etapes.get(key);
    }

    /**
     * retourne vrai si le noeud contient au moins une etape
     */
    public boolean hasEtape() {
        return etapes != null && !etapes.isEmpty();
    }

    public String getMinistereId() {
        return ministereId;
    }

    public Long getCount() {
        return count;
    }

    /**
     * Retourne le premier élement s'il existe, null sinon
     * 
     * @return
     */
    public CorbeilleTreeEtapeNode getFirst() {
        Iterator<CorbeilleTreeEtapeNode> iter = etapes.values().iterator();
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return null;
        }
    }

    /**
     * Retourne le nombre d'étapes
     * 
     * @return
     */
    public int getNbEtapes() {
        return etapes.size();
    }

	public Boolean getIsBold() {
		return isBold;
	}

	public void setIsBold(Boolean isBold) {
		this.isBold = isBold;
	}

}
