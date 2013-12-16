<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="org.esigate.servlet.HttpServletMediator"%>
<%@page import="org.apache.http.HttpEntityEnclosingRequest"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="org.esigate.UserContext"%><%@page import="org.esigate.util.HttpRequestHelper"%>
<%@page import="org.esigate.DriverFactory"%>

<%
	HttpEntityEnclosingRequest httpRequest = new HttpServletMediator(request, response, config.getServletContext()).getHttpRequest();
	DriverFactory.getInstance().initHttpRequestParams(httpRequest, null);
	UserContext context = HttpRequestHelper.getUserContext(httpRequest);
	context.setUser("test");
%><esi:include src="$(PROVIDER{default})user.jsp" />
