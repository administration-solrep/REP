package fr.dila.reponses.core.versioning;

import static fr.dila.reponses.core.constant.DocTypeConstant.FOND_DE_DOSSIER_FICHIER_TYPE;
import static fr.dila.reponses.core.constant.DocTypeConstant.REPONSE_TYPE;

import fr.dila.reponses.core.ReponseFeature;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
//@Ignore("La contrib ne fait apparemment pas effet, à corriger")
public class TestVersioning {
    private static final String LIFECYCLE_UNDEFINED = "undefined";
    private static final String LIFECYCLE_RUNNING = "running";

    @Inject
    CoreSession coreSession;

    @Test
    public void testFondDeDossierFichierVersioning() {
        DocumentModel doc = coreSession.createDocumentModel("/", "fddf", FOND_DE_DOSSIER_FICHIER_TYPE);
        DublincorePropertyUtil.setTitle(doc, "title");
        doc = coreSession.createDocument(doc);
        coreSession.save();

        // default initial
        Assert.assertEquals(LIFECYCLE_RUNNING, doc.getCurrentLifeCycleState());
        Assert.assertEquals("0.0", doc.getVersionLabel());

        // increment none
        DublincorePropertyUtil.setTitle(doc, "some title");
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_RUNNING, doc.getCurrentLifeCycleState());
        Assert.assertEquals("0.0", doc.getVersionLabel());

        // major
        DublincorePropertyUtil.setTitle(doc, "another title");
        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_RUNNING, doc.getCurrentLifeCycleState());
        Assert.assertEquals("1.0", doc.getVersionLabel());

        // minor
        DublincorePropertyUtil.setTitle(doc, "title");
        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_RUNNING, doc.getCurrentLifeCycleState());
        Assert.assertEquals("1.1", doc.getVersionLabel());
    }

    @Test
    public void testReponseVersioning() {
        DocumentModel doc = coreSession.createDocumentModel("/", "reponse", REPONSE_TYPE);
        DublincorePropertyUtil.setTitle(doc, "title");
        doc = coreSession.createDocument(doc);
        coreSession.save();

        // default initial
        Assert.assertEquals(LIFECYCLE_UNDEFINED, doc.getCurrentLifeCycleState());
        Assert.assertEquals("0.0", doc.getVersionLabel());

        // increment none
        DublincorePropertyUtil.setTitle(doc, "some title");
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_UNDEFINED, doc.getCurrentLifeCycleState());
        Assert.assertEquals("0.0", doc.getVersionLabel());

        // major
        DublincorePropertyUtil.setTitle(doc, "another title");
        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_UNDEFINED, doc.getCurrentLifeCycleState());
        Assert.assertEquals("1.0", doc.getVersionLabel());

        // minor
        DublincorePropertyUtil.setTitle(doc, "hitman");
        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
        coreSession.saveDocument(doc);
        coreSession.save();
        doc = coreSession.getDocument(doc.getRef());
        Assert.assertEquals(LIFECYCLE_UNDEFINED, doc.getCurrentLifeCycleState());
        Assert.assertEquals("1.1", doc.getVersionLabel());
    }
}
