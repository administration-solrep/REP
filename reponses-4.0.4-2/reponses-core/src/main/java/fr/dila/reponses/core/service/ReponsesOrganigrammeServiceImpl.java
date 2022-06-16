package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.api.service.ReponsesOrganigrammeService;
import fr.dila.ss.core.service.SSAbstractOrganigrammeService;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemType;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ReponsesOrganigrammeServiceImpl
    extends SSAbstractOrganigrammeService
    implements ReponsesOrganigrammeService {
    private static final long serialVersionUID = 1L;

    @Override
    protected String getDossierIdentification(String dossierId, CoreSession session) {
        return session
            .getDocument(new IdRef(dossierId))
            .getAdapter(Dossier.class)
            .getQuestion(session)
            .getSourceNumeroQuestion();
    }

    @Override
    protected String getFeuilleRouteIdentification(FeuilleRoute fdr, CoreSession session) {
        return fdr.getName();
    }

    /**
     * Vérifie qu'aucun dossier n'est rattaché à cette direction.
     */
    @Override
    protected Collection<OrganigrammeNodeDeletionProblem> validateDeleteDirectionDossiers(
        UniteStructurelleNode node,
        CoreSession coreSession
    ) {
        Collection<OrganigrammeNodeDeletionProblem> problems = new ArrayList<>();

        DossierService dossierService = ReponsesServiceLocator.getDossierService();
        List<DocumentModel> listeDocumentAttacheEntite = dossierService.getDossierRattacheToDirection(
            coreSession,
            node.getId()
        );

        if (CollectionUtils.isNotEmpty(listeDocumentAttacheEntite)) {
            for (DocumentModel docModel : listeDocumentAttacheEntite) {
                OrganigrammeNodeDeletionProblem problem = new OrganigrammeNodeDeletionProblem(
                    ProblemType.DOSSIER_ATTACHED_TO_DIRECTION,
                    node.getLabel()
                );

                problem.setBlockingObjIdentification(
                    getDossierIdentification(
                        docModel.getAdapter(Question.class).getDossier(coreSession).getDocument().getId(),
                        coreSession
                    )
                );

                problems.add(problem);
            }
        }

        return problems;
    }

    @Override
    public void migrateNode(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode destinationNode) {
        boolean canMigrateNode = nodeToCopy != null && !nodeToCopy.getId().equals(destinationNode.getId());

        if (canMigrateNode) {
            if (!isMigrateAllowed(nodeToCopy, destinationNode)) {
                throw new NuxeoException(ResourceHelper.getString("error.organigramme.service.migrate.not.allowed"));
            }

            migrateNodeChildrenToDestinationNode(nodeToCopy, destinationNode);
        }
    }
}
