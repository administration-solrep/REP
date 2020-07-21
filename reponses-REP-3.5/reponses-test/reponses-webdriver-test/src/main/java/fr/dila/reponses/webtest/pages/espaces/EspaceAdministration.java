package fr.dila.reponses.webtest.pages.espaces;

import fr.dila.reponses.webtest.pages.ReponsesPage;

public class EspaceAdministration extends ReponsesPage{

	public EspaceAdministrationMenuVoletGauche getMenuVoletGauche() {
		return getPage(EspaceAdministrationMenuVoletGauche.class);
	}

}
