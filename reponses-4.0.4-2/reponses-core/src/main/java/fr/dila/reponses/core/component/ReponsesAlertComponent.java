package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.core.service.ReponsesAlertServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesAlertComponent
    extends ServiceEncapsulateComponent<ReponsesAlertService, ReponsesAlertServiceImpl> {

    public ReponsesAlertComponent() {
        super(ReponsesAlertService.class, new ReponsesAlertServiceImpl());
    }
}
