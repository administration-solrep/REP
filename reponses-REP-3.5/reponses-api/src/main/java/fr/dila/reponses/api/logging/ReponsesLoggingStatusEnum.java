package fr.dila.reponses.api.logging;

public enum ReponsesLoggingStatusEnum {

    SUCCESS("reponses.logging.success"), FAILURE("reponses.logging.failure");

    private String message;

    ReponsesLoggingStatusEnum(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
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
