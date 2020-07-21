package fr.dila.reponses.core.recherche.traitement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 *  Le champ des statuts d'étape proposé dans la recherche est un mixte de 2 métadonnées de l'étape :
 *  - le cycle de vie d'une étape (en cours, à venir)
 *  - et le statut de validation d'une étape (uniquement dans le case d'une étape validé) : validé automatiquement, non concerné, etc ...
 *  
 *  Le traitement suivant récupère l'information donnée par l'utilisateur, et place ces 2 champs pour effectuer 
 *  des recherches dessus.
 * 
 * @author jgomez
 *
 */
public class ReponseEtapeValidationStatutTraitement implements RequeteTraitement<Requete>{

    public static final Log LOGGER = LogFactory.getLog(ReponseEtapeValidationStatutTraitement.class); 
    
    public static final String DEFAULT_ID = StringUtils.EMPTY;
    
    public static final String ALL_CHOICE = "all";
    
    @Override
    public void doBeforeQuery(Requete requete) {
        String rechercheEtapeStatut = requete.getEtapeIdStatut();
        if (DEFAULT_ID.equals(rechercheEtapeStatut) || rechercheEtapeStatut == null){
            requete.setEtapeCurrentCycleState(null);
            requete.setValidationStatutId(null);
            return;
        }
        String[] statuts = rechercheEtapeStatut.split("_");
        if (statuts.length != 2){
            LOGGER.error("Le traitement ReponseEtapeValidationStatutTraitement a échoué, vérifier la forme des identifiants dans le fichier statut_etape_recherche_statut.csv");
            LOGGER.error("Reçu : " + rechercheEtapeStatut);
            throw new RuntimeException(
            "Le vocabulaire statut_etape_recherche_statut doit posséder des identifiants de la forme <cycleState>-<validationStatutId>");
        }
        String etapeCurrentCycleState = statuts[0];
        String validationStatutId = statuts[1];
        
        if (ALL_CHOICE.equals(validationStatutId)){
            requete.setValidationStatutId(null);
        }
        else{
            requete.setValidationStatutId(validationStatutId);
        }
        requete.setEtapeCurrentCycleState(etapeCurrentCycleState);
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