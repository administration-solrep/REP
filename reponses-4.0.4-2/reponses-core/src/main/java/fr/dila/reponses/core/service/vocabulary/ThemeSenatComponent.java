package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.ThemeSenatService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ThemeSenatComponent extends ServiceEncapsulateComponent<ThemeSenatService, ThemeSenatServiceImpl> {

    public ThemeSenatComponent() {
        super(ThemeSenatService.class, new ThemeSenatServiceImpl());
    }
}
