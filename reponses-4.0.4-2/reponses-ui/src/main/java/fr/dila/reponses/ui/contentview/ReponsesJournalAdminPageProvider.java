package fr.dila.reponses.ui.contentview;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getDossierQueryService;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.DossierQueryService;
import fr.dila.ss.ui.query.pageprovider.SSJournalAdminPageProvider;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

/**
 * page provider du journal affiche dans l'espace d'administration
 *
 * @author BBY, ARN
 * @author asatre
 *
 */
public class ReponsesJournalAdminPageProvider extends SSJournalAdminPageProvider {
    private static final long serialVersionUID = 1L;

    private static final STLogger LOG = STLogFactory.getLog(ReponsesJournalAdminPageProvider.class);

    protected transient Map<String, String> dossierOriginQuestion = new HashMap<>();

    /**
     * Récupération des identifiants du dossier à partir d'un numéro
     */
    @Override
    protected void getDossierIdsList() {
        Map<String, Serializable> props = getProperties();
        // récupération de la session
        CoreSession coreSession = (CoreSession) props.get(CORE_SESSION_PROPERTY);
        if (coreSession == null) {
            throw new NuxeoException("cannot find core session");
        }

        // récupération des identifiants du dossier à partir de la question
        String numeroQuestion = (String) getParameters()[4];

        // ajout du filtre sur la référence du dossier
        if (StringUtils.isNotEmpty(numeroQuestion)) {
            String origineQuestion = EMPTY;
            String[] s = numeroQuestion.split(SPACE);
            if (s.length == 2) {
                origineQuestion = s[0];
                numeroQuestion = s[1];
            }

            // Vérification qu'il s'agit bien d'un dossier composé de chiffres
            Pattern numericPattern = Pattern.compile("[0-9]+");
            Matcher numericMatcher = numericPattern.matcher(numeroQuestion);
            if (numericMatcher.matches()) {
                DossierQueryService dqService = getDossierQueryService();

                if (origineQuestion.isEmpty()) {
                    dossierOriginQuestion = dqService.getMapDossierOrigineIdsFromNumero(coreSession, numeroQuestion);
                    dossierIdList = new ArrayList<>(dossierOriginQuestion.keySet());
                } else {
                    dossierIdList =
                        dqService.getDossierIdsFromNumeroAndOrigine(coreSession, numeroQuestion, origineQuestion);
                    final String orig = origineQuestion;
                    dossierOriginQuestion = dossierIdList.stream().collect(toMap(identity(), k -> orig));
                }
            } else {
                dossierIdList = null;
            }
        }
    }

    @Override
    public String getDossierRef(CoreSession session, LogEntry entry) {
        String dossierRef = entry.getDocUUID();
        // si la référence est nulle, on affiche la référence
        if (StringUtils.isBlank(dossierRef)) {
            return dossierRef;
        }

        String numeroQuestion = (String) getParameters()[4];
        if (isNotEmpty(numeroQuestion)) {
            String[] s = numeroQuestion.split(SPACE);
            if (s.length == 2) {
                return numeroQuestion;
            } else if (s.length == 1) {
                String origine = dossierOriginQuestion.get(dossierRef);
                if (isNotEmpty(origine)) {
                    return String.format("%s %s", origine, numeroQuestion);
                }
            }
        }

        DocumentRef docRef = new IdRef(dossierRef);
        if (session.exists(docRef)) {
            String refQuestion = getRefQuestion(session, docRef);

            if (StringUtils.isNotBlank(refQuestion)) {
                return refQuestion;
            }
        }

        LOG.debug(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, "erreur lors de la récupération du dossier");
        return dossierRef;
    }

    private static String getRefQuestion(CoreSession session, DocumentRef docRef) {
        // on récupère le numéro de la question
        DocumentModel docModel = session.getDocument(docRef);
        Dossier dossier = docModel.getAdapter(Dossier.class);
        Question appQuestion = dossier.getQuestion(session);

        // M156851 - Ajout de la provenance de la question
        StringBuilder refQuestion = new StringBuilder();
        if (appQuestion != null && StringUtils.isNotBlank(appQuestion.getOrigineQuestion())) {
            refQuestion.append(appQuestion.getOrigineQuestion());
        }

        if (dossier.getNumeroQuestion() != null) {
            if (StringUtils.isNotBlank(refQuestion)) {
                refQuestion.append(" ");
            }
            refQuestion.append(dossier.getNumeroQuestion());
        }

        return refQuestion.toString();
    }
}
