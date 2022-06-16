package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.core.user.ProfilUtilisateurImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet ProfilUtilisateur
 *
 * @author SPL
 */

public class ProfilUtilisateurAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA);
        return new ProfilUtilisateurImpl(doc);
    }
}
