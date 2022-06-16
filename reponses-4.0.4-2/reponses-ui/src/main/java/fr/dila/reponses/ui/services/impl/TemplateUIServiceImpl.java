package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.ss.ui.services.impl.SSTemplateUIServiceImpl;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;

public class TemplateUIServiceImpl extends SSTemplateUIServiceImpl {

    @Override
    public ThTemplate getLeftMenuTemplate(SpecificContext context) {
        if (context == null) {
            return new ThTemplate();
        }

        if (context.getWebcontext().getPrincipal().isMemberOf(STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER)) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }
}
