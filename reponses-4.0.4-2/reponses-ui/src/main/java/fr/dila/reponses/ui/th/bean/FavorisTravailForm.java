package fr.dila.reponses.ui.th.bean;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBean;
import java.util.Calendar;
import javax.ws.rs.FormParam;

@SwBean
public class FavorisTravailForm {
    @FormParam("id")
    private String id;

    @FormParam("nomFavoris")
    private String nomFavoris;

    @FormParam("dateFin")
    private String dateFin;

    @FormParam("idDossiers")
    private String idDossiers;

    public FavorisTravailForm() {
        super();
        this.dateFin = setDateSeven();
    }

    public FavorisTravailForm(String id, String nomFavoris, String date) {
        super();
        this.id = id;
        this.nomFavoris = nomFavoris;
        this.dateFin = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomFavoris() {
        return nomFavoris;
    }

    public void setNomFavoris(String nomFavoris) {
        this.nomFavoris = nomFavoris;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String date) {
        this.dateFin = date;
    }

    public String getIdDossiers() {
        return idDossiers;
    }

    public void setIdDossiers(String idDossiers) {
        this.idDossiers = idDossiers;
    }

    private String setDateSeven() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        return SolonDateConverter.DATE_SLASH.format(cal);
    }
}
