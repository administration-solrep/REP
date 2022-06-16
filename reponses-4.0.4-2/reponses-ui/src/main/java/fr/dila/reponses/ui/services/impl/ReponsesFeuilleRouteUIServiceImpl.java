package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.ui.enums.RepEtapeValidationStatusEnum;
import fr.dila.reponses.ui.services.ReponsesFeuilleRouteUIService;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.EtapeValidationStatus;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.impl.SSFeuilleRouteUIServiceImpl;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class ReponsesFeuilleRouteUIServiceImpl
    extends SSFeuilleRouteUIServiceImpl
    implements ReponsesFeuilleRouteUIService {

    @Override
    public void addFirstEtapesModele(SpecificContext context) {
        CoreSession session = context.getSession();
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(creationEtapeDTO.getIdBranche()));

        if (!canAddFirstEtapesModele(context, feuilleRouteDoc)) {
            throw new STAuthorizationException("L'accès à l'ajout d'un bloc d'étapes n'est pas autorisé");
        }
        saveEtapes(context, feuilleRouteDoc);
    }

    protected boolean canAddFirstEtapesModele(SpecificContext context, DocumentModel feuilleRouteDoc) {
        return LockUtils.isLockedByCurrentUser(context.getSession(), feuilleRouteDoc.getRef());
    }

    @Override
    protected EtapeValidationStatus getValidationStatut(final String validationStatus, final String typeEtape) {
        return RepEtapeValidationStatusEnum.getEnumFromKey(validationStatus, typeEtape);
    }
}
