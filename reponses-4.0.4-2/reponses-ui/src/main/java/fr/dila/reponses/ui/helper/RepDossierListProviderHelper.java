package fr.dila.reponses.ui.helper;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getProfilUtilisateurService;

import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public final class RepDossierListProviderHelper {

    public static RechercheResultPageProvider initProvider(
        DossierListForm form,
        String providerName,
        CoreSession session,
        List<Object> lstParams,
        String prefix
    ) {
        RechercheResultPageProvider provider = form.getPageProvider(session, providerName, prefix, lstParams);
        provider.setLstUserVisibleColumns(getProfilUtilisateurService().getUserColumn(session));
        return provider;
    }

    private RepDossierListProviderHelper() {}
}
