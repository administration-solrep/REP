package fr.dila.reponses.web.favoris;

import java.util.ArrayList;
import java.util.List;

import fr.dila.reponses.api.favoris.FavorisIndexation;

/**
 * 
 * @author asatre
 *
 */
public class FavorisIndexationTreeNode {

	public enum TypeIndexation{
		ROOT, AN, SE, AN_CHILD, SE_CHILD
	}
	
	private String label;
    
    private List<FavorisIndexationTreeNode> children;
    
    public Boolean opened;
    
    private Boolean loaded;
    
    private String favoris;
    
    private TypeIndexation typeIndexation;
    
    private FavorisIndexationTreeNode parent;
    
    public FavorisIndexationTreeNode(TypeIndexation typeIndexation, String label) {
    	if(typeIndexation != null){
	        this.label = label;
	        this.children = new ArrayList<FavorisIndexationTreeNode>();
	        opened = Boolean.TRUE;
	        loaded = Boolean.TRUE;
	        this.typeIndexation = typeIndexation;
    	}
    }
    
    public FavorisIndexationTreeNode(TypeIndexation typeIndexation, String niveau1, List<FavorisIndexation> listFavIndexation) {
    	this(typeIndexation, niveau1);
    	TypeIndexation typeIndexationChild = null;
    	switch (typeIndexation) {
		case SE:
			typeIndexationChild = TypeIndexation.SE_CHILD;
			break;
		case AN:
			typeIndexationChild = TypeIndexation.AN_CHILD;
			break;
		default:
			break;
		}
    	for (FavorisIndexation favorisIndexation : listFavIndexation) {
			children.add(new FavorisIndexationTreeNode(typeIndexationChild, favorisIndexation, this));
		}
	}
    
    public FavorisIndexationTreeNode(TypeIndexation typeIndexation, FavorisIndexation favorisIndexation, FavorisIndexationTreeNode parent) {
    	this(typeIndexation, favorisIndexation.getNiveau2());
    	this.favoris = favorisIndexation.getDocument().getId();
    	this.parent = parent;
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

	public void setChildren(List<FavorisIndexationTreeNode> children) {
		this.children = children;
	}

	public List<FavorisIndexationTreeNode> getChildren() {
		return children;
	}

	public void setFavoris(String favoris) {
		this.favoris = favoris;
	}

	public String getFavoris() {
		return favoris;
	}

	public void setTypeIndexation(TypeIndexation typeIndexation) {
		this.typeIndexation = typeIndexation;
	}

	public TypeIndexation getTypeIndexation() {
		return typeIndexation;
	}

	public void setParent(FavorisIndexationTreeNode parent) {
		this.parent = parent;
	}

	public FavorisIndexationTreeNode getParent() {
		return parent;
	}
    
}
