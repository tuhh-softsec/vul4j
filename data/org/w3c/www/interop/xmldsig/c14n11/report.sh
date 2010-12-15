#!/bin/sh

vendors="SUN IAIK IBM ORCL UPC" 
testcases="`cat testcases`"
testdoc=../../xmlsig-interop-doc/testcases.html

if test -f report.html ; then
	sed -n -e '/<div>/,/<\/div>/p' report.html > summary.html
fi

echo "<html><head><title>Interop report C14N 1.1</title>"
echo '<link rel="stylesheet" href="http://www.w3.org/StyleSheets/base.css"/>'
echo '<link rel="stylesheet" href="http://www.w3.org/StyleSheets/member.css"/>'
echo '<link rel="stylesheet" href="../../report.css"/>'
echo "</head>"
echo "<body>"
echo '<a href="http://www.w3.org/"><img alt="W3C Logo" src="http://www.w3.org/Icons/w3c_home"/></a>'
echo "<h1>Implementation report C14N 1.1</h1>"
echo '<dl>
  <dt>Author</dt>
    <dd>Thomas Roessler &lt;tlr@w3.org&gt;</dd>
  <dt>Last Modified</dt>
    <dd>$Date: 2008/01/08 13:09:18 $ by $Author: roessler $</dd>
</dl>'


echo '<h2>Overview</h2>'

cat summary.html

echo '<h2>Test results</h2>'

echo "<table><thead><tr><th/>"

for v in $vendors ; do
	echo "<th>${v}</th>"
done

echo "</tr></thead><tbody>"

for t in $testcases ; do
	openssl sha1 -binary ../../c14n11/$t.output | openssl base64 -e > ${t}-reference.digest
	echo '<tr>'
	u=`echo $t | tr -d /`
	echo '<td class="tc"><a href="'$testdoc'#c14n11'$u'">' $t '</a></td>'
	for v in $vendors ; do
		if test -f ${t}-${v}.xml ; then
			xsltproc digest.xsl ${t}-${v}.xml > ${t}-${v}.digest
			if diff -q ${t}-reference.digest ${t}-${v}.digest > /dev/null ; then
				echo '<td class="pass">'
				echo '<a href="'${t}-${v}.xml'">PASS</a>'
				echo '</td>'
				rm -f ${t}-${v}.fail
			elif test -f ${t}-${v}.xml ; then
				echo '<td class="fail"><a href="#diff-'${t}-${v}'">FAIL</a></td>'
				diff -u ${t}-SUN.xml ${t}-${v}.xml > ${t}-${v}.fail
			fi
		else
			echo '<td class="na">N/A</td>'
		fi
	done
	echo '</tr>'
done

echo '</tbody></table>'

echo '<h2>Differences</h2>'

for f in *.fail ; do
	if test -f "$f" ; then
		base=`basename $f .fail`
		echo '<h3 id="diff-'$base'">'$base'</h3>'
	
		echo '<pre>'
		sed -e 's/</\&lt;/g' < $f
		echo '</pre>'
	else
		echo "<p>None observed.</p>"
	fi
done
	
echo '</body><html>'
			
