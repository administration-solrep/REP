package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DetailMigrationHistoriqueMAJTimbresList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<DetailMigrationHistoriqueMAJTimbresDTO> liste = new ArrayList<>();

    public DetailMigrationHistoriqueMAJTimbresList() {
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.message", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.etat", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.details", false, true, false, true)
            );
    }

    public List<DetailMigrationHistoriqueMAJTimbresDTO> getListe() {
        return liste;
    }

    public void setListe(List<DetailMigrationHistoriqueMAJTimbresDTO> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }
}
