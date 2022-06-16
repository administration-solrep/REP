package fr.dila.reponses.core.adapter;

import fr.dila.reponses.core.feuilleroute.ReponsesFeuilleRouteImpl;
import fr.dila.reponses.core.feuilleroute.ReponsesRouteStepImpl;
import fr.dila.ss.api.constant.SSConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter.FeuilleRouteStepsContainerImpl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.ParallelRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.SerialRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.StepElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * Adaptateur de Document vers FeuilleRoute.
 *
 * @author jtremeaux
 */
public class FeuilleRouteAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        String type = doc.getType();
        if (SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            FeuilleRouteExecutionType executionType = FeuilleRouteStepFolderSchemaUtil.getExecutionType(doc);
            switch (executionType) {
                case serial:
                    return new ReponsesFeuilleRouteImpl(doc, new SerialRunner());
                case parallel:
                    return new ReponsesFeuilleRouteImpl(doc, new ParallelRunner());
            }
        } else if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(type)) {
            return new ReponsesRouteStepImpl(doc, new StepElementRunner());
        } else if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            FeuilleRouteExecutionType executionType = FeuilleRouteStepFolderSchemaUtil.getExecutionType(doc);
            switch (executionType) {
                case serial:
                    return new FeuilleRouteStepsContainerImpl(doc, new SerialRunner());
                case parallel:
                    return new FeuilleRouteStepsContainerImpl(doc, new ParallelRunner());
            }
        }

        return null;
    }
}
