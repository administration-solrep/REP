package fr.dila.reponses.rest.validator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("fr.dila.st.core:OSGI-INF/service/config-framework.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-config-contrib.xml")
@Deploy("fr.dila.reponses.webengine.test:OSGI-INF/test-envoyerreponsevalidator-config-contrib.xml")
public class TestEnvoyerReponseValidator {

    @Test
    public void testValidateReponseContent() {
        final String reponseValid1 = "ma reponse sans tag html";
        final String reponseValid2 = "ma reponse <table><th><td></td></th><tr><td/></tr><table> <strong>";
        final String reponseInvalid1 = "ma reponses <body>";
        final String reponseInvalid2 = "ma reponses <caption>";
        final String reponseInvalid3 = "ma reponses <img value=\"a\" >";
        final String reponseInvalid4 = "ma reponses <a value=\"a\" >";

        ValidatorResult result = null;

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseValid1);
        Assert.assertTrue(result.isValid());

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseValid2);
        Assert.assertTrue(result.isValid());

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid1);
        Assert.assertFalse(result.isValid());

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid2);
        Assert.assertFalse(result.isValid());

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid3);
        Assert.assertFalse(result.isValid());

        result = EnvoyerReponseValidator.getInstance().validateReponseContent(reponseInvalid4);
        Assert.assertFalse(result.isValid());
    }
}
