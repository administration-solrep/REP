package fr.dila.reponses.core.flux;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ResourceHelper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Optional;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 *
 * Regroupe les fonctions utilitaires pour calculer le delai qui reste entre la date de publication et la date limite de
 * publication
 *
 * @author jgomez, arolin
 *
 */
public class DelaiCalculateur {

    /**
     * Calcule le delai d'expiration de la feuille de route à partir d'une date de publication, et retourne une string
     * au format : J+/- delai
     *
     * @param datePublicationOrig
     *            datePublication La date de publication au journal officiel.
     * @return La string donnant le délai
     */
    public static String getDelai(final Calendar datePublicationOrig, int delaiFeuilleRouteEnMois) {
        LocalDate dateDuJour = LocalDate.now();
        return getDelai(datePublicationOrig, delaiFeuilleRouteEnMois, dateDuJour);
    }

    public static String getDelai(
        final Calendar datePublicationOrig,
        int delaiFeuilleRouteEnMois,
        LocalDate dateDuJour
    ) {
        if (datePublicationOrig == null) {
            return "";
        }

        LocalDate datePublication = DateUtil.gregorianCalendarToLocalDate(datePublicationOrig);
        LocalDate dateEcheance = datePublication.plusMonths(delaiFeuilleRouteEnMois);
        long days = ChronoUnit.DAYS.between(dateEcheance, dateDuJour);

        String result = "J";
        if (days < 0) {
            result += days;
        } else if (days > 0) {
            result += "+" + days;
        }
        return result;
    }

    /**
     * calcul le delai d'expiration d'une question en fonction de son etat, de sa date de publication et de la duree de
     * traitement
     */
    public static String computeDelaiExpirationFdr(
        Question question,
        Integer reponseDureeTraitement,
        CoreSession coreSession
    ) {
        Calendar datePublicationJO = question.getDatePublicationJO();
        return Optional
            .ofNullable(datePublicationJO)
            .map(
                date ->
                    computeDelaiQuestionPubliee(
                        question,
                        Optional
                            .ofNullable(question.getEtatQuestion(coreSession))
                            .map(QuestionStateChange::getNewState)
                            .orElse(null),
                        reponseDureeTraitement,
                        date
                    )
            )
            .orElse("");
    }

    /**
     * calcul le delai d'expiration d'une question en fonction de son etat, de sa date de publication et de la duree de
     * traitement
     */
    public static String computeDelaiExpirationFdrForCsv(
        Question question,
        Integer reponseDureeTraitement,
        CoreSession coreSession
    ) {
        String delai = "";
        if (question.getDatePublicationJO() != null) {
            QuestionStateChange etatQuestion = question.getEtatQuestion(coreSession);
            if (question.isRepondue()) {
                delai = VocabularyConstants.DELAI_DOSSIER_REPONDUE;
            } else if (question.isRetiree()) {
                delai = VocabularyConstants.DELAI_DOSSIER_RETIRE;
            } else if (
                etatQuestion != null && VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion.getNewState())
            ) {
                delai = VocabularyConstants.DELAI_DOSSIER_CADUQUE;
            } else if (
                etatQuestion != null &&
                VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion.getNewState())
            ) {
                delai = VocabularyConstants.DELAI_DOSSIER_CLOS;
            } else {
                delai = getDelai(question.getDatePublicationJO(), reponseDureeTraitement);
            }
        }
        return delai;
    }

    /**
     * calcul le delai d'expiration d'une question en fonction de son etat, de sa date de publication et de la duree de
     * traitement
     */
    public static String computeDelaiExpirationFdr(
        final Question question,
        final String etatQuestion,
        final Integer reponseDureeTraitement
    ) {
        Calendar datePublicationJO = question.getDatePublicationJO();
        return Optional
            .ofNullable(datePublicationJO)
            .map(date -> computeDelaiQuestionPubliee(question, etatQuestion, reponseDureeTraitement, date))
            .orElse("");
    }

    private static String computeDelaiQuestionPubliee(
        Question question,
        String etatQuestion,
        Integer reponseDureeTraitement,
        Calendar datePublicationJO
    ) {
        String delai;
        if (question.isRepondue()) {
            delai = ResourceHelper.getString("label.reponse.repondue");
        } else if (question.isRetiree()) {
            delai = ResourceHelper.getString("label.reponse.retiree");
        } else if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion)) {
            delai = ResourceHelper.getString("label.reponse.caduque");
        } else if (VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)) {
            delai = ResourceHelper.getString("label.reponse.clos");
        } else {
            delai = getDelai(datePublicationJO, reponseDureeTraitement);
        }
        return delai;
    }
}
