package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReponsesDossierDistributionActionService;
import fr.dila.reponses.ui.services.actions.impl.ReponsesDossierDistributionActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesDossierDistributionActionComponent
    extends ServiceEncapsulateComponent<ReponsesDossierDistributionActionService, ReponsesDossierDistributionActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesDossierDistributionActionComponent() {
        super(ReponsesDossierDistributionActionService.class, new ReponsesDossierDistributionActionServiceImpl());
    }
}
