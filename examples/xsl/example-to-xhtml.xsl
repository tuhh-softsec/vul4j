<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:chl="java:net.sf.xslthl.ConnectorSaxon6"
	xmlns:xhl="xalan://net.sf.xslthl.XalanConnector" xmlns:xslthl="http://xslthl.sf.net" extension-element-prefixes="chl xhl xslthl">
	<xsl:output indent="no" method="html" version="1.0" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />
	<xsl:param name="xslthl.config" />
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
			<head>
				<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
				<title>Example syntax highlighting with xslthl</title>
				<style><![CDATA[
BODY {
	color: black;
	background: white;
	padding: 0.5em;
}				

PRE {
	background: #ffe;
	border: 1px solid #eed;
}

H1 {
	border-left: 5px solid #aaf;
	padding-left: 0.25em;
	margin-left: -0.5em;
	background: #eef;
}
				]]></style>
			</head>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="para">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>
	<xsl:template match="header">
		<h1>
			<xsl:apply-templates />
		</h1>
	</xsl:template>
	<xsl:template match="bold">
		<b>
			<xsl:apply-templates />
		</b>
	</xsl:template>
	<xsl:template match="underline">
		<u>
			<xsl:apply-templates />
		</u>
	</xsl:template>
	<xsl:template match="code">
		<pre>
			<xsl:call-template name="do-highlight">
				<xsl:with-param name="language">
					<xsl:value-of select="@language" />
				</xsl:with-param>
				<xsl:with-param name="source" select="." />
			</xsl:call-template>
		</pre>
	</xsl:template>
	
	<!-- highlighting of the xslthl tags -->
	<xsl:template match="xslthl:keyword">
		<b>
			<xsl:value-of select="." />
		</b>
	</xsl:template>
	<xsl:template match="xslthl:string">
		<span style="color: blue;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:comment">
		<span style="color: green; font-style: italic;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:directive">
		<span style="color: maroon;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:annotation">
		<span style="color: gray; font-style: italic;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:section">
		<span style="background: silver; font-weight: bold;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	
	<!-- This template will perform the actuall highlighting -->
	<xsl:template name="do-highlight">
		<xsl:param name="language" />
		<xsl:param name="source" />
		<xsl:choose>
			<xsl:when test="function-available('chl:highlight')">
				<xsl:variable name="highlighted" select="chl:highlight($language, $source, $xslthl.config)" />
				<xsl:apply-templates select="$highlighted" />
			</xsl:when>
			<xsl:when test="function-available('xhl:highlight')">
				<xsl:variable name="highlighted" select="xhl:highlight($language, $source, $xslthl.config)" />
				<xsl:apply-templates select="$highlighted" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$source" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>