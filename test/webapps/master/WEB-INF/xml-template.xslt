<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/ul">
    <div style="background-color: blue">
    <ol>
        <xsl:for-each select="li">
            <li>updated: <xsl:value-of select="text()"/></li>
        </xsl:for-each>
    </ol>
    </div>
</xsl:template>

</xsl:stylesheet>
