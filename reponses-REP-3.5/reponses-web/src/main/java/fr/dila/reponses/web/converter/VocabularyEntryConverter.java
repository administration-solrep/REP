package fr.dila.reponses.web.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

/**
 * Converter JSF qui fournit l'identifiant et le label d'un objet VocabularyEntry.
 * Utilisé dans les composant RichFaces:listShuttle
 * 
 * @author arolin
 */
public class VocabularyEntryConverter implements Converter {
    
    /**
     * Liste de vocabulaire.
     */
    private List<VocabularyEntry> vocabulaireEntryList;
    
    /**
     * Constructeur
     * 
     * @param directoryName
     */
    public VocabularyEntryConverter(List<VocabularyEntry> vocabulaireEntryList){
        this.vocabulaireEntryList = vocabulaireEntryList;
    }
    
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String string) {
        if (string != null) {
            for (VocabularyEntry vocabularyEntry : vocabulaireEntryList) {
                if (string.equals(vocabularyEntry.getId())) {
                    return vocabularyEntry;
                }
            }
        }
        return string;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent arg1, Object object) {
        if (object != null && object instanceof VocabularyEntry) {
            VocabularyEntry vocEntry = (VocabularyEntry) object;
            return vocEntry.getId();
        }
        return null;
    }
    
}
