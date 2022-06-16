package fr.dila.reponses.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.st.ui.bean.DocumentDTO;
import fr.dila.st.ui.bean.DossierDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class DossierFondDTOTest {

    @Test
    public void testConstructor() {
        DossierDTO dto = new DossierDTO();
        assertNull(dto.getId());
        assertNull(dto.getNom());
        assertNotNull(dto.getLstDocuments());
        assertThat(dto.getLstDocuments()).isEmpty();
    }

    @Test
    public void testSetter() {
        DossierDTO dto = new DossierDTO();
        assertNull(dto.getId());
        assertNotNull(dto.getLstDocuments());
        assertThat(dto.getLstDocuments()).isEmpty();

        dto.setId("1");
        assertEquals("1", dto.getId());

        List<DocumentDTO> documents = new ArrayList<>();
        documents.add(new DocumentDTO("2", "fichiers", "", "27/08/2020", "", "", ".pdf"));
        dto.setLstDocuments(documents);
        assertNotNull(dto.getLstDocuments());
        assertEquals(1, dto.getLstDocuments().size());
        assertEquals("27/08/2020", dto.getLstDocuments().get(0).getDate());
    }
}
