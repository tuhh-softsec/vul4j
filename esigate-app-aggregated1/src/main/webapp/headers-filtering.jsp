<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%> 
<%@ page import="javax.servlet.http.Cookie"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    String upgrade = request.getHeader("Upgrade");
    if (upgrade == null) {
        upgrade = "missing";
    }

    response.addHeader("Cache-Control", "none");
    response.addHeader("Upgrade-Response", request.getHeader("Upgrade-Response"));
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Header filtering test</title>
</head>
<body>

This page display "Upgrade" header content.<br/>

Header value: <%= upgrade %>
</body>
</html>
