package fr.dila.reponses.core.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.logger.MigrationLogger;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SessionUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Gestion de la migration d'un gouvernement.
 * 
 * @author asatre
 */
public class MigrationGouvernementListener implements PostCommitEventListener {

	private static final STLogger					LOGGER					= STLogFactory
																					.getLog(MigrationGouvernementListener.class);

	// Constantes de chaines de caractères pour logs
	private static final String						CLOSED_BRACKET			= "]";
	private static final String						DASH					= "-";
	private static final String						BRACKET_VERS_BRACKET	= "] vers [";
	private static final String						MIGRATION_ORGA			= "Migration de l'organigramme : [";
	private static final String						CHGT_GOUVT				= "Changement de gouvernement : [";

	// Les services déclarés ici. Instanciés dans initServices
	private OrganigrammeService						organigrammeService;
	private STGouvernementService					gouvernementService;
	private STMinisteresService						ministeresService;
	private STPostesService							postesService;
	private MailboxPosteService						mailboxPosteService;
	private FeuilleRouteModelService				feuilleRouteModelService;
	private ReponsesMigrationService				migrationService;
	private UpdateTimbreService						updateTimbreService;

	// Les paramètres passés à l'event au fire. Récupérés par getPropertiesEvent
	private Map<String, String>						newTimbre;
	private String									nextGouvernement;
	private String									currentGouvernement;
	private String									reponsesLoggingId;
	private String									newTimbreUnchangeEntity;
	private String									newTimbreDeactivateEntity;
	private Map<String, OrganigrammeNodeTimbreDTO>	mapDto;

	public MigrationGouvernementListener() {
		super();
	}

	@Override
	public void handleEvent(final EventBundle events) throws ClientException {
		if (events.containsEventName(ReponsesEventConstant.MIGRATION_GVT_EVENT)) {
			for (final Event event : events) {
				handleEvent(event);
			}
		}
	}

	private void initServices() {
		organigrammeService = STServiceLocator.getOrganigrammeService();
		gouvernementService = STServiceLocator.getSTGouvernementService();
		ministeresService = STServiceLocator.getSTMinisteresService();
		postesService = STServiceLocator.getSTPostesService();
		mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		migrationService = ReponsesServiceLocator.getReponsesMigrationService();
		updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
	}

	@SuppressWarnings("unchecked")
	private void getPropertiesEvent(final Event event) {
		final Map<String, Serializable> properties = event.getContext().getProperties();
		newTimbre = (Map<String, String>) properties.get(ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP);
		nextGouvernement = (String) properties.get(ReponsesEventConstant.MIGRATION_GVT_NEXT_GVT);
		currentGouvernement = (String) properties.get(ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT);
		reponsesLoggingId = (String) properties.get(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING);
		newTimbreUnchangeEntity = (String) properties.get(ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY);
		newTimbreDeactivateEntity = (String) properties.get(ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY);
		mapDto = (Map<String, OrganigrammeNodeTimbreDTO>) properties.get(ReponsesEventConstant.NEW_TIMBRE_MAP);
	}

	protected void handleEvent(final Event event) throws ClientException {

		if (!event.getName().equals(ReponsesEventConstant.MIGRATION_GVT_EVENT)) {
			return;
		}
		final MigrationLogger migrationLogger = MigrationLogger.getInstance();
		CoreSession coreSession = null;

		initServices();

		getPropertiesEvent(event);

		try {
			coreSession = SessionUtil.getCoreSession();
			migrationLogger.logMigration(ReponsesLogEnumImpl.MIGRATE_TIMBRE_TEC, "Début de la migration des timbres");
			gouvernementService.setDateNewGvt(currentGouvernement, nextGouvernement);
			// Exécution de la migration dans une session administrator
			runMigrationUnrestricted(coreSession);
			migrationLogger.logMigration(ReponsesLogEnumImpl.MIGRATE_TIMBRE_TEC, "Fin de la migration des timbres");
		} finally {

			SessionUtil.close(coreSession);
		}
	}

	private void runMigrationUnrestricted(final CoreSession coreSession) throws ClientException {
		new UnrestrictedSessionRunner(coreSession) {
			@Override
			public void run() throws ClientException {
				final MigrationLogger migrationLogger = MigrationLogger.getInstance();
				final Set<OrganigrammeNode> bdcToCancel = new HashSet<OrganigrammeNode>();
				final Map<String, List<OrganigrammeNode>> posteBdcToMigrate = new HashMap<String, List<OrganigrammeNode>>();
				final ReponsesLogging reponsesLogging = session.getDocument(new IdRef(reponsesLoggingId)).getAdapter(
						ReponsesLogging.class);

				Boolean hasError = Boolean.FALSE;

				for (Entry<String, String> entry : newTimbre.entrySet()) {
					final String currentMinId = entry.getKey();

					ReponsesLoggingLine reponsesLoggingLine = null;
					List<String> listLog = new ArrayList<String>();

					OrganigrammeNodeTimbreDTO currentMinDto = mapDto.get(currentMinId);
					try {
						final String newMin = entry.getValue();
						final DocumentModel lineDoc = createLine(session, reponsesLogging, DASH + currentMinId + DASH
								+ newMin);
						reponsesLoggingLine = lineDoc.getAdapter(ReponsesLoggingLine.class);

						final List<String> listLine = reponsesLogging.getReponsesLoggingLines();
						listLine.add(reponsesLoggingLine.getDocument().getId());

						reponsesLogging.setReponsesLoggingLines(listLine);
						reponsesLogging.save(session);

						reponsesLoggingLine.setStartDate(Calendar.getInstance());
						reponsesLoggingLine.setPrevisionalCount(ReponsesLoggingLine.DASH_COUNT);

						listLog = reponsesLoggingLine.getFullLog();

						if (newTimbreDeactivateEntity.equals(newMin)) {
							deactivateEntity(session, reponsesLoggingLine, listLog, currentMinDto);
						} else if (newTimbreUnchangeEntity.equals(newMin)) {
							migrateOldEntityToNewGouv(session, reponsesLoggingLine, listLog, currentMinDto);
						} else {
							// On met à jour les colonnes de questions à migrer/questions migrées
							long previsionnalCount = currentMinDto.getCountMigrable() + currentMinDto.getCountSigne();
							if (currentMinDto.getMigratingDossiersClos()) {
								// En cas de migration de questions closes, on ajoute le nombre au total déjà calculé
								previsionnalCount += currentMinDto.getCountClose();
							}
							reponsesLoggingLine.setPrevisionalCount(previsionnalCount);

							final OrganigrammeNode newMinistere = ministeresService.getEntiteNode(newMin);
							migrateOldEntityToNewEntity(session, reponsesLoggingLine, listLog, currentMinDto,
									bdcToCancel, newMinistere, reponsesLogging, posteBdcToMigrate);

							if (ReponsesLoggingStatusEnum.FAILURE.equals(reponsesLoggingLine.getStatus())) {
								hasError = Boolean.TRUE;
							} else {
								reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
							}
							addLogToLine(reponsesLoggingLine, listLog, session, "Fin changement de gouvernement : ["
									+ currentMinDto.getLabel() + BRACKET_VERS_BRACKET + newMinistere.getLabel()
									+ CLOSED_BRACKET, STLogEnumImpl.MIGRATE_MINISTERE_TEC);
						}
					} catch (final Exception e) {
						rollbackAndCommitTransaction();
						// on catch tout pour mettre le status en failure
						if (reponsesLoggingLine != null) {
							reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);
						}
						String message = "Erreur lors de la migration du ministère : " + currentMinId;
						listLog.add(message);
						migrationLogger.logErrorMigration(STLogEnumImpl.FAIL_MIGRATE_MINISTERE_TEC, message, e);

						hasError = Boolean.TRUE;
					}
					// On met à jour la ligne d'historique fille
					reponsesLoggingLine.setEndDate(Calendar.getInstance());
					reponsesLoggingLine.setFullLog(listLog);
					reponsesLoggingLine.save(session);
					session.save();
				}
				setEndToLogging(session, reponsesLogging, hasError);
				// cancel bdc
				postesService.deactivateBdcPosteList(new ArrayList<OrganigrammeNode>(bdcToCancel));
				// migrate bdc user
				postesService.addBdcPosteToNewPosteBdc(posteBdcToMigrate);

				saveAndCommitTransaction(session);
			}
		}.runUnrestricted();
	}

	/**
	 * Renseigne la date de fin et la statut de l'historique global. Termine par un reponsesLogging.save
	 * 
	 * @param session
	 * @param reponsesLogging
	 * @param endCount
	 * @param hasError
	 * @throws ClientException
	 */
	private void setEndToLogging(final CoreSession session, final ReponsesLogging reponsesLogging,
			final Boolean hasError) throws ClientException {
		reponsesLogging.setEndDate(Calendar.getInstance());
		if (hasError) {
			reponsesLogging.setStatus(ReponsesLoggingStatusEnum.FAILURE);
		} else {
			reponsesLogging.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
		}
		reponsesLogging.save(session);
	}

	/**
	 * Désactive un ministère
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param currentMinDto
	 * @throws ClientException
	 */
	private void deactivateEntity(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, final OrganigrammeNodeTimbreDTO currentMinDto) throws ClientException {
		reponsesLoggingLine.setMessage("Migration de l'organigramme : désactivation du ministère ["
				+ currentMinDto.getLabel() + CLOSED_BRACKET);
		organigrammeService.disableNodeFromDnNoChildrenCheck(currentMinDto.getId(), currentMinDto.getType());
		listLog.add("Désactivation du ministere");
		addLogAfterMigrationEntite(session, reponsesLoggingLine, listLog, currentMinDto);
	}

	/**
	 * Migre un ministère de l'ancien gouvernement vers le nouveau gouvernement
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param currentMinDto
	 * @throws ClientException
	 */
	private void migrateOldEntityToNewGouv(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, final OrganigrammeNodeTimbreDTO currentMinDto) throws ClientException {
		reponsesLoggingLine.setMessage(MIGRATION_ORGA + currentMinDto.getLabel() + BRACKET_VERS_BRACKET
				+ currentMinDto.getLabel() + CLOSED_BRACKET);
		ministeresService.migrateUnchangedEntiteToNewGouvernement(currentMinDto.getId(), nextGouvernement);
		listLog.add("Déplacement du ministere");
		addLogAfterMigrationEntite(session, reponsesLoggingLine, listLog, currentMinDto);
	}

	/**
	 * ajoute le listLog, le compte final (migrable + signées) aux questions migrées et le status succès à l'historique
	 * fils
	 * 
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param currentMinDto
	 * @throws ClientException
	 */
	private void addLogAfterMigrationEntite(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, final OrganigrammeNodeTimbreDTO currentMinDto) throws ClientException {
		reponsesLoggingLine.setFullLog(listLog);
		reponsesLoggingLine.setEndCount(ReponsesLoggingLine.DASH_COUNT);
		reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
		reponsesLoggingLine.save(session);
	}

	/**
	 * Effectue la migration des dossiers d'un ministère de l'ancien gouvernement, vers le ministère qui sera présent
	 * dans le nouveau
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param currentMinDto
	 * @param bdcToCancel
	 * @param newMinistere
	 * @param reponsesLogging
	 * @param posteBdcToMigrate
	 * @throws ClientException
	 */
	private void migrateOldEntityToNewEntity(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, final OrganigrammeNodeTimbreDTO currentMinDto,
			final Set<OrganigrammeNode> bdcToCancel, final OrganigrammeNode newMinistere,
			final ReponsesLogging reponsesLogging, final Map<String, List<OrganigrammeNode>> posteBdcToMigrate)
			throws ClientException {

		OrganigrammeNode newPosteBdc = postesService.getPosteBdcInEntite(newMinistere.getId());
		OrganigrammeNode oldPosteBdc = postesService.getPosteBdcInEntite(currentMinDto.getId());

		final MigrationLogger migrationLogger = MigrationLogger.getInstance();

		final List<OrganigrammeNode> newMinBdcList = postesService.getPosteBdcListInEntite(newMinistere.getId());
		if (!newMinBdcList.isEmpty()) {
			newMinBdcList.removeAll(bdcToCancel);
			if (!newMinBdcList.isEmpty()) {
				newPosteBdc = newMinBdcList.get(0);
			}
		}
		final List<OrganigrammeNode> currentMinBdcList = postesService.getPosteBdcListInEntite(currentMinDto.getId());
		if (!currentMinBdcList.isEmpty()) {
			bdcToCancel.addAll(currentMinBdcList);
			oldPosteBdc = currentMinBdcList.get(0);
		}

		String message = MIGRATION_ORGA + currentMinDto.getLabel() + BRACKET_VERS_BRACKET + newMinistere.getLabel()
				+ CLOSED_BRACKET;
		reponsesLoggingLine.setMessage(message);
		migrationLogger.logMigration(STLogEnumImpl.MIGRATE_MINISTERE_TEC, message);

		if (newPosteBdc == null) {
			migrateFdrImpossible(reponsesLoggingLine, listLog, newMinistere);
		} else if (oldPosteBdc == null || currentMinBdcList.isEmpty()) {
			// pas de poste bdc dans l'ancien ministere => migration de l'organigramme et de Modele de FDR mais pas de
			// migration de dossiers
			migrateOrgaAndFdr(session, reponsesLoggingLine, listLog, newMinistere, currentMinDto, newPosteBdc);
		} else {
			migrateModeleOrgaAndDossier(session, reponsesLoggingLine, listLog, currentMinDto, newMinistere,
					reponsesLogging, posteBdcToMigrate, oldPosteBdc, newPosteBdc);
		}
	}

	/**
	 * Méthode pour le cas où la migration des modèles de feuilles de route est impossible
	 * 
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param newMinistere
	 */
	private void migrateFdrImpossible(final ReponsesLoggingLine reponsesLoggingLine, final List<String> listLog,
			final OrganigrammeNode newMinistere) {
		String message = "Migration des feuilles de route impossible. Pas de poste BDC dans le nouveau ministère : "
				+ newMinistere.getLabel();
		listLog.add(message);
		MigrationLogger.getInstance().logMigration(SSLogEnumImpl.FAIL_MIGRATE_FDR_TEC, message);
		reponsesLoggingLine.setFullLog(listLog);
		reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);
	}

	/**
	 * Effectue la migration des sous noeuds du ministère ancien et des modèles de feuille de route vers le ministère du
	 * nouveau gouvernement. La migration des dossiers n'est pas faite. Cas lorsqu'aucun poste BDC n'existe dans le
	 * ministère ancien
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param newMinistere
	 * @param currentMinDto
	 * @param newPosteBdc
	 * @throws ClientException
	 */
	private void migrateOrgaAndFdr(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, OrganigrammeNode newMinistere, final OrganigrammeNodeTimbreDTO currentMinDto,
			final OrganigrammeNode newPosteBdc) throws ClientException {

		// pas de poste bdc dans l'ancien ministere => migration de l'organigramme et de Modele de FDR mais pas de
		// migration de dossiers
		addLogToLine(reponsesLoggingLine, listLog, session, CHGT_GOUVT + currentMinDto.getLabel()
				+ BRACKET_VERS_BRACKET + newMinistere.getLabel() + CLOSED_BRACKET, STLogEnumImpl.MIGRATE_MINISTERE_TEC);

		migrateEntiteToNewGouvernement(session, newMinistere, reponsesLoggingLine, currentMinDto);

		final Mailbox newMailboxBdc = mailboxPosteService.getOrCreateMailboxPosteNotUnrestricted(session,
				newPosteBdc.getId());
		final String newMailboxBdcId = (String) newMailboxBdc.getDocument().getProperty(
				STSchemaConstant.MAILBOX_SCHEMA, STSchemaConstant.MAILBOX_ID_PROPRETY);

		addLogToLine(reponsesLoggingLine, listLog, session, "Migration des modèles de feuille de route",
				SSLogEnumImpl.MIGRATE_MOD_FDR_TEC);

		saveAndCommitTransaction(session);

		feuilleRouteModelService.migrateMinistereFeuilleRouteModel(session, currentMinDto.getId().toString(),
				newMinistere.getId().toString(), null, newMailboxBdcId, reponsesLoggingLine);

		addLogToLine(reponsesLoggingLine, listLog, session, "Fin de migration des modèles de feuille de route",
				SSLogEnumImpl.MIGRATE_MOD_FDR_TEC);
		addLogToLine(reponsesLoggingLine, listLog, session, "Migration des dossiers impossible pas de poste bdc dans "
				+ currentMinDto.getLabel(), STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC);

		reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
	}

	/**
	 * Méthode de migration nominale : déplace les sous noeud du ministère vers le nouveau ministère, migre les modèles
	 * de feuille de route, et migre les dossiers (et les dossiers clos si sélectionné).
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 *            : l'historique de migration fils (ministère vers ministère)
	 * @param listLog
	 * @param currentMinDto
	 * @param bdcToCancel
	 * @param newMinistere
	 * @param reponsesLogging
	 *            : l'historique de migration global
	 * @param posteBdcToMigrate
	 * @param oldPosteBdc
	 * @param newPosteBdc
	 * @throws ClientException
	 */
	private void migrateModeleOrgaAndDossier(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final List<String> listLog, final OrganigrammeNodeTimbreDTO currentMinDto,
			final OrganigrammeNode newMinistere, final ReponsesLogging reponsesLogging,
			final Map<String, List<OrganigrammeNode>> posteBdcToMigrate, final OrganigrammeNode oldPosteBdc,
			final OrganigrammeNode newPosteBdc) throws ClientException {

		if (posteBdcToMigrate.get(oldPosteBdc.getId()) == null) {
			posteBdcToMigrate.put(oldPosteBdc.getId(), new ArrayList<OrganigrammeNode>());
		}
		final List<OrganigrammeNode> list = posteBdcToMigrate.get(oldPosteBdc.getId());
		list.add(newPosteBdc);
		posteBdcToMigrate.put(oldPosteBdc.getId(), list);

		addLogToLine(reponsesLoggingLine, listLog, session, CHGT_GOUVT + currentMinDto.getLabel()
				+ BRACKET_VERS_BRACKET + newMinistere.getLabel() + CLOSED_BRACKET, STLogEnumImpl.MIGRATE_MINISTERE_TEC);

		migrateEntiteToNewGouvernement(session, newMinistere, reponsesLoggingLine, currentMinDto);

		final Mailbox oldMailboxBdc = mailboxPosteService.getOrCreateMailboxPosteNotUnrestricted(session,
				oldPosteBdc.getId());
		final String oldMailboxBdcId = (String) oldMailboxBdc.getDocument().getProperty(
				STSchemaConstant.MAILBOX_SCHEMA, STSchemaConstant.MAILBOX_ID_PROPRETY);

		final Mailbox newMailboxBdc = mailboxPosteService.getOrCreateMailboxPosteNotUnrestricted(session,
				newPosteBdc.getId());
		final String newMailboxBdcId = (String) newMailboxBdc.getDocument().getProperty(
				STSchemaConstant.MAILBOX_SCHEMA, STSchemaConstant.MAILBOX_ID_PROPRETY);

		// Migration des modèles de feuille de route
		addLogToLine(reponsesLoggingLine, listLog, session, "Migration des modèles de feuille de route",
				SSLogEnumImpl.MIGRATE_MOD_FDR_TEC);

		saveAndCommitTransaction(session);

		try {
			feuilleRouteModelService.migrateMinistereFeuilleRouteModel(session, currentMinDto.getId().toString(),
					newMinistere.getId().toString(), oldMailboxBdcId, newMailboxBdcId, reponsesLoggingLine);
		} catch (final Exception e) {
			String messageLog = "Erreur lors de la migration des modele de feuille de route du ministère : "
					+ currentMinDto.getId();
			logMigrationFailure(session, reponsesLoggingLine, SSLogEnumImpl.FAIL_MIGRATE_MOD_FDR_TEC, messageLog, e);
		}
		addLogToLine(reponsesLoggingLine, listLog, session, "Fin de migration des modèles de feuille de route",
				SSLogEnumImpl.MIGRATE_MOD_FDR_TEC);

		// Migration des dossiers en cours
		addLogToLine(reponsesLoggingLine, listLog, session, "Migration des dossiers", STLogEnumImpl.MIGRATE_DOSSIER_TEC);
		try {
			migrationService.migrateAllDossiersForReaffectation(session, currentMinDto, newMinistere, oldMailboxBdcId,
					newMailboxBdcId, reponsesLoggingLine, reponsesLogging, newMailboxBdc, newPosteBdc);
		} catch (ClientException ce) {
			String messageLog = "Erreur lors de la migration des dossiers du ministère : " + currentMinDto.getId();
			logMigrationFailure(session, reponsesLoggingLine, STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC, messageLog, ce);
		}
		addLogToLine(reponsesLoggingLine, listLog, session, "Fin de migration des dossiers",
				STLogEnumImpl.MIGRATE_DOSSIER_TEC);
		saveAndCommitTransaction(session);

		// Migration des dossiers clos si demandé
		if (currentMinDto.getMigratingDossiersClos()) {
			addLogToLine(reponsesLoggingLine, listLog, session, "Migration des questions closes",
					STLogEnumImpl.MIGRATE_DOSSIER_TEC);
			try {
				migrationService.migrateDossierClos(session, currentMinDto.getId(), newMinistere, reponsesLoggingLine,
						reponsesLogging);
				migrationService.migrateAllCloseDossierMinistereRattachement(session, currentMinDto.getId(),
						newMinistere, reponsesLoggingLine, reponsesLogging);
			} catch (ClientException ce) {
				String messageLog = "Erreur lors de la migration des dossiers clos du ministère : "
						+ currentMinDto.getId();
				logMigrationFailure(session, reponsesLoggingLine, STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC, messageLog,
						ce);
			}
			addLogToLine(reponsesLoggingLine, listLog, session, "Fin de migration des questions closes",
					STLogEnumImpl.MIGRATE_DOSSIER_TEC);
			reponsesLogging.save(session);
		}
		saveAndCommitTransaction(session);
	}

	/**
	 * Pour marquer l'historique de migration fils en failure.
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param logEnum
	 * @param messageLog
	 * @param exc
	 */
	private void logMigrationFailure(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final STLogEnum logEnum, final String messageLog, final Exception exc) {
		TransactionHelper.setTransactionRollbackOnly();
		TransactionHelper.commitOrRollbackTransaction();
		TransactionHelper.startTransaction();
		reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);

		MigrationLogger.getInstance().logErrorMigration(logEnum, messageLog, exc);
		try {
			reponsesLoggingLine.save(session);
			saveAndCommitTransaction(session);
		} catch (ClientException ce) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SAVE_SESSION_TEC, ce);
		}
	}

	/**
	 * Créé une nouvelle ligne dans l'historique de migration global
	 * 
	 * @param session
	 * @param reponsesLogging
	 * @param pos
	 * @return
	 * @throws ClientException
	 */
	private DocumentModel createLine(final CoreSession session, final ReponsesLogging reponsesLogging, String pos)
			throws ClientException {
		DocumentModel lineDoc = session
				.createDocumentModel(ReponsesLoggingConstant.REPONSES_LOGGING_LINE_DOCUMENT_TYPE);
		lineDoc.setPathInfo(reponsesLogging.getDocument().getPathAsString(),
				String.valueOf(Calendar.getInstance().getTimeInMillis()) + pos);
		lineDoc = session.createDocument(lineDoc);
		return lineDoc;
	}

	/**
	 * Met à jour le détail de l'historique de migration fils
	 * 
	 * @param reponsesLoggingLine
	 * @param listLog
	 * @param session
	 * @param message
	 * @param codeLog
	 * @throws ClientException
	 */
	private void addLogToLine(final ReponsesLoggingLine reponsesLoggingLine, final List<String> listLog,
			final CoreSession session, final String message, STLogEnum codeLog) throws ClientException {
		listLog.add(message);
		MigrationLogger.getInstance().logMigration(codeLog, message);
		reponsesLoggingLine.setFullLog(listLog);
		reponsesLoggingLine.save(session);
		session.save();
	}

	/**
	 * Migre un ministère de l'ancien gouvernement vers le nouveau gouvernement
	 * 
	 * @param session
	 * @param newMinistere
	 * @param reponsesLoggingLine
	 * @param currentMinDto
	 * @throws ClientException
	 */
	private void migrateEntiteToNewGouvernement(final CoreSession session, final OrganigrammeNode newMinistere,
			final ReponsesLoggingLine reponsesLoggingLine, final OrganigrammeNodeTimbreDTO currentMinDto)
			throws ClientException {
		String detailBriserSignature = briseMinistereSignature(session, reponsesLoggingLine, currentMinDto);
		DocumentModel detailDoc = session
				.createDocumentModel(ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_DOCUMENT_TYPE);
		detailDoc.setPathInfo(reponsesLoggingLine.getDocument().getPathAsString(),
				String.valueOf(Calendar.getInstance().getTimeInMillis()) + DASH + currentMinDto.getId() + DASH
						+ newMinistere.getId());

		detailDoc = session.createDocument(detailDoc);
		final ReponsesLoggingLineDetail reponsesLoggingLineDetail = detailDoc
				.getAdapter(ReponsesLoggingLineDetail.class);
		final List<String> listDetail = reponsesLoggingLine.getReponsesLoggingLineDetails();

		if (StringUtil.isNotBlank(detailBriserSignature)) {
			listDetail.add(detailBriserSignature);
		}

		listDetail.add(reponsesLoggingLineDetail.getDocument().getId());
		reponsesLoggingLine.setReponsesLoggingLineDetails(listDetail);
		reponsesLoggingLine.save(session);
		reponsesLoggingLineDetail.setMessage(MIGRATION_ORGA + currentMinDto.getLabel() + BRACKET_VERS_BRACKET
				+ newMinistere.getLabel() + CLOSED_BRACKET);

		final List<String> listLog = reponsesLoggingLineDetail.getFullLog();

		try {
			String message = "Début de la migration de l'organigramme [" + currentMinDto.getLabel()
					+ BRACKET_VERS_BRACKET + newMinistere.getLabel() + CLOSED_BRACKET;
			listLog.add(message);

			MigrationLogger.getInstance().logMigration(STLogEnumImpl.MIGRATE_MINISTERE_TEC, message);
			ministeresService.migrateEntiteToNewGouvernement(currentMinDto.getId(), newMinistere.getId());

			message = "Fin de la migration de l'organigramme [" + currentMinDto.getLabel() + BRACKET_VERS_BRACKET
					+ newMinistere.getLabel() + CLOSED_BRACKET;
			listLog.add(message);

			MigrationLogger.getInstance().logMigration(STLogEnumImpl.MIGRATE_MINISTERE_TEC, message);
			reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
		} catch (final Exception e) {
			// on catch tout pour mettre le status en failure
			reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.FAILURE);
			throw new ClientException(e);
		} finally {
			reponsesLoggingLineDetail.setFullLog(listLog);
			reponsesLoggingLineDetail.save(session);
			session.save();
		}
	}

	/**
	 * Si selectionné, la signature des réponses du ministère est brisée
	 * 
	 * @param session
	 * @param reponsesLoggingLine
	 * @param currentMinDto
	 * @return
	 */
	private String briseMinistereSignature(final CoreSession session, final ReponsesLoggingLine reponsesLoggingLine,
			final OrganigrammeNodeTimbreDTO currentMinDto) {
		String lineDetailId = "";
		List<String> listLog = new ArrayList<String>();

		// Cas du bris de la signature on a une migration en cours donc on tente de briser
		if (currentMinDto.getBreakingSeal()) {
			try {
				DocumentModel detailDoc = session
						.createDocumentModel(ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_DOCUMENT_TYPE);
				detailDoc.setPathInfo(reponsesLoggingLine.getDocument().getPathAsString(),
						String.valueOf(Calendar.getInstance().getTimeInMillis()) + DASH + currentMinDto.getId());

				detailDoc = session.createDocument(detailDoc);
				lineDetailId = detailDoc.getId();

				final ReponsesLoggingLineDetail reponsesLoggingLineDetail = detailDoc
						.getAdapter(ReponsesLoggingLineDetail.class);
				reponsesLoggingLineDetail.setMessage("Brisure du cachet des dossiers");
				listLog.add("Début du traitement");

				// On récupère les dossiers éligibles
				List<DocumentModel> lstDossiersABriser = updateTimbreService.getSignedDossiersForMinistere(session,
						currentMinDto.getId().toString());

				// Si on n'a rien à faire on l'indique
				if (lstDossiersABriser.isEmpty()) {
					listLog.add("Brisure du cachet serveur pour les questions du timbre : " + currentMinDto.getLabel()
							+ ", pas de dossiers à traiter");
					reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
				} else {
					// Sinon on tente de briser la signature et on indique si un souci est survenu
					listLog.add("Brisure du cachet serveur pour les questions du timbre : " + currentMinDto.getLabel()
							+ ", " + lstDossiersABriser.size() + " dossier(s) impacté(s)");
					List<String> lstDossiersEnErreur = new ArrayList<String>();
					List<String> lstDossierTraites = migrationService.briserSignatureDossiers(session,
							lstDossiersABriser, lstDossiersEnErreur);
					listLog.add("Liste des dossiers traités lors de la brisure du cachet : "
							+ StringUtil.join(lstDossierTraites, ";", "'"));

					// S'il reste des dossiers à traiter après le traitement on marque le traitement en erreur
					if (!lstDossiersEnErreur.isEmpty()) {
						String dossierConcat = StringUtil.join(lstDossiersEnErreur, ";", "'");
						listLog.add("Erreur de brisure du cachet des dossiers, les dossiers suivants n'ont PAS été traités : "
								+ dossierConcat);

						MigrationLogger.getInstance().logMigration(SSLogEnumImpl.FAIL_MIGRATE_FDR_TEC, dossierConcat);
						reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.FAILURE);
					} else {
						reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
					}
					listLog.add("Fin du traitement");
				}
				reponsesLoggingLineDetail.setFullLog(listLog);
				reponsesLoggingLineDetail.save(session);
				saveAndCommitTransaction(session);
			} catch (Exception ex) {
				rollbackAndCommitTransaction();
				MigrationLogger.getInstance().logErrorMigration(SSLogEnumImpl.FAIL_MIGRATE_FDR_TEC,
						"Une erreur est survenue lors de la brisure de la signature ", ex);
				// Si on a déjà commencé à sauvegarder le détail, on l'édite
				if (StringUtil.isNotBlank(lineDetailId)) {
					try {
						DocumentModel detailDoc = session.getDocument(new IdRef(lineDetailId));
						ReponsesLoggingLineDetail detail = detailDoc.getAdapter(ReponsesLoggingLineDetail.class);
						detail.setMessage("Brisure du cachet des dossiers");
						listLog.add("Erreur rencontrée lors de la brisure du cachet des dossiers : " + ex.getMessage());
						detail.setFullLog(listLog);
						detail.setStatus(ReponsesLoggingStatusEnum.FAILURE);
						detail.save(session);
						saveAndCommitTransaction(session);
					} catch (ClientException e) {
						MigrationLogger.getInstance().logMigration(SSLogEnumImpl.FAIL_MIGRATE_FDR_TEC,
								"Impossible de sauver l'erreur dans le detail de la migration");
						rollbackAndCommitTransaction();
					}
				}
			}
		}
		return lineDetailId;
	}

	/**
	 * sauvegarde la session, commit la transaction et en démarre une nouvelle
	 * 
	 * @param session
	 * @throws ClientException
	 */
	private void saveAndCommitTransaction(final CoreSession session) throws ClientException {
		session.save();
		TransactionHelper.commitOrRollbackTransaction();
		TransactionHelper.startTransaction();
	}

	/**
	 * rollback la transaction, commit la transaction et en démarre une nouvelle
	 * 
	 * @param session
	 * @throws ClientException
	 */
	private void rollbackAndCommitTransaction() {
		TransactionHelper.setTransactionRollbackOnly();
		TransactionHelper.commitOrRollbackTransaction();
		TransactionHelper.startTransaction();
	}

}
