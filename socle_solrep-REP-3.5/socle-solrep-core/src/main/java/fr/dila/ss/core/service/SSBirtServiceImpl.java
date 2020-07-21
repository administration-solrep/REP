package fr.dila.ss.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.reporting.engine.BirtEngine;
import org.nuxeo.ecm.platform.reporting.report.ReportHelper;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.core.birt.HTMLEmbeddedImageHandler;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;

public class SSBirtServiceImpl implements SSBirtService {

	/**
     * 
     */
	private static final long		serialVersionUID		= 1L;

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER					= STLogFactory.getLog(SSBirtServiceImpl.class);

	private static final String		BIRT_REPORT_ROOT_PATH	= "/case-management/birt-model-route/";

	/**
	 * default constructor
	 */
	public SSBirtServiceImpl() {
		// do nothing
	}

	protected Blob transformFileToBlob(final File file, final String reportName, final String format)
			throws FileNotFoundException {
		Blob blob = FileUtils.createSerializableBlob(new FileInputStream(file), reportName + "." + format, null);
		if (format.equals("xls")) {
			blob.setMimeType("text/plain");
		}
		return blob;
	}

	/**
	 * Génère un rapport à partir d'un ReportDocument dans un format donné
	 * 
	 * @param reportDocument
	 *            le document de base à générer dans le format
	 * @param reportName
	 *            le nom du rapport à générer
	 * @param format
	 *            le format dans lequel sera généré le rapport
	 * @param result
	 *            le fichier résultat du rapport
	 * @param imagesDir
	 *            le repertoire pour les images - peut être null
	 * @return Blob le rapport généré au format donné
	 * @throws EngineException
	 * @throws IOException
	 * @throws ClientException
	 */
	protected File generateReport(final IReportDocument reportDocument, final String reportName, final String format,
			final File fileResult, final File imagesDir) throws EngineException, IOException, ClientException {
		if (reportDocument == null) {
			LOGGER.error(null, STLogEnumImpl.NPE_PARAM_METH_TEC, "reportDocument is null");
			throw new ClientException("reportDocument ne doit pas être null");
		}
		final IRenderTask renderTask = BirtEngine.getBirtEngine().createRenderTask(reportDocument);
		File result;
		if (fileResult == null) {
			result = File.createTempFile("tempFile", "." + format);
			result.deleteOnExit();
		} else {
			result = fileResult;
		}
		final OutputStream out = new FileOutputStream(result);

		if (!format.equals("xls")) {
			final HTMLRenderOption options = new HTMLRenderOption();
			options.setSupportedImageFormats("GIF");
			options.setEmbeddable(true);
			options.setOutputFormat(format);
			if (imagesDir == null) {
				options.setImageHandler(new HTMLEmbeddedImageHandler());
			} else {
				options.setImageHandler(new HTMLServerImageHandler());
				options.setBaseImageURL(imagesDir.getName());
				options.setImageDirectory(imagesDir.getAbsolutePath());
			}
			options.setOutputStream(out);

			renderTask.setRenderOption(options);
		} else {
			final EXCELRenderOption eXCELRenderOption = new EXCELRenderOption();
			eXCELRenderOption.setOutputFormat("xls");
			eXCELRenderOption.setOutputStream(out);

			// set the input parameter of the report
			renderTask.setRenderOption(eXCELRenderOption);
		}
		renderTask.render();
		renderTask.close();
		out.close();
		return result;
	}

	/**
	 * Génère un ReportDocument à partir du reportFile
	 * 
	 * @param reportFile
	 *            le fichier rptDesign utilisé
	 * @param rptDocFile
	 *            le fichier temporaire qui sera utilisé pour le rptDoc
	 * @param inputValues
	 *            les valeurs à utiliser pour générer le rapport
	 * @return IReportDocument le document rptDocument généré
	 * @throws Exception
	 */
	protected IReportDocument generateReportDoc(String reportFile, final File rptDocFile,
			Map<String, String> inputValues) throws Exception {
		if (rptDocFile == null) {
			LOGGER.info(STLogEnumImpl.NPE_PARAM_METH_TEC, "rptDocFile est null");
			throw new SSException("rptDocFile ne doit pas être null");
		}
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(reportFile);
		if (is == null) {
			is = Framework.getResourceLoader().getResourceAsStream(reportFile);
		}
		final IReportRunnable nuxeoReportRunnable = ReportHelper.getNuxeoReport(is);
		final IRunTask runTask = BirtEngine.getBirtEngine().createRunTask(nuxeoReportRunnable);

		runTask.setParameterValues(inputValues);
		runTask.run(rptDocFile.getAbsolutePath());
		runTask.close();
		/* Récupération du rptDocument */
		return BirtEngine.getBirtEngine().openReportDocument(rptDocFile.getAbsolutePath());
	}

	protected void generateReport(final OutputStream outputStream, final String fileName,
			final Map<String, String> inputValues, final String name, final String outPutFormat) throws Exception {
		final File rptDocFile = File.createTempFile("tempFile", ".rptDocument");
		IReportDocument nuxeoReport = generateReportDoc(fileName, rptDocFile, inputValues);
		final IRenderTask renderTask = BirtEngine.getBirtEngine().createRenderTask(nuxeoReport);

		HTMLRenderOption options = new HTMLRenderOption();
		options.setHtmlTitle(name);
		options.setOutputStream(outputStream);
		options.setOutputFormat(outPutFormat);
		if (HTMLRenderOption.OUTPUT_FORMAT_HTML.equals(outPutFormat)) {
			options.setSupportedImageFormats("GIF");
			options.setEmbeddable(true);
			options.setImageHandler(new HTMLEmbeddedImageHandler());
			renderTask.setRenderOption(options);

		} else if ("xls".equals(outPutFormat)) {
			options.setSupportedImageFormats("GIF");
			options.setEmbeddable(true);
			options.setImageHandler(new HTMLEmbeddedImageHandler());
			renderTask.setRenderOption(options);

		} else if (HTMLRenderOption.OUTPUT_FORMAT_PDF.equals(outPutFormat)) {
			options.setImageHandler(new HTMLServerImageHandler());
			renderTask.setRenderOption(options);

		} else if ("doc".equals(outPutFormat)) {
			RenderOption optionsDoc = new RenderOption();
			optionsDoc.setImageHandler(new HTMLServerImageHandler());
			optionsDoc.setOutputFormat("doc");
			optionsDoc.setOutputStream(outputStream);
			renderTask.setRenderOption(optionsDoc);
		}
		// set the input parameter of the report
		renderTask.setParameterValues(inputValues);
		renderTask.render();
		renderTask.close();
		nuxeoReport.close();
		rptDocFile.delete();
	}

	@Override
	public void generateReportResult(final File fileResult, final File imagesDir, final String reportName,
			final String reportFile, final Map<String, String> inputValues, final String outPutFormat)
			throws ClientException {
		try {
			final File rptDocFile = File.createTempFile("tempFile", ".rptDocument");
			/* Récupération du rptDocument */
			IReportDocument nuxeoReport = generateReportDoc(reportFile, rptDocFile, inputValues);
			File result = generateReport(nuxeoReport, reportName, outPutFormat, fileResult, imagesDir);
			// Si aucun fichier n'a été passé en paramètre, on en a créé un temporaire pour générer le rapport
			// On le supprime donc ici
			if (fileResult == null) {
				result.delete();
			}
			nuxeoReport.close();
			rptDocFile.delete();
		} catch (Exception exc) {
			LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, exc);
			throw new ClientException("error while generating stat report birt", exc);
		}
	}

	@Override
	public Blob generateReportResults(final File fileResult, final File imagesDir, final String reportName,
			final String reportFile, final Map<String, String> inputValues, final String outPutFormat)
			throws ClientException {
		try {
			return generateReportResults(fileResult, imagesDir, reportName, reportFile, inputValues,
					Collections.singletonList(outPutFormat)).get(outPutFormat);
		} catch (Exception exc) {
			LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, exc);
			throw new ClientException("error while generating stat report birt", exc);
		}
	}

	@Override
	public Blob generateReportResults(final String reportName, final String reportFile,
			final Map<String, String> inputValues, final String outPutFormat) throws ClientException {
		try {
			return generateReportResults(null, null, reportName, reportFile, inputValues,
					Collections.singletonList(outPutFormat)).get(outPutFormat);
		} catch (Exception exc) {
			LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, exc);
			throw new ClientException("error while generating stat report birt", exc);
		}
	}

	@Override
	public Map<String, Blob> generateReportResults(final File fileResult, final File imagesDir,
			final String reportName, final String reportFile, final Map<String, String> inputValues,
			final List<String> outPutFormats) throws ClientException {
		Map<String, Blob> reportResults = new HashMap<String, Blob>();
		try {
			final File rptDocFile = File.createTempFile("tempFile", ".rptDocument");
			/* Récupération du rptDocument */
			IReportDocument nuxeoReport = generateReportDoc(reportFile, rptDocFile, inputValues);
			for (String format : outPutFormats) {
				File result = generateReport(nuxeoReport, reportName, format, fileResult, imagesDir);
				reportResults.put(format, transformFileToBlob(result, reportName, format));
				// Si aucun fichier n'a été passé en paramètre, on en a créé un temporaire pour générer le rapport
				// On le supprime donc ici
				if (fileResult == null) {
					result.delete();
				}
			}
			nuxeoReport.close();
			rptDocFile.delete();
			return reportResults;

		} catch (final Exception exc) {
			LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, exc);
			throw new ClientException("error while generating stat report birt", exc);
		}
	}

	@Override
	public Map<String, File> generateReportFileResults(String reportName, String reportFile,
			Map<String, String> inputValues, List<String> outPutFormats) throws ClientException {
		Map<String, File> reportResultsFile = new HashMap<String, File>();
		final String tempDir = System.getProperty("java.io.tmpdir");
		try {
			final File rptDocFile = File.createTempFile("tempFile", ".rptDocument");
			/* Récupération du rptDocument */
			IReportDocument nuxeoReport = generateReportDoc(reportFile, rptDocFile, inputValues);
			for (String format : outPutFormats) {
				// Controle sur la taille du chemin généré : doit être inférieur à 256
				if (tempDir.length() + reportName.length() > 250) {
					reportName = reportName.substring(0, 251 - tempDir.length());
				}
				File result = new File(tempDir, reportName + "." + format);
				result = generateReport(nuxeoReport, reportName, format, result, null);
				reportResultsFile.put(format, result);
				result.deleteOnExit();
			}
			nuxeoReport.close();
			rptDocFile.delete();
			return reportResultsFile;

		} catch (final Exception exc) {
			LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, exc);
			throw new ClientException("error while generating stat report birt", exc);
		}

	}

	@Override
	public Map<String, Blob> generateReportResults(final String reportName, final String reportFile,
			final Map<String, String> inputValues, final List<String> outPutFormats) throws ClientException {
		return generateReportResults(null, null, reportName, reportFile, inputValues, outPutFormats);
	}

	@Override
	public String getBirtReportRootId(CoreSession session) throws ClientException {
		return session.getDocument(new PathRef(BIRT_REPORT_ROOT_PATH)).getId();
	}

	@Override
	public void generatePdf(final OutputStream outputStream, final String fileName,
			final Map<String, String> inputValues, final String name) throws Exception {
		generateReport(outputStream, fileName, inputValues, name, HTMLRenderOption.OUTPUT_FORMAT_PDF);
	}

	@Override
	public void generateXls(final OutputStream outputStream, final String fileName,
			final Map<String, String> inputValues, final String name) throws Exception {
		generateReport(outputStream, fileName, inputValues, name, "xls");
	}

	@Override
	public void generateHtml(final OutputStream outputStream, final String fileName,
			final Map<String, String> inputValues, final String name) throws Exception {
		generateReport(outputStream, fileName, inputValues, name, HTMLRenderOption.OUTPUT_FORMAT_HTML);
	}

	@Override
	public void generateWord(final OutputStream outputStream, final String fileName,
			final Map<String, String> inputValues, final String name) throws Exception {
		generateReport(outputStream, fileName, inputValues, name, "doc");
	}
}
