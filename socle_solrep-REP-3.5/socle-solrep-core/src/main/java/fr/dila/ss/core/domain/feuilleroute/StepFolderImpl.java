package fr.dila.ss.core.domain.feuilleroute;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.core.impl.DocumentRouteStepsContainerImpl;
import fr.dila.ecm.platform.routing.core.impl.ElementRunner;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.core.schema.StepFolderSchemaUtils;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Implémentation des documents de type conteneur d'étape de feuille de route.
 * 
 * @author jtremeaux
 */
public class StepFolderImpl extends DocumentRouteStepsContainerImpl implements StepFolder {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2464999080273631053L;

    /**
     * Constructeur de StepFolderImpl.
     * 
     * @param doc Document
     * @param runner Runner
     */
    public StepFolderImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }
    
    @Override
    public String getExecution() {
    	return StepFolderSchemaUtils.getExecution(document);
    }

    @Override
    public void setExecution(String execution) {
    	StepFolderSchemaUtils.setExecution(document, execution);
    }

    @Override
    public boolean isSerial() {
        return STSchemaConstant.STEP_FOLDER_EXECUTION_SERIAL_VALUE.equals(getExecution());
    }
    
    @Override
    public boolean isParallel() {
        return STSchemaConstant.STEP_FOLDER_EXECUTION_PARALLEL_VALUE.equals(getExecution());
    }
}
