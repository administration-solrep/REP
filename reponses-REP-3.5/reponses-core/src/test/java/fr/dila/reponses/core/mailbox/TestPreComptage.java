package fr.dila.reponses.core.mailbox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.mailbox.PreComptage;


public class TestPreComptage extends TestCase {
    
    public void testAccessor(){
        final String ministereId = "min_id";
        final String routingTaskType = "rttype";
        final Long count = 5L;
        
        final String ministereId2 = "min_id2";
        final String routingTaskType2 = "rttype2";
        final Long count2 = 10L;
        
        PreComptage p = new PreComptageImpl(ministereId, routingTaskType, count);
        
        assertEquals(ministereId, p.getMinistereId());
        assertEquals(routingTaskType, p.getRoutingTaskType());
        assertEquals(count, p.getCount());
        
        // verification du contenu de la map
        Map<String, Serializable> map = p.getPrecomptageMap();
        assertEquals(ministereId, map.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_MINISTERE_PROPERTY));
        assertEquals(routingTaskType, map.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_ROUTINGTASK_PROPERTY));
        assertEquals(count, map.get(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_COUNT_PROPERTY));
        
        
        //initialisation à partir d'une map
        Map<String, Serializable> map2 = new HashMap<String, Serializable>();
        map2.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_MINISTERE_PROPERTY, ministereId2);
        map2.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_ROUTINGTASK_PROPERTY, routingTaskType2);
        map2.put(ReponsesConstant.REPONSES_MAILBOX_PRECOMPTAGE_COUNT_PROPERTY, count2);
        
        p.setPrecomptageMap(map2);
        assertEquals(ministereId2, p.getMinistereId());
        assertEquals(routingTaskType2, p.getRoutingTaskType());
        assertEquals(count2, p.getCount());
        
        // construction à partir d'un map
        p = new PreComptageImpl(map2);
        assertEquals(ministereId2, p.getMinistereId());
        assertEquals(routingTaskType2, p.getRoutingTaskType());
        assertEquals(count2, p.getCount());
    }
    
    public void testIncr(){
        final String ministereId = "min_id";
        final String routingTaskType = "rttype";
        final Long count = 2L;
        
        PreComptage p = new PreComptageImpl(ministereId, routingTaskType, count);
                
        final Long nextCount = count + 1;
        Long newCount = p.incrCount();
        assertEquals(nextCount, newCount);
        assertEquals(nextCount, p.getCount());
        
        Long prevCount = count;
        newCount = p.decrCount();
        assertEquals(prevCount, newCount);
        assertEquals(prevCount, p.getCount());
        
        for(int i = 0; i < 10; ++i){
            prevCount = prevCount - 1;
            newCount = p.decrCount();
            assertEquals(prevCount, newCount);
            assertEquals(prevCount, p.getCount());
        }
    }
    
    public void testDecr(){
        final String ministereId = "min_id";
        final String routingTaskType = "rttype";
        final Long count = 5L;
        
        PreComptage p = new PreComptageImpl(ministereId, routingTaskType, count);
        
        Long prevCount = count - 1;
        Long newCount = p.decrCount();
        assertEquals(prevCount, newCount);
        assertEquals(prevCount, p.getCount());
        
        for(int i = 0; i < 10; ++i){
            prevCount = prevCount - 1;
            newCount = p.decrCount();
            assertEquals(prevCount, newCount);
            assertEquals(prevCount, p.getCount());
        }
        
    }

}
