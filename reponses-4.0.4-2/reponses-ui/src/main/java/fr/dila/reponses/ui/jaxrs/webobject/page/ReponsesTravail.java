package fr.dila.reponses.ui.jaxrs.webobject.page;

import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService.EtatSignalement;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.reponses.ui.th.model.ReponsesTravailTemplate;
import fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.cli.MissingArgumentException;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliTravail")
public class ReponsesTravail extends AbstractReponsesTravail {

    public ReponsesTravail() {
        super();
    }

    @GET
    public ThTemplate getHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/espaceTravailHome");
        context.removeNavigationContextTitle();
        context.setContextData(new HashMap<>());
        // Sur le home on désactive la clé sélectionnée
        context.putInContextData(SSMailboxListComponentServiceImpl.ACTIVE_KEY, "");
        template.setContext(context);
        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.IS_FROM_TRAVAIL, true);
        template.setData(map);
        return template;
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

        return buildTemplateFromData(
            lstResults,
            resultform,
            otherParameter,
            "listeMin",
            minAttribId + "__" + routingType
        );
    }

    @GET
    @Path("listePoste")
    public ThTemplate getListePoste(
        @QueryParam(PARAM_POSTE_ID) String posteId,
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

        return buildTemplateFromData(
            lstResults,
            resultform,
            otherParameter,
            "listePoste",
            posteId + "__" + minAttribId
        );
    }

    @GET
    @Path("listeSignal")
    public ThTemplate getListeSignal(
        @QueryParam(PARAM_IS_SIGNALE) Boolean isSignale,
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

        return buildTemplateFromData(
            lstResults,
            resultform,
            otherParameter,
            "listeSignal",
            isSignale != null && isSignale
                ? EtatSignalement.QUESTIONS_SIGNALEES.toString()
                : EtatSignalement.QUESTIONS_NON_SIGNALEES + "__" + minAttribId
        );
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesTravailTemplate();
    }

    @Override
    protected String getFragment() {
        return "pages/listeQuestionCorbeille";
    }
}
