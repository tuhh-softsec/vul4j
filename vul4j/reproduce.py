import argparse
import json
import logging
import os
import subprocess

from main import Vul4J
from config import REPRODUCTION_DIR

FNULL = open(os.devnull, 'w')

WORK_DIR = "/tmp/vul4j/reproduction"


def extract_failed_tests_from_test_results(test_results):
    failing_tests = set()
    failures = test_results['tests']['failures']
    for failure in failures:
        failing_tests.add(failure['test_class'] + '#' + failure['test_method'])
    return failing_tests


def write_test_results_to_file(vul, test_results, revision):
    test_output_file = os.path.join(REPRODUCTION_DIR,
                                    '%s_%s_tests_%s.json' % (
                                        vul['project'].replace('-', '_'), vul['vul_id'].replace('-', '_'), revision))
    with (open(test_output_file, 'w', encoding='utf-8')) as f:
        f.write(test_results.decode('utf-8'))


def reproduce(args):

    vul4j = Vul4J()

    vulnerabilities = []
    if args.id is not None:
        for vul_id in args.id:
            vulnerabilities.append(vul4j.get_vulnerability(vul_id))

    success_vulnerabilities = open(os.path.join(REPRODUCTION_DIR, 'successful_vulns.txt'), 'a+')
    for vul in vulnerabilities:
        try:
            if os.path.exists(WORK_DIR):
                subprocess.call("rm -rf " + WORK_DIR, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)

            logging.info("---------------------------------------------------------")
            logging.info("Reproducing vulnerability: %s..." % vul['vul_id'])

            logging.debug("--> Checking out the vulnerable revision...")
            ret = vul4j.checkout_reproduce(vul['vul_id'], WORK_DIR)
            if ret != 0:
                logging.error("Checkout failed!")
                continue

            logging.debug("Compiling...")
            ret = vul4j.compile(WORK_DIR)
            if ret != 0:
                logging.error("Compile failed!")
                continue

            logging.debug("Running tests...")
            test_results_str = subprocess.check_output("python3 vul4j/main.py test -d %s" % WORK_DIR, shell=True)
            write_test_results_to_file(vul, test_results_str, 'vulnerable')
            test_results = json.loads(test_results_str)

            failing_tests_of_vulnerable_revision = extract_failed_tests_from_test_results(test_results)
            logging.debug("Failing tests: %s" % failing_tests_of_vulnerable_revision)
            if len(failing_tests_of_vulnerable_revision) == 0:
                logging.error("Vulnerable revision must contain at least 1 failing test!!!")
                continue

            logging.debug("--> Applying human patch to the source code...")
            if len(vul['human_patch']) == 0:
                logging.error("No patch changes were found!")
                exit(1)

            for change in vul['human_patch']:
                logging.debug("Applied " + change['file_path'])
                with open(os.path.join(WORK_DIR, change['file_path']), 'w', encoding='utf-8') as f:
                    f.write(change['content'])

            logging.debug("Compiling...")
            ret = vul4j.compile(WORK_DIR)
            if ret != 0:
                logging.error("Compile failed!")
                continue

            logging.debug("Running tests...")
            test_results_str = subprocess.check_output("python3 vul4j/main.py test -d %s" % WORK_DIR, shell=True)
            write_test_results_to_file(vul, test_results_str, 'patched')
            test_results = json.loads(test_results_str)

            failing_tests_of_patched_revision = extract_failed_tests_from_test_results(test_results)
            if len(failing_tests_of_patched_revision) != 0:
                logging.debug("Failing tests: %s" % failing_tests_of_patched_revision)
                logging.error("Patched version must contain no failing test!!!")
                continue
            else:
                logging.debug("No failing tests found!")
                logging.info("--> The vulnerability %s has been reproduced successfully ^O^!" % vul['vul_id'])
                success_vulnerabilities.write(vul['vul_id'] + '\n')
                success_vulnerabilities.flush()
        except Exception as e:
            logging.error("Error encountered: ", exc_info=e)
