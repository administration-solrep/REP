package fr.dila.reponses.core.feuilleroute;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.ss.core.feuilleroute.SSRouteStepImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation d'une étape de feuille de route Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesRouteStepImpl extends SSRouteStepImpl implements ReponsesRouteStep {
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
        return PropertyUtil.getBooleanProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            ReponsesSchemaConstant.ROUTING_TASK_REAFFECTATION_PROPERTY
        );
    }

    @Override
    public void setReaffectation(boolean isReaffectation) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            ReponsesSchemaConstant.ROUTING_TASK_REAFFECTATION_PROPERTY,
            isReaffectation
        );
    }
}
