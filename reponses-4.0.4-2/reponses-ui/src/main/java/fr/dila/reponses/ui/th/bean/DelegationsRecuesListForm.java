package fr.dila.reponses.ui.th.bean;

import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class DelegationsRecuesListForm extends AbstractSortablePaginationForm {
    private static final String DELEGATION_RECUE_DATE_FIN_SORT_NAME = "delegationRecueDateFin";
    private static final String DELEGATION_RECUE_DATE_DEBUT_SORT_NAME = "delegationRecueDateDebut";
    private static final String DELEGATION_RECUE_DE_SORT_NAME = "delegationRecueDe";
    private static final String DELEGATION_RECUE_A_SORT_NAME = "delegationRecueA";

    @QueryParam(DELEGATION_RECUE_A_SORT_NAME)
    private SortOrder delegationRecueA;

    @QueryParam(DELEGATION_RECUE_DE_SORT_NAME)
    private SortOrder delegationRecueDe;

    @QueryParam(DELEGATION_RECUE_DATE_DEBUT_SORT_NAME)
    private SortOrder delegationRecueDateDebut;

    @QueryParam(DELEGATION_RECUE_DATE_FIN_SORT_NAME)
    private SortOrder delegationRecueDateFin;

    public DelegationsRecuesListForm() {
        super();
    }

    public SortOrder getDelegationRecueA() {
        return delegationRecueA;
    }

    public void setDelegationRecueA(SortOrder delegationRecueA) {
        this.delegationRecueA = delegationRecueA;
    }

    public SortOrder getDelegationRecueDe() {
        return delegationRecueDe;
    }

    public void setDelegationRecueDe(SortOrder delegationRecueDe) {
        this.delegationRecueDe = delegationRecueDe;
    }

    public SortOrder getDelegationRecueDateDebut() {
        return delegationRecueDateDebut;
    }

    public void setDelegationRecueDateDebut(SortOrder delegationRecueDateDebut) {
        this.delegationRecueDateDebut = delegationRecueDateDebut;
    }

    public SortOrder getDelegationRecueDateFin() {
        return delegationRecueDateFin;
    }

    public void setDelegationRecueDateFin(SortOrder delegationRecueDateFin) {
        this.delegationRecueDateFin = delegationRecueDateFin;
    }

    @Override
    protected void setDefaultSort() {
        // no default sort
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(DelegationConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME, new FormSort(delegationRecueA));
        map.put(DelegationConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME, new FormSort(delegationRecueDe));
        map.put(DelegationConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME, new FormSort(delegationRecueDateDebut));
        map.put(DelegationConstant.DELEGATION_DATE_FIN_PROPERTY_NAME, new FormSort(delegationRecueDateFin));
        return map;
    }
}
