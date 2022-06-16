package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesDerniersElementsComponentService;
import fr.dila.reponses.ui.services.impl.ReponsesDerniersElementsComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesDerniersElementsComponent
    extends ServiceEncapsulateComponent<ReponsesDerniersElementsComponentService, ReponsesDerniersElementsComponentServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesDerniersElementsComponent() {
        super(ReponsesDerniersElementsComponentService.class, new ReponsesDerniersElementsComponentServiceImpl());
    }
}
