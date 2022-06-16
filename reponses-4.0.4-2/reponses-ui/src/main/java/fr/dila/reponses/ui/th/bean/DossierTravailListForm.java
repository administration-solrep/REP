package fr.dila.reponses.ui.th.bean;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_MOTS_CLES_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_NUMERO_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_REPONSES_LINK_TYPE_QUESTION_XPATH;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.HashMap;
import java.util.Map;

@SwBean(keepdefaultValue = true)
public class DossierTravailListForm extends DossierListForm {

    public DossierTravailListForm() {
        super();
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(DOSSIER_REPONSES_LINK_DATE_PUBLICATION_JO_QUESTION_XPATH, new FormSort(getDate(), getDateOrder()));
        map.put(DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_XPATH, new FormSort(getQuestion(), getQuestionOrder()));
        map.put(
            DOSSIER_REPONSES_LINK_NUMERO_QUESTION_XPATH,
            new FormSort(getNumeroQuestion(), getNumeroQuestionOrder())
        );
        map.put(DOSSIER_REPONSES_LINK_TYPE_QUESTION_XPATH, new FormSort(getNature(), getNatureOrder()));
        map.put(DOSSIER_REPONSES_LINK_NOM_COMPLET_AUTEUR_QUESTION_XPATH, new FormSort(getAuteur(), getAuteurOrder()));
        map.put(
            DOSSIER_REPONSES_LINK_DATE_SIGNALEMENT_QUESTION_XPATH,
            new FormSort(getDateSignal(), getDateSignalOrder())
        );
        map.put(DOSSIER_REPONSES_LINK_MOTS_CLES_XPATH, new FormSort(getIndexPrinc(), getIndexPrincOrder()));
        return map;
    }

    @Override
    protected void setDefaultSort() {
        setDate(SortOrder.DESC);
        setDateOrder(1);

        setQuestion(SortOrder.ASC);
        setQuestionOrder(2);
    }

    public static DossierTravailListForm newForm() {
        return initForm(new DossierTravailListForm());
    }
}
