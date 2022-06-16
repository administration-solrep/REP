package fr.dila.reponses.ui.contentview;

import static java.util.Optional.ofNullable;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.ui.bean.EliminationDonneesDTO;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ListeEliminationPageProvider extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) throws NuxeoException {
        currentItems = new ArrayList<>();

        resultsCount = QueryUtils.doCountQuery(coreSession, query);

        if (resultsCount > 0) {
            List<String> ids = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
            populateFromListeEliminationsIds(coreSession, ids);
        }
    }

    protected void populateFromListeEliminationsIds(CoreSession coreSession, List<String> ids) {
        if (!ids.isEmpty()) {
            List<DocumentModel> dml = QueryUtils.retrieveDocuments(
                coreSession,
                ReponsesSchemaConstant.LISTE_ELIMINATION_TYPE,
                ids,
                true
            );
            for (DocumentModel dm : dml) {
                if (dm != null) {
                    ListeElimination listeElimination = dm.getAdapter(ListeElimination.class);
                    Calendar dateCreation = (Calendar) dm.getPropertyValue(STSchemaConstant.DUBLINCORE_MODIFIED_XPATH);
                    EliminationDonneesDTO eddto = new EliminationDonneesDTO(
                        dm.getId(),
                        dm.getTitle(),
                        ofNullable(dateCreation)
                            .map(date -> SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(date))
                            .orElse(""),
                        listeElimination.isEnCours(),
                        listeElimination.isAbandonEnCours(),
                        listeElimination.isSuppressionEnCours()
                    );

                    if (currentItems != null) {
                        currentItems.add(eddto);
                    }
                }
            }
        }
    }
}
