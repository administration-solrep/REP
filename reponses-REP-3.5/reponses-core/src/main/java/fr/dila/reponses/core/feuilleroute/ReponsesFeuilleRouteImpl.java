package fr.dila.reponses.core.feuilleroute;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.core.impl.ElementRunner;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.feuilleroute.STFeuilleRouteImpl;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation du type FeuilleRoute de Réponses.
 * 
 * @author jtremeaux
 */
public class ReponsesFeuilleRouteImpl extends STFeuilleRouteImpl implements ReponsesFeuilleRoute {

    private static final long serialVersionUID = -1110382493524514160L;

    /**
     * Constructeur de FeuilleRouteImpl.
     * 
     * @param doc doc
     * @param runner runner
     */
    public ReponsesFeuilleRouteImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    @Override
    public String getTitreQuestion() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_TITRE_QUESTION_PROPERTY_NAME);
    }

    @Override
    public void setTitreQuestion(String titreQuestion) {
        PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_TITRE_QUESTION_PROPERTY_NAME, titreQuestion);
    }

    public String getIdDirectionPilote() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME);
    }

    @Override
    public void setIdDirectionPilote(String idDirectionPilote) {
        PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME, idDirectionPilote);

    }

    @Override
    public String getIntituleDirectionPilote() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_INTITULE_DIRECTION_PILOTE_PROPERTY_NAME);
    }

    @Override
    public void setIntituleDirectionPilote(String intituleDirectionPilote) {
        PropertyUtil.setProperty(document,STSchemaConstant.FEUILLE_ROUTE_SCHEMA, ReponsesConstant.REPONSES_FEUILLE_ROUTE_INTITULE_DIRECTION_PILOTE_PROPERTY_NAME, intituleDirectionPilote);
    }
}
