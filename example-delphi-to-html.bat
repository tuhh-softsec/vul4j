@REM   Bakalarska prace: Zvyraznovani syntaxe v XSLT
@REM   Michal Molhanec 2005

@REM   example-delphi-to-html.bat - zkovertuje example-delphi.xml na 
@REM                                example-delphi-to-html.html pomoci
@REM                                sablony example-delphi-to-html.xsl

java -cp ..\saxon\saxon.jar;classes com.icl.saxon.StyleSheet -o example-delphi-to-html.html "example_sources/example-delphi.xml" xsl/example-delphi-to-html.xsl
