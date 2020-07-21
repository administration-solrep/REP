package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_COMPLEXE_SCHEMA;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * Le traitement qui est appelé par la requête pour exécuter tous les traitements associés.
 * @author jgomez
 *
 */
public class GeneralRequeteTraitement implements RequeteTraitement<Requete> {

    private ArrayList<RequeteTraitement<Requete>> traitements;
    
    private static final String ETAT_RAPPELE = "etatRappele";
    private static final String ETAT_SIGNALE = "etatSignale";
    private static final String ETAT_RENOUVELE = "etatRenouvele";
    private static final String ETAT_REATTRIBUE = "etatReattribue";
    
    private static final Log LOGGER = LogFactory.getLog(GeneralRequeteTraitement.class);
    
    public GeneralRequeteTraitement(){
        traitements = new ArrayList<RequeteTraitement<Requete>>();
        traitements.add(new TexteIntegralTraitement());
        traitements.add(new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA,DATE_JO_QUESTION_DEBUT,DATE_JO_QUESTION_FIN));
        traitements.add(new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA,DATE_JO_REPONSE_DEBUT,DATE_JO_REPONSE_FIN));
        traitements.add(new DateIntervalleTraitement(REQUETE_COMPLEXE_SCHEMA,DATE_SIGNALEMENT_DEBUT,DATE_SIGNALEMENT_FIN));
        traitements.add(new NumeroQuestionTraitement());
        traitements.add(new ProtectSingleQuoteOnIndexationTraitement());
        traitements.add(new EtatQuestionTraitement());
        traitements.add(new ReponsePublieeTraitement());
        
        traitements.add(new GeneralAttributTraitement(ETAT_RAPPELE));
        traitements.add(new GeneralAttributTraitement(ETAT_SIGNALE));
        traitements.add(new GeneralAttributTraitement(ETAT_RENOUVELE));
        traitements.add(new GeneralAttributTraitement(ETAT_REATTRIBUE));
        
        traitements.add(new ReponseEtapeValidationStatutTraitement());
        traitements.add(new ReponsesConversionPosteIdMailboxIdTraitement());
    }
    
    @Override
    public void init(Requete requete) {
        if (requete == null){
            LOGGER.warn("La requête est nulle, pas d'initialisation des traitements");
            return ;
        }
        for (RequeteTraitement<Requete> traitement: traitements){
            traitement.init(requete);
        }
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
        if (requete == null){
            LOGGER.warn("La requête est nulle, pas d'application des traitements");
            return ;
        }
        for (RequeteTraitement<Requete> traitement: traitements){
            traitement.doBeforeQuery(requete);
        }
    }

    @Override
    public void doAfterQuery(Requete requete) {
    }
}
