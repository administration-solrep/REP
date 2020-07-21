package fr.dila.reponses.webtest.model;

import java.util.ArrayList;
import java.util.List;

public class FeuilleRoute {
	
	/** La liste des étapes série **/
	private List<Etape> etapes;
	
	private String intitule;
	
	private String ministere;
	
	private String directionPilote;
	
	private Boolean isModeleParDefaut = Boolean.FALSE;
	

	/** Constructeur par défaut **/
	public FeuilleRoute(){
		super();
		etapes = new ArrayList<Etape>();
	}
	
	public void setDirectionPilote(String directionPilote) {
		this.directionPilote = directionPilote;
	}

	public List<Etape> getEtapes() {
		return etapes;
	}

	public void setEtapes(List<Etape> etapes) {
		this.etapes = etapes;
	}
	
	public void addEtape(Etape etape){
		this.etapes.add(etape);
		
	}

	public String getIntitule() {
		return intitule;
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public String getMinistere() {
		return ministere;
	}

	public void setMinistere(String ministere) {
		this.ministere = ministere;
	}

	public String getDirectionPilote() {
		return directionPilote;
	}

	public Boolean getIsModeleParDefaut() {
		return isModeleParDefaut;
	}

	public void setIsModeleParDefaut(Boolean isModeleParDefaut) {
		this.isModeleParDefaut = isModeleParDefaut;
	}
	


}
