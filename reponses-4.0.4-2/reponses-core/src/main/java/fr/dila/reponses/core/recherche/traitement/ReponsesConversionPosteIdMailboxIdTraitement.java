package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * Convertit l'identifiant de poste en identifiant de mailbox et le place dans la requête pour pouvoir chercher sur ce champ.
 * @author jgomez
 *
 */
public class ReponsesConversionPosteIdMailboxIdTraitement implements RequeteTraitement<Requete> {

    @Override
    public void doBeforeQuery(Requete requeteSimple) {
        requeteSimple.setEtapeDistributionMailboxId(getMailboxIdFromPoste(requeteSimple));
    }

    @Override
    public void doAfterQuery(Requete requeteSimple) {
        return;
    }

    @Override
    public void init(Requete requeteSimple) {
        return;
    }

    protected String getMailboxIdFromPoste(Requete requete) {
        String idPoste = requete.getEtapeIdPoste();
        if (StringUtils.EMPTY.equals(idPoste) || idPoste == null) {
            return null;
        }
        return "poste-" + requete.getEtapeIdPoste();
    }
}
