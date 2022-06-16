package fr.dila.reponses.api.cases.flux;

import java.io.Serializable;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface QuestionStateChange extends Serializable {
    Calendar getChangeDate();

    void setChangeDate(Calendar changeDate);

    String getNewState();

    void setNewState(String texteErratum);

    DocumentModel getDocument();
}
