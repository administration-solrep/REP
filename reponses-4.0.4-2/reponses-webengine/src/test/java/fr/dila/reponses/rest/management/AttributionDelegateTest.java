package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_SCHEMA;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_DATEDEBUT;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_DATEFIN;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_ID;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_LEGISLATURE_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_LEGISLATURE_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT;
import static fr.dila.reponses.api.constant.VocabularyConstants.LEGISLATURE;
import static fr.dila.reponses.rest.management.AbstractDelegate.RECUPERATION_INFO_KO;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_ATTRIBUTIONS;
import static fr.sword.xsd.reponses.AttributionType.REATTRIBUTION;
import static fr.sword.xsd.reponses.Civilite.M;
import static fr.sword.xsd.reponses.Civilite.MME;
import static fr.sword.xsd.reponses.Civilite.NON_RENSEIGNE;
import static fr.sword.xsd.reponses.EnFonction.ALL;
import static fr.sword.xsd.reponses.EnFonction.FALSE;
import static fr.sword.xsd.reponses.EnFonction.TRUE;
import static fr.sword.xsd.reponses.TraitementStatut.KO;
import static fr.sword.xsd.reponses.TraitementStatut.OK;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.MAY;
import static java.util.Calendar.OCTOBER;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.sword.xsd.reponses.Attribution;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;
import fr.sword.xsd.reponses.Civilite;
import fr.sword.xsd.reponses.Legislature;
import fr.sword.xsd.reponses.MembreGouvernement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class AttributionDelegateTest extends CommonTestDelegate {
    private static final String MONSIEUR = "Monsieur";
    private static final String MADAME = "Madame";

    private static final Map<String, Civilite> CIVILITES = ImmutableMap.of(MONSIEUR, M, MADAME, MME);

    private AttributionDelegate delegate;
    private Map<String, EntiteNode> membresGouvernement;

    @Mock
    private Session legislatureSession;

    @Mock
    private DocumentModel legislatureDoc1;

    @Mock
    private DocumentModel legislatureDoc2;

    @Mock
    @RuntimeService
    private DirectoryService directoryService;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        delegate = new AttributionDelegate(session);

        EntiteNode premierMinistre = new EntiteNodeImpl();
        premierMinistre.setId("1");
        premierMinistre.setOrdre(1L);
        premierMinistre.setDateDebut(new GregorianCalendar(2017, MAY, 15));
        premierMinistre.setFormule("Premier Ministre");
        premierMinistre.setMembreGouvernementCivilite(MONSIEUR);
        premierMinistre.setMembreGouvernementNom("Philippe");
        premierMinistre.setMembreGouvernementPrenom("Édouard");

        EntiteNode ministreTravail = new EntiteNodeImpl();
        ministreTravail.setId("2");
        ministreTravail.setOrdre(2L);
        ministreTravail.setDateDebut(new GregorianCalendar(2017, MAY, 17));
        ministreTravail.setFormule("Ministre du Travail");
        ministreTravail.setMembreGouvernementCivilite(MADAME);
        ministreTravail.setMembreGouvernementNom("Pénicaud");
        ministreTravail.setMembreGouvernementPrenom("Muriel");
        ministreTravail.setLabel("Ministère du Travail");

        EntiteNode ministreInterieur = new EntiteNodeImpl();
        ministreInterieur.setId("3");
        ministreInterieur.setOrdre(3L);
        ministreInterieur.setDateDebut(new GregorianCalendar(2017, MAY, 17));
        ministreInterieur.setDateFin(new GregorianCalendar(2018, OCTOBER, 3));
        ministreInterieur.setFormule("Ministre de l'Intérieur");
        ministreInterieur.setLabel("");
        ministreInterieur.setMembreGouvernementCivilite(MONSIEUR);
        ministreInterieur.setMembreGouvernementNom("Collomb");
        ministreInterieur.setMembreGouvernementPrenom("Gérard");
        ministreInterieur.setLabel("Ministère de l'Intérieur");
        ministreInterieur.setEdition("Titre Jo");

        membresGouvernement =
            ImmutableMap.of(
                premierMinistre.getId(),
                premierMinistre,
                ministreTravail.getId(),
                ministreTravail,
                ministreInterieur.getId(),
                ministreInterieur
            );

        when(ministeresService.getAllMinisteres()).thenReturn(new ArrayList<>(membresGouvernement.values()));
    }

    @Test
    public void chercherAttributions() {
        ChercherAttributionsRequest request = new ChercherAttributionsRequest();
        request.getIdQuestions().add(questionId);

        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_ATTRIBUTIONS));

        when(questionDoc.hasSchema(DOSSIER_SCHEMA)).thenReturn(false);
        when(questionDoc.hasSchema(QUESTION_DOCUMENT_SCHEMA)).thenReturn(true);

        ChercherAttributionsResponse response = delegate.chercherAttributions(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.isDernierRenvoi()).isTrue();
        assertThat(response.getJetonAttributions()).isEmpty();
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getAttributions().size()).isEqualTo(1);
        Attribution attribution = response.getAttributions().get(0);
        assertQuestionId(attribution.getIdQuestion(), questionId);
        assertThat(attribution.getType()).isEqualTo(REATTRIBUTION);
        assertThat(attribution.getAttributaire().getMinistre().getId())
            .isEqualTo(Integer.parseInt(ID_MINISTERE_ATTRIBUTAIRE));
        assertThat(attribution.getAttributaire().getMinistre().getIntituleMinistere()).isEqualTo(entiteNode.getLabel());
        assertThat(attribution.getAttributaire().getMinistre().getTitreJo()).isEqualTo(entiteNode.getEdition());

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_ATTRIBUTION_EVENT,
                WEBSERVICE_CHERCHER_ATTRIBUTION_COMMENT
            );
    }

    @Test
    public void chercherLegislatures() throws DatatypeConfigurationException {
        DocumentModelList legislatureModels = new DocumentModelListImpl(
            ImmutableList.of(legislatureDoc1, legislatureDoc2)
        );

        Legislature legislature1 = new Legislature();
        int idLegislature1 = 1;
        legislature1.setId(idLegislature1);
        GregorianCalendar dateDebutLegislature1 = new GregorianCalendar(2020, JANUARY, 1);
        legislature1.setDateDebut(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateDebutLegislature1));
        GregorianCalendar dateFinLegislature1 = new GregorianCalendar(2020, JULY, 1);
        legislature1.setDateFin(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateFinLegislature1));

        Legislature legislature2 = new Legislature();
        int idLegislature2 = 2;
        legislature2.setId(idLegislature2);
        GregorianCalendar dateDebutLegislature2 = new GregorianCalendar(2020, FEBRUARY, 1);
        legislature2.setDateDebut(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateDebutLegislature2));
        GregorianCalendar dateFinLegislature2 = new GregorianCalendar(2020, AUGUST, 1);
        legislature2.setDateFin(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateFinLegislature2));

        when(directoryService.open(LEGISLATURE)).thenReturn(legislatureSession);
        when(legislatureSession.query(anyMap())).thenReturn(legislatureModels);

        when(legislatureDoc1.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_ID))
            .thenReturn(String.valueOf(idLegislature1));
        when(legislatureDoc1.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_DATEDEBUT)).thenReturn(dateDebutLegislature1);
        when(legislatureDoc1.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_DATEFIN)).thenReturn(dateFinLegislature1);

        when(legislatureDoc2.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_ID))
            .thenReturn(String.valueOf(idLegislature2));
        when(legislatureDoc2.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_DATEDEBUT)).thenReturn(dateDebutLegislature2);
        when(legislatureDoc2.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_DATEFIN)).thenReturn(dateFinLegislature2);

        ChercherLegislaturesResponse response = delegate.chercherLegislatures();

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getLegislatures().size()).isEqualTo(2);
        assertLegislature(response.getLegislatures().get(0), legislature1);
        assertLegislature(response.getLegislatures().get(1), legislature2);

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_LEGISLATURE_EVENT,
                WEBSERVICE_CHERCHER_LEGISLATURE_COMMENT
            );
    }

    @Test
    public void chercherLegislaturesWithError() throws DatatypeConfigurationException {
        DocumentModelList legislatureModels = new DocumentModelListImpl(ImmutableList.of(legislatureDoc1));

        when(directoryService.open(LEGISLATURE)).thenReturn(legislatureSession);
        when(legislatureSession.query(anyMap())).thenReturn(legislatureModels);

        when(legislatureDoc1.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_ID)).thenReturn("a");

        ChercherLegislaturesResponse response = delegate.chercherLegislatures();

        assertThat(response.getStatut()).isEqualTo(KO);
        assertThat(response.getMessageErreur()).isEqualTo(RECUPERATION_INFO_KO);
        assertThat(response.getLegislatures().size()).isEqualTo(0);

        verify(journalService, never())
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_LEGISLATURE_EVENT,
                WEBSERVICE_CHERCHER_LEGISLATURE_COMMENT
            );
    }

    @Test
    public void chercherTousMembresGouvernement() {
        ChercherMembresGouvernementRequest request = new ChercherMembresGouvernementRequest();
        request.setEnFonction(ALL);

        when(ministeresService.getAllMinisteres()).thenReturn(new ArrayList<>(membresGouvernement.values()));

        ChercherMembresGouvernementResponse response = delegate.chercherMembresGouvernement(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getMembreGouvernement().size()).isEqualTo(3);
        response
            .getMembreGouvernement()
            .stream()
            .map(
                membreGouvernement ->
                    assertThat(membreGouvernement)
                        .satisfies(
                            mg -> {
                                EntiteNode expectedMinistre = membresGouvernement.get(
                                    String.valueOf(mg.getMinistre().getId())
                                );
                                assertMembreGouvernement(mg, expectedMinistre);
                                assertThat(mg.isEnFonction()).isEqualTo(expectedMinistre.isActive());
                            }
                        )
            );

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT
            );
    }

    @Test
    public void chercherMembresGouvernementEnFonction() {
        ChercherMembresGouvernementRequest request = new ChercherMembresGouvernementRequest();
        request.setEnFonction(TRUE);

        when(ministeresService.getMinisteres(true))
            .thenReturn(membresGouvernement.values().stream().filter(OrganigrammeNode::isActive).collect(toList()));

        ChercherMembresGouvernementResponse response = delegate.chercherMembresGouvernement(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getMembreGouvernement().size()).isEqualTo(2);
        response
            .getMembreGouvernement()
            .stream()
            .map(
                membreGouvernement ->
                    assertThat(membreGouvernement)
                        .satisfies(
                            mg -> {
                                EntiteNode expectedMinistre = membresGouvernement.get(
                                    String.valueOf(mg.getMinistre().getId())
                                );
                                assertMembreGouvernement(mg, expectedMinistre);
                                assertThat(mg.isEnFonction()).isTrue();
                            }
                        )
            );

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT
            );
    }

    @Test
    public void chercherMembresGouvernementPasEnFonction() {
        ChercherMembresGouvernementRequest request = new ChercherMembresGouvernementRequest();
        request.setEnFonction(FALSE);

        when(ministeresService.getMinisteres(false))
            .thenReturn(
                membresGouvernement
                    .values()
                    .stream()
                    .filter(membreGouvernement -> !membreGouvernement.isActive())
                    .collect(toList())
            );

        ChercherMembresGouvernementResponse response = delegate.chercherMembresGouvernement(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getMembreGouvernement().size()).isEqualTo(1);
        response
            .getMembreGouvernement()
            .stream()
            .map(
                membreGouvernement ->
                    assertThat(membreGouvernement)
                        .satisfies(
                            mg -> {
                                EntiteNode expectedMinistre = membresGouvernement.get(
                                    String.valueOf(mg.getMinistre().getId())
                                );
                                assertMembreGouvernement(mg, expectedMinistre);
                                assertThat(mg.isEnFonction()).isFalse();
                            }
                        )
            );

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT,
                WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT
            );
    }

    private static void assertMembreGouvernement(MembreGouvernement membreGouvernement, EntiteNode expectedMinistre) {
        assertThat(membreGouvernement.getCivilite())
            .isEqualTo(civiliteFromValue(expectedMinistre.getMembreGouvernementCivilite()));
        assertThat(membreGouvernement.getNom()).isEqualTo(expectedMinistre.getMembreGouvernementNom());
        assertThat(membreGouvernement.getPrenom()).isEqualTo(expectedMinistre.getMembreGouvernementPrenom());
        assertThat(membreGouvernement.getDateDebut()).isEqualTo(expectedMinistre.getDateDebut());
        assertThat(membreGouvernement.getDateFin()).isEqualTo(expectedMinistre.getDateFin());
        assertThat(membreGouvernement.getMinistre().getTitreMinistre()).isEqualTo(expectedMinistre.getFormule());
        assertThat(membreGouvernement.getMinistre().getIntituleMinistere()).isEqualTo(expectedMinistre.getLabel());
        assertThat(membreGouvernement.getMinistre().getTitreJo()).isEqualTo(expectedMinistre.getEdition());
        assertThat(membreGouvernement.getMinistre().getOrdreProto()).isEqualTo(expectedMinistre.getOrdre());
    }

    private static void assertLegislature(Legislature legislature, Legislature expectedLegislature) {
        assertThat(legislature.getId()).isEqualTo(expectedLegislature.getId());
        assertThat(legislature.getDateDebut()).isEqualTo(expectedLegislature.getDateDebut());
        assertThat(legislature.getDateFin()).isEqualTo(expectedLegislature.getDateFin());
    }

    private static Civilite civiliteFromValue(String value) {
        return CIVILITES.getOrDefault(value, NON_RENSEIGNE);
    }
}
