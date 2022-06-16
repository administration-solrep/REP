package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.recherche.Requete;
import org.nuxeo.ecm.core.api.DocumentModel;

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
