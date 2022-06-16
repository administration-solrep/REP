package fr.dila.reponses.ui.th.model;

import fr.dila.reponses.ui.services.FavorisTravailComponentService;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.dila.st.ui.th.model.SpecificContext;

@IHM(
    menu = { @ActionMenu(id = "FAVORIS_TRAVAIL", category = "FAVORIS_TRAVAIL") },
    value = {
        @FragmentContainer(
            name = "left",
            value = {
                @Fragment(
                    service = FavorisTravailComponentService.class,
                    templateFile = "fragments/leftblocks/favorisTravail",
                    template = "favorisTravail",
                    order = 1
                )
            }
        )
    }
)
public class ReponsesFavorisTravailTemplate extends ReponsesLayoutThTemplate {

    public ReponsesFavorisTravailTemplate() {
        this(null, null);
    }

    public ReponsesFavorisTravailTemplate(String name, SpecificContext context) {
        super(name, context);
    }
}
