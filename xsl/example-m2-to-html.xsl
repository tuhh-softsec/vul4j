<?xml version="1.0" encoding="UTF-8"?>
<!--

  Bakalarska prace: Zvyraznovani syntaxe v XSLT
  Michal Molhanec 2005

  example-m2-to-html.xsl - konverze example-m2.xml do HTML.

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href='../output_html.xsl'/>

<xsl:output indent="no" method="xml" version="1.0" omit-xml-declaration="yes"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />

<xsl:template match='/'>
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
      <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
      <title>Example</title>
    </head>
    <body>
      <xsl:apply-templates />
    </body>
  </html>
</xsl:template>

<xsl:template match='para'>
  <p><xsl:value-of select='.'/></p>
</xsl:template>

<xsl:template match='code'>
  <pre>
    <xsl:call-template name='sh-m2-to-html'>
      <xsl:with-param name='source' select='.'/>
    </xsl:call-template>
  </pre>
</xsl:template>

</xsl:stylesheet>
