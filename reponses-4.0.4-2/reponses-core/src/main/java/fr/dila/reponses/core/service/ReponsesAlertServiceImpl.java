package fr.dila.reponses.core.service;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.core.export.RepDossierConfig;
import fr.dila.ss.core.service.AbstractAlertService;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import java.util.List;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ReponsesAlertServiceImpl
    extends AbstractAlertService<RequeteExperte, DocumentModel>
    implements ReponsesAlertService {
    private static final long serialVersionUID = 6176887476817534463L;
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesAlertServiceImpl.class);

    public ReponsesAlertServiceImpl() {
        super();
    }

    @Override
    public Alert initAlertFromRequete(final CoreSession session, final DocumentModel requeteDoc) {
        Alert alert = initAlert(session);
        if (alert == null) {
            return null;
        } else {
            // Si le document requete est null, on garde l'alerte désactivée par défaut
            if (requeteDoc == null) {
                LOGGER.warn(session, STLogEnumImpl.FAIL_CREATE_ALERT_TEC, "Requete is null - Alert deactivated");
            } else {
                alert.setIsActivated(true);
                alert.setRequeteId(requeteDoc.getId());
                String requeteTitle = "requête";
                try {
                    requeteTitle = requeteDoc.getTitle();
                } catch (NuxeoException ce) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, requeteDoc, ce);
                }
                alert.setTitle("Alerte sur " + requeteTitle);
                return alert;
            }
        }
        return null;
    }

    @Override
    protected RequeteExperte getRequeteExperte(CoreSession session, Alert alert) {
        return alert.getRequeteExperte(session);
    }

    @Override
    protected long countResultsFromRequete(CoreSession session, RequeteExperte requeteExperte, String username) {
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        return requeteurService.countResults(session, requeteExperte);
    }

    @Override
    protected String getDefaultEmailSubject(CoreSession session, STParametreService paramService) {
        return paramService.getParametreValue(session, STParametreConstant.OBJET_MAIL_NOTIFICATION_ALERTE);
    }

    @Override
    protected String getDefaultEmailContent(CoreSession session, STParametreService paramService, long nbResults) {
        String content = paramService.getParametreValue(session, STParametreConstant.TEXTE_MAIL_NOTIFICATION_ALERTE);
        return StringHelper.renderFreemarker(content, ImmutableMap.of("nb_resultats", String.valueOf(nbResults)));
    }

    @Override
    protected List<DocumentModel> getDossiersFromRequete(
        CoreSession session,
        RequeteExperte requeteExperte,
        String username,
        long nbResults
    ) {
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        return requeteurService.query(session, requeteExperte);
    }

    @Override
    protected DataSource getDataSource(CoreSession session, List<DocumentModel> dossiers) {
        RepDossierConfig dossierConfig = new RepDossierConfig(dossiers);
        return dossierConfig.getDataSource(session);
    }
}
