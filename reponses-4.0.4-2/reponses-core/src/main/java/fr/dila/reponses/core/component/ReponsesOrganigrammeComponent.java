package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesOrganigrammeService;
import fr.dila.reponses.core.service.ReponsesOrganigrammeServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesOrganigrammeComponent
    extends ServiceEncapsulateComponent<ReponsesOrganigrammeService, ReponsesOrganigrammeServiceImpl> {

    public ReponsesOrganigrammeComponent() {
        super(ReponsesOrganigrammeService.class, new ReponsesOrganigrammeServiceImpl());
    }
}
