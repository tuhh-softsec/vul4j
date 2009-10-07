<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool-helper"
	prefix="assemble"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<assemble:param name="title">
	<title>Exemple de template (titre = bloc variable)</title>
</assemble:param>
</head>
<body style="background-color: aqua">
<div><img src="images/smile.jpg" />&lt;-- Image gérée par le provider</div>
<div style="border: 1px solid red">
	<assemble:param name="param1">Bloc variable 1</assemble:param><br />
	<assemble:param name="param1">Bloc variable 1 (deuxieme instance)</assemble:param>
</div>
<br />

<div style="border: 1px solid red">
	<assemble:param name="param2">Bloc variable 2</assemble:param>
</div>
<div><img src="images/smile.jpg" /></div>
</body>
</html>