package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.List;
import org.junit.Test;

public class ModeleFDRListTest {

    @Test
    public void testConstructor() {
        RepDossierList dto = new RepDossierList();

        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColonnes());
        assertEquals(new Integer(0), dto.getNbTotal());
    }

    @Test
    public void testSetter() {
        ModeleFDRList dto = new ModeleFDRList();
        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColonnes());
        assertEquals(new Integer(0), dto.getNbTotal());

        dto.setListe(Lists.newArrayList(new FeuilleRouteDTO(), new FeuilleRouteDTO()));
        assertNotNull(dto.getListe());
        assertEquals(2, dto.getListe().size());

        dto.setNbTotal(11);
        assertEquals(new Integer(11), dto.getNbTotal());

        dto.buildColonnes(new ModeleFDRListForm());
        assertNotNull(dto.getListeColonnes());
    }

    @Test
    public void getIHMColonnes() {
        ModeleFDRList dto = new ModeleFDRList();
        dto.buildColonnes(null);
        List<ColonneInfo> lstColonnes = dto.getListeColonnes();
        assertNotNull(lstColonnes);
        assertTrue(lstColonnes.isEmpty());

        ModeleFDRListForm form = new ModeleFDRListForm();
        dto.buildColonnes(form);
        lstColonnes = dto.getListeColonnes();
        assertNotNull(lstColonnes);
        assertEquals(7, lstColonnes.size());

        ColonneInfo etatCol = lstColonnes.get(0);
        assertNotNull(etatCol);
        assertEquals("modeleFDR.content.header.etat", etatCol.getLabel());
        assertTrue(etatCol.isVisible());
        assertTrue(etatCol.isSortable());
        assertEquals("etat", etatCol.getSortName());
        assertEquals("etatHeader", etatCol.getSortId());
        assertNull(etatCol.getSortValue());

        ColonneInfo intituleCol = lstColonnes.get(1);
        assertNotNull(intituleCol);
        assertEquals("modeleFDR.content.header.intitule", intituleCol.getLabel());
        assertTrue(intituleCol.isSortable());
        assertEquals("intitule", intituleCol.getSortName());
        assertNull(intituleCol.getSortValue());
        assertTrue(intituleCol.isLabelVisible());
        assertEquals("intituleHeader", intituleCol.getSortId());
    }
}
