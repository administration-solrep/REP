package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.st.ui.th.model.SpecificContext;
import org.apache.commons.cli.MissingArgumentException;

public interface DossierListUIService {
    RepDossierList getDossiersFromPlanClassement(SpecificContext context) throws MissingArgumentException;

    RepDossierList getDossiersFromMinCorbeille(SpecificContext context) throws MissingArgumentException;

    RepDossierList getDossiersFromPosteCorbeille(SpecificContext context) throws MissingArgumentException;

    RepDossierList getDossiersFromSignaleCorbeille(SpecificContext context) throws MissingArgumentException;
}
