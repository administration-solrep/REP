package fr.dila.reponses.api.service;

import fr.dila.ss.api.client.InjectionGvtDTO;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service d'injection de gouvernement de REPONSES
 *
 * @author jbrunet
 *
 */
public interface ReponsesInjectionGouvernementService extends Serializable {
    /**
     * Prépare l'injection en récupérant les données nécessaires dans un DTO
     *
     * @param session
     *
     */
    List<InjectionGvtDTO> prepareInjection(final CoreSession session, final File file);

    /**
     * Réalise l'injection en créant les entités nécessaires
     *
     * @param session
     * @param listInjection
     *
     */
    void executeInjection(final CoreSession session, List<InjectionGvtDTO> listInjection);

    /**
     * Récupère le nouveau gouvernement à injecter
     *
     * @param listInjection
     * @return
     */
    public InjectionGvtDTO getNewGovernment(List<InjectionGvtDTO> listInjection);

    /**
     * Récupère la liste des nouvelles entités à injecter (nouveaux timbres)
     *
     * @param listInjection
     * @return
     */
    public List<InjectionGvtDTO> getAllNewEntities(List<InjectionGvtDTO> listInjection);
}
