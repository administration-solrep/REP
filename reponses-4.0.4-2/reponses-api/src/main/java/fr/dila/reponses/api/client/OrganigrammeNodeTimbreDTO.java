package fr.dila.reponses.api.client;

import fr.dila.st.api.organigramme.OrganigrammeType;
import java.io.Serializable;

/**
 * Interface de transport des informations d'un noeud de l'organigramme
 *
 */
public interface OrganigrammeNodeTimbreDTO extends Serializable {
    /**
     * getter de l'id du node dans le ldap
     *
     * @return
     */
    String getId();

    /**
     * setter de l'id du node dans le ldap
     *
     * @param nodeId
     */
    void setId(String nodeId);

    /**
     * getter du label du node
     *
     * @return
     */
    String getLabel();

    /**
     * setter du label du node
     *
     * @param label
     */
    void setLabel(String label);

    /**
     * geter du nombre de question close pour ce node
     *
     * @return
     */
    Long getCountClose();

    /**
     * setter du nombre de question closes pour ce node
     *
     * @param countClose
     */
    void setCountClose(Long countClose);

    /**
     * getter du nombre de questions signées pour ce node
     *
     * @return
     */
    Long getCountSigne();

    /**
     * setter du nombre des questions signées pour ce node
     *
     * @param countSigne
     */
    void setCountSigne(Long countSigne);

    /**
     * getter du nombre des questions migrables
     *
     * @return
     */
    Long getCountMigrable();

    /**
     * setter des questions migrables pour ce node
     *
     * @param countMigrable
     */
    void setCountMigrable(Long countMigrable);

    /**
     * getter des modeles de fdr pour ce node
     *
     * @return
     */
    Long getCountModelFDR();

    /**
     * setter du nombre des modèles de fdr pour ce node
     *
     * @param countModelFDR
     */
    void setCountModelFDR(Long countModelFDR);

    /**
     * getter de l'ordre protocolaire pour le node
     *
     * @return
     */
    long getOrder();

    /**
     * Setter de l'ordre protocolaire pour le node
     *
     * @param order
     */
    void setOrder(long order);

    /**
     * Getter indiquant si la signature doit être brisée pour les réponses de ce ministère
     *
     * @return
     */
    boolean getBreakingSeal();

    /**
     * Setter pour indiquer si la signature doit être brisée pour les réponses de ce ministère
     *
     * @param breaking
     */
    void setBreakingSeal(boolean breaking);

    /**
     * Getter pour le label du booleen : utilise le libelle dans messages.properties
     *
     * @return
     */
    String getBreakingSealLabel();

    /**
     * Getter indiquant si les dossiers clos de ce ministère doivent être migrés
     *
     * @param migrate
     */
    void setMigratingDossiersClos(boolean migrate);

    /**
     * Setter pour indiquer si les dossiers clos de ce ministère doivent être migrés
     *
     * @return
     */
    boolean getMigratingDossiersClos();

    /**
     * Getter pour le label du booleen : utilise le libelle dans messages.properties
     *
     * @return
     */
    String getMigratingDossiersClosLabel();

    /**
     * Setter du label pour le timbre remplaçant le noeud
     *
     * @param labelNextTimbre
     * @return
     */
    void setLabelNextTimbre(String labelNextTimbre);

    /**
     * Getter du label pour le timbre remplaçant le noeud
     */
    String getLabelNextTimbre();

    void setType(OrganigrammeType type);

    OrganigrammeType getType();
}
