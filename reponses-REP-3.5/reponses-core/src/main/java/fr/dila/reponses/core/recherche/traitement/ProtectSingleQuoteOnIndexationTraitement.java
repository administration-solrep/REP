package fr.dila.reponses.core.recherche.traitement;

/**
 * On a besoin de protéger 
 * 
 * 
 */


import java.util.ArrayList;
import java.util.List;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.recherche.RequeteTraitement;

public class ProtectSingleQuoteOnIndexationTraitement implements RequeteTraitement<Requete>{

    @Override
    public void init(Requete requete) {
        
    }

    @Override
    public void doBeforeQuery(Requete requete){
        ReponsesVocabularyService vocabularyService = ReponsesServiceLocator.getVocabularyService();
        ReponsesIndexableDocument indexable = requete.getDocument().getAdapter(ReponsesIndexableDocument.class);
        for (String vocabulary: vocabularyService.getVocabularyList()){
            List<String> labels;
            labels = indexable.getVocEntries(vocabulary);
            List<String> protected_labels = new ArrayList<String>();
            for (String label:labels){
                protected_labels.add(protect(label));
            }
            indexable.setVocEntries(vocabulary,protected_labels);
        }
    }

    private static String protect(String label) {
        return label.replaceAll("'","\\\\'");
    }

    @Override
    public void doAfterQuery(Requete requete) {
        
    }

}
