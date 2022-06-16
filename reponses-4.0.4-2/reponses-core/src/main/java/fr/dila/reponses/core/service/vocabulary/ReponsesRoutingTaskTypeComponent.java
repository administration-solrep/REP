package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.ReponsesRoutingTaskTypeService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesRoutingTaskTypeComponent
    extends ServiceEncapsulateComponent<ReponsesRoutingTaskTypeService, ReponsesRoutingTaskTypeServiceImpl> {

    public ReponsesRoutingTaskTypeComponent() {
        super(ReponsesRoutingTaskTypeService.class, new ReponsesRoutingTaskTypeServiceImpl());
    }
}
