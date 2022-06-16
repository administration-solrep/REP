package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.core.service.StatsServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class StatsComponent extends ServiceEncapsulateComponent<StatsService, StatsServiceImpl> {

    public StatsComponent() {
        super(StatsService.class, new StatsServiceImpl());
    }
}
