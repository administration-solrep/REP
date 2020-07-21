package fr.dila.ss.api.service;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;


public interface SSBirtService extends Serializable {

    String getBirtReportRootId(final CoreSession session) throws ClientException; 
    
    /**
     * Génère un rapport suivant le format passé en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormat le format dans lequel générer le rapport
     * @return le blob du rapport généré
     * @throws ClientException
     */
    void generateReportResult(final File fileResult, final File imagesDir, final String reportName, final String reportFile, final Map<String, String> inputValues,
            final String outPutFormat) throws ClientException;
    
    /**
     * Génère un blob (rapport) suivant le format passé en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormat le format dans lequel générer le rapport
     * @return le blob du rapport généré
     * @throws ClientException
     */
    Blob generateReportResults(final File fileResult, final File imagesDir, final String reportName, final String reportFile, final Map<String, String> inputValues,
            final String outPutFormat) throws ClientException;
    
    /**
     * Génère un blob (rapport) suivant le format passé en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormat le format dans lequel générer le rapport
     * @return le blob du rapport généré
     * @throws ClientException
     */
    Blob generateReportResults(final String reportName, final String reportFile, final Map<String, String> inputValues,
            final String outPutFormat) throws ClientException;
    
    /**
     * génère une série de blob suivant les formats passés en paramètre
     * @param fileResult le fichier résultat du rapport créé
     * @param imagesDir le repertoire des images du rapport
     * @param reportName le nom du rapport à générer 
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     * @throws ClientException
     */
    Map<String, Blob> generateReportResults(final File fileResult, final File imagesDir, final String reportName, final String reportFile, final Map<String, String> inputValues,
            final List<String> outPutFormats) throws ClientException;
    
    /**
     * génère une série de blob suivant les formats passés en paramètre
     * @param reportName le nom du rapport à générer 
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     * @throws ClientException
     */
    Map<String, Blob> generateReportResults(final String reportName, final String reportFile, final Map<String, String> inputValues,
            final List<String> outPutFormats) throws ClientException;
    
    /**
     * Génère un pdf pour un rptDesign donné
     * @param outputStream l'output dans lequel sera envoyé le rapport
     * @param fileName le nom du rpDesign
     * @param inputValues valeurs à utiliser pour générer le rapport
     * @param name le nom du rapport
     * @throws Exception
     */
    void generatePdf(final OutputStream outputStream, final String fileName, final Map<String, String> inputValues,
            final String name) throws Exception;
    
    /**
     * Génère un fichier excel pour un rptDesign donné
     * @param outputStream l'output dans lequel sera envoyé le rapport
     * @param fileName le nom du rpDesign
     * @param inputValues valeurs à utiliser pour générer le rapport
     * @param name le nom du rapport
     * @throws Exception
     */
    void generateXls(final OutputStream outputStream, final String fileName, final Map<String, String> inputValues,
            final String name) throws Exception;

    /**
     * Génère un html pour un rptDesign donné
     * @param outputStream l'output dans lequel sera envoyé le rapport
     * @param fileName le nom du rpDesign
     * @param inputValues valeurs à utiliser pour générer le rapport
     * @param name le nom du rapport
     * @throws Exception
     */
    void generateHtml(final OutputStream outputStream, final String fileName, final Map<String, String> inputValues,
            final String name) throws Exception;
    
    /**
     * Génère un word pour un rptDesign donné
     * @param outputStream l'output dans lequel sera envoyé le rapport
     * @param fileName le nom du rpDesign
     * @param inputValues valeurs à utiliser pour générer le rapport
     * @param name le nom du rapport
     * @throws Exception
     */
    void generateWord(final OutputStream outputStream, final String fileName, final Map<String, String> inputValues,
            final String name) throws Exception;

    /**
     * génère une série de File suivant les formats passés en paramètre
     * @param reportName le nom du rapport à générer 
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     * @throws ClientException
     */
    Map<String, File> generateReportFileResults(String reportName, String reportFile, Map<String, String> inputValues, List<String> outPutFormats) throws ClientException;
}
