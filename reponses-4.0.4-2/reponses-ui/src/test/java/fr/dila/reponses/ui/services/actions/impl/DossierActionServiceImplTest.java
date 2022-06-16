package fr.dila.reponses.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DossierActionServiceImplTest {
    DossierActionServiceImpl service = new DossierActionServiceImpl();

    @Mock
    SSPrincipal principal;

    @Before
    public void before() {
        principal = Mockito.mock(SSPrincipal.class);
    }

    @Test
    public void testCanReadAllotissement() {
        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER)).thenReturn(true);
        assertEquals(true, service.canReadAllotissement(principal));

        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER)).thenReturn(false);
        assertEquals(false, service.canReadAllotissement(principal));
    }

    @Test
    public void testCanReadDossierConnexe() {
        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER)).thenReturn(true);
        assertEquals(true, service.canReadDossierConnexe(principal));

        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER)).thenReturn(false);
        assertEquals(false, service.canReadDossierConnexe(principal));
    }
}
