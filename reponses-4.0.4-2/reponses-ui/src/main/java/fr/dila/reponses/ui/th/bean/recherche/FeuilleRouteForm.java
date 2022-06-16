package fr.dila.reponses.ui.th.bean.recherche;

import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_DEBUT_ETAPE_DEBUT_INTERVALLE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_DEBUT_ETAPE_FIN_INTERVALLE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_FIN_ETAPE_DEBUT_INTERVALLE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_FIN_ETAPE_FIN_INTERVALLE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DIRECTION_ID_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAPE_OBLIGATOIRE_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAPE_STATUT_ID_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.POSTE_ID_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.TITRE_FDR_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.TYPE_STEP_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.VALIDATION_AUTOMATIQUE_XPATH;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean
public class FeuilleRouteForm implements Serializable {
    private static final long serialVersionUID = -7420694231495255445L;

    @NxProp(xpath = TYPE_STEP_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("typeEtape")
    private String typeEtape;

    @NxProp(xpath = ETAPE_STATUT_ID_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("statut")
    private String statut = VocabularyConstants.STATUS_ETAPE_DEFAULT_VALUE;

    @NxProp(xpath = DIRECTION_ID_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("direction-key")
    private String direction;

    private Map<String, String> mapDirection = new HashMap<>();

    @NxProp(xpath = POSTE_ID_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("poste-key")
    private String poste;

    private Map<String, String> mapPoste = new HashMap<>();

    @NxProp(xpath = TITRE_FDR_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("intituleFdr")
    private String intituleFdr;

    @NxProp(xpath = DATE_DEBUT_ETAPE_DEBUT_INTERVALLE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateDebutEtapeDebut")
    private Calendar dateDebutEtapeDebut;

    @NxProp(xpath = DATE_DEBUT_ETAPE_FIN_INTERVALLE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateDebutEtapeFin")
    private Calendar dateDebutEtapeFin;

    @NxProp(xpath = DATE_FIN_ETAPE_DEBUT_INTERVALLE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateFinEtapeDebut")
    private Calendar dateFinEtapeDebut;

    @NxProp(xpath = DATE_FIN_ETAPE_FIN_INTERVALLE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateFinEtapeFin")
    private Calendar dateFinEtapeFin;

    @NxProp(xpath = ETAPE_OBLIGATOIRE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("etapeObligatoire")
    private Boolean etapeObligatoire;

    @NxProp(xpath = VALIDATION_AUTOMATIQUE_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("rafraichissementAuto")
    private Boolean rafraichissementAuto;

    public FeuilleRouteForm() {}

    public String getTypeEtape() {
        return typeEtape;
    }

    public void setTypeEtape(String typeEtape) {
        this.typeEtape = typeEtape;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getIntituleFdr() {
        return intituleFdr;
    }

    public void setIntituleFdr(String intituleFdr) {
        this.intituleFdr = intituleFdr;
    }

    public Calendar getDateDebutEtapeDebut() {
        return dateDebutEtapeDebut;
    }

    public void setDateDebutEtapeDebut(Calendar dateDebutEtapeDebut) {
        this.dateDebutEtapeDebut = dateDebutEtapeDebut;
    }

    public Calendar getDateDebutEtapeFin() {
        return dateDebutEtapeFin;
    }

    public void setDateDebutEtapeFin(Calendar dateDebutEtapeFin) {
        this.dateDebutEtapeFin = dateDebutEtapeFin;
    }

    public Calendar getDateFinEtapeDebut() {
        return dateFinEtapeDebut;
    }

    public void setDateFinEtapeDebut(Calendar dateFinEtapeDebut) {
        this.dateFinEtapeDebut = dateFinEtapeDebut;
    }

    public Calendar getDateFinEtapeFin() {
        return dateFinEtapeFin;
    }

    public void setDateFinEtapeFin(Calendar dateFinEtapeFin) {
        this.dateFinEtapeFin = dateFinEtapeFin;
    }

    public Boolean getEtapeObligatoire() {
        return etapeObligatoire;
    }

    public void setEtapeObligatoire(Boolean etapeObligatoire) {
        this.etapeObligatoire = etapeObligatoire;
    }

    public Boolean getRafraichissementAuto() {
        return rafraichissementAuto;
    }

    public void setRafraichissementAuto(Boolean rafraichissementAuto) {
        this.rafraichissementAuto = rafraichissementAuto;
    }

    public Map<String, String> getMapDirection() {
        return mapDirection;
    }

    public void setMapDirection(Map<String, String> mapDirection) {
        this.mapDirection = mapDirection;
    }

    public Map<String, String> getMapPoste() {
        return mapPoste;
    }

    public void setMapPoste(Map<String, String> mapPoste) {
        this.mapPoste = mapPoste;
    }
}
