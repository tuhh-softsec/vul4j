set VERSION=1.3.0
set REPO_URL=sftp://web.sourceforge.net/home/groups/x/xs/xslthl/htdocs/maven/repository
set REPO_ID=xslthl-sourceforge

call mvn deploy:deploy-file -Durl=%REPO_URL% -DrepositoryId=%REPO_ID% -DpomFile=pom.xml -Dfile=..\dist\xslthl-%VERSION%.jar
