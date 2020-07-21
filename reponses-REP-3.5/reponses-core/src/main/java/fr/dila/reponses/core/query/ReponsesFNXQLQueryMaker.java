package fr.dila.reponses.core.query;

import java.util.HashMap;
import java.util.Map;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.FavoriDossierConstant;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.ss.api.constant.SSInfoUtilisateurConnectionConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.query.FlexibleQueryMaker;

public class ReponsesFNXQLQueryMaker extends FlexibleQueryMaker {

    private static final Map<String, String> mapTypeSchema = new HashMap<String, String>();

    static {
        mapTypeSchema.put(DossierConstants.QUESTION_DOCUMENT_TYPE, DossierConstants.QUESTION_DOCUMENT_SCHEMA);
        // SPL : pas d'optim pour la reponses car n'existe pas forcement pour un dossier
        // le fait de faire l'optim entraine une jointure forte entre question et reponse où il faudrait
        // une jointure externe, l'utilisation de HIERARCHY a ce comportement 'par effet de bord'
        // mapTypeSchema.put(DossierConstants.REPONSE_DOCUMENT_TYPE, DossierConstants.REPONSE_DOCUMENT_SCHEMA);
        mapTypeSchema.put(DossierConstants.DOSSIER_DOCUMENT_TYPE, DossierConstants.DOSSIER_SCHEMA);
        mapTypeSchema.put(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA);
        mapTypeSchema.put(STConstant.DELEGATION_DOCUMENT_TYPE, STSchemaConstant.DELEGATION_SCHEMA);
        mapTypeSchema.put(FavoriDossierConstant.FAVORIS_DOSSIER_DOCUMENT_TYPE, ReponsesSchemaConstant.FAVORI_DOSSIER_SCHEMA);

        // jeton
        mapTypeSchema.put(STConstant.JETON_DOC_TYPE, STConstant.JETON_DOC_SCHEMA);

        // allotissement
        mapTypeSchema.put(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE, DossierConstants.ALLOTISSEMENT_SCHEMA);
        
        // InfoUtilisateurConnection
        mapTypeSchema.put(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DOCUMENT_TYPE,
                SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA);
        
        // route step
        //mapTypeSchema.put(STConstant.ROUTE_STEP_DOCUMENT_TYPE, STSchemaConstant.ROUTING_TASK_SCHEMA);

    };

    public ReponsesFNXQLQueryMaker() {
        super(mapTypeSchema);
    }
}
