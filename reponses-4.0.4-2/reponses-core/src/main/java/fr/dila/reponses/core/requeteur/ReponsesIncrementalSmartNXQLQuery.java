package fr.dila.reponses.core.requeteur;

import fr.dila.st.core.requeteur.STIncrementalSmartNXQLQuery;

/**
 * Reponses héritage de l'incremental smart query donnée par Nuxeo
 *
 * @since 5.4
 * @author jgomez
 */
public class ReponsesIncrementalSmartNXQLQuery extends STIncrementalSmartNXQLQuery {
    private static final long serialVersionUID = 1L;

    /**
     * Un binding pour le poste. (Retraitement de la valeur en identifiant de mailbox poste-<valeur>.
     */
    private String posteValue;

    /**
     * Un binding laisser 0 et 1 inchangé
     */
    private String booleanAsStringValue;

    /**
     *
     * @param existingQueryPart
     */
    public ReponsesIncrementalSmartNXQLQuery(String existingQueryPart) {
        super(existingQueryPart);
    }

    @Override
    public void setPosteValue(String posteValue) {
        this.posteValue = posteValue;
        setValue(posteValue);
    }

    @Override
    public String getPosteValue() {
        return posteValue;
    }

    @Override
    public void setBooleanAsStringValue(String booleanAsStringValue) {
        this.booleanAsStringValue = booleanAsStringValue;
        setValue(booleanAsStringValue);
    }

    @Override
    public String getBooleanAsStringValue() {
        return booleanAsStringValue;
    }

    /**
     * Clears all value bindings.
     */
    @Override
    protected void clearValues() {
        super.clearValues();
        posteValue = null;
        booleanAsStringValue = null;
    }
}
