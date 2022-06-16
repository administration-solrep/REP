package fr.dila.reponses.core.service;

import static java.util.stream.Collectors.toList;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesBirtConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.archive.ReponsesFddWriter;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSAbstractArchiveService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.io.STDocumentReader;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ZipUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.CountMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.io.DocumentPipe;
import org.nuxeo.ecm.core.io.DocumentReader;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;

public class ArchiveServiceImpl extends SSAbstractArchiveService implements ArchiveService {
    private static final long serialVersionUID = -7780914517900449689L;

    private static final String QUERY_QUESTION_BY_DATE_PUBLICATION =
        "SELECT count() AS count FROM " +
        DossierConstants.QUESTION_DOCUMENT_TYPE +
        " AS q WHERE q." +
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION +
        " < ?";

    private static final String PATHNAME_EXPORT_DOSSIER = "%s/FICHE_%s_%s.pdf";

    @Override
    public void writeZipStream(
        final CoreSession session,
        final List<DocumentModel> files,
        final ZipOutputStream outputStream
    ) {
        DocumentReader reader = null;
        DocumentWriter writer = null;

        reader = new STDocumentReader(files);

        writer = new ReponsesFddWriter(session, outputStream);

        final DocumentPipe pipe = new DocumentPipeImpl(10);
        pipe.setReader(reader);
        pipe.setWriter(writer);

        try {
            pipe.run();
        } catch (IOException e) {
            throw new NuxeoException(e);
        }

        reader.close();
    }

    @Override
    protected List<DocumentModel> findDossierLinkUnrestricted(final CoreSession session, final String id) {
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        return corbeilleService.findDossierLinkUnrestricted(session, id);
    }

    @Override
    public List<Dossier> ajouterDossiersListeElimination(final CoreSession session, final List<DocumentModel> docs) {
        List<Dossier> dossiers = docs.stream().map(d -> d.getAdapter(Dossier.class)).collect(toList());
        List<Dossier> dossiersErreur = dossiers.stream().filter(d -> !d.canEliminate(session)).collect(toList());
        dossiers.removeAll(dossiersErreur);

        if (dossiers.isEmpty()) {
            return dossiersErreur;
        }

        final ListeElimination currentListe = getCurrentListeElimination(session);

        for (final Dossier dossier : dossiers) {
            dossier.setListeElimination(currentListe.getDocument().getId());
            dossier.save(session);

            // Log de l'action
            final JournalService journalService = STServiceLocator.getJournalService();
            journalService.journaliserActionAdministration(
                session,
                (DocumentModel) null,
                ReponsesEventConstant.EVENT_DEMANDE_ELIMINATION,
                ReponsesEventConstant.COMMENT_DEMANDE_ELIMINATION
            );
        }
        return dossiersErreur;
    }

    private ListeElimination getCurrentListeElimination(final CoreSession session) {
        final List<DocumentModel> listes = session.query(
            "SELECT * FROM ListeElimination WHERE lel:" +
            ReponsesSchemaConstant.LISTE_ELIMINATION_EN_COURS_PROPERTY +
            " = 1"
        );
        if (CollectionUtils.isEmpty(listes)) {
            // Il n'y a pas de liste d'élimination en cours, on en crée une
            final UIDGeneratorService uidGeneratorService = ServiceUtil.getRequiredService(UIDGeneratorService.class);
            final UIDSequencer sequencer = uidGeneratorService.getSequencer();
            final String listeNumero = String.format("%04d", sequencer.getNextLong("LISTE_ELIMINATION_SEQUENCER"));
            DocumentModel listeDoc = session.createDocumentModel(
                "/case-management/listes-elimination-root",
                "liste-elimination-" + listeNumero,
                ReponsesSchemaConstant.LISTE_ELIMINATION_TYPE
            );
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
    public List<DocumentModel> getDossiersFromListeElimination(
        final CoreSession session,
        final DocumentModel listeDoc
    ) {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            "SELECT d.ecm:uuid as id FROM Dossier as d WHERE d.dos:listeElimination = ?",
            new Object[] { listeDoc.getId() }
        );
    }

    @Override
    public void abandonListeElimination(final CoreSession session, final DocumentModel listeDoc) {
        final List<DocumentModel> docs = getDossiersFromListeElimination(session, listeDoc);

        for (final DocumentModel doc : docs) {
            doc.getAdapter(Dossier.class).setListeElimination(null);
            session.saveDocument(doc);
        }
        session.removeDocument(listeDoc.getRef());
        session.save();
    }

    @Override
    public void suppressionListeElimination(final CoreSession session, final DocumentModel listeDoc) {
        final List<DocumentModel> docs = getDossiersFromListeElimination(session, listeDoc);

        for (final DocumentModel doc : docs) {
            supprimerDossier(session, doc);
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
     * @throws IOException
     */
    @Override
    protected void generateCurrentDocumentPdf(
        final ZipOutputStream outputStream,
        final DocumentModel dossierDoc,
        final CoreSession session
    )
        throws IOException {
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

        generateBirtPdf(session, outputStream2, dossier.getQuestionId(), dossierDoc);

        final String nomDossier =
            question.getTypeQuestion() + "_" + question.getOrigineQuestion() + "_" + question.getNumeroQuestion();
        final ZipEntry entry = new ZipEntry("Dossier_" + nomDossier + "/Fiche_" + nomDossier + ".pdf");
        outputStream.putNextEntry(entry);

        try (InputStream in = new ByteArrayInputStream(outputStream2.toByteArray())) {
            IOUtils.copy(in, outputStream);
        } finally {
            outputStream.closeEntry();
        }
    }

    @Override
    public String generateListeEliminationPdf(final CoreSession session, final String listeEliminationId) {
        Map<String, Serializable> scalarParameters = new HashMap<>();
        scalarParameters.put("listEliminationId", listeEliminationId);

        ConfigService configService = STServiceLocator.getConfigService();
        String tmpDirectory = configService.getValue(STConfigConstants.APP_FOLDER_TMP);
        File file = SSServiceLocator
            .getBirtGenerationService()
            .generate(
                ReponsesBirtConstant.REPORT_ELIMINATION,
                null,
                BirtOutputFormat.PDF,
                scalarParameters,
                String.format("%s/%s.pdf", tmpDirectory, listeEliminationId),
                true
            );

        return file.getName();
    }

    @Override
    public void generateBirtPdf(
        final CoreSession session,
        final OutputStream outputStream,
        final String questionId,
        final DocumentModel dossierDoc
    ) {
        File file = generateBirtPdfFile(session, questionId, dossierDoc);

        try (InputStream in = new FileInputStream(file)) {
            IOUtils.copy(in, outputStream);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public File generateBirtPdf(final CoreSession session, final String questionId, final DocumentModel dossierDoc) {
        return generateBirtPdfFile(session, questionId, dossierDoc);
    }

    @Override
    public void writeZipStream(
        final List<DocumentModel> files,
        final OutputStream outputStream,
        final DocumentModel dossierDoc,
        final CoreSession session
    ) {
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
            writeZipStream(session, files, zipOutputStream);
            generateCurrentDocumentPdf(zipOutputStream, dossierDoc, session);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    protected Consumer<ZipOutputStream> getDossierArchiveZosConsumer(
        final CoreSession session,
        final List<DocumentModel> files,
        final List<DocumentModel> dossiers
    ) {
        return new Consumer<ZipOutputStream>() {

            @Override
            public void accept(ZipOutputStream zos) {
                try {
                    writeZipStream(session, files, zos);
                    for (final DocumentModel dossierDoc : dossiers) {
                        generateCurrentDocumentPdf(zos, dossierDoc, session);
                    }
                } catch (IOException e) {
                    throw new NuxeoException(e);
                }
            }
        };
    }

    @Override
    public void writeZipStream(
        final List<DocumentModel> files,
        final OutputStream outputStream,
        final List<DocumentModel> dossiers,
        final CoreSession session
    ) {
        try {
            ZipUtil.writeZipToOutputStream(getDossierArchiveZosConsumer(session, files, dossiers), outputStream);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public Long countQuestionArchivable(final CoreSession session) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String duaDelai = paramService.getParametreValue(session, STParametreConstant.DELAI_CONSERVATION_DONNEES);
        final Calendar dua = DateUtil.removeMonthsToNow(Integer.parseInt(duaDelai));

        final Object[] params = { dua };
        return QueryUtils.doFNXQLQueryAndMapping(
            session,
            QueryUtils.ufnxqlToFnxqlQuery(QUERY_QUESTION_BY_DATE_PUBLICATION),
            params,
            new CountMapper()
        );
    }

    private Boolean canDisplayReponse(final CoreSession session, final DocumentModel dossierDoc) {
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
                if (ace.getUsername().startsWith("mailbox_poste") && ssPrincipal.isMemberOf(ace.getUsername())) {
                    userMailboxInDossier = true;
                }
            }
        }

        final Boolean isAdminFonctionnel = ssPrincipal.isMemberOf(
            ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER
        );

        return isAdminFonctionnel || userMailboxInDossier || reponseSignee;
    }

    /**
     * Génère et renvoie la fiche dossier PDF pour le dossier et la question passés
     * en paramètres.
     */
    private File generateBirtPdfFile(
        final CoreSession session,
        final String questionId,
        final DocumentModel dossierDoc
    ) {
        // Export de la réponse uniquement si l'utilisateur appartient au ministère
        // attributaire
        final String exportReponse = canDisplayReponse(session, dossierDoc) ? "TRUE" : "FALSE";

        Map<String, Serializable> scalarParameters = new HashMap<>();
        scalarParameters.put(ReponsesConstant.BIRT_REPORT_FICHE_DOSSIER_PARAM_ID, questionId);
        scalarParameters.put(ReponsesConstant.BIRT_REPORT_EXPORT_REPONSE_PARAM_ID, exportReponse);

        ConfigService configService = STServiceLocator.getConfigService();
        String tmpDirectory = configService.getValue(STConfigConstants.APP_FOLDER_TMP);
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Question question = dossier.getQuestion(session);
        String pathname = String.format(
            PATHNAME_EXPORT_DOSSIER,
            tmpDirectory,
            question.getOrigineQuestion(),
            dossier.getNumeroQuestion().toString()
        );

        return SSServiceLocator
            .getBirtGenerationService()
            .generate(
                ReponsesBirtConstant.REPORT_FICHE_DOSSIER,
                null,
                BirtOutputFormat.PDF,
                scalarParameters,
                pathname,
                true
            );
    }
}
