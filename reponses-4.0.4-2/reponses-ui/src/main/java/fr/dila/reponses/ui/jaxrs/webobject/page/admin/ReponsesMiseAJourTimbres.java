package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.CURRENT_GOUVERNEMENT;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.NEXT_GOUVERNEMENT;

import fr.dila.reponses.ui.bean.MiseAJourTimbresDetailDTO;
import fr.dila.reponses.ui.bean.MiseAJourTimbresParametrage;
import fr.dila.reponses.ui.bean.MiseAJourTimbresRecapitulatifList;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationManagerUIService;
import fr.dila.reponses.ui.th.bean.MisesAJourTimbresFormDTO;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MiseAJourTimbres")
public class ReponsesMiseAJourTimbres extends SolonWebObject {
    public static final int MAJ_TIMBRES_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String DATA_URL = "/admin/timbres#main_content";
    public static final String NAVIGATION_TITLE = "majtimbres.title.label";
    public static final String DATA_HISTORIQUE_URL = "/admin/timbres/historique#main_content";
    public static final String DATA_HISTORIQUE_DETAIL_URL = "/admin/timbres/detail";
    public static final String NAVIGATION_HISTORIQUE_TITLE = "menu.admin.user.timbres.title";
    public static final String NAVIGATION_RECAPITULATIF_TITLE = "majtimbres.recapitulatif.page.title";
    public static final String DATA_RECAPITULATIF_URL = "/admin/timbres/recapitulatif#main_content";
    public static final String PARAMETRE_TIMBRE_KEY = "parametresTimbres";

    public ReponsesMiseAJourTimbres() {
        super();
    }

    @GET
    public Object getHome() {
        SSOrganigrammeManagerService ssOrganigrammeManagerService = SSUIServiceLocator.getSSOrganigrammeManagerService();
        ssOrganigrammeManagerService.computeOrganigrammeActions(context);
        verifyAction(ReponsesActionEnum.MAJ_TIMBRES, DATA_URL);
        ThTemplate template = new ReponsesAdminTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/majTimbres/parametrage");
        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TITLE, DATA_URL, MAJ_TIMBRES_ORDER + 1, context.getWebcontext().getRequest())
        );

        final ReponsesMigrationManagerUIService service = ReponsesUIServiceLocator.getReponsesMigrationManagerUIService();

        boolean isPageAccessible = service.initDataForSelectionTimbres(context);
        template.getData().put("isPageAccessible", isPageAccessible);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        if (!isPageAccessible) {
            return template;
        }

        MiseAJourTimbresParametrage dto = service.getMiseAJourTimbresParametrage(context);

        MisesAJourTimbresFormDTO saisiForm = UserSessionHelper.getUserSessionParameter(
            context,
            PARAMETRE_TIMBRE_KEY,
            MisesAJourTimbresFormDTO.class
        );

        Map<String, MiseAJourTimbresDetailDTO> historyForm = new HashMap<>();
        boolean briserToutesSignatures = true;
        boolean migrerTousDossiersClos = true;
        if (Objects.nonNull(saisiForm)) {
            for (MiseAJourTimbresDetailDTO miseAJourTimbresDetailDTO : saisiForm.getDetails()) {
                historyForm.put(miseAJourTimbresDetailDTO.getOldMinistereId(), miseAJourTimbresDetailDTO);
            }
            briserToutesSignatures = saisiForm.getBriserToutesSignatures();
            migrerTousDossiersClos = saisiForm.getMigrerTousDossiersClos();
        }
        List<SelectValueDTO> newTimbreList = service.getNewTimbreList(context);

        STGouvernementService gouvernementService = STServiceLocator.getSTGouvernementService();
        String currGouvernementName = gouvernementService
            .getGouvernement(context.getFromContextData(CURRENT_GOUVERNEMENT))
            .getLabel();
        String nextGouvernementName = gouvernementService
            .getGouvernement(context.getFromContextData(NEXT_GOUVERNEMENT))
            .getLabel();

        template.getData().put("briserToutesSignatures", briserToutesSignatures);
        template.getData().put("migrerTousDossiersClos", migrerTousDossiersClos);
        template.getData().put("historyForm", historyForm);
        template.getData().put("ministeres", dto.getMinisteres());
        template.getData().put("newMinisteresSelect", newTimbreList);
        template.getData().put("currGouvernementName", currGouvernementName);
        template.getData().put("nextGouvernementName", nextGouvernementName);

        return template;
    }

    @GET
    @Path("recapitulatif")
    public ThTemplate getRecap() {
        ThTemplate template = new ReponsesAdminTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/majTimbres/recapitulatif");
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_RECAPITULATIF_TITLE,
                DATA_RECAPITULATIF_URL,
                MAJ_TIMBRES_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        loadRecapData(context, template);

        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }

    public void loadRecapData(SpecificContext context, ThTemplate template) {
        final ReponsesMigrationManagerUIService service = ReponsesUIServiceLocator.getReponsesMigrationManagerUIService();

        List<GouvernementNode> gvtList = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();
        if (gvtList.size() > 1) {
            context.putInContextData(CURRENT_GOUVERNEMENT, gvtList.get(gvtList.size() - 2).getId());
            context.putInContextData(NEXT_GOUVERNEMENT, gvtList.get(gvtList.size() - 1).getId());
        }

        boolean isPollCountActivated = service.isPollCountActivated(context);

        MiseAJourTimbresRecapitulatifList dto = service.getMiseAJourTimbresRecapitulatifList(context);

        template.getData().put(STTemplateConstants.RESULT_LIST, dto);
        template.getData().put(STTemplateConstants.LST_COLONNES, dto.getListeColonnes());
        template.getData().put("isPollCountActivated", isPollCountActivated);
    }

    @GET
    @Path("rafraichir")
    public ThTemplate refreshRecapRecap() {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("fragments/table/tableRecapitulatifMiseAJourTimbres");

        loadRecapData(context, template);

        return template;
    }

    @Path("historique")
    public Object getHistorique() {
        SSOrganigrammeManagerService organigrammeManagerService = SSUIServiceLocator.getSSOrganigrammeManagerService();
        organigrammeManagerService.computeOrganigrammeActions(context);
        verifyAction(ReponsesActionEnum.ADMIN_MIGRATION_TIMBRES, DATA_HISTORIQUE_URL);
        ThTemplate template = new ReponsesAdminTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/majTimbres/historique");
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_HISTORIQUE_TITLE,
                DATA_HISTORIQUE_URL,
                MAJ_TIMBRES_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        return newObject("HistoriqueMAJTimbres", context, template);
    }

    @Path("detail")
    public Object getDetail() {
        SSOrganigrammeManagerService organigrammeManagerService = SSUIServiceLocator.getSSOrganigrammeManagerService();
        organigrammeManagerService.computeOrganigrammeActions(context);
        if (context.getAction(ReponsesActionEnum.ADMIN_MIGRATION_TIMBRES) == null) {
            throw new STAuthorizationException(DATA_HISTORIQUE_DETAIL_URL);
        }
        ThTemplate template = new ReponsesAdminTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/majTimbres/detailHistorique");

        return newObject("DetailHistoriqueMAJTimbres", context, template);
    }
}
