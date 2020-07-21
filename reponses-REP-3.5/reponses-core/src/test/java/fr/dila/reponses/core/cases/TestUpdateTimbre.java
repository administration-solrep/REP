package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.query.QueryUtils;

/**
 * @author asatre
 */
public class TestUpdateTimbre extends ReponsesRepositoryTestCase {
    
    public void testFieldEcriture() throws Exception{
    	openSession();
    	final StringBuilder sb = new StringBuilder(" SELECT d.ecm:uuid AS id, d.dos:");
    	sb.append(STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY);
    	sb.append(" FROM Dossier as d, Question as q ");
		sb.append(" WHERE d.dos:");
		sb.append(DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID);
		sb.append(" = q.ecm:uuid ");
		sb.append(" AND q.qu:");
		sb.append(DossierConstants.DOSSIER_TYPE_QUESTION);
		sb.append(" = ? ");
		sb.append(" AND d.dos:");
		sb.append(DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT);
		sb.append(" = ? ");
		sb.append(" AND ");
		sb.append("	exist(");
			sb.append(" select s.ecm:uuid from DocumentRouteStep AS s WHERE s.rtsk:documentRouteId = ");
			sb.append("	d.dos:");
			sb.append(STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY);
			sb.append("	AND s.");
			sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
			sb.append(" IN ('running', 'ready')");
            sb.append(") = 0 ");

		
		final Object[] params = new Object[]{
			VocabularyConstants.QUESTION_TYPE_QE,
			"6000002"};

		QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(sb.toString()), params);
    	closeSession();
    }

}