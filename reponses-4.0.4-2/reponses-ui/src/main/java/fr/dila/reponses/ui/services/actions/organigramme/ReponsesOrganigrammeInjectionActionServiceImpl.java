package fr.dila.reponses.ui.services.actions.organigramme;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.util.RepExcelUtil;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * ActionBean d'injection de gouvernement
 */
public class ReponsesOrganigrammeInjectionActionServiceImpl implements ReponsesOrganigrammeInjectionActionService {
    public static final String EXPORT_GOUVERNEMENT_FILE_NAME = "gouvernement.xls";

    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesOrganigrammeInjectionActionServiceImpl.class);

    @Override
    public void exportGouvernement(CoreSession session) {
        try {
            FileUtils.copyInputStreamToFile(
                RepExcelUtil.createExportGvt(session).getInputStream(),
                new File(fr.dila.st.core.util.FileUtils.getAppTmpFilePath(EXPORT_GOUVERNEMENT_FILE_NAME))
            );
        } catch (IOException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, "Erreur lors de la réponse", e);
            throw new NuxeoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void executeInjection(SpecificContext context) {
        if (
            SSUIServiceLocator
                .getSSOrganigrammeManagerUIService()
                .allowUpdateOrganigramme((SSPrincipal) context.getSession().getPrincipal(), null)
        ) {
            ReponsesServiceLocator
                .getReponsesInjectionGouvernementService()
                .executeInjection(
                    context.getSession(),
                    UserSessionHelper.getUserSessionParameter(context, "injections", ArrayList.class)
                );
        }
    }
}
