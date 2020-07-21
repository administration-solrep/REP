package fr.dila.reponses.core.event;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SessionUtil;

/**
 * Gestion du calcul des questions à migrer ou non des timbres.
 * Des threads sont lancés séparement pour permettre le requetage simultané des informations
 */
public class CountInfosTimbresListener implements PostCommitEventListener {
    
	private static final String DTO_TOTAL = "total";
	private static final Long ERROR_RECUP_COUNT = -100L;
	private static final STLogger LOGGER = STLogFactory.getLog(CountInfosTimbresListener.class);
	private static final int CHOICE_CLOSED = 1;
	private static final int CHOICE_SIGNED = 2;
	private static final int CHOICE_MIGRABLE = 3;
	private static final int CHOICE_MODEL_FDR = 4;
	
	public CountInfosTimbresListener(){
    	super();
    }
	
    @Override
    public void handleEvent(final EventBundle events) throws ClientException {
        if (events.containsEventName(ReponsesEventConstant.MIGRATION_COUNT_INFOS_TIMBRES_EVENT)) {
            for (final Event event : events) {
                handleEvent(event);
            }
        }
    }

    protected void handleEvent(final Event event) {
    	if (!ReponsesEventConstant.MIGRATION_COUNT_INFOS_TIMBRES_EVENT.equals(event.getName())) {
    		return;
    	}
    	
        final Map<String, Serializable> properties = event.getContext().getProperties();
    	
    	@SuppressWarnings("unchecked")
        final Map<String, OrganigrammeNodeTimbreDTO> mapDto = (Map<String, OrganigrammeNodeTimbreDTO>) properties
                .get(ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT);

    		// Création des 4 threads utiles pour le calcul    	
		try {
			ThreadCount threadClosed = new ThreadCount(mapDto, CHOICE_CLOSED);
			threadClosed.start();
		} catch (ClientException exc) {
			LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
			setErrorCountOnClosed(mapDto);
			mapDto.get(DTO_TOTAL).setCountClose(0L);
		}
    	
		try {
			ThreadCount threadSigned = new ThreadCount(mapDto, CHOICE_SIGNED);
	    	threadSigned.start();
		} catch (ClientException exc) {
			LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
			setErrorCountOnSigned(mapDto);
			mapDto.get(DTO_TOTAL).setCountSigne(0L);
		}
    	
		try {
			ThreadCount threadMigrable = new ThreadCount(mapDto, CHOICE_MIGRABLE);
	    	threadMigrable.start();
		} catch (ClientException exc) {
			LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
			setErrorCountOnMigrable(mapDto);
			mapDto.get(DTO_TOTAL).setCountMigrable(0L);
		}
		
		try {
			ThreadCount threadModelFdr = new ThreadCount(mapDto, CHOICE_MODEL_FDR);    	
	    	threadModelFdr.start();
		} catch (ClientException exc) {
			LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
			setErrorCountOnModelFDR(mapDto);
			mapDto.get(DTO_TOTAL).setCountModelFDR(0L);
		}
    }
    
    /**
     * Classe interne Thread pour le requêtage des informations de comptage
     * Une session est récupérée dans le constructeur, il faut bien s'assurer de la fermer à la fin du run
     *
     */
    protected class ThreadCount extends Thread {
    	private final int choiceCount;
    	private final Map<String, OrganigrammeNodeTimbreDTO> map;
    	private final CoreSession session;    	
    	
    	public ThreadCount(Map<String, OrganigrammeNodeTimbreDTO> map, int choiceCount) throws ClientException {
    		super();
    		this.choiceCount = choiceCount;
    		this.map = map;
    		this.session = SessionUtil.getCoreSession();
    	}
    	
    	public void run() {
    		switch(choiceCount) {
    		case CHOICE_CLOSED:
    			countClosedQuestions(session, map);
    			break;
    		case CHOICE_SIGNED:
    			countSignedQuestions(session, map);
    			break;
    		case CHOICE_MIGRABLE:
    			countMigrableQuestions(session, map);
    			break;
    		case CHOICE_MODEL_FDR:
    			countModelFDR(session, map);
    			break;
    		default:
    			// Do nothing
    			break;
    		}
    		// On referme la session ici car on a terminé de l'utiliser
    		SessionUtil.close(session);
    	}
    }
    
    /**
     * Renseigne la valeur -100 pour les questions fermées pour tous les noeuds
     * @param mapDto
     */
    private void setErrorCountOnClosed(final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
    		String key = entry.getKey();
    		if (!DTO_TOTAL.equals(key)) {
    			OrganigrammeNodeTimbreDTO node = entry.getValue();
    			if (node != null) {
    				node.setCountClose(ERROR_RECUP_COUNT);
    			}    			
    		}
    	}
    }
    
    /**
     * Renseigne la valeur -100 pour les questions signées pour tous les noeuds
     * @param mapDto
     */
    private void setErrorCountOnSigned(final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
    		String key = entry.getKey();
    		if (!DTO_TOTAL.equals(key)) {
    			OrganigrammeNodeTimbreDTO node = entry.getValue();
    			if (node != null) {
    				node.setCountSigne(ERROR_RECUP_COUNT);
    			}    			
    		}
    	}
    }
    
    /**
     * Renseigne la valeur -100 pour les questions migrables (en attente de réponse) pour tous les noeuds
     * @param mapDto
     */
    private void setErrorCountOnMigrable(final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
    		String key = entry.getKey();
    		if (!DTO_TOTAL.equals(key)) {
    			OrganigrammeNodeTimbreDTO node = entry.getValue();
    			if (node != null) {
    				node.setCountMigrable(ERROR_RECUP_COUNT);
    			}    			
    		}
    	}
    }
    
    /**
     * Renseigne la valeur -100 pour les modèles de FDR pour tous les noeuds
     * @param mapDto
     */
    private void setErrorCountOnModelFDR(final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
    		String key = entry.getKey();
    		if (!DTO_TOTAL.equals(key)) {
    			OrganigrammeNodeTimbreDTO node = entry.getValue();
    			if (node != null) {
    				node.setCountModelFDR(ERROR_RECUP_COUNT);
    			}    			
    		}
    	}
    }
    
    /**
     * Appelle la requête de compte des questions fermées et renseigne l'information dans les noeuds
     * Si la requête n'a pas retourné d'info pour le ministere, renseigne 0
     * @param session
     * @param mapDto
     */
    private void countClosedQuestions(final CoreSession session, final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
    	Long totalCountClose = 0L;    	
    	try {
    		Map<String, Long> closeMap = updateTimbreService.getCloseCount(session);
			for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
	    		String key = entry.getKey();
	    		OrganigrammeNodeTimbreDTO node = entry.getValue();
	    		if (!DTO_TOTAL.equals(key) && node != null) {
    				Long countClose = closeMap.get(key);    				
    				if (countClose == null) {
    					node.setCountClose(0L);
    				} else {
    					node.setCountClose(countClose);
    					totalCountClose += countClose;
    				}			
	    		}
	    	}
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
			setErrorCountOnClosed(mapDto);
		}
    	mapDto.get(DTO_TOTAL).setCountClose(totalCountClose);
    }
    
    /**
     * Appelle la requête de compte des questions signées et renseigne l'information dans les noeuds
     * Si la requête n'a pas retourné d'info pour le ministere, renseigne 0
     * @param session
     * @param mapDto
     */
    private void countSignedQuestions(final CoreSession session, final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
    	Long totalCountSigned = 0L;
		try {
			Map<String, Long> signedMap = updateTimbreService.getSigneCount(session);
			for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
	    		String key = entry.getKey();
    			OrganigrammeNodeTimbreDTO node = entry.getValue();
	    		if (!DTO_TOTAL.equals(key) && node != null) {
    				Long countSigned = signedMap.get(key);
    				if (countSigned == null) {
    					node.setCountSigne(0L);
    				} else {
    					node.setCountSigne(countSigned);
    					totalCountSigned += countSigned;
    				}
	    		}
	    	}
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
			setErrorCountOnSigned(mapDto);
		}
    	mapDto.get(DTO_TOTAL).setCountSigne(totalCountSigned);
    }
    
    /**
     * Appelle la requête de compte des questions migrables (en attente de réponse) et renseigne l'information dans les noeuds
     * Si la requête n'a pas retourné d'info pour le ministere, renseigne 0
     * @param session
     * @param mapDto
     */
    private void countMigrableQuestions(final CoreSession session, final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
    	Long totalCountMigrable = 0L;
    	try {
    		Map<String, Long> migrableMap = updateTimbreService.getMigrableCount(session);
			for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
	    		String key = entry.getKey();
	    		OrganigrammeNodeTimbreDTO node = entry.getValue();
	    		if (!DTO_TOTAL.equals(key) && node != null) {
    				Long countMigrable = migrableMap.get(key);
    				if (countMigrable == null) {
    					node.setCountMigrable(0L);
    				} else {
    					node.setCountMigrable(countMigrable);
    					totalCountMigrable += countMigrable;
    				}
	    		}
	    	}
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
			setErrorCountOnMigrable(mapDto);
		}
    	mapDto.get(DTO_TOTAL).setCountMigrable(totalCountMigrable);
    }
    
    /**
     * Appelle la requête de compte des modèles de FDR et renseigne l'information dans les noeuds
     * Si la requête n'a pas retourné d'info pour le ministere, renseigne 0
     * @param session
     * @param mapDto
     */
    private void countModelFDR(final CoreSession session, final Map<String, OrganigrammeNodeTimbreDTO> mapDto) {
    	final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
    	Long totalCountModelFDR = 0L;
		try {
			Map<String, Long> modelFDRMap = updateTimbreService.getModelFDRCount(session);
			for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapDto.entrySet()) {
	    		String key = entry.getKey();
	    		OrganigrammeNodeTimbreDTO node = entry.getValue();
	    		if (!DTO_TOTAL.equals(key) && node != null) {
    				Long countModelFDR = modelFDRMap.get(key);
    				if (countModelFDR == null) {
    					node.setCountModelFDR(0L);
    				} else {
    					node.setCountModelFDR(countModelFDR);
        				totalCountModelFDR += countModelFDR;
    				}			
	    		}
	    	}
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
			setErrorCountOnModelFDR(mapDto);
		}
    	mapDto.get(DTO_TOTAL).setCountModelFDR(totalCountModelFDR);
    }
}
