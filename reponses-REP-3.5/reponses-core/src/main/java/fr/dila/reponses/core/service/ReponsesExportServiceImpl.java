package fr.dila.reponses.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.constant.ReponsesExceptionConstants;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STExportServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.ZipUtil;

/**
 * Service d'export des statistiques Reponses
 * 
 * 
 */
public class ReponsesExportServiceImpl extends STExportServiceImpl implements ReponsesExportService {

	protected static final String				EXPORT_STAT_DOCUMENT_NAME	= "Export-stat";
	protected static final String				EXPORT_STAT_FOLDER_ID		= "export-stat-root";
	protected static final String				EXPORT_STAT_PATH			= "/" + EXPORT_STAT_FOLDER_ID;
	protected static final String				EXPORT_STAT_FOLDER_TYPE		= "ExportStatRoot";

	private static final String					REPONSES_STR				= "REP-";
	private static final String					REPONSES_NAME_SEP			= "-";
	private static final String					BIRT_REPORT_ROOT			= SSBirtConstants.BIRT_REPORT_ROOT;
	private static final STLogger				LOGGER						= STLogFactory
																					.getLog(ReponsesExportServiceImpl.class);

	private static final Map<String, String>	correspNomsStats			= Collections
																					.unmodifiableMap(initNomsStats());

	private static Map<String, String> initNomsStats() {
		Map<String, String> nomsStats = new HashMap<String, String>();
		nomsStats.put("MIN01.", "MIN01. QE en cours - statistiques");
		nomsStats.put("MIN02.", "MIN02. QE étape en cours");
		nomsStats.put("MIN03.", "MIN03. QE signalées en cours de traitement");
		nomsStats.put("MIN04.", "MIN04. QE renouvelées");
		nomsStats.put("MIN05.", "MIN05. QE sans réponse publiée");
		nomsStats.put("MIN06.", "MIN06. QE traitées");
		nomsStats.put("MIN07.", "MIN07. QE traitées");
		nomsStats.put("MIN08.", "MIN08. Taux de réponse aux QE - Parlement");
		nomsStats.put("MIN09.", "MIN09. Taux de réponse aux QE - Sénat");
		nomsStats.put("MIN10.", "MIN10. Taux de réponse aux QE - AN");
		nomsStats.put("MIN11.", "MIN11. Taux de réponse aux QE");
		nomsStats.put("MIN12.", "MIN12. Nombre et répartition des QE");
		nomsStats.put("MIN13.", "MIN13. QE retirées");
		nomsStats.put("MIN14.", "MIN14. QE par groupe parlementaire");
		nomsStats.put("MIN15.", "MIN15. Données brutes");
		nomsStats.put("MIN16.", "MIN16. QE étape en cours par direction");
		nomsStats.put("MIN17.", "MIN17. QE signalées en cours de traitement par direction");
		nomsStats.put("SGG01.", "SGG01. QE en cours dans les ministères et au SGG");
		nomsStats.put("SGG02.", "SGG02. QE en cours - suivi par date");
		nomsStats.put("SGG03.", "SGG03. Variations mensuelles");
		return nomsStats;
	}

	/**
	 * Default constructor
	 */
	public ReponsesExportServiceImpl() {
		super();
		// do nothing
	}

	@Override
	public boolean isCurrentlyExportingStat(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		return this.isCurrentlyExporting(session, getExportStatDocForUser(session, user, userWorkspacePath));
	}

	@Override
	public boolean flagInitExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		return this.flagInitExport(session, getOrCreateExportStatDoc(session, user, userWorkspacePath));
	}

	@Override
	public void flagEndExportStatForUser(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		this.flagEndExport(session, getExportStatDocForUser(session, user, userWorkspacePath));
	}

	@Override
	public String getExportStatHorodatageRequest(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		return this.getExportHorodatageRequest(session, getExportStatDocForUser(session, user, userWorkspacePath));
	}

	@Override
	public DocumentModel getOrCreateExportStatDoc(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		DocumentModel exportStatDoc = getExportStatDocForUser(session, user, userWorkspacePath);
		if (exportStatDoc == null) {
			// Création du document
			String exportStatsFolderRoot = getOrCreateExportStatRootPath(session, userWorkspacePath);
			if (exportStatsFolderRoot != null) {
				exportStatDoc = session.createDocumentModel(exportStatsFolderRoot, EXPORT_STAT_DOCUMENT_NAME,
						ReponsesSchemaConstant.EXPORT_STAT_DOCUMENT_TYPE);
				ExportDocument exportDocument = exportStatDoc.getAdapter(ExportDocument.class);
				exportDocument.setOwner(user.getName());
				exportStatDoc = session.createDocument(exportStatDoc);
				session.save();
			}
		}
		return exportStatDoc;
	}

	@Override
	public DocumentModel getExportStatDocForUser(CoreSession session, SSPrincipal user, String userWorkspacePath)
			throws ClientException {
		DocumentModel exportStatRoot = getOrCreateExportStatRootDoc(session, userWorkspacePath);
		if (exportStatRoot == null) {
			return null;
		} else {
			DocumentModelList children = session.getChildren(exportStatRoot.getRef());
			if (children.size() > 0) {
				return children.get(0);
			} else {
				return null;
			}
		}
	}

	@Override
	public String getOrCreateExportStatRootPath(CoreSession session, String userWorkspacePath) throws ClientException {
		return this.getOrCreateExportDocumentRootPath(session, userWorkspacePath, EXPORT_STAT_FOLDER_ID,
				EXPORT_STAT_FOLDER_TYPE);
	}

	@Override
	public DocumentModel getOrCreateExportStatRootDoc(CoreSession session, String userWorkspacePath)
			throws ClientException {
		return this.getOrCreateExportDocumentRootDoc(session, userWorkspacePath, EXPORT_STAT_FOLDER_ID,
				EXPORT_STAT_FOLDER_TYPE);
	}

	@Override
	public void exportStat(CoreSession session, String userWorkspacePath, SSPrincipal userPrincipal,
			Map<String, String> reportsMultiExportMap, Map<String, String> reportsNamesMap,
			Map<String, String> reportsTitlesMap, List<String> formats) throws ClientException, IOException {
		DocumentModel exportStatDoc = getOrCreateExportStatDoc(session, userPrincipal, userWorkspacePath);

		if (exportStatDoc != null) {
			// Si le document d'export n'est pas null, on le traite
			if (reportsMultiExportMap.isEmpty() || reportsNamesMap.isEmpty() || reportsTitlesMap.isEmpty()) {
				throw new ReponsesException(ReponsesExceptionConstants.EMPTY_REPORTS_EXC);
			} else {
				if (reportsMultiExportMap.size() != reportsNamesMap.size()
						|| reportsNamesMap.size() != reportsTitlesMap.size()) {
					throw new ReponsesException(ReponsesExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC);
				}

				final ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
				final String name = generateNameForZip(exportStat, reportsTitlesMap);
				final File zipFile = File.createTempFile(name, STExportConstants.EXPORT_ZIP_EXTENSION);
				exportStatAndZipIt(zipFile, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
				FileInputStream zipStream = new FileInputStream(zipFile);
				Blob zipBlob = FileUtils.createSerializableBlob(zipStream, name
						+ STExportConstants.EXPORT_ZIP_EXTENSION, null);
				zipStream.close();
				zipBlob.setEncoding(STExportConstants.EXPORT_ENCODING);
				exportStat.setFileContent(zipBlob);
				exportStat.setTitle(name);

				session.saveDocument(exportStat.getDocument());
				session.save();

				if (!zipFile.delete()) {
					LOGGER.warn(session, STLogEnumImpl.FAIL_DEL_FILE_TEC, name);
				}
			}
		}
	}

	/**
	 * 
	 * @param exportStat
	 * @param reportsTitlesMap
	 *            : map des titres de l'export : ne doit pas être vide
	 * @return nom de l'export
	 */
	private String generateNameForZip(ExportDocument exportStat, Map<String, String> reportsTitlesMap) {
		Calendar dateRequest = exportStat.getDateRequest();
		StringBuilder nameSb = new StringBuilder();
		String dateRequestStr = null;
		if (dateRequest == null) {
			dateRequestStr = DateUtil.formatYYYYMMdd(Calendar.getInstance().getTime());
		} else {
			dateRequestStr = DateUtil.formatYYYYMMdd(dateRequest.getTime());
		}

		if (reportsTitlesMap.size() > 1) {
			nameSb.append("REP-STATS");
		} else if (reportsTitlesMap.size() == 1) {
			String title = StringUtil.removeSpacesAndAccent((String) reportsTitlesMap.values().toArray()[0]);
			String nomStatCourt = correspNomsStats.get(title.substring(0, 7));
			String debutNomStat;
			// Initialise le nom du fichier du rapport
			if (nomStatCourt == null) {
				debutNomStat = REPONSES_STR + title;
			} else {
				debutNomStat = REPONSES_STR + nomStatCourt;
			}
			nameSb.append(debutNomStat);
		}
		nameSb.append(REPONSES_NAME_SEP).append(dateRequestStr);
		return nameSb.toString();
	}

	/**
	 * Parcourt la map des rapports à exporter et génère les stats correspondantes dans les formats indiqués et pour
	 * tous les ministères ou directions en cours si le rapport est multi-ministère ou multi-direction
	 * 
	 * @param zipFile
	 * @param reportsMultiExportMap
	 * @param reportsNamesMap
	 * @param reportsTitlesMap
	 * @param formats
	 * @throws ClientException
	 * @throws IOException
	 */
	protected void exportStatAndZipIt(File zipFile, Map<String, String> reportsMultiExportMap,
			Map<String, String> reportsNamesMap, Map<String, String> reportsTitlesMap, List<String> formats)
			throws ClientException, IOException {
		final SSBirtService birtService = SSServiceLocator.getSSBirtService();

		if (birtService == null) {
			throw new ReponsesException(ReponsesExceptionConstants.SERVICE_NOT_AVAILABLE);
		}

		List<File> reports = new ArrayList<File>();
		
		// valeurs initialisée à la demande si une des entry de reportsMultiExportMap est true
		// (cf suite du code)
		List<EntiteNode> ministeresList = null;
		List<UniteStructurelleNode> directionsList = new ArrayList<UniteStructurelleNode>();
		Map<String, String> inputValues = null;

		final StringBuilder fileName = new StringBuilder();
		Map<String, String> emptyHashMap = new HashMap<String, String>();
		for (Entry<String, String> entry : reportsMultiExportMap.entrySet()) {
			String title = reportsTitlesMap.get(entry.getKey());
			final String name = reportsNamesMap.get(entry.getKey());

			String nomStatCourt = correspNomsStats.get(title.substring(0, 6));
			String debutNomStat;
			// Initialise le nom du fichier du rapport
			if (nomStatCourt == null) {
				debutNomStat = REPONSES_STR + title;
			} else {
				debutNomStat = REPONSES_STR + nomStatCourt;
			}
			fileName.append(debutNomStat);

			// si le boolean est vrai on doit générer le rapport pour tous les ministères
			if (entry.getValue().equals("MIN")) {
				// On récupère la liste des ministères en cours une seule fois, quand cela est nécessaire
				// (sinon on ne requête pas le ldap pour rien)
				if (inputValues == null) { // si valeur non initialisée
					ministeresList = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();
					inputValues = new HashMap<String, String>();
					inputValues.put("parameterType", "MIN");
				}
				
				for (OrganigrammeNode ministereNode : ministeresList) {
					inputValues.put("parameterValue", ministereNode.getId().toString());
					// On récupère le nom du ministère concerné
					String ministereName = ((EntiteNode) ministereNode).getEdition();
					fileName.append(REPONSES_NAME_SEP).append(ministereName);
					reports.addAll(birtService.generateReportFileResults(
							StringUtil.getValidFilename(fileName.toString()), BIRT_REPORT_ROOT + name,
							inputValues, formats).values());
					// On réinitialise le nom du fichier pour ne remettre que la partie commune
					fileName.delete(0, fileName.length());
					fileName.append(debutNomStat);
				}
			} else if (entry.getValue().equals("DIR")) {
				if (inputValues == null) { // si valeur non initialisée
					ministeresList = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();
					for (EntiteNode ministereNode : ministeresList) {
						directionsList.addAll(ministereNode.getSubUnitesStructurellesList());
					}
					directionsList.addAll(extractSubUnitesStructurellesRecursive(directionsList));
					inputValues = new HashMap<String, String>();
					inputValues.put("parameterType", "DIR");
				}

				for (OrganigrammeNode directionNode : directionsList) {
					inputValues.put("parameterValue", directionNode.getId());
					// On récupère le nom de la direction concernée
					String directionName = ((UniteStructurelleNode) directionNode).getLabel();
					fileName.append(REPONSES_NAME_SEP).append(directionName);
					reports.addAll(
							birtService.generateReportFileResults(StringUtil.getValidFilename(fileName.toString()),
									BIRT_REPORT_ROOT + name, inputValues, formats).values());
					// On réinitialise le nom du fichier pour ne remettre que la
					// partie commune
					fileName.delete(0, fileName.length());
					fileName.append(debutNomStat);
				}
			} else {
				reports.addAll(birtService.generateReportFileResults(
						StringUtil.removeSpacesAndAccent(fileName.toString()), BIRT_REPORT_ROOT + name, emptyHashMap,
						formats).values());
			}
			// On réinitialise la variable pour le nom du fichier du rapport
			fileName.delete(0, fileName.length());
		}

		ZipUtil.zipFiles(zipFile, reports, STExportConstants.EXPORT_ENCODING);
	}

	@Override
	public int removeOldExportStat(CoreSession session, Calendar dateLimit) throws ClientException {
		return super.removeOldExport(session, dateLimit, ReponsesSchemaConstant.EXPORT_STAT_DOCUMENT_TYPE);
	}

	private List<UniteStructurelleNode> extractSubUnitesStructurellesRecursive(
			List<UniteStructurelleNode> uniteStructurelles) throws ClientException {
		List<UniteStructurelleNode> usList = new ArrayList<UniteStructurelleNode>();
		List<UniteStructurelleNode> tmpList = new ArrayList<UniteStructurelleNode>();

		for (UniteStructurelleNode uniteStructurelle : uniteStructurelles) {
			tmpList = uniteStructurelle.getSubUnitesStructurellesList();
			if (tmpList != null && !tmpList.isEmpty()) {
				for (UniteStructurelleNode us : tmpList) {
					if (!us.getDeleted()) {
						usList.add(us);
					}
				}
			}
		}
		if (!usList.isEmpty()) {
			usList.addAll(extractSubUnitesStructurellesRecursive(usList));
		}
		return usList;
	}
}
