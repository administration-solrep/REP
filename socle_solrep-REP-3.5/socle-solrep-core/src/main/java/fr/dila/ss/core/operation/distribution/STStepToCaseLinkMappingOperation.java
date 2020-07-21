package fr.dila.ss.core.operation.distribution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.caselink.CaseLinkType;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Cette opération permet de renseigner les métadonnées des DossierLink lors de la distribution, à partir des données de
 * l'étape.
 * 
 * @author jtremeaux
 */
@Operation(id = STStepToCaseLinkMappingOperation.OP_ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "Step To CaseLink Mapping", description = "Create a CaseLink from the value of the Step docuemnt")
public class STStepToCaseLinkMappingOperation {

	public static final String	STEP_PREFIX	= "Step:";

	public static final String	CASE_PREFIX	= "Case:";

	public static final String	OP_ID		= "ST.Distribution.StepToCaseLinkMapping";

	@Context
	protected OperationContext	context;

	@Param(name = "actionnable")
	protected boolean			actionnable	= false;

	@Param(name = "mappingProperties")
	protected Properties		mappingProperties;

	@Param(name = "leavingChainsProperties", required = false)
	protected Properties		leavingChainsProperties;

	/**
	 * Default constructor
	 */
	public STStepToCaseLinkMappingOperation() {
		// do nothing
	}

	@OperationMethod
	public void mapCaseLinkOperation() throws ClientException {
		@SuppressWarnings("unchecked")
		final List<CaseLink> links = (List<CaseLink>) context.get(CaseConstants.OPERATION_CASE_LINKS_KEY);
		final CoreSession session = context.getCoreSession();

		for (CaseLink link : links) {
			mapCaseLink(session, link);
		}
	}

	/**
	 * Initialise les données d'un DossierLink
	 * 
	 * @param session
	 *            Session
	 * @param link
	 *            DossierLink
	 * @throws ClientException
	 */
	protected void mapCaseLink(final CoreSession session, final CaseLink link) throws ClientException {
		link.setActionnable(actionnable);

		// Charge le dossier
		DocumentModel dossierDoc = link.getCase(session).getDocument();

		DocumentModel linkDoc = link.getDocument();
		DocumentRouteStep step = (DocumentRouteStep) context.get(DocumentRoutingConstants.OPERATION_STEP_DOCUMENT_KEY);
		DocumentModel stepDoc = step.getDocument();
		Map<String, List<String>> recipients = new HashMap<String, List<String>>();
		String recipient = RoutingTaskSchemaUtils.getMailboxId(step.getDocument());
		recipients.put(CaseLinkType.FOR_ACTION.name(), Arrays.asList(new String[] { recipient }));

		if (recipient != null) {
			link.addInitialInternalParticipants(recipients);
		}

		for (Map.Entry<String, String> prop : mappingProperties.entrySet()) {
			String getter = prop.getKey();
			String setter = prop.getValue();
			DocumentModel setterDoc = null;
			if (setter.startsWith(CASE_PREFIX)) {
				setterDoc = dossierDoc;
				setter = setter.substring(CASE_PREFIX.length());
			} else if (setter.startsWith(STEP_PREFIX)) {
				setterDoc = stepDoc;
				setter = setter.substring(STEP_PREFIX.length());
			} else {
				throw new UnsupportedOperationException("No support for prefix of value : " + setter);
			}
			linkDoc.setPropertyValue(getter, setterDoc.getPropertyValue(setter));
		}

		if (link.isActionnable()) {
			ActionableCaseLink actionableLink = link.getDocument().getAdapter(ActionableCaseLink.class);
			String refuseChainId = leavingChainsProperties.get("refuse");
			String validateChainId = leavingChainsProperties.get("validate");
			actionableLink.setRefuseOperationChainId(refuseChainId);
			actionableLink.setValidateOperationChainId(validateChainId);
			actionableLink.setStepId(stepDoc.getId());
		}

		// pour les logs
		final STDossierLink stDossierLink = link.getDocument().getAdapter(STDossierLink.class);
		final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
		final STRouteStep routeStep = stepDoc.getAdapter(STRouteStep.class);
		final String routingTaskType = routeStep.getType();

		if (StringUtils.isNotBlank(routingTaskType)) {
			// pas de requete si vide
			final String routingTaskLabel = vocabularyService.getEntryLabel(
					STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY, routingTaskType);
			stDossierLink.setRoutingTaskLabel(routingTaskLabel);
		}

		stDossierLink.setRoutingTaskType(routingTaskType);
	}
}
