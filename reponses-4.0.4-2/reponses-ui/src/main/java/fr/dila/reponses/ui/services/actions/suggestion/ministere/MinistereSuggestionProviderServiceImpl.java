package fr.dila.reponses.ui.services.actions.suggestion.ministere;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.ui.services.actions.suggestion.SuggestionConstants;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.organigramme.AlphabeticalAndBdcOrderComparator;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public class MinistereSuggestionProviderServiceImpl implements MinistereSuggestionProviderService {
    private static final String SELECT_MINISTERE_INTERROGES =
        "SELECT DISTINCT qu:intituleMinistere FROM Question WHERE qu:intituleMinistere ILIKE '%s'";
    public static final String QU_INTITULE_MINISTERE =
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION;
    public static final String QU_ID_MINISTERE_INTERROGE =
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION;

    public static final String QU_DATE_DEBUT_MINISTERE_INTERROGE =
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_DATE_DEBUT_MINISTERE_INTERROGE_QUESTION;

    public static final String QU_DATE_FIN_MINISTERE_INTERROGE =
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_DATE_FIN_MINISTERE_INTERROGE_QUESTION;

    private static final String NAME = "MINISTERE_PROVIDER";

    @Override
    public List<String> getSuggestions(String input, SpecificContext context) {
        List<String> resultList = new ArrayList<>();
        String ministere = input.replace("'", "\\'");

        try (
            IterableQueryResult docs = context
                .getSession()
                .queryAndFetch(
                    String.format(SELECT_MINISTERE_INTERROGES, "%" + ministere + "%"),
                    "NXQL",
                    SuggestionConstants.MAX_RESULTS
                )
        ) {
            for (Map<String, Serializable> doc : docs) {
                resultList.add((String) doc.get(QU_INTITULE_MINISTERE));
            }
            return resultList;
        }
    }

    @Override
    public List<Map<String, Serializable>> getAllMinisteresInterroges(CoreSession session) {
        StringBuilder strBuilder = new StringBuilder("select distinct ")
            .append(DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION)
            .append(",")
            .append(DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION)
            .append(" FROM Question q ORDER BY ")
            .append(DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION);
        List<Map<String, Serializable>> intituleMinisteres = new ArrayList<>();
        final String id = QU_ID_MINISTERE_INTERROGE;
        final String label = QU_INTITULE_MINISTERE;

        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { id, label },
                strBuilder.toString(),
                new Object[] {}
            )
        ) {
            Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                Map<String, Serializable> row = it.next();
                intituleMinisteres.add(row);
            }
        }

        return intituleMinisteres;
    }

    @Override
    public List<Map<String, Serializable>> getAllMinisteres(CoreSession session) {
        // liste les ministeres attributaires
        List<Map<String, Serializable>> allMinisteres = getMinistereAttributaires();
        // ajout d'un separateur
        allMinisteres.add(getSeparator());
        // liste les ministeres interrogés
        allMinisteres.addAll(getAllMinisteresInterroges(session));
        return allMinisteres;
    }

    private Map<String, Serializable> getSeparator() {
        Map<String, Serializable> separator = new HashMap<>();
        separator.put(QU_ID_MINISTERE_INTERROGE, StringUtils.EMPTY);
        separator.put(QU_INTITULE_MINISTERE, StringUtils.EMPTY);
        return separator;
    }

    private List<Map<String, Serializable>> getMinistereAttributaires() {
        List<Map<String, Serializable>> intituleMinistereAttributaires = new ArrayList<>();
        List<EntiteNode> currentMinisteres = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();
        Collections.sort(currentMinisteres, new ProtocolarOrderComparator());
        for (EntiteNode ministere : currentMinisteres) {
            Map<String, Serializable> intitule = new HashMap<>();
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

    @Override
    public List<Map<String, Serializable>> getAllMinistereListRecherche() {
        List<Map<String, Serializable>> allMinistereListRecherche = new ArrayList<>();
        List<EntiteNode> ministeres = null;

        List<OrganigrammeNode> listeTermine = new ArrayList<>();

        // Récupération des ministères courrants :
        List<EntiteNode> currentMinisteres = STServiceLocator.getSTMinisteresService().getCurrentMinisteres();

        // On trie la liste des ministères en cours par ordre protocolaire
        Collections.sort(currentMinisteres, new ProtocolarOrderComparator());

        // On ajoute ensuite la liste des ministères en cours dans la liste complète
        for (OrganigrammeNode ministereCourant : currentMinisteres) {
            Map<String, Serializable> intituleCourant = new HashMap<>();
            intituleCourant.put(QU_ID_MINISTERE_INTERROGE, ministereCourant.getId());
            intituleCourant.put(QU_INTITULE_MINISTERE, ministereCourant.getLabel());
            intituleCourant.put(QU_DATE_FIN_MINISTERE_INTERROGE, "");
            intituleCourant.put(QU_DATE_DEBUT_MINISTERE_INTERROGE, "");
            allMinistereListRecherche.add(intituleCourant);
        }

        // Ajout d'une ligne vide entre les anciens ministères et les nouveaux
        Map<String, Serializable> intituleVide = new HashMap<>();
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
            Map<String, Serializable> intituleTermine = new HashMap<>();
            intituleTermine.put(QU_ID_MINISTERE_INTERROGE, ministerePasse.getId());
            intituleTermine.put(QU_INTITULE_MINISTERE, ministerePasse.getLabel());
            if (ministerePasse.getDateFin() == null) {
                intituleTermine.put(QU_DATE_FIN_MINISTERE_INTERROGE, "");
                intituleTermine.put(QU_DATE_DEBUT_MINISTERE_INTERROGE, "");
            } else {
                intituleTermine.put(
                    QU_DATE_DEBUT_MINISTERE_INTERROGE,
                    "(" + SolonDateConverter.DATE_SLASH.format(ministerePasse.getDateDebut())
                );
                intituleTermine.put(
                    QU_DATE_FIN_MINISTERE_INTERROGE,
                    " - " + SolonDateConverter.DATE_SLASH.format(ministerePasse.getDateFin()) + ")"
                );
            }
            allMinistereListRecherche.add(intituleTermine);
        }

        return allMinistereListRecherche;
    }
}
