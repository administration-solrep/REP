package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Reponse;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponseService {
    /**
     * get Reponse major version number.
     *
     * @return Numéro de version majeur
     *
     */
    int getReponseMajorVersionNumber(CoreSession session, DocumentModel reponse);

    /**
     * get Reponse old Version Document.
     *
     * @return old Version Document
     *
     */
    DocumentModel getReponseOldVersionDocument(CoreSession session, DocumentModel reponse, int versionNumber);

    /**
     * get Reponse Version Document List.
     *
     * @return Reponse Version Document List.
     *
     */
    List<DocumentModel> getReponseVersionDocumentList(CoreSession session, DocumentModel reponse);

    /**
     * get Reponse From Dossier.
     *
     * @return Reponse
     */
    DocumentModel getReponseFromDossier(CoreSession session, DocumentModel dossier);

    /**
     * Modification de la réponse.
     *
     * @param session Session
     * @param reponseDoc Réponse
     * @param dossier
     * @return
     *
     */
    DocumentModel saveReponse(CoreSession session, DocumentModel reponseDoc, DocumentModel dossier);

    /**
     * Modification de la réponse et de l'erratum éventuel.
     *
     * @param session Session
     * @param reponseDoc Réponse
     * @param dossier
     * @return
     *
     */
    DocumentModel saveReponseAndErratum(
        CoreSession documentManager,
        DocumentModel reponse,
        DocumentModel currentDocument
    );

    /**
     * create a Reponse version,increment Reponse and save the new Reponse
     * Document from Ministere, calling by WebService
     *
     * @param session Session
     * @param reponse Réponse
     * @return
     *
     */
    DocumentModel saveReponseFromMinistere(CoreSession session, DocumentModel reponse);

    /**
     * Increments the version of the reponse.
     *
     * @param session
     * @param reponse
     *
     */
    DocumentModel incrementReponseVersion(CoreSession session, DocumentModel reponse);

    /**
     * Return Dossier from Reponse.
     *
     * @param session
     * @param reponse
     * @return
     *
     */
    DocumentModel getDossierFromReponse(CoreSession session, DocumentModel reponse);

    /**
     *
     * @param session
     * @param reponse
     * @return liste des contributeurs de chaque version, par ordre de version
     * @throws PropertyException
     *
     */
    List<String> getVersionsContributorsFromReponse(CoreSession session, DocumentModel reponse);

    /**
     * brise la signature sur action utilisateur
     * @param session
     * @param reponse
     * @param dossier
     * @return
     *
     */
    DocumentModel briserSignatureReponse(CoreSession session, Reponse reponse, DocumentModel dossier);

    /**
     * Indique si la réponse est signée ou non
     * @param dossier
     * @return
     */
    boolean isReponseSignee(CoreSession session, DocumentModel dossierDoc);

    /**
     * Indique si la réponse est publié ou non
     * @param dossier
     * @return
     */
    boolean isReponsePublished(CoreSession session, DocumentModel dossierDoc);
}
