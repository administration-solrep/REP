package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RAPPELE;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.core.feature.SolonMockitoFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class EtatRappeleTraitementTest {
    private EtatRappeleTraitement traitement;

    @Mock
    private Requete requete;

    @Before
    public void setUp() {
        traitement = new EtatRappeleTraitement();
    }

    @Test
    public void doBeforeQueryWithEtatRappeleIsFalse() {
        when(requete.getEtat(ETAT_RAPPELE)).thenReturn(false);

        traitement.doBeforeQuery(requete);

        verify(requete).setEtat(ETAT_RAPPELE, null);
    }

    @Test
    public void doBeforeQueryWithEtatRappeleIsTrue() {
        when(requete.getEtat(ETAT_RAPPELE)).thenReturn(true);

        traitement.doBeforeQuery(requete);

        verify(requete, never()).setEtat(eq(ETAT_RAPPELE), anyBoolean());
    }
}
