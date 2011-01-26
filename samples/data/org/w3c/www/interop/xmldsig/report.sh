#!/bin/sh

# note, run as ./report.sh > report.out; mv report.out report.html
# do not direct output directly to report.html as the script attempts to read that file and it may be overwritten prematurely.

vendors="SUN IAIK IBM ORCL UPC" 
# warning - HTML had manual edit to link IBM N/A to reason

#vendors="IBM" 
directories="xpointer dname"
#directories="xpointer"

testdoc=../xmlsig-interop-doc/testcases.html
home=`pwd`

if test -f report.html ; then
	sed -n -e '/<div>/,/<\/div>/p' report.html > summary.html
fi

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">'
echo '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en"><head><title>Implementation report XML Signature, 2nd Edition</title>'
echo '<link rel="stylesheet" href="http://www.w3.org/StyleSheets/base.css" />'
echo '<link rel="stylesheet" href="http://www.w3.org/StyleSheets/member.css" />'
echo '<link rel="stylesheet" href="../report.css" />'
echo "</head>"
echo "<body>"
echo '<a href="http://www.w3.org/"><img alt="W3C Logo" src="http://www.w3.org/Icons/w3c_home"/></a>'
echo "<h1>Implementation report for XML Signature, Second Edition</h1>"
echo '<dl>
  <dt>Author</dt>
    <dd>Frederick Hirsch</dd>
    <dd>Thomas Roessler</dd>
  <dt>Last Modified</dt>
    <dd>$Date: 2008/02/15 15:16:22 $ by $Author: fhirsch3 $</dd>
</dl>'


echo '<h2>Overview</h2>'

cat summary.html

echo '<h2>Test results</h2>'

echo "<table><thead><tr><th/>"

for v in $vendors ; do
	echo "<th>${v}</th>"
done

echo "</tr></thead><tbody>"

for d in $directories; do
	cd $d
	testcases=`cat inventory`
	for t in $testcases ; do
		echo '<tr>'
		u=`echo $d-$t|sed 's/\.-//'`
		echo '<td class="tc"><a href="'$testdoc'#'$u'">' $t '</a></td>'
		sunfile="${t}-SUN.xml"
		# test by comparing SUN digest value with others.
		# need to consider cases of both <DigestValue> and <prefix:DigestValue>
		sundigest=`cat $sunfile|sed -n '/<DigestValue>/p' |sed 's/.*\<DigestValue\>//'|sed 's/\<\/DigestValue\>.*//'`
		for v in $vendors ; do
			thefile="${t}-${v}.xml"
			if test -f $thefile ; then

				# ibm test cases were verified during interop by all participants by verifying the signatures. IBM used namespace prefixes 
				# and had different digest and signature values
				if [ ${v} = "IBM" ]
				then
					echo '<td class="pass">'
					echo '<a href="'$thefile'">PASS</a>'
					echo '</td>'
					rm -f ${t}-${v}.fail
				else				

					digest=`cat $thefile |sed -n '/<DigestValue>/p' |sed 's/.*\<DigestValue\>//'|sed 's/\<\/DigestValue\>.*//'`

					if [ "${sundigest}" = "${digest}" ]
					then
						echo '<td class="pass">'
						echo '<a href="''$thefile''">PASS</a>'
						echo '</td>'
						rm -f ${t}-${v}.fail				
					else
						echo '<td class="fail"><a href="#diff-'${t}-${v}'">FAIL</a></td>'
						echo  "Reference digest=${sundigest} Value=${digest}" > ${t}-${v}.fail
					fi
				fi
			else
				echo '<td class="na">N/A</td>'
			fi
		done
		echo '</tr>'
	done
	cd $home
done

echo '</tbody></table>'

echo '<h2>Differences</h2>'

found=0
for d in $directories; do
	cd $d
    for f in *.fail ; do
	  if test -f "$f" ; then
		base=`basename $f .fail`
		echo '<h3 id="diff-'$base'">'$base'</h3>'
	
		echo '<pre>'
		sed -e 's/</\&lt;/g' < $f
		echo '</pre>'
		found=1
	  fi
	done
	cd $home
done

if test "$found" = "0"
then
	echo "<p>None observed.</p>"
fi
	
echo '</body></html>'
			
