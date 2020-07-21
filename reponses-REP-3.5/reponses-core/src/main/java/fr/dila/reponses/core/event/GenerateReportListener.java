package fr.dila.reponses.core.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.platform.reporting.engine.BirtEngine;
import org.nuxeo.ecm.platform.reporting.report.ReportHelper;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Listener qui génère un rapport birt pour afficher à la page d'accueil
 * 
 * @author mchahine
 */
public class GenerateReportListener implements EventListener {

    private static final Log LOG = LogFactory.getLog(GenerateReportListener.class);

    public GenerateReportListener() {
        // do nothing
    }

    @Override
    public void handleEvent(Event event) throws ClientException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Batch de génération du rapport birt à afficher sur la page d'accueil");
        }

        ConfigService configService = STServiceLocator.getConfigService();
        String generatedReportPath = configService.getValue(ReponsesConfigConstant.REPONSES_GENERATED_REPORT_DIRECTORY);

        // read the report fichie-dosier file
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ReponsesConstant.BIRT_REPORT_TABLEAU_BORD);
        if (is == null) {
            is = Framework.getResourceLoader().getResourceAsStream(ReponsesConstant.BIRT_REPORT_TABLEAU_BORD);
        }

        IReportRunnable nuxeoReport;
        try {
            nuxeoReport = ReportHelper.getNuxeoReport(is);
            IRunAndRenderTask task = BirtEngine.getBirtEngine().createRunAndRenderTask(nuxeoReport);

            File generatedReportDir = new File(generatedReportPath);
            if (!generatedReportDir.exists()) {
                generatedReportDir.mkdirs();
            }

            File imagesDir = new File(generatedReportPath + "/images");
            if (imagesDir.exists()) {
                FileUtils.deleteTree(imagesDir);
            }
            imagesDir.mkdir();

            File result = new File(generatedReportPath + "/stats.html");
            OutputStream out = new FileOutputStream(result);

            HTMLRenderOption options = new HTMLRenderOption();
            options.setImageHandler(new HTMLServerImageHandler());
            options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
            options.setOutputStream(out);
            options.setBaseImageURL("images");
            options.setImageDirectory(imagesDir.getAbsolutePath());

            // set the input parameter of the report
            HashMap<String, Serializable> inputValues = new HashMap<String, Serializable>();

            inputValues.put("dateDebutStat", Date.valueOf("2000-01-01"));

            task.setParameterValues(inputValues);

            task.setRenderOption(options);

            task.run();
            task.close();
            out.close();
        } catch (Exception e) {
            throw new ClientException("error while generating stat report birt", e);
        }
    }
}
