package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Une opération pour réparer l'historique d'attributions des dossiers.
 *
 * @author bgamard
 */
@Operation(
    id = ReparerHistoriqueAttributionOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "ReparerHistoriqueAttribution",
    description = "Réparer l'historique d'attributions des dossiers"
)
public class ReparerHistoriqueAttributionOperation {
    private static final String HISTORIQUE_DOSSIER_TRAITE =
        "SELECT d.ecm:uuid as id FROM Dossier AS d WHERE d.dos:historiqueDossierTraite = ?";

    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Reparer.Historique.Attribution";

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ReparerHistoriqueAttributionOperation.class);

    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() {
        LOGGER.info("Début opération " + ID);

        LOGGER.info("Comptage de tous les dossiers");
        final Long count = QueryUtils.doCountQuery(
            session,
            QueryUtils.ufnxqlToFnxqlQuery(HISTORIQUE_DOSSIER_TRAITE),
            new Object[] { false }
        );
        LOGGER.info(count + " dossiers");

        for (Long offset = (long) 0; offset <= count; offset += (long) 1000) {
            LOGGER.info("Récupération des dossiers à l'offset : " + offset);
            final List<DocumentModel> dossiersDoc = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                "Dossier",
                HISTORIQUE_DOSSIER_TRAITE,
                new Object[] { false }
            );
            LOGGER.info("Fin - Récupération de tous les dossiers");

            int i = 0;
            LOGGER.info("Réparation des historiques");

            for (final DocumentModel dossierDoc : dossiersDoc) {
                final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

                // Faut-il ajouter une attribution initiale ?
                final List<HistoriqueAttribution> historiques = dossier.getHistoriqueAttribution(session);
                boolean attributionInitaleFound = false;
                for (final HistoriqueAttribution historique : historiques) {
                    final String typeAttribution = historique.getTypeAttribution();
                    if (STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION.equals(typeAttribution)) {
                        attributionInitaleFound = true;
                        break;
                    }
                }

                // Passe le dossier à traité
                dossierDoc.setProperty(DossierConstants.DOSSIER_SCHEMA, "historiqueDossierTraite", true);
                session.saveDocument(dossierDoc);
                session.save();

                if (attributionInitaleFound) {
                    continue;
                }

                // Ajout de l'attribution initiale
                final Question question = dossier.getQuestion(session);
                Calendar date = question.getDatePublicationJO();
                if (date == null) {
                    date = DublincoreSchemaUtils.getCreatedDate(dossierDoc);
                }

                LOGGER.info(
                    "Ajout d'une attribution initiale pour le dossier " +
                    question.getSourceNumeroQuestion() +
                    "; ministère: " +
                    question.getIdMinistereInterroge() +
                    "; date: " +
                    date
                );

                dossier.addHistoriqueAttribution(
                    session,
                    STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION,
                    date,
                    question.getIdMinistereInterroge()
                );

                session.saveDocument(dossierDoc);
                session.save();

                i++;
            }
            LOGGER.info("Réparation des historiques : " + (offset + i) + "/" + count);
        }

        LOGGER.info("Réparation des historiques : " + count + "/" + count);
        LOGGER.info("Fin de l'opération" + ID);
        return;
    }
}
