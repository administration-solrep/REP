package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSProfils;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Profils")
public class ReponsesProfils extends SSProfils {

    public ReponsesProfils() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        if (context == null || context.getWebcontext() == null) {
            return super.getMyTemplate();
        }

        if (context.getWebcontext().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }
}
