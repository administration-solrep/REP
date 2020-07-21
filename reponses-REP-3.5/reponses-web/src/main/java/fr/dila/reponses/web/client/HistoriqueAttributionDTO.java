package fr.dila.reponses.web.client;

import java.util.Date;

public interface HistoriqueAttributionDTO {

	String getMinAttribution();

	void setMinAttribution(String minAttribution);

	Date getDateAttribution();

	void setDateAttribution(Date dateAttribution);

	String getTypeAttribution();

	void setTypeAttribution(String typeAttribution);
}
