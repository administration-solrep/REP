package fr.dila.reponses.web.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.flux.DelaiCalculateur;
import fr.dila.reponses.core.recherche.ReponseDossierListingDTOImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

public class ReponsesProviderHelper {
	
	public static Set<String> getIdsQuestionsWithErratum(final CoreSession session, final Collection<String> idsList) throws ClientException {
		// Récupération des errata en une seule fois : on vérifie quels documents sont parents d'un erratum#anonymousType dans hierarchy
		return getIdsQuestionsParentOf(session, idsList, "erratum#anonymousType");
	}
	
	public static Set<String> getIdsQuestionsSignalees(final CoreSession session, final Collection<String> idsList) throws ClientException {
		// Récupération des questions signalées en une seule fois : on vérifie quelles questions sont parentes d'un erratum#anonymousType dans hierarchy
		return getIdsQuestionsParentOf(session, idsList, "signalementQuestion");
	}
	
	public static Set<String> getIdsQuestionsRenouvelees(final CoreSession session, final Collection<String> idsList) throws ClientException {
		// Récupération des questions signalées en une seule fois : on vérifie quelles questions sont parentes d'un erratum#anonymousType dans hierarchy
		return getIdsQuestionsParentOf(session, idsList, "renouvellementQuestion");
	}
	
	public static Map<String, String> getQuestionsStatesFromIds(final CoreSession session, Collection<String> idsQuest) throws ClientException {
		final Map<String, String> idsQuestStatesMap = new HashMap<String, String>();
		
		final StringBuilder query = new StringBuilder("SELECT t.parentid as id, t.etatquestion FROM ")
				.append("(SELECT h.parentid, h.id, eq.date_changement_etat, eq.etatquestion, ")
				.append("RANK() OVER (PARTITION BY h.parentid ORDER BY eq.date_changement_etat DESC) dest_rank ")
				.append(" FROM hierarchy h inner join etatquestion eq on h.id = eq.id WHERE h.parentid in (")
				.append(StringUtil.getQuestionMark(idsQuest.size())).append(")) t where dest_rank = 1");
		
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { "id", "dc:title" }, query.toString(),
					idsQuest.toArray(new Object[idsQuest.size()]));
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			if (iterator.hasNext()) {
				while (iterator.hasNext()) {
					Map<String, Serializable> row = iterator.next();
					String questionId = (String) row.get("id");
					String state = (String) row.get("dc:title");
					idsQuestStatesMap.put(questionId, state);
				}
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}
		return idsQuestStatesMap;
	}
	
	public static void buildDTOFromQuestion(final ReponseDossierListingDTO rrdto, final Question question, final Integer reponseDureeTraitement, 
			final Set<String> questionsIdsWithSignalement, final Set<String> questionsIdsWithRenouvellement,
			final Set<String> questionsIdsWithErratum, final Map<String, String> questionsIdsEtats) {
		final String id = question.getDocument().getId();

		rrdto.setQuestionId(id);
		rrdto.setTypeQuestion(question.getTypeQuestion());
		rrdto.setSourceNumeroQuestion(question.getSourceNumeroQuestion());
		rrdto.setOrigineQuestion(question.getOrigineQuestion());

		if (question.getDatePublicationJO() != null) {
			rrdto.setDatePublicationJO(question.getDatePublicationJO().getTime());
		}

		if (questionsIdsWithSignalement != null && questionsIdsWithSignalement.contains(id)) {
			rrdto.setDateSignalement(question.getDateSignalementQuestion().getTime());
			rrdto.setSignale(true);
		} else {
			rrdto.setSignale(false);
		}

		rrdto.setMinistereInterroge(question.getIntituleMinistere());
		rrdto.setAuteur(question.getNomCompletAuteur());
		rrdto.setMotCles(question.getMotsCles());
		rrdto.setUrgent(question.getEtatRappele());
		if (questionsIdsWithRenouvellement != null) {
			rrdto.setRenouvelle(questionsIdsWithRenouvellement.contains(id));
		}

		rrdto.setConnexite(question.hasConnexite());

		rrdto.setDelai(DelaiCalculateur.computeDelaiExpirationFdr(question, questionsIdsEtats.get(id), reponseDureeTraitement));

		if (questionsIdsWithErratum != null) {
			rrdto.setErrata(questionsIdsWithErratum.contains(id) ? "Err" : null);
		}
		rrdto.setLegislature(question.getLegislatureQuestion() != null ? String.valueOf(question
				.getLegislatureQuestion()) : null);

		rrdto.setMinistereAttributaire(question.getIntituleMinistereAttributaire());
	}
	
	public static void buildDTOFromDossier(final ReponseDossierListingDTO rrdto, final Dossier dossier,
		final Set<String> dossiersDirecteurs, boolean isLocked, boolean updateForSelection, CoreSession session) throws ClientException {
		FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		StringBuilder libelleDirections = new StringBuilder();
		String dossierId = dossier.getDocument().getId();

		if (updateForSelection) {
			rrdto.setDocIdForSelection(dossierId);
		}
		rrdto.setDossierId(dossierId);
		rrdto.setLot(dossier.getDossierLot() != null);
		rrdto.setLocked(isLocked);

		// Analyse pour voir si le dossier a des pièces jointes
		if (dossier.hasPJ()) {
			rrdto.setAttachement(true);
		} else {
			rrdto.setAttachement(false);
		}
		
		if (dossier.isRedemarre()) {
			rrdto.setRedemarre(" Rdm");
		} else {
			rrdto.setRedemarre(null);
		}
	
		List<DocumentModel> runningStepDocList = feuilleRouteService.getRunningSteps(session, dossier.getLastDocumentRoute());
		if (runningStepDocList != null) {
			for (DocumentModel runningStepDoc : runningStepDocList) {
				STRouteStep routeStep = runningStepDoc.getAdapter(STRouteStep.class);
				
				if (routeStep.getDirectionLabel() != null) {
					if (libelleDirections.toString().isEmpty()) {
						libelleDirections.append(routeStep.getDirectionLabel());
					} else {
						libelleDirections.append(", ").append(routeStep.getDirectionLabel());
					}
				} else {
					String[] splitedMailBoxId = routeStep.getDistributionMailboxId().split("-");
					final List<OrganigrammeNode> uniteStructurelleList = STServiceLocator.getSTUsAndDirectionService()
							.getDirectionFromPoste(splitedMailBoxId[splitedMailBoxId.length-1]);
					for (final OrganigrammeNode node : uniteStructurelleList) {
						if (libelleDirections.toString().isEmpty()) {
							libelleDirections.append(node.getLabel());
						} else {
							libelleDirections.append(", ").append(node.getLabel());
						}
					}
				}
			}
			rrdto.setDirectionRunningStep(libelleDirections.toString());
			
		}

		// Gestion du cas dossier directeur
		if (dossiersDirecteurs.contains(dossierId)) {
			rrdto.setDirecteur(" * ");
		} else {
			rrdto.setDirecteur(null);
		}
	}
	
	public static void buildDTOFromDossierLink(final ReponseDossierListingDTO rrdto, final DossierLink dossierLink) {
		rrdto.setDocIdForSelection(dossierLink.getId());
		rrdto.setSourceNumeroQuestion(dossierLink.getSourceNumeroQuestion());

		if (dossierLink.getDatePublicationJO() != null) {
			rrdto.setDatePublicationJO(dossierLink.getDatePublicationJO().getTime());
		}

		rrdto.setAuteur(dossierLink.getNomCompletAuteur());
		rrdto.setMotCles(dossierLink.getMotsCles());
		rrdto.setCaseLinkId(dossierLink.getId());
		rrdto.setDossierId(dossierLink.getDossierId());
		rrdto.setRead(dossierLink.isRead());

		rrdto.setRoutingTaskType(dossierLink.getRoutingTaskLabel());

		// etats question
		rrdto.setUrgent(dossierLink.isUrgent());
		rrdto.setSignale(dossierLink.isSignale());
		rrdto.setRenouvelle(dossierLink.isRenouvelle());
	}
	
	private static Set<String> getIdsQuestionsParentOf(final CoreSession session, final Collection<String> idsList, final String primaryType) throws ClientException {
		// Récupération des questions signalées en une seule fois : on vérifie quelles questions sont parentes d'un erratum#anonymousType dans hierarchy
		final StringBuilder querySignalement = new StringBuilder("select distinct(h.parentId) as id from hierarchy h where h.parentId in (")
				.append(StringUtil.getQuestionMark(idsList.size())).append(") and h.primarytype = ?");
		final ArrayList<String> params = new ArrayList<String>(idsList);
		params.add(primaryType);
		return getIdsFromQuery(session, querySignalement.toString(), params);
	}
	
	private static Set<String> getIdsFromQuery(final CoreSession session, final String query, final Collection<String> params) throws ClientException {
		final Set<String> idsSet = new HashSet<String>();
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_ID }, query,
					params.toArray(new Object[params.size()]));
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			if (iterator.hasNext()) {
				while (iterator.hasNext()) {
					Map<String, Serializable> row = iterator.next();
					String reponseId = (String) row.get(FlexibleQueryMaker.COL_ID);
					idsSet.add(reponseId);
				}
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}
		return idsSet;
	}
}
