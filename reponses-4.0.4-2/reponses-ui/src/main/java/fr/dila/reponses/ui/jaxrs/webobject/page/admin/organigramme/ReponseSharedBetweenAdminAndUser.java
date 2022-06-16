package fr.dila.reponses.ui.jaxrs.webobject.page.admin.organigramme;

import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme.SharedBetweenAdminAndUser;
import fr.dila.st.ui.th.model.ThTemplate;

public interface ReponseSharedBetweenAdminAndUser extends SharedBetweenAdminAndUser {
    @Override
    default ThTemplate getMyAdminTemplate() {
        return new ReponsesAdminTemplate();
    }

    @Override
    default ThTemplate getMyUserTemplate() {
        return new ReponsesUtilisateurTemplate();
    }
}
