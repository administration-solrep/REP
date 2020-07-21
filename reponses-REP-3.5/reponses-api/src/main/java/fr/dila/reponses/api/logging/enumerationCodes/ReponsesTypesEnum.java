package fr.dila.reponses.api.logging.enumerationCodes;

import fr.dila.st.api.logging.enumerationCodes.STCodes;

/**
 * Enumération des types de logs propres à REPONSES
 * 
 * Décompte sur 3 chiffres, le premier (4) indique qu'il s'agit d'un type de Réponses <br />
 * 
 * 400 : Défaut <br />
 * 401 : Echec de signature <br />
 * 402 : Extraction <br />
 * 403 : Echec d'extraction <br />
 * 404 : Echec de suppression d'une signature <br />
 * 405 : Suppression d'une signature <br />
 *
 */
public enum ReponsesTypesEnum implements STCodes {
    /**
     * 400 défaut
     */
    DEFAULT(400, "Types REPONSES défaut"),
    /**
     * 401 : Echec de signature
     */
    FAIL_SIGN(401, "Echec de signature"),
    /**
     * 402 : Extraction
     */
    EXTRACT(402, "Extraction"),
    /**
     * 403 : Echec d'extraction
     */
    FAIL_EXTRACT(403, "Echec d'extraction"), 
    /**
     * 404 : Echec de suppression d'une signature
     */
    FAIL_DEL_SIGN(404, "Echec de suppression d'une signature"), 
    /**
     *  405 : Suppression d'une signature
     */
    DELETE_SIGN(405, "Suppression d'une signature");

	/* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */
    
    private int codeNumber;
    private String codeText;
    
    ReponsesTypesEnum(int codeNumber, String codeText) {
        this.codeNumber = codeNumber;
        this.codeText = codeText;
    }
    
    @Override
    public int getCodeNumber() {
        return this.codeNumber;
    }

    @Override
    public String getCodeText() {
        return this.codeText;
    }

    @Override
    public String getCodeNumberStr() {
        return String.valueOf(codeNumber);
    }

}
