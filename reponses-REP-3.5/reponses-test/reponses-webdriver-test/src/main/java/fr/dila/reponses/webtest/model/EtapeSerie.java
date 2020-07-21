package fr.dila.reponses.webtest.model;

import org.apache.commons.lang.StringUtils;

/**
 * Classe représentant une étape en série
 * 
 */
public class EtapeSerie implements Etape{

	private String etapeType;
	
	private String echeance;
	
	private String destinataire;
	
	private boolean validationAutomatique;
	
	private boolean obligatoireMinistere;
	
	private boolean obligatoireSGG;
	
	public EtapeSerie(String etapeType, String destinataire,
			String echeance, boolean toggleValidationAutomatique, boolean toggleObligatoireMinistere, boolean toggleObligatoireSGG) {
		super();
		this.etapeType = etapeType;
		this.destinataire = destinataire;
		this.echeance = echeance;
		this.validationAutomatique = toggleValidationAutomatique;
		this.obligatoireMinistere = toggleObligatoireMinistere;
		this.obligatoireSGG = toggleObligatoireSGG;
	}
	
	/**
	 * Une constructeur plus pratique que celui par défaut
	 * @param etapeType
	 * @param destinataire
	 */
	public EtapeSerie(String etapeType, String destinataire) {
		this(etapeType, destinataire, StringUtils.EMPTY, false, false, false);
	}

	public String getEcheance() {
		return echeance;
	}

	public void setEcheance(String echeance) {
		this.echeance = echeance;
	}

	public String getEtapeType() {
		return etapeType;
	}

	public void setEtapeType(String etapeType) {
		this.etapeType = etapeType;
	}

	public String getDestinataire() {
		return destinataire;
	}

	public void setDestinataire(String destinataire) {
		this.destinataire = destinataire;
	}

	public boolean isValidationAutomatique() {
		return validationAutomatique;
	}

	public void setValidationAutomatique(boolean validationAutomatique) {
		this.validationAutomatique = validationAutomatique;
	}

	public boolean isObligatoireMinistere() {
		return obligatoireMinistere;
	}

	public void setObligatoireMinistere(boolean obligatoireMinistere) {
		this.obligatoireMinistere = obligatoireMinistere;
	}

	public boolean isObligatoireSGG() {
		return obligatoireSGG;
	}

	public void setObligatoireSGG(boolean obligatoireSGG) {
		this.obligatoireSGG = obligatoireSGG;
	}
	
	@Override
	public String getKind() {
		return Etape.TYPE_SERIE;
	}


}
