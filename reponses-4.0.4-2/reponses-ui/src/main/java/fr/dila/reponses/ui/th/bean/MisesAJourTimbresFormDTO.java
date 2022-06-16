package fr.dila.reponses.ui.th.bean;

import fr.dila.reponses.ui.bean.MiseAJourTimbresDetailDTO;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class MisesAJourTimbresFormDTO {
    @FormParam("briserToutesSignatures")
    private boolean briserToutesSignatures;

    @FormParam("migrerTousDossiersClos")
    private boolean migrerTousDossiersClos;

    @FormParam("details[]")
    private ArrayList<MiseAJourTimbresDetailDTO> details = new ArrayList<>();

    public MisesAJourTimbresFormDTO() {}

    public boolean getBriserToutesSignatures() {
        return briserToutesSignatures;
    }

    public void setBriserToutesSignatures(boolean briserToutesSignatures) {
        this.briserToutesSignatures = briserToutesSignatures;
    }

    public boolean getMigrerTousDossiersClos() {
        return migrerTousDossiersClos;
    }

    public void setMigrerTousDossiersClos(boolean migrerTousDossierClos) {
        this.migrerTousDossiersClos = migrerTousDossierClos;
    }

    public ArrayList<MiseAJourTimbresDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<MiseAJourTimbresDetailDTO> details) {
        this.details = details;
    }
}
