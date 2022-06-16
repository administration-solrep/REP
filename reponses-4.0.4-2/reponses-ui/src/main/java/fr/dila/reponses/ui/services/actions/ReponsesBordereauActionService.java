package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.ui.bean.HistoriqueAttributionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesBordereauActionService {
    Boolean getPartMiseAJour(Question question);

    Boolean getPartReponse(Reponse reponse);

    Boolean getPartIndexationAN(Question question);

    Boolean getPartIndexationSE(Question question);

    Boolean getPartEditableIndexationComplementaire(SpecificContext context, String id);

    Boolean getPartIndexationComplementaireAN(Question question);

    Boolean getPartIndexationComplementaireSE(Question question);

    Boolean getPartIndexationComplementaireMotCle(Question question);

    Boolean getPartFeuilleRoute(Question question, Dossier dossier);

    Boolean getPartEditMinistereRattachement(SpecificContext context);

    Boolean getPartEditDirectionPilote(SpecificContext context);

    List<Renouvellement> getRenouvellements(Question question);

    List<HistoriqueAttributionDTO> getHistoriqueAttributionFeuilleRoute(CoreSession session, Dossier dossier);
}
