package fr.dila.reponses.ui.services.impl;

import static fr.dila.st.api.constant.STEventConstant.CATEGORY_ADMINISTRATION;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_BORDEREAU;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FDD;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FEUILLE_ROUTE;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_PARAPHEUR;

import com.google.common.collect.ImmutableSet;
import fr.dila.ss.ui.services.impl.SSJournalUIServiceImpl;
import java.util.Set;

public class ReponsesJournalUIServiceImpl extends SSJournalUIServiceImpl {

    @Override
    public Set<String> getCategoryList() {
        return ImmutableSet.of(
            CATEGORY_FEUILLE_ROUTE,
            CATEGORY_BORDEREAU,
            CATEGORY_FDD,
            CATEGORY_PARAPHEUR,
            CATEGORY_ADMINISTRATION
        );
    }
}
