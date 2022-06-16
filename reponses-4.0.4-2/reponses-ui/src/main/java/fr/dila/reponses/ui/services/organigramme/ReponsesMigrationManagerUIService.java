package fr.dila.reponses.ui.services.organigramme;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.ui.bean.DetailHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.DetailMigrationHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.HistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.MiseAJourTimbresParametrage;
import fr.dila.reponses.ui.bean.MiseAJourTimbresRecapitulatifList;
import fr.dila.reponses.ui.th.bean.MisesAJourTimbresFormDTO;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.organigramme.SSMigrationManagerUIService;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * ActionBean de gestion des migrations
 */
public interface ReponsesMigrationManagerUIService extends SSMigrationManagerUIService {
    /**
     * Initialisation de données pour la page de paramétrage d'une mise à jour des timbres
     *
     * @param context
     * @return false si une erreur ne permet pas d'accéder à l'écran
     */
    boolean initDataForSelectionTimbres(SpecificContext context);

    void checkBeforeRecapitulatif(SpecificContext context);

    /**
     * Mise à jour des timbres
     * @param context
     * @return
     * @
     */
    String updateTimbre(SpecificContext context);

    String startUpdateTimbreDiffere(CoreSession session, SSPrincipal ssPrincipal);

    /**
     * Verifie que toutes les nouveaux timbres sont renseignés et
     * @param context
     *
     * @return false si un timbre vaut "empty_value"
     * @
     */
    boolean checkNewTimbre(SpecificContext context);

    boolean isUpdateTimbreAvailable();

    List<OrganigrammeNodeTimbreDTO> getCurrentGouvernementForUpdateTimbre(SpecificContext context);

    MiseAJourTimbresParametrage getMiseAJourTimbresParametrage(SpecificContext context);

    MiseAJourTimbresRecapitulatifList getMiseAJourTimbresRecapitulatifList(SpecificContext context);

    ReponsesLogging getCurrentTimbreUpdate(CoreSession session);

    Boolean isMigrationEnCours(CoreSession session);

    HistoriqueMAJTimbresList getHistoriqueMAJTimbresList(CoreSession session);

    String setCurrentLog(String idReponsesLogging, CoreSession session);
    ReponsesLogging getCurrentReponsesLoggingDoc();

    String getReponsesLoggingNextGouvernement();
    String getReponsesLoggingCurrentGouvernement();
    List<TimbreDTO> getTimbreList();
    DetailHistoriqueMAJTimbresList getDetailHistoriqueMAJTimbresList(SpecificContext context);

    Map<String, String> getCurrentReponsesLoggingTimbre(CoreSession session);

    String setCurrentLogLine(String idReponsesLoggingLine);

    DetailMigrationHistoriqueMAJTimbresList getDetailMigrationHistoriqueMAJTimbresList(SpecificContext context);

    String getPourcentageAvancement(ReponsesLogging reponsesLogging) throws ReponsesException;

    String getPourcentageAvancementLine(ReponsesLoggingLine reponsesLoggingLine) throws ReponsesException;

    boolean displayMigrationQuestionClose();

    void setUpdatingTimbre(Boolean updatingTimbre);

    Boolean isUpdatingTimbre();

    /**
     * indique si la mise à jour du tableau est activée. Elle est active tant qu'un noeud est toujours en attente d'une
     * de ses valeurs
     * @param context
     * @return
     */
    Boolean isPollCountActivated(SpecificContext context);
    /**
     * Determine si la mise à jour du tableau doit etre activée. Elle est active tant qu'un noeud est toujours en
     * attente d'une de ses valeurs
     * @param context
     * @return
     */
    Boolean checkPollCountActivation(SpecificContext context);
    Boolean isErrorOccurred();

    /**
     * Normalise l'affichage de la valeur. Si valeur = -100 : affiche Err sinon affiche la valeur
     *
     * @param count
     * @return
     */
    String getStringFromCount(Long count);

    List<SelectValueDTO> getNewTimbreList(SpecificContext context);
    String getNewTimbreLabelForEntite(OrganigrammeNodeTimbreDTO oldEntite);

    void saveParametrageTimbres(MisesAJourTimbresFormDTO misesAJourTimbresFormDTO);

    void checkAllBriserSignature();

    boolean isCheckAllSignature();

    void setCheckAllSignature(boolean checkAllSignature);

    void checkAllClosedDossiersMigration();

    boolean isCheckAllClosedDossiersMigration();

    void setCheckAllClosedDossiersMigration(boolean checkAllClosedDossiersMigration);
}
