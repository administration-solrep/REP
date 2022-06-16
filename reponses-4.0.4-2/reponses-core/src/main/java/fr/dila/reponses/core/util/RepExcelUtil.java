package fr.dila.reponses.core.util;

import fr.dila.reponses.api.exception.ImportOrganigrammeException;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.ss.api.constant.SSInjGvtColumnsEnum;
import fr.dila.ss.core.client.InjectionGvtDTOImpl;
import fr.dila.ss.core.util.SSExcelUtil;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.STExcelUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Classe utilitaire pour créér des Documents Excel.
 *
 */
public final class RepExcelUtil {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(RepExcelUtil.class);

    private static final String KEY_MESSAGE_FORMAT_ERROR = "organigramme.error.import.format";

    private static final String KEY_MESSAGE_SIZE_ERROR = "organigramme.error.import.size";

    private static final String KEY_MESSAGE_FILE_FORMAT_ERROR = "organigramme.error.import.file.format";

    private static final int MAX_SIZE_LIBELLE = 255;

    /**
     * Clé de messages.properties associée à la détection d'un trop grand nombre de lignes dans le fichier d'import de gvt.
     */
    private static final String MSG_KEY_TOO_MANY_LINES = "gouvernement.import.warning.toomanylines";

    /**
     * Nombre max de lignes lues dans le fichier d'import de gouvernement.
     */
    private static final int IMPORT_GVT_MAX_NB_LINES = 200;

    private static final String[] GOUVERNEMENT_HEADER = {
        "gouvernement.export.header.nor",
        "gouvernement.export.header.op.s",
        "gouvernement.export.header.solon.epg.creer",
        "gouvernement.export.header.op.r",
        "gouvernement.export.header.reponses.creer",
        "gouvernement.export.header.solon.modifier",
        "gouvernement.export.header.libelle.court",
        "gouvernement.export.header.libelle.long",
        "gouvernement.export.header.formule.entetes",
        "gouvernement.export.header.civilite",
        "gouvernement.export.header.prenom",
        "gouvernement.export.header.nom",
        "gouvernement.export.header.prenom.nom",
        "gouvernement.export.header.date.debut",
        "gouvernement.export.header.date.fin",
        "gouvernement.export.header.nor.epp",
        "gouvernement.export.header.nouvelle.identite.epp",
        "gouvernement.export.header.reponses.modifier",
        "gouvernement.export.header.identifiant.reponses"
    };

    private static final String UNICODE_REGEX = "(\\P{M}\\p{M}*)+";
    private static final String NUMBER_REGEX = "[0-9]*";

    private RepExcelUtil() {
        // Default constructor
    }

    /**
     * Crée le fichier de base pour les exports de gouvernement
     *
     */
    public static DataSource createExportGvt(CoreSession session) {
        DataSource fichierExcelResultat = null;
        try (HSSFWorkbook wb = new HSSFWorkbook()) {
            // création du fichier Excel

            HSSFSheet sheet = wb.createSheet();
            wb.setSheetName(0, "gvt");
            // création des colonnes header
            HSSFRow currentRow = sheet.createRow(0);
            int nbCol = createExportGvtHeaders(currentRow, 0);

            // Font et style de la ligne de titre
            HSSFFont font = wb.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);

            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);

            // Ajout de la ligne correspondant au gouvernement actuel
            OrganigrammeNode currentGvt = STServiceLocator.getSTGouvernementService().getCurrentGouvernement();
            HSSFRow gvtRow = sheet.createRow(1);
            SSExcelUtil.createCell(gvtRow, SSInjGvtColumnsEnum.INJ_COL_LIB_LONG).setCellValue(currentGvt.getLabel());
            HSSFCell gvtCurrentDateCell = SSExcelUtil.createCell(gvtRow, SSInjGvtColumnsEnum.INJ_COL_DATE_DEB);
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

                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_OPR).setCellValue(minNode.getOrdre());
                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_A_CREER_REP).setCellValue("0");
                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_A_CREER_SOLON).setCellValue("0");
                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_A_MODIFIER_SOLON).setCellValue("0");
                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_NOUV_ENTITE_EPP).setCellValue("0");
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_LIB_COURT)
                    .setCellValue(minNode.getEdition());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_LIB_LONG)
                    .setCellValue(minNode.getLabel());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_ENTETE)
                    .setCellValue(minNode.getFormule());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_CIVILITE)
                    .setCellValue(minNode.getMembreGouvernementCivilite());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_PRENOM)
                    .setCellValue(minNode.getMembreGouvernementPrenom());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_NOM)
                    .setCellValue(minNode.getMembreGouvernementNom());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_PRENOM_NOM)
                    .setCellValue(minNode.getMembreGouvernement());
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_DATE_DEB)
                    .setCellValue(minNode.getDateDebut());

                // format date
                SSExcelUtil.getCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_DATE_DEB).setCellStyle(dateCellStyle);
                if (minNode.getDateFin() != null) {
                    SSExcelUtil
                        .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_DATE_FIN)
                        .setCellValue(minNode.getDateFin());
                    SSExcelUtil
                        .getCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_DATE_FIN)
                        .setCellStyle(dateCellStyle);
                }

                SSExcelUtil.createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_A_MODIFIER_REP).setCellValue("0");
                SSExcelUtil
                    .createCell(currentMinRow, SSInjGvtColumnsEnum.INJ_COL_IDENTIFIANT_REP)
                    .setCellValue(minNode.getId());
            }

            // size des colonnes
            for (int i = 0; i < nbCol; i++) {
                sheet.getRow(0).getCell(i).setCellStyle(cellStyle);
                sheet.autoSizeColumn(i);
            }

            fichierExcelResultat = STExcelUtil.convertExcelToDataSource(wb);
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
        }
        return fichierExcelResultat;
    }

    private static int createExportGvtHeaders(HSSFRow currentRow, int numCol) {
        String[] gvtHeaders = new String[GOUVERNEMENT_HEADER.length];
        for (int i = 0; i < GOUVERNEMENT_HEADER.length; i++) {
            gvtHeaders[i] = ResourceHelper.getString(GOUVERNEMENT_HEADER[i]);
        }
        STExcelUtil.addCellToRow(currentRow, numCol, gvtHeaders);
        return GOUVERNEMENT_HEADER.length;
    }

    public static List<InjectionGvtDTO> prepareImportGvt(final CoreSession session, final File file) {
        List<InjectionGvtDTO> listImportGvt = new ArrayList<>();

        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file))) {
            // lecture du fichier Excel
            // récupération de la première feuille
            HSSFSheet sheet = wb.getSheetAt(0);
            AtomicBoolean valid = new AtomicBoolean(true);
            List<String> errorMessages = new ArrayList<>();

            // Vérification du nombre de lignes
            if (sheet.getPhysicalNumberOfRows() <= IMPORT_GVT_MAX_NB_LINES) {
                // récupération de l'itérateur
                Iterator<Row> rowIterator = sheet.iterator();
                Row currentRow;
                int nbRow = 0;
                Date dateDeb;
                Date dateFin;

                while (rowIterator.hasNext()) {
                    currentRow = rowIterator.next();
                    if (nbRow == 0) { // entêtes
                        if (formatImportGvtIsValid(currentRow)) {
                            LOGGER.error(
                                session,
                                STLogEnumImpl.FAIL_PROCESS_EXCEL_TEC,
                                "Le fichier n'est pas formaté correctement"
                            );
                            errorMessages.add(ResourceHelper.getString(KEY_MESSAGE_FILE_FORMAT_ERROR));
                            throw new ImportOrganigrammeException(errorMessages);
                        }
                    } else if (nbRow == 1) { // gouvernement
                        dateDeb =
                            getDateValueFromCell(SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_DATE_DEB));
                        dateFin =
                            getDateValueFromCell(SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_DATE_FIN));
                        listImportGvt.add(
                            new InjectionGvtDTOImpl(
                                null,
                                false,
                                null,
                                getStringValueFromCell(
                                    SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_LIB_LONG),
                                    UNICODE_REGEX,
                                    valid,
                                    null,
                                    errorMessages
                                ),
                                null,
                                null,
                                null,
                                null,
                                null,
                                dateDeb,
                                dateFin,
                                true,
                                false,
                                null
                            )
                        );
                    } else {
                        // lignes d'entités
                        listImportGvt.add(createInjectionGvtDTO(currentRow, valid, errorMessages));
                    }
                    nbRow++;
                }
            } else {
                // Too many lines !
                LOGGER.warn(
                    session,
                    STLogEnumImpl.WARNING_TEC,
                    ResourceHelper.getString(MSG_KEY_TOO_MANY_LINES, IMPORT_GVT_MAX_NB_LINES)
                );
                valid.set(false);
            }
            if (!valid.get()) {
                LOGGER.error(STLogEnumImpl.FAIL_PROCESS_EXCEL_TEC, "Le fichier d'import est invalide");
                throw new ImportOrganigrammeException(errorMessages);
            }
        } catch (ImportOrganigrammeException exc) {
            throw exc;
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, exc);
            listImportGvt = null;
        }
        return listImportGvt;
    }

    private static boolean formatImportGvtIsValid(Row row) {
        return (
            row.getCell(row.getFirstCellNum()).getStringCellValue().compareTo("NOR") != 0 ||
            row.getPhysicalNumberOfCells() < GOUVERNEMENT_HEADER.length
        );
    }

    private static InjectionGvtDTOImpl createInjectionGvtDTO(
        Row currentRow,
        AtomicBoolean valid,
        List<String> errorMessages
    ) {
        Date dateDeb = getDateValueFromCell(SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_DATE_DEB));
        Date dateFin = getDateValueFromCell(SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_DATE_FIN));

        InjectionGvtDTOImpl injGvtDto = new InjectionGvtDTOImpl(
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_OPR),
                NUMBER_REGEX,
                valid,
                null,
                errorMessages
            ),
            "1".equals(
                    getStringValueFromCell(
                        SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_A_CREER_REP),
                        "[0|1]",
                        valid,
                        null,
                        errorMessages
                    )
                ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_LIB_COURT),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_LIB_LONG),
                UNICODE_REGEX,
                valid,
                MAX_SIZE_LIBELLE,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_ENTETE),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_CIVILITE),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_PRENOM),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_NOM),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_PRENOM_NOM),
                UNICODE_REGEX,
                valid,
                null,
                errorMessages
            ),
            dateDeb,
            dateFin,
            false,
            "1".equals(
                    getStringValueFromCell(
                        SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_A_MODIFIER_REP),
                        "[0|1]",
                        valid,
                        null,
                        errorMessages
                    )
                ),
            getStringValueFromCell(
                SSExcelUtil.getCell(currentRow, SSInjGvtColumnsEnum.INJ_COL_IDENTIFIANT_REP),
                NUMBER_REGEX,
                valid,
                null,
                errorMessages
            )
        );
        if (injGvtDto.isaModifierReponses()) {
            injGvtDto.setTypeModification("Modification ordre protocolaire");
        }
        return injGvtDto;
    }

    private static String getStringValueFromCell(
        Cell cell,
        String regex,
        AtomicBoolean valid,
        Integer sizeMax,
        List<String> errorMessages
    ) {
        if (cell == null) {
            return null;
        }
        String value = null;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                if (regex == null) {
                    regex = "[a-zA-Z]*";
                }
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (regex == null) {
                    regex = NUMBER_REGEX;
                }
                value = String.valueOf((int) cell.getNumericCellValue());
                break;
            default:
                break;
        }

        if (value == null || !value.matches(regex)) {
            final String errorMsg = String.format(
                "La valeur [%s] de la case (%d,%d) ne respecte pas le regex [%s]",
                cell,
                cell.getRowIndex() + 1,
                cell.getColumnIndex() + 1,
                regex
            );
            LOGGER.error(STLogEnumImpl.FAIL_PROCESS_EXCEL_TEC, errorMsg);
            valid.compareAndSet(true, false);
            errorMessages.add(ResourceHelper.getString(KEY_MESSAGE_FORMAT_ERROR, value));
            value = null;
        }

        if (StringUtils.isNotEmpty(value) && sizeMax != null && value.length() > sizeMax) {
            final String errorMsg = String.format(
                "La valeur [%s] de la case (%d,%d) dépasse la longueur max du champ [%s]",
                cell,
                cell.getRowIndex() + 1,
                cell.getColumnIndex() + 1,
                sizeMax
            );
            LOGGER.error(STLogEnumImpl.FAIL_PROCESS_EXCEL_TEC, errorMsg);
            valid.compareAndSet(true, false);
            errorMessages.add(ResourceHelper.getString(KEY_MESSAGE_SIZE_ERROR, value));
            value = null;
        }
        return value;
    }

    private static Date getDateValueFromCell(Cell cell) {
        return cell == null ? null : DateUtil.getJavaDate(cell.getNumericCellValue());
    }
}
