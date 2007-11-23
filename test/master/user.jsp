<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="net.webassembletool.Context"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool" prefix="assemble"%>
<%
	Context.getCurrent().setUser("test");
	Context.saveToSession(request);
%>
<assemble:includeTemplate page="user.jsp" />
