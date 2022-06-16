package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.bean.TreeElementDTO;

public class FavorisPlanClassementDTO {
    private TreeElementDTO assemblee;
    private TreeElementDTO senat;

    public FavorisPlanClassementDTO() {
        super();
    }

    public TreeElementDTO getAssemblee() {
        return assemblee;
    }

    public void setAssemblee(TreeElementDTO assemblee) {
        this.assemblee = assemblee;
    }

    public TreeElementDTO getSenat() {
        return senat;
    }

    public void setSenat(TreeElementDTO senat) {
        this.senat = senat;
    }
}
