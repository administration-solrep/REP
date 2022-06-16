package fr.dila.reponses.api.logging;

import fr.dila.reponses.api.constant.ReponsesConstant;

public enum ReponsesLoggingStatusEnum {
    SUCCESS("reponses.logging.success", ReponsesConstant.TERMINEE_STATUS),
    FAILURE("reponses.logging.failure", ReponsesConstant.FAILED_STATUS);

    private final String message;
    private final String etat;

    ReponsesLoggingStatusEnum(String message, String etat) {
        this.message = message;
        this.etat = etat;
    }

    public String getMessage() {
        return message;
    }

    public String getEtat() {
        return etat;
    }

    public static ReponsesLoggingStatusEnum findById(String status) {
        for (ReponsesLoggingStatusEnum reponsesLoggingLineStatus : ReponsesLoggingStatusEnum.values()) {
            if (reponsesLoggingLineStatus.name().equals(status)) {
                return reponsesLoggingLineStatus;
            }
        }

        return null;
    }
}
