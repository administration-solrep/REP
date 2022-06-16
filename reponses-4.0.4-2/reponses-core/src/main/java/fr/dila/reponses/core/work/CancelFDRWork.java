package fr.dila.reponses.core.work;

import fr.dila.st.core.work.SolonWork;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.Collection;
import java.util.LinkedList;

public class CancelFDRWork extends SolonWork {
    private static final long serialVersionUID = -7510810757575403979L;

    private Collection<String> routeIds;

    public CancelFDRWork(Collection<String> routeIds) {
        super();
        this.routeIds = new LinkedList<>(routeIds);
    }

    @Override
    protected void doWork() {
        openSystemSession();
        session
            .getDocuments(routeIds, null)
            .stream()
            .map(doc -> doc.getAdapter(FeuilleRoute.class))
            .forEach(fr -> fr.cancel(session));
    }
}
