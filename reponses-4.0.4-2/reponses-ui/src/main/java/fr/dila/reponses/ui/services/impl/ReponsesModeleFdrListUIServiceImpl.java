package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.ui.bean.fdr.ReponsesModeleFDRList;
import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.ss.api.criteria.SubstitutionCriteria;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.impl.SSModeleFdrListUIServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collections;

public class ReponsesModeleFdrListUIServiceImpl
    extends SSModeleFdrListUIServiceImpl
    implements ReponsesModeleFdrListUIService {

    @Override
    public ModeleFDRList getModelesFDRSubstitution(SpecificContext context) {
        Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
        SubstitutionCriteria criteria = new SubstitutionCriteria(
            Collections.singletonList(dossier.getIdMinistereAttributaireCourant())
        );
        context.putInContextData(SSContextDataKey.SUBSTITUTION_CRITERIA, criteria);
        ReponsesModeleFDRList modeleFDRList = new ReponsesModeleFDRList();
        return super.getModelesFDRSubstitution(modeleFDRList, context);
    }
}
