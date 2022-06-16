package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer le cycle de vie du dossier Réponses
 *
 * @author tlombard
 */
public interface DossierService {
    /**
     * Retourne la liste des dossiers ayant pour ministère <b>ministereId</b> et
     * pour direction <b>directionId</b> si <b>hasDirection</b> est vrai.
     *
     * @param session
     * @param directionId
     * @return la liste des dossiers ayant pour direction <b>directionId</b>.
     */
    List<DocumentModel> getDossierRattacheToDirection(CoreSession session, String directionId);

    /**
     * @return Retourne le dossier directeur d'un allotissement du dossier si le
     *         dossier est dans un lot, empty sinon.
     */
    Optional<Dossier> getDossierDirecteur(CoreSession session, Dossier dossier);

    /**
     * Renvoie la liste des questions alloties d'un dossier
     *
     * @param session
     *            CoreSession
     * @param dossier
     *            le dossier concerné
     * @return la liste des questions alloties
     */
    List<Question> getQuestionsAlloties(CoreSession session, Dossier dossier);

    /**
     * Retourne l'ensemble des noms des directions du dossier courant.
     *
     * @return l'ensemble des noms des directions du dossier courant
     */
    Set<String> getListingUnitesStruct(DocumentModel dossierDoc, CoreSession session);

    String getFinalStepLabel(CoreSession session, Dossier dossier);

    List<Question> getQERappels(CoreSession session, Dossier dossier);

    List<String> getSourceNumeroQERappels(CoreSession session, Dossier dossier);
}
