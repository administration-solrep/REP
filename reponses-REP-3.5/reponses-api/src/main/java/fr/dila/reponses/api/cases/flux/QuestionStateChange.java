package fr.dila.reponses.api.cases.flux;

import java.io.Serializable;
import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface QuestionStateChange extends Serializable {

	public Calendar getChangeDate();

	public void setChangeDate(Calendar changeDate);

	public String getNewState();

	public void setNewState(String texteErratum);

	/**
	 * Récupère le DocumentModel
	 * 
	 * @return
	 */
	DocumentModel getDocument();

}
