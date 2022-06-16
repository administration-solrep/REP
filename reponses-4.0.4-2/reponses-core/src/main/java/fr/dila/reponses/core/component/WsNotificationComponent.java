package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.reponses.core.service.WsNotificationServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class WsNotificationComponent
    extends ServiceEncapsulateComponent<WsNotificationService, WsNotificationServiceImpl> {

    public WsNotificationComponent() {
        super(WsNotificationService.class, new WsNotificationServiceImpl());
    }
}
