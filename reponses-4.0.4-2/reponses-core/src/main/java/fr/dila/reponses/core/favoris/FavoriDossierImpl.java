package fr.dila.reponses.core.favoris;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.favoris.FavoriDossier;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;

public class FavoriDossierImpl implements FavoriDossier {
    private DocumentModel doc;

    public FavoriDossierImpl(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public Dossier getDossier(CoreSession session) {
        String questionId = getTargetDocumentId();
        DocumentRef ref = new IdRef(questionId);
        return session.getDocument(ref).getAdapter(Question.class).getDossier(session);
    }

    @Override
    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public String getTargetDocumentId() {
        return PropertyUtil.getStringProperty(doc, ReponsesSchemaConstant.FAVORI_DOSSIER_SCHEMA, "targetDocument");
    }

    @Override
    public void setTargetDocumentId(String id) {
        PropertyUtil.setProperty(doc, ReponsesSchemaConstant.FAVORI_DOSSIER_SCHEMA, "targetDocument", id);
    }

    @Override
    public void setTitle(String title) {
        DublincoreSchemaUtils.setTitle(doc, title);
    }

    @Override
    public void save(CoreSession session) {
        doc = session.saveDocument(doc);
    }
}
