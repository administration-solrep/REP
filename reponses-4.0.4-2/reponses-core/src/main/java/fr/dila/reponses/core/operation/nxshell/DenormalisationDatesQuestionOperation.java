package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Une opération pour dénormaliser les dates liées aux états de la question
 *
 * @author bgamard
 */
@Operation(
    id = DenormalisationDatesQuestionOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "DenormaliserDatesQuestion",
    description = "Dénormalise les dates liées aux états de la question"
)
public class DenormalisationDatesQuestionOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Denormaliser.Dates.Question";

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(DenormalisationDatesQuestionOperation.class);

    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() {
        final List<Map<String, String>> idQuestionLot = new ArrayList<>();
        final Long legislature = ReponsesServiceLocator.getRechercheService().getLegislatureCourante(session);

        log.info("Début opération " + ID);

        log.info("Récupération des questions rappelées");
        final List<DocumentModel> questionsDoc = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            "Question",
            "SELECT q.ecm:uuid AS id FROM Question AS q WHERE q.qu:etatRappele = ? AND q.qu:legislatureQuestion = ? AND q.qu:dateRappelQuestion is null",
            new Object[] { 1, legislature }
        );

        log.info("Fin - Récupération dess questions");

        int i = 0;
        log.info("Dénormalisation des dates : " + i + "/" + questionsDoc.size());

        for (final DocumentModel questionDoc : questionsDoc) {
            final Question question = questionDoc.getAdapter(Question.class);

            final Dossier dossier = question.getDossier(session);
            final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
            final Allotissement lot = allotissementService.getAllotissement(dossier.getDossierLot(), session);

            if (lot != null && lot.getIdDossiers().size() == 2) {
                final List<String> listQuestionLot = new ArrayList<>();
                final List<String> listDossierLot = lot.getIdDossiers();

                // Récupere les id de questions
                for (final String id : listDossierLot) {
                    final DocumentModel dossierDoc = session.getDocument(new IdRef(id));
                    final Dossier dossierLot = dossierDoc.getAdapter(Dossier.class);
                    listQuestionLot.add(dossierLot.getQuestionId());
                }

                // suppression de l'id de la question rappelé de la liste
                listQuestionLot.remove(questionDoc.getTitle());

                // récuperation de l'id de l'autre question
                final String idQuestion = listQuestionLot.get(0);

                // Maj de la date de rappel avec la date de publication la question de rappel
                final DocumentModel questionAllotDoc = session.getDocument(new IdRef(idQuestion));
                final Question questionAllot = questionAllotDoc.getAdapter(Question.class);
                final Calendar cal = questionAllot.getDatePublicationJO();
                question.setDateRappelQuestion(cal);
                session.saveDocument(questionDoc);
                session.save();
            } else {
                final Map<String, String> idQuestion = new HashMap<>();

                idQuestion.put("Type", question.getTypeQuestion());
                idQuestion.put("Origine", question.getOrigineQuestion());
                idQuestion.put("Numero", question.getNumeroQuestion().toString());
                idQuestion.put("Legislature", question.getLegislatureQuestion().toString());
                idQuestionLot.add(idQuestion);

                final StringBuilder sb = new StringBuilder();
                sb.append("Question rappelée dans un lot ne contenant pas 2 questions : ");
                sb.append(question.getTypeQuestion());
                sb.append(" - ");
                sb.append(question.getOrigineQuestion());
                sb.append(" - ");
                sb.append(question.getNumeroQuestion());
                sb.append(" - ");
                sb.append(question.getLegislatureQuestion());

                log.info(sb.toString());
            }

            i++;
            if (i % 1000 == 0) {
                log.info("Dénormalisation des dates : " + i + "/" + questionsDoc.size());
            }
        }

        log.info("Dénormalisation des dates : " + questionsDoc.size() + "/" + questionsDoc.size());

        if (idQuestionLot.size() != 0) {
            log.info("Envoi du mail");
            sendMail(session, idQuestionLot);
        }

        log.info("Fin de l'opération" + ID);
        return;
    }

    public Boolean sendMail(final CoreSession session, final List<Map<String, String>> idQuestionLot) {
        //récupération des résultats dossiers
        final String nomFichier = "Resultat_denormalisation_date.xls";
        final DataSource fichierExcelResultat = creationListExcel(session, idQuestionLot);

        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String mail = paramService.getParametreValue(session, STParametreConstant.MAIL_ADMIN_TECHNIQUE);
        final List<String> recipients = new ArrayList<>();
        recipients.add(mail);

        final String content =
            "Dénormalisation date questions rappelées : liste des id de questions rappelées dans des lots de plus de deux questions.";
        final String objet = "Dénormalisation date questions rappelées";

        Boolean isSent = true;
        final STMailService mailService = STServiceLocator.getSTMailService();
        try {
            mailService.sendMailWithAttachement(recipients, objet, content, nomFichier, fichierExcelResultat);
        } catch (final Exception e) {
            log.error("erreur lors de l'envoi de mail lors du batch d'alerte", e);
            isSent = false;
        }
        return isSent;
    }

    public static DataSource creationListExcel(final CoreSession session, final List<Map<String, String>> idQuestion) {
        DataSource fichierExcelResultat = null;
        try (HSSFWorkbook wb = new HSSFWorkbook()) {
            // création du fichier Excel

            final HSSFSheet sheet = wb.createSheet();
            wb.setSheetName(0, "Resultats_requête");
            // création des colonnes header
            int numRow = 0;
            HSSFRow currentRow = sheet.createRow(numRow);
            int numCol = 0;
            currentRow.createCell(numCol++).setCellValue("Type");
            currentRow.createCell(numCol++).setCellValue("Origine");
            currentRow.createCell(numCol++).setCellValue("N° Question");
            currentRow.createCell(numCol++).setCellValue("Législature");

            final int nbCol = numCol;

            for (final Map<String, String> idMap : idQuestion) {
                numCol = 0;
                numRow++;
                currentRow = sheet.createRow(numRow);
                currentRow.createCell(numCol++).setCellValue(idMap.get("Type"));
                currentRow.createCell(numCol++).setCellValue(idMap.get("Origine"));
                currentRow.createCell(numCol++).setCellValue(idMap.get("Numero"));
                currentRow.createCell(numCol++).setCellValue(idMap.get("Legislature"));
            }

            // Font et style de la ligne de titre
            final HSSFFont font = wb.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);

            final HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);

            for (int i = 0; i < nbCol; i++) {
                sheet.getRow(0).getCell(i).setCellStyle(cellStyle);
                sheet.autoSizeColumn(i);
            }

            // récupération du fichier dans un outPutStream
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            baos.close();
            // récupération du fichier Excel en tant que DataSource
            final byte[] bytes = baos.toByteArray();
            fichierExcelResultat = new ByteArrayDataSource(bytes, "application/vnd.ms-excel");
        } catch (final Exception e) {
            log.error("Erreur lors de la création du fichier Excel de résultat : ", e);
        }
        return fichierExcelResultat;
    }
}
