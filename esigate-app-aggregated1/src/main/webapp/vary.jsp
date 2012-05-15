<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" session="false"%> 
<%@ page import="javax.servlet.http.Cookie"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<% response.addHeader("Vary", "toto"); %>
<% response.addHeader( "Cache-Control", "public, max-age=3600"); %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Vary header test </title>
</head>
<body>

This page display "test-cookie" content.

<%
    if ( request.getHeader("toto") != null) { 
%>
                test-cookie: <%=request.getHeader("toto") %>
<% 
	} else {
%>
    no cookie
<%
	}
%>

Generation time :  stime<%=System.currentTimeMillis() %>etime
</body>
</html>
