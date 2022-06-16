package fr.dila.reponses.ui.services.actions.organigramme;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesOrganigrammeInjectionActionComponent
    extends ServiceEncapsulateComponent<ReponsesOrganigrammeInjectionActionService, ReponsesOrganigrammeInjectionActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesOrganigrammeInjectionActionComponent() {
        super(ReponsesOrganigrammeInjectionActionService.class, new ReponsesOrganigrammeInjectionActionServiceImpl());
    }
}
