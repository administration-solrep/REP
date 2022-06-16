package fr.dila.reponses.core.cases.flux;

import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class QErratumImpl implements QErratum {
    protected GregorianCalendar datePublication;

    protected Integer pageJo;

    protected String texteConsolide;

    protected String texteErratum;

    protected Map<String, Serializable> erratumMap;

    public QErratumImpl() {
        erratumMap = new HashMap<String, Serializable>();
    }

    public QErratumImpl(Map<String, Serializable> erratumMap) {
        if (erratumMap != null) {
            if (
                erratumMap.get(DossierConstants.DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY) instanceof GregorianCalendar
            ) {
                this.datePublication =
                    ((GregorianCalendar) erratumMap.get(DossierConstants.DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY));
            }

            if (erratumMap.get(DossierConstants.DOSSIER_ERRATUM_PAGE_JO_PROPERTY) instanceof Long) {
                this.pageJo = ((Long) erratumMap.get(DossierConstants.DOSSIER_ERRATUM_PAGE_JO_PROPERTY)).intValue();
            }

            if (erratumMap.get(DossierConstants.DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY) instanceof String) {
                this.texteErratum = ((String) erratumMap.get(DossierConstants.DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY));
            }

            if (erratumMap.get(DossierConstants.DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY) instanceof String) {
                this.texteConsolide =
                    ((String) erratumMap.get(DossierConstants.DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY));
            }
        }
    }

    @Override
    public GregorianCalendar getDatePublication() {
        return datePublication;
    }

    @Override
    public void setDatePublication(GregorianCalendar datePublication) {
        this.datePublication = datePublication;
        erratumMap.put(DossierConstants.DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY, datePublication);
    }

    @Override
    public Integer getPageJo() {
        return pageJo;
    }

    @Override
    public void setPageJo(Integer pageJo) {
        this.pageJo = pageJo;
        erratumMap.put(DossierConstants.DOSSIER_ERRATUM_PAGE_JO_PROPERTY, pageJo);
    }

    @Override
    public String getTexteConsolide() {
        return texteConsolide;
    }

    @Override
    public void setTexteConsolide(String texteConsolide) {
        this.texteConsolide = texteConsolide;
        erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY, texteConsolide);
    }

    @Override
    public String getTexteErratum() {
        return texteErratum;
    }

    @Override
    public void setTexteErratum(String texteErratum) {
        this.texteErratum = texteErratum;
        erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY, texteConsolide);
    }
}
