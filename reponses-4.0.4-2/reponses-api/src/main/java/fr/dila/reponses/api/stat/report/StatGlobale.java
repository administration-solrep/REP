package fr.dila.reponses.api.stat.report;

import java.io.Serializable;

/** Une statistique globale */
public class StatGlobale implements Serializable {
    private static final long serialVersionUID = 1841922270473543848L;

    private Long nbRepondu = 0L;
    private Long nbQuestions = 0L;

    public Long getNbRepondu() {
        return nbRepondu;
    }

    public void setNbRepondu(Long nbRepondu) {
        this.nbRepondu = nbRepondu;
    }

    public Long getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(Long nbQuestions) {
        this.nbQuestions = nbQuestions;
    }
}
