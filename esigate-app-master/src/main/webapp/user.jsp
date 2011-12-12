<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="org.esigate.UserContext"%>
<%@page import="org.esigate.servlet.HttpRequestImpl"%>
<%@page import="org.esigate.DriverFactory"%>
<%@page import="org.esigate.cookie.SerializableBasicCookieStore"%>
<%@taglib uri="http://www.esigate.org/taglib" prefix="assemble"%>
<%
	UserContext context = DriverFactory.getInstance().getUserContext(HttpRequestImpl.wrap(request));	
	context.setUser("test");
%><assemble:includeTemplate page="user.jsp" />
