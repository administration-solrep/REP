package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import fr.dila.st.core.util.FullTextUtil;

public class MinistereRattachementTraitement implements RequeteTraitement<Requete> {

    @Override
    public void doBeforeQuery(Requete requete) {
        if (requete.getMinistereRattachement() == null) {
            return;
        } else {
            String frontEndMinistereRattachement = requete.getMinistereRattachement();
            requete.setMinistereRattachement(FullTextUtil.replaceStarByPercent(frontEndMinistereRattachement));
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
