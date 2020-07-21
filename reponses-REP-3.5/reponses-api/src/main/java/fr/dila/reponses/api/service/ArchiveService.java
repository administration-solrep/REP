package fr.dila.reponses.api.service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.ss.api.service.SSArchiveService;

/**
 * Interface du service d'archive
 * 
 * @author bgamard
 *
 */
public interface ArchiveService extends SSArchiveService, Serializable {

    /**
     * Prépare à l'archivage une liste de dossiers et retourne les dossiers en erreur
     * @param session
     * @param docs liste de dossier à marquer "préparer archivage"
     * @throws ClientException
     */
    List<Dossier> ajouterDossiersListeElimination(CoreSession session, List<DocumentModel> docs) throws ClientException;
    
    /**
     * Retourne le nombre de question présente dans l'application qui sont archivables
     * par rapport à la date du jour et au delai de conservation
     * @param session
     * @return
     * @throws ClientException
     */
    Long countQuestionArchivable(CoreSession session) throws ClientException;

    /**
     * Generate birt pdf
     * 
     * @param session
     * @param outputStream
     * @param questionId
     * @param dossierDoc
     * @throws Exception
     */
    void generateBirtPdf(CoreSession session, OutputStream outputStream, String questionId, DocumentModel dossierDoc) throws Exception;

    /**
     * Retourne la liste des dossiers d'une liste d'élimination
     * @param documentManager
     * @return
     * @throws ClientException 
     */
    List<DocumentModel> getDossiersFromListeElimination(CoreSession session, DocumentModel listeDoc) throws ClientException;

    /**
     * Génère la fiche PDF de la liste d'élimination
     * @param documentManager
     * @param outputStream
     * @param id
     * @throws Exception 
     */
    void generateListeEliminationPdf(CoreSession documentManager, OutputStream outputStream, String listeEliminationId) throws Exception;
    
    /**
     * Abandonne une liste d'élimination
     * 
     * @param session
     * @param listeDoc
     * 
     * @throws ClientException 
     */
    void abandonListeElimination(final CoreSession session, final DocumentModel listeDoc) throws ClientException;
    
    /**
     * Supprime une liste d'élimination et élimine ses données
     * 
     * @param session
     * @param listeDoc
     * 
     * @throws ClientException 
     */
    void suppressionListeElimination(final CoreSession session, final DocumentModel listeDoc)throws ClientException;

}

