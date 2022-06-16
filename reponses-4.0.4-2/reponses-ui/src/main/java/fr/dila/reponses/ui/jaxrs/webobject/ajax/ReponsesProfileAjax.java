package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getProfilUtilisateurService;
import static java.util.stream.Collectors.toList;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.ui.th.bean.ProfilParametersForm;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSProfileAjax;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.sword.naiad.nuxeo.commons.core.helper.VocabularyHelper.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ProfileAjax")
public class ReponsesProfileAjax extends SSProfileAjax {

    @GET
    @Path("metadatas")
    public ThTemplate getMetadataValues() {
        ThTemplate template = new AjaxLayoutThTemplate(
            "fragments/components/profil/profilUtilisateurModalContent",
            context
        );

        ProfilUtilisateurService profilUtilisateurService = getProfilUtilisateurService();

        List<String> profilValues = profilUtilisateurService
            .getVocEntryAllowedColumn(context.getSession())
            .stream()
            .map(Entry::getId)
            .collect(toList());

        List<SelectValueDTO> metadataValues = ProfilUtilisateurConstants.UserColumnEnum
            .findAll()
            .stream()
            .map(userColumn -> new SelectValueDTO(userColumn.name(), userColumn.getLabel()))
            .collect(toList());

        Map<String, Object> map = new HashMap<>();
        map.put("metadataValues", metadataValues);
        map.put("profilValues", profilValues);
        ProfilUtilisateur profil = (ProfilUtilisateur) profilUtilisateurService.getProfilUtilisateurForCurrentUser(
            context.getSession()
        );
        map.put("userMailParam", profil.getParametreMail());
        template.setData(map);

        return template;
    }

    @POST
    @Path("parametres")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyParameters(@SwBeanParam ProfilParametersForm params) {
        DocumentModel profil = getProfilUtilisateurService()
            .getProfilUtilisateurForCurrentUser(context.getSession())
            .getDocument();
        MapDoc2Bean.beanToDoc(params, profil);
        context.getSession().saveDocument(profil);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
