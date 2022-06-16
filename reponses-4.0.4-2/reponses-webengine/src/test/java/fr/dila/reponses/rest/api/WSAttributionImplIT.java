package fr.dila.reponses.rest.api;

import static fr.dila.reponses.rest.api.WSAttribution.METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT;
import static fr.dila.reponses.rest.api.WSAttribution.SERVICE_NAME;
import static fr.sword.xsd.reponses.EnFonction.TRUE;

import com.google.inject.Inject;
import fr.dila.reponses.core.operation.nxshell.DataInjectionOperation;
import fr.sword.xsd.commons.ReponsesVersionConstants;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;
import fr.sword.xsd.reponses.TraitementStatut;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponsesWebEngineFeature.class)
public class WSAttributionImplIT {
    @Inject
    private ReponsesWebEngineFeature reponsesWebEngineFeature;

    @Test
    public void doCheck() throws Exception {
        checkTestMethod();
        checkVersionMethod();
        chercherMembreGouvernement();
    }

    public void checkTestMethod() throws Exception {
        try (
            CloseableClientResponse response = reponsesWebEngineFeature.getResponse(
                ReponsesWebEngineFeature.RequestType.GET,
                SERVICE_NAME + "/test"
            )
        ) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(response.getEntityInputStream(), output);
            Assert.assertEquals("WSattribution", output.toString());
        }
    }

    public void checkVersionMethod() throws Exception {
        VersionResponse versionResponse = reponsesWebEngineFeature.getXmlResponseViaGet(
            SERVICE_NAME + "/version",
            VersionResponse.class
        );
        Assert.assertNotNull(versionResponse.getVersionReponse());
        Assert.assertEquals(
            ReponsesVersionConstants.WS_ATTRIBUTION,
            versionResponse.getVersionReponse().getWsAttribution()
        );
        Assert.assertEquals(ReponsesVersionConstants.QUESTIONS, versionResponse.getVersionReponse().getQuestions());
        Assert.assertEquals(
            ReponsesVersionConstants.REPONSES_COMMONS,
            versionResponse.getVersionReponse().getReponsesCommons()
        );
        Assert.assertEquals(ReponsesVersionConstants.AR, versionResponse.getVersionReponse().getAr());
    }

    public void chercherMembreGouvernement() throws Exception {
        ChercherMembresGouvernementRequest request = new ChercherMembresGouvernementRequest();
        request.setEnFonction(TRUE);

        ChercherMembresGouvernementResponse response = reponsesWebEngineFeature.getXmlResponseViaXmlPost(
            SERVICE_NAME + "/" + METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT,
            request,
            ChercherMembresGouvernementResponse.class
        );

        Assert.assertNotNull(response);
        Assert.assertEquals(TraitementStatut.OK, response.getStatut());
        Assert.assertNull(response.getMessageErreur());

        Set<ImmutableTriple<String, String, Long>> data = response
            .getMembreGouvernement()
            .stream()
            .map(
                membreGouvernement ->
                    ImmutableTriple.of(
                        String.valueOf(membreGouvernement.getMinistre().getId()),
                        membreGouvernement.getMinistre().getTitreMinistre(),
                        membreGouvernement.getMinistre().getOrdreProto().longValue()
                    )
            )
            .collect(Collectors.toSet());

        Assert.assertTrue(
            CollectionUtils.isEqualCollection(DataInjectionOperation.MEMBRES_GOUVERNEMENT.values(), data)
        );
    }
}
