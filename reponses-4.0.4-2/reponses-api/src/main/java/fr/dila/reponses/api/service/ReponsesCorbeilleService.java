package fr.dila.reponses.api.service;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.service.STCorbeilleService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service qui permet de gérer les corbeilles utilisateur / postes.
 *
 * @author jtremeaux
 */
public interface ReponsesCorbeilleService extends STCorbeilleService {
    /**
     * Retourne la Mailbox personnelle de l'utilisateur qui possède les dossiers.
     *
     * @param session
     *            Session
     * @return Mailbox
     *
     */
    Mailbox getDossierOwnerPersonalMailbox(CoreSession session);
    /**
     * Liste les etapes existantes dans une mailbox pour un ministere donné, accompagnées du nombre de dossier présent
     * dans chaque étape
     *
     * @param session
     * @param mailboxIds
     *            ensemble de valeur de l'attribut mailboxId de la mailbox
     */
    Map<String, Integer> listNotEmptyEtape(CoreSession session, Set<String> mailboxIds, String ministereId);

    /**
     * Recherche les DossierLink correspondant à la distribution du dossier que l'utilisateur peut actionner.
     * L'utilisateur a le droit d'actionner ce DossierLink, soit parce qu'il est destinataire de la distribution,
     * soit parce qu'il est administrateur.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @return Liste de DossierLink
     *
     */
    List<DocumentModel> findUpdatableDossierLinkForDossier(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne tous les dossiers d'un ministère.
     *
     * @param session
     * @param ministereId
     * @return
     *
     */
    List<DocumentModel> findDossierInMinistere(CoreSession session, String ministereId);

    /**
     * Retourne tous les dossiers correspondant à un ministère de réattribution
     *
     * @param session
     * @param ministereReattribution
     * @return
     *
     */
    List<DocumentModel> findDossierFromMinistereReattribution(CoreSession session, String ministereReattribution);

    /**
     * Recherche les DossiersLink correspondant à la distribution des dossiers dans un des ministères passés en
     * paramètre ou ceux présents pour les mailbox id.
     *
     * @param session
     *            Session
     * @param dossierId
     *            Identifiant technique des documents dossiers
     * @param ministereIdList
     *            Collection d'identifiant technique de ministère
     * @param mailboxIdList les mailbox de l'utilisateur
     * @return Liste de DossierLink
     */
    List<DocumentModel> findDossierLinkInMinistereOrMailbox(
        CoreSession session,
        List<String> dossiersDocsIds,
        Collection<String> ministereIdList,
        final Collection<String> mailboxIdList
    );

    /**
     * Recherche les DossierLink correspondant à la distribution des dossiers. L'utilisateur a le droit d'actionner ce
     * DossierLink, soit parce qu'il est destinataire de la distribution, soit parce qu'il est administrateur.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @return Liste de DossierLink
     *
     */
    List<DocumentModel> findUpdatableDossierLinkForDossiers(CoreSession session, List<String> dossiersDocsIds);

    int countDossierLinksSignalesForPostes(CoreSession session, Collection<String> postesId);

    int countDossierLinksNonSignalesForPostes(CoreSession session, Collection<String> postesId);

    Map<String, Integer> mapCountDossierLinksSignalesToMinisteres(CoreSession session, Collection<String> postesId);

    Map<String, Integer> mapCountDossierLinksNonSignalesToMinisteres(CoreSession session, Collection<String> postesId);

    List<DocumentModel> findUpdatableDossierLinkForDossier(
        final CoreSession session,
        final SSPrincipal principal,
        final DocumentModel dossierDoc
    );

    List<DocumentModel> findUpdatableDossierLinkForDossiers(
        final CoreSession session,
        final SSPrincipal principal,
        final List<String> dossiersDocsIds
    );
}
