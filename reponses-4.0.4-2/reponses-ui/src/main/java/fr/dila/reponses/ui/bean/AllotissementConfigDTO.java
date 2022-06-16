package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class AllotissementConfigDTO {
    private List<AllotissementDTO> listQuestionsErrorSearch = new ArrayList<>();
    private List<AllotissementDTO> listQuestionsAvecLotSearch = new ArrayList<>();
    private List<AllotissementDTO> listQuestionsSearch = new ArrayList<>();
    private List<ColonneInfo> lstColonnes;

    public AllotissementConfigDTO() {
        lstColonnes = new ArrayList<>();

        lstColonnes.add(new ColonneInfo("allotissement.header.numDossier", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.auteur", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.ministere.attributaire", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.etat", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.motCle", false, true, false, true));
    }

    public List<AllotissementDTO> getListQuestionsErrorSearch() {
        return listQuestionsErrorSearch;
    }

    public void setListQuestionsErrorSearch(List<AllotissementDTO> listQuestionsErrorSearch) {
        this.listQuestionsErrorSearch = listQuestionsErrorSearch;
    }

    public List<AllotissementDTO> getListQuestionsAvecLotSearch() {
        return listQuestionsAvecLotSearch;
    }

    public void setListQuestionsAvecLotSearch(List<AllotissementDTO> listQuestionsAvecLotSearch) {
        this.listQuestionsAvecLotSearch = listQuestionsAvecLotSearch;
    }

    public List<AllotissementDTO> getListQuestionsSearch() {
        return listQuestionsSearch;
    }

    public void setListQuestionsSearch(List<AllotissementDTO> listQuestionsSearch) {
        this.listQuestionsSearch = listQuestionsSearch;
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }
}
