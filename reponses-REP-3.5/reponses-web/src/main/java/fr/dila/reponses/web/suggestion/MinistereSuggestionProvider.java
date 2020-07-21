package fr.dila.reponses.web.suggestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.organigramme.AlphabeticalAndBdcOrderComparator;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

import fr.dila.st.core.util.DateUtil;

@Name("ministereInterrogeProvider")
@Scope(ScopeType.CONVERSATION)
public class MinistereSuggestionProvider implements Serializable, ISuggestionProvider {
	private static final String		SELECT_MINISTERE_INTERROGES			= "SELECT DISTINCT qu:intituleMinistere FROM Question WHERE qu:intituleMinistere ILIKE '%s'";
	private static final String		QU_INTITULE_MINISTERE				= DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX
																				+ ":"
																				+ DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION;
	private static final String		QU_ID_MINISTERE_INTERROGE			= DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX
																				+ ":"
																				+ DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION;

	private static final String		QU_DATE_DEBUT_MINISTERE_INTERROGE	= DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX
																				+ ":"
																				+ DossierConstants.DOSSIER_DATE_DEBUT_MINISTERE_INTERROGE_QUESTION;

	private static final String		QU_DATE_FIN_MINISTERE_INTERROGE		= DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX
																				+ ":"
																				+ DossierConstants.DOSSIER_DATE_FIN_MINISTERE_INTERROGE_QUESTION;

	private static final long		serialVersionUID					= 1L;

	@In(create = true, required = true)
	protected transient CoreSession	documentManager;

	private static final String		NAME								= "MINISTERE_PROVIDER";

	@Override
	public List<String> getSuggestions(Object input) throws ClientException {
		List<String> result_list = new ArrayList<String>();
		String input_str = (String) input;
		String ministere = input_str.replace("'", "\\'");
		IterableQueryResult docs = null;
		try {
			docs = documentManager.queryAndFetch(String.format(SELECT_MINISTERE_INTERROGES, "%" + ministere + "%"),
					"NXQL", MAX_RESULTS);
			for (Map<String, Serializable> doc : docs) {
				result_list.add((String) doc.get(QU_INTITULE_MINISTERE));
			}
			return result_list;
		} finally {
			if (docs != null) {
				docs.close();
			}
		}

	}

	/**
	 * Retourne la liste de tous les ministères interrogés (historique de tous les ministères utilisés dans
	 * l'application)
	 * 
	 * @return
	 * @throws ClientException
	 */
	public List<Map<String, Serializable>> getAllMinisteresInterroges() throws ClientException {
		StringBuilder strBuilder = new StringBuilder("select distinct ")
				.append(DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION).append(",")
				.append(DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION).append(" FROM Question q ORDER BY ")
				.append(DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION);
		List<Map<String, Serializable>> intituleMinisteres = new ArrayList<Map<String, Serializable>>();
		final String id = QU_ID_MINISTERE_INTERROGE;
		final String label = QU_INTITULE_MINISTERE;
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(documentManager, new String[] { id, label }, strBuilder.toString(),
					new Object[] {});
			Iterator<Map<String, Serializable>> it = res.iterator();
			while (it.hasNext()) {
				Map<String, Serializable> row = it.next();
				intituleMinisteres.add(row);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}
		return intituleMinisteres;
	}

	public List<Map<String, Serializable>> getAllMinisteres() throws ClientException {
		// liste les ministeres attributaires
		List<Map<String, Serializable>> allMinisteres = getMinistereAttributaires();
		// ajout d'un separateur
		allMinisteres.add(getSeparator());
		// liste les ministere interrohés
		allMinisteres.addAll(getAllMinisteresInterroges());
		return allMinisteres;

	}

	private Map<String, Serializable> getSeparator() {
		Map<String, Serializable> separator = new HashMap<String, Serializable>();
		separator.put(QU_ID_MINISTERE_INTERROGE, StringUtils.EMPTY);
		separator.put(QU_INTITULE_MINISTERE, StringUtils.EMPTY);
		return separator;
	}

	private List<Map<String, Serializable>> getMinistereAttributaires() throws ClientException {
		List<Map<String, Serializable>> intituleMinistereAttributaires = new ArrayList<Map<String, Serializable>>();
		List<EntiteNode> currentMinisteres = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();
		Collections.sort(currentMinisteres, new ProtocolarOrderComparator());
		for (EntiteNode ministere : currentMinisteres) {
			Map<String, Serializable> intitule = new HashMap<String, Serializable>();
			intitule.put(QU_ID_MINISTERE_INTERROGE, ministere.getId());
			intitule.put(QU_INTITULE_MINISTERE, ministere.getLabel());
			intituleMinistereAttributaires.add(intitule);
		}
		return intituleMinistereAttributaires;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public List<Map<String, Serializable>> getAllMinistereListRecherche() throws ClientException {

		List<Map<String, Serializable>> allMinistereListRecherche = new ArrayList<Map<String, Serializable>>();
		List<EntiteNode> ministeres = null;

		List<OrganigrammeNode> listeTermine = new ArrayList<OrganigrammeNode>();

		// Récupération des ministères courrants :
		List<EntiteNode> currentMinisteres = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();

		// On trie la liste des ministères en cours par ordre protocolaire
		Collections.sort(currentMinisteres, new ProtocolarOrderComparator());

		// On ajoute ensuite la liste des ministères en cours dans la liste complète
		for (OrganigrammeNode ministereCourant : currentMinisteres) {
			Map<String, Serializable> intituleCourant = new HashMap<String, Serializable>();
			intituleCourant.put(QU_ID_MINISTERE_INTERROGE, ministereCourant.getId());
			intituleCourant.put(QU_INTITULE_MINISTERE, ministereCourant.getLabel());
			intituleCourant.put(QU_DATE_FIN_MINISTERE_INTERROGE, "");
			intituleCourant.put(QU_DATE_DEBUT_MINISTERE_INTERROGE, "");
			allMinistereListRecherche.add(intituleCourant);
		}

		// Ajout d'une ligne vide entre les anciens ministères et les nouveaux
		Map<String, Serializable> intituleVide = new HashMap<String, Serializable>();
		intituleVide.put(QU_ID_MINISTERE_INTERROGE, "");
		intituleVide.put(QU_INTITULE_MINISTERE, "");
		intituleVide.put(QU_DATE_FIN_MINISTERE_INTERROGE, "");
		intituleVide.put(QU_DATE_DEBUT_MINISTERE_INTERROGE, "");
		allMinistereListRecherche.add(intituleVide);

		ministeres = STServiceLocator.getSTMinisteresService().getAllMinisteres();
		for (OrganigrammeNode ministere : ministeres) {
			if (!ministere.isActive()) {
				// Ajout du ministère à la liste des ministères terminés
				listeTermine.add(ministere);
			}
		}

		// On trie la liste des ministères terminés par ordre alphabétique
		Collections.sort(listeTermine, new AlphabeticalAndBdcOrderComparator());

		// On ajoute ensuite les ministères terminés
		for (OrganigrammeNode ministerePasse : listeTermine) {
			Map<String, Serializable> intituleTermine = new HashMap<String, Serializable>();
			intituleTermine.put(QU_ID_MINISTERE_INTERROGE, ministerePasse.getId());
			intituleTermine.put(QU_INTITULE_MINISTERE, ministerePasse.getLabel());
			if (ministerePasse.getDateFin() == null) {
				intituleTermine.put(QU_DATE_FIN_MINISTERE_INTERROGE, "");
				intituleTermine.put(QU_DATE_DEBUT_MINISTERE_INTERROGE, "");
			} else {
				intituleTermine.put(QU_DATE_DEBUT_MINISTERE_INTERROGE,
						"(" + DateUtil.formatDDMMYYYYSlash(ministerePasse.getDateDebut()));
				intituleTermine.put(QU_DATE_FIN_MINISTERE_INTERROGE,
						" - " + DateUtil.formatDDMMYYYYSlash(ministerePasse.getDateFin()) + ")");
			}
			allMinistereListRecherche.add(intituleTermine);
		}

		return allMinistereListRecherche;
	}
}
