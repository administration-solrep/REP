package fr.dila.reponses.ui.services.impl;

import com.google.common.base.Joiner;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.constant.ReponsesStatsEventConstants;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.impl.AbstractSSStatistiquesUIServiceImpl;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public class StatistiquesUIServiceImpl extends AbstractSSStatistiquesUIServiceImpl implements StatistiquesUIService {
    private static final String[] REPORTS_MULTI_MIN_NAME = {
        "MIN01",
        "MIN02",
        "MIN03",
        "MIN06",
        "MIN15",
        "SGG02",
        "SGG06",
        "SGG07",
        "SGG11"
    };

    private static final String[] REPORTS_MULTI_DIR_NAME = { "MIN16", "MIN17" };

    @Override
    public String getGeneratedReportDirectory() {
        return ReponsesConfigConstant.REPONSES_GENERATED_REPORT_DIRECTORY;
    }

    @Override
    public ReportStats getReportStats() {
        StatsService statsService = ReponsesServiceLocator.getStatsService();
        return statsService.getDailyReportStats();
    }

    private boolean isSggStatDisplayed(BirtReport report, SSPrincipal ssPrincipal) {
        Collection<ReportProperty> properties = report.getProperties().values();
        for (ReportProperty property : properties) {
            if (
                ReportProperty.TYPE_FUNCTION.equals(property.getType()) &&
                (!ssPrincipal.isMemberOf(property.getName()) || !property.getValue().equals("true"))
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected List<BirtReport> getStatistiqueReports(SSPrincipal ssPrincipal) {
        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        Map<String, BirtReport> allReports = birtGenerationService.getReports().getBirtReportMap();

        return allReports
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith("stat") && isSggStatDisplayed(entry.getValue(), ssPrincipal))
            .map(Entry::getValue)
            .sorted(Comparator.comparing(BirtReport::getTitle))
            .collect(Collectors.toList());
    }

    @Override
    public String getHtmlReportContent(SpecificContext context, String reportDirectoryName) {
        ConfigService configService = STServiceLocator.getConfigService();
        String generatedReportDirectory = configService.getValue(
            ReponsesConfigConstant.REPONSES_GENERATED_REPORT_DIRECTORY
        );

        File reportDir = new File(
            generatedReportDirectory,
            fr.dila.st.core.util.FileUtils.sanitizePathTraversal(reportDirectoryName)
        );
        if (reportDir.exists()) {
            try (Stream<Path> reportPaths = Files.list(reportDir.toPath())) {
                return reportPaths
                    .filter(path -> path.toFile().isFile())
                    .findFirst()
                    .map(StatistiquesUIServiceImpl::getFileContent)
                    .orElse(null);
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        }

        return null;
    }

    @Override
    public String generateReport(BirtReport birtReport, Map<String, String> params) {
        Map<String, Serializable> scalarValues = new HashMap<>();

        for (ReportProperty property : getScalarProperties(birtReport)) {
            String propertyName = property.getName();

            switch (property.getType()) {
                case ReportProperty.TYPE_SCALAR_VALUE:
                    // On envoie directement la valeur renseignée dans le paramètre
                    scalarValues.put(propertyName, property.getValue());
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN:
                    scalarValues.put(propertyName, params.get(ReponsesStatistiques.QUERY_PARAM_MINISTERE_ID));
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST:
                    scalarValues.put(propertyName, params.get(ReponsesStatistiques.QUERY_PARAM_UNITE_ID));
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST:
                    scalarValues.put(propertyName, params.get(ReponsesStatistiques.QUERY_PARAM_POSTE_ID));
                    break;
                default:
                // On n'envoie pas cette propriété à Birt
            }
        }

        return generateReportFile(birtReport, scalarValues);
    }

    @Override
    public Blob generateReport(SpecificContext context) {
        Map<String, Serializable> scalarValues = new HashMap<>();
        String statId = context.getFromContextData(SSContextDataKey.STAT_ID);
        BirtReport birtReport = getBirtReport(statId);

        String organigrammeId = context.getFromContextData(ReponsesContextDataKey.ORGANIGRAMME_ID);

        SSOrganigrammeService organigrammeService = (SSOrganigrammeService) STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(organigrammeId);

        for (ReportProperty property : getScalarProperties(birtReport)) {
            String propertyName = property.getName();

            switch (property.getType()) {
                case ReportProperty.TYPE_SCALAR_VALUE:
                    // On envoie directement la valeur renseignée dans le paramètre
                    scalarValues.put(propertyName, property.getValue());
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN:
                    if (node != null && OrganigrammeType.MINISTERE.equals(node.getType())) {
                        scalarValues.put(propertyName, node.getId());
                    }
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST:
                    if (
                        node != null &&
                        (
                            OrganigrammeType.UNITE_STRUCTURELLE.equals(node.getType()) ||
                            OrganigrammeType.DIRECTION.equals(node.getType())
                        )
                    ) {
                        scalarValues.put(propertyName, node.getId());
                    }
                    break;
                case ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST:
                    if (node != null && OrganigrammeType.POSTE.equals(node.getType())) {
                        scalarValues.put(propertyName, node.getId());
                    }
                    break;
                default:
                // On n'envoie pas cette propriété à Birt
            }
        }

        BirtOutputFormat outputFormat = context.getFromContextData(SSContextDataKey.BIRT_OUTPUT_FORMAT);
        return generateReportFile(birtReport, scalarValues, outputFormat);
    }

    @Override
    public boolean hasRequiredParameters(BirtReport birtReport, Map<String, String> params) {
        for (ReportProperty property : getScalarProperties(birtReport)) {
            if (
                ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN.equals(property.getType()) &&
                StringUtils.isEmpty(params.get(ReponsesStatistiques.QUERY_PARAM_MINISTERE_ID))
            ) {
                return false;
            }

            if (
                ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST.equals(property.getType()) &&
                StringUtils.isEmpty(params.get(ReponsesStatistiques.QUERY_PARAM_POSTE_ID))
            ) {
                return false;
            }

            if (
                ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST.equals(property.getType()) &&
                StringUtils.isEmpty(params.get(ReponsesStatistiques.QUERY_PARAM_UNITE_ID))
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getReportUrl(BirtReport birtReport, Map<String, String> params) {
        StringBuilder buffer = new StringBuilder(birtReport.getId());

        if (params != null && !params.isEmpty()) {
            List<String> serializedParams = new ArrayList<>();
            for (Entry<String, String> entry : params.entrySet()) {
                if (StringUtils.isNotEmpty(entry.getValue())) {
                    serializedParams.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            buffer.append("?");
            Joiner joiner = Joiner.on("&").skipNulls();
            buffer.append(joiner.join(serializedParams));
        }
        return buffer.toString();
    }

    @Override
    public Boolean hasRightToExport(SpecificContext context) {
        return context.getSession().getPrincipal().isMemberOf(ReponsesBaseFunctionConstant.DROIT_MASS_EXPORT_STATS);
    }

    /**
     * Déclenche l'évènement d'export en masse des statistiques
     */
    @Override
    public void doZipExport(SpecificContext context) {
        CoreSession session = context.getSession();
        Collection<String> reportIds = context.getFromContextData(STContextDataKey.IDS);
        List<BirtReport> reports;
        if (CollectionUtils.isEmpty(reportIds)) {
            reports = getStatistiqueReports((SSPrincipal) session.getPrincipal());
        } else {
            reports = reportIds.stream().map(this::getBirtReport).collect(Collectors.toList());
        }

        if (reports.isEmpty()) {
            throw new NuxeoException("une liste de rapports a généré est attendu");
        }

        final List<BirtOutputFormat> formats = Arrays.asList(BirtOutputFormat.XLS);
        fireExports(context, reports, formats, ReponsesStatsEventConstants.EXPORT_STATS_EXPORT_EVENT);
    }

    /**
     * Génère tous les fichiers du rapport en cours au format excel
     */
    @Override
    public void generateAllExcel(SpecificContext context) {
        generateAllStatInFormat(context, ReponsesStatsEventConstants.FORMAT_XLS_VALUE);
    }

    /**
     * Génère tous les fichiers du rapport en cours au format pdf
     */
    @Override
    public void generateAllPdf(SpecificContext context) {
        generateAllStatInFormat(context, ReponsesStatsEventConstants.FORMAT_PDF_VALUE);
    }

    /**
     * Permet la génération de tous les fichiers du rapport en cours au format
     * passé en paramètre
     *
     * @param format
     */
    protected void generateAllStatInFormat(SpecificContext context, final String format) {
        String statId = context.getFromContextData(SSContextDataKey.STAT_ID);
        Objects.requireNonNull(statId, "un rapport à générer est attendu");

        BirtReport report = getBirtReport(statId);
        Objects.requireNonNull(report, "un rapport à générer est attendu");

        fireExports(
            context,
            Arrays.asList(report),
            Arrays.asList(BirtOutputFormat.valueOf(format)),
            ReponsesStatsEventConstants.EXPORT_STATS_MAIL_EVENT
        );
    }

    /**
     * Renseigne les paramètres de l'evènement et déclenche l'evenement d'export
     *
     * @param reports
     * @throws ClientException
     */
    protected void fireExports(
        SpecificContext context,
        final List<BirtReport> reports,
        final List<BirtOutputFormat> formats,
        String event
    ) {
        CoreSession session = context.getSession();

        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        final String userWorkspacePath = STServiceLocator
            .getUserWorkspaceService()
            .getCurrentUserPersonalWorkspace(session)
            .getPathAsString();

        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        if (
            !exportService.isCurrentlyExportingStat(session, ssPrincipal, userWorkspacePath) &&
            exportService.flagInitExportStatForUser(session, ssPrincipal, userWorkspacePath)
        ) {
            final HashMap<String, String> reportsHaveMultiExportMap = new HashMap<>();
            final HashMap<String, String> reportsNameMap = new HashMap<>();
            final HashMap<String, String> reportsTitleMap = new HashMap<>();

            // On parcourt les documents stats pour determiner s'ils sont multi
            // ministere
            // et récupérer nom et titre
            for (final BirtReport report : reports) {
                final String idReport = report.getId();
                final String nameReport = report.getId();
                final String titleReport = report.getTitle();

                reportsNameMap.put(idReport, nameReport);
                reportsTitleMap.put(idReport, titleReport);

                if (isNameMatchesReportsMultiMin(titleReport)) {
                    reportsHaveMultiExportMap.put(idReport, "MIN");
                } else if (isNameMatchesReportsMultiDir(titleReport)) {
                    reportsHaveMultiExportMap.put(idReport, "DIR");
                } else {
                    reportsHaveMultiExportMap.put(idReport, "");
                }
            }

            // event
            final EventProducer eventProducer = STServiceLocator.getEventProducer();
            final Map<String, Serializable> eventProperties = new HashMap<>();

            eventProperties.put(ReponsesStatsEventConstants.USER_PROPERTY, ssPrincipal);
            eventProperties.put(ReponsesStatsEventConstants.USER_WS_PATH_PROPERTY, userWorkspacePath);
            eventProperties.put(ReponsesStatsEventConstants.FORMATS_EXPORT_PROPERTY, new ArrayList<>(formats));
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_NAMES_PROPERTY, reportsNameMap);
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_TITLES_PROPERTY, reportsTitleMap);
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_HAVE_MULTI_PROPERTY, reportsHaveMultiExportMap);

            final InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
            eventProducer.fireEvent(eventContext.newEvent(event));
            String toastMsg = ReponsesStatsEventConstants.EXPORT_STATS_EXPORT_EVENT.equals(event)
                ? "label.stats.mail.export.success"
                : "export.mail.succes.toast";
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString(toastMsg));
        } else {
            // On affiche à l'utilisateur qu'un export a déjà été demandé et est
            // en cours
            final String dateRequest = exportService.getExportStatHorodatageRequest(
                session,
                ssPrincipal,
                userWorkspacePath
            );
            final String info = "Un export a déjà été demandé le " + dateRequest;
            context.getMessageQueue().addWarnToQueue(info);
        }
    }

    /**
     *
     * @return true si le rapport est multi ministere
     */
    private boolean isNameMatchesReportsMultiMin(final String reportName) {
        return Stream.of(REPORTS_MULTI_MIN_NAME).anyMatch(name -> StringUtils.containsIgnoreCase(reportName, name));
    }

    /**
     * @return true si le rapport est multi directions
     */
    private boolean isNameMatchesReportsMultiDir(final String reportName) {
        return Stream.of(REPORTS_MULTI_DIR_NAME).anyMatch(name -> StringUtils.containsIgnoreCase(reportName, name));
    }

    @Override
    public Blob getGeneratedExport(SpecificContext context) {
        return getExportedBlob(context, true);
    }

    @Override
    public String getGeneratedExportName(SpecificContext context) {
        return Optional.ofNullable(getExportedBlob(context, false)).map(Blob::getFilename).orElse("");
    }

    private Blob getExportedBlob(SpecificContext context, boolean addNoExportInfoMessage) {
        CoreSession session = context.getSession();
        SSPrincipal principal = (SSPrincipal) session.getPrincipal();

        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        final String userWorkspacePath = STServiceLocator
            .getUserWorkspaceService()
            .getCurrentUserPersonalWorkspace(session)
            .getPathAsString();

        if (exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath)) {
            context.getMessageQueue().addInfoToQueue("Archive en cours de génération...");
            return null;
        }

        final DocumentModel exportDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        if (exportDoc == null) {
            if (addNoExportInfoMessage) {
                context.getMessageQueue().addWarnToQueue("Récupération de l'export impossible : aucun export existant");
            }
            return null;
        }

        return exportDoc.getAdapter(ExportDocument.class).getFileContent();
    }

    @Override
    public void deleteOldExportedStats(SpecificContext context) {
        CoreSession session = context.getSession();
        SSPrincipal principal = (SSPrincipal) session.getPrincipal();
        ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        final String userWorkspacePath = STServiceLocator
            .getUserWorkspaceService()
            .getCurrentUserPersonalWorkspace(session)
            .getPathAsString();

        DocumentModel exportDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);

        if (exportDoc != null) {
            session.removeDocument(exportDoc.getRef());
            session.save();
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("label.stats.suppression.ok"));
        } else {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("label.stats.erreur.suppression"));
        }
    }

    @Override
    public boolean hasRightToViewSggStat(SpecificContext context) {
        return isSggStatDisplayed(
            context.getFromContextData(SSContextDataKey.BIRT_REPORT),
            (SSPrincipal) context.getSession().getPrincipal()
        );
    }
}
