#!/bin/sh -f

for o in *.output ; do
	base=`basename $o .output`
	testcase=$o
	for f in ${base}-*.output ; do
		if test -f "$f" ; then
			vendor=`echo $f | sed -e 's/^.*-\([A-Za-z]*\).*$/\1/'`
			if test -s "$testcase" ; then
				echo "Test case: $testcase"
				testcase=""
			fi
			echo -ne "  ${vendor}:   \t"
			if cmp "$o" "$f" ; then
				echo "pass"
			else
				echo "fail"
				echo '---'
				diff -u $o $f
				echo '---'
			fi
		fi
	done
done
