package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DetailHistoriqueMAJTimbresList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<DetailHistoriqueMAJTimbresDTO> liste = new ArrayList<>();

    public DetailHistoriqueMAJTimbresList() {
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.message", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.etat", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.dateDebut", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.dateFin", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "detailHistoriqueMajTimbres.liste.header.label.questionAMigrer",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "detailHistoriqueMajTimbres.liste.header.label.questionMigrees",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo("detailHistoriqueMajTimbres.liste.header.label.details", false, true, false, true)
            );
    }

    public List<DetailHistoriqueMAJTimbresDTO> getListe() {
        return liste;
    }

    public void setListe(List<DetailHistoriqueMAJTimbresDTO> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }
}
