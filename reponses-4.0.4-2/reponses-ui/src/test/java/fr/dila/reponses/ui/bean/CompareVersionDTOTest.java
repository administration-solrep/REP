package fr.dila.reponses.ui.bean;

import static org.junit.Assert.*;

import org.junit.Test;

public class CompareVersionDTOTest {

    @Test
    public void testConstructor() {
        CompareVersionDTO dto = new CompareVersionDTO();
        assertNull(dto.getTextFirst());
        assertNull(dto.getTextLast());
    }

    @Test
    public void testSetter() {
        CompareVersionDTO dto = new CompareVersionDTO();
        dto.setTextFirst("texte 1");
        dto.setTextLast("texte 2");
        assertEquals("texte 1", dto.getTextFirst());
        assertEquals("texte 2", dto.getTextLast());
    }
}
