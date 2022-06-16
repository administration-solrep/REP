package fr.dila.reponses.ui.enums;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_DISTRIBUTION_ACTIONS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.ROUTING_ACTIONS;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.reponses.ui.bean.actions.DossierDistributionActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesRoutingActionDTO;
import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class ReponsesContextDataKeyTest extends AbstractTestSortableEnum<ReponsesContextDataKey> {

    @Override
    protected Class<ReponsesContextDataKey> getEnumClass() {
        return ReponsesContextDataKey.class;
    }

    @Test
    public void getName() {
        assertThat(DOSSIER.getName()).isEqualTo("DOSSIER");
        assertThat(DOSSIER_DISTRIBUTION_ACTIONS.getName()).isEqualTo("dossierDistributionActions");
        assertThat(ROUTING_ACTIONS.getName()).isEqualTo("routingActions");
    }

    @Test
    public void getTypeValue() {
        assertThat(DOSSIER_DISTRIBUTION_ACTIONS.getValueType()).isEqualTo(DossierDistributionActionDTO.class);
        assertThat(ROUTING_ACTIONS.getValueType()).isEqualTo(ReponsesRoutingActionDTO.class);
    }
}
