package fr.dila.reponses.ui.jaxrs.webobject.page;

import fr.dila.st.ui.jaxrs.webobject.pages.HomeObject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliHome")
public class ReponsesHomeObject extends HomeObject {

    @GET
    @Override
    public Object doHome() {
        return redirect("/travail");
    }
}
