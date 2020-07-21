package fr.dila.reponses.api.service;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.ss.api.client.InjectionGvtDTO;

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
     * @throws ClientException
     */
	List<InjectionGvtDTO> prepareInjection(final CoreSession session, final File file) throws ClientException;
	
	/**
	 * Réalise l'injection en créant les entités nécessaires
	 * 
	 * @param session
	 * @param listInjection 
	 * @throws ClientException
	 */
	void executeInjection(final CoreSession session, List<InjectionGvtDTO> listInjection) throws ClientException;
	
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
