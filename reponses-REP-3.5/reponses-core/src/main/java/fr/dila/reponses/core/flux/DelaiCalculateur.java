package fr.dila.reponses.core.flux;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.VocabularyConstants;

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
	 * au format : J+/- <delai>
	 * 
	 * @param Date
	 *            datePublication La date de publication au journal officiel.
	 * @return La string donnant le délai
	 */
	public static String getDelai(final Calendar datePublicationOrig, int delaiFeuilleRouteEnMois) {
		DateTime dateDuJour = new DateTime(Calendar.getInstance());
		return getDelai(datePublicationOrig, delaiFeuilleRouteEnMois, dateDuJour);
	}

	public static String getDelai(final Calendar datePublicationOrig, int delaiFeuilleRouteEnMois, DateTime dateDuJour) {
		if (datePublicationOrig == null) {
			return "";
		}

		DateTime datePublication = new DateTime(datePublicationOrig.getTime());
		Period expirationPeriod = new Period().withMonths(delaiFeuilleRouteEnMois);
		DateTime dateEcheance = datePublication.plus(expirationPeriod);
		Days days = Days.daysBetween(dateEcheance, dateDuJour);
		int daysCnt = days.getDays();

		String result = "J";
		if (daysCnt < 0) {
			result += daysCnt;
		} else if (daysCnt > 0) {
			result += "+" + daysCnt;
		}
		return result;
	}

	/**
	 * calcul le delai d'expiration d'une question en fonction de son etat, de sa date de publication et de la duree de
	 * traitement
	 * 
	 * @param question
	 * @param reponseDureeTraitement
	 * @param coreSession
	 * @return
	 */
	public static String computeDelaiExpirationFdr(Question question, Integer reponseDureeTraitement,
			CoreSession coreSession) {
		String delai = "";
		if (question.getDatePublicationJO() != null) {
			QuestionStateChange etatQuestion = question.getEtatQuestion(coreSession);
			if (question.isRepondue()) {
				delai = "label.reponse.repondue";
			} else if (question.isRetiree()) {
				delai = "label.reponse.retiree";
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion.getNewState())) {
				delai = "label.reponse.caduque";
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion.getNewState())) {
				delai = "label.reponse.clos";
			} else {
				delai = getDelai(question.getDatePublicationJO(), reponseDureeTraitement);
			}
		}
		return delai;
	}
	
	/**
	 * calcul le delai d'expiration d'une question en fonction de son etat, de sa date de publication et de la duree de
	 * traitement
	 * 
	 * @param question
	 * @param reponseDureeTraitement
	 * @param coreSession
	 * @return
	 */
	public static String computeDelaiExpirationFdrForCsv(Question question, Integer reponseDureeTraitement,
			CoreSession coreSession) {
		String delai = "";
		if (question.getDatePublicationJO() != null) {
			QuestionStateChange etatQuestion = question.getEtatQuestion(coreSession);
			if (question.isRepondue()) {
				delai = VocabularyConstants.DELAI_DOSSIER_REPONDUE;
			} else if (question.isRetiree()) {
				delai = VocabularyConstants.DELAI_DOSSIER_RETIRE;
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion.getNewState())) {
				delai = VocabularyConstants.DELAI_DOSSIER_CADUQUE;
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion.getNewState())) {
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
	 * 
	 * @param question
	 * @param etatQuestion
	 * @param reponseDureeTraitement
	 * @return
	 */
	public static String computeDelaiExpirationFdr(final Question question, final String etatQuestion, 
			final Integer reponseDureeTraitement) {
		String delai = "";
		final Calendar datePublicationJO = question.getDatePublicationJO();
		if (datePublicationJO != null) {
			if (question.isRepondue()) {
				delai = "label.reponse.repondue";
			} else if (question.isRetiree()) {
				delai = "label.reponse.retiree";
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion)) {
				delai = "label.reponse.caduque";
			} else if (etatQuestion != null
					&& VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)) {
				delai = "label.reponse.clos";
			} else {
				delai = getDelai(datePublicationJO, reponseDureeTraitement);
			}
		}
		return delai;
	}
}
