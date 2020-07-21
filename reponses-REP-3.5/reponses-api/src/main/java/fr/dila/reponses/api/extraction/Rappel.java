package fr.dila.reponses.api.extraction;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"id_question", "legislature"})
public class Rappel {
	
	private int id_question;
	private int legislature;
	
	public Rappel(){
		super();
	}

	public Rappel(int id_question, int legislature) {
		super();
		this.id_question = id_question;
		this.legislature = legislature;
	}

	public int getId_question() {
		return id_question;
	}

	public void setId_question(int id_question) {
		this.id_question = id_question;
	}

	public int getLegislature() {
		return legislature;
	}

	public void setLegislature(int legislature) {
		this.legislature = legislature;
	}
	
	

}
