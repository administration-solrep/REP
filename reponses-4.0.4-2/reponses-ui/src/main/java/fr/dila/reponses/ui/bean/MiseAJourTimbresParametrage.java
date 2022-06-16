package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.th.bean.EntiteForm;
import java.util.ArrayList;
import java.util.List;

public class MiseAJourTimbresParametrage {
    private List<EntiteForm> ministeres = new ArrayList<>();

    public MiseAJourTimbresParametrage() {
        super();
    }

    public List<EntiteForm> getMinisteres() {
        return ministeres;
    }

    public void setMinisteres(List<EntiteForm> ministeres) {
        this.ministeres = ministeres;
    }
}
