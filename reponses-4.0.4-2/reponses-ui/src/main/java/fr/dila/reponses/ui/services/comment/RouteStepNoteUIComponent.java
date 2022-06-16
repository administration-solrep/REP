package fr.dila.reponses.ui.services.comment;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RouteStepNoteUIComponent
    extends ServiceEncapsulateComponent<RouteStepNoteUIService, RouteStepNoteUIServiceImpl> {

    /**
     * Default constructor
     */
    public RouteStepNoteUIComponent() {
        super(RouteStepNoteUIService.class, new RouteStepNoteUIServiceImpl());
    }
}
