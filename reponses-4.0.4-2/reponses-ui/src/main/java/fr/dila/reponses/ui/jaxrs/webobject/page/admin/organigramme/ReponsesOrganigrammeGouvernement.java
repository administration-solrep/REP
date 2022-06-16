package fr.dila.reponses.ui.jaxrs.webobject.page.admin.organigramme;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import fr.dila.reponses.api.exception.ImportOrganigrammeException;
import fr.dila.reponses.core.util.RepExcelUtil;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.STIOUtils;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme.STOrganigrammeGouvernement;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeGouvernement")
public class ReponsesOrganigrammeGouvernement extends STOrganigrammeGouvernement {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesOrganigrammeGouvernement.class);

    public ReponsesOrganigrammeGouvernement() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesAdminTemplate();
    }

    @Path("/import")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Object importerGouvernement(FormDataMultiPart multipart) throws IOException {
        List<FormDataBodyPart> listFDBP = multipart.getFields("gouvernementFile");
        if (listFDBP == null) {
            // Rien n'a été uploadé
            return handleImportError(ImmutableList.of(ResourceHelper.getString("organigramme.error.import")));
        }

        // Récupération et traitement du flux
        FormDataBodyPart formDataBodyPart = listFDBP.get(0);
        FormDataContentDisposition fileDetail = formDataBodyPart.getFormDataContentDisposition();

        ThTemplate template = getMyTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Importer le nouveau gouvernement",
                "/admin/organigramme/gouvernement/import",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/importGouvernement");
        template.setContext(context);
        // Traitement du fichier
        String filename = FileUtils.sanitizePathTraversal(fileDetail.getFileName());

        List<InjectionGvtDTO> dtos;
        try (InputStream is = ((BodyPartEntity) formDataBodyPart.getEntity()).getInputStream()) {
            byte[] bytes = STIOUtils.toByteArray(is);

            if (!FileUtils.equalsMimetype(bytes, filename)) {
                // Incompatibilité de mimetype
                throw new ImportOrganigrammeException(
                    ImmutableList.of(ResourceHelper.getString("organigramme.error.import"))
                );
            }

            Blob blob = BlobUtils.createSerializableBlob(new ByteArrayInputStream(bytes), filename, null);
            File file = blob.getFile();

            dtos = RepExcelUtil.prepareImportGvt(context.getSession(), file);
        } catch (ImportOrganigrammeException e) {
            Response redirect;
            LOGGER.warn(STLogEnumImpl.LOG_EXCEPTION_TEC, e);
            if (CollectionUtils.isNotEmpty(e.getMessageKeys())) {
                redirect = handleImportError(e.getMessageKeys());
            } else {
                redirect = handleImportError(ImmutableList.of(ResourceHelper.getString("organigramme.error.import")));
            }
            return redirect;
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        UserSessionHelper.putUserSessionParameter(context, "injections", dtos);
        Map<String, Object> map = new HashMap<>();
        map.put("injections", dtos);
        Action action = context.getAction(SSActionEnum.CONFIRM_IMPORT_GOUVERNEMENT);
        map.put(STTemplateConstants.ACTION, action);
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.setData(map);
        return template;
    }

    private Response handleImportError(List<String> messages) {
        messages.forEach(m -> context.getMessageQueue().addErrorToQueue(m));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return redirect(context.getUrlPreviousPage());
    }
}
