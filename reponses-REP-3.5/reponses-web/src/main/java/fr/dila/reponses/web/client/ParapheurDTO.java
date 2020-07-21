package fr.dila.reponses.web.client;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface ParapheurDTO {

    Boolean canLockCurrentDossier();

    Boolean canUnLockCurrentDossier();

    Boolean isReponseAtLastVersion();

    Boolean canUserUpdateParapheur();

    Boolean isReponseSignee();

    Boolean isReponsePublished();

    List<DocumentModel> getListReponseVersion();

    Boolean reponseHasTexte();

    Boolean reponseHasErratum();

    Boolean isAdminFonctionnel();

    Boolean isUserMailboxInDossier();

    /* methode combinant les méthodes ci-dessus pour calculer des test fait dans le xhtml */

    Boolean cannotUnlockCurrentDossier();

    Boolean hasOneNotPublishedReponse();

    Boolean canDisplayEditReponseButton();

    Boolean canDisplaySaveReponseButton();

    Boolean canDisplayBriserReponseButton();

    Boolean canDisplayErratum();

    Boolean canDisplaySaveCancelButton();

    Boolean canDisplayReponse();

}
