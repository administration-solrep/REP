package fr.dila.reponses.ui.jaxrs.webobject.page.fdr;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.reponses.ui.th.model.ReponsesLayoutThTemplate;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.webobject.page.dossier.SSSubstitutionFDR;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SubstitutionFDR")
public class ReponsesSubstitutionFDR extends SSSubstitutionFDR {

    public ReponsesSubstitutionFDR() {
        super();
    }

    @GET
    @Path("/liste")
    public ThTemplate getListModeleSubstitution() throws InstantiationException, IllegalAccessException {
        DocumentModel dossierDoc = context.getCurrentDocument();

        // Récupération de la liste des modèles disponnible pour la substitution
        ReponsesModeleFdrListUIService modeleFDRListUIService = ReponsesUIServiceLocator.getReponsesModeleFdrListUIService();
        ModeleFDRList lstResults = modeleFDRListUIService.getModelesFDRSubstitution(context);

        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Question question = dossier.getQuestion(context.getSession());

        return buildTemplateListModeleSubstitution(lstResults, question.getSourceNumeroQuestion());
    }

    @GET
    @Path("/consult")
    public ThTemplate getModeleSubstitutionConsult(@QueryParam("idModele") String idModele)
        throws IllegalAccessException, InstantiationException {
        verifyAction(SSActionEnum.DOSSIER_SUBSTITUER_FDR_VALIDER, "/consult");

        DocumentModel dossierDoc = context.getCurrentDocument();
        context.setCurrentDocument(idModele);
        ReponsesModeleFdrForm modeleForm = new ReponsesModeleFdrForm();
        ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService().consultModeleSubstitution(context, modeleForm);

        return buildTemplateSubstitutionConsult(dossierDoc, modeleForm);
    }

    @GET
    @Path("/valider")
    public Response validerSubstitutionModele(@QueryParam("idModele") String idModele) {
        context.putInContextData(SSContextDataKey.ID_MODELE, idModele);

        ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService().substituerRoute(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return redirect(getValiderSubstitutionUrl(context.getNavigationContext()));
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesLayoutThTemplate();
    }
}
