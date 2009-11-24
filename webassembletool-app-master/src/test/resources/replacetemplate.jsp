<?xml version="1.0" encoding="ISO-8859-1" ?>




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		
		<title>Exemple d'utilisation d'un template (titre inséré par
		l'applicatif)</title>
	
	</head>
	<body style="background-color: aqua">
		<div>
			<img src="images/smile.jpg" />&lt;-- Image gérée par le provider
		</div>
		<div style="border: 1px solid red">
			
		<div style="background-color: yellow">Contenu inséré par
		l'applicatif<br />
		NB : l'image est servie par la servlet proxy</div>
	<br />
		</div>
		<br />
		<div style="border: 1px solid green">
			Texte a remplacer par le tag replace (original : Lorem&nbsp;ipsum) :<br />
			Ceci est un nouveau texte
		</div>	
		<div style="border: 1px solid red">
			
		<div style="background-color: yellow">Autre bloc de contenu
		inséré par l'applicatif</div>
	
		</div>
		<div>
			<img src="images/smile.jpg" />
		</div>
		<div style="border: 1px solid green">
			Autre texte a remplacer par le tag replace (original : Lorem&nbsp;ipsum) :<br />
			Ceci est un nouveau texte
		</div>	
	</body>
</html>
