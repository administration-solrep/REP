package fr.dila.reponses.ui.th.bean.recherche;

import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.CARACTERISTIQUE_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.CRITERE_RECHERCHE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DIRECTION_PILOTE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.GROUPE_POLITIQUE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.LEGISLATURE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.MINISTERE_ATTRIBUTE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.MINISTERE_INTERROGE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.MINISTERE_RATTACHEMENT_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.NOM_AUTEUR_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.ORIGINE_QUESTION_XPATH;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean
public class RechercheGeneraleForm implements Serializable {
    private static final long serialVersionUID = -6263215083973356927L;

    @NxProp(xpath = LEGISLATURE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("legislature")
    private String legislature = "15";

    //@NxProp(xpath = TYPE_QUESTION_XPATH, docType= RECHERCHE_DOCUMENT_TYPE)
    @FormParam("questions")
    private String questions = VocabularyConstants.QUESTION_TYPE_QE;

    @NxProp(xpath = CARACTERISTIQUE_QUESTION_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("statusReponse")
    private ArrayList<String> statusReponse;

    @NxProp(xpath = ORIGINE_QUESTION_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("origineQuestion")
    private ArrayList<String> origineQuestion = new ArrayList<>(
        Arrays.asList(VocabularyConstants.QUESTION_ORIGINE_AN, VocabularyConstants.QUESTION_ORIGINE_SENAT)
    );

    @FormParam("etatQuestion")
    private ArrayList<String> etatQuestion;

    @NxProp(xpath = CRITERE_RECHERCHE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("numeros")
    private String numeros;

    @NxProp(xpath = NOM_AUTEUR_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("auteur")
    private String auteur;

    @NxProp(xpath = MINISTERE_ATTRIBUTE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("ministereAttributaire")
    private String ministereAttributaire;

    @NxProp(xpath = MINISTERE_INTERROGE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("ministereInterroge")
    private String ministereInterroge;

    @NxProp(xpath = MINISTERE_RATTACHEMENT_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("ministereRattachement-key")
    private String ministereRattachement;

    private Map<String, String> mapMinistereRattachement = new HashMap<>();

    @NxProp(xpath = DIRECTION_PILOTE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("directionPilote-key")
    private String directionPilote;

    private Map<String, String> mapDirectionPilote = new HashMap<>();

    @NxProp(xpath = GROUPE_POLITIQUE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("groupePolitique")
    private String groupePolitique;

    public RechercheGeneraleForm() {}

    public String getLegislature() {
        return legislature;
    }

    public void setLegislature(String legislature) {
        this.legislature = legislature;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public ArrayList<String> getStatusReponse() {
        return statusReponse;
    }

    public void setStatusReponse(ArrayList<String> statusReponse) {
        this.statusReponse = statusReponse;
    }

    public ArrayList<String> getOrigineQuestion() {
        return origineQuestion;
    }

    public void setOrigineQuestion(ArrayList<String> origineQuestion) {
        this.origineQuestion = origineQuestion;
    }

    public ArrayList<String> getEtatQuestion() {
        return etatQuestion;
    }

    public void setEtatQuestion(ArrayList<String> etatQuestion) {
        this.etatQuestion = etatQuestion;
    }

    public String getNumeros() {
        return numeros;
    }

    public void setNumeros(String numeros) {
        this.numeros = numeros;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getMinistereAttributaire() {
        return ministereAttributaire;
    }

    public void setMinistereAttributaire(String ministereAttributaire) {
        this.ministereAttributaire = ministereAttributaire;
    }

    public String getMinistereInterroge() {
        return ministereInterroge;
    }

    public void setMinistereInterroge(String ministereInterroge) {
        this.ministereInterroge = ministereInterroge;
    }

    public String getMinistereRattachement() {
        return ministereRattachement;
    }

    public void setMinistereRattachement(String ministereRattachement) {
        this.ministereRattachement = ministereRattachement;
    }

    public String getDirectionPilote() {
        return directionPilote;
    }

    public void setDirectionPilote(String directionPilote) {
        this.directionPilote = directionPilote;
    }

    public String getGroupePolitique() {
        return groupePolitique;
    }

    public void setGroupePolitique(String groupePolitique) {
        this.groupePolitique = groupePolitique;
    }

    public Map<String, String> getMapMinistereRattachement() {
        return mapMinistereRattachement;
    }

    public void setMapMinistereRattachement(Map<String, String> mapMinistereRattachement) {
        this.mapMinistereRattachement = mapMinistereRattachement;
    }

    public Map<String, String> getMapDirectionPilote() {
        return mapDirectionPilote;
    }

    public void setMapDirectionPilote(Map<String, String> mapDirectionPilote) {
        this.mapDirectionPilote = mapDirectionPilote;
    }
}
