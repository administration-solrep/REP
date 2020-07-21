package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.caselink.DossierLink;

public interface DocumentRoutingService extends fr.dila.ss.api.service.DocumentRoutingService {

	/**
     * Retourne l'étape de l'instance de la feuille de route associé au caselink.
     * 
     * @return Étape de feuille de route en cours
     */
	DocumentModel getCurrentEtape(CoreSession session, DossierLink dossierLink)
			throws ClientException;
}
