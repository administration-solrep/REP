package fr.dila.reponses.core.feuilleroute;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.core.impl.ElementRunner;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.feuilleroute.STRouteStepImpl;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation d'une étape de feuille de route Réponses.
 * 
 * @author jtremeaux
 */
public class ReponsesRouteStepImpl extends STRouteStepImpl implements ReponsesRouteStep {
    /**
     * UID.
     */
    private static final long serialVersionUID = -7177633331090519475L;

    /**
     * Constructeur de ReponsesRouteStepImpl.
     * 
     * @param doc Modèle de document
     * @param runner Exécuteur d'étape
     */
    public ReponsesRouteStepImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    @Override
    public boolean isReaffectation() {
        return PropertyUtil.getBooleanProperty(document,
                STSchemaConstant.ROUTING_TASK_SCHEMA,
                ReponsesSchemaConstant.ROUTING_TASK_REAFFECTATION_PROPERTY);
    }

    @Override
    public void setReaffectation(boolean isReaffectation) {
        PropertyUtil.setProperty(document,
                STSchemaConstant.ROUTING_TASK_SCHEMA,
                ReponsesSchemaConstant.ROUTING_TASK_REAFFECTATION_PROPERTY,
                isReaffectation);
    }

}
