<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="net.webassembletool.UserContext"%>
<%@page import="net.webassembletool.DriverFactory"%>
<%@page import="net.webassembletool.cookie.SerializableBasicCookieStore"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool" prefix="assemble"%>
<%
	UserContext context = new UserContext();	
	context.setUser("test");
	context.setCookieStore( new SerializableBasicCookieStore() ); 
	context.init();
	DriverFactory.getInstance().setContext(context, request);
%><assemble:includeTemplate page="user.jsp" />
