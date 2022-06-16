package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.UpdateTimbreServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class UpdateTimbreComponent extends ServiceEncapsulateComponent<UpdateTimbreService, UpdateTimbreServiceImpl> {

    public UpdateTimbreComponent() {
        super(UpdateTimbreService.class, new UpdateTimbreServiceImpl());
    }
}
