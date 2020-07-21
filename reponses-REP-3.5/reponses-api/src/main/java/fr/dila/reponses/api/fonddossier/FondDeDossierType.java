package fr.dila.reponses.api.fonddossier;

import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;

/**
 * Type contenu dans le champs ou des noeud du fond de dossier
 * @author antoine Rolin
 *
 */
public enum FondDeDossierType {
	FOND_DE_DOSSIER_REPERTOIRE (ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE), 
	FOND_DE_DOSSIER_FICHIER (ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);
	
	private String value;
	
	FondDeDossierType(String type)
	{
		this.setValue(type);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static FondDeDossierType getEnum(String enumValue)
	{
		FondDeDossierType type = null;

		if(FOND_DE_DOSSIER_REPERTOIRE.value.equals(enumValue)){
			type = FOND_DE_DOSSIER_REPERTOIRE;
		} else if(FOND_DE_DOSSIER_FICHIER.value.equals(enumValue)){
			type = FOND_DE_DOSSIER_FICHIER;
		}
		
		return type;
	}
}
