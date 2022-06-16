package fr.dila.reponses.api.logging;

import fr.dila.st.api.domain.ComplexeType;
import fr.dila.st.api.domain.STDomainObject;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Interface de gestion des logs en base
 *
 * @author asatre
 *
 */
public interface ReponsesLogging extends STDomainObject {
    List<String> getReponsesLoggingLines();
    void setReponsesLoggingLines(List<String> reponsesLoggingLines);

    Calendar getStartDate();
    void setStartDate(Calendar startDate);

    Calendar getEndDate();
    void setEndDate(Calendar endDate);

    Long getPrevisionalCount();
    void setPrevisionalCount(Long previsionalCount);

    Long getClosePrevisionalCount();
    void setClosePrevisionalCount(Long closePrevisionalCount);

    Long getEndCount();
    void setEndCount(Long endCount);

    Long getCloseEndCount();
    void setCloseEndCount(Long closeEndCount);

    String getMessage();
    void setMessage(String message);

    ReponsesLoggingStatusEnum getStatus();
    void setStatus(ReponsesLoggingStatusEnum status);

    List<ComplexeType> getTimbres();
    void setTimbres(List<ComplexeType> timbre);

    void addtimbre(String currentMin, String nextMin);
    Map<String, String> geTimbresAsMap();

    String getCurrentGouvernement();
    void setCurrentGouvernement(String currentGouvernement);

    String getNextGouvernement();
    void setNextGouvernement(String nextGouvernement);

    boolean isAllCloseQuestionsMigrate();
}
