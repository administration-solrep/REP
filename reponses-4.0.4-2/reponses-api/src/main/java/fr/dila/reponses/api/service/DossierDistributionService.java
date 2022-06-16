package fr.dila.reponses.api.service;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Service de distribution des dossiers de l'application Réponses.
 *
 * @author jtremeaux
 */
public interface DossierDistributionService extends fr.dila.ss.api.service.DossierDistributionService {
    /**
     * Crée un dossier.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier à persister
     * @param mailbox
     *            Mailbox de création du dossier
     * @return Dossier nouvellement créé
     *
     */
    Dossier createDossier(CoreSession session, DocumentModel dossierDoc);

    /**
     * Crée le dossier en utilisant les methode de creation de case.
     *
     * @param session
     * @param mailbox
     * @return
     */
    Dossier initDossier(CoreSession session, Long numeroQuestion);

    /**
     * Crée le structure de documents du dossier (documents Dossier, etc).
     *
     * @param session
     *            Session
     * @param documentModel
     *            Dossier
     * @param question
     *            Question
     * @return Dossier Dossier
     *
     */
    Dossier createDossier(
        CoreSession session,
        Dossier dossier,
        Question question,
        Reponse reponse,
        String etatQuestion
    );

    /**
     * set the dossier path name.
     *
     *
     */
    void setDossierPathName(DocumentModel documentModel);

    /**
     * Renseigne les attributs du DossierLink au moment de sa création : dénormalise / calcule un nombre d'attributs
     * depuis le dossier ou l'étape.
     *
     * @param session
     *            Session
     * @param dossierLinkDoc
     *            Document DossierLink
     *
     */
    void setDossierLinksFields(CoreSession session, DocumentModel dossierLinkDoc);

    /**
     * set the dossier case links sort field.
     *
     * @param documentModel
     *            DossierLink DocumentModel à remplir
     * @param question
     *            question contenant les données pour créer le champ de tri
     *
     */
    void setDossierLinksSortField(DocumentModel documentModel, Question question);

    /**
     * Recherche et démarre la feuille de route associée au dossier.
     *
     * @param session
     *            Session
     * @param dossier
     *            Dossier
     * @return Instance de feuille de route
     *
     */
    SSFeuilleRoute startDefaultRoute(CoreSession session, Dossier dossier);

    /**
     * This function extracts reponse from the question and returns the last Signalement
     *
     *
     * @param questionDoc
     *            question documentModel
     * @return
     */
    Signalement getLastSignalement(DocumentModel questionDoc);

    /**
     * This function extracts question from the dossier and returns the last Renouvellement
     *
     *
     * @param questionDoc
     *            question documentModel
     * @return
     */
    Renouvellement getLastRenouvellement(DocumentModel questionDoc);

    /**
     * Recherche des dossiers par numero de Question
     *
     * @param session
     * @param numeroQuestion
     *            Numero de la question
     * @return retourne la liste des dossiers
     */
    DocumentModelList getDossierFrom(CoreSession session, String numeroQuestion);

    /**
     * Recherche une question identifié par (numero, type, source et legislature) existe
     *
     * @param session
     * @param numeroQuestion
     *            Numero de la question
     * @param type
     *            Type de la question
     * @param source
     *            source de la question
     * @param legislature
     *            identifiant de la legislature de la question
     * @return l'id du document de la question, null if not found
     */
    String retrieveDocumentQuestionId(
        CoreSession session,
        long numeroQuestion,
        String type,
        String source,
        long legislature
    );

    /**
     * Test si une question identifié par (numero, type, source et legislature) existe
     *
     * @param session
     * @param numeroQuestion
     *            Numero de la question
     * @param type
     *            Type de la question
     * @param source
     *            source de la question
     * @param legislature
     *            identifiant de la legislature de la question
     * @return si une question identifié par les paramètres existe
     */
    boolean isExistingQuestion(CoreSession session, long numeroQuestion, String type, String source, long legislature);

    /**
     * Change le ministère courant, avec dénormalisation dans le dossier et la question.
     *
     * @param session
     * @param dossierDoc
     * @param idMinistere
     * @return
     *
     */
    DocumentModel setNouveauMinistereCourant(CoreSession session, DocumentModel dossierDoc, String idMinistere);

    /**
     * Annule l'ancienne route et instancie une nouvelle route.
     *
     * @param session
     *            session
     * @param dossier
     *            dossier
     * @param etapeCourante
     *
     */
    DocumentModel reattribuerDossier(
        CoreSession session,
        Dossier dossier,
        DossierLink dossierLink,
        String idNewMinistere,
        String statusRunningStep,
        ReponsesRouteStep etapeCourante
    );

    /**
     * Effectue les opération spécifiques quand un dossier link (dlink) arrive dans des états particuliers.
     *
     * @param dlink
     * @param session
     *
     */
    void specificStepsOperation(DossierLink dlink, CoreSession session) throws Exception;

    /**
     * Initialise l'ACL du DossierLink à sa création.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @param dossierLinkDoc
     *            Document DossierLink
     *
     */
    void initDossierLinkAcl(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc);

    /**
     * Instancie une nouvelle feuille de route. Suppression des étapes à venir sur l'ancienne feuille de route. Ajout
     * des étapes déjà validé sur la nouvelle feuille de route. Annule l'ancienne feuille de route. Lève un événement
     * pour démarrer la nouvelle instance sur le cas.
     *
     * @param session
     *            Session
     * @param caseDoc
     *            Cas lié à la feuille de route
     * @param oldRouteInstanceDoc
     *            Ancienne instance de feuille de route
     * @param newRouteModelDoc
     *            Nouveau modèle de feuille de route
     * @return Nouvelle instance de feuille de route
     *
     */
    DocumentModel reattribuerFeuilleRoute(
        CoreSession session,
        DocumentModel oldRouteInstanceDoc,
        DocumentModel newRouteModelDoc,
        String statusRunningStep
    );

    /**
     * Effectue les opérations nécessaires après le retrait d'une question : - suppression des étapes à venir -
     * validation automatique de l'étape en cours
     *
     * @param session
     * @param dossierDoc
     *
     */
    void retirerFeuilleRoute(CoreSession session, final Dossier dossier);

    /**
     * mise a jour des champs pour les tris dans les tableaux
     *
     * @param dossierLink
     * @param question
     */
    void updateDenormalisation(DossierLink dossierLink, Question question);

    /**
     * Ajoute la note d'étape concernant la réaffectation
     *
     * @param session
     *            CoreSession
     * @param dossierDoc
     *            Dossier
     * @param dossierLinkDoc
     *            DossierLink
     *
     */
    void addCommentAndStepForReaffectation(CoreSession session, DocumentModel dossierDoc);

    /**
     * Met a jour le precalcul du nombre de dossierLink dans les mailbox Incremente pour les mailbox contenant le
     * dossierLink les valeur relatifs au couple (ministereAttributaire, type d'etape) du dossierLink. Dans le cas d'un
     * changement de ministere attributaire (comme dans la reattribution), si la valeur de ministerePrecId est non
     * nulle, le precalcul est décrémenté pour le couple valeur (ministerePrecId, type d'etape du dossierLink)
     *
     * @param session
     * @param dossierLink
     * @param ministerePrecId
     *
     */
    void correctCounterAfterChangeOnMinistereAttributaire(
        CoreSession session,
        DossierLink dossierLink,
        String ministerePrecId
    );

    /**
     * Supprime le dossierLink : le passe à l'état deleted
     *
     * appel les evenements beforeCaseLinkRemovedEvent et afterCaseLinkRemovedEvent
     *
     * @param session
     * @param dossierLink
     *
     */
    void deleteDossierLink(CoreSession session, DossierLink dossierLink);

    /**
     * notify mailbox modification : ajout / suppression dossierLink
     */
    void notifyMailboxModification(CoreSession session, String mailboxDocId);

    /**
     *
     * @param session
     * @param question
     * @param idMinistere
     *
     */
    void setMinistereRattachement(CoreSession session, Question question, String idMinistere);

    /**
     *
     * @param session
     * @param question
     * @param idDirection
     *
     */
    void setDirectionPilote(CoreSession session, Question question, String idDirection);

    /**
     * add comment to step
     *
     * @param session
     *            the session
     * @param etapeDoc
     *            the step
     * @param message
     *            the message to add
     */
    void addCommentToStep(final CoreSession session, DocumentModel etapeDoc, final String message);

    void setDossierLinksFields(final CoreSession session, final DocumentModel dossierLinkDoc, String oldMinistereId);
}
