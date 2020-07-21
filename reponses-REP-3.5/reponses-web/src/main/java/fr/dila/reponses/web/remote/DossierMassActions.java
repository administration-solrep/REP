package fr.dila.reponses.web.remote;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Remove;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.webapp.base.StatefulBaseLifeCycle;

/**
 * Remote bean pour la progressBar des actions de masse
 * 
 * @author asatre
 */
@Remote
public interface DossierMassActions extends StatefulBaseLifeCycle {

    void initialize();

    @Destroy
    @Remove
    @PermitAll
    void destroy();

    @WebRemote
    int getProgressPercent();

    @WebRemote
    void confirmMassAction(int index) throws ClientException;

    @WebRemote
    boolean hasNext();

}
