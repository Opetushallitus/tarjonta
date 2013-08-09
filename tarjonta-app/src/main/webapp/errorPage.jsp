<%@ page language="java" contentType="text/html; charset=ISO-8859-1" isErrorPage="true"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Virhe</title>
</head>
<body>
<jsp:useBean id="handler" 
                    class="fi.vm.sade.generic.ui.app.ErrorPageHandler">
</jsp:useBean>              

<jsp:setProperty name="handler" property="message" value="unexpectedErrorPage"/>
       
<h2><jsp:getProperty name="handler" property="message"/></h2>
<%
handler.logError(exception);
%>

</body>