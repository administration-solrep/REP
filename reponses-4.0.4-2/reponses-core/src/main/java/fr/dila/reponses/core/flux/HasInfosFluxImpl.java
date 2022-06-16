package fr.dila.reponses.core.flux;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.flux.HasInfoFlux;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Une classe pour gérer les informations de flux de type signalement, renouvellement, priorité. Le document passé au
 * constructeur doit pouvoir être adapté en Question.
 *
 * @author jgomez
 *
 */
public class HasInfosFluxImpl implements HasInfoFlux {
    protected Question question;
    private static final Log log = LogFactory.getLog(HasInfosFluxImpl.class);

    public HasInfosFluxImpl(DocumentModel doc) {
        setDocument(doc);
    }

    public DocumentModel getDocument() {
        return question.getDocument();
    }

    @Override
    public void setDocument(DocumentModel doc) {
        this.question = doc.getAdapter(Question.class);
    }

    @Override
    public Boolean isUrgent() {
        return question.getEtatRappele();
    }

    @Override
    public Boolean isRenouvelle() {
        return question.isRenouvelle();
    }

    @Override
    public Boolean isSignale() {
        return question.isSignale();
    }

    @Override
    public String getDelaiExpirationFdr(CoreSession coreSession) {
        STParametreService paramService = STServiceLocator.getSTParametreService();
        String reponseDureeTraitement;
        try {
            reponseDureeTraitement =
                paramService.getParametreValue(coreSession, STParametreConstant.QUESTION_DUREE_TRAITEMENT);
        } catch (NuxeoException e) {
            log.warn("la durée de traitement de question définie dans les paramètres n'a pas pu être récupérée", e);
            return "";
        }

        return DelaiCalculateur.computeDelaiExpirationFdr(
            question,
            Integer.parseInt(reponseDureeTraitement),
            coreSession
        );
    }

    @Override
    public String computeEtatsQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append(isUrgent() ? "t" : "f");
        sb.append(isSignale() ? "t" : "f");
        sb.append(isRenouvelle() ? "t" : "f");
        return sb.toString();
    }
}
