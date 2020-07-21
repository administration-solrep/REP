import java.util.HashMap;
import java.util.List;
import java.lang.StringBuilder;
import org.apache.commons.lang.StringUtils;

import javax.transaction.Status;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.st.api.domain.ComplexeType;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants
import fr.dila.reponses.api.constant.ReponsesParametreConstant;


/**
 * script groovy de remplissage de correctif du ministere attributaire pour les questions ecrites de la législature courante
 * 
 */
 
class TransactionUtils {
    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}
 
class FixUtils {    
    public static final String MODE_INFO = "INFO";
    public static final String MODE_FIX = "FIX";
 	
 	public static boolean fixMinistereAttributaire(final CoreSession session, final Question question, final Dossier dossier) {
 		// Récupération de la dernière attribution
		List <ComplexeType> listHistoriqueAttribution = dossier.getHistoriqueAttribution();
		ComplexeType histo = listHistoriqueAttribution.get(listHistoriqueAttribution.size()-1);
		String idMinistereAttributaireReel = (String) histo.getSerializableMap().get(DossierConstants.CT_MIN_ATTRIBUTION);
		if ( question.getIdMinistereAttributaire().equals(idMinistereAttributaireReel)) {
			// Dans ce cas il faut uniquement changer le ministere attributaire dans le dossier
			dossier.setIdMinistereAttributaireCourant(idMinistereAttributaireReel);
			session.saveDocument(dossier.getDocument());
			print "question " + question.getSourceNumeroQuestion() + " corrigée";
			return true;
		} else if ( dossier.getIdMinistereAttributaireCourant().equals(idMinistereAttributaireReel) ) {
			// Dans ce cas il faut changer l'id dans la question et l'intitulé
			OrganigrammeNode ministereNode = STServiceLocator.getOrganigrammeService().getEntiteNode(idMinistereAttributaireReel);
			if (ministereNode != null && ministereNode.isActive()) {
				question.setIdMinistereAttributaire(ministereNode.getId());
				question.setIntituleMinistereAttributaire(ministereNode.getLabel());
				session.saveDocument(question.getDocument());
				print "question " + question.getSourceNumeroQuestion() + " corrigée.";
				return true;
			} else {
				// Dans ce cas on ne peut pas corriger la question car il manque des informations
				if (ministereNode == null) {
					print "Impossible de corriger la question " + question.getSourceNumeroQuestion() + " : le ministère attributaire de la question n'est rattaché à aucun ministère (" + idMinistereAttributaireReel + ")";
				} else {
					print "Impossible de corriger la question " + question.getSourceNumeroQuestion() + " : le ministère attributaire n'est pas actif (" + ministereNode.getLabel() + ")";
				}
			}
		} else {
			// Cas qui ne doit pas se produire
			print "Impossible de corriger la question " + question.getSourceNumeroQuestion() + " : ministère attributaire non établi";
			return false;
		}
 	}
}

print "Début du script groovy de correctif du ministere attributaire pour les questions ecrites de la législature courante";
print "-------------------------------------------------------------------------------";

		String mode = Context.get("mode");
		if(StringUtils.isBlank(mode)) {
			print "Argument mode non trouvé. mode par défaut : 'INFO' ";
		    mode = FixUtils.MODE_INFO;    
		}
		mode = mode.replace("'", "");
		if (!FixUtils.MODE_INFO.equals(mode) && !FixUtils.MODE_FIX.equals(mode)) {
            print "Mode non reconnu - Vous devez spécifier : -ctx \"mode='INFO'|'FIX'\" "
        } else {
		
	        String legislatureValue = STServiceLocator.getSTParametreService().getParametreValue(Session, ReponsesParametreConstant.LEGISLATURE_COURANTE);
	
	        Long legislature = null;
	        try {
	            legislature = Long.parseLong(legislatureValue);
	        } catch (Exception e) {
	            throw new ClientException("La législature courante n'est pas paramétrée correctement.", e);
	        }
	        
	        String type = "QE";
	
	        StringBuilder sb = new StringBuilder("SELECT q.ecm:uuid as id FROM ");
	        sb.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
	        sb.append(" as q WHERE q.");
	        sb.append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX);
	        sb.append(":");
	        sb.append(DossierConstants.DOSSIER_REPONSES_LINK_TYPE_QUESTION_PROPERTY);
	        sb.append(" = 'QE' AND q.");
	        sb.append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX);
	        sb.append(":");
	        sb.append(DossierConstants.DOSSIER_LEGISLATURE_QUESTION);
	        sb.append(" = " + legislature);
	
	        Long count = QueryUtils.doCountQuery(Session, QueryUtils.ufnxqlToFnxqlQuery(sb.toString()), null);
	        print count + " questions de la législature";
	        long limit = (long) 100;
	        long totalCorrige = (long) 0;
	        long totalATraiter = (long) 0;
	
	        for (Long offset = (long) 0; offset <= count; offset += limit) {
		        if (!TransactionUtils.isTransactionAlive()) {
	                TransactionHelper.startTransaction();
	            }
	            Long borne = (count<offset+limit?count:offset+limit);
	            //print "Récupération questions de " + offset + " à " + borne;
	            List<DocumentModel> dossiers = QueryUtils.doUFNXQLQueryAndFetchForDocuments(Session, sb.toString(), null, limit, offset);
	            for (DocumentModel documentModel : dossiers) {
	                Question question = documentModel.getAdapter(Question.class);
					DocumentModel dossierDoc = Session.getDocument(question.getDossierRef());
					Dossier dossier = dossierDoc.getAdapter(Dossier.class);
	                if (question.getIdMinistereAttributaire() != dossier.getIdMinistereAttributaireCourant() ) {
						print "La question " + question.getSourceNumeroQuestion() + " présente une incohérence.";
						totalATraiter++;
						if (FixUtils.MODE_FIX.equals(mode)) {
							if (FixUtils.fixMinistereAttributaire(Session,question, dossier)) {
								totalCorrige++;
							}
						}
					}
	            }
	            // Sauvegarde de la transaction en cours
	            Session.save();
            	TransactionHelper.commitOrRollbackTransaction();
	            //print "Fin traitement questions de " + offset + " à " + borne;
	        }
	        print "Fin traitement : " + totalCorrige + " question(s) corrigée(s) sur " + totalATraiter + " question(s) à traiter. ";
	    }
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de correctif du ministere attributaire pour les questions ecrites de la législature courante";
return "Fin du script groovy";
