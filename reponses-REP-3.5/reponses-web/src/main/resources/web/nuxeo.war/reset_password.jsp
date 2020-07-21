<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Nuxeo Enterprise Platform, svn $Revision: 22925 $ -->
<%@page import="fr.dila.st.api.service.STParametreService"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="org.nuxeo.runtime.api.Framework"%>
<%@ page import="java.util.Locale"%>
<%@ page import="fr.dila.st.core.service.STServiceLocator"%>
<%@ page import="fr.dila.st.api.service.STUserService"%>
<%@ page import="org.nuxeo.ecm.core.api.ClientException"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String productName = Framework.getProperty("org.nuxeo.ecm.product.name");
String productVersion = Framework.getProperty("org.nuxeo.ecm.product.version");
String productTag = Framework.getProperty("reponses.product.tag");
String context = request.getContextPath();
Locale locale = request.getLocale();
String language = locale == null ? "en" : locale.getLanguage();
String country = locale == null ? "US" : locale.getCountry();
String pageRenseignement;
try {
  STParametreService paramService = STServiceLocator.getSTParametreService();
  pageRenseignement = paramService.getUrlRenseignement();
} catch (Exception e) {
    pageRenseignement = "#";
}
%>
<html>
<fmt:setLocale value="fr" scope="session" />
<fmt:setBundle basename="messages" var="messages" />

<head>
<title>Réponses</title>
<link rel="icon" type="image/png" href="<%=context%>/icons/favicon.png" />
<style type="text/css">
<!--
body {
	font: normal 11px "Lucida Grande", sans-serif;
	color: #343434;
}

.topBar {
	background: url('<%=context%>/img/header-reponses.png') no-repeat scroll
		center center #bd4044;
	width: 100%;
	height: 110px;
	border: 0;
}

.topBar .title {
	color: #FFFFFF;
	font: normal 30px Calibri, Arial, "Lucida Grande", sans-serif;
}

.topBar .content {
	color: #FFFFFF;
	font-size: 12px;
	vertical-align: top;
}

table.loginForm {
	border-spacing: 3px;
	padding: 3px;
}

.tdLogin {
	background-image: url("<%=context%>/img/an-senat.png");
	background-repeat: no-repeat;
	background-attachment: scroll;
	background-position: center center;
	width: 100%;
}

.login {
	background: #fff
		url("<%=context%>/img/theme_reponses/marianne_fond.gif") bottom right
		no-repeat;
	opacity: 0.95;
	filter: alpha(opacity = 95);
	border: 1px solid black;
	padding: 20px 0px 0px 0px;
	width: 350px;
}

.login_label {
	font: bold 12px "Lucida Grande", sans-serif;
	text-align: right;
	white-space: nowrap;
	color: #005595;
	margin: 0 4px 0 0;
}

.login_input {
	border: 1px inset #454545;
	background: white;
	padding: 3px;
	color: #454545;
	margin: 0 10px 5px 0px;
	font: normal 10px "Lucida Grande", sans-serif;
}

.login_title {
	text-align: center;
	font: bold 13px "Lucida Grande", sans-serif;
}

/* this class is a repeat because defined in nxthemes-setup.xml but
nxthemes css is not used in login.jsp */
.login_button {
	cursor: pointer;
	color: #454545;
	font-size: 10px;
	background: #CECFD1 url(<%=context%>/img/theme_galaxy/buttons.png)
		repeat-x scroll left top;
	border: 1px solid #BFC5CB;
	padding: 2px 5px 2px 5px;
	margin: 5px 10px 10px 0;
}

.login_button:hover {
	border: 1px solid #92999E;
	color: #000000;
}

.news_container {
	position: absolute;
	top: 36px;
	right: 360px;
	text-align: left;
}

.block_container {
	border: 1px solid #aaaaaa;
	overflow: auto;
	height: 100%;
	width: 250px;
	opacity: 0.90;
	filter: alpha(opacity = 90);
	padding: 0;
	overflow: auto;
	overflow-x: hidden;
}

.errorMessage {
	color: #000;
	font: bold 10px "Lucida Grande", sans-serif;
	border: 1px solid #666;
	background: url(<%=context%>/icons/warning.gif) 2px 3px no-repeat
		#FFCC33;
	margin-bottom: 12px;
	display: block;
	padding: 5px 5px 5px 23px;
	text-align: center;
}

.footer {
	position: absolute;
	bottom: 0px;
	left: 0px;
	width: 100%;
	text-align: center;
	font-size: 12px;
}

.footer a {
	background: #fff;
}

.version {
	color: #fff;
	background: #fff;
}
-->
</style>

</head>

<body style="margin: 0; text-align: center;">

	<%
    boolean hasError = false;
    String errorMsg = null;
	String form_submitted_marker = request.getParameter("form_submitted_marker");

	if ("true".equals(form_submitted_marker)) {
		
		STUserService userService = STServiceLocator.getSTUserService();
		
		String username=request.getParameter("user_name");
		String email=request.getParameter("user_email");
		
		try {
			userService.askResetPassword(username, email);
			
		} catch (Exception e) {
		    hasError = true;
		    errorMsg = e.getMessage();
		}
		
	}

%>
	<script type="text/javascript">


</script>
	<table cellspacing="0" cellpadding="0" border="0" width="100%"
		height="100%">
		<tbody>
			<tr class="topBar">
				<td colspan="2">
					<table width="300px">
						<tr>
							<td rowspan="2" width="75px"><img
								src="<%=context%>/img/logopm.png" /></td>
							<td class="title">RÉPONSES</td>
						</tr>
						<tr>
							<td class="content"><i>Bienvenue dans l'outil
									interministériel de traitement des questions écrites</i></td>
						</tr>


					</table>

				</td>
			</tr>
			<tr>
				<td class="tdLogin" align="center">
					<%  if (!"true".equals(form_submitted_marker) || hasError) { %>
					<form name="reset_password_form" method="post"
						action="reset_password.jsp">
						<!-- To prevent caching -->
						<%
              response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
              response.setHeader("Pragma", "no-cache"); // HTTP 1.0
              response.setDateHeader("Expires", -1); // Prevents caching at the proxy server
          %>
						<!-- ;jsessionid=<%=request.getSession().getId()%> -->
						<!-- ImageReady Slices (login_cutted.psd) -->
						<div class="login">
							<table>
								<tr>
									<td colspan="2" class="login_title"><fmt:message
											bundle="${messages}" key="label.login.askPassword" /></td>
								</tr>
								<tr>
									<td class="login_label"><label for="username"> <fmt:message
												bundle="${messages}" key="label.login.username" />
									</label></td>
									<td><input class="login_input" type="text"
										name="user_name" id="username" size="22"></td>
								</tr>
								<tr>
									<td class="login_label"><label for="email"> <fmt:message
												bundle="${messages}" key="label.login.email" />
									</label></td>
									<td><input class="login_input" type="text"
										name="user_email" id="email" size="22"></td>
								</tr>
								<tr>
									<td colspan="2">
										<% // label.login.logIn %> <input type="hidden" name="language"
										id="language" value="fr"> <input id="requestedUrl"
										type="hidden" value="" name="requestedUrl"> <input
										type="hidden" name="form_submitted_marker"
										id="form_submitted_marker" value="true"> <input
										class="login_button" type="submit" name="Submit"
										id="validateBtn"
										value="<fmt:message bundle="${messages}" key="label.login.validate" />">
										<input class="login_button" type="submit" name="Cancel"
										onclick="document.reset_password_form.action='login.jsp'"
										value="<fmt:message bundle="${messages}" key="label.login.return.logIn" />">

									</td>
								</tr>
								<tr>
									<td></td>
									<td>
										<% 
                  if (hasError) {
                      out.println("<div class='errorMessage'>"+errorMsg+"</div>");
                  }
                  %>
									</td>
								</tr>
							</table>
						</div>
					</form> <% } else { %>
					<form method="post" action="login.jsp">
						<div class="login">
							<table>
								<tr>
									<td><fmt:message bundle="${messages}"
											key="label.login.password.sent" /> <input
										class="login_button" type="submit" name="Submit"
										value="<fmt:message bundle="${messages}" key="label.login.return.logIn" />">
									</td>
								</tr>
							</table>
						</div>
					</form> <% } %>
				</td>
				<td valign="top" style="background-color: transparent;"><iframe
						id="statsModule" class="block_container" src="stats.jsp"></iframe>
				</td>
			</tr>
		</tbody>
	</table>
	<div class="footer">
		<a href="<%=pageRenseignement%>" target="_blank">Pour tout
			renseignement sur les conditions d'accès à l'outil</a> <br /> <span
			class="version"> <%=productTag%>
		</span>
	</div>
</body>
<!--   Current User = <%=request.getRemoteUser()%> -->
</html>

