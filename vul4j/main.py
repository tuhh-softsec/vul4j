import argparse
import json
import os
import subprocess
import sys

from loguru import logger

import spotbugs
import utils
from config import REPRODUCTION_DIR
from vul4j_class import Vul4J

VOID = open(os.devnull, 'w')
VUL_ID = "VUL4J"

# logger
logger.remove()
logger.add(lambda msg: print(msg, end=""),
           colorize=True,
           format="<cyan>{time:YYYY-MM-DD HH:mm:ss}</cyan> | " +
                  f"<yellow>{VUL_ID}</yellow>" +
                  " | <level>{level}</level> | <level>{message}</level>",
           level="INFO")

WORK_DIR = "/vul4j/vul4j-testing/reproduce"


def set_logger_id(vul_id: str):
    global VUL_ID
    VUL_ID = vul_id


def run_all(vul_ids, reproduce: bool = False):
    global VUL_ID
    vul4j = Vul4J()

    vulnerabilities = [vul4j.get_vulnerability(vul_id) for vul_id in vul_ids if vul_id is not None]

    if not os.path.exists(REPRODUCTION_DIR):
        os.makedirs(REPRODUCTION_DIR)

    with open(os.path.join(REPRODUCTION_DIR, 'successful_vulnerabilities.txt'), 'a+') as success_vulnerabilities:
        for vul in vulnerabilities:
            VUL_ID = vul["vul_id"]
            try:
                if os.path.exists(WORK_DIR):
                    subprocess.call("rm -rf " + WORK_DIR, shell=True, stdout=VOID, stderr=subprocess.STDOUT)

                logger.info("---------------------------------------------------------")
                logger.info(f"{'Reproducing' if reproduce else 'Verifying'} vulnerability: {vul['vul_id']}...")

                logger.info("--> Checking out the vulnerable revision...")
                # TODO TRY EXCEPT
                vul4j.checkout(vul['vul_id'], WORK_DIR, clone=reproduce)

                # if len(vul['revert_human_patch']) == 0:
                #     logger.error("No patch changes were found!")
                #     exit(1)

                vul4j.apply(WORK_DIR, "vulnerable")

                logger.info("Compiling...")
                try:
                    vul4j.compile(WORK_DIR)
                except subprocess.CalledProcessError:
                    logger.error("Compile failed! Keep going...")
                    # continue

                logger.info(f"Running{' PoV' if not reproduce else ''} tests...")
                test_results_str = vul4j.test(WORK_DIR, "all" if reproduce else "povs")  # TODO printout?
                utils.write_test_results_to_file(vul, test_results_str, 'vulnerable')
                test_results = json.loads(test_results_str)

                failing_tests_of_vulnerable_revision = utils.extract_failed_tests_from_test_results(test_results)
                logger.success(f"Failing tests: {failing_tests_of_vulnerable_revision}")
                if failing_tests_of_vulnerable_revision is None:
                    logger.error("Build failed, no tests were run! This is acceptable here.")
                elif len(failing_tests_of_vulnerable_revision) == 0:
                    logger.error("Vulnerable revision must contain at least 1 failing test!!!")
                    continue

                logger.info("--> Applying human patch to the source code...")
                vul4j.apply(WORK_DIR, "human_patch")
                # TODO error handling

                logger.info("Compiling...")
                try:
                    vul4j.compile(WORK_DIR)
                except subprocess.CalledProcessError:
                    logger.error("Compile failed! Keep going...")
                    # continue

                logger.info(f"Running{' PoV' if not reproduce else ''} tests...")
                test_results_str = vul4j.test(WORK_DIR, "all" if reproduce else "povs")
                utils.write_test_results_to_file(vul, test_results_str, 'patched')
                test_results = json.loads(test_results_str)

                failing_tests_of_patched_revision = utils.extract_failed_tests_from_test_results(test_results)
                if failing_tests_of_patched_revision is None:
                    logger.error("Build failed, no tests were run! Human patch must compile and pass the tests!")
                elif len(failing_tests_of_patched_revision) != 0:
                    logger.warning("Failing tests: %s" % failing_tests_of_patched_revision)
                    logger.error("Patched version must contain no failing test!!!")
                    continue
                else:
                    logger.debug("No failing tests found!")
                    # todo different message for reproduce
                    logger.success(
                        f"--> The vulnerability {vul['vul_id']} has been verified successfully with PoV(s): "
                        f"{failing_tests_of_vulnerable_revision}!")
                    success_vulnerabilities.write(vul['vul_id'] + '\n')
                    success_vulnerabilities.flush()
            except Exception as e:
                logger.critical("Error encountered: ", exc_info=e)


def vul4j_checkout(args):
    vul4j = Vul4J()
    vul4j.checkout(args.id, args.outdir)
    # TODO error, clone


def vul4j_compile(args):
    vul4j = Vul4J()
    try:
        vul4j.compile(args.outdir)
    except subprocess.CalledProcessError:
        logger.error("Compile failed!")
    except FileNotFoundError:
        logger.error("No vulnerability found in the directory!")


def vul4j_apply(args):
    vul4j = Vul4J()
    try:
        vul4j.apply(args.outdir, args.version)
        logger.success(f"Version applied: {args.version}")
    except FileNotFoundError:
        logger.error(f"No such version: {args.version}")
    except FileExistsError:
        logger.error("No vulnerability found in the directory!")
    except Exception:
        logger.error(f"Something went wrong when applying version: {args.version}")
    # TODO better error handling


def vul4j_sast(args):
    vul4j = Vul4J()

    versions = args.versions
    output_dir = args.outdir

    # TODO unify vul getting (now some in the method, some here...)
    vul = utils.read_vulnerability_from_output_dir(output_dir)

    if vul is None:
        logger.error("No vulnerability found in the directory!")
        return

    artifacts = spotbugs.edit_pom(os.path.join(output_dir, "pom.xml"), vul["compliance_level"])

    if versions:
        for version in versions:
            logger.info("---------------------------------------------------------")
            logger.info(f"Checking version: {version}...")
            vul4j.apply(output_dir, version, True)

            logger.info("Compiling...")
            try:
                vul4j.compile(output_dir)
            except subprocess.CalledProcessError:
                logger.error("Compile failed! Keep going...")
                continue

            logger.info("Running SpotBugs...")
            spotbugs.run_spotbugs(output_dir, artifacts, vul, version)
    else:
        logger.info("---------------------------------------------------------")
        logger.info("Compiling...")
        try:
            vul4j.compile(output_dir)
        except subprocess.CalledProcessError:
            logger.error("Compile failed!")
            exit(1)

        logger.info("Running SpotBugs...")
        spotbugs.run_spotbugs(output_dir, artifacts, vul)

    spotbugs.restore_pom(output_dir)


def vul4j_reproduce(args):
    run_all(args.id, args.reproduce)


def vul4j_test(args):
    vul4j = Vul4J()
    vul4j.test(args.outdir, args.batchtype)
    exit(0)


def vul4j_classpath(args):
    vul4j = Vul4J()
    vul4j.classpath(args.outdir)


def vul4j_info(args):
    vul4j = Vul4J()
    vul4j.get_info(args.id)


def main(args=None):
    if args is None:
        args = sys.argv[1:]

    parser = argparse.ArgumentParser(prog="vul4j", description="A Dataset of Java vulnerabilities.")

    sub_parsers = parser.add_subparsers()

    # CHECKOUT TODO url parameter
    checkout_parser = sub_parsers.add_parser('checkout',
                                             help="Checkout a vulnerability into the specified directory.")
    checkout_parser.set_defaults(func=vul4j_checkout)
    checkout_parser.add_argument("-i", "--id", type=str,
                                 help="Vulnerability ID.", required=True)
    checkout_parser.add_argument("-d", "--outdir", type=str,
                                 help="The destination directory.", required=True)

    # COMPILE TODO id
    compile_parser = sub_parsers.add_parser('compile', help="Compile the checked out vulnerability.")
    compile_parser.set_defaults(func=vul4j_compile)
    compile_parser.add_argument("-i", "--id", type=str,
                                help="Vulnerability Id.")
    compile_parser.add_argument("-d", "--outdir", type=str,
                                help="The directory to which the vulnerability was checked out.", required=True)

    # APPLY
    apply_parser = sub_parsers.add_parser('apply', help="Apply the specified file version.")
    apply_parser.set_defaults(func=vul4j_apply)
    apply_parser.add_argument("-d", "--outdir", type=str,
                              help="The directory to which the vulnerability was checked out.", required=True)
    apply_parser.add_argument("-v", "--version", type=str,
                              help="Version to apply", required=True)

    # SAST
    sast_parser = sub_parsers.add_parser('sast', help="Run spotbugs analysis.")
    sast_parser.set_defaults(func=vul4j_sast)
    sast_parser.add_argument("-i", "--id", type=str,
                             help="Vulnerability Id.")
    sast_parser.add_argument("-d", "--outdir", type=str,
                             help="The directory to which the vulnerability was checked out.", required=True)
    sast_parser.add_argument("-v", "--versions", nargs='+',
                             help="Versions to run spotbugs on.", required=False)

    # TEST
    test_parser = sub_parsers.add_parser('test', help="Run testsuite for the checked out vulnerability.")
    test_parser.set_defaults(func=vul4j_test)
    test_parser.add_argument("-i", "--id", type=str,
                             help="Vulnerability Id.")
    test_parser.add_argument("-d", "--outdir", type=str,
                             help="The directory to which the vulnerability was checked out.", required=True)
    test_parser.add_argument("-b", "--batchtype", choices=["povs", "all"], default="all", type=str,
                             help="Two modes: all tests (all) by default, and only povs (povs).", required=False)

    # CLASSPATH
    cp_parser = sub_parsers.add_parser('classpath', help="Print the classpath of the checked out vulnerability.")
    cp_parser.set_defaults(func=vul4j_classpath)
    cp_parser.add_argument("-i", "--id", type=str,
                           help="Vulnerability Id.")
    cp_parser.add_argument("-d", "--outdir", type=str,
                           help="The directory to which the vulnerability was checked out.", required=True)

    # INFO
    info_parser = sub_parsers.add_parser('info', help="Print information about a vulnerability.")
    info_parser.set_defaults(func=vul4j_info)
    info_parser.add_argument("-i", "--id", type=str,
                             help="Vulnerability Id.", required=True)

    # REPRODUCE
    reproduce_parser = sub_parsers.add_parser('reproduce', help="Reproduce of newly added vulnerabilities.")
    reproduce_parser.set_defaults(func=vul4j_reproduce, reproduce=True)
    reproduce_parser.add_argument("-i", "--id", nargs='+', type=str,
                                  help="Vulnerability Id.", required=True)

    # VERIFY
    verify_parser = sub_parsers.add_parser('verify', help="Verify the reproducibility of existing vulnerabilities.")
    verify_parser.set_defaults(func=vul4j_reproduce, reproduce=False)
    verify_parser.add_argument("-i", "--id", nargs='+', type=str,
                               help="Vulnerability Id.", required=True)

    options = parser.parse_args(args)
    if not hasattr(options, 'func'):
        parser.print_help()
        exit(1)

    options.func(options)
    exit(0)


if __name__ == "__main__":
    main()
