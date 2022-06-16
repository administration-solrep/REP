package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.reponses.ui.services.impl.StatistiquesUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class StatistiquesUIComponent
    extends ServiceEncapsulateComponent<StatistiquesUIService, StatistiquesUIServiceImpl> {

    /**
     * Default constructor
     */
    public StatistiquesUIComponent() {
        super(StatistiquesUIService.class, new StatistiquesUIServiceImpl());
    }
}
