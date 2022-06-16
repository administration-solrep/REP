package fr.dila.reponses.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class RepExcelHeaderTest extends AbstractTestSortableEnum<RepExcelHeader> {

    @Override
    protected Class<RepExcelHeader> getEnumClass() {
        return RepExcelHeader.class;
    }

    @Test
    public void getSpecificLabelKey() {
        assertThat(RepExcelHeader.DATE_PUBLICATION_JO.getSpecificLabelKey()).isEqualTo("date.publication.jo");
        assertThat(RepExcelHeader.ETAT.getSpecificLabelKey()).isNull();
    }

    @Test
    public void getLabelKey() {
        assertThat(RepExcelHeader.DATE_PUBLICATION_JO.getLabelKey()).isEqualTo("export.header.date.publication.jo");
        assertThat(RepExcelHeader.ETAT.getLabelKey()).isEqualTo("export.header.etat");
    }

    @Test
    public void getLabel() {
        assertThat(RepExcelHeader.DATE_PUBLICATION_JO.getLabel()).isEqualTo("export.header.date.publication.jo");
        assertThat(RepExcelHeader.ETAT.getLabel()).isEqualTo("export.header.etat");
    }
}
