package fr.dila.reponses.core.operation.nxshell;

import static fr.dila.cm.cases.CaseConstants.TITLE_PROPERTY_NAME;
import static java.lang.String.join;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.ss.api.constant.SmartFolderConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.operation.STVersion;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;

@Operation(id = QuestionsRappeleesCreationOperation.ID, category = STConstant.PARAMETRE_DOCUMENT_TYPE)
@STVersion(version = "4.0.0")
public class QuestionsRappeleesCreationOperation {
    public static final String ID = "Questions.Rappelees.Creation";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void run() {
        if (!context.getPrincipal().isAdministrator()) {
            throw new NuxeoException("seulement un admin nuxeo peux créer le smart folder des questions rappeléés");
        }

        CoreSession session = context.getCoreSession();
        if (
            !session.exists(
                new PathRef(
                    join("", RequeteConstants.BIBLIO_REQUETES_ROOT, RequeteConstants.REQUETE_QUESTIONS_RAPPELEES)
                )
            )
        ) {
            DocumentModel smartFolder = session.createDocumentModel(
                RequeteConstants.BIBLIO_REQUETES_ROOT,
                RequeteConstants.REQUETE_QUESTIONS_RAPPELEES,
                STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE
            );

            smartFolder.setPropertyValue(SmartFolderConstant.SMART_FOLDER_XPATH_POS, "18");
            smartFolder.setPropertyValue(TITLE_PROPERTY_NAME, "Liste des questions rappelées");
            smartFolder.setPropertyValue(
                SmartFolderConstant.SMART_FOLDER_XPATH_QUERY_PART,
                "q.qu:etatRappele = 1 AND ufnxql_ministere:(d.dos:ministereAttributaireCourant)"
            );

            session.createDocument(smartFolder);
        }
    }
}
