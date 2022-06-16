package fr.dila.reponses.api.cases.flux;

import java.util.GregorianCalendar;

public interface QErratum {
    public GregorianCalendar getDatePublication();

    public void setDatePublication(GregorianCalendar datePublication);

    public Integer getPageJo();

    public void setPageJo(Integer pageJo);

    public String getTexteConsolide();

    public void setTexteConsolide(String texteConsolide);

    public String getTexteErratum();

    public void setTexteErratum(String texteErratum);
}
