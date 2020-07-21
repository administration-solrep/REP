package fr.dila.reponses.core.archive;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

public class ListeEliminationImpl implements ListeElimination {
    
    DocumentModel doc;

    public ListeEliminationImpl(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return doc;
    }
    
    @Override
    public void setTitle(String title){
    	DublincoreSchemaUtils.setTitle(doc, title);
    }
    
    @Override
    public void save(CoreSession session) throws ClientException {
        doc = session.saveDocument(doc);
    }

    @Override
    public Boolean isEnCours() {
        return PropertyUtil.getBooleanProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_EN_COURS_PROPERTY);
    }

    @Override
    public void setEnCours(Boolean enCours) {
        PropertyUtil.setProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_EN_COURS_PROPERTY, enCours);
    }
    
    @Override
    public Boolean isSuppressionEnCours() {
        return PropertyUtil.getBooleanProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_SUPPRESSION_EN_COURS_PROPERTY);
    }

    @Override
    public void setSuppressionEnCours(Boolean enCours) {
        PropertyUtil.setProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_SUPPRESSION_EN_COURS_PROPERTY, enCours);
    }

	@Override
	public Boolean isAbandonEnCours() {
		return PropertyUtil.getBooleanProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_ABANDON_EN_COURS_PROPERTY);
	}

	@Override
	public void setAbandonEnCours(Boolean enCours) {
		PropertyUtil.setProperty(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA, ReponsesSchemaConstant.LISTE_ELIMINATION_ABANDON_EN_COURS_PROPERTY, enCours);
	}

}
