package fr.dila.reponses.ui.contentview;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Page provider dedié à la requete de suivi : "historique des validations"
 *
 * @author spesnel
 */
public class RequeteHistoriqueResultPageProvider extends RechercheResultPageProvider {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        ReponseFeuilleRouteService fdrService = ReponsesServiceLocator.getFeuilleRouteService();

        SSPrincipal ssPrincipal = (SSPrincipal) coreSession.getPrincipal();
        Set<String> postesIdSet = ssPrincipal.getPosteIdSet();
        List<Dossier> dossiers = fdrService.getLastWeekValidatedDossiers(coreSession, postesIdSet);

        resultsCount = dossiers.size();
        List<String> questionIds = new ArrayList<>();
        int i = 0;
        for (Dossier dossier : dossiers) {
            if (i > offset + getPageSize()) {
                break;
            } else if (i >= offset) {
                questionIds.add(dossier.getQuestionId());
            }
            i++;
        }

        currentItems = new ArrayList<>();

        populateFromQuestionIds(coreSession, questionIds);
    }
}
