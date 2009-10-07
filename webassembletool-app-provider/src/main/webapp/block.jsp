<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool-helper"
	prefix="assemble"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Exemple de page contenant des blocs de contenu</title>
</head>
<body style="background-color: aqua">
<div>Début de la page</div>
<div style="border: 1px solid red"><assemble:block name="block1">
	<div style="background-color: aqua">Bloc de contenu<br />
	<img src="images/smile.jpg" />&lt;--image gérée par le provider</div>
</assemble:block></div>
<div>Fin de la page</div>
</body>
</html>
