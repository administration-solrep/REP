package fr.dila.reponses.api.service;

import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;

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
     * @throws ClientException
     */
    Boolean isAllotit(Question question, CoreSession session) throws ClientException;

    /**
     * Retourne true si le dossier est allotie
     * 
     * @param dossier
     * @param session
     * @return
     * @throws ClientException
     */
    Boolean isAllotit(Dossier dossier, CoreSession session);
    
    /**
     * Crée un lot de question
     * 
     * @param q
     * @param listQuestions
     * @param session
     * @return
     * @throws ClientException
     */
    Boolean createLot(Question q, List<Question> listQuestions, CoreSession session) throws ClientException;

    /**
     * Ajoute ou supprime une question à un lot existant
     * 
     * @param q
     * @param listQuestions
     * @param session
     * @param type
     * @return
     * @throws ClientException
     */
    Boolean updateLot(Question q, List<Question> listQuestions, CoreSession session, TypeAllotissement type) throws ClientException;

    /**
     * Retourne un lot de questions à partir de son nom
     * 
     * @param nom
     * @param session
     * @return
     * @throws ClientException
     */
    Allotissement getAllotissement(String nom, CoreSession session) throws ClientException;

    /**
     * Recopie le texte de la réponse dans tous les dossiers du lot
     * 
     * @param coreSession
     * @param repDoc
     * @throws ClientException
     */
    void updateTexteLinkedReponses(CoreSession coreSession, DocumentModel repDoc) throws ClientException;

    /**
     * Recopie les informations de version dans toutes les réponses du lot
     * 
     * @param coreSession
     * @param repDoc
     * @throws ClientException
     */
    void updateVersionLinkedReponses(CoreSession coreSession, DocumentModel repDoc) throws ClientException;

    /**
     * Recopie les informations de signature dans toutes les réponses du lot
     * 
     * @param coreSession
     * @param reponse
     * @throws ClientException
     */
    void updateSignatureLinkedReponses(CoreSession coreSession, Reponse reponse) throws ClientException;

    /**
     * Retourne les questions alloties par le document allotissement
     */
    List<Question> getQuestionAlloties(CoreSession session, Allotissement allotissment) throws ClientException;
    
    /**
     * Retourne les questions alloties par le document allotissement en tenant compte de l'ordre
     * (Dossier Directeur puis dossiers par ordre d'ajout)
     */
    List<Question> getQuestionAllotiesWithOrder(CoreSession session, Allotissement allotissment) throws ClientException;
    
    /**
     * Retourne les questions alloties par le document allotissement en tenant compte de l'ordre source (AN/SENAT)
     * et numéro de question (ascendant sur les 2).
     * 
     * Mantis 158094 - ordre d'affichage des questions alloties
     */
    List<Question> getQuestionAllotiesWithOrderOrigineNumero(CoreSession session, Allotissement allotissment) throws ClientException;

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
     * @throws ClientException
     */
    Boolean createLotWS(Question q, List<Question> listQuestions, CoreSession session);

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
     * @throws ClientException
     */
    boolean validateDossierRappel(CoreSession session, Dossier dossierDirecteur, String idMinistereQuestion, Question questionRappel) throws ClientException;

    /**
     * Retourne la liste des dossiers qui sont des dossiers directeurs parmi les dossiers passés en paramètres
     * @param listDossiersDoc : Le documentModelList contenant l'ensemble des dossiers pour lesquels on souhaite avoir l'information
     * @param session
     * @return un set contenant les identifiants des dossiers directeurs
     * @throws ClientException
     */
	Set<String> extractDossiersDirecteurs(List<DocumentModel> listDossiersDoc, CoreSession session)
			throws ClientException;

}
