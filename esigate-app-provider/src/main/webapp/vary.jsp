<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%> 
<%@ page import="javax.servlet.http.Cookie"%> 
<%@taglib uri="http://www.esigate.org/taglib-helper"
	prefix="assemble"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<% response.addHeader("Vary", "Cookie"); %>
<% response.addHeader( "Cache-Control", "public, max-age=60"); %>
<% response.setDateHeader( "Date", System.currentTimeMillis()); %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Vary header test </title>
</head>
<body>

This page display first cookie content.
<assemble:block name="block1">
<% 	
	boolean found = false;
	if ( request.getCookies() != null  ) { 
		Cookie[] cookies = request.getCookies();
		for( int i = 0; i < cookies.length; i ++) {
			if( "test-cookie" .equals(cookies[i].getName())) {
				%><%=cookies[i].getValue() %><% 
				found = true; 
			}
		}
	}
	if ( !found ){ %><%="no cookie" %><% } 
%>
Generation time :  stime<%=System.currentTimeMillis() %>etime
</assemble:block>
</body>
</html>
