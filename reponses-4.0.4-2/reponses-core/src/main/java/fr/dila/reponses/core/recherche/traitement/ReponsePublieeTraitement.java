package fr.dila.reponses.core.recherche.traitement;

import static java.util.stream.Collectors.joining;

import fr.dila.reponses.api.enumeration.StatutReponseEnum;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import java.util.List;
import java.util.stream.Stream;

/**
 * Un traitement pour la sous-clause concernant les réponses avec réponses publiées ou non.
 * Ce traitement est utilisé pour régler 1 problème avec le IN dans les query model de nuxeo.
 * ( Pas vraiment myen d'utiliser des valeurs booléennes)
 * @author jgomez
 *
 */
public class ReponsePublieeTraitement implements RequeteTraitement<Requete> {
    private static final String OR_OPERATOR = " OR ";

    public ReponsePublieeTraitement() {}

    @Override
    public void doBeforeQuery(Requete requete) {
        List<String> caracteristiqueQuestion = requete.getCaracteristiqueQuestion();
        if (caracteristiqueQuestion.isEmpty()) {
            requete.setClauseCaracteristiques(null);
        } else {
            requete.setClauseCaracteristiques(
                Stream
                    .of(StatutReponseEnum.values())
                    .filter(statutReponse -> caracteristiqueQuestion.contains(statutReponse.name()))
                    .map(StatutReponseEnum::getWhereClause)
                    .collect(joining(OR_OPERATOR))
            );
        }
    }

    @Override
    public void doAfterQuery(Requete requete) {}

    @Override
    public void init(Requete requete) {}
}
