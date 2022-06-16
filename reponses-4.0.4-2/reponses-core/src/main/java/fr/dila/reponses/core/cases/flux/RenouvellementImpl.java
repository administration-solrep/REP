package fr.dila.reponses.core.cases.flux;

import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.constant.DossierConstants;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RenouvellementImpl implements Renouvellement {
    protected Date dateEffet;

    protected Map<String, Serializable> renouvellementMap;

    public RenouvellementImpl() {
        renouvellementMap = new HashMap<String, Serializable>();
    }

    public RenouvellementImpl(Map<String, Serializable> renouvellement) {
        if (renouvellement.get(DossierConstants.DOSSIER_RENOUVELLEMENT_DATEDEFFET) instanceof GregorianCalendar) {
            this.dateEffet =
                ((GregorianCalendar) renouvellement.get(DossierConstants.DOSSIER_RENOUVELLEMENT_DATEDEFFET)).getTime();
            renouvellementMap = new HashMap<String, Serializable>();
            renouvellementMap.put(DossierConstants.DOSSIER_RENOUVELLEMENT_DATEDEFFET, dateEffet);
        }
    }

    @Override
    public Date getDateEffet() {
        return dateEffet;
    }

    @Override
    public void setDateEffet(Date dateEffet) {
        this.dateEffet = dateEffet;
        renouvellementMap.put(DossierConstants.DOSSIER_RENOUVELLEMENT_DATEDEFFET, dateEffet);
    }

    @Override
    public Map<String, Serializable> getRenouvellementMap() {
        return renouvellementMap;
    }

    @Override
    public void setRenouvellementMap(Map<String, Serializable> renouvellementMap) {
        Serializable date = renouvellementMap.get(DossierConstants.DOSSIER_RENOUVELLEMENT_DATEDEFFET);
        this.renouvellementMap = renouvellementMap;
        setDateEffet(((GregorianCalendar) date).getTime());
    }
}
