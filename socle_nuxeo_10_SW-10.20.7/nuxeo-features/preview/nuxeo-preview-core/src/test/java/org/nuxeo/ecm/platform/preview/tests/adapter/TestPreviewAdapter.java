/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thierry Martins <tmartins@nuxeo.com>
 */

package org.nuxeo.ecm.platform.preview.tests.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.ecm.platform.preview.api.HtmlPreviewAdapter;
import org.nuxeo.ecm.platform.preview.helper.PreviewHelper;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandAvailability;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("org.nuxeo.ecm.platform.convert")
@Deploy("org.nuxeo.ecm.platform.preview")
@Deploy("org.nuxeo.ecm.platform.htmlsanitizer")
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.platform.preview:doctype-contrib-test.xml")
public class TestPreviewAdapter {

    @Inject
    CoreSession session;

    @Inject
    protected MimetypeRegistry mimetypeRegistry;

    protected static final String BAD = "<html>\n" //
            + "<body>\n" //
            + "<script>alert(1)</script>\n" //
            + "<b>test</b>" //
            + "</body>\n" //
            + "</html>";

    @Test
    public void testNoBlob() throws Exception {
        DocumentModel document = session.createDocumentModel("File");
        HtmlPreviewAdapter adapter = document.getAdapter(HtmlPreviewAdapter.class);
        assertEquals(false, adapter.hasBlobToPreview());
        assertFalse(PreviewHelper.blobSupportsPreview(document, "file:content"));
        List<Blob> blobs = adapter.getFilePreviewBlobs();
        assertEquals(Collections.emptyList(), blobs);
    }

    @Test
    public void testFileDocument() throws Exception {
        Blob blob = new StringBlob("test", "text/plain");
        doTestFileDocument(blob, "<pre>test</pre>");
    }

    @Test
    public void testHTMLDocument() throws Exception {
        Blob blob = new StringBlob(BAD, "text/html");
        doTestFileDocument(blob, "\n\n<b>test</b>");
    }

    @Test
    public void testXMLDocument() throws Exception {
        Blob blob = new StringBlob(BAD, "text/xml");
        doTestFileDocument(blob, "\n\n<b>test</b>");
    }

    protected void doTestFileDocument(Blob blob, String expectedBody) throws Exception {
        DocumentModel document = session.createDocumentModel("File");
        HtmlPreviewAdapter adapter = document.getAdapter(HtmlPreviewAdapter.class);
        Map<String, Serializable> file = new HashMap<>();
        // Attach one file to the list
        file.put("file", (Serializable) blob);
        document.setPropertyValue("file:content", (Serializable) blob);
        assertEquals(true, adapter.hasBlobToPreview());
        assertTrue(PreviewHelper.blobSupportsPreview(document, "file:content"));
        List<Blob> blobs = adapter.getFilePreviewBlobs();
        assertEquals(1, blobs.size());
        String preview = blobs.get(0).getString();
        assertEquals("<!doctype html><html><body>" + expectedBody + "</body></html>", preview);
    }

    @Test
    public void testCustomDocumentWithFilesSchema() throws Exception {
        DocumentModel document = session.createDocumentModel("CustomDoc");

        // no preview adapter should be found in this case (empty files property)
        assertEquals(null, document.getAdapter(HtmlPreviewAdapter.class));
        assertFalse(PreviewHelper.blobSupportsPreview(document, "files:files/0/file"));

        Blob blob = new StringBlob("test");
        Map<String, Serializable> file = new HashMap<>();
        // Attach one file to the list
        file.put("file", (Serializable) blob);
        ArrayList<Map<String, Serializable>> files = new ArrayList<>();
        files.add(file);
        document.setPropertyValue("files:files", files);
        HtmlPreviewAdapter adapter = document.getAdapter(HtmlPreviewAdapter.class);
        assertEquals(true, adapter.hasBlobToPreview());
        assertTrue(PreviewHelper.blobSupportsPreview(document, "files:files/0/file"));
        List<Blob> blobs = adapter.getFilePreviewBlobs();
        assertEquals(1, blobs.size());
        String preview = blobs.get(0).getString();
        assertTrue(preview, preview.contains("<pre>test</pre>"));
    }

    @Test
    public void testFilesOfficePreviewer() throws IOException {
        checkCommandAvailability("soffice");        

        DocumentModel document = session.createDocumentModel("CustomDoc");
        Blob officeBlob = new FileBlob(FileUtils.getResourceFileFromContext("hello.docx"));
        String mimeType = mimetypeRegistry.getMimetypeFromFilenameAndBlobWithDefault("hello.docx", officeBlob, null);
        officeBlob.setMimeType(mimeType);
        document.setPropertyValue("files:files",
                (Serializable) Collections.singletonList(Collections.singletonMap("file", officeBlob)));

        HtmlPreviewAdapter adapter = document.getAdapter(HtmlPreviewAdapter.class);
        List<Blob> previewBlobs = adapter.getFilePreviewBlobs();
        // check that we have the same result passing the xpath explicitly
        assertEquals(previewBlobs, adapter.getFilePreviewBlobs("files:files/0/file"));
        // check preview blobs
        assertEquals(2, previewBlobs.size());
        assertEquals("text/html", previewBlobs.get(0).getMimeType());
        Blob pdfBlob = previewBlobs.get(1);
        assertEquals("application/pdf", pdfBlob.getMimeType());
        // fix filename set to "pdf" by the PDFPreviewer to ensure blob equality
        pdfBlob.setFilename("hello.pdf");
        Blob expectedBlob = Framework.getService(ConversionService.class)
                                     .convert("any2pdf", new SimpleBlobHolder(officeBlob), null)
                                     .getBlob();
        assertEquals(expectedBlob, pdfBlob);
    }

    @Test
    public void testFolderishDocument() throws Exception {
        DocumentModel document = session.createDocumentModel("File");
        document.addFacet("Folderish");
        HtmlPreviewAdapter adapter = document.getAdapter(HtmlPreviewAdapter.class);
        assertNotNull(adapter);
    }


   protected void checkCommandAvailability(String command) {
        CommandLineExecutorService cles = Framework.getService(CommandLineExecutorService.class);
        assertNotNull(cles);

        CommandAvailability ca = cles.getCommandAvailability(command);
        assumeTrue(String.format("Convert command %s is not available, skipping test", command), ca.isAvailable());
    }

}
