package fr.dila.reponses.core.service;

import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doUFNXQLQueryAndMapping;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doUFNXQLQueryForIdsList;
import static java.util.Objects.requireNonNull;

import fr.dila.reponses.api.service.DossierQueryService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service pour récupérer des dossiers
 *
 * @author SCE
 *
 */
public class DossierQueryServiceImpl implements DossierQueryService {

    @Override
    public Map<String, String> getMapDossierOrigineIdsFromNumero(
        final CoreSession session,
        final String numeroQuestion
    ) {
        requireNonNull(session, "null CoreSession");
        requireNonNull(numeroQuestion, "null numeroQuestion");

        String queryUFNXQL =
            "SELECT d.ecm:uuid AS id, q.qu:origineQuestion AS orig FROM Dossier AS d, Question AS q WHERE d.dos:numeroQuestion = ? AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.ecm:currentLifeCycleState != 'deleted'";
        List<ImmutablePair<String, String>> listMap = doUFNXQLQueryAndMapping(
            session,
            queryUFNXQL,
            new String[] { numeroQuestion },
            (Map<String, Serializable> rowData) ->
                new ImmutablePair<String, String>((String) rowData.get("id"), (String) rowData.get("orig"))
        );
        return listMap.stream().collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    @Override
    public List<String> getDossierIdsFromNumeroAndOrigine(
        final CoreSession session,
        final String numeroQuestion,
        final String origineQuestion
    ) {
        requireNonNull(session, "null CoreSession");
        requireNonNull(numeroQuestion, "null numeroQuestion");

        List<String> params = new ArrayList<>(2);
        params.add(numeroQuestion);

        StringBuilder querySb = new StringBuilder(
            "SELECT d.ecm:uuid AS id FROM Dossier AS d, Question AS q WHERE d.dos:numeroQuestion = ? "
        );
        if (StringUtils.isNotBlank(origineQuestion)) {
            querySb.append("AND q.qu:origineQuestion = ? ");
            params.add(origineQuestion);
        }
        querySb.append("AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.ecm:currentLifeCycleState != 'deleted'");

        return doUFNXQLQueryForIdsList(session, querySb.toString(), params.toArray(new String[params.size()]));
    }
}
