#! /usr/bin/env python
# -*- Mode: python; py-indent-offset: 4; tab-width: 8; indent-tabs-mode: t; -*-

import os
import string
import subprocess
import time

# flow_n = 252
# threads_n = [1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100]
# iterations_n = 10

flow_n = 1
threads_n = [1]
iterations_n = 10
# iterations_n = 100

# flow_n = 42
# flow_n = 420
# flow_n = 1008

def run_command(cmd):
    """
    - Run an external command, and return a tuple: stdout as the
    first argument, and stderr as the second argument.
    - Returns None if error.
    """
    try:
	pr = subprocess.Popen(cmd, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
	ret_tuple = pr.communicate();
	if pr.returncode:
	    print "%s failed with error code: %s" % (cmd, str(pr.returncode))
	return ret_tuple
    except OSError:
	print "OS Error running %s" % cmd

def run_install_paths(flowdef_filename):
    # Prepare the flows to measure
    cmd = "web/measurement_store_flow.py -f " + flowdef_filename
    os.system(cmd)

def run_measurement(thread_n):
    # Install the Flow Paths
    cmd = ["web/measurement_install_paths.py", str(thread_n)]
    run_command(cmd)

    # Get the measurement data and print it
    cmd = "web/measurement_get_install_paths_time_nsec.py"
    r = run_command(cmd)		# Tuple: [<stdout>, <stderr>]
    res = r[0].split()			# Tuple: [<num>, nsec]
    nsec_str = res[0]
    msec = float(nsec_str) / (1000 * 1000)

    # Get the measurement data and print it
    cmd = "web/measurement_get_per_flow_install_time.py"
    r = run_command(cmd)		# Tuple: [<stdout>, <stderr>]
    res = r[0]
    print res

    # Keep checking until all Flow Paths are installed
    while True:
	# time.sleep(3)
	cmd = ["web/get_flow.py", "all"]
	r = run_command(cmd)
	if string.count(r[0], "FlowPath") != flow_n:
	    continue
	if string.find(r[0], "NOT") == -1:
	    break

    # Remove the installed Flow Paths
    cmd = ["web/delete_flow.py", "all"]
    run_command(cmd)

    # Keep checking until all Flows are removed
    while True:
	# time.sleep(3)
	cmd = ["web/get_flow.py", "all"]
	r = run_command(cmd)
	if r[0] == "":
	    break

    return msec


if __name__ == "__main__":

    # Initial cleanup
    cmd = "web/measurement_clear_all_paths.py"
    run_command(cmd)

    # Install the Flow Paths to measure
    flowdef_filename = "web/flowdef_8node_" + str(flow_n) + ".txt"
    run_install_paths(flowdef_filename)

    # Do the work
    for thread_n in threads_n:
	for n in range(iterations_n):
	    msec = run_measurement(thread_n)
	    # Format: <number of threads> <time in ms>
	    print "%d %f" % (thread_n, msec / flow_n)

    # Cleanup on exit
    cmd = "web/measurement_clear_all_paths.py"
    run_command(cmd)
