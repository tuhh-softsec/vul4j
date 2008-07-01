<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>Website Assembling Toolkit - <xsl:value-of select="document/@title" /></title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
			</head>
			<body>
				<img src="http://images.sourceforge.net/sfx_logo2.png" />
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
					<xsl:copy-of select="*" />
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="item">
		<xsl:attribute name="href"><xsl:value-of select="@id" />.xml</xsl:attribute>
		<xsl:value-of select="@title" />
	</xsl:template>
	<xsl:template match="link">
		<xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute>
		<xsl:value-of select="@title" />
	</xsl:template>
</xsl:stylesheet>