package fr.dila.reponses.ui.th.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({})
@PowerMockIgnore("javax.management.*")
public class ReponsesModeleFdrFormTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    ReponsesModeleFdrForm dto;

    @Mock
    SpecificContext context;

    @Before
    public void before() {}

    @Test
    public void testDto() {
        String id = "identifiant";
        String intitule = "intitule";
        String idMinistere = "000001";
        String libelleMinistere = "Premier ministre";
        Boolean modeleParDefaut = true;
        String titreQuestion = "Titre";
        String description = "Description";
        String idDirection = "000002";
        String libelleDirection = "direction pilote";
        ArrayList<String> anRubriqueComp = new ArrayList<>();
        anRubriqueComp.add("rubrique an");
        ArrayList<String> anAnalyseComp = new ArrayList<>();
        anAnalyseComp.add("analyse an");
        ArrayList<String> anTAnalyseComp = new ArrayList<>();
        anTAnalyseComp.add("T analyse an");
        ArrayList<String> seThemeComp = new ArrayList<>();
        seThemeComp.add("theme se");
        ArrayList<String> seRenvoiComp = new ArrayList<>();
        seRenvoiComp.add("renvoi se");
        ArrayList<String> seRubriqueComp = new ArrayList<>();
        seRubriqueComp.add("rubrique se");
        ArrayList<String> motsClesMinisteres = new ArrayList<>();
        motsClesMinisteres.add("mot clé min");
        String etat = "etat";
        IndexationDTO indexationDTO = new IndexationDTO();
        Boolean isLock = false;
        Boolean isLockByCurrentUser = false;
        FdrDTO fdrDto = new FdrDTO();

        ReponsesModeleFdrForm form = new ReponsesModeleFdrForm();

        assertNull(form.getId());
        assertNull(form.getIntitule());
        assertNull(form.getIdMinistere());
        assertNull(form.getLibelleMinistere());
        assertFalse(form.getModeleParDefaut());
        assertNull(form.getTitreQuestion());
        assertNull(form.getDescription());
        assertNull(form.getIdDirection());
        assertNull(form.getLibelleDirection());
        assertEquals(0, form.getAnRubriqueComp().size());
        assertEquals(0, form.getAnAnalyseComp().size());
        assertEquals(0, form.getAnTAnalyseComp().size());
        assertEquals(0, form.getSeThemeComp().size());
        assertEquals(0, form.getSeRenvoiComp().size());
        assertEquals(0, form.getSeRubriqueComp().size());
        assertEquals(0, form.getMotsClesMinisteres().size());
        assertNull(form.getEtat());
        assertNotNull(form.getIndexationDTO());
        assertNull(form.getIsLock());
        assertNull(form.getIsLockByCurrentUser());
        assertNull(form.getFdrDto());

        form.setId(id);
        form.setIntitule(intitule);
        form.setIdMinistere(idMinistere);
        form.setLibelleMinistere(libelleMinistere);
        form.setModeleParDefaut(modeleParDefaut);
        form.setTitreQuestion(titreQuestion);
        form.setDescription(description);
        form.setIdDirection(idDirection);
        form.setLibelleDirection(libelleDirection);
        form.setAnRubriqueComp(anRubriqueComp);
        form.setAnAnalyseComp(anAnalyseComp);
        form.setAnTAnalyseComp(anTAnalyseComp);
        form.setSeThemeComp(seThemeComp);
        form.setSeRenvoiComp(seRenvoiComp);
        form.setSeRubriqueComp(seRubriqueComp);
        form.setMotsClesMinisteres(motsClesMinisteres);
        form.setEtat(etat);
        form.setIndexationDTO(indexationDTO);
        form.setIsLock(isLock);
        form.setIsLockByCurrentUser(isLockByCurrentUser);
        form.setFdrDto(fdrDto);

        assertEquals(id, form.getId());
        assertEquals(intitule, form.getIntitule());
        assertEquals(idMinistere, form.getIdMinistere());
        assertEquals(libelleMinistere, form.getLibelleMinistere());
        assertEquals(modeleParDefaut, form.getModeleParDefaut());
        assertEquals(titreQuestion, form.getTitreQuestion());
        assertEquals(description, form.getDescription());
        assertEquals(idDirection, form.getIdDirection());
        assertEquals(libelleDirection, form.getLibelleDirection());
        assertEquals(anRubriqueComp, form.getAnRubriqueComp());
        assertEquals(anAnalyseComp, form.getAnAnalyseComp());
        assertEquals(anTAnalyseComp, form.getAnTAnalyseComp());
        assertEquals(seThemeComp, form.getSeThemeComp());
        assertEquals(seRenvoiComp, form.getSeRenvoiComp());
        assertEquals(seRubriqueComp, form.getSeRubriqueComp());
        assertEquals(motsClesMinisteres, form.getMotsClesMinisteres());
        assertEquals(etat, form.getEtat());
        assertEquals(indexationDTO, form.getIndexationDTO());
        assertEquals(isLock, form.getIsLock());
        assertEquals(isLockByCurrentUser, form.getIsLockByCurrentUser());
        assertEquals(fdrDto, form.getFdrDto());
    }
}
