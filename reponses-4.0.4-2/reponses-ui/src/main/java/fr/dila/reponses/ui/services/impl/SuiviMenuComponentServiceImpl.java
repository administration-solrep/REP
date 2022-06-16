package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.ui.bean.RequetePersoDTO;
import fr.dila.reponses.ui.enums.ReponsesActionCategory;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.SuiviMenuComponentService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.RequeteurActionService;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.automation.core.util.PageProviderHelper;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

public class SuiviMenuComponentServiceImpl extends FragmentServiceImpl implements SuiviMenuComponentService {
    public static final String LEFT_MENU_REQUETE_PERSONNELLE = "suiviMenuActionsRequetePersonnelle";
    public static final String LEFT_MENU_REQUETE_GENERALE = "suiviMenuActionsRequeteGenerale";
    public static final String LEFT_MENU_ALERTE = "suiviMenuActionsAlerte";
    private static final String MAIN_CONTENT_ID = "#main_content";
    private static final String PATH_REQUETE_GENERALE = "/suivi/requete?idRequete=%s" + MAIN_CONTENT_ID;
    private static final String PATH_ALERTES = "/suivi/alertes/%s" + MAIN_CONTENT_ID;
    public static final String REQUETES_PERSONNELLES_ACTIONS = "requetesPersonnellesActions";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put(LEFT_MENU_REQUETE_PERSONNELLE, getRequetesPersonelles(context));

        returnMap.put(LEFT_MENU_REQUETE_GENERALE, getRequetesGenerales(context));

        returnMap.put(LEFT_MENU_ALERTE, getAlertes(context));

        return returnMap;
    }

    List<Action> getRequetesGenerales(SpecificContext context) {
        RequeteurActionService requeteurActionService = ReponsesActionsServiceLocator.getRequeteurActionService();

        return convertPageProviderToActions(
            context,
            "requetesGeneralesPageProvider",
            new String[] { requeteurActionService.getBibliothequeStandard(context.getSession()).getId() },
            PATH_REQUETE_GENERALE
        );
    }

    List<RequetePersoDTO> getRequetesPersonelles(SpecificContext context) {
        UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();

        return convertPageProviderToRequetesPerso(
            context,
            "requetesPersonellesPageProvider",
            new String[] { userWorkspaceService.getCurrentUserPersonalWorkspace(context.getSession()).getId() },
            PATH_REQUETE_GENERALE
        );
    }

    private List<Action> getAlertes(SpecificContext context) {
        return convertPageProviderToActions(context, "suivi_alert_content", null, PATH_ALERTES);
    }

    private List<Action> convertPageProviderToActions(
        SpecificContext context,
        String providerName,
        String[] providerParameters,
        String path
    ) {
        PageProviderService providerService = ServiceUtil.getRequiredService(PageProviderService.class);
        PageProviderDefinition def = providerService.getPageProviderDefinition(providerName);

        CoreQueryDocumentPageProvider provider = (CoreQueryDocumentPageProvider) PageProviderHelper.getPageProvider(
            context.getSession(),
            def,
            Collections.emptyMap()
        );

        provider.setParameters(providerParameters);

        return provider.getCurrentPage().stream().map(doc -> toAction(context, path, doc)).collect(Collectors.toList());
    }

    private List<RequetePersoDTO> convertPageProviderToRequetesPerso(
        SpecificContext context,
        String providerName,
        String[] providerParameters,
        String path
    ) {
        PageProviderService providerService = ServiceUtil.getRequiredService(PageProviderService.class);
        PageProviderDefinition def = providerService.getPageProviderDefinition(providerName);

        CoreQueryDocumentPageProvider provider = (CoreQueryDocumentPageProvider) PageProviderHelper.getPageProvider(
            context.getSession(),
            def,
            Collections.emptyMap()
        );

        provider.setParameters(providerParameters);

        return provider
            .getCurrentPage()
            .stream()
            .map(doc -> toRequetePersoDTO(context, path, doc))
            .collect(Collectors.toList());
    }

    private Action toAction(SpecificContext context, String path, DocumentModel doc) {
        Action action = new Action();
        action.setLabel(doc.getTitle());
        action.setLink(String.format(path, doc.getId()));
        Map<String, Serializable> properties = new HashMap<>();
        properties.put("id", doc.getId());
        if (PATH_ALERTES.equals(path)) {
            Boolean isAlertActivated = isAlertActivated(doc);
            context.putInContextData(SSContextDataKey.IS_ALERT_ACTIVATED, isAlertActivated);
            properties.put("isActivated", isAlertActivated);
            properties.put("actions", (Serializable) context.getActions(ReponsesActionCategory.ALERTE_ACTIONS));
        }
        action.setProperties(properties);

        return action;
    }

    private Boolean isAlertActivated(DocumentModel doc) {
        Alert alert = doc.getAdapter(Alert.class);
        return alert == null || alert.isActivated();
    }

    private RequetePersoDTO toRequetePersoDTO(SpecificContext context, String path, DocumentModel doc) {
        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_ID, doc.getId());
        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_LABEL, doc.getTitle());
        return new RequetePersoDTO(
            doc.getId(),
            doc.getTitle(),
            String.format(path, doc.getId()),
            context.getActions(ReponsesActionCategory.REQUETE_PERSO_ACTIONS)
        );
    }
}
