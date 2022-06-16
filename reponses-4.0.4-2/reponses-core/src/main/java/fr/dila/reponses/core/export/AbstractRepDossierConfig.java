package fr.dila.reponses.core.export;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.export.enums.RepExcelSheetName;
import fr.dila.reponses.core.util.QuestionUtils;
import fr.dila.st.core.export.AbstractEnumExportConfig;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public abstract class AbstractRepDossierConfig<T> extends AbstractEnumExportConfig<T> {
    protected static final String[] EMPTY_DOSSIER_LINE = new String[RepExcelSheetName.DOSSIER.getHeadersSize()];

    private static final Map<String, String> ETATS_QUESTION = STServiceLocator
        .getVocabularyService()
        .getAllEntries(VocabularyConstants.ETAT_QUESTION);

    protected AbstractRepDossierConfig(List<T> items) {
        super(items, RepExcelSheetName.DOSSIER);
    }

    protected String[] getDataCellsForDossier(CoreSession session, DocumentModel item) {
        Dossier dossier = null;
        Question quest = null;

        if (DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(item.getType())) {
            dossier = item.getAdapter(Dossier.class);
            quest = dossier.getQuestion(session);
        } else if (DossierConstants.QUESTION_DOCUMENT_TYPE.equals(item.getType())) {
            quest = item.getAdapter(Question.class);
            dossier = quest.getDossier(session);
        }

        if (dossier != null && quest != null) {
            return new String[] {
                quest.getOrigineQuestion(),
                dossier.getNumeroQuestion().toString(),
                quest.getTypeQuestion(),
                SolonDateConverter.DATE_SLASH.format(quest.getDatePublicationJO()),
                quest.getNomCompletAuteur(),
                quest.getIntituleMinistereAttributaire(),
                QuestionUtils.joinMotsClefs(quest),
                ETATS_QUESTION.getOrDefault(quest.getEtatQuestionSimple(), quest.getEtatQuestionSimple())
            };
        } else {
            return EMPTY_DOSSIER_LINE;
        }
    }
}
