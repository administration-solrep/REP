package fr.dila.reponses.core.event;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.AbstractSyncEventListener;
import fr.dila.st.core.factory.STLogFactory;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;

/**
 * Gestion du calcul des questions à migrer ou non des timbres. Des threads sont
 * lancés séparement pour permettre le requetage simultané des informations
 *
 */
public class CountInfosTimbresListener extends AbstractSyncEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(CountInfosTimbresListener.class);

    public static final String DTO_TOTAL = "total";
    public static final Long ERROR_RECUP_COUNT = -100L;

    public CountInfosTimbresListener() {
        super();
    }

    @Override
    protected boolean accept(Event event) {
        return true;
    }

    @Override
    protected void doHandleEvent(Event event) {
        CoreSession session = event.getContext().getCoreSession();
        final Map<String, Serializable> properties = event.getContext().getProperties();

        @SuppressWarnings("unchecked")
        final Map<String, OrganigrammeNodeTimbreDTO> mapDto = (Map<String, OrganigrammeNodeTimbreDTO>) properties.get(
            ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT
        );

        try {
            countClosedQuestions(mapDto, event.getContext().getCoreSession());
        } catch (NuxeoException exc) {
            LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
            setErrorCountOnClosed(mapDto);
            mapDto.get(DTO_TOTAL).setCountClose(0L);
        }

        try {
            countSignedQuestions(mapDto, session);
        } catch (NuxeoException exc) {
            LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
            setErrorCountOnSigned(mapDto);
            mapDto.get(DTO_TOTAL).setCountSigne(0L);
        }

        try {
            countMigrableQuestions(mapDto, session);
        } catch (NuxeoException exc) {
            LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
            setErrorCountOnMigrable(mapDto);
            mapDto.get(DTO_TOTAL).setCountMigrable(0L);
        }

        try {
            countModelFDR(mapDto, session);
        } catch (NuxeoException exc) {
            LOGGER.error(ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
            setErrorCountOnModelFDR(mapDto);
            mapDto.get(DTO_TOTAL).setCountModelFDR(0L);
        }
    }

    private void countClosedQuestions(final Map<String, OrganigrammeNodeTimbreDTO> mapDto, CoreSession session) {
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
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
            setErrorCountOnClosed(mapDto);
        }
        mapDto.get(DTO_TOTAL).setCountClose(totalCountClose);
    }

    /**
     * Renseigne la valeur -100 pour les questions fermées pour tous les noeuds
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

    private void countSignedQuestions(final Map<String, OrganigrammeNodeTimbreDTO> mapDto, CoreSession session) {
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
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
            setErrorCountOnSigned(mapDto);
        }
        mapDto.get(DTO_TOTAL).setCountSigne(totalCountSigned);
    }

    /**
     * Renseigne la valeur -100 pour les questions signées pour tous les noeuds
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

    private void countMigrableQuestions(final Map<String, OrganigrammeNodeTimbreDTO> mapDto, CoreSession session) {
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
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
            setErrorCountOnMigrable(mapDto);
        }
        mapDto.get(DTO_TOTAL).setCountMigrable(totalCountMigrable);
    }

    /**
     * Renseigne la valeur -100 pour les questions migrables (en attente de réponse)
     * pour tous les noeuds
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

    private void countModelFDR(final Map<String, OrganigrammeNodeTimbreDTO> mapDto, CoreSession session) {
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
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, exc);
            setErrorCountOnModelFDR(mapDto);
        }
        mapDto.get(DTO_TOTAL).setCountModelFDR(totalCountModelFDR);
    }

    /**
     * Renseigne la valeur -100 pour les modèles de FDR pour tous les noeuds
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
}
