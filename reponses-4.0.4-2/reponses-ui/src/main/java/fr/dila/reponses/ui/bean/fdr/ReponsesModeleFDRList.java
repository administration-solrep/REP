package fr.dila.reponses.ui.bean.fdr;

import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.ui.bean.ColonneInfo;

public class ReponsesModeleFDRList extends ModeleFDRList {

    @Override
    public void buildColonnesSubstitution(ModeleFDRListForm form) {
        listeColonnes.clear();

        listeColonnes.add(
            new ColonneInfo("modeleFDR.content.header.intitule", false, COLUMN_INTITULE, null, false, true)
        );
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.auteur", false, COLUMN_AUTEUR, null, false, true));
        listeColonnes.add(
            new ColonneInfo("modeleFDR.content.header.derniereModif", false, COLUMN_DATE_MODIF, null, true, true)
        );
    }
}
