package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.jaxrs.webobject.page.dossier.SSDossierFdr;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.List;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierFdr")
public class ReponsesDossierFdr extends SSDossierFdr {

    public ReponsesDossierFdr() {
        super();
    }

    @Override
    public List<SelectValueDTO> getTypeEtapeAjout() {
        return ReponsesUIServiceLocator.getSelectValueUIService().getRoutingTaskTypesFiltered();
    }
}
