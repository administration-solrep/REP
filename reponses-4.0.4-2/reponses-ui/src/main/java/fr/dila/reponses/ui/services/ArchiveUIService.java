package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.EliminationDonneesConsultationList;
import fr.dila.reponses.ui.bean.EliminationDonneesList;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service UI pour les méthodes d'archivage (export zip)
 *
 */
public interface ArchiveUIService {
    /**
     * Export zip d'un dossier avec son fond de dossier
     *
     */
    void export(SpecificContext context);

    /**
     * Export ZIP en masse d'une selection de dossier
     *
     * @throws Exception
     */
    void masseExport(SpecificContext context);

    void masseEnvoyerMailDossier(SpecificContext context);

    /**
     * Ajout des dossiers selectionnés à la liste d'élimination courante
     *
     */
    void demanderElimination(SpecificContext context, List<String> docIds);

    /**
     * Suppression des dossiers de la liste d'élimination courante
     *
     * @param context
     */
    void eliminerListe(SpecificContext context);

    /**
     * Edite le bordereau d'archivage et enlève l'état en cours d'une liste
     * d'élimination
     *
     * @param context
     * @return nom du bordereau pdf généré
     */
    String editerBordereau(SpecificContext context);

    /**
     * Retire une liste de dossiers d'une liste d'élimination
     *
     * @param context
     */
    void retirerDossierListeElimination(SpecificContext context);

    /**
     * Vide une liste d'élimination
     *
     * @param context
     */
    void viderListeElimination(SpecificContext context);

    /**
     * Abandonnne une liste d'élimination
     *
     * @param context
     */
    void abandonListeElimination(SpecificContext context);

    /**
     * Retourne true si le document courant est une liste d'élimination en cours
     *
     * @return
     */
    boolean isCurrentListeEliminationEnCours(DocumentModel currentDocument);

    /**
     * Retourne une {@link EliminationDonneesList}
     *
     * @param context
     * @return
     */
    EliminationDonneesList getEliminationDonneesList(SpecificContext context);

    /**
     * Retourne une {@link EliminationDonneesConsultationList}
     *
     * @param context
     * @return
     */
    EliminationDonneesConsultationList getEliminationDonneesConsultationList(SpecificContext context);

    /**
     * Retourne le titre complet de la liste d'élimination
     *
     * @param context
     * @return
     */
    String getTitreListeElimination(SpecificContext context);
}
