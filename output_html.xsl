<?xml version="1.0" encoding="UTF-8"?>
<!--

  Bakalarska prace: Zvyraznovani syntaxe v XSLT
  Michal Molhanec 2005

  output_html.xsl - transformace zvyrazneneho textu do HTML

-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:hl="java:net.sf.xslthl.ConnectorSaxon6"
  xmlns:xslthl="http://xslthl.sf.net"
  extension-element-prefixes="hl xslthl">

<xsl:template match='xslthl:keyword'>
  <b><xsl:value-of select='.'/></b>
</xsl:template>

<xsl:template match='xslthl:string'>
  <font color='red'><xsl:value-of select='.'/></font>
</xsl:template>

<xsl:template match='xslthl:comment'>
  <font color='green'><xsl:value-of select='.'/></font>
</xsl:template>

<xsl:template match='xslthl:tag'>
  <font color='blue'><xsl:value-of select='.'/></font>
</xsl:template>

<xsl:template match='xslthl:html'>
  <span style='background:#AFF'><font color='blue'><xsl:value-of select='.'/></font></span>
</xsl:template>

<xsl:template match='xslthl:xslt'>
  <span style='background:#AAA'><font color='blue'><xsl:value-of select='.'/></font></span>
</xsl:template>

<xsl:template match='xslthl:section'>
  <span style='background:yellow'><xsl:value-of select='.'/></span>
</xsl:template>

<xsl:template match='xslthl:attribute'>
  <span style='background:#FAF'><xsl:value-of select='.'/></span>
</xsl:template>

<xsl:template match='xslthl:value'>
  <span style='background:#FFA'><xsl:value-of select='.'/></span>
</xsl:template>

<!--
<xsl:template match="@*|node()">
  <xsl:copy-of select="."/>
</xsl:template>
-->

<xsl:template name='sh-java-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('java', $source)" />
  <xsl:apply-templates select='$pass1' />
</xsl:template>

<xsl:template name='sh-delphi-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('delphi', $source)"/>
  <xsl:apply-templates select='$pass1' />
</xsl:template>

<xsl:template name='sh-xml-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('myxml', $source)"/>
  <xsl:apply-templates select='$pass1' />
</xsl:template>

<xsl:template name='sh-ini-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('ini', $source)"/>
  <xsl:apply-templates select='$pass1' />
</xsl:template>

<xsl:template name='sh-php-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('php', $source)"/>
  <xsl:apply-templates select='$pass1' />
</xsl:template>

<xsl:template name='sh-m2-to-html'>
  <xsl:param name='source'/>
  <xsl:variable name='pass1' select="hl:highlight('m2', $source)" />
  <xsl:apply-templates select='$pass1' />
</xsl:template>

</xsl:stylesheet>
