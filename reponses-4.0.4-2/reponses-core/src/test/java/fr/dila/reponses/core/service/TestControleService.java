/**
 *
 */
package fr.dila.reponses.core.service;

import fr.dila.reponses.api.service.ControleService;
import fr.dila.reponses.core.ReponseFeature;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test de {@link ControleService}
 *
 * @author asatre
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestControleService {

    @Test
    public void testCompareReponses() throws IOException {
        final ControleService controleService = ReponsesServiceLocator.getControleService();
        final File file1 = new File("src/test/resources/datas/reponse_in_app");
        final File file2 = new File("src/test/resources/datas/reponse_in_jo");
        final String reponse1 = FileUtils.readFileToString(file1, Charset.defaultCharset());
        final String reponse2 = FileUtils.readFileToString(file2, Charset.defaultCharset());
        final boolean diff = controleService.compareReponses(reponse1, reponse2);
        Assert.assertFalse(diff);

        final File file3 = new File("src/test/resources/datas/reponse_in_jo_2");
        final String reponse3 = FileUtils.readFileToString(file3, Charset.defaultCharset());
        final boolean same = controleService.compareReponses(reponse1, reponse3);
        Assert.assertTrue(same);
    }
}
