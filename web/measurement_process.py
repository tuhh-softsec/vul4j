#! /usr/bin/env python
# -*- Mode: python; py-indent-offset: 4; tab-width: 8; indent-tabs-mode: t; -*-

import functools
import math
import sys

## {{{ http://code.activestate.com/recipes/511478/ (r1)

def percentile(N, percent, key=lambda x:x):
    """
    Find the percentile of a list of values.

    @parameter N - is a list of values. Note N MUST BE already sorted.
    @parameter percent - a float value from 0.0 to 1.0.
    @parameter key - optional key function to compute value from each element of N.

    @return - the percentile of the values
    """
    if not N:
        return None
    k = (len(N)-1) * percent
    f = math.floor(k)
    c = math.ceil(k)
    if f == c:
        return key(N[int(k)])
    d0 = key(N[int(f)]) * (c-k)
    d1 = key(N[int(c)]) * (k-f)
    return d0+d1

# median is 50th percentile.
# median = functools.partial(percentile, percent=0.5)
## end of http://code.activestate.com/recipes/511478/ }}}

if __name__ == "__main__":

    dict = {}

    #
    # Read the data from the stdin, and store it in a dictionary.
    # The dictionary uses lists as values.
    #
    data = sys.stdin.readlines()
    for line in data:
	words = line.split()
	thread_n = int(words[0])
	msec = float(words[1])
	dict.setdefault(thread_n, []).append(msec)

    #
    # Compute and print the values: median (50-th), 10-th, and 90-th
    # percentile:
    # <key> <median> <10-percentile> <90-percentile>
    #
    for key, val_list in sorted(dict.items()):
	val_10 = percentile(sorted(val_list), 0.1)
	val_50 = percentile(sorted(val_list), 0.5)
	val_90 = percentile(sorted(val_list), 0.9)
	print "%s %s %s %s" % (str(key), str(val_50), str(val_10), str(val_90))
