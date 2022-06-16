package fr.dila.reponses.core.operation.nxshell;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.FAIL_GET_DOCUMENT_TEC;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * Opération pour modifier les paramètres techniques d'administration dans réponses.
 */
@Operation(
    id = ModifierParametreTechniqueOperation.ID,
    category = STConstant.PARAMETRE_DOCUMENT_TYPE,
    label = "ModifierParametreTechnique",
    description = "Modifie le titre des paramètres techniques d'administration dans réponses"
)
@STVersion(version = "4.0.2")
public class ModifierParametreTechniqueOperation {
    public static final String ID = "Reponses.Parametre.Modification";
    private static final STLogger LOGGER = STLogFactory.getLog(ModifierParametreTechniqueOperation.class);
    private static final Map<String, String> PARAMETERS_MAPPING = new ImmutableMap.Builder<String, String>()
        .put(
            STParametreConstant.USER_DECONNEXION_DESACTIVATION_DELAI,
            STParametreConstant.USER_DECONNEXION_DESACTIVATION_TITLE
        )
        .build();

    @Context
    private OperationContext context;

    public ModifierParametreTechniqueOperation() {
        super();
    }

    @OperationMethod
    public void run() {
        if (!getContext().getPrincipal().isAdministrator()) {
            throw new NuxeoException(
                "Seul un administrateur Nuxeo peux modifier des paramètres",
                HttpServletResponse.SC_FORBIDDEN
            );
        }
        CoreSession session = getContext().getCoreSession();
        PARAMETERS_MAPPING.forEach((key, value) -> modifyParameter(session, key, value));
    }

    private void modifyParameter(CoreSession session, String docName, String title) {
        String parameterRootPath = STServiceLocator
            .getSTParametreService()
            .getParametreFolder(session)
            .getPath()
            .toString();
        DocumentRef pathRef = new PathRef(parameterRootPath, docName);
        if (session.exists(pathRef)) {
            DocumentModel doc = session.getDocument(pathRef);
            DublincoreSchemaUtils.setTitle(doc, title);
            session.saveDocument(doc);
        } else {
            LOGGER.warn(FAIL_GET_DOCUMENT_TEC, String.format("Document %s introuvable", pathRef));
        }
    }

    public OperationContext getContext() {
        return context;
    }
}
