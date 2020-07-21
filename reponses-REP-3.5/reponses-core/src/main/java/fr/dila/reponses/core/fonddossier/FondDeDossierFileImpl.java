package fr.dila.reponses.core.fonddossier;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFileImpl;

public class FondDeDossierFileImpl extends SSFondDeDossierFileImpl implements FondDeDossierFile {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FondDeDossierFileImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public void setNumeroVersion(String numeroVersion) {
        setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION, numeroVersion);
    }

    @Override
    public String getNumeroVersion() {
        return getStringProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION);
    }

    @Override
    public void setNiveauVisibilite(String niveauVisibilite) {
        setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, niveauVisibilite);
    }

    @Override
    public String getNiveauVisibilite() {
        return getStringProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE);
    }

    @Override
    public void setMinistereAjoute(String ministereAjoute) {
        setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA, ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
                ministereAjoute);
    }

    @Override
    public String getMinistereAjoute() {
        return getStringProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA, ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE);
    }
}
