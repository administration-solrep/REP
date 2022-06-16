package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import fr.dila.st.core.util.FullTextUtil;

/**
 *
 * Met à jour le champ ministère interrogé de manière
 * à transformer la valeur de l'utilisateur en valeur utilisable
 * par le LIKE du moteur de recherche
 * @author jgomez
 *
 */
public class MinistereInterrogeTraitement implements RequeteTraitement<Requete> {

    @Override
    public void doBeforeQuery(Requete requete) {
        if (requete.getMinistereInterroge() == null) {
            return;
        } else {
            String frontEndMinistereInterroge = requete.getMinistereInterroge();
            requete.setMinistereInterroge(FullTextUtil.replaceStarByPercent(frontEndMinistereInterroge));
        }
    }

    @Override
    public void doAfterQuery(Requete requete) {
        return;
    }

    @Override
    public void init(Requete requete) {
        return;
    }
}
