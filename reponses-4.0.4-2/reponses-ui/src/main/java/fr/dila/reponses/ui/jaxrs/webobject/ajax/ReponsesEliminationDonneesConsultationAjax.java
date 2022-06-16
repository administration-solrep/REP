package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.LISTE_ELIMINATION_CONSULTATION_LIST;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.LISTE_ELIMINATION;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.ui.bean.EliminationDonneesConsultationList;
import fr.dila.reponses.ui.bean.EliminationDonneesDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EliminationDonneesConsultationAjax")
public class ReponsesEliminationDonneesConsultationAjax extends SolonWebObject {
    private static final String DATA_URL_CONSULT = "/admin/eliminationDonnees/consultation";

    public ReponsesEliminationDonneesConsultationAjax() {
        super();
    }

    @GET
    public ThTemplate getListeConsult(
        @QueryParam("id") String id,
        @SwBeanParam PaginationForm eliminationDonneesConsultationListForm
    ) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL_CONSULT);
        template.setContext(context);

        context.setCurrentDocument(id);
        context.putInContextData(STContextDataKey.PAGINATION_FORM, eliminationDonneesConsultationListForm);
        EliminationDonneesConsultationList dto = ReponsesUIServiceLocator
            .getArchiveUIService()
            .getEliminationDonneesConsultationList(context);

        template.getData().put("listId", id);
        template.getData().put(STTemplateConstants.TITRE, dto.getTitre());
        template.getData().put(STTemplateConstants.RESULT_LIST, dto);
        template.getData().put(STTemplateConstants.LST_COLONNES, dto.getListeColonnes());
        template.getData().put(STTemplateConstants.DATA_URL, DATA_URL_CONSULT);
        template.getData().put(STTemplateConstants.RESULT_FORM, eliminationDonneesConsultationListForm);
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/eliminationDonnees/consultation");
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        // Récupération des actions sur la liste d'élimination
        EliminationDonneesDTO eliminationDonneesDTO = new EliminationDonneesDTO();
        eliminationDonneesDTO.setId(id);
        eliminationDonneesDTO.setEnCours(context.getCurrentDocument().getAdapter(ListeElimination.class).isEnCours());
        context.putInContextData(LISTE_ELIMINATION, eliminationDonneesDTO);
        template
            .getData()
            .put(STTemplateConstants.MAIN_ACTIONS, context.getActions(LISTE_ELIMINATION_CONSULTATION_LIST));

        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate(
            "fragments/table/tableEliminationDonneesConsultation",
            context
        );
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
