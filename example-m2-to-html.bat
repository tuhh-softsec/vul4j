@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-m2-to-html.bat - zkovertuje example-m2.xml na 
@REM                            example-m2-to-html.html pomoci
@REM                            sablony example-m2-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-m2-to-html.html "example_sources/example-m2.xml" xsl/example-m2-to-html.xsl
