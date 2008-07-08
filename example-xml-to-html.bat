@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-xml-to-html.bat - zkovertuje example-xml.xml na 
@REM                             example-xml-to-html.html pomoci
@REM                             sablony example-xml-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-xml-to-html.html "example_sources/example-xml.xml" xsl/example-xml-to-html.xsl
