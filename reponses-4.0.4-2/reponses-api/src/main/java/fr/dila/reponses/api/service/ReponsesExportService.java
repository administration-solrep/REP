package fr.dila.reponses.api.service;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.service.STExportService;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
     *
     */
    boolean isCurrentlyExportingStat(CoreSession session, SSPrincipal currentUser, String userWorkspacePath);

    /**
     * Modifie le document d'exportStat pour indiquer qu'une demande d'export est en cours
     * @param session
     * @param user
     * @return
     *
     */
    boolean flagInitExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath);

    /**
     * Modifie le document d'exportStat pour indiquer que la demande d'export est terminée
     * @param session
     * @param user
     *
     */
    void flagEndExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath);

    /**
     * récupère la date de la dernière demande d'export statistiques pour l'utilisateur au format dd/MM/yyyy hh:mm
     * @param session
     * @param currentUser
     * @return
     *
     */
    String getExportStatHorodatageRequest(CoreSession session, SSPrincipal currentUser, String userWorkspacePath);

    /**
     * Récupère ou créé le documentModel du document d'exportStat de l'utilisateur
     * @param session
     * @param user
     * @param userWorkspacePath
     * @return
     *
     */
    DocumentModel getOrCreateExportStatDoc(CoreSession session, SSPrincipal user, String userWorkspacePath);

    /**
     * Récupère le documentModel du document d'exportStat de l'utilisateur <br>
     * Null si inexistant
     * @param session
     * @param userPrincipal
     * @return
     *
     */
    DocumentModel getExportStatDocForUser(CoreSession session, SSPrincipal userPrincipal, String userWorkspacePath);

    /**
     * Retourne le chemin du répertoire racine pour le stockage des documents exportStats
     * @param session
     * @param userWorkspacePath
     * @param folderExportId
     * @param folderExportName
     * @return
     *
     */
    String getOrCreateExportStatRootPath(CoreSession session, String userWorkspacePath);

    /**
     * Retourne le répertoire racine pour le stockage des documents exportStats
     * @param session
     * @param userWorkspacePath
     * @param folderExportId
     * @param folderExportName
     * @return
     *
     */
    DocumentModel getOrCreateExportStatRootDoc(CoreSession session, String userWorkspacePath);

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
     *
     */
    void exportStat(
        CoreSession session,
        String userWorkspacePath,
        SSPrincipal userPrincipal,
        Map<String, String> reportsMultiExportMap,
        Map<String, String> reportsNamesMap,
        Map<String, String> reportsTitlesMap,
        List<BirtOutputFormat> formats
    )
        throws IOException;

    /**
     * Supprime les documents d'export stat dont la dateRequest > dateLimit
     * @param session
     * @param dateLimit
     * @return nombre de documents supprimés
     *
     */
    int removeOldExportStat(CoreSession session, Calendar dateLimit);
}
