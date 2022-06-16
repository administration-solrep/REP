package fr.dila.reponses.ui.th.model;

import fr.dila.reponses.ui.services.FavorisTravailComponentService;
import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.reponses.ui.services.SearchMenuComponentService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_RECHERCHE", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = SearchMenuComponentService.class,
                    templateFile = "fragments/leftblocks/searchMenu",
                    template = "searchMenu",
                    order = 1
                ),
                @Fragment(
                    service = ReponsesDerniersElementsComponentService.class,
                    templateFile = "fragments/derniersElementsMenu/derniersElements",
                    template = "derniersElements",
                    order = 2
                ),
                @Fragment(
                    service = FavorisTravailComponentService.class,
                    templateFile = "fragments/leftblocks/favorisTravail",
                    template = "favorisTravail",
                    order = 3
                )
            }
        )
    }
)
public class ReponsesRechercheTemplate extends ReponsesLayoutThTemplate {

    public ReponsesRechercheTemplate() {
        this(null, null);
    }

    public ReponsesRechercheTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
