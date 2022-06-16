package fr.dila.reponses.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean
public class SearchForm {
    @FormParam("legislature")
    @QueryParam("legislature")
    private String legislature;

    @FormParam("questions")
    @QueryParam("questions")
    private ArrayList<String> questions;

    @FormParam("assemblee")
    @QueryParam("assemblee")
    private ArrayList<String> assemblee;

    @FormParam("isRep")
    @QueryParam("isRep")
    private String isRep;

    @FormParam("isAttente")
    @QueryParam("isAttente")
    private String isAttente;

    @FormParam("numeros")
    @QueryParam("numeros")
    private String numeros;

    public String getLegislature() {
        return legislature;
    }

    public void setLegislature(String legislature) {
        this.legislature = legislature;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public ArrayList<String> getAssemblee() {
        return assemblee;
    }

    public void setAssemblee(ArrayList<String> assemblee) {
        this.assemblee = assemblee;
    }

    public String getIsRep() {
        return isRep;
    }

    public void setIsRep(String isRep) {
        this.isRep = isRep;
    }

    public String getIsAttente() {
        return isAttente;
    }

    public void setIsAttente(String isAttente) {
        this.isAttente = isAttente;
    }

    public String getNumeros() {
        return numeros;
    }

    public void setNumeros(String numeros) {
        this.numeros = numeros;
    }
}
