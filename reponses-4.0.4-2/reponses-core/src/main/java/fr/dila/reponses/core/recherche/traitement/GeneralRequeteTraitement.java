package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RAPPELE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_REATTRIBUE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RENOUVELE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_SIGNALE;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_COMPLEXE_SCHEMA;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Le traitement qui est appelé par la requête pour exécuter tous les traitements associés.
 * @author jgomez
 *
 */
public class GeneralRequeteTraitement implements RequeteTraitement<Requete> {
    private static final Log LOGGER = LogFactory.getLog(GeneralRequeteTraitement.class);

    private final List<RequeteTraitement<Requete>> traitements;

    public GeneralRequeteTraitement() {
        traitements = new ArrayList<>();
        traitements.add(new TexteIntegralTraitement());
        traitements.add(
            new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA, DATE_JO_QUESTION_DEBUT, DATE_JO_QUESTION_FIN)
        );
        traitements.add(
            new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA, DATE_JO_REPONSE_DEBUT, DATE_JO_REPONSE_FIN)
        );
        traitements.add(
            new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA, DATE_SIGNALEMENT_DEBUT, DATE_SIGNALEMENT_FIN)
        );
        traitements.add(new NumeroQuestionTraitement());
        traitements.add(new ProtectSingleQuoteOnIndexationTraitement());
        traitements.add(new EtatQuestionTraitement());
        traitements.add(new ReponsePublieeTraitement());

        traitements.add(new GeneralAttributTraitement(ETAT_RAPPELE));
        traitements.add(new GeneralAttributTraitement(ETAT_SIGNALE));
        traitements.add(new GeneralAttributTraitement(ETAT_RENOUVELE));
        traitements.add(new GeneralAttributTraitement(ETAT_REATTRIBUE));

        traitements.add(new ReponseEtapeValidationStatutTraitement());
        traitements.add(new ReponsesConversionPosteIdMailboxIdTraitement());
    }

    @Override
    public void init(Requete requete) {
        if (requete == null) {
            LOGGER.warn("La requête est nulle, pas d'initialisation des traitements");
            return;
        }

        traitements.forEach(traitement -> traitement.init(requete));
    }

    @Override
    public void doBeforeQuery(Requete requete) {
        if (requete == null) {
            LOGGER.warn("La requête est nulle, pas d'application des traitements");
            return;
        }

        traitements.forEach(traitement -> traitement.doBeforeQuery(requete));
    }

    @Override
    public void doAfterQuery(Requete requete) {}

    /**
     * Cette méthode n'est utilisée que pour les tests.
     * @return la liste des traitements
     */
    public List<RequeteTraitement<Requete>> getTraitements() {
        return traitements;
    }
}
