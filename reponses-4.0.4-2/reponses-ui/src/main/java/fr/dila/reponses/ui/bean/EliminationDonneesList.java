package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class EliminationDonneesList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<EliminationDonneesDTO> liste = new ArrayList<>();

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public EliminationDonneesList() {
        this.nbTotal = 0;
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonnees.liste.header.label.titre", false, true, false, true)
            );
        this.listeColonnes.add(
                new ColonneInfo("eliminationDonnees.liste.header.label.dateCreation", false, true, false, true)
            );
        this.listeColonnes.add(new ColonneInfo("header.label.actions", false, true, false, false));
    }

    public List<EliminationDonneesDTO> getListe() {
        return liste;
    }

    public void setListe(List<EliminationDonneesDTO> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }
}
