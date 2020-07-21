package fr.dila.reponses.core.event.batch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Batch d'envoi de mails d'archivage à l'administrateur fonctionnel
 * 
 * @author bgamard, spl
 */
public class ArchivageMailListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ArchivageMailListener.class);

    public ArchivageMailListener(){
    	super(LOGGER, ReponsesEventConstant.ARCHIVAGE_MAIL_BATCH_EVENT);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {

        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_ARCH_MAIL_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
        Long count = 0L;
        try {
            count = archiveService.countQuestionArchivable(session);
            if (count > 0) {
                final STMailService mailService = STServiceLocator.getSTMailService();
                final ProfileService profileService = STServiceLocator.getProfileService();
                List<STUser> users = profileService.getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);
                final STParametreService paramService = STServiceLocator.getSTParametreService();
            	String parametreObjet = paramService.getParametreValue(session, STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_OBJET);
            	String parametreText = paramService.getParametreValue(session, STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_TEXT);
            	String object = "[Réponses] Dossiers en attente d'élimination";
        		String text = count + " dossiers sont en attente d'élimination (fin de DUA atteinte)";
            	if(parametreObjet != null && !parametreObjet.isEmpty()){
            		object = parametreObjet; 
            	}
            	if(parametreText != null && !parametreText.isEmpty()
            			&& parametreText.contains("${nombreDossiers}") && parametreText.contains("${dureeDUA}")){
            		Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("nombreDossiers", String.valueOf(count));
                    paramMap.put("dureeDUA", paramService.getParametreValue(session, STParametreConstant.DELAI_CONSERVATION_DONNEES));
                    text = StringUtil.renderFreemarker(parametreText, paramMap);
            	}
                mailService.sendMailToUserList(session, users, object, text);
            }
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, "Archivage", e);
            errorCount++;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Nombre de dossiers en attente d'archivage",count, endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC,e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_ARCH_MAIL_TEC);
    }
}
