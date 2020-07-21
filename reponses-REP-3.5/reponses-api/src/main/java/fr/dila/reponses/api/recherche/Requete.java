package fr.dila.reponses.api.recherche;

	import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.st.api.recherche.RequeteTraitement;

	/**
	 * 
	 * @author JGZ
	 * Requete : une requête est l'objet qui contient les données pour effectuer une recherche.
	 *
	 */

	public interface Requete extends Serializable{
		
	    /**
	     * Une énumération pour donner les différents mode d'indexation possible :
	     *  INDEX_ORIG : indexation d'origine des dossiers.
	     *  INDEX_COMPL : l'indexation utilisateur
	     *  TOUS : les 2 modes d'indexation
	     */
	    public static enum INDEX_MODE {
	        TOUS(RequeteConstants.PART_REQUETE_INDEX_TOUS),
	        INDEX_ORIG(RequeteConstants.PART_REQUETE_INDEX_ORIGINE),
	        INDEX_COMPL(RequeteConstants.PART_REQUETE_INDEX_COMPL);
	        
	        private final String queryModelName;   
	        
	        INDEX_MODE(String modelName){
	            this.queryModelName = modelName;
	        }
	        
	        public String getQueryModelName() {
	            return this.queryModelName;
	        }
	    }
	    
	    public DocumentModel getDocument();
	    
	    public void setDocument(DocumentModel doc);
		
		/**
		 * Ajoute une entrée à une des listes gérant les vocabulaires associés à cette requête
		 * @param voc
		 * @param label
		 * @throws ClientException
		 */
		public void addVocEntry(String voc, String label) throws ClientException;
		
		/**
		 * enlève une entrée à une des listes gérant les vocabulaires associés à cette requête
		 * @param voc
		 * @param label
		 * @throws ClientException
		 */
		public void removeVocEntry(String voc, String label) throws ClientException;
		
		/**
		 * 
		 * @param string
		 * @return
		 * @throws ClientException
		 */
		public List<String[]> getListIndexByZone(String string) throws ClientException;
		
		
		/** Getter/setter**/
		
		public Calendar getDateQuestionDebut();

		public Calendar getDateQuestionFin();
		
		public Calendar getDateSignalementDebut();

        public Calendar getDateSignalementFin();
		
        public void setNumeroRange(int debut, int fin) throws ClientException;

        public void setDateRange(Calendar dateDebut, Calendar dateFin);
        
        public void setDateRangeReponse(Calendar debut,Calendar fin);
        
        public void setGroupePolitique(String groupePolitique);
        
        public String getGroupePolitique();
        
        public void setChampRequeteSimple(String critereRecherche);

        public void setOrigineQuestion(List<String>  stringList);
        
        public void setCaracteristiqueQuestion(List<String>  caracteristiqueList);

        public void setTypeQuestion(List<String> types);
        
        public void setDansTexteQuestion(Boolean value);

        public void setDansTitre(Boolean value);

        public void setDansTexteReponse(Boolean value);

        public void setCritereRechercheIntegral(String string);

        public String getSubClause();

        public void setLegislature(String string);

        public String getLegislature();
        
        public String getNomAuteur();
        
        public void setNomAuteur(String nomAuteur);

        public void setDateQuestionDebut(Calendar dateDebut);
        
        public void setDateQuestionFin(Calendar dateFin);
        
        public void setDateSignalementDebut(Calendar dateDebut);
        
        public void setDateSignalementFin(Calendar dateFin);

        public Calendar getDateReponseDebut();

        public void setDateReponseDebut(Calendar dateDebut);

        public void setDateReponseFin(Calendar dateFin);

        public void setNumeroQuestionDebut(int debut);

        public void setNumeroQuestionFin(int fin);

        void setSubClause(String clause);

        public String getCritereRechercheIntegral();

        public Boolean getDansTexteReponse();

        public Boolean getDansTitre();

        public Boolean getDansTexteQuestion();
        
        /**
         * Mettre à vrai pour effectuer une recherche de manière exact
         * @param rechercheExacte
         */
        public void setAppliquerRechercheExacte(Boolean rechercheExacte);
        
        /**
         * Retourne vrai si la recherche exacte est appliquée, 
         * faux sinon
         * @return
         */
        public Boolean getAppliquerRechercheExacte();

        public String getQueryType();

        public void setQueryType(String type);

        public void setEtat(String property_etat, Boolean value);

        public Boolean getEtat(String etat);

        public String getChampRequeteSimple();

        public String getClauseChampRequeteSimple();

        public void setClauseChampRequeteSimple(String clause);

        /**
         * Donne la valeur du mode d'indexation pour cette requête : 
         *  - INDEX_ORIG pour l'indexation d'origine
         *  - INDEX_COMPL pour l'indexation complémentaire
         *  - TOUS pour les 2 modes d'indexation
         * @return le mode d'indexation
         */
        public INDEX_MODE getIndexationMode();
        
        /**
         * Positionne la valeur du mode d'indexation
         * @return
         */
        public void setIndexationMode(INDEX_MODE indexationMode);

        void setMinistereInterroge(String ministereInterroge);

        public String getMinistereInterroge();
        
        void setMinistereRattachement(String ministereRattachement) ;

        String getMinistereRattachement() ;
        
        void setDirectionPilote(String directionPilote) ;

        String getDirectionPilote() ;
        /**
         * Retourne vrai si une des recherches du texte intégral est selectionnée
         * @return
         */
        public Boolean hasTextRechercheSelected();

        /**
         * Met en place le fragment OR pour l'état retiré ou non retiré
         * @param clause
         */
        public void setClauseEtatRetireOuNonRetire(String clause);

        /**
         * Retourne le la clause qui donne la conjonction des états rétirés et non retirés
         * @return
         */
        public String getClauseEtatRetireOuNonRetire();
        
        public RequeteTraitement<Requete> getTraitement();
        
        public void setTraitement(RequeteTraitement<Requete> traitement);
        
        public void init();
        
        
        public void doBeforeQuery();

        /**
         * Met en place la recherche des dossiers caduques
         * @param etatCaduque
         */
        public void setEtatCaduque(Boolean etatCaduque);

        /**
         * Retourne vrai si la recherche des dossiers clos est en place
         */
        public Boolean getEtatCaduque();

        /**
         *  Met en place la recherche des dossiers en état cloture autre
         * @param etatClotureAutre
         */
        public void setEtatClotureAutre(Boolean etatClotureAutre);

        /**
         * Retourne vrai si la recherche des dossiers cloture autre est en place
         */
        public Boolean getEtatClotureAutre();

        /**
         * Met en place la liste des états de la question sur lesquelle la recherche porte
         * @param etats
         */
        public void setEtatQuestionList(List<String> etats);

        /***
         * Retourne la liste des états de la question sur lesquelle la recherche porte
         * @return
         */
        public List<String> getEtatQuestionList();

        /**
         * Retourne la liste des valeurs cochées par l'utilisateur concernant les réponses publiées ou non.
         * @return
         */
        public List<String> getCaracteristiqueQuestion();
        
        
        /**
         * Retourne la clause concernant les réponses publiées ou non
         * @return
         */
        public String getClauseCaracteristiques();
        
        /**
         * Met en place le paramêtre clause comme clause des réponses publiées ou non.
         * @param clause
         * @return
         */
        public void setClauseCaracteristiques(String clause);
        
        /**
         * Place l'identifiant de la mailbox à rechercher sur les étapes
         * @param mailboxIdFromPoste
         */
        public void setEtapeDistributionMailboxId(String mailboxIdFromPoste);
        
        /**
         * Retourne l'identificant de la direction à rechercher sur les étapes/
         * @return
         */
        public String getEtapeIdDirection();

        /**
         * Retourne l'identificant de poste à rechercher sur les étapes/
         * @return
         */
        public String getEtapeIdPoste();

        /**
         * Retourne le statut d'une étape. 
         * Attention ! Il s'agit du statut de recherche, qui regroupe des informations relatives à l'identifiant de validation de l'étape et le cycle de vie d'une étape.
         * @return
         */
        public String getEtapeIdStatut();

        /**
         * Recherche sur le cycle de vie d'une étape (en cours, à venir, terminé)
         * @param object
         */
        public void setEtapeCurrentCycleState(String state);

        /**
         * Recherche sur le statut de validation d'une étape. (pour le cycle de vie à validated : savoir si la sortie est non concerné, validé automatiquement, etc ...) 
         * @param object
         */
        public void setValidationStatutId(String validationStatutId);
        
        
        /**
         * Positionne le titre de la feuille de route pour effectuer une recherche sur ce champ sur les instances de feuille de route.
         * @param fdrTitre
         */
        public void setTitreFeuilleRoute(String fdrTitre);
        
        /**
         * Recherche sur la date de début d'étape - intervalle de départ
         */
        public void setEtapeDateActivation(Calendar date);
        
        /**
         * Retourne la date de début d'étape - intervalle de départ
         */
        public Calendar getEtapeDateActivation();
        
        /**
         * Recherche sur la date de début d'étape - intervalle de fin
         */
        public void setEtapeDateActivation_2(Calendar date);
        
        /**
         * Retourne la date de début d'étape - intervalle de fin
         */
        public Calendar getEtapeDateActivation_2();
        
        /**
         * Recherche sur la date de fin d'étape - intervalle de départ
         */
        public void setEtapeDateValidation(Calendar date);
        
        /**
         * Retourne la date de fin d'étape - intervalle de départ
         */
        public Calendar getEtapeDateValidation();
        
        /**
         * Recherche sur la date de fin d'étape - intervalle de fin
         */
        public void setEtapeDateValidation_2(Calendar dateFin);
        
        /**
         * Retourne la date de fin d'étape - intervalle de fin
         */
        public Calendar getEtapeDateValidation_2();
        
}

