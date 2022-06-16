package fr.dila.reponses.ui.th.model;

import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.reponses.ui.services.SuiviMenuComponentService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_SUIVI", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = SuiviMenuComponentService.class,
                    templateFile = "fragments/leftblocks/suiviMenu",
                    template = "suiviMenu",
                    order = 1
                ),
                @Fragment(
                    service = ReponsesDerniersElementsComponentService.class,
                    templateFile = "fragments/derniersElementsMenu/derniersElements",
                    template = "derniersElements",
                    order = 2
                )
            }
        )
    }
)
public class ReponsesSuiviTemplate extends ReponsesLayoutThTemplate {

    public ReponsesSuiviTemplate() {
        this(null, null);
    }

    public ReponsesSuiviTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
