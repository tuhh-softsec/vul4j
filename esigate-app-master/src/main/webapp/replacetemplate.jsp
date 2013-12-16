<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<esi:include src="$(PROVIDER{default})template.jsp">
	<esi:replace expression ="Lorem ipsum">Ceci est un nouveau texte</esi:replace>
	<esi:replace fragment="title">
		<title>Exemple d'utilisation d'un template (titre inséré par
		l'applicatif)</title>
	</esi:replace>
	<esi:replace fragment="param1">
		<div style="background-color: yellow">Contenu inséré par
		l'applicatif<br />
		NB : l'image est servie par la servlet proxy</div>
	</esi:replace>
	<esi:replace fragment="param2">
		<div style="background-color: yellow">Autre bloc de contenu
		inséré par l'applicatif</div>
	</esi:replace>
</esi:include>
