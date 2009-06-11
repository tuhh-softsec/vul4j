<?xml version="1.0" encoding="ISO-8859-1" ?>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Exemple d'inclusion d'un bloc</title>
<base href="http://localhost:8080/webassembletool-app-provider/block.jsp" />
</head>
<body style="background-color: yellow">
Page servie par l'application
<br />
Le tag include permet d'intégrer des blocs de contenu fournis par le provider

	<div style="background-color: aqua">Bloc 2 (was : Bloc) de contenu<br />
	<img src="images/smile.jpg" />&lt;--image gérée par le provider</div>

NB : l'image est servie directement par le serveur distant grace à
l'utilisation du tag base.
</body>
</html>