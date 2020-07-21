package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants.ExecutionTypeValues;
import fr.dila.ecm.platform.routing.core.impl.DocumentRouteStepsContainerImpl;
import fr.dila.ecm.platform.routing.core.impl.ParallelRunner;
import fr.dila.ecm.platform.routing.core.impl.SerialRunner;
import fr.dila.ecm.platform.routing.core.impl.StepElementRunner;
import fr.dila.reponses.core.feuilleroute.ReponsesFeuilleRouteImpl;
import fr.dila.reponses.core.feuilleroute.ReponsesRouteStepImpl;
import fr.dila.ss.core.schema.StepFolderSchemaUtils;
import fr.dila.st.api.constant.STConstant;

/**
 * Adaptateur de Document vers FeuilleRoute.
 * 
 * @author jtremeaux
 */
public class FeuilleRouteAdapterFactory implements DocumentAdapterFactory {

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        String type = doc.getType();
        if (STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            ExecutionTypeValues executionType = StepFolderSchemaUtils.getExecutionType(doc);
            switch (executionType) {
            case serial:
                return new ReponsesFeuilleRouteImpl(doc, new SerialRunner());
            case parallel:
                return new ReponsesFeuilleRouteImpl(doc, new ParallelRunner());
            }
        } else if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(type)) {
            return new ReponsesRouteStepImpl(doc, new StepElementRunner());
        } else if (DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            ExecutionTypeValues executionType = StepFolderSchemaUtils.getExecutionType(doc);
            switch (executionType) {
            case serial:
                return new DocumentRouteStepsContainerImpl(doc, new SerialRunner());
            case parallel:
                return new DocumentRouteStepsContainerImpl(doc, new ParallelRunner());
            }
        }

        return null;
	}
	
}
