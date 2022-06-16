package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.ui.services.SearchMenuComponentService;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.Map;

public class SearchMenuComponentImpl extends FragmentServiceImpl implements SearchMenuComponentService {
    public static final String LEFT_MENU_KEY = "searchMenuActions";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put(LEFT_MENU_KEY, context.getActions(STActionCategory.LEFT_SEARCH_MENU));
        return returnMap;
    }
}
