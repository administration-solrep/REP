package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.CORBEILLE_DOSSIERS_ACTIONS_GENERAL;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.CORBEILLE_DOSSIERS_ACTIONS_NOTE;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;

import com.google.common.base.Strings;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.bean.actions.SSNavigationActionDTO;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractReponsesTravail extends SolonWebObject {
    public static final String PARAM_SELECTION_USER = "selectionUser";
    public static final String PARAM_SELECTION_POSTE = "selectionPoste";
    public static final String PARAM_MIN_ATTRIB = "minAttribId";
    protected static final String PARAM_ROUTING_TASK_TYPE = "routingTaskType";
    public static final String PARAM_POSTE_ID = "posteId";
    protected static final String PARAM_IS_SIGNALE = "isSignale";

    public AbstractReponsesTravail() {
        super();
    }

    protected final void fillContextData(
        String selectionPoste,
        String selectionUser,
        String minAttribId,
        DossierTravailListForm resultform
    ) {
        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);
        // Map du context global (service de chargement du layout)
        context.putInContextData(PARAM_SELECTION_POSTE, selectionPoste);
        context.putInContextData(PARAM_SELECTION_USER, selectionUser);
        context.putInContextData(PARAM_MIN_ATTRIB, minAttribId);
        context.putInContextData(STContextDataKey.FORM, resultform);
    }

    protected final Map<String, Object> getOtherParameterMap(
        String selectionPoste,
        String selectionUser,
        String minAttribId
    ) {
        Map<String, Object> otherParameter = new HashMap<>();
        otherParameter.put(PARAM_SELECTION_POSTE, selectionPoste);
        otherParameter.put(PARAM_SELECTION_USER, selectionUser);
        otherParameter.put(PARAM_MIN_ATTRIB, minAttribId);
        return otherParameter;
    }

    protected ThTemplate buildTemplateFromData(
        RepDossierList lstResults,
        DossierListForm resultForm,
        Map<String, Object> otherParameter,
        String origin
    ) {
        return buildTemplateFromData(lstResults, resultForm, otherParameter, origin, null);
    }

    protected ThTemplate buildTemplateFromData(
        RepDossierList lstResults,
        DossierListForm resultForm,
        Map<String, Object> otherParameter,
        String origin,
        String activeKey
    ) {
        ThTemplate template = getMyTemplate();
        template.setName(getFragment());
        template.setContext(context);

        resultForm = ObjectHelper.requireNonNullElseGet(resultForm, DossierTravailListForm::newForm);
        resultForm.setColumnVisibility(lstResults.getListeColonnes());

        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(ReponsesTemplateConstants.NB_DOSSIER, lstResults.getNbTotal());
        map.put(STTemplateConstants.TITRE, lstResults.getTitre());
        map.put(STTemplateConstants.SOUS_TITRE, lstResults.getSousTitre());

        map.put(STTemplateConstants.RESULT_FORM, resultForm);

        map.put(STTemplateConstants.DATA_URL, "/travail/" + Strings.emptyToNull(origin));
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/travail/" + Strings.emptyToNull(origin));
        map.put(STTemplateConstants.OTHER_PARAMETER, otherParameter);
        map.put("activeKey", activeKey);

        SSNavigationActionDTO navigationActionDTO = new SSNavigationActionDTO();
        navigationActionDTO.setIsFromEspaceTravail(
            SSActionsServiceLocator.getNavigationActionService().isFromEspaceTravail(context)
        );
        map.put(STTemplateConstants.IS_FROM_TRAVAIL, true);
        map.put(SSTemplateConstants.NAVIGATION_ACTIONS, navigationActionDTO);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(CORBEILLE_DOSSIERS_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.NOTE_ACTIONS, context.getActions(CORBEILLE_DOSSIERS_ACTIONS_NOTE));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));

        template.setData(map);

        template
            .getContext()
            .setNavigationContextTitle(
                new Breadcrumb(
                    lstResults.getTitre(),
                    "/travail/" + Strings.nullToEmpty(origin),
                    Breadcrumb.TITLE_ORDER,
                    template.getContext().getWebcontext().getRequest()
                )
            );

        return template;
    }

    protected abstract String getFragment();
}
