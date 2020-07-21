package fr.dila.reponses.core.favoris;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.st.core.util.PropertyUtil;

public class FavorisIndexationImpl implements FavorisIndexation {
    
    private static final long serialVersionUID = 1L;
    
    protected DocumentModel document;
    
    public FavorisIndexationImpl(DocumentModel doc){
        this.document = doc;
    }
    
    @Override
    public void setDocument(DocumentModel doc){
        this.document = doc;
    }
    
    @Override
    public DocumentModel getDocument(){
        return this.document;
    }

    @Override
    public String getTypeIndexation() {
        return PropertyUtil.getStringProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_TYPE);
    }

    @Override
    public void setTypeIndexation(String typeIndexation) {
        PropertyUtil.setProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_TYPE, typeIndexation);
    }

    @Override
    public String getNiveau1() {
        return PropertyUtil.getStringProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_NIVEAU1);
    }

    @Override
    public void setNiveau1(String niveau1) {
        PropertyUtil.setProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_NIVEAU1, niveau1);
    }

    @Override
    public String getNiveau2() {
        return PropertyUtil.getStringProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_NIVEAU2);
    }

    @Override
    public void setNiveau2(String niveau2) {
        PropertyUtil.setProperty(document, ReponsesSchemaConstant.INDEXATION_SCHEMA, ReponsesSchemaConstant.INDEXATION_NIVEAU2, niveau2);
    }
}
