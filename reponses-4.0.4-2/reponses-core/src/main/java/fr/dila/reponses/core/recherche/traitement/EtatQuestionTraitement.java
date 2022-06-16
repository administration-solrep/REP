package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RETIRE;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * Le traitement pour les états de la question : on prends les états de la requête,
 * que l'on place sous forme de liste
 * @author jgomez
 *
 */
public class EtatQuestionTraitement implements RequeteTraitement<Requete> {

    public EtatQuestionTraitement() {}

    @Override
    public void doBeforeQuery(Requete requete) {
        List<String> etatsClos = new ArrayList<>();
        if (requete.getEtatCaduque()) {
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_CADUQUE);
        }
        if (requete.getEtatClotureAutre()) {
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE);
        }
        if (requete.getEtat(ETAT_RETIRE)) {
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_RETIREE);
        }
        requete.setEtatQuestionList(etatsClos);
        if (CollectionUtils.isEmpty(requete.getEtatQuestionList())) {
            requete.setEtatQuestionList(null);
        }
    }

    @Override
    public void doAfterQuery(Requete requete) {}

    @Override
    public void init(Requete requete) {}
}
