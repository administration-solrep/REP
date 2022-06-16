package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.BordereauUIService;
import fr.dila.reponses.ui.services.impl.BordereauUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class BordereauUIComponent extends ServiceEncapsulateComponent<BordereauUIService, BordereauUIServiceImpl> {

    /**
     * Default constructor
     */
    public BordereauUIComponent() {
        super(BordereauUIService.class, new BordereauUIServiceImpl());
    }
}
