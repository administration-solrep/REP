package fr.dila.reponses.ui.bean;

import fr.dila.ss.ui.bean.SSHistoriqueMigrationDTO;

public class ReponsesHistoriqueMigrationDTO extends SSHistoriqueMigrationDTO {
    private String directionPilote;

    public String getDirectionPilote() {
        return directionPilote;
    }

    public void setDirectionPilote(String directionPilote) {
        this.directionPilote = directionPilote;
    }
}
