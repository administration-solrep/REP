package fr.dila.reponses.core.service;

import static fr.dila.st.api.constant.STSchemaConstant.FILE_CONTENT_PROPERTY;
import static fr.dila.st.api.constant.STSchemaConstant.FILE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.FondDeDossierService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

@RunWith(MockitoJUnitRunner.class)
public class FondDeDossierServiceImplTest {
    private FondDeDossierService fondDeDossierService;

    @Mock
    private DocumentModel fondDeDossierFileDoc;

    @Mock
    private Blob blob;

    @Before
    public void setUp() {
        fondDeDossierService = new FondDeDossierServiceImpl();

        when(fondDeDossierFileDoc.getProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY)).thenReturn(blob);
    }

    @Test
    public void shouldIsFondDeDossierFileNameCorrect() {
        when(blob.getFilename()).thenReturn("nom-de-fichier-correct.pdf");

        boolean result = fondDeDossierService.isFondDeDossierFileNameCorrect(fondDeDossierFileDoc);

        assertThat(result).isTrue();
    }

    @Test
    public void isFondDeDossierFileNameCorrectShouldReturnFalse() {
        when(blob.getFilename()).thenReturn("nom'de'fichier'incorrect.pdf");

        boolean result = fondDeDossierService.isFondDeDossierFileNameCorrect(fondDeDossierFileDoc);

        assertThat(result).isFalse();
    }
}
