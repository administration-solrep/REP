package fr.dila.reponses.ui.jaxrs.webobject.page.admin.organigramme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import fr.dila.reponses.core.util.RepExcelUtil;
import fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesOrganigramme;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.STIOUtils;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.services.STGouvernementUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        WebEngine.class,
        STUIServiceLocator.class,
        SSUIServiceLocator.class,
        ReponsesOrganigrammeGouvernement.class,
        FileUtils.class,
        BlobUtils.class,
        RepExcelUtil.class,
        STIOUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesOrganigrammeGouvernementTest {
    private static final byte[] BYTE_ARRAY = new byte[] { 0, 1, 2 };

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private static final String FILENAME = "foo.xls";

    @Spy
    private ReponsesOrganigrammeGouvernement reponseOrgGouvernement;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private UserSession userSession;

    @Mock
    private InputStream uploadedInputStream;

    @Mock
    private FormDataContentDisposition fileDetail;

    @Mock
    private Blob blob;

    @Mock
    private File file;

    @Mock
    private List<InjectionGvtDTO> injections;

    @Mock
    private STGouvernementUIService gouvernementUIService;

    @Mock
    SSOrganigrammeManagerService organigrammeManager;

    @Before
    public void before() throws Exception {
        mockStatic(WebEngine.class);
        mockStatic(STUIServiceLocator.class);
        mockStatic(SSUIServiceLocator.class);

        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(context);
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);
        when(STUIServiceLocator.getSTGouvernementUIService()).thenReturn(gouvernementUIService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
        when(SSUIServiceLocator.getSSOrganigrammeManagerService()).thenReturn(organigrammeManager);

        when(fileDetail.getFileName()).thenReturn(FILENAME);

        mockStatic(STIOUtils.class);
        when(STIOUtils.toByteArray(uploadedInputStream)).thenReturn(BYTE_ARRAY);
    }

    @Test
    public void testGetGouvernementCreation() {
        Whitebox.setInternalState(reponseOrgGouvernement, "context", context);
        Mockito.when(context.getAction(STActionEnum.CREATE_GOUVERNEMENT)).thenReturn(new Action());
        ThTemplate template = reponseOrgGouvernement.getGouvernementCreation();

        assertNotNull(template);
        assertNotNull(template.getData());
        assertNotNull(template.getData().get("gouvernementForm"));
        GouvernementForm createGvForm = (GouvernementForm) template.getData().get("gouvernementForm");
        assertNotNull(createGvForm);
        assertEquals("pages/organigramme/editGouvernement", template.getName());
        assertEquals(context, template.getContext());
    }

    @Test
    public void testImporterGouvernement() throws IOException {
        mockStatic(FileUtils.class);
        mockStatic(BlobUtils.class);
        mockStatic(RepExcelUtil.class);

        Mockito.when(context.getAction(STActionEnum.CREATE_GOUVERNEMENT)).thenReturn(new Action());
        when(
            BlobUtils.createSerializableBlob(
                Mockito.any(ByteArrayInputStream.class),
                Mockito.anyString(),
                Mockito.anyString()
            )
        )
            .thenReturn(blob);
        when(FileUtils.sanitizePathTraversal(FILENAME)).thenReturn(FILENAME);
        when(FileUtils.equalsMimetype(BYTE_ARRAY, FILENAME)).thenReturn(true);

        when(blob.getFile()).thenReturn(file);
        when(RepExcelUtil.prepareImportGvt(context.getSession(), file)).thenReturn(injections);
        Whitebox.setInternalState(reponseOrgGouvernement, "context", context);
        FormDataMultiPart multipart = mock(FormDataMultiPart.class);
        FormDataBodyPart field = mock(FormDataBodyPart.class);
        BodyPartEntity ent = mock(BodyPartEntity.class);
        List<FormDataBodyPart> fields = ImmutableList.of(field);

        when(multipart.getFields("gouvernementFile")).thenReturn(fields);

        when(field.getFormDataContentDisposition()).thenReturn(fileDetail);
        when(field.getEntity()).thenReturn(ent);
        when(ent.getInputStream()).thenReturn(uploadedInputStream);

        ThTemplate template = (ThTemplate) reponseOrgGouvernement.importerGouvernement(multipart);
        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(
                    "Importer le nouveau gouvernement",
                    "/admin/organigramme/gouvernement/import",
                    ReponsesOrganigramme.ORGANIGRAMME_ORDER + 1
                )
            );
        verify(context).getAction(SSActionEnum.CONFIRM_IMPORT_GOUVERNEMENT);
        verify(context).getUrlPreviousPage();
        assertNotNull(template);
        assertEquals("pages/organigramme/importGouvernement", template.getName());
        assertEquals(context, template.getContext());
        assertNotNull(template.getData());
        assertEquals(injections, template.getData().get("injections"));
        assertEquals(3, template.getData().size());
    }
}
