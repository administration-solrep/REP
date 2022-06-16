package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesLoginManager;
import fr.dila.reponses.core.service.ReponsesLoginManagerImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesLoginComponent
    extends ServiceEncapsulateComponent<ReponsesLoginManager, ReponsesLoginManagerImpl> {

    public ReponsesLoginComponent() {
        super(ReponsesLoginManager.class, new ReponsesLoginManagerImpl());
    }
}
