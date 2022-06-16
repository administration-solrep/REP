package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.TemplateUIServiceImpl;
import fr.dila.ss.ui.services.SSTemplateUIService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class TemplateUIComponent extends ServiceEncapsulateComponent<SSTemplateUIService, TemplateUIServiceImpl> {

    /**
     * Default constructor
     */
    public TemplateUIComponent() {
        super(SSTemplateUIService.class, new TemplateUIServiceImpl());
    }
}
