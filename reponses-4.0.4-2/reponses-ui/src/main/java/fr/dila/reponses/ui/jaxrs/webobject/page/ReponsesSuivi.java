package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.reponses.ui.th.model.ReponsesSuiviTemplate;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.jaxrs.webobject.page.SSRequeteExperte;
import fr.dila.ss.ui.jaxrs.webobject.page.SSSuivi;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliSuivi")
public class ReponsesSuivi extends SSSuivi implements SSRequeteExperte {
    public static final String REPONSE_CHAMP_CONTRIB_NAME = "fr.dila.reponses.ui.recherche-champ-contrib";

    public ReponsesSuivi() {
        super();
    }

    @GET
    public ThTemplate getHome() {
        template.setName("pages/suivi");
        template.setContext(context);
        context.removeNavigationContextTitle();

        RechercheChampService champService = STServiceLocator.getRechercheChampService();
        Map<String, Object> map = new HashMap<>();

        RequeteExperteDTO requeteExperteDTO = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        if (requeteExperteDTO == null) {
            requeteExperteDTO = new RequeteExperteDTO();
            List<ChampDescriptor> champs = champService.getChamps(REPONSE_CHAMP_CONTRIB_NAME);
            requeteExperteDTO.setChamps(champs);
            UserSessionHelper.putUserSessionParameter(context, getDtoSessionKey(context), requeteExperteDTO);
        } else {
            if (UserSessionHelper.getUserSessionParameter(context, getResultsSessionKey(context), Map.class) != null) {
                map.putAll(UserSessionHelper.getUserSessionParameter(context, getResultsSessionKey(context)));
            }
        }

        map.put("id", context.getFromContextData(ID));
        map.put("requeteExperteDTO", requeteExperteDTO);
        map.put("isFirstChamp", requeteExperteDTO.getRequetes().isEmpty());
        template.setData(map);
        return template;
    }

    @Path("/requete")
    public Object getRequetesGenerales() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/suivi/requete-generale/listeRequeteGenerale");
        return newObject("ReponsesSuiviRequete", context, template);
    }

    @Path("/perso")
    public Object getRequetesPerso() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/suivi/requete-perso/requetePerso");
        return newObject("ReponsesSuiviPerso", context, template);
    }

    @GET
    @Path("/{id}")
    public ThTemplate getRequete(@PathParam("id") String id) {
        context.putInContextData(ID, id);
        return getHome();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesSuiviTemplate();
    }

    @Override
    public String getSuffixForSessionKeys(SpecificContext context) {
        return "_REP";
    }
}
