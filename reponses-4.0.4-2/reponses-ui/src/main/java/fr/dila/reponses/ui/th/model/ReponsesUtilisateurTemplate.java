package fr.dila.reponses.ui.th.model;

import fr.dila.st.ui.services.UtilisateurMenuService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "MAIN_UTILISATEUR", category = "MAIN_MENU") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = UtilisateurMenuService.class,
                    templateFile = "fragments/leftblocks/leftMenuUtilisateur",
                    template = "leftMenuUtilisateur",
                    order = 1
                )
            }
        )
    }
)
public class ReponsesUtilisateurTemplate extends ReponsesLayoutThTemplate {

    public ReponsesUtilisateurTemplate() {
        this(null, null);
    }

    public ReponsesUtilisateurTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
