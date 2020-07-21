<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Nuxeo Enterprise Platform, svn $Revision: 22925 $ -->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="org.nuxeo.runtime.api.Framework"%>
<%@ page import="org.nuxeo.common.utils.FileUtils"%>
<%@ page import="fr.dila.st.api.service.ConfigService"%>
<%@ page import="fr.dila.reponses.api.constant.ReponsesConfigConstant"%>
<%@ page import="fr.dila.st.core.service.STServiceLocator"%>
<%@ page import="java.io.File"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%

ConfigService configService = STServiceLocator.getConfigService();
String generatedReportPath = configService.getValue(ReponsesConfigConstant.REPONSES_GENERATED_REPORT_DIRECTORY);

Log log = LogFactory.getLog("stats.jsp");

File file = new File(generatedReportPath+"/stats.html");

String generatedHtml = "Statistiques non disponibles";

try {
    generatedHtml = FileUtils.readFile(file);
} catch(Exception e){
    log.error(e);
}

%>
<%=generatedHtml%>
