<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pe="xalan://org.xmlsecurity.temp.TestProperties"
	extension-element-prefixes="pe"
	version="1.0">

	<xsl:output indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<pe:properties>
		   <xsl:apply-templates />
		</pe:properties>
	</xsl:template>
</xsl:stylesheet>
