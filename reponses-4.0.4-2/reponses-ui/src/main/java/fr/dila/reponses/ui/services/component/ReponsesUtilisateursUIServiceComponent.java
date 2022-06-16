package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesUtilisateursUIService;
import fr.dila.reponses.ui.services.impl.ReponsesUtilisateursUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesUtilisateursUIServiceComponent
    extends ServiceEncapsulateComponent<ReponsesUtilisateursUIService, ReponsesUtilisateursUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesUtilisateursUIServiceComponent() {
        super(ReponsesUtilisateursUIService.class, new ReponsesUtilisateursUIServiceImpl());
    }
}
