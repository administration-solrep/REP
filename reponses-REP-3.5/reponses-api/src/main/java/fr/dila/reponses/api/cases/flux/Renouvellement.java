package fr.dila.reponses.api.cases.flux;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface Renouvellement {

     Date getDateEffet();

     void setDateEffet(Date dateEffet);
    
     Map<String, Serializable> getRenouvellementMap();

     void setRenouvellementMap(Map<String, Serializable> renouvellementMap);

}
