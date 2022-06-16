package fr.dila.reponses.ui.bean;

import fr.dila.st.core.client.AbstractMapDTO;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class EliminationDonneesDTO extends AbstractMapDTO {
    private static final long serialVersionUID = 1L;

    private static final String ID = "id";
    private static final String TITRE = "titre";
    private static final String DATE_CREATION = "dateCreation";
    private static final String EN_COURS = "enCours";
    private static final String ABANDON_EN_COURS = "abandonEnCours";
    private static final String SUPPRESSION_EN_COURS = "suppressionEnCours";
    private static final String ACTIONS = "actions";
    private static final String DOC_ID_FOR_SELECTION = "docIdForSelection";

    public EliminationDonneesDTO() {
        super();
    }

    public EliminationDonneesDTO(
        String id,
        String titre,
        String dateCreation,
        boolean isEnCours,
        boolean isAbandonEnCours,
        boolean isSuppressionEnCours
    ) {
        super();
        setId(id);
        setTitre(titre);
        setDateCreation(dateCreation);
        setEnCours(isEnCours);
        setAbandonEnCours(isAbandonEnCours);
        setSuppressionEnCours(isSuppressionEnCours);
    }

    public String getId() {
        return getString(ID);
    }

    public void setId(String id) {
        put(ID, id);
    }

    public String getTitre() {
        return getString(TITRE);
    }

    public void setTitre(String titre) {
        put(TITRE, titre);
    }

    public String getDateCreation() {
        return getString(DATE_CREATION);
    }

    public void setDateCreation(String dateCreation) {
        put(DATE_CREATION, dateCreation);
    }

    public boolean isEnCours() {
        return getBoolean(EN_COURS);
    }

    public void setEnCours(boolean enCours) {
        put(EN_COURS, enCours);
    }

    public boolean isAbandonEnCours() {
        return getBoolean(ABANDON_EN_COURS);
    }

    public void setAbandonEnCours(boolean abadonEnCours) {
        put(ABANDON_EN_COURS, abadonEnCours);
    }

    public boolean isSuppressionEnCours() {
        return getBoolean(SUPPRESSION_EN_COURS);
    }

    public void setSuppressionEnCours(boolean suppressionEnCours) {
        put(SUPPRESSION_EN_COURS, suppressionEnCours);
    }

    @SuppressWarnings("unchecked")
    public List<Action> getActions() {
        return (List<Action>) get(ACTIONS);
    }

    public void setActions(List<Action> actions) {
        put(ACTIONS, (Serializable) actions);
    }

    @Override
    public String getType() {
        return "EliminationDonnees";
    }

    @Override
    public String getDocIdForSelection() {
        return getString(DOC_ID_FOR_SELECTION);
    }

    public void setDocIdForSelection(String docIdForSelection) {
        put(DOC_ID_FOR_SELECTION, docIdForSelection);
    }
}
