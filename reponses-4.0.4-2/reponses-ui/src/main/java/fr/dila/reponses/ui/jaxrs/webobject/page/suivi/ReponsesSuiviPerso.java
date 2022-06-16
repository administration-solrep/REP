package fr.dila.reponses.ui.jaxrs.webobject.page.suivi;

import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesOrganigramme;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.jaxrs.webobject.page.SSRequeteExperte;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ReponsesSuiviPerso")
public class ReponsesSuiviPerso extends SolonWebObject implements SSRequeteExperte {
    private static final String NAVIGATION_TITLE = "Sauvegarde de la requête";

    public ReponsesSuiviPerso() {
        super();
    }

    @GET
    @Path("creation")
    public ThTemplate getRequetePersoCreation() {
        RequeteExperteDTO requeteExperteDto = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        RequetePersoForm form = ReponsesUIServiceLocator
            .getRequeteUIService()
            .getRequetePersoForm(
                Optional
                    .ofNullable(requeteExperteDto)
                    .map(RequeteExperteDTO::getRequetes)
                    .orElseGet(Collections::emptyList)
            );

        return prepareTemplate(form);
    }

    @GET
    @Path("renommer")
    public ThTemplate getRequetePersoCreation(@QueryParam("id") String id) {
        context.setCurrentDocument(id);
        RequetePersoForm form = ReponsesUIServiceLocator.getRequeteUIService().getRequetePerso(context);

        template = prepareTemplate(form);
        template.getData().put("id", id);

        return template;
    }

    private ThTemplate prepareTemplate(RequetePersoForm form) {
        Map<String, Object> map = new HashMap<>();

        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TITLE, "/suivi/perso/creation", ReponsesOrganigramme.ORGANIGRAMME_ORDER + 1)
        );

        map.put("form", form);

        template.setContext(context);
        template.setData(map);
        return template;
    }

    @Override
    public String getSuffixForSessionKeys(SpecificContext context) {
        return "_REP";
    }
}
