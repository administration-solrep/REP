package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_MIN_ATTRIB;
import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_POSTE_ID;
import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_SELECTION_POSTE;
import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_SELECTION_USER;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.core.recherche.ReponsesMinimalEscaper;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.contentview.RepCorbeillePageProvider;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.helper.RepDossierListHelper;
import fr.dila.reponses.ui.helper.RepDossierListProviderHelper;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;

public class DossierListUIServiceImpl implements DossierListUIService {
    private static final String LABEL_MINISTERE_INCONNU = "label.ministere.inconnu";
    protected static final String MISSING_PARAM_ERROR =
        "Un ou plusieurs paramètre manquants pour l'affichage de la liste des dossiers";
    private static final STLogger LOG = STLogFactory.getLog(DossierListUIServiceImpl.class);

    private RepDossierList getDossiersFromCorbeille(
        String pageProviderName,
        SpecificContext context,
        List<Object> params,
        DossierTravailListForm form,
        String titre,
        String sousTitre
    ) {
        form = ObjectHelper.requireNonNullElseGet(form, DossierTravailListForm::newForm);

        RepCorbeillePageProvider provider = buildCorbeilleProvider(
            pageProviderName,
            params,
            form,
            context.getSession()
        );

        List<Map<String, Serializable>> docList2 = provider.getCurrentPage();

        LOG.debug(STLogEnumImpl.GET_DOSSIER_TEC, provider.getResultsCount() + " questions trouvées pour ces critères");

        return RepDossierListHelper.buildDossierList(
            docList2,
            titre,
            sousTitre,
            form,
            provider.getLstUserVisibleColumns(),
            (int) provider.getResultsCount(),
            true
        );
    }

    @Override
    public RepDossierList getDossiersFromMinCorbeille(SpecificContext context) throws MissingArgumentException {
        String idMin = (String) context.getFromContextData(PARAM_MIN_ATTRIB);
        String routingTask = (String) context.getFromContextData(ReponsesContextDataKey.ROUTING_TASK_TYPE);

        // si un seul de nos argument est vide on ne retourne rien
        if (StringUtils.isAnyBlank(idMin, routingTask)) {
            throw new MissingArgumentException(MISSING_PARAM_ERROR);
        }

        String selectionPoste = (String) context.getContextData().get(PARAM_SELECTION_POSTE);
        String selectionUser = (String) context.getContextData().get(PARAM_SELECTION_USER);
        String mailboxParam = ReponsesServiceLocator
            .getMailboxService()
            .getMailboxListQuery(context.getSession(), selectionUser, selectionPoste);

        OrganigrammeNode node = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(idMin, OrganigrammeType.MINISTERE);
        String routingTaskLabel = ReponsesServiceLocator
            .getVocabularyService()
            .getEntryLabel(STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY, routingTask);

        if (Strings.isNullOrEmpty(routingTaskLabel)) {
            routingTaskLabel = "label.etape.inconnue";
        }

        String titre;
        if (node == null) {
            titre = LABEL_MINISTERE_INCONNU;
        } else {
            titre = node.getLabel();
        }
        DossierTravailListForm form = (DossierTravailListForm) context.getContextData().get("form");

        return getDossiersFromCorbeille(
            "corbeilleMinPageProvider",
            context,
            Lists.newArrayList(idMin, routingTask, mailboxParam),
            form,
            titre,
            routingTaskLabel
        );
    }

    protected RepCorbeillePageProvider buildCorbeilleProvider(
        String pageProviderName,
        List<Object> lstParams,
        DossierListForm form,
        CoreSession session
    ) {
        ProfilUtilisateurService profilService = ReponsesServiceLocator.getProfilUtilisateurService();

        RepCorbeillePageProvider pageProvider = form.getPageProvider(session, pageProviderName, "dl.", lstParams);
        pageProvider.setLstUserVisibleColumns(profilService.getUserColumn(session));

        return pageProvider;
    }

    @Override
    public RepDossierList getDossiersFromPlanClassement(SpecificContext context) throws MissingArgumentException {
        String origine = (String) context.getContextData().get("origine");
        String cle = (String) context.getContextData().get("cle");
        String cleParent = (String) context.getContextData().get("cleParent");
        DossierListForm form = (DossierListForm) context.getContextData().get("form");

        form = ObjectHelper.requireNonNullElseGet(form, DossierListForm::newForm);

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        // si un seul de nos argument est vide on ne retourne rien
        if (StringUtils.isAnyBlank(origine, cle, cleParent)) {
            throw new MissingArgumentException(MISSING_PARAM_ERROR);
        }

        List<Object> lstParams = new ArrayList<>();
        ReponsesMinimalEscaper escaper = new ReponsesMinimalEscaper();
        if (DossierConstants.ORIGINE_QUESTION_AN.equals(origine)) {
            lstParams.add(escaper.escape(cleParent));
            lstParams.add(escaper.escape(cleParent));
            if (!ResourceHelper.getString("plan.classement.tous.label").equals(cle)) {
                lstParams.add(escaper.escape(cle));
                lstParams.add(escaper.escape(cle));
            }
        } else if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(origine)) {
            lstParams.add(escaper.escape(cle));
            lstParams.add(escaper.escape(cle));
        }

        RepDossierList lstResult = new RepDossierList();

        try {
            PlanClassementService planClasService = ReponsesServiceLocator.getPlanClassementService();

            String query = planClasService.getDossierFromIndexationQuery(origine, cleParent, cle);

            RechercheResultPageProvider provider = buildRechercheProvider(query, lstParams, form, context.getSession());

            List<Map<String, Serializable>> docList2 = provider.getCurrentPage();

            LOG.debug(
                STLogEnumImpl.GET_DOSSIER_TEC,
                provider.getResultsCount() + " questions trouvées pour ces critères"
            );
            lstResult =
                RepDossierListHelper.buildDossierList(
                    docList2,
                    "",
                    "",
                    form,
                    provider.getLstUserVisibleColumns(),
                    (int) provider.getResultsCount(),
                    false
                );
        } catch (Exception e) {
            LOG.error(
                context.getSession(),
                STLogEnumImpl.FAIL_GET_DOSSIER_TEC,
                "Une erreur est survenue lors de la récupération des questions",
                e
            );
            throw e;
        }

        return lstResult;
    }

    protected RechercheResultPageProvider buildRechercheProvider(
        String query,
        List<Object> lstParams,
        DossierListForm form,
        CoreSession session
    ) {
        RechercheResultPageProvider provider = RepDossierListProviderHelper.initProvider(
            form,
            "dossierPageProvider",
            session,
            lstParams,
            ""
        );
        provider.getDefinition().setPattern(query);

        return provider;
    }

    @Override
    public RepDossierList getDossiersFromPosteCorbeille(SpecificContext context) throws MissingArgumentException {
        String idMin = (String) context.getFromContextData(PARAM_MIN_ATTRIB);
        String posteId = (String) context.getFromContextData(PARAM_POSTE_ID);

        // si un seul de nos argument est vide on ne retourne rien
        if (StringUtils.isAnyBlank(idMin, posteId)) {
            throw new MissingArgumentException(MISSING_PARAM_ERROR);
        }

        Mailbox mailbox = SSServiceLocator.getMailboxPosteService().getMailboxPoste(context.getSession(), posteId);

        String selectionPoste = (String) context.getContextData().get(PARAM_SELECTION_POSTE);
        String selectionUser = (String) context.getContextData().get(PARAM_SELECTION_USER);
        String mailboxParam = ReponsesServiceLocator
            .getMailboxService()
            .getMailboxListQuery(context.getSession(), selectionUser, selectionPoste);

        OrganigrammeNode node = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(idMin, OrganigrammeType.MINISTERE);
        String sousTitre;
        String mailboxId = "";
        if (mailbox == null) {
            sousTitre = "label.poste.inconnu";
        } else {
            sousTitre = mailbox.getTitle();
            mailboxId = mailbox.getDocument().getId();
        }

        String titre;
        if (node == null) {
            titre = LABEL_MINISTERE_INCONNU;
        } else {
            titre = node.getLabel();
        }
        DossierTravailListForm form = (DossierTravailListForm) context.getContextData().get("form");

        return getDossiersFromCorbeille(
            "corbeillePostePageProvider",
            context,
            Lists.newArrayList(idMin, mailboxId, mailboxParam),
            form,
            titre,
            sousTitre
        );
    }

    @Override
    public RepDossierList getDossiersFromSignaleCorbeille(SpecificContext context) throws MissingArgumentException {
        String idMin = context.getFromContextData(PARAM_MIN_ATTRIB);

        // si un seul de nos argument est vide on ne retourne rien
        if (StringUtils.isBlank(idMin) || context.getFromContextData("isSignale") == null) {
            throw new MissingArgumentException(MISSING_PARAM_ERROR);
        }

        Boolean isSignale = context.getFromContextData("isSignale");
        String signalEtat;

        if (isTrue(isSignale)) {
            signalEtat = "_t_";
        } else {
            isSignale = false;
            signalEtat = "_f_";
        }

        String selectionPoste = context.getFromContextData(PARAM_SELECTION_POSTE);
        String selectionUser = context.getFromContextData(PARAM_SELECTION_USER);
        String mailboxParam = ReponsesServiceLocator
            .getMailboxService()
            .getMailboxListQuery(context.getSession(), selectionUser, selectionPoste);

        OrganigrammeNode node = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrammeNodeById(idMin, OrganigrammeType.MINISTERE);
        String sousTitre;
        if (isTrue(isSignale)) {
            sousTitre = "label.dossier.isSignale";
        } else {
            sousTitre = "label.dossier.notSignale";
        }

        String titre;
        if (node == null) {
            titre = LABEL_MINISTERE_INCONNU;
        } else {
            titre = node.getLabel();
        }
        DossierTravailListForm form = context.getFromContextData("form");

        return getDossiersFromCorbeille(
            "corbeilleSignalPageProvider",
            context,
            Lists.newArrayList(idMin, signalEtat, mailboxParam),
            form,
            titre,
            sousTitre
        );
    }
}
