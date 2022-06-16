package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.ui.bean.AllotissementConfigDTO;
import fr.dila.reponses.ui.bean.AllotissementListDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Une classe qui permet de gérer les actions relatives aux allotissements.
 *
 * @author asatre
 */
public interface AllotissementActionService {
    boolean existLot(DocumentModel doc);

    boolean isDossierDirecteur(CoreSession session, DocumentModel doc, String sourceNumeroQuestion);

    AllotissementListDTO getListQuestionsAllotis(SpecificContext context);

    void removeFromLot(SpecificContext context);

    /**
     * Méthode d'allotissement à appeler dans l'onglet allotissement
     * @param context le specific context qui référence le numéro de question à rajouter et le dossier courant en cours de consultation
     */
    void addToLot(SpecificContext context);

    AllotissementConfigDTO getListQuestionsAllotisSearch(SpecificContext context);

    /**
     * Méthode d'allotissement à appeler dans l'onglet dossier connexe ou dans
     * @param context
     */
    void createOrUpdateLot(SpecificContext context);

    boolean isLotCoherent(
        SpecificContext context,
        Question questionDirectrice,
        Set<Question> listQuestion,
        boolean updateMode
    );
}
