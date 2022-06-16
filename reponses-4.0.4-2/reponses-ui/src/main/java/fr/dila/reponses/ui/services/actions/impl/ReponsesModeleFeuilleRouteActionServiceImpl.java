package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponsesModeleFeuilleRouteActionService;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.ss.ui.services.actions.impl.ModeleFeuilleRouteActionServiceImpl;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponsesModeleFeuilleRouteActionServiceImpl
    extends ModeleFeuilleRouteActionServiceImpl
    implements ReponsesModeleFeuilleRouteActionService {

    @Override
    public DocumentModel initFeuilleRoute(CoreSession session, ModeleFdrForm form, String creatorName) {
        DocumentModel route = super.initFeuilleRoute(session, form, creatorName);
        FeuilleRouteStepFolderSchemaUtil.setExecution(route, FeuilleRouteExecutionType.serial);
        return route;
    }

    @Override
    public boolean canUserModifyRoute(SpecificContext context) {
        DocumentModel doc = context.getCurrentDocument();
        ReponsesFeuilleRoute route = doc.getAdapter(ReponsesFeuilleRoute.class);
        ReponsesModeleFdrForm form = context.getFromContextData(ReponsesContextDataKey.MODELE_FDR_FORM);
        if (!StringUtils.equals(route.getMinistere(), form.getIdMinistere())) {
            return PermissionHelper.isAdminFonctionnel(context.getSession().getPrincipal());
        }

        return super.canUserModifyRoute(context);
    }
}
