package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.ReponsesAdminMenuServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.AdminMenuService;

public class ReponsesAdminMenuComponent
    extends ServiceEncapsulateComponent<AdminMenuService, ReponsesAdminMenuServiceImpl> {

    public ReponsesAdminMenuComponent() {
        super(AdminMenuService.class, new ReponsesAdminMenuServiceImpl());
    }
}
