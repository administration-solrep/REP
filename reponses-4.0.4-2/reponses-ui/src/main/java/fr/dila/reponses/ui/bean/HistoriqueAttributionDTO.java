package fr.dila.reponses.ui.bean;

import java.util.Calendar;
import java.util.Date;

public class HistoriqueAttributionDTO {
    private String minAttribution;
    private Date dateAttribution;
    private String typeAttribution;

    public HistoriqueAttributionDTO() {}

    public HistoriqueAttributionDTO(String minAttribution, Calendar dateAttribution, String typeAttribution) {
        this.minAttribution = minAttribution;
        this.dateAttribution = dateAttribution != null ? dateAttribution.getTime() : null;
        this.typeAttribution = typeAttribution;
    }

    public String getMinAttribution() {
        return minAttribution;
    }

    public void setMinAttribution(String minAttribution) {
        this.minAttribution = minAttribution;
    }

    public Date getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(Date dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public String getTypeAttribution() {
        return typeAttribution;
    }

    public void setTypeAttribution(String typeAttribution) {
        this.typeAttribution = typeAttribution;
    }
}
