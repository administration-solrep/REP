package fr.dila.reponses.core.event;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;

/**
 * Listener qui génère les statistiques du rapport à afficher sur la page
 * d'accueil
 *
 * @author mchahine
 */
public class GenerateReportListener implements EventListener {
    private static final Log LOG = LogFactory.getLog(GenerateReportListener.class);

    public GenerateReportListener() {
        // do nothing
    }

    @Override
    public void handleEvent(Event event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Batch de génération des statistiques du rapport à afficher sur la page d'accueil");
        }

        ReponsesServiceLocator.getStatsService().generateReportStats(event.getContext().getCoreSession());
    }
}
