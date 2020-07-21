package fr.dila.reponses.core.recherche.traitement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * 
 * Place l'état rappelé à null au début de la requête
 * @author jgomez
 *
 */
public class EtatRappeleTraitement implements RequeteTraitement<Requete>{
    
    private static final String ETAT_RAPPELE = "etatRappele";
    
    public static final Log log = LogFactory.getLog(EtatRappeleTraitement.class); 
    
    public EtatRappeleTraitement(){
        super();
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
            // Si l'état est à faux, on le passe à nul de façon à l'ignorer.
            if (!requete.getEtat(ETAT_RAPPELE)){
                requete.setEtat(ETAT_RAPPELE, null);
            }
            return;
    }
    
    @Override
    public void doAfterQuery(Requete requete) {
    }
    
    @Override
    public void init(Requete requete) {
        requete.setEtat(ETAT_RAPPELE, null);
    }
    
}
