package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DossierConnexeList {
    private List<ColonneInfo> lstColonnes = new ArrayList<>();
    private List<DossierConnexeDTO> dossiers = new ArrayList<>();
    private String titre;
    private Boolean isSameMinistere = true;

    public DossierConnexeList() {
        super();
        buildColonnes();
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public void setLstColonnes(List<ColonneInfo> lstColonnes) {
        this.lstColonnes = lstColonnes;
    }

    public List<DossierConnexeDTO> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<DossierConnexeDTO> dossiers) {
        this.dossiers = dossiers;
    }

    private void buildColonnes() {
        lstColonnes.clear();

        lstColonnes.add(
            new ColonneInfo("dossier.connexe.ministere.column.header.numero.dossier", false, true, false, true)
        );
        lstColonnes.add(new ColonneInfo("dossier.connexe.ministere.column.header.auteur", false, true, false, true));
        lstColonnes.add(new ColonneInfo("dossier.connexe.ministere.column.header.etat", false, true, false, true));
        lstColonnes.add(
            new ColonneInfo("dossier.connexe.ministere.column.header.indexation.principale", false, true, false, true)
        );
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Boolean getIsSameMinistere() {
        return isSameMinistere;
    }

    public void setIsSameMinistere(Boolean isSameMinistere) {
        this.isSameMinistere = isSameMinistere;
    }
}
