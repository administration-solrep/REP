package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.TeteAnalyseANService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class TeteAnalyseANComponent
    extends ServiceEncapsulateComponent<TeteAnalyseANService, TeteAnalyseANServiceImpl> {

    public TeteAnalyseANComponent() {
        super(TeteAnalyseANService.class, new TeteAnalyseANServiceImpl());
    }
}
