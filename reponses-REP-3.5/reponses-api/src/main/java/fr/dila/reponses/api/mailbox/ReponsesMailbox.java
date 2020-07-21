package fr.dila.reponses.api.mailbox;

import java.util.List;
import java.util.Map;

import fr.dila.cm.mailbox.Mailbox;

/**
 * Interface des documents de type ReponsesMailbox.
 * 
 * @author spl
 */
public interface ReponsesMailbox extends Mailbox {

    /**
     * retourne les nombres de dossier link precalculés par ministere et type d'etape
     * @return
     */
    List<PreComptage> getPreComptages();
    
    /**
     * retourne les nombre de dossier link par ministere et type d'etape groupé par ministere
     * @return
     */
    Map<String, List<PreComptage>> getPreComptagesGroupByMinistereId();
    
    /**
     * remplace les nombres de dossier link precalculés par ministere et type d'etape
     * @param precomptages
     */
    void setPreComptages(List<PreComptage> precomptages);
    
    /**
     * decremente la valeur correspondant à un ministere et a un type d'action
     * Supprime l'entrée si la valeur de comptage est nulle
     * @param ministereId
     * @param routingTaskType
     */
    void decrPreComptage(String ministereId, String routingTaskType);
    
    /**
     * incremente la valeur correspondant à un ministere et a un type d'action
     * Ajoute une nouvelle entrée si nécessaire
     * @param ministereId
     * @param routingTaskType
     */
    void incrPreComptage(String ministereId, String routingTaskType);
    
}