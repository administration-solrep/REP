package fr.dila.reponses.rest.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;
import fr.dila.reponses.core.operation.nxshell.DataInjectionOperation;
import fr.dila.reponses.core.service.ReponsesUserManagerImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.helper.ParameterTestHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.test.STAuditFeature;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.jaxrs.test.JerseyClientHelper;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.ServletContainerFeature;

@Features({ PlatformFeature.class, AutomationFeature.class, WebEngineFeature.class, STAuditFeature.class })
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.content.template")
@Deploy("org.nuxeo.ecm.platform.uidgen.core")
@Deploy("org.nuxeo.ecm.platform.types.api")
@Deploy("org.nuxeo.ecm.core.persistence")
@Deploy("fr.sword.naiad.nuxeo.ufnxql.core")
@Deploy("fr.dila.st.api")
@Deploy("fr.dila.st.core")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-datasource.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-datasource-sword-provider.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-default-user-directory.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-config-contrib.xml")
@Deploy("fr.dila.ss.api")
@Deploy("fr.dila.ss.core")
@Deploy("fr.dila.reponses.api")
@Deploy("fr.dila.reponses.core")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-event-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-feuille-route-ecm-type-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-vocabulary-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/service/test-feuille-route-model-framework.xml")
@Deploy("fr.dila.reponses.webengine")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-datainjection-operation-contrib.xml")
@Deploy("fr.dila.reponses.webengine.test:OSGI-INF/uidgenerator-test-contrib.xml")
@BlacklistComponent({ "fr.dila.st.core.datasources.contrib", "org.nuxeo.ecm.platform.usermanager.test" })
public class ReponsesWebEngineFeature extends ServletContainerFeature {
    private static final String URL = "http://localhost:%d/reponses/";
    public static final String AN_GROUPNAME = "Webservices AN";
    public static final String AN_USERNAME = "ws_an";
    private static final String PASSWORD = "Solon2NG";

    public static enum RequestType {
        GET,
        POST
    }

    @Inject
    private CoreFeature coreFeature;

    protected WebResource service;
    private Client client;

    @Inject
    private UserManager userManager;

    @Inject
    private STParametreService stParametreService;

    @Inject
    private EtatApplicationService etatAppService;

    @Inject
    private STGouvernementService gvtService;

    @Override
    public void beforeSetup(FeaturesRunner runner) {
        Assert.assertTrue(userManager instanceof ReponsesUserManagerImpl);

        Assert.assertNotNull(STServiceLocator.getOrganigrammeService());

        stParametreService.clearCache();
        etatAppService.resetCache();

        Framework.doPrivileged(
            () -> {
                CoreInstance.doPrivileged(
                    "test",
                    session -> {
                        ParameterTestHelper.changeOrCreateParammeter(
                            session,
                            STParametreConstant.DELAI_RENOUVELLEMENT_MOTS_DE_PASSE,
                            "1"
                        );
                        session.save();

                        Assert.assertTrue(session.exists(new PathRef("/case-management/workspaces/admin")));
                        Assert.assertTrue(
                            session.exists(new PathRef("/case-management/workspaces/admin/etat-application"))
                        );

                        Assert.assertNotNull(etatAppService.getEtatApplicationDocument(session));

                        new DataInjectionOperation().run(session);

                        Assert.assertEquals(2, gvtService.getGouvernementList().size());
                    }
                );
            }
        );

        service = getServiceFor(getBaseUrl());
        coreFeature.waitForAsyncCompletion();

        Assert.assertEquals(2, gvtService.getGouvernementList().size());
        Assert.assertNotNull(gvtService.getCurrentGouvernement());
    }

    private WebResource getServiceFor(String resource) {
        if (client != null) {
            client.destroy();
        }
        client = JerseyClientHelper.clientBuilder().setCredentials(AN_USERNAME, PASSWORD).build();
        return client.resource(resource);
    }

    private String getBaseUrl() {
        return String.format(URL, getPort());
    }

    final <T> T getResponse(String path, Class<T> responseType, Object request) {
        WebResource wr = service.path(path);

        WebResource.Builder builder;
        builder = wr.accept("*/*");

        return builder.post(responseType, request);
    }

    final <T> T getResponseGET(String path, Class<T> responseType) {
        WebResource wr = service.path(path);

        WebResource.Builder builder;
        builder = wr.accept("*/*");

        return builder.get(responseType);
    }

    public CloseableClientResponse getResponse(RequestType requestType, String path) {
        return getResponse(requestType, path, null, null, null, null);
    }

    public <T> T getXmlResponseViaGet(String path, Class<T> clazz) throws JAXBException {
        try (CloseableClientResponse response = getResponse(ReponsesWebEngineFeature.RequestType.GET, path)) {
            return unmarshall(response.getEntityInputStream(), clazz);
        }
    }

    public <R, T> T getXmlResponseViaXmlPost(String path, R request, Class<T> clazz) throws JAXBException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml");

        String content = marshall(request);
        try (
            CloseableClientResponse response = getResponse(
                ReponsesWebEngineFeature.RequestType.POST,
                path,
                content,
                null,
                null,
                headers
            )
        ) {
            return unmarshall(response.getEntityInputStream(), clazz);
        }
    }

    private <T> T unmarshall(InputStream inputstream, Class<T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        @SuppressWarnings("unchecked")
        T t = (T) unmarshaller.unmarshal(inputstream);
        return t;
    }

    private <R> String marshall(R request) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(request.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        return writer.toString();
    }

    public CloseableClientResponse getResponse(
        RequestType requestType,
        String path,
        String data,
        MultivaluedMap<String, String> queryParams,
        MultiPart mp,
        Map<String, String> headers
    ) {
        WebResource wr = service.path(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            wr = wr.queryParams(queryParams);
        }
        WebResource.Builder builder;
        builder = wr.accept("*/*");

        // Adding some headers if needed
        if (headers != null && !headers.isEmpty()) {
            for (String headerKey : headers.keySet()) {
                builder.header(headerKey, headers.get(headerKey));
            }
        }
        ClientResponse response = null;
        switch (requestType) {
            case GET:
                response = builder.get(ClientResponse.class);
                break;
            case POST:
                if (mp != null) {
                    response = builder.type(MultiPartMediaTypes.createFormData()).post(ClientResponse.class, mp);
                } else if (data != null) {
                    response = builder.post(ClientResponse.class, data);
                } else {
                    response = builder.post(ClientResponse.class);
                }
                break;
            default:
                throw new RuntimeException();
        }

        // Make the ClientResponse AutoCloseable by wrapping it in a CloseableClientResponse.
        // This is to strongly encourage the caller to use a try-with-resources block to make sure the response is
        // closed and avoid leaking connections.
        return CloseableClientResponse.of(response);
    }
}
