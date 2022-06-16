package fr.dila.reponses.core.recherche.traitement;

import static org.mockito.Mockito.verify;

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
public class GeneralRequeteTraitementTest {
    private GeneralRequeteTraitement generalRequeteTraitement;

    @Mock
    private Requete requete;

    @Mock
    private TexteIntegralTraitement texteIntegralTraitement;

    @Mock
    private NumeroQuestionTraitement numeroQuestionTraitement;

    @Mock
    private ProtectSingleQuoteOnIndexationTraitement protectSingleQuoteOnIndexationTraitement;

    @Mock
    private EtatQuestionTraitement etatQuestionTraitement;

    @Mock
    private ReponseEtapeValidationStatutTraitement reponseEtapeValidationStatutTraitement;

    @Before
    public void setUp() {
        generalRequeteTraitement = new GeneralRequeteTraitement();

        generalRequeteTraitement.getTraitements().clear();

        generalRequeteTraitement.getTraitements().add(texteIntegralTraitement);
        generalRequeteTraitement.getTraitements().add(numeroQuestionTraitement);
        generalRequeteTraitement.getTraitements().add(protectSingleQuoteOnIndexationTraitement);
        generalRequeteTraitement.getTraitements().add(etatQuestionTraitement);
        generalRequeteTraitement.getTraitements().add(reponseEtapeValidationStatutTraitement);
    }

    @Test
    public void init() {
        generalRequeteTraitement.init(requete);

        verify(texteIntegralTraitement).init(requete);
        verify(numeroQuestionTraitement).init(requete);
        verify(protectSingleQuoteOnIndexationTraitement).init(requete);
        verify(etatQuestionTraitement).init(requete);
        verify(reponseEtapeValidationStatutTraitement).init(requete);
    }

    @Test
    public void doBeforeQuery() {
        generalRequeteTraitement.doBeforeQuery(requete);

        verify(texteIntegralTraitement).doBeforeQuery(requete);
        verify(numeroQuestionTraitement).doBeforeQuery(requete);
        verify(protectSingleQuoteOnIndexationTraitement).doBeforeQuery(requete);
        verify(etatQuestionTraitement).doBeforeQuery(requete);
        verify(reponseEtapeValidationStatutTraitement).doBeforeQuery(requete);
    }
}
