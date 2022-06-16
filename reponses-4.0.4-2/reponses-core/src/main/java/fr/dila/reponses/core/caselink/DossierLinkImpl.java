package fr.dila.reponses.core.caselink;

import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.core.caselink.STDossierLinkImpl;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class DossierLinkImpl extends STDossierLinkImpl implements DossierLink {
    private static final long serialVersionUID = -94563234903621891L;

    private static final int ETAT_QUESTION_URGENT_IDX = 0;
    private static final int ETAT_QUESTION_SIGNALE_IDX = 1;
    private static final int ETAT_QUESTION_RENOUVELLE_IDX = 2;
    private static final int ETAT_QUESTION_NB_ETATS = 3;

    /////////////////////////////////////////
    // cosntructeur DossierLink
    /////////////////////////////////////////

    public DossierLinkImpl(DocumentModel doc) {
        super(doc);
    }

    /////////////////////////////////////////
    // Question property
    /////////////////////////////////////////

    @Override
    public Long getNumeroQuestion() {
        return getLongProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_NUMERO_QUESTION_PROPERTY
        );
    }

    @Override
    public void setNumeroQuestion(Long numeroQuestion) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_NUMERO_QUESTION_PROPERTY,
            numeroQuestion
        );
    }

    @Override
    public String getTypeQuestion() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY
        );
    }

    @Override
    public void setTypeQuestion(String typeQuestion) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY,
            typeQuestion
        );
    }

    @Override
    public String getSourceNumeroQuestion() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY
        );
    }

    @Override
    public void setSourceNumeroQuestion(String sourceNumero) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY,
            sourceNumero
        );
    }

    @Override
    public String getSortField() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_SORT_FIELD_PROPERTY
        );
    }

    @Override
    public void setSortField(String sortField) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_SORT_FIELD_PROPERTY,
            sortField
        );
    }

    @Override
    public Calendar getDatePublicationJO() {
        return getDateProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_PROPERTY
        );
    }

    @Override
    public void setDatePublicationJO(Calendar datePublicationJO) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_PROPERTY,
            datePublicationJO
        );
    }

    @Override
    public String getIdMinistereAttributaire() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY
        );
    }

    @Override
    public void setIdMinistereAttributaire(String idMinistereAttributaire) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY,
            idMinistereAttributaire
        );
    }

    @Override
    public String getIntituleMinistere() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_INTITULE_MINISTERE_QUESTION_PROPERTY
        );
    }

    @Override
    public void setIntituleMinistere(String intituleMinistere) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_INTITULE_MINISTERE_QUESTION_PROPERTY,
            intituleMinistere
        );
    }

    @Override
    public String getNomCompletAuteur() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_PROPERTY
        );
    }

    @Override
    public void setNomCompletAuteur(String nomCompletAuteur) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_PROPERTY,
            nomCompletAuteur
        );
    }

    @Override
    protected String getDossierLinkSchema() {
        return DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA;
    }

    @Override
    public String getEtatsQuestion() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY
        );
    }

    @Override
    public void setEtatsQuestion(String etatsQuestion) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY,
            etatsQuestion
        );
    }

    @Override
    public void setEtatsQuestion(Boolean isUrgent, Boolean isSignale, Boolean isRenouvelle) {
        char c[] = new char[ETAT_QUESTION_NB_ETATS];
        c[ETAT_QUESTION_URGENT_IDX] = isUrgent ? 't' : 'f';
        c[ETAT_QUESTION_SIGNALE_IDX] = isSignale ? 't' : 'f';
        c[ETAT_QUESTION_RENOUVELLE_IDX] = isRenouvelle ? 't' : 'f';
        setEtatQuestionSimple(new String(c));
    }

    @Override
    public Calendar getDateSignalementQuestion() {
        return getDateProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_PROPERTY
        );
    }

    @Override
    public void setDateSignalementQuestion(Calendar dateSignalementQuestion) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_PROPERTY,
            dateSignalementQuestion
        );
    }

    @Override
    public String getMotsCles() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_MOTS_CLES_PROPERTY
        );
    }

    @Override
    public void setMotsCles(String motsCles) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_MOTS_CLES_PROPERTY,
            motsCles
        );
    }

    @Override
    public void setEtatQuestionSimple(String etatQuestion) {
        setProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY,
            etatQuestion
        );
    }

    @Override
    public String getEtatQuestionSimple() {
        return getStringProperty(
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
            DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY
        );
    }

    @Override
    public Boolean isUrgent() {
        String etats = getEtatsQuestion();
        if (etats == null || etats.length() != 3) {
            return false;
        } else {
            return etats.charAt(ETAT_QUESTION_URGENT_IDX) == 't';
        }
    }

    @Override
    public Boolean isSignale() {
        String etats = getEtatsQuestion();
        if (etats == null || etats.length() != 3) {
            return false;
        } else {
            return etats.charAt(ETAT_QUESTION_SIGNALE_IDX) == 't';
        }
    }

    @Override
    public Boolean isRenouvelle() {
        String etats = getEtatsQuestion();
        if (etats == null || etats.length() != 3) {
            return false;
        } else {
            return etats.charAt(ETAT_QUESTION_RENOUVELLE_IDX) == 't';
        }
    }

    @Override
    public Dossier getDossier(CoreSession session) {
        return getDossier(session, Dossier.class);
    }

    @Override
    public List<String> getInitialActionInternalParticipant() {
        return getListStringProperty(
            CaseDistribConstants.DISTRIBUTION_SCHEMA,
            DossierConstants.INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME
        );
    }

    @Override
    public void setInitialActionInternalParticipant(List<String> initialActionInternamParticipant) {
        setProperty(
            CaseDistribConstants.DISTRIBUTION_SCHEMA,
            DossierConstants.INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME,
            initialActionInternamParticipant
        );
    }

    @Override
    public List<String> getAllActionParticipant() {
        return getListStringProperty(
            CaseDistribConstants.DISTRIBUTION_SCHEMA,
            DossierConstants.ALL_ACTION_PARTICIPANTS_PROPERTY_NAME
        );
    }

    @Override
    public void setAllActionParticipant(List<String> allActionParticipant) {
        setProperty(
            CaseDistribConstants.DISTRIBUTION_SCHEMA,
            DossierConstants.ALL_ACTION_PARTICIPANTS_PROPERTY_NAME,
            allActionParticipant
        );
    }
}
