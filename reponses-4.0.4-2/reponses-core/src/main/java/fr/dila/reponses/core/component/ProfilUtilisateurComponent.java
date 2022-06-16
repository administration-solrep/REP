package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.core.service.ProfilUtilisateurServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ProfilUtilisateurComponent
    extends ServiceEncapsulateComponent<ProfilUtilisateurService, ProfilUtilisateurServiceImpl> {

    public ProfilUtilisateurComponent() {
        super(ProfilUtilisateurService.class, new ProfilUtilisateurServiceImpl());
    }
}
