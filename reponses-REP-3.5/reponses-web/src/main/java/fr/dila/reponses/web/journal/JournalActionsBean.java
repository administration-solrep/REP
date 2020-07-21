package fr.dila.reponses.web.journal;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.web.journal.STJournalActionsBean;

/**
 * ActionBean de gestion du journal.
 * 
 * @author BBY, ARN
 */
@Name("journalActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class JournalActionsBean extends STJournalActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void initCategoryList() {
        setContentViewName(getDefaultContentViewName());
        // categorie d'opération : interface, feuille de route, bordereau , fond de dossier , parapheur, journal et administration.
        String[] category = new String[] { "",
//                ReponsesEventConstant.CATEGORY_INTERFACE,
                STEventConstant.CATEGORY_FEUILLE_ROUTE, 
                STEventConstant.CATEGORY_BORDEREAU,
                STEventConstant.CATEGORY_FDD, 
                STEventConstant.CATEGORY_PARAPHEUR,
//                STEventConstant.CATEGORY_JOURNAL,
                STEventConstant.CATEGORY_ADMINISTRATION
                };
        categoryList.addAll(Arrays.asList(category));
    }
    
    @Override
    public String getDefaultContentViewName(){
        return "JOURNAL_DOSSIER";
    };

}
