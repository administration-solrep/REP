package fr.dila.reponses.core.recherche;

import java.util.Date;

import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.recherche.IdLabel;
import fr.dila.st.core.client.AbstractMapDTO;


public class ReponseDossierListingDTOImpl extends AbstractMapDTO implements ReponseDossierListingDTO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ReponseDossierListingDTOImpl(){
    }
    
    @Override
    public String getSourceNumeroQuestion() {
        return getString(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION);
    }
    
    @Override
    public String getOrigineQuestion() {
        return getString(ReponseDossierListingConstants.ORIGINE_QUESTION);
    }

    @Override
    public Date getDatePublicationJO() {
        return getDate(ReponseDossierListingConstants.DATE_PUBLICATION_JO);
    }
    
    @Override
	public Date getDateSignalement() {
		return getDate(ReponseDossierListingConstants.DATE_SIGNALEMENT_QUESTION);
	}
    
    @Override
    public String getMinistereInterroge(){
        return getString(ReponseDossierListingConstants.MINISTERE_INTERROGE);
    }

    @Override
    public String getAuteur() {
        return getString(ReponseDossierListingConstants.AUTEUR);
    }

    @Override
    public String getMotCles() {
        return getString(ReponseDossierListingConstants.MOT_CLES);
    }

    @Override
    public String getDelai() {
        return getString(ReponseDossierListingConstants.DELAI);
    }
    
    @Override
    public String getEtatQuestion() {
        return getString(ReponseDossierListingConstants.ETAT);
    }

    @Override
    public void setSourceNumeroQuestion(String sourceNumeroQuestion) {
        put(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION, sourceNumeroQuestion);
    }

    @Override
    public void setOrigineQuestion(String origineQuestion){
        put(ReponseDossierListingConstants.ORIGINE_QUESTION, origineQuestion);
    }
    
    @Override
    public void setDatePublicationJO(Date datePublicationJO) {
        put(ReponseDossierListingConstants.DATE_PUBLICATION_JO, datePublicationJO);
    }
    
    @Override
	public void setDateSignalement(Date dateSignalement) {
    	put(ReponseDossierListingConstants.DATE_SIGNALEMENT_QUESTION, dateSignalement);
	}
    
    @Override
    public void setMinistereInterroge(String ministereInterroge){
        put(ReponseDossierListingConstants.MINISTERE_INTERROGE, ministereInterroge);
    }

    @Override
    public void setAuteur(String auteur) {
        put(ReponseDossierListingConstants.AUTEUR, auteur);
    }

    @Override
    public void setMotCles(String motcles) {
        put(ReponseDossierListingConstants.MOT_CLES, motcles);
        
    }

    @Override
    public void setDelai(String delai) {
        put(ReponseDossierListingConstants.DELAI, delai);
        
    }
    
    @Override
    public void setEtatQuestion(String etatQuestion) {
        put(ReponseDossierListingConstants.ETAT, etatQuestion);
        
    }

    
    @Override
    public IdLabel[] getCaseLinkIdsLabels() {
        return (IdLabel[])get(ReponseDossierListingConstants.CASE_LINK_IDS_LABELS);
    }


    @Override
    public String getDossierId() {
        return getString(ReponseDossierListingConstants.DOSSIER_ID);
    }

 


    
    @Override
    public void setCaseLinkIdsLabels(IdLabel[] caseLinkIdsLabels) {
        put(ReponseDossierListingConstants.CASE_LINK_IDS_LABELS, caseLinkIdsLabels);
    }


    @Override
    public void setDossierId(String dossierId) {
       put(ReponseDossierListingConstants.DOSSIER_ID, dossierId);
    }


    @Override
    public Boolean isUrgent() {
        return getBoolean(ReponseDossierListingConstants.IS_URGENT);
    }


    @Override
    public Boolean isRenouvelle() {
        return getBoolean(ReponseDossierListingConstants.IS_RENOUVELLE);
    }


    @Override
    public Boolean isSignale() {
        return getBoolean(ReponseDossierListingConstants.IS_SIGNALE);
    }


    @Override
    public void setUrgent(Boolean isUrgent) {
        put(ReponseDossierListingConstants.IS_URGENT, isUrgent);
    }


    @Override
    public void setRenouvelle(Boolean isRenouvelle) {
        put(ReponseDossierListingConstants.IS_RENOUVELLE, isRenouvelle);
        
    }


    @Override
    public void setSignale(Boolean isSignale) {
        put(ReponseDossierListingConstants.IS_SIGNALE, isSignale);
    }
    
    @Override
    public String getDocIdForSelection(){
        return getString(ReponseDossierListingConstants.DOC_ID_FOR_SELECTION);
    }
    
    @Override
	public void setDocIdForSelection(String docIdForSelection){
        put(ReponseDossierListingConstants.DOC_ID_FOR_SELECTION, docIdForSelection);
    }

    @Override
    public String getType() {
        return "Dossier";
    }

    
    @Override 
    public String getQuestionId(){
        return getString(ReponseDossierListingConstants.QUESTION_ID);
    }
    
    @Override 
    public void setQuestionId(String questionId){
        put(ReponseDossierListingConstants.QUESTION_ID, questionId);
    }
    
    @Override
    public Boolean hasLot(){
        return getBoolean(ReponseDossierListingConstants.HAS_LOT);
    }

    @Override
    public void setLot(Boolean hasLot){
        put(ReponseDossierListingConstants.HAS_LOT, hasLot);
    }
    
    @Override
    public Boolean isLocked(){
        return getBoolean(ReponseDossierListingConstants.IS_LOCKED);
    }
    
    @Override
    public void setLocked(Boolean isLocked){
        put(ReponseDossierListingConstants.IS_LOCKED, isLocked);
    }
    
    @Override
    public Boolean hasAttachement(){
        return getBoolean(ReponseDossierListingConstants.HAS_ATTACHEMENT);
    }
    
    @Override
    public void setAttachement(Boolean hasAttachement){
        put(ReponseDossierListingConstants.HAS_ATTACHEMENT, hasAttachement);
    }
    
    @Override
    public Boolean hasConnexite(){
        return getBoolean(ReponseDossierListingConstants.HAS_CONNEXITE);
    }
    
    @Override
    public void setConnexite(Boolean hasConnexite){
        put(ReponseDossierListingConstants.HAS_CONNEXITE, hasConnexite);
    }
    
    @Override
    public String getTypeQuestion(){
        return getString(ReponseDossierListingConstants.TYPE_QUESTION);
    }
    
    @Override
    public void setTypeQuestion(String typeQuestion){
        put(ReponseDossierListingConstants.TYPE_QUESTION, typeQuestion);
    }

    @Override
    public String getCaseLinkId() {
        return getString(ReponseDossierListingConstants.CASE_LINK_ID);
    }

    @Override
    public Boolean getRead() {
        return getBoolean(ReponseDossierListingConstants.IS_READ);
    }

    @Override
    public String getRoutingTaskType() {
        return getString(ReponseDossierListingConstants.ROUTING_TASK_TYPE);
    }

    @Override
    public String getLegislature() {
        return getString(ReponseDossierListingConstants.LEGISLATURE);
    }

    @Override
    public String getMinistereAttributaire() {
        return getString(ReponseDossierListingConstants.MINISTERE_ATTRIBUTAIRE);
    }

    @Override
    public String getDirectionRunningStep() {
        return getString(ReponseDossierListingConstants.DIRECTION_RUNNING_STEP);
    }

    @Override
    public void setCaseLinkId(String caseLinkId) {
       put(ReponseDossierListingConstants.CASE_LINK_ID, caseLinkId);
    }

    @Override
    public void setRead(Boolean read) {
        put(ReponseDossierListingConstants.IS_READ, read);
    }

    @Override
    public void setRoutingTaskType(String routingTaskType) {
        put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, routingTaskType);
    }

    @Override
    public void setLegislature(String legilsature) {
        put(ReponseDossierListingConstants.LEGISLATURE, legilsature);
    }

    @Override
    public void setMinistereAttributaire(String ministereAttributaire) {
        put(ReponseDossierListingConstants.MINISTERE_ATTRIBUTAIRE, ministereAttributaire);
    }

    @Override
    public void setDirectionRunningStep(String directionRunningStep) {
        put(ReponseDossierListingConstants.DIRECTION_RUNNING_STEP, directionRunningStep);
    }

    @Override
    public String getErrata() {
        return getString(ReponseDossierListingConstants.HAS_ERRATA);
    }

    @Override
    public void setErrata(String hasErrata) {
        put(ReponseDossierListingConstants.HAS_ERRATA, hasErrata);        
    }
    
    @Override
    public String getRedemarre() {
        return getString(ReponseDossierListingConstants.HAS_REDEMARRE);
    }
    
    @Override
    public void setRedemarre(String hasRedemarre) {
        put(ReponseDossierListingConstants.HAS_REDEMARRE, hasRedemarre);        
    }

	@Override
	public String getDirecteur() {
		return getString(ReponseDossierListingConstants.IS_DIRECTEUR);
	}

	@Override
	public void setDirecteur(String isDirecteur) {
		put(ReponseDossierListingConstants.IS_DIRECTEUR, isDirecteur);
	}
	
}
