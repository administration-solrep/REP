package fr.dila.reponses.ui.helper;

import static java.lang.String.format;

import fr.dila.st.ui.bean.TreeElementDTO;

public final class TreeElementDTOHelper {

    /**
     * Construit le lien pour un élement dans le dernier niveau du plan de classement
     *
     * @param element {@link TreeElementDTO}
     * @param origin AN ou SENAT
     * @param cle la clé de l'élement
     * @param cleParent la clé du parent
     */
    public static void setLink(TreeElementDTO element, String origin, String cle, String cleParent) {
        element.setLink(format("/classement/liste?origine=%s&cle=%s&cleParent=%s", origin, cle, cleParent));
    }

    private TreeElementDTOHelper() {
        // utility class
    }
}
