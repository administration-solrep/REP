package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.LISTE_ELIMINATION_ACTIONS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.LISTE_ELIMINATION;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.EliminationDonneesConsultationList;
import fr.dila.reponses.ui.bean.EliminationDonneesDTO;
import fr.dila.reponses.ui.bean.EliminationDonneesDossierDTO;
import fr.dila.reponses.ui.bean.EliminationDonneesList;
import fr.dila.reponses.ui.contentview.ListeEliminationDossierPageProvider;
import fr.dila.reponses.ui.contentview.ListeEliminationPageProvider;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ArchiveUIService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.impl.SSArchiveUIServiceImpl;
import fr.dila.ss.ui.th.bean.DossierMailForm;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public class ReponsesArchiveUIServiceImpl extends SSArchiveUIServiceImpl implements ArchiveUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesArchiveUIServiceImpl.class);

    @Override
    public void export(SpecificContext context) {
        CoreSession session = context.getSession();

        DocumentModel dossierDoc = context.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        OutputStream outputStream = context.getFromContextData(SSContextDataKey.OUTPUTSTREAM);

        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();

        final List<DocumentModel> fondDossierDocs = fondDeDossierService.getFddDocuments(session, dossier);

        archiveService.writeZipStream(fondDossierDocs, outputStream, dossierDoc, session);

        // log de l'export
        final JournalService journalService = STServiceLocator.getJournalService();
        journalService.journaliserActionParapheur(
            session,
            dossierDoc,
            STEventConstant.EVENT_EXPORT_ZIP_DOSSIER,
            STEventConstant.COMMENT_EXPORT_ZIP_DOSSIER
        );
    }

    @Override
    public void masseExport(SpecificContext context) {
        CoreSession session = context.getSession();
        // Choix de la liste de selection
        final List<DocumentModel> files = new ArrayList<>();
        final List<DocumentModel> dossiers = new ArrayList<>();
        List<String> selectionIds = context.getFromContextData(ReponsesContextDataKey.DOSSIER_IDS);
        OutputStream outputStream = context.getFromContextData(SSContextDataKey.OUTPUTSTREAM);

        // Pour chaque dossier, ajout au flux du fichier ZIP
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
        for (final String dossierId : selectionIds) {
            DocumentModel doc = session.getDocument(new IdRef(dossierId));
            Dossier dossier = doc.getAdapter(Dossier.class);
            files.addAll(fondDeDossierService.getFddDocuments(session, dossier));
            dossiers.add(dossier.getDocument());
        }

        try {
            final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
            archiveService.writeZipStream(files, outputStream, dossiers, session);
            context.getMessageQueue().addInfoToQueue(ResourceHelper.getString("dossier.action.masse.export.success"));
        } catch (final NuxeoException exc) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.action.masse.export.error"));
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXPORT_DOSSIER_MASS, exc);
        }

        // Journalise l'action
        final JournalService journalService = STServiceLocator.getJournalService();
        journalService.journaliserActionAdministration(
            session,
            STEventConstant.EVENT_EXPORT_ZIP_DOSSIER,
            STEventConstant.COMMENT_EXPORT_ZIP_DOSSIER
        );
    }

    @Override
    public void masseEnvoyerMailDossier(SpecificContext context) {
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

        CoreSession session = context.getSession();
        List<String> selection = context.getFromContextData(ReponsesContextDataKey.DOSSIER_IDS);
        DossierMailForm formMail = context.getFromContextData(SSContextDataKey.DOSSIER_MAIL_FORM);
        Boolean formCopieMail = formMail.getEtreEnCopie();

        final List<DocumentModel> files = new ArrayList<>();
        final List<DocumentModel> dossiers = new ArrayList<>();

        // Pour chaque dossier, ajout au flux du fichier ZIP
        for (final String dossierId : selection) {
            DocumentModel doc = session.getDocument(new IdRef(dossierId));
            final DossierCommon common = doc.getAdapter(DossierCommon.class);
            final Dossier dossier = common.getDossier(session);
            files.addAll(fondDeDossierService.getFddDocuments(session, dossier));
            dossiers.add(dossier.getDocument());
        }

        final List<String> listMail = new ArrayList<>(getDestinataireMail(formMail.getDestinataireIds()));
        if (StringUtils.isNotBlank(formMail.getAutresDestinataires())) {
            listMail.addAll(Arrays.asList(formMail.getAutresDestinataires().split(";")));
        }

        String formObjetMail = formMail.getObjet();
        String formTexteMail = formMail.getMessage();
        try {
            archiveService.prepareAndSendArchiveMail(
                session,
                files,
                listMail,
                formCopieMail,
                formObjetMail,
                formTexteMail,
                dossiers
            );
            context.getMessageQueue().addInfoToQueue(ResourceHelper.getString(OK_DOSSIER_MAIL));
        } catch (final Exception exc) {
            context.getMessageQueue().addWarnToQueue(ERROR_DOSSIER_MAIL);
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_FONC, exc);
        }
    }

    @Override
    public void demanderElimination(SpecificContext context, List<String> docIds) {
        CoreSession session = context.getSession();
        List<DocumentModel> docs = session.getDocuments(
            docIds.stream().map(id -> new IdRef(id)).toArray(DocumentRef[]::new)
        );
        final List<Dossier> dossiersErreur = new ArrayList<>();
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                dossiersErreur.addAll(archiveService.ajouterDossiersListeElimination(session, docs));
            }
        }
            .runUnrestricted();

        if (dossiersErreur.isEmpty()) {
            context
                .getMessageQueue()
                .addMessageToQueue(
                    ResourceHelper.getString("recherche.action.demander.elimination.succes"),
                    AlertType.TOAST_SUCCESS
                );
        } else {
            List<Dossier> dossiersDelaiNonAtteint = dossiersErreur
                .stream()
                .filter(dossier -> !dossier.isArchivable(session))
                .collect(Collectors.toList());
            List<Dossier> dossiersPartiDeListe = dossiersErreur
                .stream()
                .filter(dossier -> dossier.getListeElimination() != null)
                .collect(Collectors.toList());

            StringBuilder messageErreur = new StringBuilder(
                ResourceHelper.getString("recherche.action.demander.elimination.erreur")
            )
            .append("<ul>");
            if (!dossiersDelaiNonAtteint.isEmpty()) {
                messageErreur
                    .append("<li>")
                    .append(ResourceHelper.getString("recherche.action.demander.elimination.erreur.delai"))
                    .append(
                        dossiersDelaiNonAtteint
                            .stream()
                            .map(dossier -> dossier.getQuestion(session).getSourceNumeroQuestion())
                            .collect(Collectors.joining(", "))
                    )
                    .append("</li>");
            }
            if (!dossiersPartiDeListe.isEmpty()) {
                messageErreur
                    .append("<li>")
                    .append(ResourceHelper.getString("recherche.action.demander.elimination.erreur.liste"))
                    .append(
                        dossiersPartiDeListe
                            .stream()
                            .map(dossier -> dossier.getQuestion(session).getSourceNumeroQuestion())
                            .collect(Collectors.joining(", "))
                    )
                    .append("</li>");
            }
            messageErreur.append("</ul>");
            // Afficher l'alerte au dessus de la liste des résultats
            context.getMessageQueue().addErrorToQueue(messageErreur.toString(), "listeDossiers");
        }
    }

    @Override
    public void eliminerListe(SpecificContext context) {
        CoreSession session = context.getSession();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        if (!ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ARCHIVAGE_EDITOR)) {
            throw new NuxeoException("Archivage des dossiers non autorisé.");
        }

        // Précise que la liste est en cours de suppression
        DocumentModel listeDoc = context.getCurrentDocument();
        final ListeElimination listeElim = listeDoc.getAdapter(ListeElimination.class);
        listeElim.setSuppressionEnCours(true);
        listeElim.save(session);
        session.save();

        final EventProducer eventProducer = STServiceLocator.getEventProducer();
        final Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(ReponsesEventConstant.DOSSIER_EVENT_PARAM, listeDoc);
        final InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.AFTER_ELIMINATION_LISTE));
    }

    @Override
    public String editerBordereau(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel listeDoc = context.getCurrentDocument();

        // Suppression de l'état en cours de la liste d'élimination
        final ListeElimination liste = listeDoc.getAdapter(ListeElimination.class);
        liste.setEnCours(false);
        liste.save(session);
        session.save();

        // Export PDF du bordereau d'archivage
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
        return archiveService.generateListeEliminationPdf(session, listeDoc.getId());
    }

    @Override
    public void retirerDossierListeElimination(SpecificContext context) {
        CoreSession session = context.getSession();
        List<String> docIds = context.getFromContextData(DOSSIER_IDS);
        List<DocumentModel> docs = session.getDocuments(
            docIds.stream().map(id -> new IdRef(id)).toArray(DocumentRef[]::new)
        );
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                for (final DocumentModel doc : docs) {
                    doc.getAdapter(Dossier.class).setListeElimination(null);
                    session.saveDocument(doc);
                }
            }
        }
            .runUnrestricted();

        session.save();
    }

    @Override
    public void viderListeElimination(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel listeDoc = context.getCurrentDocument();
        final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
        final List<DocumentModel> docs = archiveService.getDossiersFromListeElimination(session, listeDoc);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                for (final DocumentModel doc : docs) {
                    doc.getAdapter(Dossier.class).setListeElimination(null);
                    session.saveDocument(doc);
                }
            }
        }
            .runUnrestricted();

        session.save();
    }

    @Override
    public void abandonListeElimination(SpecificContext context) {
        viderListeElimination(context);

        CoreSession session = context.getSession();
        DocumentModel listeDoc = context.getCurrentDocument();

        // Précise que la liste est en cours d'abandon
        final ListeElimination liste = listeDoc.getAdapter(ListeElimination.class);
        liste.setAbandonEnCours(true);
        liste.save(session);
        session.save();

        final EventProducer eventProducer = STServiceLocator.getEventProducer();
        final Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(ReponsesEventConstant.DOSSIER_EVENT_PARAM, listeDoc);
        final InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.AFTER_ABANDON_LISTE));
    }

    @Override
    public boolean isCurrentListeEliminationEnCours(DocumentModel currentDocument) {
        if (currentDocument != null && currentDocument.hasSchema(ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA)) {
            return currentDocument.getAdapter(ListeElimination.class).isEnCours();
        }
        return false;
    }

    @Override
    public EliminationDonneesList getEliminationDonneesList(SpecificContext context) {
        EliminationDonneesList eliminationDonneesList = new EliminationDonneesList();

        PaginationForm form = context.computeFromContextDataIfAbsent(
            STContextDataKey.PAGINATION_FORM,
            k -> new PaginationForm()
        );
        ListeEliminationPageProvider provider = form.getPageProvider(
            context.getSession(),
            "listeEliminationPageProvider"
        );
        List<Map<String, Serializable>> docList = provider.getCurrentPage();

        eliminationDonneesList.setNbTotal((int) provider.getResultsCount());

        // On fait le mapping des documents vers notre DTO
        docList
            .stream()
            .filter(EliminationDonneesDTO.class::isInstance)
            .map(EliminationDonneesDTO.class::cast)
            .map(dto -> setEliminationActions(context, dto))
            .forEach(dto -> eliminationDonneesList.getListe().add(dto));

        return eliminationDonneesList;
    }

    @Override
    public EliminationDonneesConsultationList getEliminationDonneesConsultationList(SpecificContext context) {
        EliminationDonneesConsultationList elimDonneesConsultList = new EliminationDonneesConsultationList();
        elimDonneesConsultList.setTitre(getTitreListeElimination(context));

        PaginationForm form = context.computeFromContextDataIfAbsent(
            STContextDataKey.PAGINATION_FORM,
            k -> new PaginationForm()
        );

        ListeEliminationDossierPageProvider provider = form.getPageProvider(
            context.getSession(),
            "listeEliminationDossierPageProvider",
            Arrays.asList(context.getCurrentDocument().getId())
        );
        List<Map<String, Serializable>> docList = provider.getCurrentPage();

        elimDonneesConsultList.setNbTotal((int) provider.getResultsCount());

        // On fait le mapping des documents vers notre DTO
        docList
            .stream()
            .filter(doc -> doc instanceof EliminationDonneesDossierDTO)
            .map(EliminationDonneesDossierDTO.class::cast)
            .forEach(dto -> elimDonneesConsultList.getListe().add(dto));

        return elimDonneesConsultList;
    }

    private static EliminationDonneesDTO setEliminationActions(
        SpecificContext context,
        EliminationDonneesDTO eliminationDonneesDTO
    ) {
        context.putInContextData(LISTE_ELIMINATION, eliminationDonneesDTO);
        eliminationDonneesDTO.setActions(context.getActions(LISTE_ELIMINATION_ACTIONS));
        return eliminationDonneesDTO;
    }

    @Override
    public String getTitreListeElimination(SpecificContext context) {
        DocumentModel listeEliminationDoc = context.getCurrentDocument();
        ListeElimination listeElimination = listeEliminationDoc.getAdapter(ListeElimination.class);
        StringBuilder titre = new StringBuilder(listeEliminationDoc.getTitle());
        if (Boolean.TRUE.equals(listeElimination.isEnCours())) {
            titre.append(" " + ResourceHelper.getString("eliminationDonnees.titre.encours"));
        }
        return titre.toString();
    }
}
