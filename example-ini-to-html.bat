@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-ini-to-html.bat - zkovertuje example-ini.xml na 
@REM                             example-ini-to-html.html pomoci
@REM                             sablony example-ini-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-ini-to-html.html "example_sources/example-ini.xml" xsl/example-ini-to-html.xsl
