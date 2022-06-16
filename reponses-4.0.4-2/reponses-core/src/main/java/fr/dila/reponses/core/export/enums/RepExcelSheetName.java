package fr.dila.reponses.core.export.enums;

import com.google.common.collect.ImmutableList;
import fr.dila.st.core.export.enums.ExcelHeader;
import fr.dila.st.core.export.enums.ExcelSheetName;
import fr.dila.st.core.util.ResourceHelper;
import java.util.List;
import java.util.Optional;

public enum RepExcelSheetName implements ExcelSheetName {
    DOSSIER(
        ImmutableList.of(
            RepExcelHeader.ORIGINE,
            RepExcelHeader.NUMERO_QUESTION,
            RepExcelHeader.NATURE,
            RepExcelHeader.DATE_PUBLICATION_JO,
            RepExcelHeader.AUTEUR,
            RepExcelHeader.MINISTERE_ATTRIBUTAIRE,
            RepExcelHeader.MOTS_CLES,
            RepExcelHeader.ETAT
        )
    ),
    RECHERCHE_DOSSIER(
        "dossier",
        ImmutableList.of(
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
        )
    );

    private final String labelKey;
    private final List<ExcelHeader> headers;

    RepExcelSheetName(List<ExcelHeader> headers) {
        this(null, headers);
    }

    RepExcelSheetName(String labelKey, List<ExcelHeader> headers) {
        this.labelKey = labelKey;
        this.headers = headers;
    }

    @Override
    public String getLabel() {
        return ResourceHelper.getString(
            "export." + Optional.ofNullable(labelKey).orElse(name().toLowerCase()) + ".sheet.name"
        );
    }

    @Override
    public List<ExcelHeader> getHeaders() {
        return headers;
    }
}
