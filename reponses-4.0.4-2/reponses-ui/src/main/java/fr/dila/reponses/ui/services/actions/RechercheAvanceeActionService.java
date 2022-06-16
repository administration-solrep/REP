package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Un action service pour les traitements spécifiques à la recherche avancée.
 *
 * @author jgomez
 */
public interface RechercheAvanceeActionService {
    List<Recherche> getRechercheList();

    Recherche getRecherche(String rechercheName);

    /**
     * Pré-traitement de la recherche avancée.
     *
     * @return Vue
     */
    String precomputeResults(CoreSession session, DocumentModel currentRequete, IndexModeEnum currentIndexationMode);

    /**
     * Retourne la chaine de caractère représentant la requête.
     *
     * @return
     */
    String getCurrentRequetePattern(CoreSession session, DocumentModel currentRequete);

    /**
     * Réinitialise le document model de la requete courante.
     *
     * @return
     */
    Requete resetRequete(DocumentModel currentRequete);

    DocumentModel getCurrentRequete(SpecificContext context, CoreSession session, DocumentModel currentRequete);
}
