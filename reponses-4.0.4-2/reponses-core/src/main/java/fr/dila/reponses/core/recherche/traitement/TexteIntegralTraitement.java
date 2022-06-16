package fr.dila.reponses.core.recherche.traitement;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.constant.STQueryConstant;
import fr.dila.st.api.recherche.RequeteTraitement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TexteIntegralTraitement implements RequeteTraitement<Requete> {

    @Override
    public void doBeforeQuery(Requete requete) {
        String texteIntegral = requete.getCritereRechercheIntegral();
        if (StringUtils.isBlank(texteIntegral)) {
            requete.setSubClause(null);
            return;
        }
        // Si rien n'est sélectionné, on fait une recherche dans le texte de la question
        if (!requete.hasTextRechercheSelected()) {
            requete.setDansTexteQuestion(true);
        }
        List<String> indexFragments = new ArrayList<>();
        for (ReponsesFulltextIndexEnum index : getChosenIndex(requete)) {
            // Recherche Exacte
            if (requete.getAppliquerRechercheExacte()) {
                indexFragments.add(index.getRechercheExacte(texteIntegral));
            }
            //Recherche avec les opérateurs de stemming
            else {
                indexFragments.add(index.getFullText(texteIntegral));
            }
        }
        if (indexFragments.isEmpty()) {
            return;
        }
        String rawSubClause = StringUtils.join(indexFragments, " OR ");
        if (StringUtils.isNotEmpty(rawSubClause)) {
            rawSubClause = "(" + rawSubClause + ")";
        }
        requete.setSubClause(rawSubClause);
    }

    @Override
    public void doAfterQuery(Requete requete) {}

    @Override
    public void init(Requete requete) {
        requete.setDansTexteQuestion(true);
    }

    protected List<ReponsesFulltextIndexEnum> getChosenIndex(final Requete requete) {
        List<ReponsesFulltextIndexEnum> usedIndex = new ArrayList<>();
        for (ReponsesFulltextIndexEnum index : ReponsesFulltextIndexEnum.values()) {
            if (index.isNeededBy(requete)) {
                usedIndex.add(index);
                if (STQueryConstant.NXQL.equals(requete.getQueryType())) {
                    index.desactivateUFNXQL();
                } else {
                    index.activateUFNXQL();
                }
            }
        }
        return usedIndex;
    }
}
