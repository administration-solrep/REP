package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.ArrayList;
import java.util.List;

public class InitValueRechercheDTO {
    private List<SelectValueDTO> legislatures = new ArrayList<>();

    private List<SelectValueDTO> typesQuestion = new ArrayList<>();

    private List<SelectValueDTO> ministeresAttributaires = new ArrayList<>();

    private List<SelectValueDTO> ministeresInterroges = new ArrayList<>();

    private List<SelectValueDTO> groupesPolitiques = new ArrayList<>();

    private List<SelectValueDTO> typesEtape = new ArrayList<>();

    private List<SelectValueDTO> listStatus = new ArrayList<>();

    private List<SelectValueDTO> listIndexSenat = new ArrayList<>();

    private List<SelectValueDTO> listIndexAn = new ArrayList<>();

    private List<SelectValueDTO> listIndexMinistere = new ArrayList<>();

    public InitValueRechercheDTO() {}

    public List<SelectValueDTO> getLegislatures() {
        return legislatures;
    }

    public void setLegislatures(List<SelectValueDTO> legislatures) {
        this.legislatures = legislatures;
    }

    public List<SelectValueDTO> getTypesQuestion() {
        return typesQuestion;
    }

    public void setTypesQuestion(List<SelectValueDTO> typesQuestion) {
        this.typesQuestion = typesQuestion;
    }

    public List<SelectValueDTO> getMinisteresAttributaires() {
        return ministeresAttributaires;
    }

    public void setMinisteresAttributaires(List<SelectValueDTO> ministeresAttributaires) {
        this.ministeresAttributaires = ministeresAttributaires;
    }

    public List<SelectValueDTO> getMinisteresInterroges() {
        return ministeresInterroges;
    }

    public void setMinisteresInterroges(List<SelectValueDTO> ministeresInterroges) {
        this.ministeresInterroges = ministeresInterroges;
    }

    public List<SelectValueDTO> getGroupesPolitiques() {
        return groupesPolitiques;
    }

    public void setGroupesPolitiques(List<SelectValueDTO> groupesPolitiques) {
        this.groupesPolitiques = groupesPolitiques;
    }

    public List<SelectValueDTO> getTypesEtape() {
        return typesEtape;
    }

    public void setTypesEtape(List<SelectValueDTO> typesEtape) {
        this.typesEtape = typesEtape;
    }

    public List<SelectValueDTO> getListStatus() {
        return listStatus;
    }

    public void setListStatus(List<SelectValueDTO> listStatus) {
        this.listStatus = listStatus;
    }

    public List<SelectValueDTO> getListIndexSenat() {
        return listIndexSenat;
    }

    public void setListIndexSenat(List<SelectValueDTO> listIndexSenat) {
        this.listIndexSenat = listIndexSenat;
    }

    public List<SelectValueDTO> getListIndexAn() {
        return listIndexAn;
    }

    public void setListIndexAn(List<SelectValueDTO> listIndexAn) {
        this.listIndexAn = listIndexAn;
    }

    public List<SelectValueDTO> getListIndexMinistere() {
        return listIndexMinistere;
    }

    public void setListIndexMinistere(List<SelectValueDTO> listIndexMinistere) {
        this.listIndexMinistere = listIndexMinistere;
    }
}
