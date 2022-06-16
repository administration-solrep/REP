package fr.dila.reponses.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.st.ui.bean.IColonneInfo;
import java.util.List;
import org.junit.Test;

public class RepDossierListTest {

    @Test
    public void testConstructor() {
        RepDossierList dto = new RepDossierList();

        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColonnes());
        assertEquals(new Integer(0), dto.getNbTotal());
    }

    @Test
    public void testSetter() {
        RepDossierList dto = new RepDossierList();
        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColonnes());
        assertEquals(new Integer(0), dto.getNbTotal());

        dto.setListe(Lists.newArrayList(new RepDossierListingDTO(), new RepDossierListingDTO()));
        assertNotNull(dto.getListe());
        assertEquals(2, dto.getListe().size());

        dto.setNbTotal(5);
        assertEquals(new Integer(5), dto.getNbTotal());

        dto.buildColonnes(new DossierListForm(), Lists.newArrayList(), false);
        assertNotNull(dto.getListeColonnes());
    }

    @Test
    public void getIHMColonnes() {
        RepDossierList dto = new RepDossierList();
        dto.buildColonnes(null, null, false);
        List<IColonneInfo> lstColonnes = dto.getListeColonnes();
        assertNotNull(lstColonnes);
        assertTrue(lstColonnes.isEmpty());

        DossierListForm form = new DossierListForm();
        dto.buildColonnes(form, Lists.newArrayList(), false);
        lstColonnes = dto.getListeColonnes();
        assertNotNull(lstColonnes);
        assertEquals(17, lstColonnes.size());
        assertValidData(lstColonnes, 7, 8);

        IColonneInfo questionCol = lstColonnes.get(0);
        assertNotNull(questionCol);
        assertEquals("label.content.header.sourceNumeroQuestion", questionCol.getLabel());
        assertTrue(questionCol.isVisible());
        assertTrue(questionCol.isSortable());
        assertEquals("questionSort", questionCol.getSortName());
        assertEquals("questionSortHeader", questionCol.getSortId());
        assertNull(questionCol.getSortValue());

        dto.buildColonnes(form, Lists.newArrayList(UserColumnEnum.LEGISLATURE.toString()), false);
        lstColonnes = dto.getListeColonnes();
        assertNotNull(lstColonnes);
        assertEquals(17, lstColonnes.size());
        assertValidData(lstColonnes, 8, 8);
    }

    private void assertValidData(List<IColonneInfo> lstColonnes, int expectedVisible, int expectedSortable) {
        int actualVisible = 0;
        int actualSortable = 0;

        for (IColonneInfo col : lstColonnes) {
            assertNotNull(col);
            if (col.isVisible()) {
                actualVisible++;
            }

            if (col.isSortable()) {
                actualSortable++;
                assertNotNull(col.getSortName());
                assertNotNull(col.getSortId());
            }
        }

        assertEquals(expectedSortable, actualSortable);
        assertEquals(expectedVisible, actualVisible);
    }
}
