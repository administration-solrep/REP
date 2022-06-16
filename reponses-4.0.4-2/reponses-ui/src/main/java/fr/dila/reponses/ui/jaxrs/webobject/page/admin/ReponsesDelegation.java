package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DELEGATION_CONSULTATION_ACTIONS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DELEGATION_CREATION_ACTIONS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DELEGATION_MODIFICATION_ACTIONS;

import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsDonneesListForm;
import fr.dila.reponses.ui.th.bean.DelegationsRecuesListForm;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Delegation")
public class ReponsesDelegation extends SolonWebObject {
    public static final int DELEGATION_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String NAVIGATION_TITLE_AFFICHAGE = "reponses.delegation.detail.title";
    public static final String NAVIGATION_TITLE_CREATION = "reponses.delegation.create.action";
    public static final String NAVIGATION_TITLE_MODIFICATION = "reponses.delegation.modification.action";
    public static final String DATA_URL_AFFICHAGE = "/admin/delegation/%s";
    public static final String DATA_URL_CREATION = "/admin/delegation/creation";
    public static final String NAVIGATION_HOME_TITLE = "menu.admin.user.delegation.title";
    public static final String DATA_HOME_URL = "/admin/delegation/liste";
    public static final String DATA_URL_MODIFICATION = "/admin/delegation/%s/modification";

    public ReponsesDelegation() {
        super();
    }

    @Path("liste")
    public Object doHome(
        @SwBeanParam DelegationsRecuesListForm delegationsRecuesListForm,
        @SwBeanParam DelegationsDonneesListForm delegationsDonneesListForm
    ) {
        ThTemplate template = getMyTemplate(context);
        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_HOME_TITLE, DATA_HOME_URL, DELEGATION_ORDER, context.getWebcontext().getRequest())
        );
        template.setName("pages/admin/delegations/liste");
        template.setContext(context);

        return newObject("DelegationAjax", context, template);
    }

    @GET
    @Path("creation")
    public ThTemplate getDelegationCreation() {
        verifyAction(ReponsesActionEnum.ADMIN_DELEGATION_CREATE, DATA_URL_CREATION);

        template = getMyTemplate(context);
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                DATA_URL_CREATION,
                DELEGATION_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        template.getData().put("delegationCreation", true);
        template.getData().put(STTemplateConstants.EDIT_ACTIONS, context.getActions(DELEGATION_CREATION_ACTIONS));

        DelegationForm delegationForm = new DelegationForm();
        delegationForm.setSourceName(
            STServiceLocator.getSTUserService().getUserFullName(context.getSession().getPrincipal().getName())
        );

        return delegationForm(delegationForm);
    }

    @GET
    @Path("{id}")
    public ThTemplate getDelegation(@PathParam("id") String id) {
        template = getMyTemplate(context);
        template.setName("pages/admin/delegations/delegation");
        template.setContext(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_AFFICHAGE,
                String.format(DATA_URL_AFFICHAGE, id),
                DELEGATION_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        context.putInContextData(STContextDataKey.ID, id);
        DelegationForm delegationForm = ReponsesUIServiceLocator.getDelegationUIService().fetchDelegation(context);

        context.putInContextData(
            "canModifyDelegation",
            context.getSession().getPrincipal().getName().equals(delegationForm.getSourceId())
        );
        template.getData().put(STTemplateConstants.EDIT_ACTIONS, context.getActions(DELEGATION_CONSULTATION_ACTIONS));

        template.getData().put("delegationForm", delegationForm);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }

    @GET
    @Path("{id}/modification")
    public ThTemplate getDelegationModification(@PathParam("id") String id) {
        template = getMyTemplate(context);
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                String.format(DATA_URL_MODIFICATION, id),
                DELEGATION_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        context.putInContextData(STContextDataKey.ID, id);
        DelegationForm delegationForm = ReponsesUIServiceLocator.getDelegationUIService().fetchDelegation(context);

        context.putInContextData(
            "canModifyDelegation",
            context.getSession().getPrincipal().getName().equals(delegationForm.getSourceId())
        );
        verifyAction(ReponsesActionEnum.ADMIN_DELEGATION_MODIFY, String.format(DATA_URL_MODIFICATION, id));
        template.getData().put(STTemplateConstants.EDIT_ACTIONS, context.getActions(DELEGATION_MODIFICATION_ACTIONS));

        return delegationForm(delegationForm);
    }

    private ThTemplate delegationForm(DelegationForm delegationForm) {
        template.setName("pages/admin/delegations/delegationForm");
        template.setContext(context);

        List<SelectValueDTO> profilsUtilisateur = ReponsesUIServiceLocator
            .getDelegationUIService()
            .getProfilsForCurrentUser(context);

        delegationForm.setMapDelegue(
            Collections.singletonMap(
                delegationForm.getDelegue(),
                STServiceLocator.getSTUserService().getUserFullNameWithUsername(delegationForm.getDelegue())
            )
        );

        template.getData().put("delegationForm", delegationForm);
        template.getData().put("profilsUtilisateur", profilsUtilisateur);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }

    protected ThTemplate getMyTemplate(SpecificContext context) {
        if (context.getSession().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }
}
