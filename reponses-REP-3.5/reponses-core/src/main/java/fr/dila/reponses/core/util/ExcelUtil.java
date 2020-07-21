package fr.dila.reponses.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.flux.DelaiCalculateur;
import fr.dila.reponses.core.recherche.ReponseDossierListingDTOImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.ss.api.constant.SSInjectionGouvernementConstants;
import fr.dila.ss.core.client.InjectionGvtDTOImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.STExcelUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Classe utilitaire pour créér des Documents Excel.
 * 
 */
public class ExcelUtil extends STExcelUtil {
	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(ExcelUtil.class);

	private static final String		DOSSIERS_SHEET_NAME	= "Resultats_requête";

	private static final String[]	DOSSIER_HEADER		= { "Origine", "N° Question", "Nature",
			"Date de publication JO", "Auteur", "Ministère attributaire", "Mots-clés", "Etat"};
	
	private static final String[]	DOSSIER_HEADER_MAIL		= { "Origine", "N° Question", "Nature",
			"Date de publication JO", "Auteur", "Ministère attributaire", "Direction étapes en cours", "Mots-clés", "Délai", "Etat", "Etape en cours" };

	private static final String[]	QUESTION_HEADER		= { "Question", "Nature (QE,QC)", "Date", "Auteur",
			"Ministère attributaire", "Ministère interrogé", "Indexation principale", "", "Délai", "Etat",
			"Etape en cours", "Législature"			};

	private ExcelUtil() {
		// Default constructor
	}

	/**
	 * Créé un fichier Excel contenant les informations souhaitées pour une liste de dossiers;
	 * 
	 * @param session
	 * @param dossiers
	 * @return fichier Excel sous forme de Datasource (afin d'envoyer le fichier comme pièce jointe dans un mail).
	 */
	public static DataSource creationListDossierExcel(CoreSession session, List<DocumentModel> documents) {

		return createExcelFileFromDocument(session, documents, DOSSIERS_SHEET_NAME, DOSSIER_HEADER);
	}

	public static DataSource createExcelFileFromDocument(CoreSession session, List<DocumentModel> documents,
			String sheetName, String[] header) {
		DataSource fichierExcelResultat = null;
		try {
			HSSFWorkbook wb = initExcelFile(sheetName, header);
			HSSFSheet sheet = wb.getSheet(sheetName);
			int numRow = 1;

			for (DocumentModel doc : documents) {
				addDocumentToSheet(session, sheet, numRow++, doc);
			}

			formatStyle(wb, sheetName, header.length);

			fichierExcelResultat = convertExcelToDataSource(wb);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}

		return fichierExcelResultat;
	}
	
	public static DataSource createExcelFileFromQueryResult(CoreSession session, List<ReponseDossierListingDTO> ReponseDossierDTO,
			String sheetName, String[] header) {
		DataSource fichierExcelResultat = null;
		try {
			HSSFWorkbook wb = initExcelFile(sheetName, header);
			HSSFSheet sheet = wb.getSheet(sheetName);
			int numRow = 1;

			for (ReponseDossierListingDTO dto : ReponseDossierDTO) {
				addQueryResultToSheet(sheet, numRow++, dto);
			}

			formatStyle(wb, sheetName, header.length);

			fichierExcelResultat = convertExcelToDataSource(wb);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}

		return fichierExcelResultat;
	}
	
	protected static void addQueryResultToSheet(final HSSFSheet sheet, final int numRow,
			final ReponseDossierListingDTO result) {
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (result != null) {

			HSSFRow currentRow = sheet.createRow(numRow);
			// On rajoute la ligne avec les données précédentes
			addCellToRow(currentRow, 0, result.getOrigineQuestion(), result.getSourceNumeroQuestion(), result.getTypeQuestion(), 
					result.getDatePublicationJO() != null ? sdf.format(result.getDatePublicationJO()) : null, 
					result.getAuteur(), result.getMinistereAttributaire(), result.getDirectionRunningStep(), result.getMotCles(), result.getDelai(), result.getEtatQuestion(), result.getRoutingTaskType());
		}
	}

	public static DataSource creationExportAllResultExcel(CoreSession session, String query) throws ClientException {
		CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		
		List<ReponseDossierListingDTO> dtos = new ArrayList<ReponseDossierListingDTO>();
		FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		
		List<DocumentModel> documents = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				DossierConstants.QUESTION_DOCUMENT_TYPE, query, null);

		final Map<String, String> libelleRoutingTask = VocabularyConstants.LIST_LIBELLE_ROUTING_TASK_PAR_ID;
		
		for (DocumentModel doc : documents) {
			Dossier dossier = null;
			Question quest = null;
			if (doc.getType().equals(DossierConstants.DOSSIER_DOCUMENT_TYPE)) {
				dossier = doc.getAdapter(Dossier.class);
				quest = dossier.getQuestion(session);
			} else if (doc.getType().equals(DossierConstants.QUESTION_DOCUMENT_TYPE)) {
				quest = doc.getAdapter(Question.class);
				dossier = quest.getDossier(session);
			}

			if (quest != null && dossier != null) {

				ReponseDossierListingDTO dto = new ReponseDossierListingDTOImpl();
				
				List<DocumentModel> dossiersLinks = corbeilleService.findUpdatableDossierLinkForDossier(session, dossier.getDocument());

				int i = 0;
				String[] routingTasks = new String[dossiersLinks.size()];
				List<String> etatQuestion = new ArrayList<String>();
				for (DocumentModel dossierLinkDoc : dossiersLinks) {
					DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
					if (Boolean.TRUE.equals(dossierLink.isUrgent()) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_RAPPELE)) {
						etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_RAPPELE);
					}
					if ((Boolean.TRUE.equals(dossierLink.isSignale()) || Boolean.TRUE.equals(quest.getEtatSignale())) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_SIGNALE)) {
						etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_SIGNALE);
					}
					if ((Boolean.TRUE.equals(dossierLink.isRenouvelle()) || Boolean.TRUE.equals(quest.getEtatRenouvele())) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_RENOUVELE)) {
						etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_RENOUVELE);
					}
					routingTasks[i] = libelleRoutingTask.get(dossierLink.getRoutingTaskType());
					i++;
				}
				if (Boolean.TRUE.equals(quest.getEtatRappele()) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_RAPPELE)) {
					etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_RAPPELE);
				}
				if (Boolean.TRUE.equals(quest.getEtatSignale()) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_SIGNALE)) {
					etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_SIGNALE);
				}
				if (Boolean.TRUE.equals(quest.getEtatRenouvele()) && !etatQuestion.contains(VocabularyConstants.ETAT_DOSSIER_RENOUVELE)) {
					etatQuestion.add(VocabularyConstants.ETAT_DOSSIER_RENOUVELE);
				}
				if (routingTasks.length != 0) {
					dto.setRoutingTaskType(StringUtil.join(routingTasks, ",", ""));
				}
				if (!etatQuestion.isEmpty()) {
					dto.setEtatQuestion(StringUtil.join((String[]) etatQuestion.toArray(new String[0]), ",", ""));
				}
				dto.setOrigineQuestion(quest.getOrigineQuestion());
				dto.setSourceNumeroQuestion(dossier.getNumeroQuestion() != null ? dossier.getNumeroQuestion().toString() : null);
				dto.setTypeQuestion(quest.getTypeQuestion());
				dto.setDatePublicationJO(quest.getDatePublicationJO() != null ? quest.getDatePublicationJO().getTime() : null);
				dto.setAuteur(quest.getNomCompletAuteur());
				dto.setMinistereAttributaire(quest.getIntituleMinistereAttributaire());
				dto.setMotCles(formatMotsClefs(quest, ", "));
				dto.setDelai(getDelaiExpirationFdr(session, quest));
				
				StringBuilder libelleDirections = new StringBuilder();
				List<DocumentModel> runningStepDocList = feuilleRouteService.getRunningSteps(session, dossier.getLastDocumentRoute());
				if (runningStepDocList != null) {
					for (DocumentModel runningStepDoc : runningStepDocList) {
						STRouteStep routeStep = runningStepDoc.getAdapter(STRouteStep.class);
						if (libelleDirections.toString().isEmpty()) {
							libelleDirections.append(routeStep.getDirectionLabel());
						} else {
							libelleDirections.append(", ").append(routeStep.getDirectionLabel());
						}
					}
					dto.setDirectionRunningStep(libelleDirections.toString());
				}
				
				dtos.add(dto);
			}
		}

		return createExcelFileFromQueryResult(session, dtos, DOSSIERS_SHEET_NAME, DOSSIER_HEADER_MAIL);
	}
	
	public static String getDelaiExpirationFdr(CoreSession coreSession, Question question) {
		STParametreService paramService = STServiceLocator.getSTParametreService();
		String reponseDureeTraitement;
		try {
			reponseDureeTraitement = paramService.getParametreValue(coreSession,
					STParametreConstant.QUESTION_DUREE_TRAITEMENT);
		} catch (ClientException e) {
			LOGGER.warn(ReponsesLogEnumImpl.FAIL_GET_PARAM_VAL_DELAI_TRAIT_QUESTION, e);
			return "";
		}

		return DelaiCalculateur.computeDelaiExpirationFdrForCsv(question, Integer.parseInt(reponseDureeTraitement),
				coreSession);
	}

	public static DataSource creationExportResultExcel(CoreSession session, List<Map<String, Serializable>> results) {

		DataSource fichierExcelResultat = null;
		try {

			if (results != null && !results.isEmpty()) {
				HSSFWorkbook wb = initExcelFile(DOSSIERS_SHEET_NAME, QUESTION_HEADER);
				HSSFSheet sheet = wb.getSheet(DOSSIERS_SHEET_NAME);
				int numRow = 1;

				for (Map<String, Serializable> elem : results) {
					addResultToSheet(sheet, numRow++, elem);
				}

				formatStyle(wb, DOSSIERS_SHEET_NAME, QUESTION_HEADER.length);

				fichierExcelResultat = convertExcelToDataSource(wb);
			}
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}

		return fichierExcelResultat;
	}

	public static DataSource creationListDossierExcelFromIds(CoreSession session, List<String> documentsIds) {
		DataSource fichierExcelResultat = null;
		try {
			// création du fichier Excel
			HSSFWorkbook wb = initExcelFile(DOSSIERS_SHEET_NAME, DOSSIER_HEADER);
			HSSFSheet sheet = wb.getSheet(DOSSIERS_SHEET_NAME);
			int numRow = 1;

			for (String docId : documentsIds) {
				IdRef docRef = new IdRef(docId);
				if (session.exists(docRef)) {
					DocumentModel document = session.getDocument(new IdRef(docId));
					addDocumentToSheet(session, sheet, numRow++, document);
				}
			}

			STExcelUtil.formatStyle(wb, DOSSIERS_SHEET_NAME, DOSSIER_HEADER.length);

			fichierExcelResultat = STExcelUtil.convertExcelToDataSource(wb);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}
		return fichierExcelResultat;
	}

	/**
	 * Ajoute un documentModel (dossier ou une question) à une feuille de calcul
	 * 
	 * @param session
	 * @param sheet
	 * @param numRow
	 *            la ligne où ajouter les données
	 * @param document
	 * @throws ClientException
	 */
	protected static void addDocumentToSheet(final CoreSession session, final HSSFSheet sheet, final int numRow,
			final DocumentModel document) throws ClientException {
		if (document != null) {
			Dossier dossier = null;
			Question quest = null;

			if (document.getType().equals(DossierConstants.DOSSIER_DOCUMENT_TYPE)) {
				dossier = document.getAdapter(Dossier.class);
				quest = dossier.getQuestion(session);
			} else if (document.getType().equals(DossierConstants.QUESTION_DOCUMENT_TYPE)) {
				quest = document.getAdapter(Question.class);
				dossier = quest.getDossier(session);
			}
			String datePublicationQ = formatDatePublication(quest);
			String mots = formatMotsClefs(quest, ", ");

			if (dossier != null && quest != null) {

				HSSFRow currentRow = sheet.createRow(numRow);
				addCellToRow(currentRow, 0, quest.getOrigineQuestion(), dossier.getNumeroQuestion().toString(),
						quest.getTypeQuestion(), datePublicationQ, quest.getNomCompletAuteur(),
						quest.getIntituleMinistereAttributaire(), mots, quest.getEtatQuestionSimple());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected static void addResultToSheet(final HSSFSheet sheet, final int numRow,
			final Map<String, Serializable> result) {
		if (result != null) {

			// On récupère toutes les données
			String questionLabel = (String) ((Map) result).get(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION);
			String nature = (String) ((Map) result).get(ReponseDossierListingConstants.TYPE_QUESTION);

			String datePublication = fr.dila.st.core.util.DateUtil.formatDDMMYYYYSlash((Date) ((Map) result)
					.get(ReponseDossierListingConstants.DATE_PUBLICATION_JO));
			String auteur = (String) ((Map) result).get(ReponseDossierListingConstants.AUTEUR);
			String ministereAttrib = (String) ((Map) result).get(ReponseDossierListingConstants.MINISTERE_ATTRIBUTAIRE);
			String ministereInter = (String) ((Map) result).get(ReponseDossierListingConstants.MINISTERE_INTERROGE);
			String motsCles = (String) ((Map) result).get(ReponseDossierListingConstants.MOT_CLES);
			String renseignement = buildRenseignement(result);
			String delai = (String) ((Map) result).get(ReponseDossierListingConstants.DELAI);
			String etat = buildEtat(result);
			String etapeEnCours = (String) ((Map) result).get(ReponseDossierListingConstants.ROUTING_TASK_TYPE);
			String legislature = (String) ((Map) result).get(ReponseDossierListingConstants.LEGISLATURE)
					+ "eme legislature";

			HSSFRow currentRow = sheet.createRow(numRow);
			// On rajoute la ligne avec les données précédentes
			addCellToRow(currentRow, 0, questionLabel, nature, datePublication, auteur, ministereAttrib,
					ministereInter, motsCles, renseignement, delai, etat, etapeEnCours, legislature);

		}
	}

	@SuppressWarnings("rawtypes")
	private static String buildEtat(Map result) {
		Boolean isRappele = (Boolean) result.get(ReponseDossierListingConstants.IS_LOCKED);
		Boolean isRenouvele = (Boolean) result.get(ReponseDossierListingConstants.HAS_LOT);
		Boolean isSignale = (Boolean) result.get(ReponseDossierListingConstants.HAS_CONNEXITE);

		StringBuilder etat = new StringBuilder();

		if (isRappele != null && isRappele) {
			etat.append("Dossier rappelé, ");
		}
		if (isSignale != null && isSignale) {
			etat.append("signalé, ");
		}
		if (isRenouvele != null && isRenouvele) {
			etat.append("renouvelé, ");
		}

		return etat.toString();
	}

	@SuppressWarnings("rawtypes")
	private static String buildRenseignement(Map result) {
		Boolean isLocked = (Boolean) result.get(ReponseDossierListingConstants.IS_LOCKED);
		Boolean hasLot = (Boolean) result.get(ReponseDossierListingConstants.HAS_LOT);
		Boolean hasConnexite = (Boolean) result.get(ReponseDossierListingConstants.HAS_CONNEXITE);
		Boolean hasAttachment = (Boolean) result.get(ReponseDossierListingConstants.HAS_ATTACHEMENT);

		StringBuilder renseignement = new StringBuilder();

		if (isLocked != null && isLocked) {
			renseignement.append("verrouillé, ");
		}
		if (hasLot != null && hasLot) {
			renseignement.append("est alloti, ");
		}
		if (hasConnexite != null && hasConnexite) {
			renseignement.append("a des dossiers connexes, ");
		}
		if (hasAttachment != null && hasAttachment) {
			renseignement.append("a des pièces jointes, ");
		}

		return renseignement.toString();
	}

	public static String formatMotsClefs(Question quest, String separateur) {
		if (quest != null) {
			List<String> motsClefs = quest.getMotsClef();
			String mots = "";
			if (!motsClefs.isEmpty()) {
				mots = StringUtil.join(motsClefs, separateur);
			}

			return mots;
		} else {
			return null;
		}
	}
	
	public String getLibelleEtatQuestion(DossierLink dossierLink) {
		
		return null;
		
	}

	public static String formatDatePublication(Question quest) {
		if (quest != null) {
			Calendar publi = quest.getDatePublicationJO();
			String datePublicationQ = "Non Communiquée";
			int jour = publi.get(Calendar.DAY_OF_MONTH);
			String zeroJ = jour < 10 ? "0" : "";
			int mois = publi.get(Calendar.MONTH) + 1;
			String zeroM = mois < 10 ? "0" : "";
			int annee = publi.get(Calendar.YEAR);
			datePublicationQ = zeroJ + jour + "/" + zeroM + mois + "/" + annee;

			return datePublicationQ;
		} else {
			return null;
		}

	}

	/**
	 * Crée le fichier de base pour les exports de gouvernement
	 * 
	 */
	public static DataSource createExportGvt(CoreSession session) {
		DataSource fichierExcelResultat = null;
		HSSFWorkbook wb = null;
		try {
			// création du fichier Excel
			wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			wb.setSheetName(0, "gvt");
			// création des colonnes header
			HSSFRow currentRow = sheet.createRow(0);
			int numCol = 0;

			numCol = createExportGvtHeaders(currentRow, numCol);

			int nbCol = numCol;

			// Font et style de la ligne de titre
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setFont(font);

			// Ajout de la ligne correspondant au gouvernement actuel
			OrganigrammeNode currentGvt = STServiceLocator.getSTGouvernementService().getCurrentGouvernement();
			HSSFRow gvtRow = sheet.createRow(1);
			gvtRow.createCell(SSInjectionGouvernementConstants.INJ_COL_LIB_LONG).setCellValue(currentGvt.getLabel());
			HSSFCell gvtCurrentDateCell = gvtRow.createCell(SSInjectionGouvernementConstants.INJ_COL_DATE_DEB);
			gvtCurrentDateCell.setCellValue(currentGvt.getDateDebut());
			HSSFCellStyle dateCellStyle = wb.createCellStyle();
			HSSFDataFormat hssfDataFormat = wb.createDataFormat();
			dateCellStyle.setDataFormat(hssfDataFormat.getFormat("dd/mm/yyyy"));
			gvtCurrentDateCell.setCellStyle(dateCellStyle);

			// Ajout des lignes du gouvernement
			HSSFRow currentMinRow;
			
			int rowNum = 1;
			List<EntiteNode> currentMinisteres = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();
			Collections.sort(currentMinisteres, new ProtocolarOrderComparator());
			for (EntiteNode minNode : currentMinisteres) {
				rowNum++;
				currentMinRow = sheet.createRow(rowNum);

				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_OPR).setCellValue(minNode.getOrdre());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_A_CREER_REP).setCellValue("0");
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_A_CREER_SOLON).setCellValue("0");
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_A_MODIFIER_SOLON).setCellValue("0");
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_NOUV_ENTITE_EPP).setCellValue("0");
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_LIB_COURT)
						.setCellValue(minNode.getEdition());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_LIB_LONG)
						.setCellValue(minNode.getLabel());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_ENTETE)
						.setCellValue(minNode.getFormule());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_CIVILITE)
						.setCellValue(minNode.getMembreGouvernementCivilite());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_PRENOM)
						.setCellValue(minNode.getMembreGouvernementPrenom());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_NOM)
						.setCellValue(minNode.getMembreGouvernementNom());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_PRENOM_NOM)
						.setCellValue(minNode.getMembreGouvernement());
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_DATE_DEB)
						.setCellValue(minNode.getDateDebut());

				// format date
				currentMinRow.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_DEB).setCellStyle(dateCellStyle);
				if (minNode.getDateFin() != null) {
					currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_DATE_FIN)
							.setCellValue(minNode.getDateFin());
					currentMinRow.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_FIN)
							.setCellStyle(dateCellStyle);
				}

				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_A_MODIFIER_REP).setCellValue("0");
				currentMinRow.createCell(SSInjectionGouvernementConstants.INJ_COL_IDENTIFIANT_REP)
						.setCellValue(minNode.getId());
			}

			// size des colonnes
			for (int i = 0; i < nbCol; i++) {
				sheet.getRow(0).getCell(i).setCellStyle(cellStyle);
				sheet.autoSizeColumn(i);
			}

			// récupération du fichier dans un outPutStream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wb.write(baos);
			baos.close();
			// récupération du fichier Excel en tant que DataSource
			byte[] bytes = baos.toByteArray();
			fichierExcelResultat = new ByteArrayDataSource(bytes, "application/vnd.ms-excel");
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, e);
				}
			}
		}
		return fichierExcelResultat;
	}

	private static int createExportGvtHeaders(HSSFRow currentRow, int numCol) {
		currentRow.createCell(numCol++).setCellValue("NOR");
		currentRow.createCell(numCol++).setCellValue("OP S");
		currentRow.createCell(numCol++).setCellValue("SOLON EPG à créer");
		currentRow.createCell(numCol++).setCellValue("OP R");
		currentRow.createCell(numCol++).setCellValue("REPONSES à créer");
		currentRow.createCell(numCol++).setCellValue("SOLON à modifier");
		currentRow.createCell(numCol++).setCellValue("Libellé Court");
		currentRow.createCell(numCol++).setCellValue("Libellé Long");
		currentRow.createCell(numCol++).setCellValue("Formule Entêtes");
		currentRow.createCell(numCol++).setCellValue("Civilité");
		currentRow.createCell(numCol++).setCellValue("Prénom");
		currentRow.createCell(numCol++).setCellValue("Nom");
		currentRow.createCell(numCol++).setCellValue("Prénom et Nom");
		currentRow.createCell(numCol++).setCellValue("Date de début");
		currentRow.createCell(numCol++).setCellValue("Date de fin");
		currentRow.createCell(numCol++).setCellValue("NOR EPP (ministère de rattachement)");
		currentRow.createCell(numCol++).setCellValue("Nouvelle identité EPP");
		currentRow.createCell(numCol++).setCellValue("REPONSES à modifier");
		currentRow.createCell(numCol++).setCellValue("Identifiant REPONSES");
		return numCol;
	}
	
	public static List<InjectionGvtDTO> prepareImportGvt(final CoreSession session, final File file) {
		List<InjectionGvtDTO> listImportGvt = new ArrayList<InjectionGvtDTO>();
		HSSFWorkbook wb = null;
		try {
			// lecture du fichier Excel
			wb = new HSSFWorkbook(new FileInputStream(file));
			// récupération de la première feuille
			HSSFSheet sheet = wb.getSheetAt(0);
			// récupération de l'itérateur
			Iterator<Row> rowIterator = sheet.iterator();
			Row currentRow;
			int nbRow = 0;
			Date dateDeb;
			Date dateFin;
			
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
			while (rowIterator.hasNext()) {
				currentRow = rowIterator.next();
				if (nbRow == 0) { // entêtes
					if (currentRow.getCell(currentRow.getFirstCellNum()).getStringCellValue().compareTo("NOR") != 0) {
						LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_EXCEL_TEC,
								"Le fichier n'est pas formaté correctement");
						return null;
					}
				} else if (nbRow == 1) { // gouvernement
					dateDeb = getDateValueFromCell(currentRow
							.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_DEB));
					dateFin = getDateValueFromCell(currentRow
							.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_FIN));
					listImportGvt.add(new InjectionGvtDTOImpl(null, false, null,
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_LIB_LONG)),
							null, null, null, null, null, dateDeb, dateFin, true, false, null));
				} else { // lignes d'entités
					dateDeb = getDateValueFromCell(currentRow
							.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_DEB));
					dateFin = getDateValueFromCell(currentRow
							.getCell(SSInjectionGouvernementConstants.INJ_COL_DATE_FIN));
					InjectionGvtDTOImpl injGvtDto = new InjectionGvtDTOImpl(
							getStringValueFromCell(currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_OPR)),
							"1".equals(getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_A_CREER_REP))),
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_LIB_COURT)),
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_LIB_LONG)),
							getStringValueFromCell(currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_ENTETE)),
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_CIVILITE)),
							getStringValueFromCell(currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_PRENOM)),
							getStringValueFromCell(currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_NOM)),
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_PRENOM_NOM)),
							dateDeb, dateFin, false,
							"1".equals(getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_A_MODIFIER_REP))),
							getStringValueFromCell(
									currentRow.getCell(SSInjectionGouvernementConstants.INJ_COL_IDENTIFIANT_REP)));
					if (injGvtDto.isaModifierReponses()) {
						injGvtDto.setTypeModification("Modification ordre protocolaire");
					}
					listImportGvt.add(injGvtDto);
				}
				nbRow++;
			}
		} catch (Exception exc) {
			return null;
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, e);
				}
			}
		}
		return listImportGvt;
	}

	private static String getStringValueFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		String value = null;
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING: {
				value = cell.getStringCellValue();
				break;
			}
			case HSSFCell.CELL_TYPE_NUMERIC: {
				value = String.valueOf((int) cell.getNumericCellValue());
				break;
			}
			default: {
				break;
			}
		}
		return value;
	}

	private static Date getDateValueFromCell(Cell cell) {
		return cell == null ? null : DateUtil.getJavaDate(cell.getNumericCellValue());
	}

}
