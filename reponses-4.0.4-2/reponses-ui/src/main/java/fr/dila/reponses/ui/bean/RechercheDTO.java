package fr.dila.reponses.ui.bean;

import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;

public class RechercheDTO {
    private RechercheGeneraleForm rechercheGeneraleForm;

    private BlocDatesForm blocDatesForm;

    private FeuilleRouteForm feuilleRouteForm;

    private TexteIntegralForm texteIntegralForm;

    private MotsClesForm motsClesForm;

    private InitValueRechercheDTO initValueRechercheDto;

    public RechercheDTO() {}

    public RechercheGeneraleForm getRechercheGeneraleForm() {
        return rechercheGeneraleForm;
    }

    public void setRechercheGeneraleForm(RechercheGeneraleForm rechercheGeneraleForm) {
        this.rechercheGeneraleForm = rechercheGeneraleForm;
    }

    public BlocDatesForm getBlocDatesForm() {
        return blocDatesForm;
    }

    public void setBlocDatesForm(BlocDatesForm blocDatesForm) {
        this.blocDatesForm = blocDatesForm;
    }

    public FeuilleRouteForm getFeuilleRouteForm() {
        return feuilleRouteForm;
    }

    public void setFeuilleRouteForm(FeuilleRouteForm feuilleRouteForm) {
        this.feuilleRouteForm = feuilleRouteForm;
    }

    public TexteIntegralForm getTexteIntegralForm() {
        return texteIntegralForm;
    }

    public void setTexteIntegralForm(TexteIntegralForm texteIntegralForm) {
        this.texteIntegralForm = texteIntegralForm;
    }

    public MotsClesForm getMotsClesForm() {
        return motsClesForm;
    }

    public void setMotsClesForm(MotsClesForm motsClesForm) {
        this.motsClesForm = motsClesForm;
    }

    public InitValueRechercheDTO getInitValueRechercheDto() {
        return initValueRechercheDto;
    }

    public void setInitValueRechercheDto(InitValueRechercheDTO initValueRechercheDto) {
        this.initValueRechercheDto = initValueRechercheDto;
    }
}
