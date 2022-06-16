package fr.dila.reponses.core.service;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getDossierService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.ExcelService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.util.QuestionUtils;
import fr.dila.ss.core.service.SSExcelServiceImpl;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ExcelServiceImpl extends SSExcelServiceImpl implements ExcelService {
    private static final String[] DOSSIER_HEADER = {
        ResourceHelper.getString("label.excel.dossier.origine"),
        ResourceHelper.getString("label.excel.dossier.numquestion"),
        ResourceHelper.getString("label.excel.dossier.nature"),
        ResourceHelper.getString("label.excel.dossier.dateJo"),
        ResourceHelper.getString("label.excel.dossier.auteur"),
        ResourceHelper.getString("label.excel.dossier.minattributaire"),
        ResourceHelper.getString("label.excel.dossier.motscles"),
        ResourceHelper.getString("label.excel.dossier.etat"),
        ResourceHelper.getString("label.excel.dossier.mininterroge"),
        ResourceHelper.getString("label.excel.dossier.delai"),
        ResourceHelper.getString("label.excel.dossier.legislature"),
        ResourceHelper.getString("label.excel.dossier.datesignalement"),
        ResourceHelper.getString("label.excel.dossier.daterenouvellement"),
        ResourceHelper.getString("label.excel.dossier.daterapple"),
        ResourceHelper.getString("label.excel.dossier.qerappel"),
        ResourceHelper.getString("label.excel.dossier.directionetape")
    };

    @Override
    protected String[] getListDossiersHeader() {
        return DOSSIER_HEADER;
    }

    @Override
    protected void addDocumentToSheet(
        final CoreSession session,
        final HSSFSheet sheet,
        final int numRow,
        final DocumentModel document
    ) {
        if (document == null) {
            return;
        }

        Dossier dossier = null;
        Question quest = null;

        if (document.getType().equals(DossierConstants.DOSSIER_DOCUMENT_TYPE)) {
            dossier = document.getAdapter(Dossier.class);
            quest = dossier.getQuestion(session);
        } else if (document.getType().equals(DossierConstants.QUESTION_DOCUMENT_TYPE)) {
            quest = document.getAdapter(Question.class);
            dossier = quest.getDossier(session);
        }
        String mots = QuestionUtils.joinMotsClefs(quest);

        ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();

        if (dossier != null && quest != null) {
            HSSFRow currentRow = sheet.createRow(numRow);
            addCellToRow(
                currentRow,
                0,
                quest.getOrigineQuestion(),
                dossier.getNumeroQuestion().toString(),
                quest.getTypeQuestion(),
                SolonDateConverter.DATE_SLASH.format(quest.getDatePublicationJO()),
                quest.getNomCompletAuteur(),
                quest.getIntituleMinistereAttributaire(),
                mots,
                quest.getEtatQuestionSimple(),
                quest.getIntituleMinistere(),
                QuestionUtils.getDelaiExpiration(session, quest),
                quest.getLegislatureQuestion() != null ? String.valueOf(quest.getLegislatureQuestion()) : null,
                SolonDateConverter.DATE_SLASH.format(quest.getDateSignalementQuestion()),
                SolonDateConverter.DATE_SLASH.format(quest.getDateRenouvellementQuestion()),
                SolonDateConverter.DATE_SLASH.format(quest.getDateRappelQuestion()),
                getQEsRappel(session, dossier),
                feuilleRouteService.getDirectionsRunningSteps(session, dossier)
            );
        }
    }

    private String getQEsRappel(CoreSession session, Dossier dossier) {
        return String.join(", ", getDossierService().getSourceNumeroQERappels(session, dossier));
    }
}
