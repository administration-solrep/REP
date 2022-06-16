package fr.dila.reponses.ui.jaxrs.webobject.page;

import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.st.ui.jaxrs.webobject.pages.user.STUtilisateur;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliUtilisateurs")
public class ReponsesUtilisateur extends STUtilisateur {

    public ReponsesUtilisateur() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesUtilisateurTemplate();
    }
}
