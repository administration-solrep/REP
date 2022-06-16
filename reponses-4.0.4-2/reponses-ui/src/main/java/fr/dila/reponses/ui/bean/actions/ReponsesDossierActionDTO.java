package fr.dila.reponses.ui.bean.actions;

public class ReponsesDossierActionDTO {
    private boolean isDossierContainsMinistere;

    private boolean canReadDossierConnexe;

    private boolean canReadAllotissement;

    private boolean canUpdateAllotissement;

    private boolean isCurrentDossierInUserMinistere;

    private boolean isDossierArbitrated;

    private boolean canUserUpdateFondDossier;

    private boolean canUserBriserReponse;

    private boolean isFeuilleRouteRestartable;

    private String messageActionRedemarrerQuestion;

    public ReponsesDossierActionDTO() {
        // Default constructor
    }

    public boolean getIsDossierContainsMinistere() {
        return isDossierContainsMinistere;
    }

    public void setIsDossierContainsMinistere(boolean isDossierContainsMinistere) {
        this.isDossierContainsMinistere = isDossierContainsMinistere;
    }

    public boolean getCanReadDossierConnexe() {
        return canReadDossierConnexe;
    }

    public void setCanReadDossierConnexe(boolean canReadDossierConnexe) {
        this.canReadDossierConnexe = canReadDossierConnexe;
    }

    public boolean getCanReadAllotissement() {
        return canReadAllotissement;
    }

    public void setCanReadAllotissement(boolean canReadAllotissement) {
        this.canReadAllotissement = canReadAllotissement;
    }

    public boolean getIsCurrentDossierInUserMinistere() {
        return isCurrentDossierInUserMinistere;
    }

    public void setIsCurrentDossierInUserMinistere(boolean isCurrentDossierInUserMinistere) {
        this.isCurrentDossierInUserMinistere = isCurrentDossierInUserMinistere;
    }

    public boolean getIsDossierArbitrated() {
        return isDossierArbitrated;
    }

    public void setIsDossierArbitrated(boolean isDossierArbitrated) {
        this.isDossierArbitrated = isDossierArbitrated;
    }

    public boolean getCanUserUpdateFondDossier() {
        return canUserUpdateFondDossier;
    }

    public void setCanUserUpdateFondDossier(boolean canUserUpdateFondDossier) {
        this.canUserUpdateFondDossier = canUserUpdateFondDossier;
    }

    public boolean isCanUpdateAllotissement() {
        return canUpdateAllotissement;
    }

    public void setCanUpdateAllotissement(boolean canUpdateAllotissement) {
        this.canUpdateAllotissement = canUpdateAllotissement;
    }

    public boolean getIsFeuilleRouteRestartable() {
        return isFeuilleRouteRestartable;
    }

    public void setIsFeuilleRouteRestartable(boolean isFeuilleRouteRestartable) {
        this.isFeuilleRouteRestartable = isFeuilleRouteRestartable;
    }

    public String getMessageActionRedemarrerQuestion() {
        return messageActionRedemarrerQuestion;
    }

    public void setMessageActionRedemarrerQuestion(String messageActionRedemarrerQuestion) {
        this.messageActionRedemarrerQuestion = messageActionRedemarrerQuestion;
    }

    public boolean getCanUserBriserReponse() {
        return canUserBriserReponse;
    }

    public void setCanUserBriserReponse(boolean canUserBriserReponse) {
        this.canUserBriserReponse = canUserBriserReponse;
    }
}
