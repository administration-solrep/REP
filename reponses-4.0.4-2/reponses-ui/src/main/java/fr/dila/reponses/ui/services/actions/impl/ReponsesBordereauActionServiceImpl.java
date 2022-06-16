package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.HistoriqueAttributionDTO;
import fr.dila.reponses.ui.services.actions.ReponsesBordereauActionService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class ReponsesBordereauActionServiceImpl implements ReponsesBordereauActionService {

    @Override
    public Boolean getPartMiseAJour(Question question) {
        return (
            isNotEmptyCollection(question.getSignalements()) ||
            isNotEmptyCollection(question.getRenouvellements()) ||
            question.getDateRetraitQuestion() != null ||
            question.getDateTransmissionAssemblees() != null ||
            question.getDateCaducite() != null ||
            question.getDateRappelQuestion() != null
        );
    }

    @Override
    public Boolean getPartReponse(Reponse reponse) {
        return (reponse.getPageJOreponse() != null || reponse.getDateJOreponse() != null);
    }

    @Override
    public Boolean getPartIndexationAN(Question question) {
        return question.hasIndexationAn();
    }

    @Override
    public Boolean getPartIndexationSE(Question question) {
        return question.hasIndexationSenat();
    }

    @Override
    public Boolean getPartEditableIndexationComplementaire(SpecificContext context, String id) {
        return (
            STActionsServiceLocator.getDossierLockActionService().getCanUnlockCurrentDossier(context) &&
            getCanEditIndexationComplementaire(context, id)
        );
    }

    @Override
    public Boolean getPartIndexationComplementaireAN(Question question) {
        return question.hasIndexationComplementaireAn();
    }

    @Override
    public Boolean getPartIndexationComplementaireSE(Question question) {
        return question.hasIndexationComplementaireSenat();
    }

    @Override
    public Boolean getPartIndexationComplementaireMotCle(Question question) {
        return question.hasIndexationComplementaireMotCleMinistere();
    }

    @Override
    public Boolean getPartFeuilleRoute(Question question, Dossier dossier) {
        return question.isQuestionTypeEcrite() && dossier.hasFeuilleRoute();
    }

    @Override
    public Boolean getPartEditMinistereRattachement(SpecificContext context) {
        return (
            STActionsServiceLocator.getDossierLockActionService().getCanUnlockCurrentDossier(context) &&
            getCanEditMinistereRattachement((SSPrincipal) context.getSession().getPrincipal())
        );
    }

    @Override
    public Boolean getPartEditDirectionPilote(SpecificContext context) {
        return (
            STActionsServiceLocator.getDossierLockActionService().getCanUnlockCurrentDossier(context) &&
            getCanEditDirectionPilote((SSPrincipal) context.getSession().getPrincipal())
        );
    }

    private Boolean isNotEmptyCollection(Collection<?> liste) {
        return liste != null && !liste.isEmpty();
    }

    /**
     * Retourne vrai si l'utilisateur courant peut éditer l'indexation
     * complémentaire.
     *
     * @return Condition
     */
    private Boolean getCanEditIndexationComplementaire(SpecificContext context, String id) {
        SSPrincipal ssPrincipal = (SSPrincipal) context.getSession().getPrincipal();
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_UPDATER)) {
            return true;
        }

        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_UPDATER)) {
            return (
                (DossierLink) SSActionsServiceLocator.getSSCorbeilleActionService().getCurrentDossierLink(context) !=
                null
            );
        }

        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_MINISTERIEL_UPDATER)) {
            return isCurrentDossierInUserMinistere(context.getSession(), ssPrincipal, id);
        }
        return false;
    }

    /**
     * Retourne vrai si le ministère interpellé dossier en cours fait partie des
     * ministères de l'utilisateur.
     *
     * @return Condition
     */
    private boolean isCurrentDossierInUserMinistere(CoreSession session, SSPrincipal ssPrincipal, String id) {
        final DocumentModel doc = session.getDocument(new IdRef(id));
        if (doc == null) {
            return false;
        }

        final Dossier dossier = doc.getAdapter(Dossier.class);
        if (dossier == null) {
            return false;
        }
        return ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant());
    }

    /**
     * Retourne vrai si l'utilisateur courant peut éditer le minsitere
     * racttachement
     *
     * @return Condition
     */
    public Boolean getCanEditMinistereRattachement(SSPrincipal ssPrincipal) {
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.MINISTERE_RATTACHEMENT_UPDATER)) {
            return true;
        }
        return false;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut éditer direction pilote
     *
     * @return Condition
     */
    public Boolean getCanEditDirectionPilote(SSPrincipal ssPrincipal) {
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DIRECTION_PILOTE_UPDATER)) {
            return true;
        }
        return false;
    }

    /**
     * Retourne la liste des renouvellements de la question courante triés par ordre antéchronologique.
     *
     * @return
     */
    @Override
    public List<Renouvellement> getRenouvellements(Question question) {
        List<Renouvellement> out = question.getRenouvellements();
        Collections.sort(
            out,
            new Comparator<Renouvellement>() {

                @Override
                public int compare(Renouvellement o1, Renouvellement o2) {
                    if (o1.getDateEffet() == null) {
                        return 1;
                    }
                    if (o2.getDateEffet() == null) {
                        return -1;
                    }
                    return -o1.getDateEffet().compareTo(o2.getDateEffet());
                }
            }
        );
        return out;
    }

    @Override
    public List<HistoriqueAttributionDTO> getHistoriqueAttributionFeuilleRoute(CoreSession session, Dossier dossier) {
        List<HistoriqueAttributionDTO> result = new ArrayList<>();
        List<HistoriqueAttribution> historiquesAttribution = dossier.getHistoriqueAttribution(session);
        for (HistoriqueAttribution historique : historiquesAttribution) {
            String minAttribution = historique.getMinAttribution() != null
                ? STServiceLocator.getSTMinisteresService().getEntiteNode(historique.getMinAttribution()).getLabel()
                : null;
            Calendar dateAttribution = historique.getDateAttribution();
            String typeAttribution = historique.getTypeAttribution() != null
                ? ReponsesServiceLocator
                    .getVocabularyService()
                    .getEntryLabel(VocabularyConstants.FEUILLEROUTE_TYPE_CREATION, historique.getTypeAttribution())
                : null;
            result.add(new HistoriqueAttributionDTO(minAttribution, dateAttribution, typeAttribution));
        }
        return result;
    }
}
