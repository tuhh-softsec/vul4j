<xsl:stylesheet  xmlns="http://www.w3.org/1999/xhtml" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="html:ul">
    <div style="background-color: blue">
    <ol>
        <xsl:for-each select="html:li">
            <li>updated: <xsl:value-of select="text()"/></li>
        </xsl:for-each>
    </ol>
    </div>
</xsl:template>

</xsl:stylesheet>
