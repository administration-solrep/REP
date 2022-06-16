package fr.dila.reponses.api.enumeration;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.st.api.constant.STEventConstant;

public enum WeeklyBatchEnum {
    BATCH_EVENT_EXTRACTION_QUESTIONS(ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT),
    BATCH_EVENT_PURGE_TENTATIVES_CONNEXION(STEventConstant.BATCH_EVENT_PURGE_TENTATIVES_CONNEXION);

    private String eventName;

    WeeklyBatchEnum(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }
}
