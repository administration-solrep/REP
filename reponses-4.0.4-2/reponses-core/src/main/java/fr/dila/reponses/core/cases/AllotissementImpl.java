package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class AllotissementImpl implements Allotissement {
    private static final long serialVersionUID = 1L;

    private DocumentModel document;

    public AllotissementImpl(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public List<String> getIdDossiers() {
        return PropertyUtil.getStringListProperty(
            document,
            DossierConstants.ALLOTISSEMENT_SCHEMA,
            DossierConstants.ALLOTISSEMENT_IDDOSSIERS_PROPERTY
        );
    }

    @Override
    public void setIdDossiers(List<String> idDossiers) {
        PropertyUtil.setProperty(
            document,
            DossierConstants.ALLOTISSEMENT_SCHEMA,
            DossierConstants.ALLOTISSEMENT_IDDOSSIERS_PROPERTY,
            idDossiers
        );
    }

    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(
            document,
            DossierConstants.ALLOTISSEMENT_SCHEMA,
            DossierConstants.ALLOTISSEMENT_NOM_PROPERTY
        );
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(
            document,
            DossierConstants.ALLOTISSEMENT_SCHEMA,
            DossierConstants.ALLOTISSEMENT_NOM_PROPERTY,
            nom
        );
    }
}
