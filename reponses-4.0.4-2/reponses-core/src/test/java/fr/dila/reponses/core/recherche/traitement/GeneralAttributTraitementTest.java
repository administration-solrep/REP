package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_SIGNALE;
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
public class GeneralAttributTraitementTest {
    private GeneralAttributTraitement traitement;

    @Mock
    private Requete requete;

    @Before
    public void setUp() {
        traitement = new GeneralAttributTraitement(ETAT_SIGNALE);
    }

    @Test
    public void doBeforeQueryWithEtatSignaleIsFalse() {
        when(requete.getEtat(ETAT_SIGNALE)).thenReturn(false);

        traitement.doBeforeQuery(requete);

        verify(requete).setEtat(ETAT_SIGNALE, null);
    }

    @Test
    public void doBeforeQueryWithEtatSignaleIsTrue() {
        when(requete.getEtat(ETAT_SIGNALE)).thenReturn(true);

        traitement.doBeforeQuery(requete);

        verify(requete, never()).setEtat(eq(ETAT_SIGNALE), anyBoolean());
    }
}
