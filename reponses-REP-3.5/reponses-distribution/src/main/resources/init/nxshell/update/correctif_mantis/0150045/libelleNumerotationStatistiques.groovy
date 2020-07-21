import java.util.ArrayList;
import java.lang.StringBuilder;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;

/**
 * JBT : Script pour la version 1.12.3 de Réponses
 */

print "Début du script groovy de changement des noms de statistiques pour la version 1.12.3";
print "------------------------------------------------------------------------------------";

final static int NOMBRE_CHANGEMENT_STATISTIQUES = 18;
final static int NOMBRE_SUPPRESSION_STATISTIQUES = 5;

/* Liste des Nouveaux Noms de statistiques */
String[] listeNouveauxNoms = new String[NOMBRE_CHANGEMENT_STATISTIQUES];
listeNouveauxNoms[0]="MIN11. Taux de réponse du Gouvernement aux questions écrites, depuis le début de la législature";
listeNouveauxNoms[1]="MIN08. Taux de réponse aux QE, par ministère, depuis le début de la législature - Parlement";
listeNouveauxNoms[2]="MIN10. Taux de réponse aux QE, par ministère, depuis le début de la législature - Assemblée nationale";
listeNouveauxNoms[3]="MIN09. Taux de réponse aux QE, par ministère, depuis le début de la législature - Sénat";
listeNouveauxNoms[4]="SGG03. Variations mensuelles du nombre de réponses publiées et du taux de réponse par ministère";
listeNouveauxNoms[5]="MIN12. Nombre et répartition des questions écrites attribuées, depuis le début de la législature";
listeNouveauxNoms[6]="MIN04. Questions écrites renouvelées, par ministère";
listeNouveauxNoms[7]="MIN05. Questions écrites sans réponse publiée, par ministère";
listeNouveauxNoms[8]="MIN13. Questions écrites retirées, par ministère, depuis le début de la législature";
listeNouveauxNoms[9]="MIN14. Origine des questions par groupe parlementaire";
listeNouveauxNoms[10]="MIN15. Données brutes";
listeNouveauxNoms[11]="SGG02. Questions écrites en cours de traitement (suivi par date)";
listeNouveauxNoms[12]="MIN03. Questions écrites signalées par l'Assemblée nationale en cours de traitement (suivi par date)";
listeNouveauxNoms[13]="MIN02. Questions écrites en cours de traitement (étape en cours)";
listeNouveauxNoms[14]="MIN06. Questions écrites traitées par le ministère depuis le début de la législature (délais de traitement)";
listeNouveauxNoms[15]="SGG01. Questions écrites en cours de traitement dans les ministères et au SGG (statistiques de l'ensemble du gouvernement)";
listeNouveauxNoms[16]="MIN01. Questions écrites en cours de traitement (statistiques)";
listeNouveauxNoms[17]="MIN07. Questions écrites traitées par les ministères depuis le début de la législature (statistiques du Gouvernement)";

/* Nom du chemin du BirtReportModel correspondant */
String[] nomStats = new String[NOMBRE_CHANGEMENT_STATISTIQUES];
nomStats[0]="stat01";
nomStats[1]="stat04";
nomStats[2]="stat05";
nomStats[3]="stat06";
nomStats[4]="stat07";
nomStats[5]="stat08";
nomStats[6]="stat09";
nomStats[7]="stat10";
nomStats[8]="stat11";
nomStats[9]="stat12";
nomStats[10]="stat16";
nomStats[11]="stat17";
nomStats[12]="stat18";
nomStats[13]="stat19";
nomStats[14]="stat20";
nomStats[15]="stat21";
nomStats[16]="stat22";
nomStats[17]="stat23";

/* Liste des BirtReportModel à supprimer */
String[] nomSupprimeStats = new String[NOMBRE_SUPPRESSION_STATISTIQUES];
nomSupprimeStats[0]="stat02";
nomSupprimeStats[1]="stat03";
nomSupprimeStats[2]="stat13";
nomSupprimeStats[3]="stat14";
nomSupprimeStats[4]="stat15";

/* Liste des ID des BirtReportModel et BirtReportInstance correspondant */
String[] idStatsModel = new String[18];
String[] idStatsInstance = new String[18];
String[] idStatsModelSupprime = new String[NOMBRE_SUPPRESSION_STATISTIQUES];
String[] idStatsInstanceSupprime = new String[NOMBRE_SUPPRESSION_STATISTIQUES];

print "############################### Changement nom BirtReportModel #################################";

StringBuilder sb = new StringBuilder("SELECT ecm:uuid FROM BirtReportModel WHERE ecm:path='/case-management/birt-model-route/");
StringBuilder sbTemp;
for (int i=0 ; i<nomStats.length ; i++ ) {
	sbTemp = new StringBuilder(sb);
	sbTemp.append(nomStats[i]);
	sbTemp.append("'");
	try {
		iterableQueryResult = Session.queryAndFetch(sbTemp.toString(), "NXQL");
		idStatsModel[i] = (String) iterableQueryResult.next().get("ecm:uuid");
		DocumentModel dm = Session.getDocument(new IdRef(idStatsModel[i]));
		print dm.getPropertyValue("dc:title");
		dm.setPropertyValue("dc:title",listeNouveauxNoms[i]);
		Session.saveDocument(dm);
		print " est remplacé par : " + dm.getPropertyValue("dc:title");
	} catch (Exception e) {
		print "Erreur a la récupération du modèle de la statistique " + nomStats[i];
	} finally {
		iterableQueryResult.close();
	}
}

print "############################### Changement nom BirtReportInstance ##############################";

sb = new StringBuilder("SELECT ecm:uuid FROM BirtReport WHERE birt:modelRef='");
for (int i=0 ; i<nomStats.length ; i++ ) {
	sbTemp = new StringBuilder(sb);
	sbTemp.append(idStatsModel[i]);
	sbTemp.append("'");
	try {
		iterableQueryResult = Session.queryAndFetch(sbTemp.toString(), "NXQL");
		idStatsInstance[i] = (String) iterableQueryResult.next().get("ecm:uuid");
		DocumentModel dm = Session.getDocument(new IdRef(idStatsInstance[i]));
		print dm.getPropertyValue("dc:title");
		dm.setPropertyValue("dc:title",listeNouveauxNoms[i]);
		Session.saveDocument(dm);
		print " est remplacé par : " + dm.getPropertyValue("dc:title");
	} catch (Exception e) {
		print "Erreur a la récupération de l'instance de la statistique " + nomStats[i];
	} finally {
		iterableQueryResult.close();
	}
}

print "############################### Résultats suppression BirtReportModel ##########################";

sb = new StringBuilder("SELECT ecm:uuid FROM BirtReportModel WHERE ecm:path='/case-management/birt-model-route/");
for (int i=0 ; i<nomSupprimeStats.length ; i++ ) {
	sbTemp = new StringBuilder(sb);
	sbTemp.append(nomSupprimeStats[i]);
	sbTemp.append("'");
	try {
		iterableQueryResult = Session.queryAndFetch(sbTemp.toString(), "NXQL");
		idStatsModelSupprime[i] = (String) iterableQueryResult.next().get("ecm:uuid");
		DocumentModel dm = Session.getDocument(new IdRef(idStatsModelSupprime[i]));
		print dm.getPropertyValue("dc:title");
		print " est supprimé.";
		Session.removeDocument(new IdRef(idStatsModelSupprime[i]));
	} catch (Exception e) {
		print "Erreur a la suppression du modèle de la statistique [déjà supprimé ?] " + nomSupprimeStats[i];
	} finally {
		iterableQueryResult.close();
	}
}

print "############################### Résultats suppression BirtReportInstance #######################";

sb = new StringBuilder("SELECT ecm:uuid FROM BirtReport WHERE birt:modelRef='");
for (int i=0 ; i<nomSupprimeStats.length ; i++ ) {
	sbTemp = new StringBuilder(sb);
	sbTemp.append(idStatsModelSupprime[i]);
	sbTemp.append("'");
	try {
		iterableQueryResult = Session.queryAndFetch(sbTemp.toString(), "NXQL");
		idStatsInstanceSupprime[i] = (String) iterableQueryResult.next().get("ecm:uuid");
		DocumentModel dm = Session.getDocument(new IdRef(idStatsInstanceSupprime[i]));
		print dm.getPropertyValue("dc:title");
		print " est supprimé.";
		Session.removeDocument(new IdRef(idStatsInstanceSupprime[i]));
	} catch (Exception e) {
		print "Erreur a la suppression de l'instance de la statistique [déjà supprimée ?] " + nomSupprimeStats[i];
	} finally {
		iterableQueryResult.close();
	}
}

/* Enregistrement des modifications effectuées */
Session.save();


print "------------------------------------------------------------------------------------";
print "Fin du script groovy de changement des noms de statistiques pour la version 1.12.3";
return "Fin du script groovy";
