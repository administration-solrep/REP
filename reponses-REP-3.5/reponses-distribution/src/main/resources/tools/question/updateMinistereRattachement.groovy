import java.util.HashMap;
import java.util.List;
import java.lang.StringBuilder;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import fr.dila.reponses.api.cases.Question;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.reponses.api.constant.DossierConstants
import fr.dila.reponses.api.constant.ReponsesParametreConstant;


/**
 * script groovy de remplissage de ministere de rattachement pour les questions ecrites de la législature courante
 * 
 */

print "Début du script groovy de remplissage de ministere de rattachement pour les questions ecrites de la législature courante";
print "-------------------------------------------------------------------------------";

        String legislatureValue = STServiceLocator.getSTParametreService().getParametreValue(Session, ReponsesParametreConstant.LEGISLATURE_COURANTE);

        Long legislature = null;
        try {
            legislature = Long.parseLong(legislatureValue);
        } catch (Exception e) {
            throw new ClientException("La législature courante n'est pas paramétrée correctement.", e);
        }
        
        String type = "QE";

        StringBuilder sb = new StringBuilder("SELECT d.ecm:uuid as id FROM ");
        sb.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
        sb.append(" as d WHERE d.");
        sb.append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(DossierConstants.DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY);
        sb.append(" = 'QE' AND d.");
        sb.append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(DossierConstants.DOSSIER_LEGISLATURE_QUESTION);
        sb.append(" = " + legislature);

        Long count = QueryUtils.doCountQuery(Session, QueryUtils.ufnxqlToFnxqlQuery(sb.toString()), null);
        print count + " questions trouvees";
        long limit = (long) 100;

        for (Long offset = (long) 0; offset <= count; offset += limit) {
            Long borne = (count<offset+limit?count:offset+limit);
            print "Récupération questions de " + offset + " à " + borne;
            List<DocumentModel> dossiers = QueryUtils.doUFNXQLQueryAndFetchForDocuments(Session, sb.toString(), null, limit, offset);
            for (DocumentModel documentModel : dossiers) {
                Question question = documentModel.getAdapter(Question.class);
                String idMinistere = question.getIdMinistereAttributaire();
                if (idMinistere != null) {
                    OrganigrammeNode ministereNode = STServiceLocator.getOrganigrammeService().getEntiteNode(idMinistere);
                    if (ministereNode != null && ministereNode.isActive()) {
                        question.setIdMinistereRattachement(ministereNode.getId());
                        question.setIntituleMinistereRattachement(ministereNode.getLabel());
                        Session.saveDocument(question.getDocument());
                        print "question " + question.getDocument().getTitle() + " is updated";
                    }
                }
            }
            print "Fin traitement questions de " + offset + " à " + borne;
        }
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de remplissage de ministere de rattachement pour les questions ecrites de la législature courante";
return "Fin du script groovy";
