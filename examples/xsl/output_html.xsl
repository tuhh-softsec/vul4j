<?xml version="1.0" encoding="UTF-8"?>
<!--
	
	Bakalarska prace: Zvyraznovani syntaxe v XSLT
	Michal Molhanec 2005
	
	output_html.xsl - transformace zvyrazneneho textu do HTML
	
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:chl="java:net.sf.xslthl.ConnectorSaxon6" xmlns:xhl="xalan://net.sf.xslthl.XalanConnector"
	xmlns:xslthl="http://xslthl.sf.net" extension-element-prefixes="chl xhl xslthl">

	<xsl:template match='h1'>
		<h1>
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="." />
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates />
		</h1>
	</xsl:template>

	<xsl:template match='u'>
		<u>
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="." />
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates />
		</u>
	</xsl:template>

	<xsl:template match='xslthl:keyword'>
		<b>
			<xsl:value-of select='.' />
		</b>
	</xsl:template>

	<xsl:template match='xslthl:string'>
		<font color='red'>
			<xsl:value-of select='.' />
		</font>
	</xsl:template>

	<xsl:template match='xslthl:comment'>
		<font color='green'>
			<xsl:value-of select='.' />
		</font>
	</xsl:template>

	<xsl:template match='xslthl:tag'>
		<font color='blue'>
			<xsl:value-of select='.' />
		</font>
	</xsl:template>

	<xsl:template match='xslthl:html'>
		<span style='background:#AFF'>
			<font color='blue'>
				<xsl:value-of select='.' />
			</font>
		</span>
	</xsl:template>

	<xsl:template match='xslthl:xslt'>
		<span style='background:#AAA'>
			<font color='blue'>
				<xsl:value-of select='.' />
			</font>
		</span>
	</xsl:template>

	<xsl:template match='xslthl:section'>
		<span style='background:yellow'>
			<xsl:value-of select='.' />
		</span>
	</xsl:template>

	<xsl:template match='xslthl:attribute'>
		<span style='background:#FAF'>
			<xsl:value-of select='.' />
		</span>
	</xsl:template>

	<xsl:template match='xslthl:value'>
		<span style='background:#FFA'>
			<xsl:value-of select='.' />
		</span>
	</xsl:template>

	<!--
		<xsl:template match="@*|node()">
		<xsl:copy-of select="."/>
		</xsl:template>
	-->

	<xsl:template name='do-highlight'>
		<xsl:param name='language' />
		<xsl:param name='source' />
		<xsl:choose>
			<xsl:when test="function-available('chl:highlight')">
				<xsl:variable name="highlighted" select="chl:highlight($language, $source)" />
				<xsl:apply-templates select="$highlighted" />
			</xsl:when>
			<xsl:when test="function-available('xhl:highlight')">
				<xsl:variable name="highlighted" select="xhl:highlight($language, $source)" />
				<xsl:apply-templates select="$highlighted" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$source" />
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
