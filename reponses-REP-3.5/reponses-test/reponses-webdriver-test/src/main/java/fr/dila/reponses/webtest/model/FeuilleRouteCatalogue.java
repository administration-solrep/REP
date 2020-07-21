package fr.dila.reponses.webtest.model;

import fr.dila.reponses.webtest.constant.ConstantesEtapeType;
import fr.dila.reponses.webtest.constant.ConstantesOrga;

/**
 * Contient l'ensemble des modèles de feuille de route utilisées pour les tests
 * 
 * @author jgomez
 * 
 */
public class FeuilleRouteCatalogue {

	public static final String	NOM_FDR_1	= "FDR 1";

	public static final String	NOM_FDR_2	= "FDR SUBSTITUTION 1";

	public static final String	NOM_FDR_3	= "FDR AGRI";
	public static final String	NOM_FDR		= "TEST VERROU";

	public static FeuilleRoute getFDR1() {
		FeuilleRoute fdr1 = new FeuilleRoute();
		fdr1.setIntitule(NOM_FDR_1);
		fdr1.setIsModeleParDefaut(true);
		fdr1.setMinistere(ConstantesOrga.MIN_ECO);
		fdr1.setDirectionPilote(ConstantesOrga.DIR_BUREAU_BDC);
		Etape etape1 = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_BDC_ECO, "3", false,
				true, true);
		Etape etape2 = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_DGEFP);
		Etape etape3 = new EtapeSerie(ConstantesEtapeType.POUR_REDACTION, ConstantesOrga.POSTE_DLF);
		Etape etape4 = new EtapeSerie(ConstantesEtapeType.POUR_VISA, ConstantesOrga.POSTE_AGENT_SEC_GENERAL);
		Etape etape5 = new EtapeSerie(ConstantesEtapeType.POUR_SIGNATURE, ConstantesOrga.POSTE_BDC_ECO);
		Etape etape6 = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_ADMIN_SOLON);
		Etape etape7_1 = new EtapeSerie(ConstantesEtapeType.POUR_IMPRESSION, ConstantesOrga.POSTE_SECRETARIAT_AFF_ECO);
		Etape etape7_2 = new EtapeSerie(ConstantesEtapeType.POUR_VALIDATION_PREMIER_MINISTRE,
				ConstantesOrga.POSTE_CONSEILLERE_AFF_ECO);
		Etape etape7 = new EtapeBranche(etape7_1, etape7_2);
		Etape etape8 = new EtapeSerie(ConstantesEtapeType.POUR_TRANSMISSION_ASSEMBLEE, ConstantesOrga.POSTE_ADMIN_SOLON);
		fdr1.addEtape(etape1);
		fdr1.addEtape(etape2);
		fdr1.addEtape(etape3);
		fdr1.addEtape(etape4);
		fdr1.addEtape(etape5);
		fdr1.addEtape(etape6);
		fdr1.addEtape(etape7);
		fdr1.addEtape(etape8);
		return fdr1;
	}

	public static FeuilleRoute getFDR2() {
		FeuilleRoute fdr = new FeuilleRoute();
		fdr.setIntitule(NOM_FDR_2);
		fdr.setMinistere(ConstantesOrga.MIN_ECO);
		fdr.setDirectionPilote(ConstantesOrga.DIR_BUREAU_BDC);
		Etape etape1 = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_BDC_ECO, "3", true,
				true, true);
		Etape etape2_1 = new EtapeSerie(ConstantesEtapeType.POUR_IMPRESSION, ConstantesOrga.POSTE_SECRETARIAT_AFF_ECO);
		Etape etape2_2 = new EtapeSerie(ConstantesEtapeType.POUR_VALIDATION_PREMIER_MINISTRE,
				ConstantesOrga.POSTE_CONSEILLERE_AFF_ECO);
		Etape etape2 = new EtapeBranche(etape2_1, etape2_2);
		Etape etape3 = new EtapeSerie(ConstantesEtapeType.POUR_TRANSMISSION_ASSEMBLEE, ConstantesOrga.POSTE_ADMIN_SOLON);
		fdr.addEtape(etape1);
		fdr.addEtape(etape2);
		fdr.addEtape(etape3);
		return fdr;
	}

	public static FeuilleRoute getFDR3() {
		FeuilleRoute fdr = new FeuilleRoute();
		fdr.setIntitule(NOM_FDR_3);
		fdr.setMinistere(ConstantesOrga.MIN_AGRI);
		fdr.setDirectionPilote(ConstantesOrga.DIR_BDC_AGRI);
		fdr.setIsModeleParDefaut(true);
		Etape etape1 = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_BDC_AGRI, "3", true,
				true, true);
		// Etape etape2 = new
		// EtapeSerie(ConstantesEtapeType.POUR_TRANSMISSION_ASSEMBLEE,ConstantesOrga.POSTE_ADMIN_SOLON);
		fdr.addEtape(etape1);
		// fdr.addEtape(etape2);
		return fdr;
	}

	public static FeuilleRoute getFDR() {
		FeuilleRoute fdr = new FeuilleRoute();

		fdr.setIntitule(NOM_FDR);
		fdr.setIsModeleParDefaut(true);
		fdr.setMinistere(ConstantesOrga.MINISTERE_JUSTICE);
		fdr.setDirectionPilote(ConstantesOrga.DIR_BUREAU_JUSTICE);
		fdr.setIsModeleParDefaut(true);
		EtapeSerie etape = new EtapeSerie(ConstantesEtapeType.POUR_AVIS, ConstantesOrga.POSTE_BDC_1_JUSTICE, "3", true,
				true, true);
		fdr.addEtape(etape);

		Etape etape1 = new EtapeSerie(ConstantesEtapeType.POUR_CORRECTION, ConstantesOrga.POSTE_BDC1_ECOLOGIE, "3",
				true, true, true);

		fdr.addEtape(etape1);
		return fdr;
	}

}
