package fr.dila.reponses.core.recherche.traitement;

import static fr.dila.reponses.api.constant.RequeteConstants.DATE_DEBUT_ARBITRAIRE;
import static fr.dila.reponses.api.constant.RequeteConstants.FORMAT_DATE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.RequeteTraitement;
import fr.dila.st.core.util.PropertyUtil;

public class DateIntervalleTraitement implements RequeteTraitement<Requete> {

    private String schema, dateDebut, dateFin;

    private static final Log log = LogFactory.getLog(DateIntervalleTraitement.class);

    public DateIntervalleTraitement(String schema, String dateDebut, String dateFin) {
        this.schema = schema;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    @Override
    public void doBeforeQuery(Requete requete) {
        DocumentModel document = requete.getDocument();
        Calendar calDebut = PropertyUtil.getCalendarProperty(document, schema, dateDebut);
        Calendar calFin = PropertyUtil.getCalendarProperty(document, schema, dateFin);
        // Si les 2 dates sont nulles, on ne fait rien
        if (calDebut == null && calFin == null) {
            return;
        }
        // Si l'une des deux dates est nulle, on l'initialise avec une date arbitraire.
        if (calDebut != null || calFin != null) {
            if (calDebut == null) {
                calDebut = GregorianCalendar.getInstance();
                calDebut.setTime(getDateDebutArbitraire());
            }
            if (calFin == null) {
                calFin = GregorianCalendar.getInstance();
                calFin.setTime(getDateFinArbitraire());
            }
        }
        PropertyUtil.setProperty(document, schema, dateDebut, calDebut);
        PropertyUtil.setProperty(document, schema, dateFin, calFin);
    }

    @Override
    public void doAfterQuery(Requete requete) {
        return;
    }

    @Override
    public void init(Requete requete) {
        return;
    }

    protected Date getDateFinArbitraire() {
        Date dateFinArbitraire = new Date();
        return dateFinArbitraire;
    }

    protected Date getDateDebutArbitraire() {
        Date dateDebutArbitraire = null;
        try {
            dateDebutArbitraire = new SimpleDateFormat(FORMAT_DATE).parse(DATE_DEBUT_ARBITRAIRE);
        } catch (ParseException e) {
            log.warn("Mauvais format pour la date de début arbitraire", e);
        }
        return dateDebutArbitraire;
    }

}
