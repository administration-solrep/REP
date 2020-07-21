package fr.dila.reponses.core.recherche.traitement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * 
 * Le traitement pour les états de la question : on prends les états de la requête,
 * que l'on place sous forme de liste
 * @author jgomez
 *
 */
public class EtatQuestionTraitement implements RequeteTraitement<Requete>{
   
    private static final String ETAT_RETIRE = "etatRetire";
    
//    private static final String ETAT_REATTRIBUE = "etatReattribue";
    
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(EtatQuestionTraitement.class); 
    
    public EtatQuestionTraitement(){
        super();
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
        List<String> etatsClos = new ArrayList<String>();
        if (requete.getEtatCaduque()){
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_CADUQUE);
        }
        if (requete.getEtatClotureAutre()){
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE);
        }
        if (requete.getEtat(ETAT_RETIRE)){
            etatsClos.add(VocabularyConstants.ETAT_QUESTION_RETIREE);
        }
        requete.setEtatQuestionList(etatsClos);
        if (requete.getEtatQuestionList() == null || requete.getEtatQuestionList().size() == 0){
            requete.setEtatQuestionList(null);
        }
        return;
    }
    
    @Override
    public void doAfterQuery(Requete requete) {
    }
    
    @Override
    public void init(Requete requete) {
    }
    
}
