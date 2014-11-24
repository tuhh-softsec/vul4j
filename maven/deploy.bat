set VERSION=2.1.3
set REPO_URL=sftp://web.sourceforge.net/home/project-web/xslthl/htdocs/maven/repository
set REPO_ID=xslthl-sourceforge

call c:\maven\bin\mvn -X deploy:deploy-file -Durl=%REPO_URL% -DrepositoryId=%REPO_ID% -DpomFile=pom.xml -Dfile=..\dist\xslthl-%VERSION%.jar
