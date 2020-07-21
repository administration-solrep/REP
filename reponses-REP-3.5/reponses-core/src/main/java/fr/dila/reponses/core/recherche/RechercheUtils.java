package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.recherche.Requete;

/**
 * Classe de méthodes utilitaire pour la recherche Réponses.
 * @author admin
 *
 */
public class RechercheUtils {

    public static Requete adaptRequete(DocumentModel requeteDoc) {
        Requete requete = requeteDoc.getAdapter(Requete.class);
        return requete;
    }
}
