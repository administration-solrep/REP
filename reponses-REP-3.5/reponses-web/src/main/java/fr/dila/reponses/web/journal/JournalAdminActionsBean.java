package fr.dila.reponses.web.journal;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.web.journal.STJournalAdminActionsBean;

/**
 * ActionBean de gestion du journal de l'espace d'administration.
 * 
 * @author BBY, ARN
 */
@Name("journalAdminActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class JournalAdminActionsBean extends STJournalAdminActionsBean implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private static final Log	log					= LogFactory.getLog(JournalAdminActionsBean.class);

	@Override
	protected void initCategoryList() {
		setContentViewName(getDefaultContentViewName());
		// categorie d'opération : interface, feuille de route, bordereau , fond de dossier , parapheur, journal et
		// administration.
		String[] category = new String[] { "",
				// ReponsesEventConstant.CATEGORY_INTERFACE,
				STEventConstant.CATEGORY_FEUILLE_ROUTE, STEventConstant.CATEGORY_BORDEREAU,
				STEventConstant.CATEGORY_FDD, STEventConstant.CATEGORY_PARAPHEUR,
				// STEventConstant.CATEGORY_JOURNAL,
				STEventConstant.CATEGORY_ADMINISTRATION };
		categoryList.addAll(Arrays.asList(category));
	}

	/**
	 * On affiche le numéro de question du dossier à partir de son identifiant technqiue.
	 */
	@Override
	public String getDossierRef(String dossierRef) {
		// si la référence est nulle, on affiche la référence
		if (dossierRef == null || dossierRef.isEmpty()) {
			return dossierRef;
		}
		DocumentModel docModel;
		try {
			// on récupère le dossierà partir de son identifiant
			docModel = documentManager.getDocument(new IdRef(dossierRef));
		} catch (ClientException e) {
			log.debug("erreur lors de la récupération du dossier");
			return dossierRef;
		}
		if (docModel != null) {
			// on récupère le numéro de la quesiton
			Dossier dossier = docModel.getAdapter(Dossier.class);
			String refQuestion = "";
			Question appQuestion = dossier.getQuestion(documentManager);
			// M156851 - Ajout de la provenance de la question
			if (appQuestion != null && appQuestion.getOrigineQuestion() != null) {
				refQuestion = appQuestion.getOrigineQuestion();
			}
			if (dossier != null && dossier.getNumeroQuestion() != null) {
				refQuestion += refQuestion.isEmpty() ? dossier.getNumeroQuestion().toString() : " "
						+ dossier.getNumeroQuestion().toString();
			}
			if (!refQuestion.isEmpty()) {
				return refQuestion;
			}
		}
		log.debug("erreur lors de la récupération du dossier");
		return dossierRef;
	}
}
