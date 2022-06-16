package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class AllotissementListDTO {
    private List<AllotissementDTO> listQuestionsAlloties = new ArrayList<>();
    private String nomDossierDirecteur;
    private List<ColonneInfo> lstColonnes;

    public AllotissementListDTO(String nomDossierDirecteur) {
        lstColonnes = new ArrayList<>();

        lstColonnes.add(new ColonneInfo("allotissement.header.numDossier", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.auteur", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.etat", false, true, false, true));
        lstColonnes.add(new ColonneInfo("allotissement.header.motCle", false, true, false, true));
        this.nomDossierDirecteur = nomDossierDirecteur;
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public List<AllotissementDTO> getListQuestionsAlloties() {
        return listQuestionsAlloties;
    }

    public void setListQuestionsAlloties(List<AllotissementDTO> listQuestionsAlloties) {
        this.listQuestionsAlloties = listQuestionsAlloties;
    }

    public String getNomDossierDirecteur() {
        return nomDossierDirecteur;
    }

    public void setNomDossierDirecteur(String nomDossierDirecteur) {
        this.nomDossierDirecteur = nomDossierDirecteur;
    }
}
