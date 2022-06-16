package fr.dila.reponses.ui.bean;

import static org.junit.Assert.*;

import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import org.junit.Test;

public class RechercheDTOTest {

    @Test
    public void testConstructor() {
        RechercheDTO dto = new RechercheDTO();
        assertNull(dto.getInitValueRechercheDto());
        assertNull(dto.getRechercheGeneraleForm());
        assertNull(dto.getBlocDatesForm());
        assertNull(dto.getTexteIntegralForm());
        assertNull(dto.getFeuilleRouteForm());
        assertNull(dto.getMotsClesForm());
    }

    @Test
    public void testSetter() {
        RechercheDTO dto = new RechercheDTO();

        InitValueRechercheDTO initValueRechercheDTO = new InitValueRechercheDTO();
        RechercheGeneraleForm rechercheGeneraleForm = new RechercheGeneraleForm();
        BlocDatesForm blocDatesForm = new BlocDatesForm();
        TexteIntegralForm texteIntegralForm = new TexteIntegralForm();
        FeuilleRouteForm feuilleRouteForm = new FeuilleRouteForm();
        MotsClesForm motsClesForm = new MotsClesForm();

        dto.setInitValueRechercheDto(initValueRechercheDTO);
        assertNotNull(dto.getInitValueRechercheDto());
        dto.setRechercheGeneraleForm(rechercheGeneraleForm);
        assertNotNull(dto.getRechercheGeneraleForm());
        dto.setBlocDatesForm(blocDatesForm);
        assertNotNull(dto.getBlocDatesForm());
        dto.setTexteIntegralForm(texteIntegralForm);
        assertNotNull(dto.getTexteIntegralForm());
        dto.setFeuilleRouteForm(feuilleRouteForm);
        assertNotNull(dto.getFeuilleRouteForm());
        dto.setMotsClesForm(motsClesForm);
        assertNotNull(dto.getMotsClesForm());
    }
}
