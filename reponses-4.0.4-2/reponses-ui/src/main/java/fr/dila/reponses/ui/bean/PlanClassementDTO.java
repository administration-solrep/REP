package fr.dila.reponses.ui.bean;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.ui.bean.TreeDTO;
import fr.dila.st.ui.bean.TreeElementDTO;

public class PlanClassementDTO extends TreeDTO<TreeElementDTO> {
    private String assemblee = DossierConstants.ORIGINE_QUESTION_AN;

    public String getAssemblee() {
        return assemblee;
    }

    public void setAssemblee(String assemblee) {
        this.assemblee = assemblee;
    }
}
