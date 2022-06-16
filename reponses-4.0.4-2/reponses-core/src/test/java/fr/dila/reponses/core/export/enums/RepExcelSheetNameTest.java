package fr.dila.reponses.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class RepExcelSheetNameTest extends AbstractTestSortableEnum<RepExcelSheetName> {

    @Override
    protected Class<RepExcelSheetName> getEnumClass() {
        return RepExcelSheetName.class;
    }

    @Test
    public void getLabel() {
        assertThat(RepExcelSheetName.DOSSIER.getLabel()).isEqualTo("export.dossier.sheet.name");
        assertThat(RepExcelSheetName.RECHERCHE_DOSSIER.getLabel()).isEqualTo("export.dossier.sheet.name");
    }

    @Test
    public void getHeaders() {
        assertThat(RepExcelSheetName.DOSSIER.getHeaders())
            .containsExactly(
                RepExcelHeader.ORIGINE,
                RepExcelHeader.NUMERO_QUESTION,
                RepExcelHeader.NATURE,
                RepExcelHeader.DATE_PUBLICATION_JO,
                RepExcelHeader.AUTEUR,
                RepExcelHeader.MINISTERE_ATTRIBUTAIRE,
                RepExcelHeader.MOTS_CLES,
                RepExcelHeader.ETAT
            );
        assertThat(RepExcelSheetName.RECHERCHE_DOSSIER.getHeaders())
            .containsExactly(
                RepExcelHeader.ORIGINE,
                RepExcelHeader.NUMERO_QUESTION,
                RepExcelHeader.NATURE,
                RepExcelHeader.DATE_PUBLICATION_JO,
                RepExcelHeader.AUTEUR,
                RepExcelHeader.MINISTERE_ATTRIBUTAIRE,
                RepExcelHeader.DIRECTION_ETAPES_EN_COURS,
                RepExcelHeader.MOTS_CLES,
                RepExcelHeader.DELAI,
                RepExcelHeader.ETAT,
                RepExcelHeader.ETAPE_EN_COURS
            );
    }

    @Test
    public void getHeadersSize() {
        assertThat(RepExcelSheetName.DOSSIER.getHeadersSize()).isEqualTo(8);
        assertThat(RepExcelSheetName.RECHERCHE_DOSSIER.getHeadersSize()).isEqualTo(11);
    }

    @Test
    public void getHeadersLabels() {
        assertThat(RepExcelSheetName.DOSSIER.getHeadersLabels())
            .containsExactly(
                "export.header.origine",
                "export.header.numero.question",
                "export.header.nature",
                "export.header.date.publication.jo",
                "export.header.auteur",
                "export.header.ministere.attributaire",
                "export.header.mots.cles",
                "export.header.etat"
            );
    }

    @Test
    public void getHeadersLabelKeys() {
        assertThat(RepExcelSheetName.DOSSIER.getHeadersLabelKeys())
            .containsExactly(
                "export.header.origine",
                "export.header.numero.question",
                "export.header.nature",
                "export.header.date.publication.jo",
                "export.header.auteur",
                "export.header.ministere.attributaire",
                "export.header.mots.cles",
                "export.header.etat"
            );
    }
}
