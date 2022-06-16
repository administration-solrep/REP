package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class DossierConnexeMinistereList {
    private List<ColonneInfo> lstColonnes = new ArrayList<>();
    private List<DossierConnexeMinistereDTO> dossiers = new ArrayList<>();

    public DossierConnexeMinistereList() {
        super();
        buildColonnes();
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public void setLstColonnes(List<ColonneInfo> lstColonnes) {
        this.lstColonnes = lstColonnes;
    }

    public List<DossierConnexeMinistereDTO> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<DossierConnexeMinistereDTO> dossiers) {
        this.dossiers = dossiers;
    }

    private void buildColonnes() {
        lstColonnes.clear();

        lstColonnes.add(new ColonneInfo("dossier.connexe.column.header.ministere", false, true, false, true));
        lstColonnes.add(new ColonneInfo("dossier.connexe.column.header.nb.question.connexe", false, true, false, true));
    }
}
