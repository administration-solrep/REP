package fr.dila.reponses.web.contentview;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.core.client.AbstractMapDTO;

@Name("providerBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class ProviderBean extends fr.dila.st.web.contentview.ProviderBean {

    private static final long serialVersionUID = 5747655387411311492L;

    public static final String REFRESH_CONTENT_VIEW_EVENT = "refreshContentView";
    
    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    /**
     * split pour le csv/pdf qui ne reconnait pas fn:split
     * 
     * @param value
     * @param regex
     * @return
     */
    public String[] split(final String value, final String regex) {
        return value.split(regex);
    }

    /**
     * length pour le csv/pdf qui ne reconnait pas fn:length
     * 
     * @param value
     * @param regex
     * @return
     */
    public int length(final Collection<?> list) {
        return list == null ? 0 : list.size();
    }

    @Observer(value = { REFRESH_CONTENT_VIEW_EVENT })
    public void refreshProvider() {
        contentViewActions.refreshOnSeamEvent(EventNames.DOCUMENT_CHANGED);
    }
    
    public String getResultCount(final List<DocumentModel> documentModels, final long resultCount) {
        final StringBuilder builder = new StringBuilder();

        if (documentModels != null && !documentModels.isEmpty()) {
            if (documentModels.get(0) instanceof DocumentModel) {
                final DocumentModel documentModel = documentModels.get(0);
                builder.append(resultCount);
                builder.append(" ");
                builder.append(resourcesAccessor.getMessages().get("page.result." + documentModel.getType() + (resultCount > 1 ? "s" : "")));
            } else if (documentModels.get(0) instanceof AbstractMapDTO) {
                final AbstractMapDTO abstractMapDTO = (AbstractMapDTO) documentModels.get(0);
                builder.append(resultCount);
                builder.append(" ");
                builder.append(resourcesAccessor.getMessages().get("page.result." + abstractMapDTO.getType() + (resultCount > 1 ? "s" : "")));
            } else {
                builder.append(resultCount);
                builder.append(" ");
                builder.append(resourcesAccessor.getMessages().get("page.result.entry" + (resultCount > 1 ? "s" : "")));
            }
        }
        return builder.toString();
    }

}
