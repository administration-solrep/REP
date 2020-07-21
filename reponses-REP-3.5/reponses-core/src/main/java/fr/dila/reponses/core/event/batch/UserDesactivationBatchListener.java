package fr.dila.reponses.core.event.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Batch de désactivation des utilisateurs et d'information de l'administrateur.
 * 
 * @author arn
 */
public class UserDesactivationBatchListener extends AbstractBatchEventListener {

    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(UserDesactivationBatchListener.class);
    private long nbUserDesactivated = 0;

    public UserDesactivationBatchListener(){
    	super(LOGGER, ReponsesEventConstant.USER_DESACTIVATION_BATCH_EVENT);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_DEACTIVATE_USERS_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        // Récupération des utilisateurs à désactiver
        List<String> userNameInfoList = new ArrayList<String>();
        try {
            userNameInfoList = getUsersToDisable(session);
        } catch (ClientException e1) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_USER_TEC, e1);
            ++errorCount;
        }

        final STParametreService paramService = STServiceLocator.getSTParametreService();

        // envoi du mail à l'administrateur
        if (!userNameInfoList.isEmpty()) {
            final ProfileService profileService = STServiceLocator.getProfileService();
            final ReponsesMailService mailService = ReponsesServiceLocator.getReponsesMailService();
            List<STUser> users = new ArrayList<STUser>();
            try {
                users = profileService.getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);
            } catch (ClientException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_USER_TEC, ce);
                ++errorCount;
            }
            // on récupère le message et l'objet du mail
            StringBuilder text = new StringBuilder();
            String object = null;
            try {
                text.append(paramService.getParametreValue(session, STParametreConstant.NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT));
                // on ajoute la liste des utilisateurs dans le message
                text.append(StringUtil.join(userNameInfoList, ", "));
                object = paramService.getParametreValue(session, STParametreConstant.NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET);
            } catch (ClientException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, ce);                
            }
            // envoi du mail
            if (object != null && text != null && !users.isEmpty()) {
                try {
                    mailService.sendMailToUserList(session, users, object, text.toString());
                } catch (ClientException ce) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, ce);
                    ++errorCount;
                }
            } else {
                LOGGER.warn(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, "Objet, message ou liste destinataires vide");
            }
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Nombre d'utilisateurs désactivés", nbUserDesactivated, endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_DEACTIVATE_USERS_TEC);
    }

    /**
     * Méthode renvoyant une liste des noms des utilisateurs à désactiver suite à une non connexion depuis un temps prédéfini dans les paramètres de l'application
     * 
     * @param session
     * @return userNameInfoList
     * @throws ClientException
     */
    protected List<String> getUsersToDisable(CoreSession session) throws ClientException {
        // initialisation de la liste des utilisateurs
        final List<String> userNameInfoList = new ArrayList<String>();

        // date de connexion en dessous de laquelle on supprime l'utilisateur
        final Calendar delaiMaxDesactivation = new GregorianCalendar();
        // date de connexion en dessous de laquelle on informe l'administrateur
        final Calendar delaiMaxInformation = new GregorianCalendar();
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        // on récupère le paramètre sur la date maximal de déconnexion pour supprimer un utilisateur en mois
        String delaiDesactivation = paramService.getParametreValue(session, STParametreConstant.USER_DECONNEXION_DESACTIVATION_DELAI);
        // calcul de la date au dessous de laquelle on déverrouille les documents
        delaiMaxDesactivation.add(Calendar.MONTH, -Integer.parseInt(delaiDesactivation));

        // on récupère le paramètre sur la date maximal de déconnexion pour informer l'administrateur en mois
        String delaiInformation = paramService.getParametreValue(session, STParametreConstant.USER_DECONNEXION_INFORMATION_DELAI);
        // calcul de la date au dessous de laquelle on informe l'administrateur
        delaiMaxInformation.add(Calendar.MONTH, -Integer.parseInt(delaiInformation));

        // récupération de tous les utilisateurs : on ne peut pas filtrer le LDAP sur une date
        final SSUtilisateurConnectionMonitorService utilisateurConnectionService = SSServiceLocator.getUtilisateurConnectionMonitorService();
        final UserManager userManager = STServiceLocator.getUserManager();
        Map<String, Serializable> filter = new HashMap<String, Serializable>();
        final DocumentModelList userModelList = userManager.searchUsers(filter, null);

        UnrestrictedSessionRunner unrestrictedSession = new UnrestrictedSessionRunner(session) {

            @Override
            public void run() throws ClientException {
                for (DocumentModel userDocModel : userModelList) {
                    SSInfoUtilisateurConnection infoUserConnection = null;
                    // on récupère le STUser
                    STUser user = userDocModel.getAdapter(STUser.class);
                    // on recupere le workspace associé
                    try {
                    	infoUserConnection = utilisateurConnectionService.getInfoUtilisateurConnection(session, user.getUsername());
                    } catch (ClientException ce) {
                        LOGGER.error(session, STLogEnumImpl.FAIL_GET_UW_TEC, user.getDocument(), ce);
                        ++errorCount;
                    }                    
                    if (infoUserConnection != null) {
                        Calendar dateLastConnection = infoUserConnection.getDateDerniereConnexion();
                        if (dateLastConnection == null) {
                            LOGGER.debug(session, ReponsesLogEnumImpl.PROCESS_B_DEACTIVATE_USERS_TEC, "utilisateur jamais connecte : " + userDocModel.getId());
                        } else if (delaiMaxDesactivation.compareTo(dateLastConnection) > 0) {
                            // suppression de l'utilisateur
                            LOGGER.info(session, STLogEnumImpl.DEL_USER_TEC, userDocModel);
                            userManager.deleteUser(user.getDocument());
                            ++nbUserDesactivated;
                            session.save();
                        } else if (delaiMaxInformation.compareTo(dateLastConnection) > 0) {
                            // mise à jour
                            if (userDocModel.getId() != null && !userDocModel.getId().isEmpty()) {
                                userNameInfoList.add(userDocModel.getId());
                            }
                        }
                    }
                    commitAndRestartTransaction(session, true);
                }
            }
        };
        unrestrictedSession.run();        
        return userNameInfoList;
    }
}
