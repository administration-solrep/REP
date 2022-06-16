package fr.dila.reponses.core.recherche;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.ss.api.recherche.IdLabel;
import fr.dila.st.core.client.AbstractMapDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class RepDossierListingDTO extends AbstractMapDTO {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RepDossierListingDTO() {
        // Constructeur par défaut
    }

    public String getSourceNumeroQuestion() {
        return getString(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION);
    }

    public String getOrigineQuestion() {
        return getString(ReponseDossierListingConstants.ORIGINE_QUESTION);
    }

    public Date getDatePublicationJO() {
        return getDate(ReponseDossierListingConstants.DATE_PUBLICATION_JO);
    }

    public Date getDateSignalement() {
        return getDate(ReponseDossierListingConstants.DATE_SIGNALEMENT_QUESTION);
    }

    public String getMinistereInterroge() {
        return getString(ReponseDossierListingConstants.MINISTERE_INTERROGE);
    }

    public String getAuteur() {
        return getString(ReponseDossierListingConstants.AUTEUR);
    }

    public String getMotCles() {
        return getString(ReponseDossierListingConstants.MOT_CLES);
    }

    public String getDelai() {
        return getString(ReponseDossierListingConstants.DELAI);
    }

    public String getEtatQuestion() {
        return getString(ReponseDossierListingConstants.ETAT);
    }

    public void setSourceNumeroQuestion(String sourceNumeroQuestion) {
        put(ReponseDossierListingConstants.SOURCE_NUMERO_QUESTION, sourceNumeroQuestion);
    }

    public void setOrigineQuestion(String origineQuestion) {
        put(ReponseDossierListingConstants.ORIGINE_QUESTION, origineQuestion);
    }

    public void setDatePublicationJO(Date datePublicationJO) {
        put(ReponseDossierListingConstants.DATE_PUBLICATION_JO, datePublicationJO);
    }

    public void setDateSignalement(Date dateSignalement) {
        put(ReponseDossierListingConstants.DATE_SIGNALEMENT_QUESTION, dateSignalement);
    }

    public void setMinistereInterroge(String ministereInterroge) {
        put(ReponseDossierListingConstants.MINISTERE_INTERROGE, ministereInterroge);
    }

    public void setAuteur(String auteur) {
        put(ReponseDossierListingConstants.AUTEUR, auteur);
    }

    public void setMotCles(String motcles) {
        put(ReponseDossierListingConstants.MOT_CLES, motcles);
    }

    public void setDelai(String delai) {
        put(ReponseDossierListingConstants.DELAI, delai);
    }

    public void setEtatQuestion(String etatQuestion) {
        put(ReponseDossierListingConstants.ETAT, etatQuestion);
    }

    public IdLabel[] getCaseLinkIdsLabels() {
        return (IdLabel[]) get(ReponseDossierListingConstants.CASE_LINK_IDS_LABELS);
    }

    public String getDossierId() {
        return getString(ReponseDossierListingConstants.DOSSIER_ID);
    }

    public void setCaseLinkIdsLabels(IdLabel[] caseLinkIdsLabels) {
        put(ReponseDossierListingConstants.CASE_LINK_IDS_LABELS, caseLinkIdsLabels);
    }

    public void setDossierId(String dossierId) {
        put(ReponseDossierListingConstants.DOSSIER_ID, dossierId);
    }

    public Boolean isUrgent() {
        return getBoolean(ReponseDossierListingConstants.IS_URGENT);
    }

    public Boolean isRenouvelle() {
        return getBoolean(ReponseDossierListingConstants.IS_RENOUVELLE);
    }

    public Boolean isSignale() {
        return getBoolean(ReponseDossierListingConstants.IS_SIGNALE);
    }

    public void setUrgent(Boolean isUrgent) {
        put(ReponseDossierListingConstants.IS_URGENT, isUrgent);
    }

    public void setRenouvelle(Boolean isRenouvelle) {
        put(ReponseDossierListingConstants.IS_RENOUVELLE, isRenouvelle);
    }

    public void setSignale(Boolean isSignale) {
        put(ReponseDossierListingConstants.IS_SIGNALE, isSignale);
    }

    @Override
    public String getDocIdForSelection() {
        return getString(ReponseDossierListingConstants.DOC_ID_FOR_SELECTION);
    }

    public void setDocIdForSelection(String docIdForSelection) {
        put(ReponseDossierListingConstants.DOC_ID_FOR_SELECTION, docIdForSelection);
    }

    @Override
    public String getType() {
        return "Dossier";
    }

    public String getQuestionId() {
        return getString(ReponseDossierListingConstants.QUESTION_ID);
    }

    public void setQuestionId(String questionId) {
        put(ReponseDossierListingConstants.QUESTION_ID, questionId);
    }

    public Boolean hasLot() {
        return getBoolean(ReponseDossierListingConstants.HAS_LOT);
    }

    public void setLot(Boolean hasLot) {
        put(ReponseDossierListingConstants.HAS_LOT, hasLot);
    }

    public Boolean isLocked() {
        return getBoolean(ReponseDossierListingConstants.IS_LOCKED);
    }

    public void setLocked(Boolean isLocked) {
        put(ReponseDossierListingConstants.IS_LOCKED, isLocked);
    }

    public Boolean hasAttachement() {
        return getBoolean(ReponseDossierListingConstants.HAS_ATTACHEMENT);
    }

    public void setAttachement(Boolean hasAttachement) {
        put(ReponseDossierListingConstants.HAS_ATTACHEMENT, hasAttachement);
    }

    public Boolean hasConnexite() {
        return getBoolean(ReponseDossierListingConstants.HAS_CONNEXITE);
    }

    public void setConnexite(Boolean hasConnexite) {
        put(ReponseDossierListingConstants.HAS_CONNEXITE, hasConnexite);
    }

    public String getTypeQuestion() {
        return getString(ReponseDossierListingConstants.TYPE_QUESTION);
    }

    public void setTypeQuestion(String typeQuestion) {
        put(ReponseDossierListingConstants.TYPE_QUESTION, typeQuestion);
    }

    public String getCaseLinkId() {
        return getString(ReponseDossierListingConstants.CASE_LINK_ID);
    }

    public Boolean isRead() {
        return getBoolean(ReponseDossierListingConstants.IS_READ);
    }

    public String getRoutingTaskType() {
        return getString(ReponseDossierListingConstants.ROUTING_TASK_TYPE);
    }

    public String getLegislature() {
        return getString(ReponseDossierListingConstants.LEGISLATURE);
    }

    public String getMinistereAttributaire() {
        return getString(ReponseDossierListingConstants.MINISTERE_ATTRIBUTAIRE);
    }

    public String getDirectionRunningStep() {
        return getString(ReponseDossierListingConstants.DIRECTION_RUNNING_STEP);
    }

    public void setCaseLinkId(String caseLinkId) {
        put(ReponseDossierListingConstants.CASE_LINK_ID, caseLinkId);
    }

    public void setRead(Boolean read) {
        put(ReponseDossierListingConstants.IS_READ, read);
    }

    public void setRoutingTaskType(String routingTaskType) {
        put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, routingTaskType);
    }

    public void setLegislature(String legilsature) {
        put(ReponseDossierListingConstants.LEGISLATURE, legilsature);
    }

    public void setMinistereAttributaire(String ministereAttributaire) {
        put(ReponseDossierListingConstants.MINISTERE_ATTRIBUTAIRE, ministereAttributaire);
    }

    public void setDirectionRunningStep(String directionRunningStep) {
        put(ReponseDossierListingConstants.DIRECTION_RUNNING_STEP, directionRunningStep);
    }

    public String getErrata() {
        return getString(ReponseDossierListingConstants.HAS_ERRATA);
    }

    public void setErrata(String hasErrata) {
        put(ReponseDossierListingConstants.HAS_ERRATA, hasErrata);
    }

    public String getRedemarre() {
        return getString(ReponseDossierListingConstants.HAS_REDEMARRE);
    }

    public void setRedemarre(String hasRedemarre) {
        put(ReponseDossierListingConstants.HAS_REDEMARRE, hasRedemarre);
    }

    public String getDirecteur() {
        return getString(ReponseDossierListingConstants.IS_DIRECTEUR);
    }

    public void setDirecteur(String isDirecteur) {
        put(ReponseDossierListingConstants.IS_DIRECTEUR, isDirecteur);
    }

    public List<String> getExposantIhm() {
        List<String> htmlLabel = new ArrayList<>();

        if (!StringUtils.isAllBlank(getRedemarre(), getDirecteur(), getErrata())) {
            htmlLabel =
                Stream
                    .of(getErrata(), getDirecteur(), getRedemarre())
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return htmlLabel;
    }

    public String getQuestionLabelIhm() {
        return (getSourceNumeroQuestion()).trim();
    }

    public List<String> getEtat() {
        List<ImmutablePair<String, Boolean>> etats = ImmutableList.of(
            ImmutablePair.of("renouvellé", isRenouvelle()),
            ImmutablePair.of("signalé", isSignale()),
            ImmutablePair.of("rappelé", isUrgent())
        );
        return etats
            .stream()
            .filter(pair -> pair.getValue() != null && pair.getValue())
            .map(ImmutablePair::getLeft)
            .collect(Collectors.toList());
    }

    public Date getDateEffetRenouvellement() {
        return getDate(ReponseDossierListingConstants.DATE_EFFET_RENOUVELLEMENT);
    }

    public void setDateEffetRenouvellement(Date dategetDateEffetRenouvellement) {
        put(ReponseDossierListingConstants.DATE_EFFET_RENOUVELLEMENT, dategetDateEffetRenouvellement);
    }

    public Date getDateRappel() {
        return getDate(ReponseDossierListingConstants.DATE_RAPPEL);
    }

    public void setDateRappel(Date dateRappel) {
        put(ReponseDossierListingConstants.DATE_RAPPEL, dateRappel);
    }

    public String getQERappel() {
        return getString(ReponseDossierListingConstants.QE_RAPPEL);
    }

    public void setQERappel(String qERappel) {
        put(ReponseDossierListingConstants.QE_RAPPEL, qERappel);
    }

    public String getLockOwner() {
        return getString(ReponseDossierListingConstants.LOCK_OWNER);
    }

    public void setLockOwner(String lockOwner) {
        put(ReponseDossierListingConstants.LOCK_OWNER, lockOwner);
    }
}
