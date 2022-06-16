package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.ui.bean.DossierConnexeDTO;
import fr.dila.reponses.ui.bean.DossierConnexeList;
import fr.dila.reponses.ui.bean.DossierConnexeMinistereDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

/**
 * Une classe qui permet de gérer les actions relatives aux questions connexes.
 *
 * @author asatre
 */
public interface DossierConnexeActionService {
    DossierConnexeList getListDossierConnexe(SpecificContext context);

    List<DossierConnexeMinistereDTO> getListMinisteres(SpecificContext context);

    DossierConnexeDTO getQuestion(SpecificContext context);

    void setReponseQuestion(SpecificContext context);

    void createLot(SpecificContext context);

    void updateLot(SpecificContext context);
}
