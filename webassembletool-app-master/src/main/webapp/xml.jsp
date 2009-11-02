<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool" prefix="assemble"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Exemple d'inclusion d'un bloc</title>
</head>
<body style="background-color: yellow">
<p>Fragment below was retrieved from 'provider' source using following code:<br />
<code>&lt;assemble:include-xml source="xml-page.xml" xpath="//html:div/html:div" /&gt;</code><br />
<assemble:include-xml source="xml-page.xml" xpath="//html:div/html:div" />
<b>NB</b>: You should see bulleted list with aqua background color without any border.</p>

<p>Fragment below was retrieved from 'provider' source using following code:<br />
<code>&lt;assemble:include-xml source="xml-page.xml" template="/WEB-INF/xml-template.xslt" /&gt;</code><br />
<assemble:include-xml source="xml-page.xml" template="/WEB-INF/xml-template.xslt" />
<b>NB</b>: You should see numbered list with blue background color without any border, each list item should have 'updated: ' prefix.</p>
</body>
</html>