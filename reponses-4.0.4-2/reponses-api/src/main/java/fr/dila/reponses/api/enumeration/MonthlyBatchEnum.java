package fr.dila.reponses.api.enumeration;

import fr.dila.reponses.api.constant.ReponsesEventConstant;

public enum MonthlyBatchEnum {
    BATCH_EVENT_EXTRACTION_QUESTIONS(ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT);

    private String eventName;

    MonthlyBatchEnum(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }
}
