package fr.dila.reponses.ui.th.model;

import fr.dila.reponses.ui.services.FavorisPlanClassementComponentService;
import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = {
        @ActionMenu(id = "MAIN_RECHERCHE", category = "MAIN_MENU"),
        @ActionMenu(id = "SEARCH_CLASSEMENT", category = "LEFT_SEARCH_MENU")
    },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = PlanClassementComponentService.class,
                    templateFile = "fragments/leftblocks/planClassement",
                    template = "planClassement",
                    order = 1
                ),
                @Fragment(
                    service = ReponsesDerniersElementsComponentService.class,
                    templateFile = "fragments/derniersElementsMenu/derniersElements",
                    template = "derniersElements",
                    order = 2
                ),
                @Fragment(
                    service = FavorisPlanClassementComponentService.class,
                    templateFile = "fragments/leftblocks/favorisPlanClassement",
                    template = "favorisPlanClassement",
                    order = 3
                )
            }
        )
    }
)
public class ReponsesPlanClassementTemplate extends ReponsesLayoutThTemplate {

    public ReponsesPlanClassementTemplate() {
        this(null, null);
    }

    public ReponsesPlanClassementTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
