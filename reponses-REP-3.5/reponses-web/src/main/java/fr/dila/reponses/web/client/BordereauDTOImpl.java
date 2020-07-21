package fr.dila.reponses.web.client;

import java.io.Serializable;

public class BordereauDTOImpl implements BordereauDTO, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Boolean partMiseAJour = Boolean.FALSE;
    private Boolean partReponse = Boolean.FALSE;
    private Boolean partIndexationAN = Boolean.FALSE;
    private Boolean partIndexationSENAT = Boolean.FALSE;
    private Boolean partEditableIndexationComplementaire = Boolean.FALSE;
    private Boolean partIndexationComplementaireAN = Boolean.FALSE;
    private Boolean partIndexationComplementaireSE = Boolean.FALSE;
    private Boolean partIndexationComplementaireMotCle = Boolean.FALSE;
    private Boolean partFeuilleRoute = Boolean.FALSE;
    private Boolean partEditMinistereRattachement  = Boolean.FALSE;
    private Boolean partEditDirectionPilote  = Boolean.FALSE;
    
    public BordereauDTOImpl(Boolean partMiseAJour,
            Boolean partReponse,
            Boolean partIndexationAN,
            Boolean partIndexationSENAT,
            Boolean partEditableIndexationComplementaire,
            Boolean partIndexationComplementaireAN,
            Boolean partIndexationComplementaireSE,
            Boolean partIndexationComplementaireMotCle,
            Boolean partFeuilleRoute,
            Boolean partCanEditMinistereRattachement,
            Boolean partCanDirectionPilote) {
        
        this.partMiseAJour = partMiseAJour;
        this.partReponse = partReponse;
        this.partIndexationAN = partIndexationAN;
        this.partIndexationSENAT = partIndexationSENAT;
        this.partEditableIndexationComplementaire = partEditableIndexationComplementaire;
        this.partIndexationComplementaireAN = partIndexationComplementaireAN;
        this.partIndexationComplementaireSE = partIndexationComplementaireSE;
        this.partIndexationComplementaireMotCle = partIndexationComplementaireMotCle;
        this.partFeuilleRoute = partFeuilleRoute;
        this.partEditMinistereRattachement = partCanEditMinistereRattachement;
        this.partEditDirectionPilote = partCanDirectionPilote;
    }
    
    @Override
    public Boolean hasPartMiseAJour(){
        return partMiseAJour;
    }
    
    @Override
    public Boolean hasPartReponse(){
        return partReponse;
    }
    
    @Override
    public Boolean hasPartIndexation(){
        return partIndexationAN || partIndexationSENAT; 
    }
    
    @Override
    public Boolean hasPartIndexationAN(){
        return partIndexationAN;
    }
    
    @Override
    public Boolean hasPartIndexationSENAT(){
        return partIndexationSENAT;
    }
    
    @Override
    public Boolean hasPartEditableIndexationComplementaire(){
        return partEditableIndexationComplementaire;
    }
    
    @Override
    public Boolean hasPartIndexationComplementaireAN(){
        return partIndexationComplementaireAN;
    }
    
    @Override
    public Boolean hasPartIndexationComplementaireSE(){
        return partIndexationComplementaireSE;
    }
    
    @Override
    public Boolean hasPartIndexationComplementaireMotCle(){
        return partIndexationComplementaireMotCle;
    }
    
    @Override
    public Boolean hasPartFeuilleRoute(){
        return partFeuilleRoute;
    }

    @Override
    public Boolean hasPartEditMinistereRattachement() {
        return partEditMinistereRattachement;
    }

    @Override
    public Boolean hasPartEditDirectionPilote() {
        return partEditDirectionPilote;
    }
}
