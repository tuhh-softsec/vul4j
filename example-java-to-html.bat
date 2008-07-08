@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-java-to-html.bat - zkovertuje example-java.xml na 
@REM                              example-java-to-html.html pomoci
@REM                              sablony example-java-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-java-to-html.html "example_sources/example-java.xml" xsl/example-java-to-html.xsl
