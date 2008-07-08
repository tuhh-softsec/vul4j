@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-php-to-html.bat - zkovertuje example-php.xml na 
@REM                             example-php-to-html.html pomoci
@REM                             sablony example-php-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-php-to-html.html "example_sources/example-php.xml" xsl/example-php-to-html.xsl
