package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.StatutEtapeRechercheService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class StatutEtapeRechercheComponent
    extends ServiceEncapsulateComponent<StatutEtapeRechercheService, StatutEtapeRechercheServiceImpl> {

    public StatutEtapeRechercheComponent() {
        super(StatutEtapeRechercheService.class, new StatutEtapeRechercheServiceImpl());
    }
}
