package fr.dila.reponses.ui.contentview;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.ui.bean.EliminationDonneesDossierDTO;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ListeEliminationDossierPageProvider extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) throws NuxeoException {
        currentItems = new ArrayList<>();

        resultsCount = QueryUtils.doCountQuery(coreSession, query);

        if (resultsCount > 0) {
            List<String> ids = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
            populateFromDossierIds(coreSession, ids);
        }
    }

    protected void populateFromDossierIds(CoreSession coreSession, List<String> ids) {
        if (!ids.isEmpty()) {
            List<DocumentModel> dml = QueryUtils.retrieveDocuments(
                coreSession,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                ids,
                true
            );
            for (DocumentModel dm : dml) {
                if (dm != null) {
                    Question question = dm.getAdapter(Dossier.class).getQuestion(coreSession);

                    EliminationDonneesDossierDTO dto = new EliminationDonneesDossierDTO();
                    dto.setId(dm.getId());
                    dto.setOrigine(question.getOrigineQuestion());
                    dto.setQuestion(question.getNumeroQuestion().toString());
                    dto.setNature(question.getTypeQuestion());
                    dto.setDatePublication(SolonDateConverter.DATE_SLASH.format(question.getDatePublicationJO()));
                    dto.setAuteur(question.getNomCompletAuteur());
                    dto.setMotsCles(question.getMotsClef());
                    dto.setMinistereAttributaire(question.getIntituleMinistere());

                    if (currentItems != null) {
                        currentItems.add(dto);
                    }
                }
            }
        }
    }
}
