package fr.dila.reponses.ui.bean;

import java.util.List;

public class FavorisTravailList {
    private List<FavorisTravailDTO> liste;

    public List<FavorisTravailDTO> getListe() {
        return liste;
    }

    public void setListe(List<FavorisTravailDTO> liste) {
        this.liste = liste;
    }

    public FavorisTravailList() {
        super();
    }
}
