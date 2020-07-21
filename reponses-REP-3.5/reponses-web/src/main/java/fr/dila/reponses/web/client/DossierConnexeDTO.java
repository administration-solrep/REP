package fr.dila.reponses.web.client;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Question;

public interface DossierConnexeDTO extends Map<String, Serializable> {

	public static final String	QUESTION_ID				= "questionId";
	public static final String	NUMERO_DOSSIER			= "numeroDossier";
	public static final String	AUTEUR					= "auteur";
	public static final String	ETAT					= "etat";
	public static final String	MOTS_CLES				= "motsCles";
	public static final String	IS_CHECKABLED			= "checkabled";

	/**
	 * correspond a la valeur a manipuler en tant qu'id de document lors de la selection sur la contentview
	 */
	public static final String	DOC_ID_FOR_SELECTION	= "docIdForSelection";

	String getNumeroDossier();

	void setNumeroDossier(String numeroDossier);

	String getDocIdForSelection();

	void setDocIdForSelection(String docIdForSelection);

	String getAuteur();

	void setAuteur(String auteur);

	String getEtat();

	void setEtat(String etat);

	String getMotsCles();

	void setMotsCles(String motsCles);

	Boolean isCheckable();

	void setCheckable(Boolean checkabled);

	String getQuestionId();

	void setQuestionid(String questionId);

	void mapFields(Question question, Boolean disabledCheck, CoreSession documentManager);

}
