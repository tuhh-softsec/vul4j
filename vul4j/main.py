import argparse
import subprocess
import sys

from loguru import logger

import vul4j.spotbugs as spotbugs
import vul4j.utils as utils
import vul4j.vul4j_class as vul4j


# logger
logger.remove()
logger.add(lambda msg: print(msg, end=""),
           colorize=True,
           format="<cyan>{time:YYYY-MM-DD HH:mm:ss}</cyan> | " +
                  "<level>{level}</level> | <level>{message}</level>",
           level="INFO")

WORK_DIR = "/vul4j/vul4j-testing/reproduce"


@utils.log_frame("STATUS")
def vul4j_status(args):
    utils.check_status()


@utils.log_frame("CHECKOUT")
def vul4j_checkout(args):
    vul_id = args.id
    output_dir = args.outdir

    try:
        vul4j.checkout(vul_id, output_dir)
    except (vul4j.VulnerabilityNotFoundError, AssertionError) as err:
        logger.error(err)


@utils.log_frame("COMPILE")
def vul4j_compile(args):
    output_dir = args.outdir

    try:
        vul4j.build(output_dir)
    except subprocess.CalledProcessError:
        logger.error("Compile failed!")
    except (vul4j.VulnerabilityNotFoundError, AssertionError) as err:
        logger.error(err)


@utils.log_frame("APPLY")
def vul4j_apply(args):
    output_dir = args.outdir
    version = args.version

    try:
        vul4j.apply(output_dir, version)
    except FileNotFoundError:
        logger.error(f"No such version: {version}")
    except vul4j.VulnerabilityNotFoundError as err:
        logger.error(err)


@utils.log_frame("SAST")
def vul4j_sast(args):
    versions = args.versions
    output_dir = args.outdir

    versions = versions if versions else [None]

    for version in versions:
        if version:
            if version != versions[0]:
                logger.info(utils.SEPARATOR)
            logger.info(f"Checking version: {version}...")
            vul4j.apply(output_dir, version, True)

        try:
            spotbugs.run_spotbugs(output_dir, version, bool(version))
        except subprocess.CalledProcessError:
            # compile, method getter or spotbugs fails
            logger.error("Task failed!")
            continue
        except StopIteration:
            # the correct jar was not found
            logger.error("No runnable artifact found")
            continue
        except (vul4j.VulnerabilityNotFoundError, AssertionError) as err:
            # any file fails to be created
            logger.error(err)
            continue


@utils.log_frame("REPRODUCE")
def vul4j_reproduce(args):
    vul4j.reproduce(args.id, args.reproduce)


@utils.log_frame("TEST")
def vul4j_test(args):
    output_dir = args.outdir
    batch_type = args.batchtype

    try:
        vul4j.test(output_dir, batch_type)
    except subprocess.CalledProcessError:
        logger.error("Testing failed!")
    except (vul4j.VulnerabilityNotFoundError, AssertionError) as err:
        logger.error(err)


@utils.log_frame("CLASSPATH")
def vul4j_classpath(args):
    vul4j.classpath(args.outdir)


@utils.log_frame("INFO")
def vul4j_info(args):
    vul_id = args.id

    try:
        vul4j.get_info(vul_id)
    except vul4j.VulnerabilityNotFoundError as err:
        logger.error(err)


def get_spotbugs(args):
    utils.get_spotbugs()


def main(args=None):
    if args is None:
        args = sys.argv[1:]

    parser = argparse.ArgumentParser(prog="vul4j", description="A Dataset of Java vulnerabilities.")

    sub_parsers = parser.add_subparsers()

    # STATUS
    status_parser = sub_parsers.add_parser("status",
                                           help="Lists vul4j requirements and availability.")
    status_parser.set_defaults(func=vul4j_status)

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
                                help="Vulnerability ID.")
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
                             help="Vulnerability ID.")
    sast_parser.add_argument("-d", "--outdir", type=str,
                             help="The directory to which the vulnerability was checked out.", required=True)
    sast_parser.add_argument("-v", "--versions", nargs='+',
                             help="Versions to run spotbugs on.", required=False)

    # TEST
    test_parser = sub_parsers.add_parser('test', help="Run testsuite for the checked out vulnerability.")
    test_parser.set_defaults(func=vul4j_test)
    test_parser.add_argument("-i", "--id", type=str,
                             help="Vulnerability ID.")
    test_parser.add_argument("-d", "--outdir", type=str,
                             help="The directory to which the vulnerability was checked out.", required=True)
    test_parser.add_argument("-b", "--batchtype", choices=["povs", "all"], default="all", type=str,
                             help="Two modes: all tests (all) by default, and only povs (povs).", required=False)

    # CLASSPATH
    cp_parser = sub_parsers.add_parser('classpath', help="Print the classpath of the checked out vulnerability.")
    cp_parser.set_defaults(func=vul4j_classpath)
    cp_parser.add_argument("-i", "--id", type=str,
                           help="Vulnerability ID.")
    cp_parser.add_argument("-d", "--outdir", type=str,
                           help="The directory to which the vulnerability was checked out.", required=True)

    # INFO
    info_parser = sub_parsers.add_parser('info', help="Print information about a vulnerability.")
    info_parser.set_defaults(func=vul4j_info)
    info_parser.add_argument("-i", "--id", type=str,
                             help="Vulnerability ID.", required=True)

    # REPRODUCE
    reproduce_parser = sub_parsers.add_parser('reproduce', help="Reproduce of newly added vulnerabilities.")
    reproduce_parser.set_defaults(func=vul4j_reproduce, reproduce=True)
    reproduce_parser.add_argument("-i", "--id", nargs='+', type=str,
                                  help="Vulnerability ID.", required=True)

    # VERIFY
    verify_parser = sub_parsers.add_parser('verify', help="Verify the reproducibility of existing vulnerabilities.")
    verify_parser.set_defaults(func=vul4j_reproduce, reproduce=False)
    verify_parser.add_argument("-i", "--id", nargs='+', type=str,
                               help="Vulnerability ID.", required=True)

    # GET SPOTBUGS
    spotbugs_parser = sub_parsers.add_parser("get-spotbugs",
                                             help="Downloads Spotbugs into the user directory.")
    spotbugs_parser.set_defaults(func=get_spotbugs)

    options = parser.parse_args(args)
    if not hasattr(options, 'func'):
        parser.print_help()
        exit(1)

    options.func(options)
    exit(0)


if __name__ == "__main__":
    main()
