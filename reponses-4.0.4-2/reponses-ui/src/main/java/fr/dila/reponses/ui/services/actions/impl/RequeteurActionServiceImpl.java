package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.ui.services.actions.RequeteurActionService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STAclConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

public class RequeteurActionServiceImpl implements RequeteurActionService {
    private static final STLogger LOGGER = STLogFactory.getLog(RequeteurActionService.class);

    private static final String SMART_FOLDER = STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE;

    public RequeteExperte saveQueryAsRequeteExperte(SpecificContext context, String queryPart) {
        try {
            DocumentModel doc = context.getSession().createDocumentModel(SMART_FOLDER);
            RequeteExperte requeteExperte = doc.getAdapter(RequeteExperte.class);
            requeteExperte.setWhereClause(queryPart);
            return requeteExperte;
        } catch (Exception e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_UPDATE_REQUETE_TEC,
                "Erreur ! Le requêteur a été incapable de sauvegarder à jour la requête ",
                e
            );
            context
                .getMessageQueue()
                .addErrorToQueue("Votre requête n'a pas pu être sauvegardée, veuillez recommencer.");
        }
        return null;
    }

    public DocumentModel updateRequeteExperte(SpecificContext context, DocumentModel doc, String queryPart) {
        try {
            RequeteExperte requeteExperte = doc.getAdapter(RequeteExperte.class);
            requeteExperte.setWhereClause(queryPart);
            return context.getSession().saveDocument(doc);
        } catch (Exception e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_UPDATE_REQUETE_TEC,
                "Erreur ! Le requêteur a été incapable de mettre à jour la requête ",
                e
            );
            context
                .getMessageQueue()
                .addErrorToQueue("Votre requête n'a pas pu être mise à jour, veuillez recommencer.");
        }
        return null;
    }

    public DocumentModel createRequete(SpecificContext context, DocumentModel doc) {
        try {
            SecurityService service = STServiceLocator.getSecurityService();
            SSPrincipal ssPrincipal = (SSPrincipal) context.getSession().getPrincipal();
            service.addAceToAcl(doc, ACL.LOCAL_ACL, ssPrincipal.getName(), SecurityConstants.EVERYTHING);
            return doc;
        } catch (Exception e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.DEFAULT,
                "Le requêteur n'a pas été capable de créer la requête"
            );
            context.getMessageQueue().addErrorToQueue("Le requêteur n'a pas pu créer votre requête");
        }
        return null;
    }

    public String getFullQuery(String where) {
        JointureService jointureService = STServiceLocator.getJointureService();
        QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(where);
        return assembler.getFullQuery();
    }

    public String getFullQuery(CoreSession session, RequeteExperte req) {
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        String query = requeteurService.getPattern(session, req);

        // Remplacement du paramètre de la législature courante
        if (query.contains(":legislatureCourante")) {
            String legislatureCourante = STServiceLocator
                .getSTParametreService()
                .getParametreValue(session, ReponsesParametreConstant.LEGISLATURE_COURANTE);
            query = query.replace(":legislatureCourante", legislatureCourante);
        }

        return query;
    }

    public void publish(SpecificContext context, DocumentModel doc) {
        try {
            SecurityService service = STServiceLocator.getSecurityService();
            service.addAceToSecurityAcl(doc, SecurityConstants.EVERYONE, SecurityConstants.READ);
            doc.followTransition("approve");
        } catch (Exception e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.DEFAULT,
                "La publication de la requete dans l'espace public a echoué",
                e
            );
            context.getMessageQueue().addErrorToQueue("erreur.requeteExperte.publication.failed");
            return;
        }
        context.getMessageQueue().addInfoToQueue("info.requeteExperte.publication.succeeded");
    }

    public void unpublish(SpecificContext context, DocumentModel doc) {
        try {
            ACP acp = doc.getACP();
            acp.removeACL(STAclConstant.ACL_SECURITY);
            doc.followTransition("backToProject");
        } catch (Exception e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.DEFAULT,
                "La publication de la requete dans l'espace public a echoué",
                e
            );
            context.getMessageQueue().addErrorToQueue("erreur.requeteExperte.publication.failed");
            return;
        }
        context.getMessageQueue().addInfoToQueue("info.requeteExperte.unpublish.succeeded");
    }

    public DocumentModel getBibliothequeStandard(CoreSession session) {
        PathRef biblioPath = new PathRef(RequeteConstants.BIBLIO_REQUETES_ROOT);
        return session.getDocument(biblioPath);
    }

    public boolean canPublish(CoreSession session, DocumentModel doc) {
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        final List<String> groups = ssPrincipal.getGroups();
        boolean canPublish = groups.contains(ReponsesBaseFunctionConstant.REQUETE_PUBLISHER);
        // Les requêtes crées par le systeme ne sont pas publiables :
        boolean isSystemRequete = STConstant.NUXEO_SYSTEM_USERNAME.equals(DublincoreSchemaUtils.getCreator(doc));
        return canPublish && !isSystemRequete;
    }

    public boolean canDelete(CoreSession session, DocumentModel doc) {
        if (doc == null) {
            return false;
        } else {
            return session.hasPermission(doc.getRef(), SecurityConstants.REMOVE);
        }
    }

    public boolean canEdit(CoreSession session, DocumentModel doc) {
        if (doc == null) {
            return false;
        } else {
            return session.hasPermission(doc.getRef(), SecurityConstants.WRITE);
        }
    }
}
