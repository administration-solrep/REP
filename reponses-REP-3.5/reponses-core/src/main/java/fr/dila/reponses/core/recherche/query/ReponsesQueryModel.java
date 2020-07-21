package fr.dila.reponses.core.recherche.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.search.api.client.querymodel.QueryModelService;
import org.nuxeo.ecm.core.search.api.client.querymodel.descriptor.QueryModelDescriptor;

import fr.dila.reponses.api.constant.RequeteConstants;

/**
 * 
 * Classe utilitaire pour donner la clause WHERE d'un model nuxeo.
 * @author jgomez
 *
 */
@SuppressWarnings("deprecation")
public class ReponsesQueryModel {
    
    /**
     * Retourne la clause d'un model Nuxeo.
     * @param qmService
     * @param model
     * @param requeteModelName
     * @return
     * @throws ClientException
     */
    public String getRequetePart(QueryModelService qmService, DocumentModel model,String requeteModelName) throws ClientException {
        QueryModelDescriptor qmDesc = qmService.getQueryModelDescriptor(requeteModelName);
        if (qmDesc == null){
            return StringUtils.EMPTY;
        }
        String query = qmDesc.getQuery(model);
        return query.replace("SELECT * FROM Document" , StringUtils.EMPTY).replace(" WHERE ",StringUtils.EMPTY);
    }

    /**
     * Retourne les clauses de tous les models donnés en argument, liées par un AND (et avec des parenthèses).
     * @param qmService
     * @param model
     * @param modelNames
     * @return
     * @throws ClientException
     */
    public String getAndRequeteParts(QueryModelService qmService, DocumentModel model,String... modelNames) throws ClientException {
        List<String> clauses = new ArrayList<String>();
        for (String modelName : modelNames) {
            StringBuffer bf = new StringBuffer(StringUtils.EMPTY);
            // Cas spécial pour le cas du model RequeteIndexTous qui n'existe pas, mais qui est 
            // constitué des models requeteIndex et requeteIndexCompl séparés par un OR.
            if (RequeteConstants.PART_REQUETE_INDEX_TOUS.equals(modelName)){
                bf.append("(");
                bf.append(getRequetePart(qmService,model,RequeteConstants.PART_REQUETE_INDEX_ORIGINE));
                bf.append(" OR ");
                bf.append(getRequetePart(qmService,model,RequeteConstants.PART_REQUETE_INDEX_COMPL));
                bf.append(")");
            }
            else{
                String requetePart = getRequetePart(qmService,model,modelName);
                if (RequeteConstants.PART_REQUETE_SIMPLE.equals(modelName) && !StringUtils.EMPTY.equals(requetePart)){
                    requetePart = "(" + requetePart + ")";
                }
                bf.append(requetePart);
            }
            if (!StringUtils.isBlank(bf.toString())){
                clauses.add( "(" + bf.toString() + ")");
            }
        }
        return StringUtils.join(clauses, " AND ");
    }

}
