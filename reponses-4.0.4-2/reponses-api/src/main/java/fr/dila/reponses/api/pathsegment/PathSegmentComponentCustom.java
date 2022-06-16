package fr.dila.reponses.api.pathsegment;

import java.util.Arrays;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentServiceDefault;

public class PathSegmentComponentCustom extends PathSegmentServiceDefault {
    private List<String> toBeReplace = Arrays.asList("$");

    @Override
    public String generatePathSegment(DocumentModel doc) {
        String s = super.generatePathSegment(doc);
        // Remplacer les caractères spéciaux qui corrompent le PATH
        for (String rp : toBeReplace) {
            s = s.replace(rp, "-");
        }
        return s;
    }
}
