package fr.dila.reponses.ui.services.impl;

import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.AdminMenuServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public class ReponsesAdminMenuServiceImpl extends AdminMenuServiceImpl {

    public ReponsesAdminMenuServiceImpl() {
        super();
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        STUserManagerActionsDTO dto = new STUserManagerActionsDTO();
        dto.setIsCurrentUserPermanent(
            STUIServiceLocator.getSTUserManagerUIService().isCurrentUserPermanent(context.getSession().getPrincipal())
        );
        context.getContextData().put("reponsesUserManagerActions", dto);
        return super.getData(context);
    }
}
