package fr.dila.reponses.webtest.model;

import java.util.ArrayList;
import java.util.List;

public class EtapeBranche implements Etape{

	private List<Etape> etapes;
	
	public EtapeBranche(Etape etape1, Etape etape2){
		etapes = new ArrayList<Etape>();
		etapes.add(etape1);
		etapes.add(etape2);
	}

	public List<Etape> getEtapes() {
		return etapes;
	}

	@Override
	public String getKind() {
		return Etape.TYPE_BRANCHE;
	}

}
