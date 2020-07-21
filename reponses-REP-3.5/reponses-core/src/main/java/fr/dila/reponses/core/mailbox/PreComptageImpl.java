package fr.dila.reponses.core.mailbox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.mailbox.PreComptage;

public class PreComptageImpl implements PreComptage {

    private Map<String, Serializable> precomptageMap;
    
    public PreComptageImpl(Map<String, Serializable> precomptageMap){
        setPrecomptageMap(precomptageMap);
    }
    
    public PreComptageImpl(String precomptageStr){
        final String[] parts = precomptageStr.split(":");
        final String ministereId = parts[0];
        final String routingTaskType = parts[1];
        final Long count = Long.valueOf(parts[2]);
        precomptageMap = new HashMap<String, Serializable>();
        setMinistereId(ministereId);
        setCount(count);
        setRoutingTaskType(routingTaskType);    
    }
    
    public PreComptageImpl(String ministereId, String routingTaskType, Long count){
        precomptageMap = new HashMap<String, Serializable>();
        setMinistereId(ministereId);
        setCount(count);
        setRoutingTaskType(routingTaskType);
    }
    
    public String getAsStr(){
        StringBuffer sb = new StringBuffer();
        sb.append(getMinistereId())
            .append(':')
            .append(getRoutingTaskType())
            .append(':')
            .append(getCount());
        return sb.toString();
    }
    
    @Override
    public String getMinistereId() {
        return (String)precomptageMap.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_MINISTERE_PROPERTY);
    }

    @Override
    public String getRoutingTaskType() {
        return (String)precomptageMap.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_ROUTINGTASK_PROPERTY);
    }

    @Override
    public Long getCount() {
        return (Long)precomptageMap.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_COUNT_PROPERTY);
    }

    @Override
    public Map<String, Serializable> getPrecomptageMap() {       
        return precomptageMap;
    }

    @Override
    public void setPrecomptageMap(Map<String, Serializable> precomptageMap) {
        if(precomptageMap == null){
            throw new IllegalArgumentException("precomptageMap is null");
        }
        this.precomptageMap = precomptageMap;        
    }
    
    @Override
    public Long decrCount(){
        long count = getCount() - 1;
        setCount(count);
        return getCount();
    }
    
    @Override
    public Long incrCount(){
        long count = getCount() + 1;
        setCount(count);
        return getCount();
    }

    
    public void setMinistereId(String ministereId) {
        precomptageMap.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_MINISTERE_PROPERTY, ministereId);
    }
    
    public void setRoutingTaskType(String routingTaskType) {
        precomptageMap.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_ROUTINGTASK_PROPERTY, routingTaskType);
    }
    
    public void setCount(Long count) {
        precomptageMap.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_COUNT_PROPERTY, count);
    }
    
    
    
}
