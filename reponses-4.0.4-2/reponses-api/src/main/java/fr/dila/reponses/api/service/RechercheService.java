package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.STRechercheService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 *
 *
 * @author JGZ
 * La classe de service pour les recherches
 *
 */

public interface RechercheService extends STRechercheService {
    /**
     * Crée un objet requête avec un nom.
     * @param session la session utilisateur
     * @param name  le nom de la requête
     * @return
     *
     */
    public Requete createRequete(CoreSession session, String name);

    /**
     * Retourne une requête pas encore sauvegardé.
     * @param session la session utilisateur
     * @param name le nom de la requête
     * @return
     *
     */
    public Requete getRequete(CoreSession session, String name);

    /**
     * Retourne la liste des documents retournée par l'exécution de la requête.
     * @param session : la session de l'utilisateur
     * @param requete : la requête pour la recherche
     * @return
     *
     */
    public DocumentModelList query(CoreSession session, Requete requete);

    /**
     * Renvoie la clause WHERE d'un document model de type requête
     * @param model
     * @return
     *
     */
    public String getWhereClause(Requete requete, String... modelNames);

    /**
     * Retourne la requête complête, construite à partir de différents models.
     * @param model
     * @param modelNames
     * @return
     *
     */
    String getFullQuery(CoreSession session, Requete requete, String... modelNames);

    /**
     * Recherche une question par son origine + numéro
     * @param session
     * @param title
     * @return
     *
     */
    Question searchQuestionBySourceNumero(CoreSession session, String sourcenumero);

    /**
     * Renvoie le contenu du paramètre legislature-courante.
     *
     * @param session la session
     * @return un Long
     */
    Long getLegislatureCourante(CoreSession session);
}
