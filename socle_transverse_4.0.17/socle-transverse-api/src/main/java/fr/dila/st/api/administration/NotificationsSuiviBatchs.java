package fr.dila.st.api.administration;

import fr.dila.st.api.domain.STDomainObject;
import java.util.List;

/**
 * Interface Adapter Document Notifications Suivi Batchs
 *
 * @author JBT
 *
 */
public interface NotificationsSuiviBatchs extends STDomainObject {
    boolean getEtatNotification();

    void setEtatNotification(boolean etatNotification);

    List<String> getReceiverMailList();

    void setReceiverMailList(List<String> receiverMailList);
}
