package fr.dila.reponses.core.archive;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.AbstractDocumentWriter;
import org.nuxeo.ecm.core.io.impl.DWord;
import org.nuxeo.ecm.core.io.impl.DocumentTranslationMapImpl;

public class ReponsesFddWriter extends AbstractDocumentWriter {
    protected ZipOutputStream out;
    protected CoreSession session;

    public ReponsesFddWriter(CoreSession session, ZipOutputStream out) {
        this.out = out;
        this.session = session;
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument doc) throws IOException {
        Path path = doc.getPath();
        DocumentModel dossierDoc = null;
        try {
            dossierDoc = session.getDocument(new PathRef(path.uptoSegment(6).toString()));
        } catch (NuxeoException e) {
            throw new IOException(e);
        }
        path = path.removeFirstSegments(7);
        Question question = dossierDoc.getAdapter(Dossier.class).getQuestion(session);
        String nomDossier =
            "Dossier_" +
            question.getTypeQuestion() +
            "_" +
            question.getOrigineQuestion() +
            "_" +
            question.getNumeroQuestion();

        List<String> pathList = new ArrayList<>();
        pathList.add(nomDossier);
        pathList.addAll(Arrays.asList(path.segments()));
        String[] segments = new String[pathList.size()];
        pathList.toArray(segments);
        path = Path.createFromSegments(segments);

        writeDocument(path.toString(), doc);
        // keep location unchanged
        DocumentLocation oldLoc = doc.getSourceLocation();
        String oldServerName = oldLoc.getServerName();
        DocumentRef oldDocRef = oldLoc.getDocRef();
        DocumentTranslationMap map = new DocumentTranslationMapImpl(oldServerName, oldServerName);
        map.put(oldDocRef, oldDocRef);
        return map;
    }

    private void writeDocument(String path, ExportedDocument doc) throws IOException {
        //nettoyage du chemin
        //nettoyage du chemin
        if (path.charAt(0) == '/') {
            path = path.replaceFirst("/", "");
        }
        path = StringUtils.toAscii(path);

        //cas d'un document avec un fichier blob, pas de sous dossier
        if (doc.getBlobs().size() == 0) {
            path += '/';
            ZipEntry entry = new ZipEntry(path);
            entry.setExtra(new DWord(doc.getFilesCount()).getBytes());
            out.putNextEntry(entry);
            out.closeEntry();
        }

        ZipEntry entry = null;

        // write blobs
        Map<String, Blob> blobs = doc.getBlobs();
        for (Map.Entry<String, Blob> blobEntry : blobs.entrySet()) {
            entry = new ZipEntry(path);
            out.putNextEntry(entry);
            InputStream in = null;
            try {
                in = blobEntry.getValue().getStream();
                IOUtils.copy(in, out);
            } finally {
                if (in != null) {
                    in.close();
                }
                out.closeEntry();
                // System.out.println(">> add entry: "+entry.getName());
            }
        }
    }

    public void close() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // do nothing
            } finally {
                out = null;
            }
        }
    }
}
