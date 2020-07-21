package fr.dila.reponses.api.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.service.STExportService;

/**
 * Service gerant les exports de document asynchrones
 * Permet la création de la racine des documents ExportStat ainsi que la gestion de ces documents
 *
 */
public interface ReponsesExportService extends STExportService {

    /**
     * indique si l'utilisateur a déjà une demande d'export stats en cours
     * @param session
     * @param currentUser
     * @return
     * @throws ClientException
     */
    boolean isCurrentlyExportingStat(CoreSession session, SSPrincipal currentUser, String userWorkspacePath) throws ClientException;
    
    /**
     * Modifie le document d'exportStat pour indiquer qu'une demande d'export est en cours
     * @param session
     * @param user
     * @return
     * @throws ClientException 
     */
    boolean flagInitExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath) throws ClientException;
    
    /**
     * Modifie le document d'exportStat pour indiquer que la demande d'export est terminée
     * @param session
     * @param user
     * @throws ClientException 
     */
    void flagEndExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath) throws ClientException;

    /**
     * récupère la date de la dernière demande d'export statistiques pour l'utilisateur au format dd/MM/yyyy hh:mm
     * @param session
     * @param currentUser
     * @return
     * @throws ClientException 
     */
    String getExportStatHorodatageRequest(CoreSession session, SSPrincipal currentUser, String userWorkspacePath) throws ClientException;
    
    /**
     * Récupère ou créé le documentModel du document d'exportStat de l'utilisateur
     * @param session
     * @param user
     * @param userWorkspacePath
     * @return
     * @throws ClientException 
     */
    DocumentModel getOrCreateExportStatDoc(CoreSession session, SSPrincipal user, String userWorkspacePath) throws ClientException;
    
    /**
     * Récupère le documentModel du document d'exportStat de l'utilisateur <br>
     * Null si inexistant
     * @param session
     * @param userPrincipal
     * @return
     * @throws ClientException 
     */
    DocumentModel getExportStatDocForUser(CoreSession session, SSPrincipal userPrincipal, String userWorkspacePath) throws ClientException;
    
    /**
     * Retourne le chemin du répertoire racine pour le stockage des documents exportStats
     * @param session
     * @param userWorkspacePath
     * @param folderExportId
     * @param folderExportName
     * @return
     * @throws ClientException
     */
    String getOrCreateExportStatRootPath(CoreSession session, String userWorkspacePath) throws ClientException;

    /**
     * Retourne le répertoire racine pour le stockage des documents exportStats
     * @param session
     * @param userWorkspacePath
     * @param folderExportId
     * @param folderExportName
     * @return
     * @throws ClientException
     */
    DocumentModel getOrCreateExportStatRootDoc(CoreSession session, String userWorkspacePath) throws ClientException;
    
    /**
     * Créé l'archve des stats 
     * @param session
     * @param userWorkspacePath
     * @param user
     * @param reportsMultiExportMap map <UID, String> avec UID rapport birt et "MIN" ou "DIR" si l'export est multi (vide sinon)
     * @param reportsNamesMap map <UID, String> avec UID rapport birt et name du rapport
     * @param reportsTitlesMap map <UID, String> avec UID rapport birt et title du rapport
     * @param formats
     * @throws IOException 
     * @throws ClientException 
     */
    void exportStat(CoreSession session, String userWorkspacePath, SSPrincipal userPrincipal, Map<String, String> reportsMultiExportMap, Map<String, String> reportsNamesMap,
            Map<String, String> reportsTitlesMap, List<String> formats) throws ClientException, IOException;

    /**
     * Supprime les documents d'export stat dont la dateRequest > dateLimit
     * @param session
     * @param dateLimit
     * @return nombre de documents supprimés
     * @throws ClientException 
     */
    int removeOldExportStat(CoreSession session, Calendar dateLimit) throws ClientException;
}
