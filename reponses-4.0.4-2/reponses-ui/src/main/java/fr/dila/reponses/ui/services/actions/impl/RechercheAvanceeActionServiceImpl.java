package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.recherche.RechercheUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.RechercheAvanceeActionService;
import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RechercheAvanceeActionServiceImpl implements RechercheAvanceeActionService {
    private static final Log LOG = LogFactory.getLog(RechercheAvanceeActionServiceImpl.class);

    public List<Recherche> getRechercheList() {
        final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        return rechercheService.getRecherches();
    }

    public Recherche getRecherche(String rechercheName) {
        final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        Recherche recherche = rechercheService.getRecherche(rechercheName);
        return recherche;
    }

    public String precomputeResults(
        CoreSession session,
        DocumentModel currentRequete,
        IndexModeEnum currentIndexationMode
    ) {
        Requete requete = currentRequete.getAdapter(Requete.class);
        requete.setIndexationMode(currentIndexationMode);
        requete.doBeforeQuery();

        final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String fullQuery = rechercheService.getFullQuery(session, requete);
        LOG.info(fullQuery);
        return fullQuery;
    }

    public String getCurrentRequetePattern(CoreSession session, DocumentModel currentRequete) {
        final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        Requete requete = RechercheUtils.adaptRequete(currentRequete);
        String query = rechercheService.getFullQuery(session, requete);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Query :" + query);
        }
        return query;
    }

    public Requete resetRequete(DocumentModel currentRequete) {
        currentRequete.reset();
        Requete requete = RechercheUtils.adaptRequete(currentRequete);
        requete.init();
        return requete;
    }

    public DocumentModel getCurrentRequete(SpecificContext context, CoreSession session, DocumentModel currentRequete) {
        final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();

        Requete requete = rechercheService.getRequete(session, "requete");
        if (requete == null) {
            context.getMessageQueue().addInfoToQueue("La requete n'a pas pu être initialisée");
        } else {
            currentRequete = requete.getDocument();
        }
        return currentRequete;
    }
}
