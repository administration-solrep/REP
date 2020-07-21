package fr.dila.reponses.webtest.model;

public interface Etape {
	
	public final static String TYPE_SERIE = "SERIE";
	
	public final static String TYPE_BRANCHE = "BRANCHE";
	
	/**
	 * Retourne le type d'étape 
	 * @return
	 */
	public String getKind();
	
}
