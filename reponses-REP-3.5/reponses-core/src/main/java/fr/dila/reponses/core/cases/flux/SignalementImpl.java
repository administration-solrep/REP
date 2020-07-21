package fr.dila.reponses.core.cases.flux;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;

public class SignalementImpl implements Signalement {

    protected Date dateEffet;

    protected Date dateAttendue;

    protected Map<String, Serializable> signalementMap;

    public SignalementImpl() {
	signalementMap = new HashMap<String, Serializable>();
    }

    public SignalementImpl(Map<String, Serializable> signalement) {
        setSignalementMap(signalement);
    }

    @Override
    public Date getDateEffet() {
	return dateEffet;
    }

    @Override
    public void setDateEffet(Date dateEffet) {
	this.dateEffet = dateEffet;
	this.signalementMap.put(
		DossierConstants.DOSSIER_SIGNALEMENT_DATEDEFFET, dateEffet);
    }

    @Override
    public Date getDateAttendue() {
	return dateAttendue;
    }

    @Override
    public void setDateAttendue(Date dateAttendue) {
	this.dateAttendue = dateAttendue;
	this.signalementMap
		.put(DossierConstants.DOSSIER_SIGNALEMENT_DATEATTENDUE,
			dateAttendue);
    }

    @Override
    public Map<String, Serializable> getSignalementMap() {
	return signalementMap;
    }

    @Override
    public void setSignalementMap(Map<String, Serializable> signalementMap) {
        this.signalementMap = signalementMap;
        
        GregorianCalendar gc1= ((GregorianCalendar) signalementMap
                .get(DossierConstants.DOSSIER_SIGNALEMENT_DATEDEFFET));
        
        this.dateEffet =(gc1!=null)?gc1.getTime():null; 

        
        GregorianCalendar gc2 = ((GregorianCalendar) signalementMap
                .get(DossierConstants.DOSSIER_SIGNALEMENT_DATEATTENDUE));
        
        this.dateAttendue = (gc2!=null)?gc2.getTime():null;

    }

}
