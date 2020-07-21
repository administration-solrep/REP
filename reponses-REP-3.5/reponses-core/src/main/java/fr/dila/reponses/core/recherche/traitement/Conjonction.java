package fr.dila.reponses.core.recherche.traitement;

import org.apache.commons.lang.BooleanUtils;

public class Conjonction {
    private Boolean etatRetire;
    private Boolean etatNonRetire;

    
    public Conjonction(){
    }
    
    public void setEtatRetire(Boolean value) {
        this.etatRetire = value;
    }

    public void setEtatNonRetire(Boolean value) {
        this.etatNonRetire = value;
    }

    public String getClause() {
        String subClause = null;
        String predicate = "";
        if (this.etatRetire == null && this.etatNonRetire == null){
            return subClause;
        }
        if  (BooleanUtils.isTrue(this.etatRetire) && BooleanUtils.isTrue(this.etatNonRetire)){
            return subClause;
        }
        if (BooleanUtils.isTrue(this.etatRetire)){
            predicate = "q.qu:etatRetire = 1";
        }
        if (BooleanUtils.isTrue(this.etatNonRetire)){
            predicate = "q.qu:etatRetire = 0";
        }
        subClause = "(" + predicate + ")";
        return subClause;
    }

}
