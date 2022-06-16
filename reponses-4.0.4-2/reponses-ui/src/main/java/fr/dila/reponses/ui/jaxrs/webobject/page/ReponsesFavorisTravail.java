package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.FAVORIS_TRAVAIL_FORM;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.FavorisTravailForm;
import fr.dila.reponses.ui.th.model.ReponsesFavorisTravailTemplate;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.constants.STURLConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliFavoris")
public class ReponsesFavorisTravail extends SolonWebObject {
    private static final String IS_CREATION = "isCreation";
    private static final String LIST_DOSSIERS_ID = "idDossiers";

    public ReponsesFavorisTravail() {
        super();
    }

    @GET
    @Path("ajout")
    public ThTemplate getCreation(@QueryParam("idDossiers") List<String> idDossiers)
        throws InstantiationException, IllegalAccessException {
        ThTemplate template = getMyTemplate(context);
        template.setName("pages/favorisTravail");
        context.setNavigationContextTitle(
            new Breadcrumb("label.favoris.travail.title", "/favoris/ajout", Breadcrumb.TITLE_ORDER + 1)
        );
        FavorisTravailForm favorisForm = new FavorisTravailForm();
        favorisForm.setIdDossiers(String.join(";", idDossiers));
        Map<String, Object> map = new HashMap<>();
        map.put(IS_CREATION, true);
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        map.put("favorisForm", favorisForm);
        context.putInContextData(LIST_DOSSIERS_ID, idDossiers);
        template.setContext(context);
        template.setData(map);
        return template;
    }

    /**
     * Création d'un nouveau favori pour la liste de dossiers sélectionnés
     */
    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("/sauvegarde")
    public Object saveNewFavoris(@SwBeanParam FavorisTravailForm favorisForm) {
        context.putInContextData(FAVORIS_TRAVAIL_FORM, favorisForm);
        ReponsesUIServiceLocator.getFavorisDossierUIService().createAndAddFavoris(context);
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        String urlPreviousPage = context.getUrlPreviousPage();
        if (!StringUtils.contains(context.getUrlPreviousPage(), "#")) {
            urlPreviousPage += STURLConstants.MAIN_CONTENT_ID;
        }
        return redirect(urlPreviousPage);
    }

    @Path("listeDossiers")
    public Object getListeDossiers() throws InstantiationException, IllegalAccessException {
        ThTemplate template = getMyTemplate(context);
        template.setName("pages/listeQuestionFavorisTravail");
        template.setContext(context);
        return newObject("FavorisTravailAjax", context, template);
    }

    protected ThTemplate getMyTemplate(SpecificContext context) throws InstantiationException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        Class<ThTemplate> oldTemplate = (Class<ThTemplate>) context
            .getWebcontext()
            .getUserSession()
            .get(SpecificContext.LAST_TEMPLATE);
        if (oldTemplate == ThTemplate.class) {
            return new ReponsesFavorisTravailTemplate();
        } else {
            return oldTemplate.newInstance();
        }
    }
}
