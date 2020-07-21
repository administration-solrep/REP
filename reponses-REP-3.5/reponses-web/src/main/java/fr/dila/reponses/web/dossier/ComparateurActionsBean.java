package fr.dila.reponses.web.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Bean pour la gestion du comparateur de versions.
 * 
 * @author
 */
@Name("ComparateurActions")
@Scope(ScopeType.CONVERSATION)
public class ComparateurActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String currentVersion1;

    protected String currentVersion2;

    protected DocumentModel selectedReponse1;

    protected DocumentModel selectedReponse2;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient DossierActionsBean dossierActions;

    /**
     * reset des selections et initialisation des contenus
     * 
     * @return
     */
    public void reset() throws ClientException {
        setCurrentVersion1("1");
        setCurrentVersion2("1");
    }

    /**
     * liste des versions d'une reponse
     * 
     * @return
     * @throws ClientException
     */
    public List<SelectItem> getVersionList() throws ClientException {
        List<SelectItem> versionList = new ArrayList<SelectItem>();
        List<DocumentModel> reponsesList = getCurrentReponseVersions();
        int cpt = 1;

        for (DocumentModel reponseDoc : reponsesList) {
            Reponse reponse = reponseDoc.getAdapter(Reponse.class);
            String label = Integer.toString(cpt) + " - " + reponse.getIdAuteurReponse();
            if (reponsesList.indexOf(reponseDoc) == reponsesList.size() - 1) {
                label = "Version de travail";
                if (dossierActions.isDossierContainsMinistere()) {
                    versionList.add(new SelectItem(Integer.toString(cpt), label));
                }
            } else {
                versionList.add(new SelectItem(Integer.toString(cpt), label));
            }
            cpt++;
        }

        return versionList;
    }

    private List<DocumentModel> getCurrentReponseVersions() throws ClientException {
        DocumentModel doc = navigationContext.getCurrentDocument();
        ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        DocumentModel currentReponse = reponseService.getReponseFromDossier(documentManager, doc);
        List<DocumentModel> versionsList = reponseService.getReponseVersionDocumentList(documentManager, currentReponse);
        versionsList.add(currentReponse);
        return versionsList;
    }

    public void setCurrentVersion1(String currentVersion1) throws ClientException {
        Integer versionNb = Integer.parseInt(currentVersion1);
        List<DocumentModel> reponsesList = getCurrentReponseVersions();
        DocumentModel reponseSelectedDoc = reponsesList.get(versionNb - 1);
        this.currentVersion1 = currentVersion1;
        selectedReponse1 = reponseSelectedDoc;
    }

    public void setCurrentVersion2(String currentVersion2) throws ClientException {
        Integer versionNb = Integer.parseInt(currentVersion2);
        List<DocumentModel> reponsesList = getCurrentReponseVersions();
        DocumentModel reponseSelectedDoc = reponsesList.get(versionNb - 1);
        this.currentVersion2 = currentVersion2;
        selectedReponse2 = reponseSelectedDoc;
    }

    /**
     * suppression des balises html dans une chaine de caracteres
     * 
     * @param String
     * @return string
     */
    private static String removeHTMLTag(String chaine) {
        if (chaine != null) {

            String space = " ";
            String nbsp = "&nbsp;";

            char[] contenuChaine = chaine.toCharArray();
            char charCourant;
            int c = 0;
            boolean dansBalise = false;
            StringBuffer chaineTagRemoved = new StringBuffer();

            // Parcours de la chaine à découper
            while (c < contenuChaine.length) {
                charCourant = contenuChaine[c];

                // On entre dans une balise
                if (charCourant == '<') {
                    dansBalise = true;
                    chaineTagRemoved.append(nbsp);
                }
                // On sort d'une balise
                else if (dansBalise && charCourant == '>') {
                    dansBalise = false;
                    chaineTagRemoved.append(nbsp);
                } else if (!dansBalise) {
                    chaineTagRemoved.append(charCourant);
                }
                c++;
            }
            String chaineOut = chaineTagRemoved.toString();
            chaineOut = chaineOut.replaceAll("(" + nbsp + ")+", space);
            chaineOut = chaineOut.replaceAll("(" + space + ")+", space);

            return chaineOut;
        } else {
            return chaine;
        }
    }

    /**
     * nettoyage des caracteres pour le javascript
     * 
     * @param string
     * @return string
     */
    public static String parseStringForJavaScript(String value) {

        if (value != null) {
            value = value.replaceAll("'", "\\\\'");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("\n", "");
            value = value.replaceAll("\r", "");
            value = value.replaceAll("\0", "");
        }

        return removeHTMLTag(value);
    }

    public String getCurrentVersion1() {
        return currentVersion1;
    }

    public String getCurrentVersion2() {
        return currentVersion2;
    }

    public String getSelectedReponse1() {
        if (selectedReponse1 != null) {
            Reponse reponse = selectedReponse1.getAdapter(Reponse.class);
            return parseStringForJavaScript(reponse.getTexteReponse());
        }
        return "";
    }

    public String getSelectedReponse2() {
        if (selectedReponse2 != null) {
            Reponse reponse = selectedReponse2.getAdapter(Reponse.class);
            return parseStringForJavaScript(reponse.getTexteReponse());
        }
        return "";
    }

    public String getTitleVersion1() {
        if (selectedReponse1 != null) {
            Reponse reponse = selectedReponse1.getAdapter(Reponse.class);
            return currentVersion1 + " - " + reponse.getIdAuteurReponse();
        }
        return "";
    }

    public String getTitleVersion2() {
        if (selectedReponse2 != null) {
            Reponse reponse = selectedReponse2.getAdapter(Reponse.class);
            return currentVersion2 + " - " + reponse.getIdAuteurReponse();
        }
        return "";
    }
}
