package fr.dila.reponses.core.service.organigramme;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesChangementGouvernementComponent
    extends ServiceEncapsulateComponent<ReponsesChangementGouvernementService, ReponsesChangementGouvernementServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesChangementGouvernementComponent() {
        super(ReponsesChangementGouvernementService.class, new ReponsesChangementGouvernementServiceImpl());
    }
}
