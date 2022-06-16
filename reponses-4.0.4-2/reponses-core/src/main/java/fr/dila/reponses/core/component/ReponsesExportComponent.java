package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.service.ReponsesExportServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesExportComponent
    extends ServiceEncapsulateComponent<ReponsesExportService, ReponsesExportServiceImpl> {

    public ReponsesExportComponent() {
        super(ReponsesExportService.class, new ReponsesExportServiceImpl());
    }
}
