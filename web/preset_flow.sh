#! /bin/bash
Cluster=4
for n in 8 24; do 
   let nr_flows=\($Cluster-1\)*\($Cluster-2\)*$n
   ./preset_flow.py $Cluster $n > flowdef_${Cluster}node_$nr_flows.txt
done
Cluster=8
for n in 1 3 6 10 24; do 
   let nr_flows=\($Cluster-1\)*\($Cluster-2\)*$n
   ./preset_flow.py $Cluster $n > flowdef_${Cluster}node_$nr_flows.txt
done
