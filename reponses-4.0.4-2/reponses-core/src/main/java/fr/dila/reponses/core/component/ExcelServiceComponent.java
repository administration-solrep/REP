package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ExcelService;
import fr.dila.reponses.core.service.ExcelServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ExcelServiceComponent extends ServiceEncapsulateComponent<ExcelService, ExcelServiceImpl> {

    public ExcelServiceComponent() {
        super(ExcelService.class, new ExcelServiceImpl());
    }
}
