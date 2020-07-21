/**
 * 
 */
package fr.dila.reponses.core.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import fr.dila.reponses.api.service.ControleService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;

/**
 * Test de {@link ControleService}
 * 
 * @author asatre
 * 
 */
public class TestControleService extends ReponsesRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testCompareReponses() throws IOException {
        final ControleService controleService = ReponsesServiceLocator.getControleService();
        final File file1 = new File("src/test/resources/datas/reponse_in_app");
        final File file2 = new File("src/test/resources/datas/reponse_in_jo");
        final String reponse1 = FileUtils.readFileToString(file1);
        final String reponse2 = FileUtils.readFileToString(file2);
        final boolean diff = controleService.compareReponses(reponse1, reponse2);
        Assert.assertFalse(diff);

        final File file3 = new File("src/test/resources/datas/reponse_in_jo_2");
        final String reponse3 = FileUtils.readFileToString(file3);
        final boolean same = controleService.compareReponses(reponse1, reponse3);
        Assert.assertTrue(same);
    }

}
