package fr.dila.reponses.core.service;

import static fr.dila.ss.core.util.SSPdfUtil.addBreak;
import static fr.dila.ss.core.util.SSPdfUtil.addBreakPage;
import static fr.dila.ss.core.util.SSPdfUtil.addTableRow;
import static fr.dila.ss.core.util.SSPdfUtil.addTitle;
import static fr.dila.ss.core.util.SSPdfUtil.ajoutBooleen;
import static fr.dila.ss.core.util.SSPdfUtil.ajoutParagrapheTexte;
import static fr.dila.ss.core.util.SSPdfUtil.ajoutTexteList;
import static fr.dila.ss.core.util.SSPdfUtil.createDateCell;
import static fr.dila.ss.core.util.SSPdfUtil.createHeaderCell;
import static fr.dila.ss.core.util.SSPdfUtil.createImageCell;
import static fr.dila.ss.core.util.SSPdfUtil.createInvisibleTable;
import static fr.dila.ss.core.util.SSPdfUtil.createTextCell;
import static fr.dila.ss.core.util.SSPdfUtil.htmlToText;
import static fr.dila.ss.core.util.SSPdfUtil.mergeCellVertically;
import static fr.dila.ss.core.util.SSPdfUtil.mergePdfs;
import static org.apache.commons.lang3.StringUtils.SPACE;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.api.service.PdfDossierService;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.core.pdf.AbstractPdfDossierService;
import fr.dila.ss.core.pdf.SpanPosition;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

public class PdfDossierServiceImpl extends AbstractPdfDossierService<Dossier> implements PdfDossierService {
    private static final STLogger LOGGER = STLogFactory.getLog(PdfDossierServiceImpl.class);

    private void genererTableauFeuilleDeRoute(XWPFDocument document, Dossier dossier, CoreSession session) {
        DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        DocumentModel dossierRoute = dossierDistributionService.getLastDocumentRouteForDossier(
            session,
            dossier.getDocument()
        );

        SSFeuilleRoute currentRoute = dossierRoute.getAdapter(SSFeuilleRoute.class);
        DocumentRoutingService documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();
        List<RouteTableElement> routeTableElementList = documentRoutingService.getFeuilleRouteElements(
            currentRoute,
            session
        );

        addTitle(document, ResourceHelper.getString("dossier.onglet.fdr"), SIZE_TITRE1);
        if (routeTableElementList != null && CollectionUtils.isNotEmpty(routeTableElementList)) {
            if (ReponsesServiceLocator.getFeuilleRouteService().hasStepFolders(routeTableElementList)) {
                List<SpanPosition> rowSpanPositionList = new ArrayList<>();
                XWPFTable table = document.createTable();
                addTableRow(
                    table,
                    true,
                    FOND_HEADER,
                    createHeaderCell(""),
                    createHeaderCell(""),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.etat")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.action")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.ministere")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.poste")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.utilisateur")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.echeance")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.date.traitement")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.obligatoire"))
                );
                int position = 1;
                for (RouteTableElement docRouteTableElement : routeTableElementList) {
                    DocumentModel docModel = docRouteTableElement.getDocument();
                    ReponsesRouteStep routeStep = docModel.getAdapter(ReponsesRouteStep.class);
                    Map<String, String> feuilleRouteValMap = setFeuilleDeRouteVal(routeStep, session);

                    addTableRow(
                        table,
                        false,
                        setCouleurLigneEtape(routeStep),
                        createImageCell(getEtapeParallelIcon(docRouteTableElement, position, rowSpanPositionList)),
                        createImageCell(getEtapeSerialIcon(docRouteTableElement)),
                        createImageCell(feuilleRouteValMap.get("etat")),
                        createTextCell(feuilleRouteValMap.get("action")),
                        createTextCell(feuilleRouteValMap.get("ministere")),
                        createTextCell(feuilleRouteValMap.get("poste")),
                        createTextCell(feuilleRouteValMap.get("utilisateur")),
                        createTextCell(feuilleRouteValMap.get("echeance")),
                        createTextCell(feuilleRouteValMap.get("traitement")),
                        createImageCell(feuilleRouteValMap.get("obligatoire"))
                    );
                    position++;
                }

                for (SpanPosition rowSpan : rowSpanPositionList) {
                    mergeCellVertically(table, 0, rowSpan.getBegin(), rowSpan.getEnd());
                }
                if (CollectionUtils.isNotEmpty(rowSpanPositionList)) {
                    rowSpanPositionList.clear();
                }
            } else {
                XWPFTable table = document.createTable();
                addTableRow(
                    table,
                    true,
                    FOND_HEADER,
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.etat")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.action")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.ministere")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.poste")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.utilisateur")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.echeance")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.date.traitement")),
                    createHeaderCell(ResourceHelper.getString("pdf.fdr.column.header.obligatoire"))
                );

                for (RouteTableElement docRouteTableElement : routeTableElementList) {
                    DocumentModel docModel = docRouteTableElement.getDocument();
                    ReponsesRouteStep routeStep = docModel.getAdapter(ReponsesRouteStep.class);
                    Map<String, String> feuilleRouteValMap = setFeuilleDeRouteVal(routeStep, session);
                    addTableRow(
                        table,
                        false,
                        setCouleurLigneEtape(routeStep),
                        createImageCell(feuilleRouteValMap.get("etat")),
                        createTextCell(feuilleRouteValMap.get("action")),
                        createTextCell(feuilleRouteValMap.get("ministere")),
                        createTextCell(feuilleRouteValMap.get("poste")),
                        createTextCell(feuilleRouteValMap.get("utilisateur")),
                        createTextCell(feuilleRouteValMap.get("echeance")),
                        createTextCell(feuilleRouteValMap.get("traitement")),
                        createImageCell(feuilleRouteValMap.get("obligatoire"))
                    );
                }
            }
        }
        addBreakPage(document);
    }

    private void genererDonneesBordereau(XWPFDocument document, Dossier dossier, CoreSession session) {
        addTitle(document, ResourceHelper.getString("bordereau.principal.bloc"), SIZE_TITRE1);
        genererBordereauDonneesPrincipales(document, dossier, session);
        addBreak(document);
        genererBordereauDonneesDeMAJ(document, dossier, session);
        genererBordereauDonneesPublication(document, dossier, session);
        addBreak(document);
        genererBordereauIndexation(document, dossier, session);
        addBreak(document);
        genererBordereauIndexationComplementaire(document, dossier, session);
        addBreak(document);
        genererBordereauDonneesSecondaires(document, dossier, session);
        addBreakPage(document);
    }

    private void genererBordereauDonneesPrincipales(XWPFDocument document, Dossier dossier, CoreSession session) {
        Question question = dossier.getQuestion(session);
        XWPFTable table = createInvisibleTable(document);
        addTableRow(
            table,
            true,
            createHeaderCell(ResourceHelper.getString("bordereau.numero.label")),
            createTextCell(question.getNumeroQuestion().toString())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.origine.label")),
            createTextCell(question.getOrigineQuestion())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.auteur.label")),
            createTextCell(question.getCiviliteEtNomCompletAuteur())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.groupe.politique.label")),
            createTextCell(question.getGroupePolitique())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.circonscription.label")),
            createTextCell(question.getCirconscriptionAuteur())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.type.question.label")),
            createTextCell(question.getTypeQuestion())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.date.publication.label")),
            createDateCell(question.getDatePublicationJO())
        );

        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.page.jo.label")),
            createTextCell(question.getPageJO())
        );

        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.ministere.attributaire.label")),
            createTextCell(question.getIntituleMinistereAttributaire())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.ministere.interoge.label")),
            createTextCell(question.getIntituleMinistere())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.ministere.rattachement.label")),
            createTextCell(question.getIntituleMinistereRattachement())
        );
        addTableRow(
            table,
            false,
            createHeaderCell(ResourceHelper.getString("bordereau.direction.pilote.label")),
            createTextCell(STServiceLocator.getSTUsAndDirectionService().getLabel(question.getIdDirectionPilote()))
        );
    }

    private void genererBordereauDonneesDeMAJ(XWPFDocument document, Dossier dossier, CoreSession session) {
        Question question = dossier.getQuestion(session);
        boolean afficherMAJ =
            question.isSignale() ||
            question.isRetiree() ||
            question.getDateCaducite() != null ||
            question.getDateRappelQuestion() != null ||
            question.getDateTransmissionAssemblees() != null;

        if (afficherMAJ) {
            addTitle(document, ResourceHelper.getString("bordereau.mise.jour.label"), SIZE_TITRE2);
            XWPFTable table = createInvisibleTable(document);
            addTableRow(table, true, createTextCell(""), createTextCell(""));

            if (question.isSignale()) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.signalement.label")),
                    createDateCell(question.getDateSignalementQuestion())
                );
            }

            if (question.getDateReponseSignalement().isPresent()) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.reponses.attendue.label")),
                    createDateCell(question.getDateReponseSignalement().get())
                );
            }

            if (question.isRetiree()) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.retrait.label")),
                    createDateCell(question.getDateRetraitQuestion())
                );
            }

            if (question.getDateCaducite() != null) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.caducite.label")),
                    createDateCell(question.getDateCaducite())
                );
            }

            if (question.getDateRappelQuestion() != null) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.rappel.label")),
                    createDateCell(question.getDateRappelQuestion())
                );
            }

            if (question.getDateTransmissionAssemblees() != null) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.date.reception.label")),
                    createDateCell(question.getDateTransmissionAssemblees())
                );
            }
        }

        if (question.isRenouvelle()) {
            addTitle(document, ResourceHelper.getString("bordereau.renouvellements.label"), SIZE_TITRE3);
            XWPFTable tab = createInvisibleTable(document);
            addTableRow(
                tab,
                false,
                createHeaderCell(ResourceHelper.getString("bordereau.date.effet.label")),
                createDateCell(question.getDateRenouvellementQuestion())
            );
        }
    }

    private void genererBordereauDonneesPublication(XWPFDocument document, Dossier dossier, CoreSession session) {
        if (dossier.getQuestion(session).isRepondue()) {
            addBreak(document);
            addTitle(document, ResourceHelper.getString("bordereau.reponse.label"), SIZE_TITRE3);
            XWPFTable table = createInvisibleTable(document);
            addTableRow(
                table,
                true,
                createHeaderCell(ResourceHelper.getString("bordereau.date.publication.reponse.label")),
                createDateCell(dossier.getReponse(session).getDateJOreponse())
            );
            addTableRow(
                table,
                false,
                createHeaderCell(ResourceHelper.getString("bordereau.page.jo.label")),
                createTextCell(dossier.getReponse(session).getPageJOreponse().toString())
            );
        }
    }

    private void genererBordereauDonneesSecondaires(XWPFDocument document, Dossier dossier, CoreSession session) {
        addTitle(document, ResourceHelper.getString("bordereau.secondaire.bloc"), SIZE_TITRE1);
        genererBordereauHistoriqueAttribution(document, dossier, session);
        addBreak(document);
        genererBordereauResumeFeuilleDeRoute(document, dossier, session);
    }

    private void genererBordereauIndexation(XWPFDocument document, Dossier dossier, CoreSession session) {
        Question question = dossier.getQuestion(session);
        if (VocabularyConstants.QUESTION_ORIGINE_AN.equals(question.getOrigineQuestion())) {
            addTableRowIndexationAN(
                document,
                "bordereau.indexation.label",
                question.getAssNatRubrique(),
                question.getAssNatTeteAnalyse(),
                question.getAssNatAnalyses()
            );
        }

        if (VocabularyConstants.QUESTION_ORIGINE_SENAT.equals(question.getOrigineQuestion())) {
            addTableRowIndexationSenat(
                document,
                "bordereau.indexation.label",
                question.getSenatQuestionRubrique(),
                question.getSenatQuestionThemes(),
                question.getSenatQuestionRenvois()
            );
        }
        addTableRowIndexationMotCles(document, question.getMotsClefMinistere());
    }

    private void genererBordereauResumeFeuilleDeRoute(XWPFDocument document, Dossier dossier, CoreSession session) {
        setOrientationPaysage(document);
        addTitle(document, ResourceHelper.getString("bordereau.resume.fdr.label"), SIZE_TITRE2);
        addTitle(document, ResourceHelper.getString("bordereau.directions.concernees.label"), SIZE_TITRE3);
        DossierService dossierService = ReponsesServiceLocator.getDossierService();
        for (String direction : dossierService.getListingUnitesStruct(dossier.getDocument(), session)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(direction != null ? "• " + direction : "•");
        }

        List<DocumentModel> runningSteps = ReponsesServiceLocator
            .getFeuilleRouteService()
            .getRunningSteps(session, dossier.getLastDocumentRoute());
        if (CollectionUtils.isNotEmpty(runningSteps)) {
            addBreak(document);
            addTitle(document, ResourceHelper.getString("bordereau.tache.cours.label"), SIZE_TITRE3);
            XWPFTable table = createInvisibleTable(document);
            SSRouteStep currentStep = runningSteps.get(0).getAdapter(SSRouteStep.class);
            addTableRow(
                table,
                true,
                createHeaderCell(ResourceHelper.getString("bordereau.debut.tache.cours.label")),
                createDateCell(currentStep.getDateDebutEtape())
            );
            addTableRow(
                table,
                false,
                createHeaderCell(ResourceHelper.getString("bordereau.poste.tache.label")),
                createTextCell(
                    STServiceLocator
                        .getSTPostesService()
                        .getPoste(
                            SSServiceLocator
                                .getMailboxPosteService()
                                .getPosteIdFromMailboxId(currentStep.getDistributionMailboxId())
                        )
                        .getLabel()
                )
            );

            addTableRow(
                table,
                false,
                createHeaderCell(ResourceHelper.getString("bordereau.delai.tache.label")),
                createTextCell(
                    currentStep.getDeadLine() == null
                        ? ResourceHelper.getString("pdf.label.jour")
                        : ResourceHelper.getString("pdf.label.jours")
                )
            );

            addTableRow(
                table,
                false,
                createHeaderCell(ResourceHelper.getString("bordereau.franchissement.auto.label")),
                ajoutBooleen(currentStep.isAutomaticValidated())
            );
        }
        addBreak(document);
        addTitle(document, ResourceHelper.getString("bordereau.tache.finale.signature.label"), SIZE_TITRE2);
        XWPFTable tab = createInvisibleTable(document);
        addTableRow(
            tab,
            true,
            createHeaderCell(ResourceHelper.getString("bordereau.tache.finale.ministere.label")),
            createTextCell(dossierService.getFinalStepLabel(session, dossier), false)
        );
    }

    private void genererBordereauHistoriqueAttribution(XWPFDocument document, Dossier dossier, CoreSession session) {
        addTitle(
            document,
            ResourceHelper.getString("pdf.bordereau.historique.attributions.table.caption"),
            SIZE_TITRE2
        );
        XWPFTable table = document.createTable();
        List<HistoriqueAttribution> historiquesAttribution = dossier.getHistoriqueAttribution(session);
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        addTableRow(
            table,
            true,
            FOND_HEADER,
            createHeaderCell(ResourceHelper.getString("bordereau.historique.attributions.label")),
            createHeaderCell(ResourceHelper.getString("bordereau.date.label")),
            createHeaderCell(ResourceHelper.getString("bordereau.type.attribution.label"))
        );
        for (HistoriqueAttribution historiqueAttribution : historiquesAttribution) {
            String ministere = StringUtils.isNotEmpty(historiqueAttribution.getMinAttribution())
                ? organigrammeService
                    .getOrganigrammeNodeById(historiqueAttribution.getMinAttribution(), OrganigrammeType.MINISTERE)
                    .getLabel()
                : "";

            addTableRow(
                table,
                false,
                createTextCell(ministere, false),
                createDateCell(historiqueAttribution.getDateAttribution()),
                createTextCell(historiqueAttribution.getTypeAttribution())
            );
        }
    }

    private void genererTableauAllotissement(XWPFDocument document, Dossier dossier, CoreSession session) {
        List<Question> questions = null;
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), session);
        if (allotissement != null && CollectionUtils.isNotEmpty(allotissement.getIdDossiers())) {
            questions = allotissementService.getQuestionAllotiesWithOrderOrigineNumero(session, allotissement);
            addTitle(document, ResourceHelper.getString("dossier.onglet.allotissement"), SIZE_TITRE1);
            XWPFTable table = document.createTable();
            addTableRow(
                table,
                true,
                FOND_HEADER,
                createHeaderCell(ResourceHelper.getString("allotissement.header.numDossier")),
                createHeaderCell(ResourceHelper.getString("allotissement.header.auteur")),
                createHeaderCell(ResourceHelper.getString("allotissement.header.etat")),
                createHeaderCell(ResourceHelper.getString("allotissement.header.motCle"))
            );
            for (Question question : questions) {
                addTableRow(
                    table,
                    false,
                    createTextCell(question.getSourceNumeroQuestion()),
                    createTextCell(question.getNomCompletAuteur()),
                    createTextCell(question.getEtatQuestion(session).getNewState()),
                    ajoutTexteList(question.getMotsClef())
                );
            }
            addBreakPage(document);
        }
    }

    private void genererTableauDossierConnexe(XWPFDocument document, Dossier dossier, CoreSession session) {
        Question question = dossier.getQuestion(session);
        QuestionConnexeService questionConnexeService = ReponsesServiceLocator.getQuestionConnexeService();
        Map<String, List<String>> map = questionConnexeService.getMinisteresMap(question, session);
        if (map.size() > 0) {
            addTitle(document, ResourceHelper.getString("dossier.onglet.connexe"), SIZE_TITRE1);
            XWPFTable table = document.createTable();

            addTableRow(
                table,
                true,
                FOND_HEADER,
                createHeaderCell(ResourceHelper.getString("dossier.connexe.ministere.column.header.ministere")),
                createHeaderCell(ResourceHelper.getString("dossier.connexe.ministere.column.header.numero.dossier")),
                createHeaderCell(ResourceHelper.getString("dossier.connexe.ministere.column.header.auteur")),
                createHeaderCell(ResourceHelper.getString("dossier.connexe.ministere.column.header.etat")),
                createHeaderCell(
                    ResourceHelper.getString("dossier.connexe.ministere.column.header.indexation.principale")
                )
            );

            for (Entry<String, List<String>> entry : map.entrySet()) {
                List<String> questionIds = questionConnexeService.getMinisteresDossiersConnexe(
                    question,
                    entry.getKey(),
                    session
                );
                for (String id : questionIds) {
                    Question selectedQuestion = session.getDocument(new IdRef(id)).getAdapter(Question.class);
                    addTableRow(
                        table,
                        false,
                        createTextCell(entry.getKey()),
                        createTextCell(selectedQuestion.getSourceNumeroQuestion()),
                        createTextCell(selectedQuestion.getNomCompletAuteur()),
                        createTextCell(selectedQuestion.getEtatQuestion(session).getNewState()),
                        createTextCell(selectedQuestion.getMotsCles())
                    );
                }
            }
            addBreakPage(document);
        }
    }

    private void genererTableauJournal(XWPFDocument document, Dossier dossier) {
        JournalService journalService = STServiceLocator.getJournalService();
        List<LogEntry> logEntryList = journalService.queryDocumentAllLogs(
            Collections.singletonList(dossier.getDocument().getId()),
            null,
            0,
            0,
            null
        );
        if (logEntryList != null && CollectionUtils.isNotEmpty(logEntryList)) {
            addTitle(document, ResourceHelper.getString("dossier.onglet.journal"), SIZE_TITRE1);
            XWPFTable table = document.createTable();

            addTableRow(
                table,
                true,
                FOND_HEADER,
                createHeaderCell(ResourceHelper.getString("pdf.journal.column.header.date")),
                createHeaderCell(ResourceHelper.getString("pdf.journal.column.header.utilisateur")),
                createHeaderCell(ResourceHelper.getString("pdf.journal.column.header.poste")),
                createHeaderCell(ResourceHelper.getString("pdf.journal.column.header.categorie")),
                createHeaderCell(ResourceHelper.getString("pdf.journal.column.header.commentaire"))
            );

            for (LogEntry logEntry : logEntryList) {
                addTableRow(
                    table,
                    false,
                    createDateCell(logEntry.getEventDate()),
                    createTextCell(logEntry.getPrincipalName()),
                    createTextCell(logEntry.getDocPath()),
                    createTextCell(logEntry.getCategory()),
                    createTextCell(ResourceHelper.translateKeysInString(logEntry.getComment(), SPACE))
                );
            }
        }
    }

    private void genererBordereauIndexationComplementaire(XWPFDocument document, Dossier dossier, CoreSession session) {
        Question question = dossier.getQuestion(session);
        if (VocabularyConstants.QUESTION_ORIGINE_AN.equals(question.getOrigineQuestion())) {
            addTableRowIndexationAN(
                document,
                "bordereau.indexation.bloc",
                question.getIndexationComplAssNatRubrique(),
                question.getIndexationComplAssNatTeteAnalyse(),
                question.getIndexationComplAssNatAnalyses()
            );
        }

        if (VocabularyConstants.QUESTION_ORIGINE_SENAT.equals(question.getOrigineQuestion())) {
            addTableRowIndexationSenat(
                document,
                "bordereau.indexation.bloc",
                question.getIndexationComplSenatQuestionRubrique(),
                question.getIndexationComplSenatQuestionThemes(),
                question.getIndexationComplSenatQuestionRenvois()
            );
        }
        addTableRowIndexationMotCles(document, question.getIndexationComplMotsClefMinistere());
    }

    private void addTableRowIndexationSenat(
        XWPFDocument document,
        String title,
        List<String> rubriques,
        List<String> themes,
        List<String> renvois
    ) {
        boolean afficherIndexSenat =
            CollectionUtils.isNotEmpty(rubriques) ||
            CollectionUtils.isNotEmpty(themes) ||
            CollectionUtils.isNotEmpty(renvois);
        if (afficherIndexSenat) {
            addTitle(document, ResourceHelper.getString(title), SIZE_TITRE2);
            addTitle(document, ResourceHelper.getString("bordereau.senat.label"), SIZE_TITRE3);
            XWPFTable table = createInvisibleTable(document);
            addTableRow(table, true, createTextCell(""), createTextCell(""));
            if (CollectionUtils.isNotEmpty(rubriques)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.rubrique.label")),
                    ajoutTexteList(rubriques)
                );
            }
            if (CollectionUtils.isNotEmpty(themes)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.theme.label")),
                    ajoutTexteList(themes)
                );
            }
            if (CollectionUtils.isNotEmpty(renvois)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.renvoi.label")),
                    ajoutTexteList(renvois)
                );
            }
        }
    }

    private void addTableRowIndexationAN(
        XWPFDocument document,
        String title,
        List<String> rubriques,
        List<String> teteAnalyses,
        List<String> titres
    ) {
        boolean afficherIndexAN =
            CollectionUtils.isNotEmpty(rubriques) ||
            CollectionUtils.isNotEmpty(teteAnalyses) ||
            CollectionUtils.isNotEmpty(titres);
        if (afficherIndexAN) {
            addTitle(document, ResourceHelper.getString(title), SIZE_TITRE2);
            addTitle(document, ResourceHelper.getString("bordereau.an.label"), SIZE_TITRE3);
            XWPFTable table = createInvisibleTable(document);
            addTableRow(table, true, createTextCell(""), createTextCell(""));
            if (CollectionUtils.isNotEmpty(rubriques)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.rubrique.label")),
                    ajoutTexteList(rubriques)
                );
            }
            if (CollectionUtils.isNotEmpty(teteAnalyses)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.tete.analyse.label")),
                    ajoutTexteList(teteAnalyses)
                );
            }

            if (CollectionUtils.isNotEmpty(titres)) {
                addTableRow(
                    table,
                    false,
                    createHeaderCell(ResourceHelper.getString("bordereau.indexation.titre.label")),
                    ajoutTexteList(titres)
                );
            }
        }
    }

    private void addTableRowIndexationMotCles(XWPFDocument document, List<String> motsCles) {
        if (motsCles != null && CollectionUtils.isNotEmpty(motsCles)) {
            addTitle(document, ResourceHelper.getString("bordereau.ministeres.label"), SIZE_TITRE3);
            XWPFTable tab = createInvisibleTable(document);
            addTableRow(
                tab,
                true,
                createHeaderCell(ResourceHelper.getString("bordereau.indexation.mot.cles.label")),
                ajoutTexteList(motsCles)
            );
        }
    }

    @Override
    public File generateDossierPdf(CoreSession session, Dossier dossier) throws IOException {
        if (dossier == null) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, "Dossier null !");
            return null;
        }
        Question question = dossier.getQuestion(session);
        try (XWPFDocument document = new XWPFDocument()) {
            setOrientationPaysage(document);
            ajoutParagrapheTexte(
                document,
                ResourceHelper.getString("parapheur.question.bloc"),
                htmlToText(question.getTexteQuestion())
            );
            Reponse reponse = dossier.getReponse(session);
            Optional
                .ofNullable(reponse.getTexteReponse())
                .ifPresent(
                    texteReponse -> {
                        addBreak(document);
                        ajoutParagrapheTexte(
                            document,
                            ResourceHelper.getString("parapheur.reponse.bloc"),
                            htmlToText(texteReponse)
                        );
                    }
                );
            Optional
                .ofNullable(reponse.getCurrentErratum())
                .ifPresent(
                    erratum -> {
                        addBreak(document);
                        ajoutParagrapheTexte(
                            document,
                            ResourceHelper.getString("parapheur.erratum.reponse"),
                            htmlToText(erratum)
                        );
                    }
                );
            addBreakPage(document);
            genererTableauFeuilleDeRoute(document, dossier, session);
            genererDonneesBordereau(document, dossier, session);
            genererTableauAllotissement(document, dossier, session);
            genererTableauDossierConnexe(document, dossier, session);
            genererTableauJournal(document, dossier);
            // Ajout du fond de dossier
            File pdfDossierFile = generatePdf(session, dossier, document);
            List<DocumentModel> fddFiles = ReponsesServiceLocator
                .getFondDeDossierService()
                .getFddDocuments(session, dossier);
            List<File> pdfFondFiles = convertDocumentModelsBlobsToPdf(fddFiles);
            if (CollectionUtils.isNotEmpty(pdfFondFiles)) {
                pdfFondFiles.add(0, pdfDossierFile);

                String mergedPdfFilename = FileUtils.generateCompletePdfFilename(
                    "DossierFinal_" + generateDossierFileName(session, dossier)
                );
                pdfDossierFile = mergePdfs(FileUtils.getAppTmpFilePath(mergedPdfFilename), pdfFondFiles);
            }

            return pdfDossierFile;
        }
    }

    @Override
    protected String generateDossierFileName(CoreSession session, Dossier dossier) {
        String dossierRef = dossier.getQuestion(session).getSourceNumeroQuestion().replaceAll(" ", "_");
        return ("Dossier_" + dossierRef + '-' + SolonDateConverter.DATETIME_UNDER_SECOND_UNDER.formatNow());
    }
}
