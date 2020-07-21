package fr.dila.reponses.web.requeteur;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.smart.folder.jsf.SmartNXQLFolderActions;
import org.nuxeo.ecm.platform.smart.query.SmartQuery;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.web.suivi.SuiviActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STAclConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.schema.SmartFolderSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.converter.DateClauseConverter;
import fr.dila.st.web.converter.OrganigrammeMinIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeMultiIdToLabelConverter;
import fr.dila.st.web.converter.VocabularyIdsConverter;

/**
 * Le bean seam chargé de gérer les actions du requêteur dans suivi.
 * 
 * @author admin
 * 
 */
@Name("requeteurActions")
@Scope(ScopeType.CONVERSATION)
public class RequeteurActionsBean implements Serializable {

    private static final String SAUVEGARDER_REQUETE_EXPERTE_VIEW = "sauvegarder_requete_experte";
    
    private static final String RENOMMER_REQUETE_EXPERTE_VIEW = "renommer_requete_experte";

    private static final long serialVersionUID = 1L;

    private static final Log LOGGER = LogFactory.getLog(RequeteurActionsBean.class);

    private static final String VIEW_RESULTATS_REQUETEUR = "view_saved_requete_results";

    private static final String SMART_FOLDER = STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE;

    private static final String MESREQUETES_PATH = "/mesrequetes/";

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = true)
    protected transient SmartNXQLFolderActions smartNXQLFolderActions;

    @In(create = true, required = true)
    protected transient SuiviActionsBean suiviActions;

    @In(create = true, required = true)
    protected transient ReponsesSmartNXQLQueryActions smartNXQLQueryActions;

    @In(create = true, required = true)
    protected ContentViewActions contentViewActions;

    @In(create = true, required = true)
    protected DocumentActions documentActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = true)
    protected transient ActionManager actionManager;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    @In(create = true, required = false)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    private DocumentModel currentSmartFolder;

    /**
     * Sauvegarde un document à partir des valeurs mise dans le bean seam smartQueryActions
     * 
     * @param docType
     * @return
     */
    public String saveQueryAsRequeteExperte() {
        String result = StringUtils.EMPTY;
        try {
            navigateToUserRequeteRoot();
            DocumentModel doc = documentManager.createDocumentModel(SMART_FOLDER);
            navigationContext.setChangeableDocument(doc);
            RequeteExperte requeteExperte = doc.getAdapter(RequeteExperte.class);
            String queryPart = smartNXQLQueryActions.getQueryPart();
            requeteExperte.setWhereClause(queryPart);
            this.setCurrentSmartFolder(navigationContext.getChangeableDocument());
            result = SAUVEGARDER_REQUETE_EXPERTE_VIEW;
        } catch (Exception e) {
            LOGGER.error("Erreur ! Le requêteur a été incapable de sauvegarder à jour la requête ", e);
            facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être sauvegardée, veuillez recommencer.");
        }
        return result;
    }

    /**
     * 
     * Met à jour une requête à partir des valeurs mise dans le bean seam smartQueryActions
     * 
     * @param docType
     * @return
     */
    public String updateRequeteExperte() {
        String requeteResults = StringUtils.EMPTY;
        try {
            DocumentModel doc = navigationContext.getCurrentDocument();
            RequeteExperte requeteExperte = doc.getAdapter(RequeteExperte.class);
            String queryPart = smartNXQLQueryActions.getQueryPart();
            requeteExperte.setWhereClause(queryPart);
            documentActions.updateDocument();
            this.setCurrentSmartFolder(doc);
            requeteResults = goToResults();
        } catch (Exception e) {
            LOGGER.error("Erreur ! Le requêteur a été incapable de mettre à jour la requête ", e);
            facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être mise à jour, veuillez recommencer.");
        }
        return requeteResults;
    }

    /**
     * Edite la requête
     * 
     * @param doc le documentModel que l'on veut editer
     * @return
     * @throws ClientException
     */
    public String editRequete(DocumentModel doc) {
        String retourToSuivi = StringUtils.EMPTY;
        try {
            suiviActions.setCurrentSuiviSubcontainerAction("create_requeteur");
            String queryPart = SmartFolderSchemaUtils.getQueryPart(doc);
            smartNXQLQueryActions.initCurrentSmartQuery(queryPart, true);
            smartNXQLQueryActions.setQueryPart(queryPart);
            SmartQuery query = smartNXQLQueryActions.getCurrentSmartQuery();
            query.buildQuery();
            smartNXQLQueryActions.updateQuery();
            this.setCurrentSmartFolder(doc);
            contentViewActions.reset("recherche_requeteur");
            retourToSuivi = suiviActions.initializeSuivi();
        } catch (Exception e) {
            LOGGER.error("Erreur ! Le requêteur a été incapable d'éditer la requête " + doc.getName(), e);
            facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être édité, veuillez recommencer.");
        }
        return retourToSuivi;
    }  
    

    /**
     * Edite la requête courante.
     * 
     * @return
     */
    public String editCurrentRequete() {
        if (currentSmartFolder == null) {
            // On essaie de prendre le document en train d'être crée
            DocumentModel changeableDoc = navigationContext.getChangeableDocument();
            if (changeableDoc != null && SMART_FOLDER.equals(changeableDoc.getType())) {
                this.setCurrentSmartFolder(changeableDoc);
            } else {
                LOGGER.warn("Attention ! Le requêteur a enregistré une requête courante qui est null ou pas de type SmartFolder");
                // facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être édité, veuillez recommencer.");
                return suiviActions.initializeSuivi();
            }
        }
        // Verification du type
        if (STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE.equals(currentSmartFolder.getType())) {
            return editRequete(currentSmartFolder);
        }
        // Sinon, c'est un bug
        else {
            LOGGER.error("Attention ! Le requêteur a enregistré une requête courante qui n'est pas de type SmartFolder");
            facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être édité, veuillez recommencer.");
            return suiviActions.initializeSuivi();
        }
    }
    
    /**
     * Renomme la requête
     * 
     * @param doc le documentModel que l'on veut editer
     * @return
     * @throws ClientException
     */
    public String renameRequete(DocumentModel doc) {
            String result = StringUtils.EMPTY;
            try {                
                navigationContext.setCurrentDocument(doc);
                this.setCurrentSmartFolder(navigationContext.getCurrentDocument());
                result = RENOMMER_REQUETE_EXPERTE_VIEW;
            } catch (Exception e) {
                LOGGER.error("Erreur ! Le requêteur a été incapable de sauvegarder à jour la requête ", e);
                facesMessages.add(StatusMessage.Severity.ERROR, "Votre requête n'a pas pu être sauvegardée, veuillez recommencer.");
            }
            return result;  
    }
    
    /**
     * Edite le document de la requête et renvoie vers sa page de résultats
     * 
     * @return
     * @throws ClientException
     */
    public String editDocAndGoToResults() {
        String requeteResults = StringUtils.EMPTY;
        try {
            DocumentModel doc = navigationContext.getCurrentDocument();
            if(doc != null) {
                documentManager.saveDocument(doc);
            }else {
                LOGGER.error("Le requêteur n'a pas été capable de renommer la requête");
                facesMessages.add("Le requêteur n'a pas pu renommer votre requête");
            }
            requeteResults = goToResults();
        } catch (ClientException e) {
            LOGGER.error("Le requêteur n'a pas été capable de réaliser la requête");
            facesMessages.add("Le requêteur n'a pas pu réaliser votre requête");
        }
        return requeteResults;
    }
    
    public DocumentModel getCurrentDocument() {
        return navigationContext.getCurrentDocument();
    }

    public String goToResults() {
        DocumentModel currentRequete = navigationContext.getCurrentDocument();
        RequeteExperte requeteExperte = null;
        if (currentRequete == null) {
            return StringUtils.EMPTY;
        } else {
            try {
                requeteExperte = currentRequete.getAdapter(RequeteExperte.class);
                String queryPart = requeteExperte.getWhereClause();
                smartNXQLQueryActions.initCurrentSmartQuery(queryPart, true);
                smartNXQLQueryActions.setQueryPart(queryPart);
                SmartQuery query = smartNXQLQueryActions.getCurrentSmartQuery();
                query.buildQuery();
                smartNXQLQueryActions.updateQuery();
                this.setCurrentSmartFolder(currentRequete);
                contentViewActions.reset("recherche_requeteur_sauvegarde");
                return VIEW_RESULTATS_REQUETEUR;
            } catch (Exception e) {
                LOGGER.error("La requete experte n'a pas pu afficher ses résultats", e);
                facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("requeteExperte.erreur.affichage"));
            }
        }
        return suiviActions.initializeSuivi();
    }

    public String viewResults(DocumentModel doc) throws ClientException {
        if (doc == null) {
            return "";
        }
        navigationContext.setCurrentDocument(doc);
        this.setCurrentSmartFolder(doc);
        return VIEW_RESULTATS_REQUETEUR;
    }

    /**
     * Crée la requête et renvoie vers sa page de résultats
     * 
     * @return
     * @throws ClientException
     */
    public String createAndGoToResults() {
        String requeteResults = StringUtils.EMPTY;
        try {
            documentActions.saveDocument();
            
            DocumentModel doc = navigationContext.getCurrentDocument();
            SecurityService service = STServiceLocator.getSecurityService();
            service.addAceToAcl(doc, ACL.LOCAL_ACL, ssPrincipal.getName(), SecurityConstants.EVERYTHING);
            
            requeteResults = goToResults();
        } catch (Exception e) {
            LOGGER.error("Le requêteur n'a pas été capable de créer la requête");
            facesMessages.add("Le requêteur n'a pas pu créer votre requête");
        }
        return requeteResults;
    }

    /**
     * Retourne le converter approprié à partir du searchField
     * 
     * @param searchField
     * @return
     */
    public Converter getConverter(String searchField) {
        Map<String, Converter> converterMap = new HashMap<String, Converter>();
        converterMap.put("q.qu:datePublicationJO", new DateClauseConverter());
        converterMap.put("q.qu:typeQuestion", new VocabularyIdsConverter("type_question"));
        converterMap.put("q.ixa:SE_theme", new VocabularyIdsConverter("SE_theme"));
        converterMap.put("q.ixa:SE_rubriques", new VocabularyIdsConverter("SE_rubrique"));
        converterMap.put("q.ixa:AN_rubrique", new VocabularyIdsConverter("AN_rubrique"));
        converterMap.put("q.ixa:AN_analyse", new VocabularyIdsConverter("AN_analyse"));
        converterMap.put("q.ixa:TA_rubrique", new VocabularyIdsConverter("TA_rubrique"));
        converterMap.put("e2.rtsk:type", new VocabularyIdsConverter("cm_routing_task_type", true));
        converterMap.put("e2.rtsk:dateDebutEtape", new DateClauseConverter());
        converterMap.put("e2.rtsk:dateFinEtape", new DateClauseConverter());
        converterMap.put("e2.rtsk:validationStatus", new VocabularyIdsConverter("validation_statut_etape"));
        converterMap.put("e2.rtsk:distributionMailboxId", new OrganigrammeMultiIdToLabelConverter());
        converterMap.put("e2.ecm:currentLifeCycleState", new VocabularyIdsConverter("etat_etape"));
        converterMap.put("r.rep:datePublicationJOReponse", new DateClauseConverter());
        converterMap.put("d.dos:ministereAttributaireCourant", new OrganigrammeMinIdToLabelConverter());
        // converterMap.put("q.qu:idMinistereInterroge",new OrganigrammeMinIdConverter());
        converterMap.put("q.qu:etatRappele", new VocabularyIdsConverter("boolean_requeteur_voc"));
        converterMap.put("q.qu:etatSignale", new VocabularyIdsConverter("boolean_requeteur_voc"));
        converterMap.put("q.qu:etatRenouvele", new VocabularyIdsConverter("boolean_requeteur_voc"));
        if (!converterMap.containsKey(searchField)) {
            return null;
        } else {
            return converterMap.get(searchField);
        }
    }

    public String getFullQuery(String where) {
        JointureService jointureService = STServiceLocator.getJointureService();
        QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(where);
        return assembler.getFullQuery();
    }

    /**
     * Retourne la requête complête, à partir du document courant, ou d'une requête sauvegardée en tant que currentSmartFolder.
     * 
     * @return
     * @throws ClientException 
     * 
     **/
    public String getFullQuery() throws ClientException {
        if (navigationContext.getCurrentDocument() != null && SMART_FOLDER.equals(navigationContext.getCurrentDocument().getType())) {
            setCurrentSmartFolder(navigationContext.getCurrentDocument());
        }
        if (this.getCurrentSmartFolder() == null) {
            return StringUtils.EMPTY;
        }
        RequeteExperte req = this.getCurrentSmartFolder().getAdapter(RequeteExperte.class);
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        String query = requeteurService.getPattern(documentManager, req);
        
        // Remplacement du paramètre de la législature courante
        if (query.contains(":legislatureCourante")) {
            String legislatureCourante = STServiceLocator.getSTParametreService()
                    .getParametreValue(documentManager, ReponsesParametreConstant.LEGISLATURE_COURANTE);
            query = query.replace(":legislatureCourante", legislatureCourante);
        }
        
        return query;
    }

    /**
     * Retourne la requête courante
     * 
     * @return la requête courante
     */
    public DocumentModel getCurrentSmartFolder() {
        return currentSmartFolder;
    }

    /**
     * Positionne la requête courante
     * 
     * @param doc
     */
    public void setCurrentSmartFolder(DocumentModel doc) {
        currentSmartFolder = doc;
    }

    /**
     * Publie la requête dans l'espace utilisateur.
     * 
     * @return
     */
    public void publish() {
        DocumentModel doc = navigationContext.getCurrentDocument();
        try {
            SecurityService service = STServiceLocator.getSecurityService();
            service.addAceToSecurityAcl(doc, SecurityConstants.EVERYONE, SecurityConstants.READ);
            doc.followTransition("approve");
        } catch (Exception e) {
            LOGGER.error("La publication de la requete dans l'espace public a echoué", e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("erreur.requeteExperte.publication.failed"));
            return;
        }
        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("info.requeteExperte.publication.succeeded"));
    }

    /**
     * Annule le passage à la publication de la requête
     * 
     * @return
     */
    public void unpublish() {
        DocumentModel doc = navigationContext.getCurrentDocument();
        try {
            ACP acp = doc.getACP();
            acp.removeACL(STAclConstant.ACL_SECURITY);
            doc.followTransition("backToProject");
        } catch (Exception e) {
            LOGGER.error("La publication de la requete dans l'espace public a echoué", e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("erreur.requeteExperte.publication.failed"));
            return;
        }
        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("info.requeteExperte.unpublish.succeeded"));
    }

    /**
     * Retourne vrai si la requête est rendu publique
     * 
     * @param doc la requête que l'on veut publier
     * @return
     * @throws ClientException
     */
    private Boolean isPublished(DocumentModel doc) throws ClientException {
        return "project".equals(doc.getCurrentLifeCycleState());
    }

    /**
     * Annule le passage à la publication de la requête
     * 
     * @return
     * @throws ClientException
     */
    public Action getPublishAction(DocumentModel doc) {

        try {
            if (isPublished(doc)) {
                return actionManager.getAction("publish_requete");
            } else {
                return actionManager.getAction("unpublish_requete");
            }
        } catch (Exception e) {
            LOGGER.error("Pas d'action de publication, erreur non attendue", e);
            return null;
        }
    }

    /**
     * Retourne le document qui contient toutes les requêtes préparamêtrées
     * 
     * @return la racine de toutes les requêtes sauvegardées
     * @throws ClientException
     */
    public DocumentModel getBibliothequeStandard() throws ClientException {
        PathRef biblioPath = new PathRef(RequeteConstants.BIBLIO_REQUETES_ROOT);
        return documentManager.getDocument(biblioPath);
    }
    public String getBibliothequeStandardId() throws ClientException {
    	return getBibliothequeStandard().getId();
    }
    
    public String getUserBibliothequeId() throws ClientException {
    	// SPL : on ne va pas chercher le répertoire mes-requetes car les requetes ne sont enregistrées dedans.
    	return userWorkspaceManagerActions.getCurrentUserPersonalWorkspace().getId();
    }

    /**
     * Retourne vrai si l'utilisateur courant peut publier des requêtes.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canPublish(DocumentModel doc) throws ClientException {
        final List<String> groups = ssPrincipal.getGroups();
        boolean canPublish = groups.contains(ReponsesBaseFunctionConstant.REQUETE_PUBLISHER);
        // Les requêtes crées par le systeme ne sont pas publiables :
        boolean isSystemRequete = STConstant.NUXEO_SYSTEM_USERNAME.equals(DublincoreSchemaUtils.getCreator(doc));
        return canPublish && !isSystemRequete;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut supprimer la requête.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canDelete(DocumentModel doc) throws ClientException {
        if (doc == null) {
            return false;
        } else {
            return documentManager.hasPermission(doc.getRef(), SecurityConstants.REMOVE);
        }
    }

    /**
     * Retourne vrai si l'utilisateur courant peut éditer la requête
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canEdit(DocumentModel doc) throws ClientException {
        if (doc == null) {
            return false;
        } else {
            return documentManager.hasPermission(doc.getRef(), SecurityConstants.WRITE);
        }
    }
    
    /**
     * Retourne vrai si l'utilisateur courant peut éditer la requête courante
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canEditCurrentDoc() throws ClientException {
    	DocumentModel doc = navigationContext.getCurrentDocument();
        if (doc == null) {
            return false;
        } else {
            return documentManager.hasPermission(doc.getRef(), SecurityConstants.WRITE);
        }
    }

    /**
     * Va vers la racine des requêtes utilisateurs.
     * 
     * @throws ClientException
     */
    private void navigateToUserRequeteRoot() throws ClientException {
        String requeteRootPath = userWorkspaceManagerActions.getCurrentUserPersonalWorkspace().getPathAsString() + MESREQUETES_PATH;
        navigationContext.navigateToRef(new PathRef(requeteRootPath));
    }
}
