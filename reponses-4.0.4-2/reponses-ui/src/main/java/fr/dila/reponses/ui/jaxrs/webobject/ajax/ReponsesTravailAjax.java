package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.cli.MissingArgumentException;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ReponsesTravailAjax")
public class ReponsesTravailAjax extends AbstractReponsesTravail {

    public ReponsesTravailAjax() {
        super();
    }

    @GET
    @Path("listeMin")
    public ThTemplate getListeMin(
        @QueryParam(PARAM_MIN_ATTRIB) String minAttribId,
        @QueryParam(PARAM_ROUTING_TASK_TYPE) String routingType,
        @QueryParam(PARAM_SELECTION_POSTE) String selectionPoste,
        @QueryParam(PARAM_SELECTION_USER) String selectionUser,
        @SwBeanParam DossierTravailListForm resultform
    )
        throws MissingArgumentException {
        fillContextData(selectionPoste, selectionUser, minAttribId, resultform);
        context.putInContextData(ReponsesContextDataKey.ROUTING_TASK_TYPE, routingType);

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getDossierListUIService()
            .getDossiersFromMinCorbeille(context);

        Map<String, Object> otherParameter = getOtherParameterMap(selectionPoste, selectionUser, minAttribId);
        otherParameter.put(PARAM_ROUTING_TASK_TYPE, routingType);

        return buildTemplateFromData(lstResults, resultform, otherParameter, "listeMin");
    }

    @GET
    @Path("listePoste")
    public ThTemplate getListePoste(
        @QueryParam("posteId") String posteId,
        @QueryParam(PARAM_MIN_ATTRIB) String minAttribId,
        @QueryParam(PARAM_SELECTION_POSTE) String selectionPoste,
        @QueryParam(PARAM_SELECTION_USER) String selectionUser,
        @SwBeanParam DossierTravailListForm resultform
    )
        throws MissingArgumentException {
        fillContextData(selectionPoste, selectionUser, minAttribId, resultform);
        context.putInContextData(PARAM_POSTE_ID, posteId);

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getDossierListUIService()
            .getDossiersFromPosteCorbeille(context);

        Map<String, Object> otherParameter = getOtherParameterMap(selectionPoste, selectionUser, minAttribId);
        otherParameter.put(PARAM_POSTE_ID, posteId);

        return buildTemplateFromData(lstResults, resultform, otherParameter, "listePoste");
    }

    @GET
    @Path("listeSignal")
    public ThTemplate getListeSignal(
        @QueryParam("isSignale") Boolean isSignale,
        @QueryParam(PARAM_MIN_ATTRIB) String minAttribId,
        @QueryParam(PARAM_SELECTION_POSTE) String selectionPoste,
        @QueryParam(PARAM_SELECTION_USER) String selectionUser,
        @SwBeanParam DossierTravailListForm resultform
    )
        throws MissingArgumentException {
        fillContextData(selectionPoste, selectionUser, minAttribId, resultform);
        context.putInContextData(PARAM_IS_SIGNALE, isSignale);

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getDossierListUIService()
            .getDossiersFromSignaleCorbeille(context);

        Map<String, Object> otherParameter = getOtherParameterMap(selectionPoste, selectionUser, minAttribId);
        otherParameter.put(PARAM_IS_SIGNALE, isSignale);

        return buildTemplateFromData(lstResults, resultform, otherParameter, "listeSignal");
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate();
    }

    @Override
    protected String getFragment() {
        return "fragments/table/tableDossiers";
    }
}
