package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReportingActionService;
import fr.dila.reponses.ui.services.actions.impl.ReportingActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReportingActionComponent
    extends ServiceEncapsulateComponent<ReportingActionService, ReportingActionServiceImpl> {

    public ReportingActionComponent() {
        super(ReportingActionService.class, new ReportingActionServiceImpl());
    }
}
