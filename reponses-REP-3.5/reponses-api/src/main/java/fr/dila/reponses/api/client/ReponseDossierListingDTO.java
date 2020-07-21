package fr.dila.reponses.api.client;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import fr.dila.reponses.api.recherche.IdLabel;

public interface ReponseDossierListingDTO extends Map<String,Serializable> {
        
    String getSourceNumeroQuestion();
    
    String getOrigineQuestion();
    
    String getTypeQuestion();
    
    Date getDatePublicationJO();
    
    Date getDateSignalement();
    
    String getMinistereInterroge();
    
    String getAuteur();
    
    String getMotCles();
       
    String getDelai();

    IdLabel[] getCaseLinkIdsLabels();
    
    String getDossierId();
    
    String getQuestionId();
    
    Boolean isUrgent();
    
    Boolean isRenouvelle();
    
    Boolean isSignale();
    
    String getDocIdForSelection();
    
    Boolean isLocked();
    
    Boolean hasConnexite();
    
    Boolean hasAttachement();
    
    Boolean hasLot();
    
    String getCaseLinkId();
    
    Boolean getRead();
    
    String getErrata();
    
    String getRedemarre();
    
    String getDirecteur();
    
    String getRoutingTaskType();
    
    String getLegislature();
    
    String getMinistereAttributaire();
    
    String getDirectionRunningStep();
    
    String getEtatQuestion();
    
    void setSourceNumeroQuestion(String sourceNumeroQuestion);
    
    void setOrigineQuestion(String origineQuestion);
    
    void setTypeQuestion(String typeQuestion);
        
    void setDatePublicationJO(Date datePublicationJO);
    
    void setDateSignalement(Date dateSignalement);
    
    void setMinistereInterroge(String ministereInterroge);
    
    void setAuteur(String auteur);
    
    void setMotCles(String motcles);    
    
    void setDelai(String delai);    
    
    void setCaseLinkIdsLabels(IdLabel[] caseLinkLabels);
    
    void setDossierId(String dossierId);
    
    void setQuestionId(String questionId);
    
    void setUrgent(Boolean isUrgent);
    
    void setRenouvelle(Boolean isRenouvelle);
    
    void setSignale(Boolean isSignale);
    
    void setDocIdForSelection(String docIdForSelection);
    
    void setLocked(Boolean isLocked);
    
    void setConnexite(Boolean hasConnexite);
    
    void setAttachement(Boolean hasAttachement);
    
    void setLot(Boolean hasLot);
    
    void setCaseLinkId(String caseLinkId);
    
    void setRead(Boolean read);
    
    void setRoutingTaskType(String routingTaskType);
    
    void setLegislature(String legilsature);
    
    void setMinistereAttributaire(String ministereAttributaire);
    
    void setDirectionRunningStep(String directionRunningStep);
    
    void setErrata(String errata);
    
    void setRedemarre(String redemarre);
    
    void setDirecteur(String isDirecteur);
    
    void setEtatQuestion(String etatQuestion);
}
