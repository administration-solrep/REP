package fr.dila.reponses.ui.services.impl;

import fr.dila.st.ui.services.impl.HeaderServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public class ReponsesHeaderServiceImpl extends HeaderServiceImpl {
    public static final String NUM_QUESTION_KEY = "searchNumQuestion";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> map = super.getData(context);
        map.put(NUM_QUESTION_KEY, context.getContextData().get(NUM_QUESTION_KEY));
        return map;
    }
}
