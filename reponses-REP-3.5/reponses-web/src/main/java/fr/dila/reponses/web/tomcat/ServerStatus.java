package fr.dila.reponses.web.tomcat;

import fr.dila.reponses.web.tomcat.test.DictaoServerTest;

/**
 * Classe utilisée pour vérifier le statut du serveur.
 */
public class ServerStatus extends fr.dila.st.web.tomcat.ServerStatus {

    /**
     * Constructeur.
     */
    public ServerStatus() {
        super();
        tests.add(new DictaoServerTest());
    }
}
