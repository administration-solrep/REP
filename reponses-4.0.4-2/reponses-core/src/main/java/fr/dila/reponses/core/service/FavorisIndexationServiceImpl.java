package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.ReponsesSchemaConstant.INDEXATION_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.ReponsesSchemaConstant.INDEXATION_ROOT_FOLDER;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.requireNonNull;

import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.reponses.api.service.FavorisIndexationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

public class FavorisIndexationServiceImpl implements FavorisIndexationService {

    @Override
    public DocumentRef getFavorisIndexationRootRef(CoreSession session) {
        DocumentModel uw = getRequiredService(UserWorkspaceService.class).getCurrentUserPersonalWorkspace(session);
        requireNonNull(
            uw,
            () -> format("L'utilisateur [%s] n'a pas de workspace", session.getPrincipal().getActingUser())
        );
        return new PathRef(join("/", uw.getPathAsString(), INDEXATION_ROOT_FOLDER));
    }

    @Override
    public void addFavoris(CoreSession session, String origine, String cle, String cleParent) {
        String favorisFolderPath = getFavorisIndexationRootRef(session).toString();
        final String favoriName = join("-", origine, cleParent, cle);
        if (session.exists(new PathRef(join("/", favorisFolderPath, favoriName)))) {
            return;
        }

        DocumentModel favorisDoc = session.createDocumentModel(favorisFolderPath, favoriName, INDEXATION_DOCUMENT_TYPE);
        FavorisIndexation favoris = favorisDoc.getAdapter(FavorisIndexation.class);
        favoris.setTypeIndexation(origine);
        favoris.setNiveau1(cleParent);
        favoris.setNiveau2(cle);
        session.createDocument(favorisDoc);
    }
}
