package fr.dila.reponses.ui.th.bean.recherche;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class MotsClesForm {
    @FormParam("rechercheSur")
    private String rechercheSur;

    @FormParam("indexSenat")
    private ArrayList<String> indexSenat;

    @FormParam("indexAn")
    private ArrayList<String> indexAn;

    @FormParam("indexMinistere")
    private ArrayList<String> indexMinistere;

    public MotsClesForm() {}

    public String getRechercheSur() {
        return rechercheSur;
    }

    public void setRechercheSur(String rechercheSur) {
        this.rechercheSur = rechercheSur;
    }

    public ArrayList<String> getIndexSenat() {
        return indexSenat;
    }

    public void setIndexSenat(ArrayList<String> indexSenat) {
        this.indexSenat = indexSenat;
    }

    public ArrayList<String> getIndexAn() {
        return indexAn;
    }

    public void setIndexAn(ArrayList<String> indexAn) {
        this.indexAn = indexAn;
    }

    public ArrayList<String> getIndexMinistere() {
        return indexMinistere;
    }

    public void setIndexMinistere(ArrayList<String> indexMinistere) {
        this.indexMinistere = indexMinistere;
    }
}
