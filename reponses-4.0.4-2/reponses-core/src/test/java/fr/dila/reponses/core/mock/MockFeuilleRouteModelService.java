package fr.dila.reponses.core.mock;

import fr.dila.reponses.core.service.FeuilleRouteModelServiceImpl;

public class MockFeuilleRouteModelService extends FeuilleRouteModelServiceImpl {
    private static final long serialVersionUID = 1L;

    public void clear() {
        feuilleRouteModelFolderDocId = null;
    }

    public void setFeuilleRouteModelFolderDocId(String id) {
        feuilleRouteModelFolderDocId = id;
    }
}
