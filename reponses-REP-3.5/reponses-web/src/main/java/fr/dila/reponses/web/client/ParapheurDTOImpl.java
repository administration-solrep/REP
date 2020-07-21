package fr.dila.reponses.web.client;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

public class ParapheurDTOImpl implements ParapheurDTO, Serializable {

    private static final long serialVersionUID = -5475893778816878563L;

    private Boolean lockCurrentDossier = Boolean.FALSE;
    private Boolean unLockCurrentDossier = Boolean.FALSE;
    private Boolean reponseAtLastVersion = Boolean.FALSE;
    private Boolean userUpdateParapheur = Boolean.FALSE;
    private Boolean reponseSignee = Boolean.FALSE;
    private Boolean reponsePublished = Boolean.FALSE;
    private Boolean reponseHasTexte = Boolean.FALSE;
    private Boolean userMailboxInDossier = Boolean.FALSE;
    private List<DocumentModel> listReponseVersion;
    private Boolean reponseHasErratum;
    private Boolean adminFonctionnel;

    public ParapheurDTOImpl(Boolean lockCurrentDossier, Boolean unLockCurrentDossier, Boolean reponseAtLastVersion, Boolean userUpdateParapheur,
            Boolean reponseSignee, List<DocumentModel> listReponseVersion, Boolean reponsePublished, Boolean reponseHasTexte,
            Boolean reponseHasErratum, Boolean userMailboxInDossier, Boolean adminFonctionnel) {
        this.lockCurrentDossier = lockCurrentDossier;
        this.unLockCurrentDossier = unLockCurrentDossier;
        this.reponseAtLastVersion = reponseAtLastVersion;
        this.userUpdateParapheur = userUpdateParapheur;
        this.reponseSignee = reponseSignee;
        this.listReponseVersion = listReponseVersion;
        this.reponsePublished = reponsePublished;
        this.reponseHasTexte = reponseHasTexte;
        this.reponseHasErratum = reponseHasErratum;
        this.userMailboxInDossier = userMailboxInDossier;
        this.adminFonctionnel = adminFonctionnel;
    }

    @Override
    public Boolean isAdminFonctionnel() {
        return adminFonctionnel;
    }

    @Override
    public Boolean canLockCurrentDossier() {
        return lockCurrentDossier;
    }

    @Override
    public Boolean canUnLockCurrentDossier() {
        return unLockCurrentDossier;
    }

    @Override
    public Boolean isReponseAtLastVersion() {
        return reponseAtLastVersion;
    }

    @Override
    public Boolean canUserUpdateParapheur() {
        return userUpdateParapheur;
    }

    @Override
    public Boolean isReponseSignee() {
        return reponseSignee;
    }

    @Override
    public Boolean isUserMailboxInDossier() {
        return userMailboxInDossier;
    }

    @Override
    public List<DocumentModel> getListReponseVersion() {
        return listReponseVersion;
    }

    @Override
    public Boolean isReponsePublished() {
        return reponsePublished;
    }

    @Override
    public Boolean reponseHasTexte() {
        return reponseHasTexte;
    }

    @Override
    public Boolean reponseHasErratum() {
        return reponseHasErratum;
    }

    /* methode combinant les méthodes ci-dessus pour calculer des test fait dans le xhtml */
    @Override
    public Boolean cannotUnlockCurrentDossier() {
        return !canUnLockCurrentDossier();

    }

    @Override
    public Boolean hasOneNotPublishedReponse() {
        return getListReponseVersion().size() == 1 && !isReponsePublished();
    }

    @Override
    public Boolean canDisplayEditReponseButton() {
        return canLockCurrentDossier() && isReponseAtLastVersion() && canUserUpdateParapheur();
    }

    @Override
    public Boolean canDisplaySaveReponseButton() {
        return canUnLockCurrentDossier() && isReponseAtLastVersion() && canUserUpdateParapheur();
    }

    @Override
    public Boolean canDisplayBriserReponseButton() {
        return canUnLockCurrentDossier() && isReponseAtLastVersion() && canUserUpdateParapheur() && isReponseSignee();
    }

    @Override
    public Boolean canDisplayErratum() {
        return canUnLockCurrentDossier() && isReponseAtLastVersion() && canUserUpdateParapheur() && !isReponseSignee();
    }

    @Override
    public Boolean canDisplaySaveCancelButton() {
        return canUnLockCurrentDossier() && isReponseAtLastVersion() && canUserUpdateParapheur() && !isReponseSignee();
    }

    @Override
    public Boolean canDisplayReponse() {
        return isAdminFonctionnel() || canUserUpdateParapheur() || isUserMailboxInDossier() || isReponsePublished();
    }
}
