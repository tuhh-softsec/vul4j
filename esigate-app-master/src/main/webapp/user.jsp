<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="org.esigate.http.IncomingRequest"%>
<%@page import="org.esigate.servlet.HttpServletMediator"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="org.esigate.UserContext"%>
<%@page import="org.esigate.DriverFactory"%>

<%
	IncomingRequest httpRequest = new HttpServletMediator(request, response, config.getServletContext()).getHttpRequest();
	UserContext context = new UserContext(httpRequest, "default");
	context.setUser("test");
%><esi:include src="$(PROVIDER{default})user.jsp" />
