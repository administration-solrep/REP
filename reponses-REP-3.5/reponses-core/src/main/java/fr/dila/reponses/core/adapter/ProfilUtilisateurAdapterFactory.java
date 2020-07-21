package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.core.user.ProfilUtilisateurImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet ProfilUtilisateur
 * 
 * @author SPL
 */

public class ProfilUtilisateurAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        return new ProfilUtilisateurImpl(doc);
    }
}
