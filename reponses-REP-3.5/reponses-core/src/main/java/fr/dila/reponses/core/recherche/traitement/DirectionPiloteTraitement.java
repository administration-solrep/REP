package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import fr.dila.st.core.util.FullTextUtil;

public class DirectionPiloteTraitement implements RequeteTraitement<Requete>{

    @Override
    public void doBeforeQuery(Requete requete) {
            if (requete.getDirectionPilote() == null){
                return;
            }
            else{
                String frontEndDirectionPilote = requete.getDirectionPilote();
                requete.setDirectionPilote(FullTextUtil.replaceStarByPercent(frontEndDirectionPilote));
            }
    }

    @Override
    public void doAfterQuery(Requete requete) {
        return ;
    }

    @Override
    public void init(Requete requete) {
       return ;
    }
}
