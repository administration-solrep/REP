package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.impl.ReponsesModeleFdrFicheUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesModeleFdrFicheUIComponent
    extends ServiceEncapsulateComponent<ReponsesModeleFdrFicheUIService, ReponsesModeleFdrFicheUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesModeleFdrFicheUIComponent() {
        super(ReponsesModeleFdrFicheUIService.class, new ReponsesModeleFdrFicheUIServiceImpl());
    }
}
