<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>Website Assembling Toolkit - <xsl:value-of select="document/@title" /></title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
				<meta name="keywords" content="Website Assembling Toolkit,WAT,webassembletool,portal,jsr 168,web clipping,web scraping,ESI,JESI,Edge Side Include,SSI,Tiles,frame,iframe,Java,integration,agregation,mashup"/>
				<meta name="description" content="Website Assembling Toolkit, the tool for developpers to integrate web applications together"/>
				<meta name="author" content="François-Xavier Bonnet"/>
				<meta name="verify-v1" content="1ryEsoSGASwBoIJBnGDudtyAxYm+A1fEae6TJLtTzEE=" />			</head>
			<body>
				<a href="http://sourceforge.net"><img src="http://sflogo.sourceforge.net/sflogo.php?group_id=209844&amp;type=2" width="125" height="37" border="0" alt="SourceForge.net Logo" /></a>
				<div class="title">Website Assembling Toolkit</div>
				<hr />
				<ul id="menu">
					<xsl:for-each select="document('menu.xml')/menu/*">
						<li>
							<a>
								<xsl:apply-templates select="." />
							</a>
						</li>
					</xsl:for-each>
				</ul>
				<div id="main">
					<xsl:copy-of select="document/*" />
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="item">
		<xsl:attribute name="href"><xsl:value-of select="@id" />.html</xsl:attribute>
		<xsl:value-of select="@title" />
	</xsl:template>
	<xsl:template match="link">
		<xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute>
		<xsl:value-of select="@title" />
	</xsl:template>
</xsl:stylesheet>