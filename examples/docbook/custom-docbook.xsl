<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xslthl="http://xslthl.sf.net"
	xmlns:d="http://docbook.org/ns/docbook">
	<xsl:import href="docbook-xsl-ns/html/docbook.xsl" />
	
	<!-- add syntax highlighting to "code" elements -->
	<xsl:template match="d:code">
		<xsl:variable name="content">
			<xsl:call-template name="apply-highlighting" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="count(ancestor::d:programlisting) &gt; 0">
				<xsl:copy-of select="$content" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="name(..)" />
				<xsl:call-template name="inline.monoseq">
					<xsl:with-param name="content" select="$content" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- the following is to make sure all elements are highlighted -->
	<xsl:template match="xslthl:keyword">
		<span style="font-weight: bold;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:string">
		<span style="color: blue;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:number">
		<span style="color: blue;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:comment">
		<span style="color: green; font-style: italic;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:doccomment">
		<span style="color: teal; font-style: italic;">
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
	<!-- default XML styles -->
	<xsl:template match="xslthl:tag">
		<span style="color: teal;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:attribute">
		<span style="color: purple;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
	<xsl:template match="xslthl:value">
		<span style="color: blue;">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
</xsl:stylesheet>