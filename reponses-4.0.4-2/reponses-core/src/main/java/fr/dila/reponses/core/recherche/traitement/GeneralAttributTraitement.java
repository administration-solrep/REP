package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 *
 * Place l'attribut à null au début de la requête
 * @author jgomez
 *
 */
public class GeneralAttributTraitement implements RequeteTraitement<Requete> {
    private final String etat;

    public GeneralAttributTraitement(String etat) {
        this.etat = etat;
    }

    @Override
    public void doBeforeQuery(Requete requete) {
        // Si l'état est à faux, on le passe à nul de façon à l'ignorer.
        if (!requete.getEtat(etat)) {
            requete.setEtat(etat, null);
        }
    }

    @Override
    public void doAfterQuery(Requete requete) {}

    @Override
    public void init(Requete requete) {
        requete.setEtat(etat, null);
    }
}
