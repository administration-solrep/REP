package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.JetonService;
import fr.dila.reponses.core.service.JetonServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class JetonComponent extends ServiceEncapsulateComponent<JetonService, JetonServiceImpl> {

    public JetonComponent() {
        super(JetonService.class, new JetonServiceImpl());
    }
}
