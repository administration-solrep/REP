package fr.dila.reponses.web.suggestion;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.st.core.query.QueryUtils;

@Name("nomAuteurProvider")
@Scope(ScopeType.CONVERSATION)
public class NomAuteurSuggestionProvider implements Serializable, ISuggestionProvider {
	private static final int		MAX_RESULTS			= 10;

	private static final long		serialVersionUID	= 1L;

	@In(create = true, required = true)
	protected transient CoreSession	documentManager;

	private static final String		NAME				= "NOM_AUTEUR_PROVIDER";

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Renvoie les suggestions de noms complets des auteurs, basé sur le contenu du nom ou du prénom. Insensible à la
	 * casse et aux accents.
	 * 
	 */
	@Override
	public List<String> getSuggestions(Object input) throws ClientException {
		String search = "%"
				+ Normalizer.normalize(input.toString(), Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toUpperCase() + "%";

		StringBuilder request = new StringBuilder(
				"select distinct nomCompletAuteur from "
						+ "question Q where TRANSLATE(UPPER(nomAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?"
						+ " OR TRANSLATE(UPPER(prenomAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?"
						+ " OR TRANSLATE(UPPER(nomCompletAuteur), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?");

		List<String> out = new ArrayList<String>();

		final String colname = "qu:nomCompletAuteur";
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(documentManager, new String[] { colname }, request.toString(), new Object[] {
					search, search, search }, MAX_RESULTS, 0);
			Iterator<Map<String, Serializable>> it = res.iterator();
			while (it.hasNext()) {
				Map<String, Serializable> row = it.next();
				out.add((String) row.get(colname));
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return out;
	}
}
