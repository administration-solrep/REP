package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.jaxrs.webobject.page.dossier.SSSubstitutionFDR;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SubstitutionFDRAjax")
public class ReponsesSubstitutionFDRAjax extends SSSubstitutionFDR {

    @POST
    @Path("liste")
    public ThTemplate getListModeleSubstitution(@SwBeanParam ModeleFDRListForm form) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/table/tableModelesFDR", context);
        DocumentModel dossierDoc = context.getCurrentDocument();

        context.putInContextData(SSContextDataKey.LIST_MODELE_FDR, form);
        UserSessionHelper.putUserSessionParameter(context, SSUserSessionKey.MODELE_FDR_LIST_FORM, form);

        // Récupération de la liste des modèles disponnible pour la substitution
        ReponsesModeleFdrListUIService modeleFDRListUIService = ReponsesUIServiceLocator.getReponsesModeleFdrListUIService();
        ModeleFDRList lstResults = modeleFDRListUIService.getModelesFDRSubstitution(context);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.RESULT_FORM, form);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.ID_DOSSIER, dossierDoc.getId());
        map.put(STTemplateConstants.DATA_URL, "/dossier/" + dossierDoc.getId() + "/substitution/liste");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/dossier/" + dossierDoc.getId() + "/substitution/liste");
        template.setData(map);
        return template;
    }
}
