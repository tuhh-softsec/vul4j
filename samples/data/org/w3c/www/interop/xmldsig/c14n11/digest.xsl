<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0"
	       xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	       xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
  <xsl:output method="text"/>
  <xsl:template match="//ds:DigestValue">
    <xsl:value-of select="text()"/><xsl:text>
</xsl:text>    
  </xsl:template>
  <xsl:template match="/">
    <xsl:apply-templates select="//ds:DigestValue"/>
  </xsl:template>
</xsl:transform>
		
