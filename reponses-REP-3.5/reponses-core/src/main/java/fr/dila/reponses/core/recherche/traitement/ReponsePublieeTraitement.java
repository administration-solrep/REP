package fr.dila.reponses.core.recherche.traitement;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * Un traitement pour la sous-clause concernant les réponses avec réponses publiées ou non.
 * Ce traitement est utilisé pour régler 1 problème avec le IN dans les query model de nuxeo.
 * ( Pas vraiment myen d'utiliser des valeurs booléennes)
 * @author jgomez
 *
 */
public class ReponsePublieeTraitement implements RequeteTraitement<Requete>{
    
    static Log log = LogFactory.getLog(ReponsePublieeTraitement.class); 
    
    public ReponsePublieeTraitement(){
        super();
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
        // Les 2 valeurs sont cochées, pas de traitement à effectuer
        List<String> caracteristiqueQuestion = requete.getCaracteristiqueQuestion();
        if ( caracteristiqueQuestion.size() == 0 ){
            requete.setClauseCaracteristiques(null);
            return;
        }
        if (caracteristiqueQuestion.size() == 2 ){
            requete.setClauseCaracteristiques("q.qu:etatQuestion = 'repondu' OR q.qu:etatQuestion = 'en cours'");
            return ;
        }
        // Le critère En attente de réponse
        if (caracteristiqueQuestion.contains(RequeteConstants.AVEC_REPONSE_PUBLIEE)){
            requete.setClauseCaracteristiques("q.qu:etatQuestion = 'repondu'");
            return;
        }
        // Le critère  Répondu
        if (caracteristiqueQuestion.contains(RequeteConstants.SANS_REPONSE_PUBLIEE)){
            requete.setClauseCaracteristiques("q.qu:etatQuestion = 'en cours'");
            return;
        }
    }
    
    @Override
    public void doAfterQuery(Requete requete) {
    }
    
    @Override
    public void init(Requete requete) {
    }
    
}
