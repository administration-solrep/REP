package fr.dila.reponses.api.service;

import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import java.util.Collection;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesOrganigrammeService extends SSOrganigrammeService {
    /**
     * Vérifie que le noeud est supprimable.
     *
     * @param coreSession
     * @param node
     * @return
     */
    Collection<OrganigrammeNodeDeletionProblem> validateDeleteNode(CoreSession coreSession, OrganigrammeNode node);
}
