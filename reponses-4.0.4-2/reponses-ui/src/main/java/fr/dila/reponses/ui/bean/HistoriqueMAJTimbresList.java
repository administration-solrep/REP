package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueMAJTimbresList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<HistoriqueMAJTimbresDTO> liste = new ArrayList<>();

    public HistoriqueMAJTimbresList() {
        this.listeColonnes.add(
                new ColonneInfo("historiqueMajTimbres.liste.header.label.message", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("historiqueMajTimbres.liste.header.label.etat", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("historiqueMajTimbres.liste.header.label.dateDebut", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("historiqueMajTimbres.liste.header.label.dateFin", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "historiqueMajTimbres.liste.header.label.questionEnCoursAMigrer",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "historiqueMajTimbres.liste.header.label.questionEnCoursMigrees",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "historiqueMajTimbres.liste.header.label.questionClosesAMigrer",
                    false,
                    true,
                    false,
                    true
                )
            );
        this.listeColonnes.add(
                new ColonneInfo(
                    "historiqueMajTimbres.liste.header.label.questionClosesMigrees",
                    false,
                    true,
                    false,
                    true
                )
            );
    }

    public List<HistoriqueMAJTimbresDTO> getListe() {
        return liste;
    }

    public void setListe(List<HistoriqueMAJTimbresDTO> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }
}
