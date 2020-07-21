package fr.dila.reponses.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STAlertServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

public class ReponsesAlertServiceImpl extends STAlertServiceImpl implements ReponsesAlertService {
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesAlertServiceImpl.class);
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public ReponsesAlertServiceImpl(){
    	super();
    }
    
    @Override
    public Alert initAlertFromRequete(final CoreSession session, final DocumentModel requeteDoc) {
        Alert alert = initAlert(session);
        if (alert == null) {
            return null;
        } else {
            // Si le document requete est null, on garde l'alerte désactivée par défaut
            if (requeteDoc == null) {
                LOGGER.warn(session, STLogEnumImpl.FAIL_CREATE_ALERT_TEC, "Requete is null - Alert deactivated");
            } else {
                alert.setIsActivated(true);
                alert.setRequeteId(requeteDoc.getId());
                String requeteTitle = "requête";
                try {
                    requeteTitle = requeteDoc.getTitle();
                } catch (ClientException ce) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, requeteDoc, ce);
                }
                alert.setTitle("Alerte sur " + requeteTitle);
                return alert;
            }            
        }
        return null;        
    }
    
    @Override
    public Boolean sendMail(CoreSession session, Alert alert) throws ClientException {
        RequeteExperte requeteExperte = alert.getRequeteExperte(session);
        if (requeteExperte == null){
            return false;
        }
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        STUserService userService = STServiceLocator.getSTUserService();
        List<String> recipients = userService.getEmailAddressFromUserList(alert.getRecipients()); 
        //récupération des résultats dossiers
        List<DocumentModel> dossiers = requeteurService.query(session, requeteExperte);
        String nomFichier = "Resultat_dossier_alerte.xls";
        DataSource fichierExcelResultat = ExcelUtil.creationListDossierExcel(session, dossiers);
        
        STParametreService paramService = STServiceLocator.getSTParametreService();        
        String content = paramService.getParametreValue(session, STParametreConstant.TEXTE_MAIL_NOTIFICATION_ALERTE);
        String objet = paramService.getParametreValue(session, STParametreConstant.OBJET_MAIL_NOTIFICATION_ALERTE);
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nb_resultats", String.valueOf(dossiers.size()));
        String corps = StringUtil.renderFreemarker(content, paramMap);
        
        Boolean isSent = true;
        STMailService mailService = STServiceLocator.getSTMailService();
        try {
            mailService.sendMailWithAttachement(recipients, objet, corps, nomFichier, fichierExcelResultat);
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
            isSent = false;
        }
        return isSent;
    }

}
