package fr.dila.reponses.web.client;

/**
 * Procure les info nécessaire a l'affichage de bordereau.xhtml
 * @author spesnel
 *
 */
public interface BordereauDTO {
    
    Boolean hasPartMiseAJour();
    
    Boolean hasPartReponse();
    
    Boolean hasPartIndexation();
    
    Boolean hasPartIndexationAN();
    
    Boolean hasPartIndexationSENAT();
    
    Boolean hasPartEditableIndexationComplementaire();
    
    Boolean hasPartIndexationComplementaireAN();
    
    Boolean hasPartIndexationComplementaireSE();
    
    Boolean hasPartIndexationComplementaireMotCle();
    
    Boolean hasPartFeuilleRoute();
    
    Boolean hasPartEditMinistereRattachement();

    Boolean hasPartEditDirectionPilote();

}
