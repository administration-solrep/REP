package fr.dila.reponses.ui.jaxrs.webobject.page;

import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.SSAdmin;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliAdmin")
public class ReponsesAdmin extends SSAdmin {

    public ReponsesAdmin() {
        super();
    }

    @Path("eliminationDonnees")
    public Object getDonnees() {
        return newObject("EliminationDonnees", context);
    }

    @Path("timbres")
    public Object getTimbres() {
        return newObject("MiseAJourTimbres", context);
    }

    @Path("/delegation")
    public Object getDelegation() {
        return newObject("Delegation", context);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesAdminTemplate();
    }
}
