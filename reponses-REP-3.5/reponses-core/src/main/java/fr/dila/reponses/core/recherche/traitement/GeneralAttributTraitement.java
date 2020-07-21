package fr.dila.reponses.core.recherche.traitement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * 
 * Place l'attribut à null au début de la requête
 * @author jgomez
 *
 */
public class GeneralAttributTraitement implements RequeteTraitement<Requete>{
    

    
    public static final Log log = LogFactory.getLog(GeneralAttributTraitement.class);

    private String etat; 
    
    public GeneralAttributTraitement(String etat){
        super();
        this.etat = etat;
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
            // Si l'état est à faux, on le passe à nul de façon à l'ignorer.
            if (!requete.getEtat(this.etat)){
                requete.setEtat(this.etat, null);
            }
            return;
    }
    
    @Override
    public void doAfterQuery(Requete requete) {
    }
    
    @Override
    public void init(Requete requete) {
        requete.setEtat(this.etat, null);
    }
    
}
