package fr.dila.reponses.ui.th.bean.recherche;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.CRITERE_RECHERCHE_TXT_INTEGRAL_XPATH;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class TexteIntegralForm implements Serializable {
    private static final long serialVersionUID = 5383283315904101021L;

    @NxProp(xpath = CRITERE_RECHERCHE_TXT_INTEGRAL_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("expression")
    private String expression;

    @FormParam("rechercherDans")
    private ArrayList<String> rechercherDans = newArrayList(RequeteConstants.DANS_TEXTE_QUESTION);

    @FormParam("rechercheExacte")
    private ArrayList<String> rechercheExacte;

    public String getExpression() {
        return expression;
    }

    public TexteIntegralForm() {}

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ArrayList<String> getRechercherDans() {
        return rechercherDans;
    }

    public void setRechercherDans(ArrayList<String> rechercherDans) {
        this.rechercherDans = rechercherDans;
    }

    public ArrayList<String> getRechercheExacte() {
        return rechercheExacte;
    }

    public void setRechercheExacte(ArrayList<String> rechercheExacte) {
        this.rechercheExacte = rechercheExacte;
    }
}
