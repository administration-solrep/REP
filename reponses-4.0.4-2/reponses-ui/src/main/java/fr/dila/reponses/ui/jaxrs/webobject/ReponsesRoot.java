package fr.dila.reponses.ui.jaxrs.webobject;

import fr.dila.ss.ui.jaxrs.webobject.SsRoot;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Path("app-ui")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "reponses-ui")
public class ReponsesRoot extends SsRoot {

    public ReponsesRoot() {
        super();
    }

    @POST
    @Path("/@@login")
    @Override
    public Object execLogin() {
        return redirect("/travail");
    }

    @GET
    @Override
    public Object doHome() {
        return redirect("/travail");
    }
}
