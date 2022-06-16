package fr.dila.reponses.ui.services.actions.suggestion.ministere;

import fr.dila.st.ui.services.actions.suggestion.ISuggestionProvider;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

public interface MinistereSuggestionProviderService extends ISuggestionProvider {
    /**
     * Retourne la liste de tous les ministères interrogés (historique de tous les ministères utilisés dans
     * l'application)
     *
     * @return
     * @throws ClientException
     */
    List<Map<String, Serializable>> getAllMinisteresInterroges(CoreSession session);

    List<Map<String, Serializable>> getAllMinisteres(CoreSession session);

    List<Map<String, Serializable>> getAllMinistereListRecherche();
}
