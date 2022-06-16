package fr.dila.reponses.ui.jaxrs.webobject;

import static fr.dila.ss.ui.jaxrs.webobject.ajax.AbstractSSDossierAjax.DOSSIER_AJAX_WEBOBJECT;

import fr.dila.ss.ui.jaxrs.webobject.SSAjax;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliAjax")
public class ReponsesAjax extends SSAjax {

    public ReponsesAjax() {
        super();
    }

    @Path("classement")
    public Object doListeDossiersPlan() {
        return newObject("ReponsesPlanAjax");
    }

    @Path("travail")
    public Object doListeDossiersTravail() {
        return newObject("ReponsesTravailAjax");
    }

    @Path("dossier")
    public Object doDossier() {
        return newObject(DOSSIER_AJAX_WEBOBJECT);
    }

    @Override
    @Path("profile")
    public Object doProfile() {
        return newObject("ProfileAjax");
    }

    @Override
    @Path("fdr")
    public Object doFDR() {
        return newObject("ModeleFeuilleRouteAjax");
    }

    @Path("suivi")
    public Object doSuivi() {
        return newObject("ReponsesSuiviAjax");
    }

    @Path("utilisateurs")
    public Object doUtilisateurs() {
        return newObject("UtilisateursAjax");
    }

    @Path("eliminationDonnees")
    public Object doDonnees() {
        return newObject("EliminationDonneesAjax");
    }

    @Path("majTimbres")
    public Object doMajTimbres() {
        return newObject("MiseAJourTimbresAjax");
    }

    @Path("timbres/historique")
    public Object doHistoriqueTimbres() {
        return newObject("HistoriqueMAJTimbres");
    }

    @Path("timbres/detail")
    public Object doDetailHistoriqueTimbres() {
        return newObject("DetailHistoriqueMAJTimbres");
    }

    @Path("favoris")
    public Object doFavorisTravail() {
        return newObject("FavorisTravailAjax");
    }

    @Path("delegation")
    public Object doDelegations() {
        return newObject("DelegationAjax");
    }

    @Path("allotissement")
    public Object doNote() {
        return newObject("AjaxAllotissement");
    }

    @Path("actionMasse")
    public Object doMassAction() {
        return newObject("AjaxMassAction");
    }
}
