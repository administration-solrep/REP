package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getParapheurUIService;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierParapheur")
public class ReponsesDossierParapheur extends SolonWebObject {

    public ReponsesDossierParapheur() {
        super();
    }

    @GET
    public ThTemplate getParapheur() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }
        ParapheurDTO dto = getParapheurUIService().getParapheur(context);

        if (
            context.getFromContextData(ReponsesContextDataKey.DOSSIER_ACTIONS) == null &&
            ReponsesActionsServiceLocator.getReponseActionService().canUserBriserReponse(context)
        ) {
            ConsultDossierDTO dossier = ReponsesUIServiceLocator.getDossierUIService().getDossierConsult(context);
            template.getData().put("monDossier", dossier);
        }

        // texte Question : splitter en plusieurs paragraphes
        List<String> questionParagraphe = new ArrayList<>(Arrays.asList(dto.getTexteQuestion().split("\n")));
        template.getData().put("question", questionParagraphe);
        template.getData().put("versions", getParapheurUIService().getVersionDTOs(context));
        template.getData().put("parapheurDto", dto);
        template.getData().put("tabActions", context.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS));
        template.getData().put("cachetAction", context.getAction(ReponsesActionEnum.CASSER_CACHET_SERVEUR));
        template.getData().put("comparerAction", context.getAction(ReponsesActionEnum.COMPARER_VERSIONS_PARAPHEUR));
        // Valeurs possibles pour le champ format
        List<SelectValueDTO> formats = new ArrayList<>();
        formats.add(new SelectValueDTO("html", "Html"));
        formats.add(new SelectValueDTO("text", "Text"));
        formats.add(new SelectValueDTO("xml", "Xml"));
        template.getData().put("formatListValues", formats);
        template.getData().put("format", "html"); // valeur par défaut

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Object saveParapheur(@SwBeanParam ParapheurDTO dto) {
        ReponsesActionsServiceLocator.getParapheurActionService().saveDossier(context, dto);

        //Comme je recharge la page si pas d'erreur je met en session les messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/parapheur", getMyContext());
    }
}
