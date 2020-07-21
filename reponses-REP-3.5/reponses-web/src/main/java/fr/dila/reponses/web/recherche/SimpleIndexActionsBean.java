package fr.dila.reponses.web.recherche;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.recherche.IndexationProvider;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Ce bean seam permet d'assurer le support pour le widget indexation_widget.
 * 
 */
@Name("simpleIndexActions")
@Scope(CONVERSATION)
public class SimpleIndexActionsBean implements Serializable {

    private static final long serialVersionUID = -7882073590849165234L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected WebActions webActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    private static final Log LOG = LogFactory.getLog(SimpleIndexActionsBean.class);

    public static final String VALUE_PARAMETER_NAME = "value";

    public static final String INDEXATION_PARAMETER_NAME = "indexationProvider";

    /**
     * 
     * @return indexationProviderMap Un map chargé de faire le lien entre une zone d'indexation et une boite de suggestion.
     * @throws ClientException
     * 
     */
    @Factory(value = "indexationProviderMap")
    public Map<String, IndexationProvider> getIndexationProviderMap() {
        HashMap<String, IndexationProvider> indexationProviderMap = new HashMap<String, IndexationProvider>();
        ReponsesVocabularyService vocabularyService = ReponsesServiceLocator.getVocabularyService();
        for (String zone : vocabularyService.getZones()) {
            indexationProviderMap.put(zone, new IndexationProvider(zone));
        }
        return indexationProviderMap;
    }

    public String addIndex(String componentId, DocumentModel indexableDocument, IndexationProvider provider) {
        ReponsesIndexableDocument indexableDoc = getDocumentAdapted(indexableDocument);
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        UIComponent component = findComponent(root, componentId);
        if (provider == null || indexableDoc == null) {
            String messageKey = "message.add.index.keyword_not_in_list";
            context.addMessage(component.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, resourcesAccessor.getMessages().get(messageKey),
                    null));
            return StringUtils.EMPTY;
        }
        // Suppression des espaces inutiles
        provider.setLabel(StringUtils.trim(provider.getLabel()));
        if (StringUtils.isBlank(provider.getLabel())) {
            String messageKey = "message.add.new.index.not.empty";
            context.addMessage(component.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, resourcesAccessor.getMessages().get(messageKey),
                    provider.getLabel()));
            return StringUtils.EMPTY;
        }
        try {
            indexableDoc.addVocEntry(provider.getVocabulary(), provider.getLabel());
        } catch (Exception e) {
            String messageKey = "message.add.index.keyword_not_in_list";
            context.addMessage(component.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, resourcesAccessor.getMessages().get(messageKey),
                    null));
            LOG.warn("echec de l'ajout de l'index ");
        }
        provider.reset();
        return StringUtils.EMPTY;
    }

    // public String addIndexSaisieLibre(String id, DocumentModel indexableDocument, IndexationProvider provider){
    // ReponsesIndexableDocument indexableDoc = getDocumentAdapted(indexableDocument);
    // FacesContext context = FacesContext.getCurrentInstance();
    // UIViewRoot root = context.getViewRoot();
    // UIComponent c = findComponent(root, id);
    // if (provider == null || indexableDoc == null){
    // String messageKey = "message.add.index.keyword_not_in_list";
    // context.addMessage(c.getClientId(context),new FacesMessage(FacesMessage.SEVERITY_ERROR,resourcesAccessor.getMessages().get(messageKey),null));
    // return StringUtils.EMPTY;
    // }
    // if (StringUtils.isBlank(provider.getLabel())){
    // String messageKey = "message.add.new.index.not.empty";
    // context.addMessage(c.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, resourcesAccessor.getMessages().get(messageKey), provider.getLabel()));
    // return StringUtils.EMPTY;
    // }
    // try{
    // indexableDoc.addVocEntry(provider.getVocabulary(), provider.getLabel());
    // }
    // catch (Exception e) {
    // String messageKey = "message.add.index.keyword_not_in_list";
    // context.addMessage(c.getClientId(context),new FacesMessage(FacesMessage.SEVERITY_ERROR,resourcesAccessor.getMessages().get(messageKey),null));
    // LOG.error("echec de l'ajout de l'index ");
    // }
    // provider.reset();
    // return StringUtils.EMPTY;
    // }

    public void removeIndex(DocumentModel indexableDocument, String[] item) {
        String vocabulary = item[0].trim();
        String label = item[1].trim();
        try {
            getDocumentAdapted(indexableDocument).removeVocEntry(vocabulary, label);
        } catch (ClientException e) {
            String messageKey = "message.remove.index.failed";
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(messageKey));
        }
    }

    /**
     * Retourne un document adapté en ReponseIndexableDocument
     * 
     * @param doc
     * @return Le document adapté
     * @throws ClientException
     */
    public ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc) {
        return doc.getAdapter(ReponsesIndexableDocument.class);
    }

    // Helper pour déclencher un appel AJAX qui met à jour une propriété
    public void refresh() {
        // DO NOTHING
    }

    /**
     * Finds component with the given id
     */
    private UIComponent findComponent(UIComponent component, String componentId) {
        if (componentId.equals(component.getId())) {
            return component;
        }
        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent found = findComponent(kids.next(), componentId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

}