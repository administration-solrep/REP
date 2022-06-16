package fr.dila.reponses.api.fonddossier;

import fr.dila.ss.api.fondDeDossier.SSFondDeDossierFile;

public interface FondDeDossierFile extends SSFondDeDossierFile {
    void setNumeroVersion(String numeroVersion);

    String getNumeroVersion();

    void setNiveauVisibilite(String niveauVisibilite);

    String getNiveauVisibilite();

    /**
     * renseigne l'ID du ministere ajoute
     * @param ministereAjoute
     */
    void setMinistereAjoute(String ministereAjoute);

    /**
     * récupère la métadonnée ministereAjoute
     * @return
     */
    String getMinistereAjoute();
}
