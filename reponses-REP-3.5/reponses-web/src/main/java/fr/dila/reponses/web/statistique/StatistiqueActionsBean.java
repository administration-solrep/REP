package fr.dila.reponses.web.statistique;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.reporting.api.BirtReportInstance;
import org.nuxeo.ecm.platform.reporting.api.ReportInstance;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import com.google.common.base.Objects;

import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesStatsEventConstants;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.ZipUtil;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam permettant de gérer les statistiques.
 * 
 * @author jtremeaux
 */
@Name("statistiqueActions")
@Scope(ScopeType.CONVERSATION)
public class StatistiqueActionsBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8870324536789519518L;

    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(StatistiqueActionsBean.class);

    @In(create = true, required = false)
    protected transient ActionManager actionManager;

    @In(create = true, required = false)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient WebActions webActions;

    @In(required = true, create = false)
    protected SSPrincipal ssPrincipal;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient DocumentsListsManager documentsListsManager;

    private static final String[] REPORTS_MULTI_MIN_NAME = { "MIN01", "MIN02", "MIN03", "MIN06", "MIN15", "SGG02", "SGG06", "SGG07", "SGG11" };

	private static final String[] REPORTS_MULTI_DIR_NAME = { "MIN16", "MIN17" };

    /**
     * Valeur de organigrammeParametre (cf reponses-content-template-contrib.xml) pour autoriser la sélection de MINISTERE
     */
    private static final String[] PARAMETRE_ORGANIGRAMME_ALLOW_MINISTERE = { "MINISTERE", "POSTE" };

    /**
     * Valeur de parametreOrganigramme (cf reponses-content-template-contrib.xml) pour autoriser la sélection de DIRECTION
     */
    private static final String[] PARAMETRE_ORGANIGRAMME_ALLOW_DIRECTION = { "DIRECTION" };

    /**
     * Valeur de parametreOrganigramme (cf reponses-content-template-contrib.xml) pour autoriser la sélection de POSTE
     */
    private static final String[] PARAMETRE_ORGANIGRAMME_ALLOW_POSTE = { "POSTE" };

    /**
     * Nom du paramètre destiné au rapport Birt
     */
    private String parameterType = null;

    /**
     * Valeur du paramètre destiné au rapport Birt
     */
    private String parameterValue = null;

    /**
     * Navigue vers l'espace statistique.
     * 
     * @return Ecran d'accueil de l'espace statistique
     * @throws ClientException
     */
    public String navigateToEspaceStatistique() throws ClientException {
        // Renseigne le menu du haut
        final Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_STATISTIQUES);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);
        navigationWebActions.setLeftPanelIsOpened(true);
        // Renseigne le menu de gauche
        navigationWebActions.setCurrentLeftMenuAction(null);
        corbeilleActions.setCurrentView(ReponsesViewConstant.ESPACE_STATISTIQUE);

        navigationContext.resetCurrentDocument();

        return ReponsesViewConstant.ESPACE_STATISTIQUE;
    }

    /**
     * Navigation vers un rapport de statistique Birt.
     * 
     * @param reportDoc
     * @param parameterType
     * @param parameterValue
     * @return
     * @throws ClientException
     */
    public String navigateToReport(final DocumentModel reportDoc, final String parameterType, final String parameterValue) throws ClientException {
        navigationContext.setCurrentDocument(reportDoc);
        webActions.setCurrentTabAction(actionManager.getAction("TAB_BIRT_VIEW"));
        this.parameterType = parameterType;
        this.parameterValue = parameterValue;
        return ReponsesViewConstant.ESPACE_STATISTIQUE;
    }

    /**
     * Getter de parameterType
     * 
     * @return parameterType
     */
    public String getParameterType() {
        return parameterType;
    }

    /**
     * Getter de parameterValue
     * 
     * @return parameterValue
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * 
     * @return
     * @throws ClientException
     */
    public String getQuery() throws ClientException {
        String s;
        if (ssPrincipal.isAdministrator() || ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DROIT_VISIBILITE_STATS_SGG)) {
            s = "SELECT d.ecm:uuid as id FROM BirtReport as d WHERE  d.ecm:parentId = ?";
        } else {
            s = "SELECT d.ecm:uuid as id FROM BirtReport as d, BirtReportModel as brp  WHERE brp.ecm:uuid = d.birt:modelRef AND (brp.birtmodel:droitVisibiliteRestraintSGG = 0 OR brp.birtmodel:droitVisibiliteRestraintSGG is null)  AND d.ecm:parentId = ?";
        }

        return s;
    }

    /**
     * 
     * @return
     * @throws ClientException
     */
    public List<Object> getQueryParameter() throws ClientException {
        final List<Object> list = new ArrayList<Object>();
        list.add(SSServiceLocator.getSSBirtService().getBirtReportRootId(documentManager));
        return list;
    }

    /**
     * Déclenche l'évènement d'export en masse des statistiques
     */
    public void masseExport() {
        try {
            final List<DocumentModel> selection = documentsListsManager.getWorkingList("STATISTIQUES_SELECTION");
            if (selection.size() == 0) {
                // Si la liste de selection de l'utilisateur est vide, on prend par défaut toutes les statistiques
                selection.addAll(QueryUtils.doUFNXQLQueryAndFetchForDocuments(documentManager, "BirtReport", getQuery(),
                        new Object[] { SSServiceLocator.getSSBirtService().getBirtReportRootId(documentManager) }));
            }
            final ArrayList<String> formats = new ArrayList<String>();
            formats.add(ReponsesStatsEventConstants.FORMAT_XLS_VALUE);
            formats.add(ReponsesStatsEventConstants.FORMAT_PDF_VALUE);
            fireExports(selection, formats);
            selection.clear();
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la demande d'export");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
    }

    /**
     * Renseigne les paramètres de l'evènement et déclenche l'evenement d'export
     * @param exports
     * @throws ClientException
     */
    protected void fireExports(final List<DocumentModel> exports, final ArrayList<String> formats) throws ClientException {
        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        final String userWorkspacePath = STServiceLocator.getUserWorkspaceService().getCurrentUserPersonalWorkspace(documentManager, null)
                .getPathAsString();

        if (!exportService.isCurrentlyExportingStat(documentManager, ssPrincipal, userWorkspacePath)
                && exportService.flagInitExportStatForUser(documentManager, ssPrincipal, userWorkspacePath)) {
            final HashMap<String, String> reportsHaveMultiExportMap = new HashMap<String, String>();
            final HashMap<String, String> reportsNameMap = new HashMap<String, String>();
            final HashMap<String, String> reportsTitleMap = new HashMap<String, String>();

            // On parcourt les documents stats pour determiner s'ils sont multi ministere 
            // et récupérer nom et titre
            for (final DocumentModel reportDoc : exports) {
                final BirtReportInstance report = (BirtReportInstance) reportDoc.getAdapter(ReportInstance.class);
                final String idReport = reportDoc.getId();
                final String nameReport = report.getModel().getReportName();
                final String titleReport = report.getDoc().getTitle();

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
            final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();

            eventProperties.put(ReponsesStatsEventConstants.USER_PROPERTY, ssPrincipal);
            eventProperties.put(ReponsesStatsEventConstants.USER_WS_PATH_PROPERTY, userWorkspacePath);
            eventProperties.put(ReponsesStatsEventConstants.FORMATS_EXPORT_PROPERTY, formats);
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_NAMES_PROPERTY, reportsNameMap);
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_TITLES_PROPERTY, reportsTitleMap);
            eventProperties.put(ReponsesStatsEventConstants.REPORTS_HAVE_MULTI_PROPERTY, reportsHaveMultiExportMap);

            final InlineEventContext eventContext = new InlineEventContext(documentManager, ssPrincipal, eventProperties);
            eventProducer.fireEvent(eventContext.newEvent(ReponsesStatsEventConstants.EXPORT_STATS_EVENT));
            facesMessages.add(StatusMessage.Severity.INFO, "La demande d'export a été prise en compte.");
        } else {
            // On affiche à l'utilisateur qu'un export a déjà été demandé et est en cours
            final String dateRequest = exportService.getExportStatHorodatageRequest(documentManager, ssPrincipal, userWorkspacePath);
            final String info = "Un export a déjà été demandé le " + dateRequest;
            facesMessages.add(StatusMessage.Severity.INFO, info);
        }
    }

    /**
     * Génère tous les fichiers du rapport en cours au format excel
     */
    public void generateAllExcel() {
    	webActions.setCurrentTabAction(actionManager.getAction("TAB_BIRT_VIEW"));
        generateAllStatInFormat(ReponsesStatsEventConstants.FORMAT_XLS_VALUE);
    }

    /**
     * Génère tous les fichiers du rapport en cours au format pdf
     */
    public void generateAllPdf() {
    	webActions.setCurrentTabAction(actionManager.getAction("TAB_BIRT_VIEW"));
        generateAllStatInFormat(ReponsesStatsEventConstants.FORMAT_PDF_VALUE);
    }

    /**
     * Permet la génération de tous les fichiers du rapport en cours au format passé en paramètre 
     * @param format
     */
    protected void generateAllStatInFormat(final String format) {
        final ArrayList<String> formats = new ArrayList<String>();
        formats.add(format);
        final List<DocumentModel> reports = new ArrayList<DocumentModel>();
        reports.add(navigationContext.getCurrentDocument());
        try {
            fireExports(reports, formats);
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la demande d'export");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
    }

    /**
     * Détermine si la statistique en cours fait partie de celles qui sont par ministère ou par direction
     * @return vrai si multiministere ou multidirection, faux sinon
     */
    public boolean isMulti() {
        final DocumentModel reportDoc = navigationContext.getCurrentDocument();
        final BirtReportInstance report = (BirtReportInstance) reportDoc.getAdapter(ReportInstance.class);
        try {
            final String reportName = report.getDoc().getTitle();
            return isNameMatchesReportsMultiMin(reportName) || isNameMatchesReportsMultiDir(reportName);
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la récupération du rapport");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
        return false;
    }

    /**
     * parcourt la liste des statistiques multi ministères et compare avec le nom de rapport transmis en paramètre
     * @param reportName
     * @return
     */
    protected boolean isNameMatchesReportsMultiMin(final String reportName) {
        for (final String name : REPORTS_MULTI_MIN_NAME) {
            if (StringUtil.containsIgnoreCase(reportName, name)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * parcourt la liste des statistiques multi directions et compare avec le nom de rapport transmis en paramètre
	 * 
	 * @param reportName
	 * @return
	 */
	protected boolean isNameMatchesReportsMultiDir(final String reportName) {
		for (final String name : REPORTS_MULTI_DIR_NAME) {
			if (StringUtil.containsIgnoreCase(reportName, name)) {
				return true;
			}
		}
		return false;
	}

    public void getGeneratedExport() {
        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        try {
            final String userWorkspacePath = STServiceLocator.getUserWorkspaceService().getCurrentUserPersonalWorkspace(documentManager, null)
                    .getPathAsString();
            if (!exportService.isCurrentlyExportingStat(documentManager, ssPrincipal, userWorkspacePath)) {
                final DocumentModel exportDoc = exportService.getExportStatDocForUser(documentManager, ssPrincipal, userWorkspacePath);
                if (exportDoc != null) {
                    final ExportDocument exportStat = exportDoc.getAdapter(ExportDocument.class);
                    final Blob zipBlob = exportStat.getFileContent();
                    if (zipBlob != null) {
                        final FacesContext facesContext = FacesContext.getCurrentInstance();
                        final HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                        ZipUtil.downloadZip(response, zipBlob);

                        facesContext.responseComplete();
                        return;
                    }
                }
                facesMessages.add(StatusMessage.Severity.WARN, "Récupération de l'export impossible : aucun export existant");
            } else {
                facesMessages.add(StatusMessage.Severity.INFO, "Récupération de l'export impossible : export en cours");
            }
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la récupération du rapport");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
    }

    public String getGeneratedExportName() {
        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        try {
            final String userWorkspacePath = STServiceLocator.getUserWorkspaceService().getCurrentUserPersonalWorkspace(documentManager, null)
                    .getPathAsString();
            if (!exportService.isCurrentlyExportingStat(documentManager, ssPrincipal, userWorkspacePath)) {
                final DocumentModel exportDoc = exportService.getExportStatDocForUser(documentManager, ssPrincipal, userWorkspacePath);
                if (exportDoc != null) {
                    final ExportDocument exportStat = exportDoc.getAdapter(ExportDocument.class);
                    final Blob content = exportStat.getFileContent();
                    if (content != null) {
                        return content.getFilename();
                    }
                }
            } else {
                return "Archive en cours de génération...";
            }
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la récupération du rapport");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
        return "";
    }

    public Boolean isExportExisting() {
        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
        try {
            final String userWorkspacePath = STServiceLocator.getUserWorkspaceService().getCurrentUserPersonalWorkspace(documentManager, null)
                    .getPathAsString();
            final DocumentModel exportStatDoc = exportService.getExportStatDocForUser(documentManager, ssPrincipal, userWorkspacePath);

            if (exportStatDoc != null) {
                return true;
            }
        } catch (final ClientException ce) {
            facesMessages.add(StatusMessage.Severity.ERROR, "Erreur dans la récupération du rapport");
            LOGGER.error(documentManager, SSLogEnumImpl.FAIL_EXPORT_STATS_FONC, ce);
        }
        return false;
    }

    public Boolean hasRightToExport() {
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DROIT_MASS_EXPORT_STATS)) {
            return true;
        }
        return false;
    }

	public Boolean isVisibleMinistere(String parametreOrganigramme) {
		return ArrayUtils.contains(PARAMETRE_ORGANIGRAMME_ALLOW_MINISTERE, parametreOrganigramme);
	}

	public Boolean isVisibleDirection(String parametreOrganigramme) {
		return ArrayUtils.contains(PARAMETRE_ORGANIGRAMME_ALLOW_DIRECTION, parametreOrganigramme);
	}

	public Boolean isVisiblePoste(String parametreOrganigramme) {
		return ArrayUtils.contains(PARAMETRE_ORGANIGRAMME_ALLOW_POSTE, parametreOrganigramme);
	}

}
