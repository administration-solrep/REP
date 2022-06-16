package fr.dila.reponses.ui.th.model;

import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(menu = { @ActionMenu(id = "MAIN_STATS", category = "MAIN_MENU") })
public class ReponsesStatistiquesTemplate extends ReponsesLayoutThTemplate {

    public ReponsesStatistiquesTemplate() {
        this(null, null);
    }

    public ReponsesStatistiquesTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
