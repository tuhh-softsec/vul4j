<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="org.esigate.UserContext"%><%@page import="org.esigate.util.HttpRequestHelper"%><%@page import="org.esigate.servlet.HttpResponseImpl"%><%@page import="org.esigate.api.HttpResponse"%><%@page import="org.esigate.api.HttpRequest"%>
<%@page import="org.esigate.servlet.HttpRequestImpl"%>
<%@page import="org.esigate.DriverFactory"%>
<%@taglib uri="http://www.esigate.org/taglib" prefix="assemble"%>

<%
	HttpRequest httpRequest = HttpRequestImpl.wrap(request, config.getServletContext());
	HttpResponse httpResponse = HttpResponseImpl.wrap(response);
	DriverFactory.getInstance().initHttpRequestParams(httpRequest, httpResponse, null);
	UserContext context = HttpRequestHelper.getUserContext(httpRequest);
	context.setUser("test");
%><assemble:includeTemplate page="user.jsp" />
