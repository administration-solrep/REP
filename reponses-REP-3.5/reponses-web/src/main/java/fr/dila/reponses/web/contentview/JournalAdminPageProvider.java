package fr.dila.reponses.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.web.contentview.STJournalAdminPageProvider;

/**
 * page provider du journal affiche dans l'espace d'administration
 * 
 * @author BBY, ARN
 * @author asatre
 * 
 */
public class JournalAdminPageProvider extends STJournalAdminPageProvider {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Récupération des identifiants du dossier à partir d'un numéro
	 */
	@Override
	protected void getDossierIdsList() throws ClientException {
		Map<String, Serializable> props = getProperties();
		// récupération de la session
		CoreSession coreSession = (CoreSession) props.get(CORE_SESSION_PROPERTY);
		if (coreSession == null) {
			throw new ClientRuntimeException("cannot find core session");
		}

		// récupération des identifiants du dossier à partir de la question
		String numeroQuestion = (String) getParameters()[4];

		// ajout du filtre sur la référence du dossier
		if (StringUtils.isNotEmpty(numeroQuestion)) {
			// Vérification qu'il s'agit bien d'un dossier composé de chiffres
			Pattern numericPattern = Pattern.compile("[0-9]+");
			Matcher numericMatcher = numericPattern.matcher(numeroQuestion);
			if (numericMatcher.matches()) {

				// on récupère les identifiants de dossier qui le numéro de question indiqué
				DossierDistributionService dossierDistributionservice = ReponsesServiceLocator
						.getDossierDistributionService();
				DocumentModelList dossierList = dossierDistributionservice.getDossierFrom(coreSession, numeroQuestion);
				if (dossierList == null || dossierList.isEmpty()) {
					dossierIdList = null;
				} else {
					dossierIdList = new ArrayList<String>();
					for (DocumentModel documentModel : dossierList) {
						dossierIdList.add(documentModel.getId());
					}
				}
			} else {
				dossierIdList = null;
			}
		}
	}
}
