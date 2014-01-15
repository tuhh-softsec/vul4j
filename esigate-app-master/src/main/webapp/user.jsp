<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="org.esigate.UserContext"%>
<%
	session.setAttribute(UserContext.getUserSessionKey("default"), "test");
%>
<esi:include src="$(PROVIDER{default})user.jsp" />
