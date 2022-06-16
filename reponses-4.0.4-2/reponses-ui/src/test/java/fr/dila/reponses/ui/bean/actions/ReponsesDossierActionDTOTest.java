package fr.dila.reponses.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReponsesDossierActionDTOTest {

    @Test
    public void testConstructor() {
        ReponsesDossierActionDTO dto = new ReponsesDossierActionDTO();
        assertEquals(false, dto.getIsDossierContainsMinistere());
        assertEquals(false, dto.getCanReadDossierConnexe());
        assertEquals(false, dto.getCanReadAllotissement());
        assertEquals(false, dto.getIsCurrentDossierInUserMinistere());
        assertEquals(false, dto.getIsDossierArbitrated());
    }

    @Test
    public void testSetter() {
        ReponsesDossierActionDTO dto = new ReponsesDossierActionDTO();
        assertEquals(false, dto.getIsDossierContainsMinistere());
        assertEquals(false, dto.getCanReadDossierConnexe());
        assertEquals(false, dto.getCanReadAllotissement());
        assertEquals(false, dto.getIsCurrentDossierInUserMinistere());
        assertEquals(false, dto.getIsDossierArbitrated());

        dto.setIsDossierContainsMinistere(true);
        assertEquals(true, dto.getIsDossierContainsMinistere());
        dto.setCanReadDossierConnexe(true);
        assertEquals(true, dto.getCanReadDossierConnexe());
        dto.setCanReadAllotissement(true);
        assertEquals(true, dto.getCanReadAllotissement());
        dto.setIsCurrentDossierInUserMinistere(true);
        assertEquals(true, dto.getIsCurrentDossierInUserMinistere());
        dto.setIsDossierArbitrated(true);
        assertEquals(true, dto.getIsDossierArbitrated());
    }
}
