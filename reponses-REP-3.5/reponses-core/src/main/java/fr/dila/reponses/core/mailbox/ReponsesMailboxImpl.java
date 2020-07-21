package fr.dila.reponses.core.mailbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.mailbox.MailboxImpl;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.mailbox.PreComptage;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation des Mailbox de l'application Réponses.
 * 
 * @author sly
 */
public class ReponsesMailboxImpl extends MailboxImpl implements ReponsesMailbox {

    private static final long serialVersionUID = 8820645351060312648L;
    
    /**
     * Constructeur de ReponsesMailboxImpl.
     * 
     * @param doc Modèle de document
     */
    public ReponsesMailboxImpl(DocumentModel doc) {
        super(doc);
    }

    /////////////////////////////////////////
    // ReponsesMailbox properties
    /////////////////////////////////////////
    @Override
    public List<PreComptage> getPreComptages() {
        List<String> pre =  PropertyUtil.getStringListProperty(doc, ReponsesConstant.REPONSES_MAILBOX_SCHEMA, "preCalculList");
        List<PreComptage> precalculs = new ArrayList<PreComptage>();
        for(String str : pre){
            precalculs.add(new PreComptageImpl(str));
        }
        return precalculs;
    }
    
    @Override
    public Map<String, List<PreComptage>> getPreComptagesGroupByMinistereId(){
        List<PreComptage> precomptages = getPreComptages();
        Map<String, List<PreComptage>> mapprecomptage = new HashMap<String, List<PreComptage>>();
        for(PreComptage precomptage : precomptages){
            List<PreComptage> precompmin = mapprecomptage.get(precomptage.getMinistereId());
            if(precompmin == null){
                precompmin = new ArrayList<PreComptage>();
                mapprecomptage.put(precomptage.getMinistereId(), precompmin);
            }
            precompmin.add(precomptage);
        }
        // tri les liste de precalcul par type d'etape
        for(Entry<String, List<PreComptage>> entry : mapprecomptage.entrySet()){
            List<PreComptage> precompmin = entry.getValue();
            Collections.sort(precompmin, new Comparator<PreComptage>() {

                @Override
                public int compare(PreComptage p1, PreComptage p2) {                    
                    return p1.getRoutingTaskType().compareTo(p2.getRoutingTaskType());
                }
                
            });
        }
        return mapprecomptage;
    }
    
    @Override
    public void setPreComptages(List<PreComptage> precomptages){        
        List<String> listPrecomptage = new ArrayList<String>();
        
        for(PreComptage p : precomptages){
            PreComptageImpl pp = (PreComptageImpl)p;
            listPrecomptage.add(pp.getAsStr());
        }
        
        PropertyUtil.setProperty(doc, ReponsesConstant.REPONSES_MAILBOX_SCHEMA, "preCalculList", listPrecomptage);
    }

    @Override
    public void decrPreComptage(String ministereId, String routingTaskType) {
        if(ministereId == null || routingTaskType == null){
            throw new IllegalArgumentException("Argument should not be null [ministereId=" + ministereId + 
                    ",routingTaskType="+routingTaskType+"]");
        }
        List<PreComptage> precomptages = getPreComptages();
        PreComptage matchingElement = null;
        for(PreComptage precomptage : precomptages){
            if(precomptage.getMinistereId().equals(ministereId) && precomptage.getRoutingTaskType().equals(routingTaskType)){
                matchingElement = precomptage;
                break;
            }
        }
        if(matchingElement != null){
            if(matchingElement.decrCount() <= 0){
                precomptages.remove(matchingElement);
            }
            setPreComptages(precomptages);
        }
    }
    
    @Override
    public void incrPreComptage(String ministereId, String routingTaskType){
        List<PreComptage> precomptages = getPreComptages();
        PreComptage matchingElement = null;
        for(PreComptage precomptage : precomptages){
            if(precomptage.getMinistereId().equals(ministereId) && precomptage.getRoutingTaskType().equals(routingTaskType)){
                matchingElement = precomptage;
                break;
            }
        }
        if(matchingElement == null){
            precomptages.add(new PreComptageImpl(ministereId, routingTaskType, 1L));
        } else {
            matchingElement.incrCount();    
        }        
        setPreComptages(precomptages);
    }
        
}
