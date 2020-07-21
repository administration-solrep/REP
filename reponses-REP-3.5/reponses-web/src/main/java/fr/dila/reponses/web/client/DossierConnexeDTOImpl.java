package fr.dila.reponses.web.client;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.core.client.AbstractMapDTO;

public class DossierConnexeDTOImpl extends AbstractMapDTO implements DossierConnexeDTO {

	private static final long	serialVersionUID	= 1L;

	public DossierConnexeDTOImpl() {
	}

	@Override
	public String getAuteur() {
		return getString(AUTEUR);
	}

	@Override
	public void setAuteur(String auteur) {
		put(AUTEUR, auteur);
	}

	@Override
	public String getMotsCles() {
		return getString(MOTS_CLES);
	}

	@Override
	public void setMotsCles(String motscles) {
		put(MOTS_CLES, motscles);

	}

	public String getDocIdForSelection() {
		return getString(DOC_ID_FOR_SELECTION);
	}

	public void setDocIdForSelection(String docIdForSelection) {
		put(DOC_ID_FOR_SELECTION, docIdForSelection);
	}

	@Override
	public String getNumeroDossier() {
		return getString(NUMERO_DOSSIER);
	}

	@Override
	public void setNumeroDossier(String numeroDossier) {
		put(NUMERO_DOSSIER, numeroDossier);
	}

	@Override
	public String getEtat() {
		return getString(ETAT);
	}

	@Override
	public void setEtat(String etat) {
		put(ETAT, etat);
	}

	@Override
	public Boolean isCheckable() {
		return getBoolean(IS_CHECKABLED);
	}

	@Override
	public void setCheckable(Boolean checkabled) {
		put(IS_CHECKABLED, checkabled);
	}

	@Override
	public String getQuestionId() {
		return getString(QUESTION_ID);
	}

	@Override
	public void setQuestionid(String questionId) {
		put(QUESTION_ID, questionId);
	}

	@Override
	public String getType() {
		return DossierConstants.QUESTION_DOCUMENT_TYPE;
	}

	@Override
	public void mapFields(Question question, Boolean disabledCheck, CoreSession documentManager) {
		setQuestionid(question.getDocument().getId());
		setDocIdForSelection(question.getDocument().getId());
		setNumeroDossier(question.getOrigineQuestion() + " " + question.getNumeroQuestion());
		setAuteur(question.getNomCompletAuteur());
		setEtat(question.getEtatQuestion(documentManager).getNewState());
		setMotsCles(question.getMotsCles());
		setCheckable(disabledCheck);
	}

}
