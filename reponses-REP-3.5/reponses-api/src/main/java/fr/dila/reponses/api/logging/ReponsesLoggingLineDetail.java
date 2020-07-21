package fr.dila.reponses.api.logging;

import java.util.List;

import fr.dila.st.api.domain.STDomainObject;

/**
 * Interface representant une ligne de log en base
 * 
 * @author asatre
 * 
 */
public interface ReponsesLoggingLineDetail extends STDomainObject {

    ReponsesLoggingStatusEnum getStatus();
    void setStatus(ReponsesLoggingStatusEnum status);

    List<String> getFullLog();
    void setFullLog(List<String> fullLog);
    
    String getMessage();
    void setMessage(String message);
}
