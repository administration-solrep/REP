package fr.dila.reponses.ui.jaxrs.webobject.page.admin.organigramme;

import fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme.STOrganigrammePoste;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammePoste")
public class ReponsesOrganigrammePoste extends STOrganigrammePoste implements ReponseSharedBetweenAdminAndUser {

    public ReponsesOrganigrammePoste() {
        super();
    }

    @Override
    public ThTemplate getPosteCreation(String idParent, String ministereId) {
        ThTemplate template = super.getPosteCreation(idParent, ministereId);
        template.getData().put("hasCharge", true);
        template.getData().put("hasSuperviseur", true);
        template.getData().put("hasPosteBDC", true);
        return template;
    }

    @Override
    public ThTemplate getPosteModification(String id, String ministereId) {
        ThTemplate template = super.getPosteModification(id, ministereId);
        template.getData().put("hasCharge", true);
        template.getData().put("hasSuperviseur", true);
        template.getData().put("hasPosteBDC", true);
        return template;
    }
}
