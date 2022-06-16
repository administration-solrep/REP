package fr.dila.reponses.ui.contentview;

import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.RowMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Cette content view attend une requete de type UNFXQL retournant les ids des favori dossier et les ids des questions associés
 *
 * (la requete doit commencer par 'SELECT f.ecm:uuid AS favoriId, f.targetDocument AS questionId FROM')
 *
 * Par rapport à la recherche, le document de reference n'est pas le dossier mais le favoriDossier
 *
 * @author spesnel
 *
 */
public class FavorisDossierPageProvider extends RechercheResultPageProvider {
    private static final String QUESTION_ID_KEY = "questionId";
    private static final String FAVORI_ID_KEY = "favoriId";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        currentItems = new ArrayList<Map<String, Serializable>>();

        List<Map<String, String>> favoriQuestionIds = null;

        resultsCount = QueryUtils.doCountQuery(coreSession, query);
        if (resultsCount > 0) {
            favoriQuestionIds =
                QueryUtils.doUFNXQLQueryAndMapping(
                    coreSession,
                    query,
                    null,
                    getPageSize(),
                    offset,
                    new RowMapper<Map<String, String>>() {

                        @Override
                        public Map<String, String> doMapping(Map<String, Serializable> rowData) {
                            Map<String, String> res = new HashMap<>();
                            res.put(QUESTION_ID_KEY, (String) rowData.get(QUESTION_ID_KEY));
                            res.put(FAVORI_ID_KEY, (String) rowData.get(FAVORI_ID_KEY));
                            return res;
                        }
                    }
                );

            List<String> questionIds = new ArrayList<>();
            Map<String, String> mapQuestionToFavori = new HashMap<>();
            for (Map<String, String> favQuesId : favoriQuestionIds) {
                String favoriId = favQuesId.get(FAVORI_ID_KEY);
                String questionId = favQuesId.get(QUESTION_ID_KEY);
                questionIds.add(questionId);
                mapQuestionToFavori.put(questionId, favoriId);
            }

            populateFromQuestionIds(coreSession, questionIds);

            // ecrase l'id de selection : met celui du favoriDossier
            for (Map<String, Serializable> item : currentItems) {
                RepDossierListingDTO rrdto = (RepDossierListingDTO) item;
                String favoriId = mapQuestionToFavori.get(rrdto.getQuestionId());
                rrdto.setDocIdForSelection(favoriId);
            }
        }
    }
}
