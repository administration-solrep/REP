package fr.dila.reponses.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.io.DocumentPipe;
import org.nuxeo.ecm.core.io.DocumentReader;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.platform.io.selectionReader.DocumentModelListReader;
import org.nuxeo.ecm.platform.mail.service.MailService;
import org.nuxeo.ecm.platform.reporting.engine.BirtEngine;
import org.nuxeo.ecm.platform.reporting.report.ReportHelper;
import org.nuxeo.ecm.platform.uidgen.UIDSequencer;
import org.nuxeo.ecm.platform.uidgen.service.ServiceHelper;
import org.nuxeo.ecm.platform.uidgen.service.UIDGeneratorService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.core.archive.ReponsesFddWriter;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSArchiveServiceImpl;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STMailServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;

public class ArchiveServiceImpl extends SSArchiveServiceImpl implements ArchiveService {
    private static final long serialVersionUID = -7780914517900449689L;

    private static final String QUERY_QUESTION_BY_DATE_PUBLICATION = "SELECT count() AS count FROM " + DossierConstants.QUESTION_DOCUMENT_TYPE
            + " AS q WHERE q." + DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION
            + " < ?";

    @Override
    public void sendMail(final CoreSession documentManager, final List<DocumentModel> files, final List<String> listMail,
            final Boolean formCopieMail, final String formObjetMail, final String formTexteMail, final List<DocumentModel> dossiers) throws Exception {
        // Chargement des services
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        final JournalService journalService = STServiceLocator.getJournalService();

        final Session session = Framework.getService(MailService.class).getSession(STMailServiceImpl.MAIL_SESSION);

        // construct the text body part
        final MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(formTexteMail);

        // now write the ZIP content to the output stream
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
        writeZipStream(documentManager, files, zipOutputStream);

        for (final DocumentModel dossierDoc : dossiers) {
            generateCurrentDocumentPdf(zipOutputStream, dossierDoc, documentManager);
        }

        zipOutputStream.close();

        final byte[] bytes = outputStream.toByteArray();

        // construct the pdf body part
        final String nomFichier = "export_reponses_" + DateUtil.formatDDMMYYYY(new GregorianCalendar()) + ".zip";
        final DataSource dataSource = new ByteArrayDataSource(bytes, "application/zip");
        final MimeBodyPart pdfBodyPart = new MimeBodyPart();
        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
        pdfBodyPart.setFileName(nomFichier);

        // construct the mime multi part
        final MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(textBodyPart);
        mimeMultipart.addBodyPart(pdfBodyPart);

        // create the sender addresses
        final InternetAddress iaSender = new InternetAddress(session.getProperty("mail.from"));

        // construct the mime message
        final MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setSender(iaSender);
        mimeMessage.setSubject(formObjetMail);
        mimeMessage.setContent(mimeMultipart);

        // destinataire en copie
        if (formCopieMail) {
            final NuxeoPrincipal userName = (NuxeoPrincipal) documentManager.getPrincipal();
            final String userMail = organigrammeService.getMailFromUid(userName.getName());
            if (!userMail.isEmpty()) {
                final InternetAddress iaRecipient = new InternetAddress(userMail);
                mimeMessage.addRecipient(Message.RecipientType.TO, iaRecipient);
            }
        }

        // Destinaires choisis
        for (final String destMail : listMail) {
            final InternetAddress iaRecipient = new InternetAddress(destMail);
            mimeMessage.addRecipient(Message.RecipientType.TO, iaRecipient);
        }

        STServiceLocator.getSTMailService().setFromAndSentDate(mimeMessage);
        Transport.send(mimeMessage);

        // log de l'action
        for (final DocumentModel dossierDoc : dossiers) {
            journalService.journaliserActionParapheur(documentManager, dossierDoc, STEventConstant.EVENT_ENVOI_MAIL_DOSSIER,
                    STEventConstant.COMMENT_ENVOI_MAIL_DOSSIER);
        }
    }

    @Override
    public void writeZipStream(final CoreSession session, final List<DocumentModel> files, final ZipOutputStream outputStream) throws Exception {
        DocumentReader reader = null;
        DocumentWriter writer = null;

        reader = new DocumentModelListReader(files);

        writer = new ReponsesFddWriter(session, outputStream);

        final DocumentPipe pipe = new DocumentPipeImpl(10);
        pipe.setReader(reader);
        pipe.setWriter(writer);

        pipe.run();

        reader.close();
    }

    @Override
    protected List<DocumentModel> findDossierLinkUnrestricted(final CoreSession session, final String id) throws ClientException {
        final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        return corbeilleService.findDossierLinkUnrestricted(session, id);
    }

    @Override
    public List<Dossier> ajouterDossiersListeElimination(final CoreSession session, final List<DocumentModel> docs) throws ClientException {
        final JournalService journalService = STServiceLocator.getJournalService();
        final ListeElimination currentListe = getCurrentListeElimination(session);

        final List<Dossier> dossiersErreur = new ArrayList<Dossier>();
        for (final DocumentModel doc : docs) {
            final Dossier dossier = doc.getAdapter(Dossier.class);
            if (dossier.isArchivable(session) && dossier.getListeElimination() == null) {
                dossier.setListeElimination(currentListe.getDocument().getId());
                session.saveDocument(doc);
            } else {
                dossiersErreur.add(dossier);
            }

            // Log de l'action
            journalService.journaliserActionAdministration(session, (DocumentModel) null, ReponsesEventConstant.EVENT_DEMANDE_ELIMINATION,
                    ReponsesEventConstant.COMMENT_DEMANDE_ELIMINATION);
        }
        return dossiersErreur;
    }

    private ListeElimination getCurrentListeElimination(final CoreSession session) throws ClientException {
        final List<DocumentModel> listes = session.query("SELECT * FROM ListeElimination WHERE lel:"
                + ReponsesSchemaConstant.LISTE_ELIMINATION_EN_COURS_PROPERTY + " = 1");
        if (listes.size() == 0) {
            // Il n'y a pas de liste d'élimination en cours, on en crée une
            final UIDGeneratorService uidGeneratorService = ServiceHelper.getUIDGeneratorService();
            final UIDSequencer sequencer = uidGeneratorService.getSequencer();
            final String listeNumero = String.format("%04d", sequencer.getNext("LISTE_ELIMINATION_SEQUENCER"));
            DocumentModel listeDoc = new DocumentModelImpl("/case-management/listes-elimination-root", "liste-elimination-" + listeNumero,
                    ReponsesSchemaConstant.LISTE_ELIMINATION_TYPE);
            listeDoc = session.createDocument(listeDoc);
            final ListeElimination liste = listeDoc.getAdapter(ListeElimination.class);
            liste.setTitle("Liste d'élimination " + listeNumero);
            liste.setEnCours(true);
            liste.setSuppressionEnCours(false);
            liste.setAbandonEnCours(false);
            liste.save(session);
            session.save();
            return liste;
        }
        return listes.get(0).getAdapter(ListeElimination.class);
    }

    @Override
    public List<DocumentModel> getDossiersFromListeElimination(final CoreSession session, final DocumentModel listeDoc) throws ClientException {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "SELECT d.ecm:uuid as id FROM Dossier as d WHERE d.dos:listeElimination = ?", new Object[] { listeDoc.getId() });
    }

    @Override
    public void abandonListeElimination(final CoreSession session, final DocumentModel listeDoc) throws ClientException {
        final List<DocumentModel> docs = getDossiersFromListeElimination(session, listeDoc);

        for (final DocumentModel doc : docs) {
            try {
                doc.getAdapter(Dossier.class).setListeElimination(null);
                session.saveDocument(doc);
            } catch (final Exception e) {
                throw new ClientException(e);
            }
        }
        session.removeDocument(listeDoc.getRef());
        session.save();
    }

    @Override
    public void suppressionListeElimination(final CoreSession session, final DocumentModel listeDoc) throws ClientException {
        final List<DocumentModel> docs = getDossiersFromListeElimination(session, listeDoc);

        for (final DocumentModel doc : docs) {
            try {
                supprimerDossier(session, doc);
            } catch (final Exception e) {
                throw new ClientException(e);
            }
        }
        session.removeDocument(listeDoc.getRef());
        session.save();
    }

    /**
     * Ajoute du zip la fiche dossier au format PDF
     * 
     * @param outputStream
     * @param dossierDoc
     * @param session
     * @throws Exception
     */
    private void generateCurrentDocumentPdf(final ZipOutputStream outputStream, final DocumentModel dossierDoc, final CoreSession session)
            throws Exception {

        final ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();

        Dossier dossier = null;
        if (dossierDoc == null) {
            return;
        } else {
            final DossierCommon common = dossierDoc.getAdapter(DossierCommon.class);
            dossier = common.getDossier(session);
        }

        if (dossier == null) {
            return;
        }
        final Question question = dossier.getQuestion(session);

        generateBirtPdf(session, outputStream2, dossier.getQuestionId().toString(), dossierDoc);

        final String nomDossier = question.getTypeQuestion() + "_" + question.getOrigineQuestion() + "_" + question.getNumeroQuestion();
        final ZipEntry entry = new ZipEntry("Dossier_" + nomDossier + "/Fiche_" + nomDossier + ".pdf");
        outputStream.putNextEntry(entry);

        InputStream in = null;
        try {
            in = new ByteArrayInputStream(outputStream2.toByteArray());
            FileUtils.copy(in, outputStream);
        } finally {
            if (in != null) {
                in.close();
            }
            outputStream.closeEntry();
        }
    }

    @Override
    public void generateListeEliminationPdf(final CoreSession session, final OutputStream outputStream, final String listeEliminationId)
            throws Exception {
        // Lecture du fichier birt .rptdesign
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("birtReports/elimination.rptdesign");
        if (is == null) {
            is = Framework.getResourceLoader().getResourceAsStream("birtReports/elimination.rptdesign");
        }
        final IReportRunnable nuxeoReport = ReportHelper.getNuxeoReport(is);
        final IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

        final HTMLRenderOption options = new HTMLRenderOption();
        options.setImageHandler(new HTMLServerImageHandler());
        options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
        options.setOutputStream(outputStream);

        // Paramètre de l'id de la liste d'élimination
        final Map<String, String> inputValues = new HashMap<String, String>();
        inputValues.put("listEliminationId", listeEliminationId);

        task.setParameterValues(inputValues);
        task.setRenderOption(options);
        task.run();
        task.close();
    }

    @Override
    public void generateBirtPdf(final CoreSession session, final OutputStream outputStream, final String questionId, final DocumentModel dossierDoc)
            throws Exception {
        // Lecture du fichier birt .rptdesign
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ReponsesConstant.BIRT_REPORT_FICHE_DOSSIER);
        if (is == null) {
            is = Framework.getResourceLoader().getResourceAsStream(ReponsesConstant.BIRT_REPORT_FICHE_DOSSIER);
        }
        final IReportRunnable nuxeoReport = ReportHelper.getNuxeoReport(is);
        final IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

        final HTMLRenderOption options = new HTMLRenderOption();
        options.setImageHandler(new HTMLServerImageHandler());
        options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
        options.setOutputStream(outputStream);

        // Export de la réponse uniquement si l'utilisateur appartient au ministère attributaire
        final String exportReponse = canDisplayReponse(session, dossierDoc) ? "TRUE" : "FALSE";

        // set the input parameter of the report
        final Map<String, String> inputValues = new HashMap<String, String>();
        inputValues.put(ReponsesConstant.BIRT_REPORT_FICHE_DOSSIER_PARAM_ID, questionId);
        inputValues.put(ReponsesConstant.BIRT_REPORT_EXPORT_REPONSE_PARAM_ID, exportReponse);

        task.setParameterValues(inputValues);
        task.setRenderOption(options);
        task.run();
        task.close();
    }

    @Override
    public void writeZipStream(final List<DocumentModel> files, final OutputStream outputStream, final DocumentModel dossierDoc,
            final CoreSession session) throws Exception {
        final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
        writeZipStream(session, files, zipOutputStream);
        generateCurrentDocumentPdf(zipOutputStream, dossierDoc, session);
        zipOutputStream.close();

    }

    @Override
    public void writeZipStream(final List<DocumentModel> files, final OutputStream outputStream, final List<DocumentModel> dossiers,
            final CoreSession session) throws Exception {
        final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
        writeZipStream(session, files, zipOutputStream);

        for (final DocumentModel dossierDoc : dossiers) {
            generateCurrentDocumentPdf(zipOutputStream, dossierDoc, session);
        }

        zipOutputStream.close();
    }

    @Override
    public Long countQuestionArchivable(final CoreSession session) throws ClientException {
        final Calendar dua = Calendar.getInstance();
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String duaDelai = paramService.getParametreValue(session, STParametreConstant.DELAI_CONSERVATION_DONNEES);
        dua.add(Calendar.MONTH, -Integer.parseInt(duaDelai));

        final Object params[] = { dua };
        final Long count = QueryUtils.doFNXQLQueryAndMapping(session, QueryUtils.ufnxqlToFnxqlQuery(QUERY_QUESTION_BY_DATE_PUBLICATION), params,
                new QueryUtils.CountMapper());
        return count;
    }

    private Boolean canDisplayReponse(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {
        final SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Reponse reponse = dossier.getReponse(session);
        final Boolean reponseSignee = reponse.isSignee();
        Boolean userMailboxInDossier = false;
        final ACP acp = dossierDoc.getACP();
        final ACL[] acls = acp.getACLs();

        for (final ACL acl : acls) {
            final ACE[] aces = acl.getACEs();
            for (final ACE ace : aces) {
                if (ace.getUsername().startsWith("mailbox_poste")) {
                    if (ssPrincipal.isMemberOf(ace.getUsername())) {
                        userMailboxInDossier = true;
                    }
                }
            }
        }

        final Boolean isAdminFonctionnel = ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER);

        return isAdminFonctionnel || userMailboxInDossier || reponseSignee;
    }
}
