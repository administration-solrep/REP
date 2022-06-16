package fr.dila.reponses.core.feuilleroute;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.core.feuilleroute.SSFeuilleRouteImpl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du type FeuilleRoute de Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesFeuilleRouteImpl extends SSFeuilleRouteImpl implements ReponsesFeuilleRoute {
    private static final long serialVersionUID = -1110382493524514160L;

    private transient ReponsesIndexableDocument indexation;

    /**
     * Constructeur de FeuilleRouteImpl.
     *
     * @param doc doc
     * @param runner runner
     */
    public ReponsesFeuilleRouteImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
        indexation = getDocument().getAdapter(ReponsesIndexableDocument.class);
    }

    @Override
    public String getTitreQuestion() {
        return PropertyUtil.getStringProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_TITRE_QUESTION_PROPERTY_NAME
        );
    }

    @Override
    public void setTitreQuestion(String titreQuestion) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_TITRE_QUESTION_PROPERTY_NAME,
            titreQuestion
        );
    }

    @Override
    public String getIdDirectionPilote() {
        return PropertyUtil.getStringProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME
        );
    }

    @Override
    public void setIdDirectionPilote(String idDirectionPilote) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME,
            idDirectionPilote
        );
    }

    @Override
    public String getIntituleDirectionPilote() {
        return PropertyUtil.getStringProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_INTITULE_DIRECTION_PILOTE_PROPERTY_NAME
        );
    }

    @Override
    public void setIntituleDirectionPilote(String intituleDirectionPilote) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            ReponsesConstant.REPONSES_FEUILLE_ROUTE_INTITULE_DIRECTION_PILOTE_PROPERTY_NAME,
            intituleDirectionPilote
        );
    }

    @Override
    public List<String> getANAnalyses() {
        return indexation.getAssNatAnalyses();
    }

    @Override
    public void setANAnalyses(List<String> analyseAN) {
        indexation.setAssNatAnalyses(analyseAN);
    }

    @Override
    public List<String> getANRubrique() {
        return indexation.getAssNatRubrique();
    }

    @Override
    public void setANRubrique(List<String> rubriqueAN) {
        indexation.setAssNatRubrique(rubriqueAN);
    }

    @Override
    public List<String> getANTeteAnalyse() {
        return indexation.getAssNatTeteAnalyse();
    }

    @Override
    public void setANTeteAnalyse(List<String> teteAnalyseAN) {
        indexation.setAssNatTeteAnalyse(teteAnalyseAN);
    }

    @Override
    public List<String> getSenatRubrique() {
        return indexation.getSenatQuestionRubrique();
    }

    @Override
    public void setSenatRubrique(List<String> senatRubrique) {
        indexation.setSenatQuestionRubrique(senatRubrique);
    }

    @Override
    public List<String> getSenatRenvois() {
        return indexation.getSenatQuestionRenvois();
    }

    @Override
    public void setSenatRenvois(List<String> senatRenvois) {
        indexation.setSenatQuestionRenvois(senatRenvois);
    }

    @Override
    public List<String> getSenatThemes() {
        return indexation.getSenatQuestionThemes();
    }

    @Override
    public void setSenatThemes(List<String> senatTheme) {
        indexation.setSenatQuestionThemes(senatTheme);
    }

    @Override
    public List<String> getMotsClesMinistere() {
        return indexation.getMotsClefMinistere();
    }

    @Override
    public void setMotsClesMinistere(List<String> motClesMinistere) {
        indexation.setMotsClefMinistere(motClesMinistere);
    }
}
