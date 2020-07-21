package fr.dila.reponses.webtest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.st.webdriver.model.STUser;

/**
 * Classe qui gère les utilisateurs par rapport à leur poste
 * 
 * @author jgomez
 * 
 */
public class UserManager {

	private Map<String, STUser>	mapUserByPoste;

	private List<STUser>		sTUsers	= new ArrayList<STUser>();

	/**
	 * Constructeur par défaut
	 */
	public UserManager() {
		super();
		initialize();
	}

	private void initialize() {
		STUser bdcJustice = new STUser("bdc", "bdc", ConstantesOrga.POSTE_BDC_1_JUSTICE);
		addUser(bdcJustice);

		STUser bdc = new STUser("bdc", "bdc", ConstantesOrga.POSTE_BDC_ECO);
		addUser(bdc);

		STUser dgefp = new STUser("dgefp", "dgefp", ConstantesOrga.POSTE_DGEFP);
		addUser(dgefp);

		STUser dlf = new STUser("dlf", "dlf", ConstantesOrga.POSTE_DLF);
		addUser(dlf);

		STUser secgenco = new STUser("secgeneco", "secgeneco", ConstantesOrga.POSTE_AGENT_SEC_GENERAL);
		addUser(secgenco);

		STUser superviseursgg = new STUser("superviseursgg", "superviseursgg", ConstantesOrga.POSTE_ADMIN_SOLON);
		addUser(superviseursgg);

		STUser sececofinance = new STUser("finance_sececofinance", "finance_sececofinance", ConstantesOrga.POSTE_SECRETARIAT_AFF_ECO);
		addUser(sececofinance);

		STUser ecofinance = new STUser("ecofinance", "ecofinance", ConstantesOrga.POSTE_CONSEILLERE_AFF_ECO);
		addUser(ecofinance);

		STUser bdcagri = new STUser("agriculture_bdc", "agriculture_bdc", ConstantesOrga.POSTE_BDC_AGRI);
		addUser(bdcagri);

		STUser bdcecologie = new STUser("budget_bdc", "budget_bdc", ConstantesOrga.POSTE_BDC_BUDGET);
		addUser(bdcecologie);

		this.mapUserByPoste = new HashMap<String, STUser>();
		for (STUser sTUser : sTUsers) {
			this.mapUserByPoste.put(sTUser.getPoste(), sTUser);
		}
	}

	private void addUser(STUser sTUser) {
		this.sTUsers.add(sTUser);
	}

	public STUser getUserFrom(String poste) {
		return mapUserByPoste.get(poste);
	}
}
