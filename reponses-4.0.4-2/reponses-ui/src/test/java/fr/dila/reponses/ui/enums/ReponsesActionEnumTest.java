package fr.dila.reponses.ui.enums;

import static fr.dila.reponses.ui.enums.ReponsesActionEnum.ADMIN_MIGRATION_TIMBRES;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class ReponsesActionEnumTest extends AbstractTestSortableEnum<ReponsesActionEnum> {

    @Override
    protected Class<ReponsesActionEnum> getEnumClass() {
        return ReponsesActionEnum.class;
    }

    @Test
    public void getName() {
        assertThat(ADMIN_MIGRATION_TIMBRES.getName()).isEqualTo("ADMIN_MIGRATION_TIMBRES");
    }
}
