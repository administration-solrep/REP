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
public class DelegationsDonneesListForm extends AbstractSortablePaginationForm {
    private static final String DELEGATION_DONNEE_DATE_FIN_SORT_NAME = "delegationDonneeDateFin";
    private static final String DELEGATION_DONNEE_DATE_DEBUT_SORT_NAME = "delegationDonneeDateDebut";
    private static final String DELEGATION_DONNEE_DE_SORT_NAME = "delegationDonneeDe";
    private static final String DELEGATION_DONNEE_A_SORT_NAME = "delegationDonneeA";

    @QueryParam(DELEGATION_DONNEE_A_SORT_NAME)
    private SortOrder delegationDonneeA;

    @QueryParam(DELEGATION_DONNEE_DE_SORT_NAME)
    private SortOrder delegationDonneeDe;

    @QueryParam(DELEGATION_DONNEE_DATE_DEBUT_SORT_NAME)
    private SortOrder delegationDonneeDateDebut;

    @QueryParam(DELEGATION_DONNEE_DATE_FIN_SORT_NAME)
    private SortOrder delegationDonneeDateFin;

    public DelegationsDonneesListForm() {
        super();
    }

    public SortOrder getDelegationDonneeA() {
        return delegationDonneeA;
    }

    public void setDelegationDonneeA(SortOrder delegationDonneeA) {
        this.delegationDonneeA = delegationDonneeA;
    }

    public SortOrder getDelegationDonneeDe() {
        return delegationDonneeDe;
    }

    public void setDelegationDonneeDe(SortOrder delegationDonneeDe) {
        this.delegationDonneeDe = delegationDonneeDe;
    }

    public SortOrder getDelegationDonneeDateDebut() {
        return delegationDonneeDateDebut;
    }

    public void setDelegationDonneeDateDebut(SortOrder delegationDonneeDateDebut) {
        this.delegationDonneeDateDebut = delegationDonneeDateDebut;
    }

    public SortOrder getDelegationDonneeDateFin() {
        return delegationDonneeDateFin;
    }

    public void setDelegationDonneeDateFin(SortOrder delegationDonneeDateFin) {
        this.delegationDonneeDateFin = delegationDonneeDateFin;
    }

    @Override
    protected void setDefaultSort() {
        // no default sort
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(DelegationConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME, new FormSort(delegationDonneeA));
        map.put(DelegationConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME, new FormSort(delegationDonneeDe));
        map.put(DelegationConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME, new FormSort(delegationDonneeDateDebut));
        map.put(DelegationConstant.DELEGATION_DATE_FIN_PROPERTY_NAME, new FormSort(delegationDonneeDateFin));
        return map;
    }
}
