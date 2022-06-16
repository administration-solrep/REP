package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.ss.api.service.SSArchiveService;
import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
     *
     */
    List<Dossier> ajouterDossiersListeElimination(CoreSession session, List<DocumentModel> docs);

    /**
     * Retourne le nombre de question présente dans l'application qui sont archivables
     * par rapport à la date du jour et au delai de conservation
     * @param session
     * @return
     *
     */
    Long countQuestionArchivable(CoreSession session);

    /**
     * Generate birt pdf
     *
     * @param session
     * @param outputStream
     * @param questionId
     * @param dossierDoc
     */
    void generateBirtPdf(CoreSession session, OutputStream outputStream, String questionId, DocumentModel dossierDoc);

    /**
     * Retourne la liste des dossiers d'une liste d'élimination
     * @param documentManager
     * @return
     *
     */
    List<DocumentModel> getDossiersFromListeElimination(CoreSession session, DocumentModel listeDoc);

    /**
     * Génère la fiche PDF de la liste d'élimination
     * @param documentManager
     * @param listeEliminationId
     * @return nom du bordereau pdf généré
     */
    String generateListeEliminationPdf(CoreSession documentManager, String listeEliminationId);

    /**
     * Abandonne une liste d'élimination
     *
     * @param session
     * @param listeDoc
     *
     *
     */
    void abandonListeElimination(final CoreSession session, final DocumentModel listeDoc);

    /**
     * Supprime une liste d'élimination et élimine ses données
     *
     * @param session
     * @param listeDoc
     *
     *
     */
    void suppressionListeElimination(final CoreSession session, final DocumentModel listeDoc);

    File generateBirtPdf(final CoreSession session, final String questionId, final DocumentModel dossierDoc);
}
