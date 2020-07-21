package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.constant.RequeteConstants.CARACTERISTIQUE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.CLAUSE_ETAT_RETIRE_OU_NON_RETIRE;
import static fr.dila.reponses.api.constant.RequeteConstants.CRITERE_RECHERCHE;
import static fr.dila.reponses.api.constant.RequeteConstants.CRITERE_RECHERCHE_CLAUSE;
import static fr.dila.reponses.api.constant.RequeteConstants.CRITERE_RECHERCHE_TXT_INTEGRAL;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TEXTE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TEXTE_REPONSE;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TITRE;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.GROUPE_POLITIQUE;
import static fr.dila.reponses.api.constant.RequeteConstants.LEGISLATURE;
import static fr.dila.reponses.api.constant.RequeteConstants.MINISTERE_INTERPELLE;
import static fr.dila.reponses.api.constant.RequeteConstants.NOM_AUTEUR;
import static fr.dila.reponses.api.constant.RequeteConstants.NUMERO_QUESTION_DEBUT;
import static fr.dila.reponses.api.constant.RequeteConstants.NUMERO_QUESTION_FIN;
import static fr.dila.reponses.api.constant.RequeteConstants.ORIGINE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_COMPLEXE_SCHEMA;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_METADONNEES_SCHEMA;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_SIMPLE_SCHEMA;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_TEXTE_INTEGRAL_SCHEMA;
import static fr.dila.reponses.api.constant.RequeteConstants.SUBCLAUSE;
import static fr.dila.reponses.api.constant.RequeteConstants.TYPE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.DOSSIER_ETAT_CADUQUE;
import static fr.dila.reponses.api.constant.RequeteConstants.DOSSIER_ETAT_CLOTURE_AUTRE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_QUESTION_LIST;
import static fr.dila.reponses.api.constant.RequeteConstants.CLAUSE_CARACTERISTIQUE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.APPLIQUER_RECHERCHE_EXACTE;
import static fr.dila.reponses.api.constant.RequeteConstants.*;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.recherche.traitement.GeneralRequeteTraitement;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STQueryConstant;
import fr.dila.st.api.recherche.RequeteTraitement;
import fr.dila.st.core.util.PropertyUtil;


/**
 * 
 * L'implementation d'une requete, c'est-à-dire l'objet créé pour effectuer une recherche
 * @author jgomez
 *
 */
public class RequeteImpl implements Requete{

    private static final long serialVersionUID = 6160682333116646611L;

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(RequeteImpl.class);

    protected DocumentModel document;
    protected ReponsesVocabularyService vocService;

    private RequeteTraitement<Requete> traitement;

    private String queryType = "NXQL";
    
    private INDEX_MODE indexationMode;

    
    public RequeteImpl(DocumentModel doc) {
    	super(); 
    	document = doc; 
    	this.indexationMode = Requete.INDEX_MODE.INDEX_ORIG;
        this.queryType = STQueryConstant.UFNXQL;
    	vocService = ReponsesServiceLocator.getVocabularyService();
    	setTraitement(new GeneralRequeteTraitement());
    }
  
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public void setDocument(DocumentModel doc) {
        this.document = doc;
    }
    

    @Override
    public void doBeforeQuery() {
        this.getTraitement().doBeforeQuery(this);
     // Pour les dates de fin d'intervalle, on ajoute un jour afin d'inclure
        // le dernier jour choisi par l'utilisateur dans la recherche
        Calendar dateAct2 = getEtapeDateActivation_2();
        Calendar dateValidation2 = getEtapeDateValidation_2();
        
        if (dateAct2 != null) {
            dateAct2.add(Calendar.HOUR_OF_DAY, 23);
            dateAct2.add(Calendar.MINUTE, 59);
            dateAct2.add(Calendar.SECOND, 59);
            setEtapeDateActivation_2(dateAct2);
        }
        
        if (dateValidation2 != null) {
            dateValidation2.add(Calendar.HOUR_OF_DAY,23); 
            dateValidation2.add(Calendar.MINUTE, 59);
            dateValidation2.add(Calendar.SECOND, 59);
            setEtapeDateValidation_2(dateValidation2);
        }        
    }
    
    @Override
    public void addVocEntry(String voc, String label) throws ClientException {
        @SuppressWarnings("unchecked")
        List<String> indexList = (List<String>) document.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,voc);
        indexList.add(label);
        document.setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, voc, indexList);
    }

    @Override
    public void removeVocEntry(String voc, String label) throws ClientException {
        @SuppressWarnings("unchecked")
        List<String> indexList = (List<String>) document.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,voc);
        indexList.remove(label);
        document.setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, voc, indexList);
    }

    // Test si la zone n'est pas contenue dans la map.
    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> getListIndexByZone(String zone) throws ClientException{
        if (vocService == null){
            return new ArrayList<String[]>();
        }
        List<String[]> libelleResultList = new ArrayList<String[]>();
        Map<String, List<String>> vocabularies = vocService.getMapVocabularyToZone();
        for (String vocabulary: vocabularies.get(zone)){
            List<String> indexList = (List<String>) document.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, vocabulary);
            for (String libelle: indexList){
                String[] libelleArray = new String[3];
                libelleArray[0] = vocabulary;
                libelleArray[1] = libelle;
                libelleArray[2] = "label.vocabulary." + vocabulary;
                libelleResultList.add(libelleArray);
            }
        }
    //  Collections.sort(libelleResultList
        return libelleResultList;
    }
	
	public void setNumeroRange(int debut,int fin){
	    setNumeroQuestionDebut(debut);
	    setNumeroQuestionFin(fin);
	    PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, NUMERO_QUESTION_FIN,fin);
    }
	    
    public void setDateRange(Calendar debut,Calendar fin){
        setDateQuestionDebut(debut);
        setDateQuestionFin(fin);
    }
    
    public void setDateRangeReponse(Calendar debut,Calendar fin){
        setDateReponseDebut(debut);
        setDateReponseFin(fin);
    }
    
    /** Getter/setters **/
    @Override
    public void setNumeroQuestionDebut(int debut){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, NUMERO_QUESTION_DEBUT,debut);
    }
    
    @Override
    public void setNumeroQuestionFin(int fin){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, NUMERO_QUESTION_FIN,fin);
    }
        
    @Override
    public Calendar getDateQuestionDebut(){
        return PropertyUtil.getCalendarProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_QUESTION_DEBUT);
    }
    
    @Override
    public void setDateQuestionDebut(Calendar dateDebut){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_QUESTION_DEBUT,dateDebut);
    }
    
    @Override
    public Calendar getDateSignalementDebut(){
        return PropertyUtil.getCalendarProperty(document, REQUETE_COMPLEXE_SCHEMA, DATE_SIGNALEMENT_DEBUT);
    }
    
    @Override
    public void setDateSignalementDebut(Calendar dateDebut){
        PropertyUtil.setProperty(document, REQUETE_COMPLEXE_SCHEMA, DATE_SIGNALEMENT_DEBUT,dateDebut);
    }
    
    @Override
    public Calendar getDateSignalementFin(){
        return PropertyUtil.getCalendarProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_SIGNALEMENT_FIN);
    }
    
    @Override
    public void setDateSignalementFin(Calendar dateDebut){
        PropertyUtil.setProperty(document, REQUETE_COMPLEXE_SCHEMA, DATE_SIGNALEMENT_FIN,dateDebut);
    }
    
    @Override
    public void setDateQuestionFin(Calendar dateFin){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_QUESTION_FIN,dateFin);
    }
    
    @Override
    public Calendar getDateReponseDebut(){
        return PropertyUtil.getCalendarProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_REPONSE_DEBUT);
    }
    
    @Override
    public void setDateReponseDebut(Calendar dateDebut){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_REPONSE_DEBUT,dateDebut);
    }
    
    @Override
    public void setDateReponseFin(Calendar dateFin){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_REPONSE_FIN,dateFin);
    }
    
    @Override
    public Calendar getDateQuestionFin(){
        return PropertyUtil.getCalendarProperty(document,REQUETE_COMPLEXE_SCHEMA, DATE_JO_QUESTION_FIN);
    }
    
    public void setGroupePolitique(String groupePolitique){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,GROUPE_POLITIQUE,groupePolitique);
    }
    
    public String getNomAuteur(){
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,NOM_AUTEUR);
    }
    
    public void setNomAuteur(String nomAuteur){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,NOM_AUTEUR, nomAuteur);
    }
    
    public String getGroupePolitique(){
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,GROUPE_POLITIQUE);
    }
    
    @Override
    public void setClauseChampRequeteSimple(String clause){
        PropertyUtil.setProperty(document,REQUETE_SIMPLE_SCHEMA,CRITERE_RECHERCHE_CLAUSE,clause);
    }
    
    @Override
    public String getClauseChampRequeteSimple(){
        return PropertyUtil.getStringProperty(document,REQUETE_SIMPLE_SCHEMA,CRITERE_RECHERCHE_CLAUSE);
    }
    
    @Override
    public void setChampRequeteSimple(String critereRecherche){
        PropertyUtil.setProperty(document,REQUETE_SIMPLE_SCHEMA,CRITERE_RECHERCHE,critereRecherche);
    }
    
    @Override
    public String getChampRequeteSimple(){
        return PropertyUtil.getStringProperty(document,REQUETE_SIMPLE_SCHEMA,CRITERE_RECHERCHE);
    }
    
    @Override
    public void setOrigineQuestion(List<String> origineList){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,ORIGINE_QUESTION,origineList);
    }
    
    @Override
    public void setCaracteristiqueQuestion(List<String> caracteristiqueList){
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,CARACTERISTIQUE_QUESTION,caracteristiqueList);
    }
    
    @Override
    public List<String> getCaracteristiqueQuestion(){
        return PropertyUtil.getStringListProperty(document,REQUETE_COMPLEXE_SCHEMA,CARACTERISTIQUE_QUESTION);
    }

    @Override
    public void setTypeQuestion(List<String> types) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,TYPE_QUESTION,types);
    }

    @Override
    public void setDansTexteQuestion(Boolean value) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TEXTE_QUESTION,value);
    }

    @Override
    public Boolean getDansTexteQuestion() {
        Boolean result = PropertyUtil.getBooleanProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TEXTE_QUESTION);
        return (result == null ? false : result);
    }
    
    @Override
    public void setDansTitre(Boolean value) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TITRE,value);
    }
    
    @Override
    public Boolean getDansTitre() {
        Boolean result = PropertyUtil.getBooleanProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TITRE);
        return (result == null ? false : result);
    }

    @Override
    public void setDansTexteReponse(Boolean value) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TEXTE_REPONSE,value);
    }
    
    @Override
    public Boolean getDansTexteReponse() {
        Boolean result = PropertyUtil.getBooleanProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,DANS_TEXTE_REPONSE);
        return (result == null ? false : result);
    }

    @Override
    public Boolean hasTextRechercheSelected() {
        return (getDansTitre() || getDansTexteQuestion() || getDansTexteReponse());
    }
    
    @Override
    public void setCritereRechercheIntegral(String critere) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,CRITERE_RECHERCHE_TXT_INTEGRAL,critere);
    }
    
    @Override
    public String getCritereRechercheIntegral() {
        return PropertyUtil.getStringProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,CRITERE_RECHERCHE_TXT_INTEGRAL);
    }
    
    @Override
    public void setSubClause(String clause) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,SUBCLAUSE,clause);
    }

    @Override
    public String getSubClause() {
        return PropertyUtil.getStringProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,SUBCLAUSE);
    }
    
    @Override
    public void setLegislature(String legislature) {
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,LEGISLATURE,legislature);
    }

    @Override
    public String getLegislature() {
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,LEGISLATURE);
    }
    
    @Override
    public void setMinistereInterroge(String ministereInterroge) {
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,MINISTERE_INTERPELLE,ministereInterroge);
    }

    @Override
    public String getMinistereInterroge() {
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,MINISTERE_INTERPELLE);
    }
    
    @Override
    public void setMinistereRattachement(String ministereRattachement) {
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,MINISTERE_RATTACHEMENT,ministereRattachement);
    }

    @Override
    public String getMinistereRattachement() {
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,MINISTERE_RATTACHEMENT);
    }
    
    @Override
    public void setDirectionPilote(String directionPilote) {
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,DIRECTION_PILOTE,directionPilote);
    }

    @Override
    public String getDirectionPilote() {
        return PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,DIRECTION_PILOTE);
    }

    @Override
    public String getQueryType() {
        return this.queryType ;
    }
    @Override
    public void setQueryType(String type) {
       this.queryType = type;
    }

    @Override
    public void setEtat(String property_etat, Boolean value) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,property_etat,value);
    }

    @Override
    public Boolean getEtat(String property_etat) {
        return PropertyUtil.getBooleanProperty(document,REQUETE_METADONNEES_SCHEMA,property_etat);
    }
    
    @Override
    public void setClauseEtatRetireOuNonRetire(String clause) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,CLAUSE_ETAT_RETIRE_OU_NON_RETIRE,clause);
    }

    @Override
    public String getClauseEtatRetireOuNonRetire() {
        return PropertyUtil.getStringProperty(document,REQUETE_METADONNEES_SCHEMA,CLAUSE_ETAT_RETIRE_OU_NON_RETIRE);
    }

    @Override
    public void setEtatCaduque(Boolean etatCaduque) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,DOSSIER_ETAT_CADUQUE,etatCaduque);
    }

    @Override
    public Boolean getEtatCaduque() {
        return PropertyUtil.getBooleanProperty(document,REQUETE_METADONNEES_SCHEMA,DOSSIER_ETAT_CADUQUE);
    }
    
    @Override
    public void setEtatClotureAutre(Boolean etatClotureAutre) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,DOSSIER_ETAT_CLOTURE_AUTRE,etatClotureAutre);
    }
    
    @Override
    public Boolean getEtatClotureAutre() {
        return PropertyUtil.getBooleanProperty(document,REQUETE_METADONNEES_SCHEMA,DOSSIER_ETAT_CLOTURE_AUTRE);
    }
    
    @Override
    public void setEtatQuestionList(List<String> etats) {
        PropertyUtil.setProperty(document,REQUETE_METADONNEES_SCHEMA,ETAT_QUESTION_LIST,etats);
    }
    
    @Override
    public List<String> getEtatQuestionList() {
        return PropertyUtil.getStringListProperty(document,REQUETE_METADONNEES_SCHEMA,ETAT_QUESTION_LIST);
    }
        
        
    @Override
    public INDEX_MODE getIndexationMode() {
        return indexationMode;
    }

    @Override
    public void setIndexationMode(INDEX_MODE indexationMode) {
    	this.indexationMode = indexationMode;
    }

    public List<Integer> getListEtatRetire() {
        List<Integer> etatRetires = new ArrayList<Integer>();
        Boolean etatRetire = this.getEtat(DossierConstants.DOSSIER_ETAT_RETIRE);
        Boolean etatNonRetire = this.getEtat(DossierConstants.DOSSIER_ETAT_NON_RETIRE);
        if ( etatRetire != null){
            etatRetires.add(0);
        }
        if ( etatNonRetire != null){
            etatRetires.add(1);
        }
        return etatRetires;
    }

    public void setTraitement(RequeteTraitement<Requete> traitement) {
        this.traitement = traitement;
    }

    public RequeteTraitement<Requete> getTraitement() {
        return traitement;
    }

    @Override
    public void init() {
        this.getTraitement().init(this);
    }

    @Override
    public String getClauseCaracteristiques() {
        return  PropertyUtil.getStringProperty(document,REQUETE_COMPLEXE_SCHEMA,CLAUSE_CARACTERISTIQUE_QUESTION);
    }

    @Override
    public void setClauseCaracteristiques(String clause) {
        PropertyUtil.setProperty(document,REQUETE_COMPLEXE_SCHEMA,CLAUSE_CARACTERISTIQUE_QUESTION,clause);
    }

    @Override
    public void setAppliquerRechercheExacte(Boolean rechercheExacte) {
        PropertyUtil.setProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,APPLIQUER_RECHERCHE_EXACTE,rechercheExacte);
    }

    @Override
    public Boolean getAppliquerRechercheExacte() {
        return PropertyUtil.getBooleanProperty(document,REQUETE_TEXTE_INTEGRAL_SCHEMA,APPLIQUER_RECHERCHE_EXACTE);
    }

    @Override
    public void setEtapeDistributionMailboxId(String mailboxId) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,DISTRIBUTION_MAILBOX_ID,mailboxId);
    }

    @Override
    public String getEtapeIdPoste() {
        return PropertyUtil.getStringProperty(document,REQUETE_FDR_SCHEMA,POSTE_ID);
    }

    @Override
    public String getEtapeIdDirection() {
        return PropertyUtil.getStringProperty(document,REQUETE_FDR_SCHEMA,DIRECTION_ID);
    }

    @Override
    public String getEtapeIdStatut() {
        return PropertyUtil.getStringProperty(document,REQUETE_FDR_SCHEMA,ETAPE_STATUT_ID);
    }

    @Override
    public void setEtapeCurrentCycleState(String state) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,CURRENT_LIFE_CYCLE_STATE,state);
    }

    @Override
    public void setValidationStatutId(String validationStatutId) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,VALIDATION_STATUT_ID,validationStatutId);
    }

    @Override
    public void setTitreFeuilleRoute(String fdrTitre) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,TITRE_FDR,fdrTitre);
    }

    @Override
    public void setEtapeDateActivation(Calendar dateDebut) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,DATE_DEBUT_ETAPE_DEBUT_INTERVALLE,dateDebut);
    }

    @Override
    public Calendar getEtapeDateActivation() {
        return PropertyUtil.getCalendarProperty(document,REQUETE_FDR_SCHEMA, DATE_DEBUT_ETAPE_DEBUT_INTERVALLE);
    }

    @Override
    public void setEtapeDateActivation_2(Calendar dateFin) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,DATE_DEBUT_ETAPE_FIN_INTERVALLE,dateFin);        
    }

    @Override
    public Calendar getEtapeDateActivation_2() {
        return PropertyUtil.getCalendarProperty(document,REQUETE_FDR_SCHEMA, DATE_DEBUT_ETAPE_FIN_INTERVALLE);
    }

    @Override
    public void setEtapeDateValidation(Calendar dateDebut) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,DATE_FIN_ETAPE_DEBUT_INTERVALLE,dateDebut);
    }

    @Override
    public Calendar getEtapeDateValidation() {
        return PropertyUtil.getCalendarProperty(document,REQUETE_FDR_SCHEMA, DATE_FIN_ETAPE_DEBUT_INTERVALLE);
    }

    @Override
    public void setEtapeDateValidation_2(Calendar dateFin) {
        PropertyUtil.setProperty(document,REQUETE_FDR_SCHEMA,DATE_FIN_ETAPE_FIN_INTERVALLE,dateFin);
    }

    @Override
    public Calendar getEtapeDateValidation_2() {
        return PropertyUtil.getCalendarProperty(document,REQUETE_FDR_SCHEMA, DATE_FIN_ETAPE_FIN_INTERVALLE);
    }

}
