package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;

import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.DossierSaveForm;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;

public class DossierActionServiceImpl implements DossierActionService {

    @Override
    public boolean isDossierContainsMinistere(SSPrincipal ssPrincipal, DocumentModel dossierDoc) {
        boolean result = false;
        if (
            ssPrincipal.isAdministrator() ||
            ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)
        ) {
            // L'administrateur fonctionnel a le droit de lecture et d'ecriture
            // sur tous les dossiers
            result = true;
        }

        if (!result && dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
            // recupere les poste autorise a ecrire sur ce dossier
            final Set<String> posteAutorise = new HashSet<>();
            final ACL acl = dossierDoc.getACP().getACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            for (final ACE ace : acl.getACEs()) {
                final String posteId = ace
                    .getUsername()
                    .replace(CaseManagementSecurityConstants.MAILBOX_PREFIX + SSConstant.MAILBOX_POSTE_ID_PREFIX, "");
                posteAutorise.add(posteId);
            }

            // teste les postes de l'utilisateur
            for (final String posteId : ssPrincipal.getPosteIdSet()) {
                if (posteAutorise.contains(posteId)) {
                    result = true;
                    break;
                }
            }

            // L'administrateur ministériel peut modifier le parapheur des
            // dossiers en cours de son ministère
            // recupere tous les poste des ministeres auquel il est rattaché
            // pour
            result = isDossierContainsMinAsAdminMininisteriel(ssPrincipal, result, posteAutorise);
        }

        return result;
    }

    /**
     * Check dans le cas où l'utilisateur est administrateur ministériel
     *
     * @param ssPrincipal
     * @param result
     * @param posteAutorise
     * @return
     */
    private boolean isDossierContainsMinAsAdminMininisteriel(
        SSPrincipal ssPrincipal,
        boolean result,
        final Set<String> posteAutorise
    ) {
        if (!result && ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)) {
            final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
            final STPostesService postesService = STServiceLocator.getSTPostesService();
            final Set<String> idsMinistere = ssPrincipal.getMinistereIdSet();
            for (final String idMinistere : idsMinistere) {
                final OrganigrammeNode node = ministeresService.getEntiteNode(idMinistere);
                result = checkPosteAutoriseInSubNodes(result, posteAutorise, postesService, node);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    private boolean checkPosteAutoriseInSubNodes(
        boolean result,
        final Set<String> posteAutorise,
        final STPostesService postesService,
        final OrganigrammeNode node
    ) {
        if (node != null) {
            final List<String> postes = postesService.getPosteIdInSubNode(node);
            for (final String posteId : postes) {
                if (posteAutorise.contains(posteId)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean canReadDossierConnexe(SSPrincipal ssPrincipal) {
        return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIERS_CONNEXES_READER);
    }

    @Override
    public boolean canReadAllotissement(SSPrincipal ssPrincipal) {
        return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_READER);
    }

    @Override
    public boolean canUpdateAllotissement(SSPrincipal ssPrincipal) {
        return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALLOTISSEMENT_UPDATER);
    }

    @Override
    public boolean isCurrentDossierInUserMinistere(SpecificContext context) {
        final DocumentModel doc = context.getCurrentDocument();
        if (doc == null) {
            return false;
        }

        final Dossier dossier = doc.getAdapter(Dossier.class);
        return ((SSPrincipal) context.getSession().getPrincipal()).getMinistereIdSet()
            .contains(dossier.getIdMinistereAttributaireCourant());
    }

    @Override
    public boolean isDossierArbitrated(SpecificContext context) {
        final DocumentModel dossierDoc = context.getCurrentDocument();
        return dossierDoc.getAdapter(Dossier.class).isArbitrated();
    }

    @Override
    public void saveIndexationComplementaire(SpecificContext context, DossierSaveForm newDossier) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();

        Question question = dossierDoc.getAdapter(Dossier.class).getQuestion(session);
        question.setIndexationComplSenatQuestionThemes(newDossier.getSeThemeComp());
        question.setIndexationComplSenatQuestionRubrique(newDossier.getSeRubriqueComp());
        question.setIndexationComplSenatQuestionRenvois(newDossier.getSeRenvoiComp());
        question.setIndexationComplAssNatRubrique(newDossier.getAnRubriqueComp());
        question.setIndexationComplAssNatTeteAnalyse(newDossier.getAnTAnalyseComp());
        question.setIndexationComplAssNatAnalyses(newDossier.getAnAnalyseComp());
        question.setIndexationComplMotsClefMinistere(newDossier.getMotsClesMinisteres());
        handleMinistereVocabulary(question.getNumeroQuestion(), newDossier.getMotsClesMinisteres());
        question.setIdMinistereRattachement(newDossier.getMinistereRattachement());
        question.setIdDirectionPilote(newDossier.getDirectionPilote());

        ReponsesServiceLocator.getDossierBordereauService().logAllDocumentUpdate(session, question.getDocument());

        session.saveDocument(question.getDocument());
    }

    @Override
    public boolean isUserMailboxInDossier(SpecificContext context, DocumentModel dossierDoc) {
        // L'utilisateur peut voir la reponse si il est destinataire de la
        // distribution
        if (SSActionsServiceLocator.getSSCorbeilleActionService().getCurrentDossierLink(context) != null) {
            return true;
        }

        final ACP acp = dossierDoc.getACP();
        final ACL[] acls = acp.getACLs();

        for (final ACL acl : acls) {
            final ACE[] aces = acl.getACEs();
            for (final ACE ace : aces) {
                SSPrincipal ssPrincipal = (SSPrincipal) context.getSession().getPrincipal();
                if (ace.getUsername().startsWith("mailbox_poste") && ssPrincipal.isMemberOf(ace.getUsername())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isFeuilleRouteRestartable(SpecificContext context) {
        NuxeoPrincipal principal = context.getWebcontext().getPrincipal();
        if (PermissionHelper.isAdminFonctionnel(principal) || PermissionHelper.isAdminMinisteriel(principal)) {
            DocumentRoutingService documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();
            CoreSession session = context.getSession();
            DocumentModel dossierDoc = context.getCurrentDocument();
            FeuilleRoute route = documentRoutingService.getDocumentRouteForDossier(session, dossierDoc.getId());

            if (route != null && route.isDone()) {
                ReponseFeuilleRouteService routeService = ReponsesServiceLocator.getFeuilleRouteService();
                List<DocumentModel> stepsDoc = routeService.getSteps(session, dossierDoc);

                if (hasRunningStep(stepsDoc)) {
                    return false;
                }
                FeuilleRouteElement lastStep = stepsDoc.get(stepsDoc.size() - 1).getAdapter(FeuilleRouteElement.class);
                return !lastStep.isDone();
            }
        }
        return false;
    }

    private static boolean hasRunningStep(List<DocumentModel> stepsDoc) {
        return stepsDoc
            .stream()
            .map(stepDoc -> stepDoc.getAdapter(FeuilleRouteElement.class))
            .anyMatch(FeuilleRouteElement::isRunning);
    }

    /**
     * Gère l'ajout et la suppression au sein d'un vocabulaire de mot-clés pour
     * un Ministère dans une question.
     *
     * @param numeroQuestion
     *            ID de la Question
     * @param motsClesMinisteres
     *            Liste des mot-clés envoyés lors de la sauvegarde
     */
    private static void handleMinistereVocabulary(Long numeroQuestion, List<String> motsClesMinisteres) {
        saveMinistereVocabulary(numeroQuestion, motsClesMinisteres);
        deleteMinistereVocabulary(numeroQuestion, motsClesMinisteres);
    }

    /**
     * Ajoute au vocabulaire "motclef-ministere" les mot-clés d'une question.
     *
     * @param numeroQuestion
     *            ID de la Question
     * @param motsClesMinisteres
     *            Liste des mot-clés envoyés lors de la sauvegarde
     */
    private static void saveMinistereVocabulary(Long numeroQuestion, List<String> motsClesMinisteres) {
        if (CollectionUtils.isNotEmpty(motsClesMinisteres)) {
            VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
            for (String keyword : motsClesMinisteres) {
                String idKeyword = numeroQuestion + "-" + keyword.toLowerCase();
                if (!vocabularyService.hasDirectoryEntry(MOTSCLEF_MINISTERE.getValue(), idKeyword)) {
                    DocumentModel newVocMinistere = vocabularyService.getNewEntry(MOTSCLEF_MINISTERE.getValue());
                    PropertyUtil.setProperty(
                        newVocMinistere,
                        STVocabularyConstants.XVOCABULARY,
                        STVocabularyConstants.COLUMN_ID,
                        idKeyword
                    );
                    PropertyUtil.setProperty(
                        newVocMinistere,
                        STVocabularyConstants.XVOCABULARY,
                        STVocabularyConstants.COLUMN_LABEL,
                        keyword
                    );
                    vocabularyService.createDirectoryEntryPrivileged(MOTSCLEF_MINISTERE.getValue(), newVocMinistere);
                }
            }
        }
    }

    /**
     * Supprimer du vocabulaire "motclef-ministere" les mot-clés d'une question.
     *
     * @param numeroQuestion
     *            ID de la Question
     * @param motsClesMinisteres
     *            Liste des mot-clés envoyés lors de la sauvegarde
     */
    private static void deleteMinistereVocabulary(Long numeroQuestion, List<String> motsClesMinisteres) {
        VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        vocabularyService
            .getAllEntry(MOTSCLEF_MINISTERE.getValue())
            .stream()
            .filter(
                doc ->
                    StringUtils.substringBefore(doc.getId(), "-").equals(numeroQuestion.toString()) &&
                    (
                        CollectionUtils.isEmpty(motsClesMinisteres) ||
                        !motsClesMinisteres.contains(
                            doc.getProperty(STVocabularyConstants.XVOCABULARY, STVocabularyConstants.COLUMN_LABEL)
                        )
                    )
            )
            .forEach(doc -> vocabularyService.deleteDirectoryEntryPrivileged(MOTSCLEF_MINISTERE.getValue(), doc));
    }
}
