package fr.dila.reponses.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.st.api.organigramme.OrganigrammeNode;

/**
 * Service de migration
 * 
 */
public interface ReponsesMigrationService {

	void migrateAllCloseDossierMinistereRattachement(CoreSession session, String oldMinistereId,
			OrganigrammeNode newMinistere, ReponsesLoggingLine reponsesLoggingLine, ReponsesLogging reponsesLogging)
			throws ClientException;

	/**
	 * Migre les dossier du ministère (oldMinistereId) vers le nouveau. Ajoute une étape pour réaffectation
	 * 
	 * @param session
	 * @param oldMinistereDto
	 * @param newMinistereId
	 * @param reponsesLoggingLine
	 * @param reponsesLogging
	 * @param newMailbox
	 * @param newPosteBdc
	 * @param mailboxId
	 * @return
	 * @throws ClientException
	 */
	void migrateAllDossiersForReaffectation(CoreSession session, OrganigrammeNodeTimbreDTO oldMinistereDto,
			OrganigrammeNode newMinistere, String oldMailboxId, String newMailboxId,
			ReponsesLoggingLine reponsesLoggingLine, ReponsesLogging reponsesLogging, Mailbox newMailbox,
			OrganigrammeNode newPosteBdc) throws ClientException;

	/**
	 * Retourne la liste des questions concernées par la brisure du cachet serveur pour le ministère passé en paramètre
	 * 
	 * @param session
	 * @param ministereId
	 *            : identifiant du ministère dont on souhaite briser le cachet ou ALL si on souhaite briser le cachet de
	 *            tous les ministères
	 * @return la liste des questions si on en a trouvé, une liste vide sinon
	 */
	List<DocumentModel> getLstDossiersEligibleBriseSignature(CoreSession session, String ministereId);

	/**
	 * Effectue l'action de brisure de cachet serveur sur les dossiers de questions passés en paramètres (ainsi que les
	 * questions alloties)
	 * 
	 * @param session
	 * @param dossiersDocList
	 * @param lstDossiersEnErreur
	 *            : liste orgine - question des dossiers dont le traitement n'a pas pu être effectué
	 * @return
	 * @throws ClientException
	 */
	List<String> briserSignatureDossiers(CoreSession session, List<DocumentModel> dossiersDocList,
			final List<String> lstDossiersEnErreur) throws ClientException;

	/**
	 * Migre les dossiers clos
	 */
	void migrateDossierClos(CoreSession session, String oldMinistereId, OrganigrammeNode newMinistere,
			ReponsesLoggingLine reponsesLoggingLine, ReponsesLogging reponsesLogging) throws ClientException;
}
