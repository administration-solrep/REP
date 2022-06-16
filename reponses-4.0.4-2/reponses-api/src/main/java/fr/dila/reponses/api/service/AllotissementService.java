package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.exception.AllotissementException;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service d'allotissement.
 *
 * @author asatre
 */
public interface AllotissementService {
    /**
     * Retourne true si la question est allotie
     *
     * @param question
     * @param session
     * @return
     *
     */
    boolean isAllotit(Question question, CoreSession session);

    /**
     * Retourne true si le dossier est alloti
     *
     * @param dossier
     * @return
     *
     */
    boolean isAllotit(Dossier dossier);

    /**
     * Crée un lot de question
     *
     * @param q
     * @param listQuestions
     * @param session
     * @return
     *
     */
    boolean createLot(Question q, List<Question> listQuestions, CoreSession session);

    /**
     * Ajoute ou supprime une question à un lot existant
     *
     * @param q
     * @param listQuestions
     * @param session
     * @param type
     * @return
     *
     */
    boolean updateLot(Question q, List<Question> listQuestions, CoreSession session, TypeAllotissement type);

    /**
     * Retourne un lot de questions à partir de son nom
     *
     * @param nom
     * @param session
     * @return
     *
     */
    Allotissement getAllotissement(String nom, CoreSession session);

    /**
     * Recopie le texte de la réponse dans tous les dossiers du lot
     *
     * @param coreSession
     * @param repDoc
     *
     */
    void updateTexteLinkedReponses(CoreSession coreSession, DocumentModel repDoc);

    /**
     * Recopie les informations de version dans toutes les réponses du lot
     *
     * @param coreSession
     * @param repDoc
     *
     */
    void updateVersionLinkedReponses(CoreSession coreSession, DocumentModel repDoc);

    /**
     * Recopie les informations de signature dans toutes les réponses du lot
     *
     * @param coreSession
     * @param reponse
     *
     */
    void updateSignatureLinkedReponses(CoreSession coreSession, Reponse reponse);

    /**
     * Retourne les questions alloties par le document allotissement en
     * concervant l'ordre par rapport aux ids de dossier de l'allotissement
     */
    List<Question> getQuestionAlloties(CoreSession session, Allotissement allotissment);

    /**
     * Retourne les questions alloties par le document allotissement en tenant compte de l'ordre source (AN/SENAT)
     * et numéro de question (ascendant sur les 2).
     *
     * Mantis 158094 - ordre d'affichage des questions alloties
     */
    List<Question> getQuestionAllotiesWithOrderOrigineNumero(CoreSession session, Allotissement allotissment);

    /**
     * Retire un dossier de son lot. Si le dossier est null ou non alloti, ne fait rien.
     *
     * @param session la session de l'utilisateur
     * @param dossier le dossier à retirer
     * @throws AllotissementException une exception est levée si le retrait n'a pas pu aboutir.
     */
    void removeDossierFromLotIfNeeded(CoreSession session, Dossier dossier) throws AllotissementException;

    /**
     * Crée un lot de question (spécifique pour les webservices, envoi d'un mail et non d'une exception)
     *
     * @param q
     * @param listQuestions
     * @param session
     * @return
     *
     */
    boolean createLotWS(Question q, List<Question> listQuestions, CoreSession session);

    /**
     * Mise à jour ou creation d'un lot sur une question rappelée.
     * On suppose que le dossier est dans le bon état (précédemment testé par validateDossierRappel
     *
     * @param questionDirectrice
     * @param question : question sans feuille de route
     * @param session
     * @param ajout
     */
    void createOrAddToLotRappel(final Question questionDirectrice, final Question question, CoreSession session);

    /**
     * Valide l'état du dossier avant allotissement avec une question rappelée.
     *
     * @param session
     * @param dossierDirecteur
     * @param idMinistreQuestion
     * @param questionRappel
     * @return True si le dossier est valide
     *
     */
    boolean validateDossierRappel(
        CoreSession session,
        Dossier dossierDirecteur,
        String idMinistereQuestion,
        Question questionRappel
    );

    /**
     * Retourne la liste des dossiers qui sont des dossiers directeurs parmi les dossiers passés en paramètres
     * @param listDossiersDoc : Le documentModelList contenant l'ensemble des dossiers pour lesquels on souhaite avoir l'information
     * @param session
     * @return un set contenant les identifiants des dossiers directeurs
     *
     */
    Set<String> extractDossiersDirecteurs(List<DocumentModel> listDossiersDoc, CoreSession session);
}
