#! /usr/bin/env python
import itertools
import sys

src_port=1
dst_port=1

n=int(sys.argv[1])
a=range(2,n+1)
nflow=int(sys.argv[2])

print "# For %d nodes cluster, %d flows per network pair, total %d flows" % (n, nflow, (n-1)*(n-2)/2 * nflow * 2)

flow_id=1
pair_id=1
for i in itertools.combinations(a,2):
  for f in range(2, nflow+2):
    snet_id=int(i[0])
    dnet_id=int(i[1])
    term_id=f
    print "%d ps_%d_1 00:00:00:00:00:00:%02x:%02x %d 00:00:00:00:00:00:%02x:%02x %d matchSrcMac 00:00:c0:a8:%02x:%02x matchDstMac 00:00:c0:a8:%02x:%02x" % (flow_id,pair_id,snet_id,term_id,src_port,dnet_id,term_id,dst_port,snet_id,term_id,dnet_id,term_id)
    flow_id = flow_id + 1
    print "%d ps_%d_2 00:00:00:00:00:00:%02x:%02x %d 00:00:00:00:00:00:%02x:%02x %d matchSrcMac 00:00:c0:a8:%02x:%02x matchDstMac 00:00:c0:a8:%02x:%02x" % (flow_id,pair_id,dnet_id,term_id,dst_port,snet_id,term_id,src_port,dnet_id,term_id,snet_id,term_id)
    flow_id = flow_id + 1
    pair_id = pair_id + 1
