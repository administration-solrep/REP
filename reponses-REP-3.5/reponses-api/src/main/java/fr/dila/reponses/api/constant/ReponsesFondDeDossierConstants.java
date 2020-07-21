package fr.dila.reponses.api.constant;

import fr.dila.ss.api.constant.SSFondDeDossierConstants;

public class ReponsesFondDeDossierConstants extends SSFondDeDossierConstants {
    /**
     * Default constructor
     */
    protected ReponsesFondDeDossierConstants() {
        super();
    }
    
    /**
     * Nom des propriétés associées au fond de dossier.
     */
    public static final String FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE = "FondDeDossierRepertoire";

    public static final String FOND_DE_DOSSIER_ID_REPERTOIRE_PARLEMENT = "repertoire_parlement";

    public static final String FOND_DE_DOSSIER_ID_REPERTOIRE_MINISTERE = "repertoire_ministere";

    public static final String FOND_DE_DOSSIER_ID_REPERTOIRE_SGG = "repertoire_sgg";

    /**
     * Nom des propriétés associées aux fichiers du fond de dossier.
     */
    public static final String FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE = "niveauVisibilite";

    public static final String FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE = "ministereAjoute";

    public static final String FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION = "numeroVersion";

    public static final String FOND_DE_DOSSIER_ELEMENT_USER_NAME = "userName";

    /**
     * Nom et label associé aux répertoires par défaut du fond de dossier.
     */
    public static final String FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME = "Pièces destinées au Parlement";

    public static final String FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME = "Pièces strictement réservées ministère";

    public static final String FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME = "Pièces ministère attributaire et SGG";

    public static final String FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL = "A diffuser vers le parlement";

    public static final String FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL = "Uniquement Ministère";

    public static final String FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL = "Ministère et SGG";   
    
}
