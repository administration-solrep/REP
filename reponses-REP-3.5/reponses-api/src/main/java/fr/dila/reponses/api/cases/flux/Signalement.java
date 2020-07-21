/**
 * 
 */
package fr.dila.reponses.api.cases.flux;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author admin
 * 
 */
public interface Signalement {

    Date getDateEffet();

    void setDateEffet(Date dateEffet);

    Date getDateAttendue();

    void setDateAttendue(Date dateAttendue);

    Map<String, Serializable> getSignalementMap();

    void setSignalementMap(Map<String, Serializable> signalementMap);

}
