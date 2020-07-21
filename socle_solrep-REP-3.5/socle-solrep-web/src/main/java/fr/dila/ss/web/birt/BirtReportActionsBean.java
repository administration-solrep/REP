package fr.dila.ss.web.birt;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.reporting.engine.BirtEngine;
import org.nuxeo.ecm.platform.reporting.report.ReportHelper;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ss.core.service.SSServiceLocator;

@Name("birtReportingActions")
@Scope(ScopeType.CONVERSATION)
public class BirtReportActionsBean implements Serializable {

	/**
     * 
     */
	private static final long		serialVersionUID	= 6560255663686133734L;

	@In(create = true, required = false)
	protected transient CoreSession	documentManager;

	/**
	 * Default constructor
	 */
	public BirtReportActionsBean() {
		// do nothing
	}

	public void generatePdf(String fileName, Map<String, String> inputValues, String name) throws Exception {
		name = name.indexOf(' ') != -1 ? name.substring(0, name.indexOf(' ')) : name;
		HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();

		// prepare pdf response
		response.setHeader("Content-Type", "application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + name + ".pdf");

		OutputStream outputStream = response.getOutputStream();

		SSServiceLocator.getSSBirtService().generatePdf(outputStream, fileName, inputValues, name);

		FacesContext.getCurrentInstance().responseComplete();
	}

	public void generateXls(String fileName, Map<String, String> inputValues, String name) throws Exception {
		name = name.indexOf(' ') != -1 ? name.substring(0, name.indexOf(' ')) : name;
		HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();

		// prepare xls response
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "inline; filename=" + name + ".xls");

		OutputStream outputStream = response.getOutputStream();

		SSServiceLocator.getSSBirtService().generateXls(outputStream, fileName, inputValues, name);

		FacesContext.getCurrentInstance().responseComplete();
	}

	public void generatehtml(String fileName, Map<String, String> inputValues, String name) throws Exception {

		HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();
		response.setContentType("text/html;charset=utf-8");

		OutputStream outputStream = response.getOutputStream();

		SSServiceLocator.getSSBirtService().generateHtml(outputStream, fileName, inputValues, name);

		FacesContext.getCurrentInstance().responseComplete();
	}

	public void generateWord(String fileName, Map<String, String> inputValues, String name) throws Exception {
		HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		response.reset();

		OutputStream outputStream = response.getOutputStream();

		// read the report fichie-dosier file
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		if (is == null) {
			is = Framework.getResourceLoader().getResourceAsStream(fileName);
		}
		IReportRunnable nuxeoReport = ReportHelper.getNuxeoReport(is);

		IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

		// prepare word response
		response.addHeader("Content-Type", "application/vnd.ms-word");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + name + ".doc\"");
		// OutputStream out = new FileOutputStream(result);
		RenderOption options = new RenderOption();
		options.setImageHandler(new HTMLServerImageHandler());
		options.setOutputFormat("doc");
		options.setOutputStream(outputStream);

		// options.setHtmlTitle(name);

		// set the input parameter of the report
		task.setParameterValues(inputValues);

		task.setRenderOption(options);

		task.run();
		task.close();

		FacesContext.getCurrentInstance().responseComplete();
	}

	private static HttpServletResponse getHttpServletResponse() {
		ServletResponse response = null;
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			response = (ServletResponse) facesContext.getExternalContext().getResponse();
		}

		if (response != null && response instanceof HttpServletResponse) {
			return (HttpServletResponse) response;
		}
		return null;
	}

	protected String buildTmpPath() {
		String dirPath = new Path(System.getProperty("java.io.tmpdir")).toString();
		File baseDir = new File(dirPath);
		if (baseDir.exists()) {
			return dirPath;
		}
		baseDir.mkdir();
		File imagesDir = new File(dirPath + "/images");
		imagesDir.mkdir();

		return dirPath;
	}
}
