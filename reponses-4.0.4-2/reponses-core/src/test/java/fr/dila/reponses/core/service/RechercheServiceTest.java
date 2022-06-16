package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;

import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ STServiceLocator.class })
public class RechercheServiceTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private STParametreService mockSTParametreService;

    @Mock
    private CoreSession session;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);

        Mockito.when(STServiceLocator.getSTParametreService()).thenReturn(mockSTParametreService);
    }

    @Test
    public void testGetLegislatureCourante() {
        Mockito
            .when(
                mockSTParametreService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(ReponsesParametreConstant.LEGISLATURE_COURANTE)
                )
            )
            .thenReturn("14");

        RechercheService rechercheService = new RechercheServiceImpl();

        assertEquals(14, rechercheService.getLegislatureCourante(session).longValue());
    }

    @Test(expected = NuxeoException.class)
    public void testGetLegislatureCouranteNull() {
        Mockito
            .when(
                mockSTParametreService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(ReponsesParametreConstant.LEGISLATURE_COURANTE)
                )
            )
            .thenReturn(null);

        RechercheService rechercheService = new RechercheServiceImpl();

        rechercheService.getLegislatureCourante(session);
    }

    @Test(expected = NuxeoException.class)
    public void testGetLegislatureCouranteNotLong() {
        Mockito
            .when(
                mockSTParametreService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(ReponsesParametreConstant.LEGISLATURE_COURANTE)
                )
            )
            .thenReturn("mauvais paramétrage");

        RechercheService rechercheService = new RechercheServiceImpl();

        rechercheService.getLegislatureCourante(session);
    }
}
