package fr.dila.reponses.api.logging;

import fr.dila.st.api.domain.STDomainObject;
import java.util.Calendar;
import java.util.List;

/**
 * Interface representant une ligne de log en base
 *
 * @author asatre
 *
 */
public interface ReponsesLoggingLine extends STDomainObject {
    public static final long DASH_COUNT = -999999;

    ReponsesLoggingStatusEnum getStatus();

    void setStatus(ReponsesLoggingStatusEnum status);

    List<String> getReponsesLoggingLineDetails();

    void setReponsesLoggingLineDetails(List<String> reponsesLoggingLines);

    Calendar getStartDate();

    void setStartDate(Calendar startDate);

    Calendar getEndDate();

    void setEndDate(Calendar endDate);

    List<String> getFullLog();

    void setFullLog(List<String> fullLog);

    String getMessage();

    void setMessage(String message);

    Long getPrevisionalCount();

    void setPrevisionalCount(Long previsionalCount);

    Long getEndCount();

    void setEndCount(Long endCount);
}
