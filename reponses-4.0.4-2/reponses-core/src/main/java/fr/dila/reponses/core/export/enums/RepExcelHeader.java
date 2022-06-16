package fr.dila.reponses.core.export.enums;

import fr.dila.st.core.export.enums.ExcelHeader;

public enum RepExcelHeader implements ExcelHeader {
    AUTEUR,
    DATE_PUBLICATION_JO("date.publication.jo"),
    DELAI,
    DIRECTION_ETAPES_EN_COURS("direction.etapes.en.cours"),
    ETAPE_EN_COURS("etape.en.cours"),
    ETAT,
    MINISTERE_ATTRIBUTAIRE("ministere.attributaire"),
    MOTS_CLES("mots.cles"),
    NATURE,
    NUMERO_QUESTION("numero.question"),
    ORIGINE;

    private final String specificLabelKey;

    RepExcelHeader() {
        this(null);
    }

    RepExcelHeader(String specificLabelKey) {
        this.specificLabelKey = specificLabelKey;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getSpecificLabelKey() {
        return specificLabelKey;
    }
}
