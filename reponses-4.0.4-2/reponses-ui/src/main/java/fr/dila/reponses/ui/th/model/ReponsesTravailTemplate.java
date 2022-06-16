package fr.dila.reponses.ui.th.model;

import fr.dila.reponses.ui.services.FavorisPlanClassementComponentService;
import fr.dila.reponses.ui.services.FavorisTravailComponentService;
import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.reponses.ui.services.ReponsesMailboxListComponentService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_TRAVAIL", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = ReponsesMailboxListComponentService.class,
                    templateFile = "fragments/leftblocks/mailboxList",
                    template = "mailboxList",
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
                ),
                @Fragment(
                    service = FavorisTravailComponentService.class,
                    templateFile = "fragments/leftblocks/favorisTravail",
                    template = "favorisTravail",
                    order = 4
                )
            }
        )
    }
)
public class ReponsesTravailTemplate extends ReponsesLayoutThTemplate {

    public ReponsesTravailTemplate() {
        this(null, null);
    }

    public ReponsesTravailTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
