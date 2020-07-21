package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.service.STAlertService;

public interface ReponsesAlertService extends STAlertService {

    /**
     * Créé une alerte activée, à partir d'une requête 
     * @param session
     * @param requeteDoc la requête pour l'alerte
     * @return Alert l'alerte créée, null si la création a échoué
     */
    Alert initAlertFromRequete(final CoreSession session, final DocumentModel requeteDoc);
    
}
