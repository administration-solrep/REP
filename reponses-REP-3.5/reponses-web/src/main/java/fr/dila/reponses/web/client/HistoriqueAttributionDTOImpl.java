package fr.dila.reponses.web.client;

import java.util.Calendar;
import java.util.Date;

public class HistoriqueAttributionDTOImpl implements HistoriqueAttributionDTO{
	
	private String minAttribution;
	private Date dateAttribution;
	private String typeAttribution;
	
	public HistoriqueAttributionDTOImpl(String minAttribution, Calendar dateAttribution, String typeAttribution) {
		this.minAttribution = minAttribution;
		this.dateAttribution = dateAttribution != null ? dateAttribution.getTime() : null;
		this.typeAttribution = typeAttribution;
	}

	@Override
	public String getMinAttribution() {
		return minAttribution;
	}

	@Override
	public void setMinAttribution(String minAttribution) {
		this.minAttribution = minAttribution;
	}

	@Override
	public Date getDateAttribution() {
		return dateAttribution;
	}

	@Override
	public void setDateAttribution(Date dateAttribution) {
		this.dateAttribution = dateAttribution;
	}

	@Override
	public String getTypeAttribution() {
		return typeAttribution;
	}

	@Override
	public void setTypeAttribution(String typeAttribution) {
		this.typeAttribution = typeAttribution;
	}

}
