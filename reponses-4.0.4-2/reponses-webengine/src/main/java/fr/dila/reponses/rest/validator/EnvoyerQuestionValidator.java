package fr.dila.reponses.rest.validator;

import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.EtatQuestion;
import fr.sword.xsd.reponses.IndexationAn;
import fr.sword.xsd.reponses.IndexationSenat;
import fr.sword.xsd.reponses.IndexationSenat.Renvois;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.Question;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Contient un ensemble de test pour valider une requete auprès du service web
 * EnvoyerQuestion
 * @author spesnel
 *
 */
public class EnvoyerQuestionValidator {
    private static final int INDEXATION_SENAT_NB_MAX_RENVOI = 3;

    private static final String ERROR_AUTEUR_INVALID_MSG = "Veuillez corriger les informations sur l'auteur.";

    private static final String ERROR_TEXTE_INVALID_MSG = "Veuillez saisir un texte pour la question.";

    private static final String ERROR_ETAT_QUESTION_INVALID_MSG = "Veuillez saisir un état de question.";

    private static final String ERROR_MINISTRE_DEPOT_INVALID_MSG = "Veuillez saisir un ministère de dépot.";

    private static final String ERROR_INDEXATION_NO_DATA_MSG = "Données d'indexation absentes";
    private static final String ERROR_INDEXATION_MULTIPLE_DATA_TYPE_MSG =
        "Présence simultanée des deux types AN/SENAT d'indexation de données non autorisée.";

    private static final String ERROR_INDEXATION_AN_NO_DATA_MSG = ERROR_INDEXATION_NO_DATA_MSG;
    private static final String ERROR_INDEXATION_AN_NO_RUBRIQUE_MSG = "Rubrique(s) d'indexation absente(s).";

    private static final String ERROR_INDEXATION_SENAT_NO_DATA_MSG = ERROR_INDEXATION_NO_DATA_MSG;
    private static final String ERROR_INDEXATION_SENAT_NO_TITRE_MSG = "Senat : Titre sénat absent.";
    private static final String ERROR_INDEXATION_SENAT_NO_THEMES_MSG = "SENAT : Thème(s) absent(s).";
    private static final String ERROR_INDEXATION_SENAT_NO_RUBRIQUES_MSG = "SENAT : Rubrique(s) absente(s).";
    private static final String ERROR_INDEXATION_SENAT_TOO_MUCH_RENVOI_MSG =
        "SENAT : Le nombre de renvoi excède la limite de " + INDEXATION_SENAT_NB_MAX_RENVOI + ". ";

    /**
     * Auteur valide si tous les attribut sont non null
     * @param auteur
     * @return
     */
    private static boolean isAuteurValid(Auteur auteur) {
        return !(
            auteur == null ||
            auteur.getCivilite() == null ||
            auteur.getIdMandat() == null ||
            auteur.getGrpPol() == null ||
            auteur.getCirconscription() == null ||
            auteur.getNom() == null ||
            auteur.getPrenom() == null
        );
    }

    /**
     * le texte de la question est valide si non vide
     * @param texte
     * @return
     */
    private static boolean isTexteQuestionValid(String texte) {
        return StringUtils.isNotEmpty(texte);
    }

    private static boolean isEtatQuestionValid(EtatQuestion etatQueston) {
        return etatQueston != null;
    }

    private static boolean isMinistereDepotValid(Ministre ministreDepot) {
        return (
            ministreDepot != null && ministreDepot.getIntituleMinistere() != null && ministreDepot.getTitreJo() != null
        );
    }

    /**
     * teste si la question de données d'indexation pour l'AN ou pour le SENAT
     * @param question
     * @return
     */
    private static boolean hasIndexationData(Question question) {
        return (question.getIndexationAn() != null) || (question.getIndexationSenat() != null);
    }

    /**
     * teste si la question a des donnees d'indexation que pour l'an ou que pour le senat
     * @param question
     * @return
     */
    private static boolean hasOnlyOneTypeOfIndexationData(Question question) {
        return (question.getIndexationAn() == null) || (question.getIndexationSenat() == null);
    }

    private static ValidatorResult validateAuteur(Auteur auteur) {
        if (isAuteurValid(auteur)) {
            return ValidatorResult.RESULT_OK;
        } else {
            return ValidatorResult.error(ERROR_AUTEUR_INVALID_MSG);
        }
    }

    private static ValidatorResult validateTexteQuestion(String texte) {
        if (isTexteQuestionValid(texte)) {
            return ValidatorResult.RESULT_OK;
        } else {
            return ValidatorResult.error(ERROR_TEXTE_INVALID_MSG);
        }
    }

    private static ValidatorResult validateEtatQuestion(EtatQuestion etatQuestion) {
        if (isEtatQuestionValid(etatQuestion)) {
            return ValidatorResult.RESULT_OK;
        } else {
            return ValidatorResult.error(ERROR_ETAT_QUESTION_INVALID_MSG);
        }
    }

    private static ValidatorResult validateMinistreDepot(Ministre ministreDepot) {
        if (isMinistereDepotValid(ministreDepot)) {
            return ValidatorResult.RESULT_OK;
        } else {
            return ValidatorResult.error(ERROR_MINISTRE_DEPOT_INVALID_MSG);
        }
    }

    private static ValidatorResult validateIndexation(Question question) {
        if (!hasIndexationData(question)) {
            return ValidatorResult.error(ERROR_INDEXATION_NO_DATA_MSG);
        }
        if (!hasOnlyOneTypeOfIndexationData(question)) {
            return ValidatorResult.error(ERROR_INDEXATION_MULTIPLE_DATA_TYPE_MSG);
        }
        return ValidatorResult.RESULT_OK;
    }

    /**
     * valide
     *  - l'auteur,
     *  - le texte de la question,
     *  - son etat
     *  - le ministre depot
     *  - la présence d'indexation
     * @param question
     * @return
     */
    public static ValidatorResult validateQuestionData(Question question) {
        ValidatorResult validResult = EnvoyerQuestionValidator.validateAuteur(question.getAuteur());
        if (!validResult.isValid()) {
            // stop auteur invalid
            return validResult;
        }

        validResult = EnvoyerQuestionValidator.validateTexteQuestion(question.getTexte());
        if (!validResult.isValid()) {
            // stop texte question invalide
            return validResult;
        }

        validResult = EnvoyerQuestionValidator.validateEtatQuestion(question.getEtatQuestion());
        if (!validResult.isValid()) {
            // stop etat question invalid
            return validResult;
        }

        validResult = EnvoyerQuestionValidator.validateMinistreDepot(question.getMinistreDepot());
        if (!validResult.isValid()) {
            // stop ministre depot invalide
            return validResult;
        }

        validResult = EnvoyerQuestionValidator.validateIndexation(question);
        if (!validResult.isValid()) {
            // stop : indexation incorrect
            return validResult;
        }

        return ValidatorResult.RESULT_OK;
    }

    public static ValidatorResult validateIndexationAN(IndexationAn indexationAn) {
        if (indexationAn == null) {
            return ValidatorResult.error(ERROR_INDEXATION_AN_NO_DATA_MSG);
        }

        if (indexationAn.getRubrique() == null) {
            return ValidatorResult.error(ERROR_INDEXATION_AN_NO_RUBRIQUE_MSG);
        }

        return ValidatorResult.RESULT_OK;
    }

    public static ValidatorResult validateIndexatioSenat(String titreSenat, IndexationSenat indexationSenat) {
        if (titreSenat == null) {
            return ValidatorResult.error(ERROR_INDEXATION_SENAT_NO_TITRE_MSG);
        }

        if (indexationSenat == null) {
            return ValidatorResult.error(ERROR_INDEXATION_SENAT_NO_DATA_MSG);
        }

        if (indexationSenat.getTheme() == null) {
            return ValidatorResult.error(ERROR_INDEXATION_SENAT_NO_THEMES_MSG);
        }

        if (indexationSenat.getRubrique() == null) {
            return ValidatorResult.error(ERROR_INDEXATION_SENAT_NO_RUBRIQUES_MSG);
        }

        Renvois renvois = indexationSenat.getRenvois();
        if (renvois != null) {
            List<String> renvoisList = renvois.getRenvoi();
            if (renvoisList != null && renvoisList.size() > INDEXATION_SENAT_NB_MAX_RENVOI) {
                return ValidatorResult.error(ERROR_INDEXATION_SENAT_TOO_MUCH_RENVOI_MSG);
            }
        }

        return ValidatorResult.RESULT_OK;
    }

    // utility class
    private EnvoyerQuestionValidator() {}
}
