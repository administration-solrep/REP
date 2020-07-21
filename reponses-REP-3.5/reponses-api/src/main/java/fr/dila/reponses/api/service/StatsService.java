package fr.dila.reponses.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.event.batch.BatchLoggerModel;

public interface StatsService extends Serializable {

    /**
     * Calcul et rempli les tables de statistiques.
     */
    public void computeStats(final CoreSession session, final BatchLoggerModel batchLoggerModel) throws ClientException;

    /**
     * Dénormalise les directions sur les étapes de feuille de route passées.
     * @throws ClientException 
     */
    public void denormaliserDirection(CoreSession session) throws ClientException;

    /**
     * Dénormalise les labels sur les étapes suivante de feuille de route pour les dossiers.
     * @throws ClientException 
     */
    public void denormaliserEtapeSuivante(CoreSession session) throws ClientException;

    /**
     * Dénormalise les ministeres sur les étapes de feuille de route
     * @param session
     * @throws ClientException
     */
    void denormaliserMinistere(CoreSession session) throws ClientException;
}
