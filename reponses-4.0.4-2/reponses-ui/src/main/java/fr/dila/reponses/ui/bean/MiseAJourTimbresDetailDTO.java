package fr.dila.reponses.ui.bean;

import org.apache.commons.lang3.StringUtils;

public class MiseAJourTimbresDetailDTO {

    public MiseAJourTimbresDetailDTO() {
        super();
    }

    private String oldMinistere = StringUtils.EMPTY;

    private String newMinistere = StringUtils.EMPTY;

    private String oldMinistereId = StringUtils.EMPTY;

    private String newMinistereId = StringUtils.EMPTY;

    private Boolean briserSignature = false;
    private Boolean migrerDossiersClos = false;

    public String getOldMinistere() {
        return oldMinistere;
    }

    public void setOldMinistere(String oldMinistere) {
        this.oldMinistere = oldMinistere;
    }

    public String getNewMinistere() {
        return newMinistere;
    }

    public void setNewMinistere(String newMinistere) {
        this.newMinistere = newMinistere;
    }

    public String getOldMinistereId() {
        return oldMinistereId;
    }

    public void setOldMinistereId(String oldMinistereId) {
        this.oldMinistereId = oldMinistereId;
    }

    public String getNewMinistereId() {
        return newMinistereId;
    }

    public void setNewMinistereId(String newMinistereId) {
        this.newMinistereId = newMinistereId;
    }

    public Boolean getBriserSignature() {
        return briserSignature;
    }

    public void setBriserSignature(Boolean briserSignature) {
        this.briserSignature = briserSignature;
    }

    public Boolean getMigrerDossiersClos() {
        return migrerDossiersClos;
    }

    public void setMigrerDossiersClos(Boolean migrerDossiersClos) {
        this.migrerDossiersClos = migrerDossiersClos;
    }
}
