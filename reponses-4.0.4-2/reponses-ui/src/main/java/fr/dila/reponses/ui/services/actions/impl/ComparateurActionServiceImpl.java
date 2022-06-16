package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getReponseService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.ui.bean.CompareVersionDTO;
import fr.dila.reponses.ui.services.actions.ComparateurActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ComparateurActionServiceImpl implements ComparateurActionService {

    /**
     * Récupère le texte de la réponse du dossier courant du contexte dont les ids grace à leurs ids
     *
     * @param context
     * @param idVersion1
     * @param idVersion2
     */
    public CompareVersionDTO getVersionTexts(SpecificContext context, String idVersion1, String idVersion2) {
        CompareVersionDTO dto = new CompareVersionDTO();
        List<DocumentModel> versionDocs = getVersionDocList(context);
        DocumentModel version1Doc = getVersionFromId(idVersion1, versionDocs);
        DocumentModel version2Doc = getVersionFromId(idVersion2, versionDocs);
        if (version1Doc != null) {
            dto.setTextFirst(getSelectedReponse(version1Doc.getAdapter(Reponse.class)));
        }
        if (version2Doc != null) {
            dto.setTextLast(getSelectedReponse(version2Doc.getAdapter(Reponse.class)));
        }
        return dto;
    }

    private List<DocumentModel> getVersionDocList(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        DocumentModel currentVersionDoc = dossier.getReponse(context.getSession()).getDocument();
        List<DocumentModel> versionDocs = getReponseService()
            .getReponseVersionDocumentList(context.getSession(), currentVersionDoc);
        versionDocs.add(versionDocs.size(), currentVersionDoc); // on met la version en cours à la fin de la liste
        return versionDocs;
    }

    private DocumentModel getVersionFromId(String id, List<DocumentModel> versionDocs) {
        return versionDocs.stream().filter(v -> id.equals(v.getId())).findFirst().orElse(null);
    }

    /**
     * Suppression des balises html dans une chaine de caracteres
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
            StringBuilder chaineTagRemoved = new StringBuilder();

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
    private static String parseStringForJavaScript(String value) {
        if (value != null) {
            value = value.replaceAll("'", "\\\'");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("\n", "");
            value = value.replaceAll("\r", "");
            value = value.replaceAll("\0", "");
        }

        return removeHTMLTag(value);
    }

    private String getSelectedReponse(Reponse reponse) {
        if (reponse != null) {
            return parseStringForJavaScript(reponse.getTexteReponse());
        }
        return "";
    }
}
