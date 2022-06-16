package fr.dila.reponses.ui.enums;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DELEGATION_CONSULTATION_ACTIONS;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class ReponsesActionCategoryTest extends AbstractTestSortableEnum<ReponsesActionCategory> {

    @Override
    protected Class<ReponsesActionCategory> getEnumClass() {
        return ReponsesActionCategory.class;
    }

    @Test
    public void getName() {
        assertThat(DELEGATION_CONSULTATION_ACTIONS.getName()).isEqualTo("DELEGATION_CONSULTATION_ACTIONS");
    }
}
