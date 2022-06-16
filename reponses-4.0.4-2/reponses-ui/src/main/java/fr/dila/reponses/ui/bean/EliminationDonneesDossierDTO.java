package fr.dila.reponses.ui.bean;

import fr.dila.st.core.client.AbstractMapDTO;
import java.io.Serializable;
import java.util.List;

public class EliminationDonneesDossierDTO extends AbstractMapDTO {
    private static final long serialVersionUID = 1L;

    private static final String ID = "id";
    private static final String ORIGINE = "origine";
    private static final String QUESTION = "question";
    private static final String NATURE = "nature";
    private static final String DATE_PUBLICATION = "datePublication";
    private static final String AUTEUR = "auteur";
    private static final String MINISTERE_ATTRIBUTAIRE = "ministereAttributaire";
    private static final String MOTS_CLES = "motsCles";
    private static final String DOC_ID_FOR_SELECTION = "docIdForSelection";

    public EliminationDonneesDossierDTO() {
        super();
    }

    public String getId() {
        return getString(ID);
    }

    public void setId(String id) {
        put(ID, id);
    }

    public String getOrigine() {
        return getString(ORIGINE);
    }

    public void setOrigine(String origine) {
        put(ORIGINE, origine);
    }

    public String getQuestion() {
        return getString(QUESTION);
    }

    public void setQuestion(String question) {
        put(QUESTION, question);
    }

    public String getNature() {
        return getString(NATURE);
    }

    public void setNature(String nature) {
        put(NATURE, nature);
    }

    public String getDatePublication() {
        return getString(DATE_PUBLICATION);
    }

    public void setDatePublication(String datePublication) {
        put(DATE_PUBLICATION, datePublication);
    }

    public String getAuteur() {
        return getString(AUTEUR);
    }

    public void setAuteur(String auteur) {
        put(AUTEUR, auteur);
    }

    public String getMinistereAttributaire() {
        return getString(MINISTERE_ATTRIBUTAIRE);
    }

    public void setMinistereAttributaire(String ministereAttributaire) {
        put(MINISTERE_ATTRIBUTAIRE, ministereAttributaire);
    }

    public List<String> getMotsCles() {
        return getListString(MOTS_CLES);
    }

    public void setMotsCles(List<String> motsCles) {
        put(MOTS_CLES, (Serializable) motsCles);
    }

    @Override
    public String getType() {
        return "EliminationDonneesDossier";
    }

    @Override
    public String getDocIdForSelection() {
        return getString(DOC_ID_FOR_SELECTION);
    }

    public void setDocIdForSelection(String docIdForSelection) {
        put(DOC_ID_FOR_SELECTION, docIdForSelection);
    }
}
