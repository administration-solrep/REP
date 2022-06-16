package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService.EtatSignalement;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ ReponseFeature.class, SolonMockitoFeature.class })
public class ReponsesCorbeilleTreeServiceIT {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature repFeature;

    @Mock
    @RuntimeService
    private ReponsesCorbeilleService corbeilleService;

    @Mock
    @RuntimeService
    private STMinisteresService ministeresService;

    @Inject
    private ReponsesCorbeilleTreeService corbeilleTreeService;

    @Inject
    private MailboxPosteService mailboxPosteService;

    private SSPrincipal principal;

    @Before
    public void setUp() {
        principal = Mockito.mock(SSPrincipalImpl.class);
        when(principal.isAdministrator()).thenReturn(true);

        Set<String> posteIdSet = new HashSet<>();
        posteIdSet.add("p-1");
        posteIdSet.add("p-2");
        posteIdSet.add("p-3");
        when(principal.getPosteIdSet()).thenReturn(posteIdSet);

        List<EntiteNode> currentMinisteres = new ArrayList<>();
        currentMinisteres.add(buildEntite("2"));
        currentMinisteres.add(buildEntite("1"));
        currentMinisteres.add(buildEntite("4"));
        currentMinisteres.add(buildEntite("3"));
        List<EntiteNode> userMinisteres = new ArrayList<>();
        userMinisteres.add(buildEntite("1"));
        userMinisteres.add(buildEntite("2"));
        when(ministeresService.getCurrentMinisteres()).thenReturn(currentMinisteres);
        when(ministeresService.getMinistereParentFromPostes(Mockito.any())).thenReturn(userMinisteres);
        when(ministeresService.getEntiteNode(Mockito.anyString()))
            .thenAnswer(
                (Answer<EntiteNode>) invocation -> {
                    String entiteId = (String) invocation.getArguments()[0];
                    return buildEntite(entiteId.substring(4));
                }
            );

        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            ReponsesMailbox mailbox1 = mailboxPosteService
                .createPosteMailbox(session, "p-1", "poste1")
                .getDocument()
                .getAdapter(ReponsesMailbox.class);
            mailbox1.incrPreComptage("min-1", VocabularyConstants.ROUTING_TASK_TYPE_AVIS);
            mailbox1.incrPreComptage("min-5", VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION);
            session.saveDocument(mailbox1.getDocument());
            ReponsesMailbox mailbox2 = mailboxPosteService
                .createPosteMailbox(session, "p-2", "poste2")
                .getDocument()
                .getAdapter(ReponsesMailbox.class);
            mailbox2.incrPreComptage("min-1", VocabularyConstants.ROUTING_TASK_TYPE_REDACTION);
            mailbox2.incrPreComptage("min-1", VocabularyConstants.ROUTING_TASK_TYPE_AVIS);
            mailbox2.incrPreComptage("min-3", VocabularyConstants.ROUTING_TASK_TYPE_AVIS);
            session.saveDocument(mailbox2.getDocument());
        }

        List<DocumentModel> dossierLinkDocs = new ArrayList<>();
        dossierLinkDocs.add(buildDossierLinkDoc("dl-1", "min-1", true));
        dossierLinkDocs.add(buildDossierLinkDoc("dl-2", "min-1", false));
        dossierLinkDocs.add(buildDossierLinkDoc("dl-3", "min-3", true));
        dossierLinkDocs.add(buildDossierLinkDoc("dl-4", "min-5", false));
        when(corbeilleService.findDossierLinkFromPoste(Mockito.any(), Mockito.any())).thenReturn(dossierLinkDocs);

        when(corbeilleService.countDossierLinksSignalesForPostes(Mockito.any(), Mockito.any())).thenReturn(2);
        when(corbeilleService.countDossierLinksNonSignalesForPostes(Mockito.any(), Mockito.any())).thenReturn(2);

        HashMap<String, Integer> map = Maps.newHashMap(ImmutableMap.of("min-1", 1, "min-2", 0, "min-5", 1));
        when(corbeilleService.mapCountDossierLinksSignalesToMinisteres(Mockito.any(), Mockito.any())).thenReturn(map);

        map = Maps.newHashMap(ImmutableMap.of("min-1", 1, "min-2", 0, "min-3", 1));
        when(corbeilleService.mapCountDossierLinksNonSignalesToMinisteres(Mockito.any(), Mockito.any()))
            .thenReturn(map);
    }

    @Test
    public void testGetCorbeilleTreeNiveau1ParMinistere() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            when(principal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER)).thenReturn(true);
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau1(
                session,
                TypeRegroupement.PAR_MINISTERE,
                null,
                null
            );
            assertThat(tree).isEqualTo(ImmutableMap.of("min-1", 3, "min-2", 0, "min-3", 1, "min-4", 0, "min-5", 1));

            when(principal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER)).thenReturn(false);
            tree = corbeilleTreeService.getCorbeilleTreeNiveau1(session, TypeRegroupement.PAR_MINISTERE, null, null);
            assertThat(tree).isEqualTo(ImmutableMap.of("min-1", 3, "min-2", 0, "min-3", 1, "min-5", 1));
        }
    }

    @Test
    public void testGetCorbeilleTreeNiveau2ParMinistere() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau2(
                session,
                TypeRegroupement.PAR_MINISTERE,
                "min-1",
                null,
                null
            );
            assertThat(tree)
                .isEqualTo(
                    ImmutableMap.of(
                        VocabularyConstants.ROUTING_TASK_TYPE_AVIS,
                        2,
                        VocabularyConstants.ROUTING_TASK_TYPE_REDACTION,
                        1
                    )
                );
            tree =
                corbeilleTreeService.getCorbeilleTreeNiveau2(
                    session,
                    TypeRegroupement.PAR_MINISTERE,
                    "min-4",
                    null,
                    null
                );
            assertNotNull(tree);
            assertTrue(tree.isEmpty());
        }
    }

    @Test
    public void testGetCorbeilleTreeNiveau1ParPoste() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau1(
                session,
                TypeRegroupement.PAR_POSTE,
                null,
                null
            );
            assertThat(tree).isEqualTo(ImmutableMap.of("p-1", 2, "p-2", 3, "p-3", 0));
        }
    }

    @Test
    public void testGetCorbeilleTreeNiveau2ParPoste() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            List<EntiteNode> entites = new ArrayList<>();
            entites.add(buildEntite("1"));
            entites.add(buildEntite("2"));
            when(ministeresService.getMinistereParentFromPoste(Mockito.anyString())).thenReturn(entites);
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau2(
                session,
                TypeRegroupement.PAR_POSTE,
                "p-2",
                null,
                null
            );
            assertThat(tree).isEqualTo(ImmutableMap.of("min-1", 2, "min-2", 0, "min-3", 1));
        }
    }

    @Test
    public void testGetCorbeilleTreeNiveau1ParSignale() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau1(
                session,
                TypeRegroupement.PAR_SIGNALE,
                null,
                null
            );
            assertThat(tree)
                .isEqualTo(
                    ImmutableMap.of(
                        EtatSignalement.QUESTIONS_SIGNALEES.toString(),
                        2,
                        EtatSignalement.QUESTIONS_NON_SIGNALEES.toString(),
                        2
                    )
                );
        }
    }

    @Test
    public void testGetCorbeilleTreeNiveau2ParSignale() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            Map<String, Integer> tree = corbeilleTreeService.getCorbeilleTreeNiveau2(
                session,
                TypeRegroupement.PAR_SIGNALE,
                EtatSignalement.QUESTIONS_SIGNALEES.toString(),
                null,
                null
            );
            assertThat(tree).isEqualTo(ImmutableMap.of("min-1", 1, "min-2", 0, "min-5", 1));
            tree =
                corbeilleTreeService.getCorbeilleTreeNiveau2(
                    session,
                    TypeRegroupement.PAR_SIGNALE,
                    EtatSignalement.QUESTIONS_NON_SIGNALEES.toString(),
                    null,
                    null
                );
            assertThat(tree).isEqualTo(ImmutableMap.of("min-1", 1, "min-2", 0, "min-3", 1));
        }
    }

    private EntiteNode buildEntite(String id) {
        EntiteNode entite = new EntiteNodeImpl();

        entite.setId("min-" + id);
        entite.setOrdre(Long.parseLong(id));

        return entite;
    }

    private DocumentModel buildDossierLinkDoc(String id, String idMinistere, Boolean isSignale) {
        try (CloseableCoreSession session = coreFeature.openCoreSession(principal)) {
            DossierLink dossierLink = repFeature.createDossierLink(session, id);
            dossierLink.setIdMinistereAttributaire(idMinistere);
            dossierLink.setEtatsQuestion(isSignale ? "fff" : "ftf");
            dossierLink.save(session);
            return dossierLink.getDocument();
        }
    }
}
