package fr.dila.reponses.api.feuilleroute;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.io.Serializable;
import java.util.List;

/**
 * Interface des documents de type feuille de route.
 *
 * @author jtremeaux
 */
public interface ReponsesFeuilleRoute extends FeuilleRoute, SSFeuilleRoute, Serializable {
    /**
     * Retourne le titre de la question.
     *
     * @return Titre de la question
     */
    String getTitreQuestion();

    /**
     * Renseigne le titre de la question.
     */
    void setTitreQuestion(String titreQuestion);

    /**
     * Gets the Question id Ministere rattachement.
     */
    String getIdDirectionPilote();

    void setIdDirectionPilote(String idDirectionPilote);

    /**
     * Gets the Question intitule  pilote.
     */
    String getIntituleDirectionPilote();

    void setIntituleDirectionPilote(String intituleDirectionPilote);

    /**
     * Gets indexation AN Analyse.
     */
    List<String> getANAnalyses();

    void setANAnalyses(List<String> analyseAN);

    /**
     * Gets indexation AN Rubrique.
     */
    List<String> getANRubrique();

    void setANRubrique(List<String> rubriqueAN);

    /**
     * Gets indexation AN Tête Analyse.
     */
    List<String> getANTeteAnalyse();

    void setANTeteAnalyse(List<String> teteAnalyseAN);

    /**
     * Gets indexation Senat Thèmes
     */
    List<String> getSenatThemes();

    void setSenatThemes(List<String> senatTheme);

    /**
     * Gets indexation Senat Rubrique.
     */
    List<String> getSenatRubrique();

    void setSenatRubrique(List<String> senatRubrique);

    /**
     * Gets indexation Senat Renvois.
     */
    List<String> getSenatRenvois();

    void setSenatRenvois(List<String> senatRenvois);

    /**
     * Gets indexation mot cles ministere.
     */
    List<String> getMotsClesMinistere();

    void setMotsClesMinistere(List<String> motClesMinistere);
}
