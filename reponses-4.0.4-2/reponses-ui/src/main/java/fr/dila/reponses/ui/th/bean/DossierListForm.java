package fr.dila.reponses.ui.th.bean;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_AUTEUR_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DATE_SIGNAL_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DATE_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_MINATTR_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_MININT_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_MOTCLE_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_NUMERO_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_ORIGINE_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_SOURCE_NUMERO_XPATH;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_TYPE_QUESTION_XPATH;
import static fr.dila.st.api.constant.STConstant.SORT_ORDER_SUFFIX;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.bean.IColonneInfo;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class DossierListForm extends AbstractSortablePaginationForm {
    public static final String MINISTERE_INTERROGE_SORT_NAME = "minInterSort";
    public static final String MINISTERE_ATTRIBUTAIRE_SORT_NAME = "minAttrSort";
    public static final String INDEXATION_PRINCIPALE_SORT_NAME = "indexPrincSort";
    public static final String DATE_SIGNALEMENT_SORT_NAME = "dateSignalSort";
    public static final String AUTEUR_SORT_NAME = "auteurSort";
    public static final String NATURE_SORT_NAME = "natureSort";
    public static final String QUESTION_SORT_NAME = "questionSort";
    public static final String ORIGINE_QUESTION_SORT_NAME = "origineQuestionSort";
    public static final String DATE_SORT_NAME = "dateSort";
    public static final String DELAI_SORT_NAME = "delai";
    public static final String ETAT_SORT_NAME = "etat";

    @QueryParam(QUESTION_SORT_NAME)
    @FormParam(QUESTION_SORT_NAME)
    private SortOrder question;

    @QueryParam(ORIGINE_QUESTION_SORT_NAME)
    @FormParam(ORIGINE_QUESTION_SORT_NAME)
    private SortOrder origineQuestion;

    @QueryParam(DATE_SORT_NAME)
    @FormParam(DATE_SORT_NAME)
    private SortOrder date;

    @QueryParam(AUTEUR_SORT_NAME)
    @FormParam(AUTEUR_SORT_NAME)
    private SortOrder auteur;

    @QueryParam(INDEXATION_PRINCIPALE_SORT_NAME)
    @FormParam(INDEXATION_PRINCIPALE_SORT_NAME)
    private SortOrder indexPrinc;

    @QueryParam(DELAI_SORT_NAME)
    @FormParam(DELAI_SORT_NAME)
    private SortOrder delai;

    @QueryParam(ETAT_SORT_NAME)
    @FormParam(ETAT_SORT_NAME)
    private SortOrder etat;

    @QueryParam(MINISTERE_ATTRIBUTAIRE_SORT_NAME)
    @FormParam(MINISTERE_ATTRIBUTAIRE_SORT_NAME)
    private SortOrder minAttr;

    @QueryParam(NATURE_SORT_NAME)
    @FormParam(NATURE_SORT_NAME)
    private SortOrder nature;

    @QueryParam(MINISTERE_INTERROGE_SORT_NAME)
    @FormParam(MINISTERE_INTERROGE_SORT_NAME)
    private SortOrder minInter;

    @QueryParam(DATE_SIGNALEMENT_SORT_NAME)
    @FormParam(DATE_SIGNALEMENT_SORT_NAME)
    private SortOrder dateSignal;

    @QueryParam(QUESTION_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(QUESTION_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer questionOrder;

    @QueryParam(ORIGINE_QUESTION_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(ORIGINE_QUESTION_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer origineQuestionOrder;

    @QueryParam(DATE_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(DATE_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer dateOrder;

    @QueryParam(AUTEUR_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(AUTEUR_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer auteurOrder;

    @QueryParam(INDEXATION_PRINCIPALE_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(INDEXATION_PRINCIPALE_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer indexPrincOrder;

    @QueryParam(MINISTERE_ATTRIBUTAIRE_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(MINISTERE_ATTRIBUTAIRE_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer minAttrOrder;

    @QueryParam(NATURE_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(NATURE_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer natureOrder;

    @QueryParam(MINISTERE_INTERROGE_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(MINISTERE_INTERROGE_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer minInterOrder;

    @QueryParam(DATE_SIGNALEMENT_SORT_NAME + SORT_ORDER_SUFFIX)
    @FormParam(DATE_SIGNALEMENT_SORT_NAME + SORT_ORDER_SUFFIX)
    private Integer dateSignalOrder;

    private SortOrder numeroQuestion;
    private Integer numeroQuestionOrder;

    private boolean minAttrVisible;
    private boolean minInterVisible;
    private boolean etapeVisible;
    private boolean dirEtapeVisible;
    private boolean natureVisible;
    private boolean legisVisible;
    private boolean dateSignalVisible;
    private boolean dateEffetRenouvellementVisible;
    private boolean dateRappelVisible;
    private boolean qERappelVisible;

    public DossierListForm() {
        super();
    }

    public SortOrder getQuestion() {
        return question;
    }

    public void setQuestion(SortOrder question) {
        this.question = question;
    }

    public SortOrder getOrigineQuestion() {
        return origineQuestion;
    }

    public void setOrigineQuestion(SortOrder origineQuestion) {
        this.origineQuestion = origineQuestion;
    }

    public SortOrder getDate() {
        return date;
    }

    public void setDate(SortOrder date) {
        this.date = date;
    }

    public SortOrder getAuteur() {
        return auteur;
    }

    public void setAuteur(SortOrder auteur) {
        this.auteur = auteur;
    }

    public SortOrder getIndexPrinc() {
        return indexPrinc;
    }

    public void setIndexPrinc(SortOrder indexPrinc) {
        this.indexPrinc = indexPrinc;
    }

    public SortOrder getDelai() {
        return delai;
    }

    public void setDelai(SortOrder delai) {
        this.delai = delai;
    }

    public SortOrder getEtat() {
        return etat;
    }

    public void setEtat(SortOrder etat) {
        this.etat = etat;
    }

    public SortOrder getMinAttr() {
        return minAttr;
    }

    public void setMinAttr(SortOrder minAttr) {
        this.minAttr = minAttr;
    }

    public SortOrder getNature() {
        return nature;
    }

    public void setNature(SortOrder nature) {
        this.nature = nature;
    }

    public SortOrder getMinInter() {
        return minInter;
    }

    public void setMinInter(SortOrder minInter) {
        this.minInter = minInter;
    }

    public SortOrder getDateSignal() {
        return dateSignal;
    }

    public void setDateSignal(SortOrder dateSignal) {
        this.dateSignal = dateSignal;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getOrigineQuestionOrder() {
        return origineQuestionOrder;
    }

    public void setOrigineQuestionOrder(Integer origineQuestionOrder) {
        this.origineQuestionOrder = origineQuestionOrder;
    }

    public Integer getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Integer dateOrder) {
        this.dateOrder = dateOrder;
    }

    public Integer getAuteurOrder() {
        return auteurOrder;
    }

    public void setAuteurOrder(Integer auteurOrder) {
        this.auteurOrder = auteurOrder;
    }

    public Integer getIndexPrincOrder() {
        return indexPrincOrder;
    }

    public void setIndexPrincOrder(Integer indexPrincOrder) {
        this.indexPrincOrder = indexPrincOrder;
    }

    public Integer getMinAttrOrder() {
        return minAttrOrder;
    }

    public void setMinAttrOrder(Integer minAttrOrder) {
        this.minAttrOrder = minAttrOrder;
    }

    public Integer getNatureOrder() {
        return natureOrder;
    }

    public void setNatureOrder(Integer natureOrder) {
        this.natureOrder = natureOrder;
    }

    public Integer getMinInterOrder() {
        return minInterOrder;
    }

    public void setMinInterOrder(Integer minInterOrder) {
        this.minInterOrder = minInterOrder;
    }

    public Integer getDateSignalOrder() {
        return dateSignalOrder;
    }

    public void setDateSignalOrder(Integer dateSignalOrder) {
        this.dateSignalOrder = dateSignalOrder;
    }

    public boolean isMinAttrVisible() {
        return minAttrVisible;
    }

    public boolean isMinInterVisible() {
        return minInterVisible;
    }

    public boolean isEtapeVisible() {
        return etapeVisible;
    }

    public boolean isDirEtapeVisible() {
        return dirEtapeVisible;
    }

    public boolean isNatureVisible() {
        return natureVisible;
    }

    public boolean isLegisVisible() {
        return legisVisible;
    }

    public boolean isDateSignalVisible() {
        return dateSignalVisible;
    }

    public boolean isDateEffetRenouvellementVisible() {
        return dateEffetRenouvellementVisible;
    }

    public boolean isDateRappelVisible() {
        return dateRappelVisible;
    }

    public boolean isQERappelVisible() {
        return qERappelVisible;
    }

    public SortOrder getNumeroQuestion() {
        return numeroQuestion;
    }

    public void setNumeroQuestion(SortOrder numeroQuestion) {
        this.numeroQuestion = numeroQuestion;
    }

    public Integer getNumeroQuestionOrder() {
        return numeroQuestionOrder;
    }

    public void setNumeroQuestionOrder(Integer numeroQuestionOrder) {
        this.numeroQuestionOrder = numeroQuestionOrder;
    }

    public void setColumnVisibility(List<IColonneInfo> lstUserColumns) {
        for (IColonneInfo userColumn : lstUserColumns) {
            if (userColumn.getLabel() != null) {
                switch (userColumn.getLabel()) {
                    case "label.content.header.dateSignalement.question":
                        dateSignalVisible = userColumn.isVisible();
                        break;
                    case "label.requete.resultat.dossierNature":
                        natureVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.etapes":
                        etapeVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.legislature":
                        legisVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.ministere.attributaire":
                        minAttrVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.ministere.interroge":
                        minInterVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.direction.runningStep":
                        dirEtapeVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.date.effet.renouvellement":
                        dateEffetRenouvellementVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.date.rappel":
                        dateRappelVisible = userColumn.isVisible();
                        break;
                    case "label.content.header.qe.rappel":
                        qERappelVisible = userColumn.isVisible();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(QUESTION_DATE_XPATH, new FormSort(date, dateOrder));
        map.put(QUESTION_ORIGINE_XPATH, new FormSort(origineQuestion, origineQuestionOrder));
        map.put(QUESTION_NUMERO_XPATH, new FormSort(numeroQuestion, numeroQuestionOrder));
        map.put(QUESTION_SOURCE_NUMERO_XPATH, new FormSort(question, questionOrder));
        map.put(QUESTION_TYPE_QUESTION_XPATH, new FormSort(nature, natureOrder));
        map.put(QUESTION_AUTEUR_XPATH, new FormSort(auteur, auteurOrder));
        map.put(QUESTION_DATE_SIGNAL_XPATH, new FormSort(dateSignal, dateSignalOrder));
        map.put(QUESTION_MOTCLE_XPATH, new FormSort(indexPrinc, indexPrincOrder));
        map.put(QUESTION_MINATTR_XPATH, new FormSort(minAttr, minAttrOrder));
        map.put(QUESTION_MININT_XPATH, new FormSort(minInter, minInterOrder));
        return map;
    }

    @Override
    protected void setDefaultSort() {
        date = SortOrder.DESC;
        dateOrder = 1;

        origineQuestion = SortOrder.ASC;
        origineQuestionOrder = 2;

        numeroQuestion = SortOrder.ASC;
        numeroQuestionOrder = 3;
    }

    public static DossierListForm newForm() {
        return initForm(new DossierListForm());
    }
}
