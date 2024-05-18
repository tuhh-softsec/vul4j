import glob
import json
import os
import subprocess
import sys
import xml.etree.ElementTree as ElementTree

from loguru import logger

import vul4j.utils as utils
from vul4j.config import VUL4J_WORKDIR, SPOTBUGS_PATH, METHOD_GETTER_PATH, LOG_TO_FILE
import vul4j.vul4j_class as vul4j

original_stdout = sys.stdout


def run_spotbugs(output_dir: str, version=None, force_compile=False) -> None:
    """
    Runs Spotbugs check on the project found in the provided directory.
    The project must contain a 'vulnerability_info.json' file.
    Creates a separate spotbugs directory in the project's vul4j work directory.

    If a version is provided (and is not None) the project will be compiled.
    One can manually force recompilation by setting force_compile to True.

    The project's target folder is searched for artifacts.
    The jar that ends in 'SNAPSHOT.jar' will be used for the Spotbugs analysis.

    The method getter extracts the modified method names and their classes into the modifications.json file.
    Then Spotbugs analysis is run.

    The spotbugs_report.xml file is checked for warnings in the methods extracted by the method getter.
    The results are saved in the warnings.json file or warnings_version.json if a version was provided.

    :param output_dir:  path to the projects directory
    :param version: version name, used for naming output files
    :param force_compile:   recompile project
    """

    vul = vul4j.read_vul_from_file(output_dir)

    # create spotbugs directory
    reports_dir = os.path.join(output_dir, VUL4J_WORKDIR, "spotbugs")
    os.makedirs(reports_dir, exist_ok=True)
    assert os.path.exists(reports_dir), "Failed to create spotbugs directory!"
    logger.debug("Spotbugs directory created!")
    # exception can be thrown

    # get module path where compiled jars are located
    failing_module = vul["failing_module"]
    if failing_module == "root":
        module_path = output_dir
    else:
        module_path = os.path.join(output_dir, failing_module)
    logger.debug(f"Module path: {module_path}")

    # check for artifacts, compiling if necessary
    try:
        assert not force_compile, "Forced compile"
        artifacts = get_artifacts(module_path)
    except AssertionError as err:
        logger.debug(err)
        vul4j.build(output_dir, clean=True)
        artifacts = get_artifacts(module_path)
    logger.debug(f"Found artifacts: {artifacts}")
    # exception can be thrown

    # select the correct jar path
    jar_path = next(file for file in artifacts if 'SNAPSHOT.jar' in file)
    # exception can be thrown

    # find modified methods and their classes
    method_getter_output = os.path.join(reports_dir, utils.suffix_filename("modifications.json", version))
    method_getter_command = f"java -jar {METHOD_GETTER_PATH} {output_dir} {method_getter_output}"
    method_getter_log_path = os.path.join(reports_dir, utils.suffix_filename("modifications.log", version))
    log_to_file = open(method_getter_log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL
    logger.debug(method_getter_command)

    logger.info("Running method getter...")
    subprocess.run(method_getter_command,
                   shell=True,
                   stdout=log_to_file,
                   stderr=subprocess.STDOUT,
                   check=True)
    assert os.path.exists(method_getter_output), "Method getter failed to create output files"
    # exception can be thrown

    # run spotbugs
    spotbugs_output = os.path.join(reports_dir, utils.suffix_filename("spotbugs_report.xml", version))
    spotbugs_command = f"java -jar {SPOTBUGS_PATH} -textui -low -xml={spotbugs_output} {jar_path}"
    spotbugs_log_path = os.path.join(reports_dir, utils.suffix_filename("spotbugs.log", version))
    log_to_file = open(spotbugs_log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL
    logger.debug(spotbugs_command)

    logger.info("Running spotbugs...")
    subprocess.run(spotbugs_command,
                   shell=True,
                   stdout=log_to_file,
                   stderr=subprocess.STDOUT,
                   check=True)
    assert os.path.exists(spotbugs_output), "Spotbugs failed to create output files"
    # exception can be thrown

    # find warnings in modified methods
    warnings = {}
    with open(method_getter_output, 'r') as file:
        modifications = json.load(file)

    tree = ElementTree.parse(spotbugs_output)
    root = tree.getroot()
    for java_class, java_methods in modifications.items():
        for java_method in java_methods:
            warnings[java_method] = set()
            for bug_instance in root.findall('.//BugInstance'):
                method = bug_instance.find('.//Method')
                if method and method.attrib['classname'] == java_class and method.attrib['name'] == java_method:
                    warnings[java_method].add(bug_instance.attrib['type'])

    warnings_output = os.path.join(reports_dir, utils.suffix_filename('warnings.json', version))
    with open(warnings_output, 'w') as file:
        json.dump({key: list(value) for key, value in warnings.items()}, file, indent=2)

    warning_list = []
    for warning_set in warnings.values():
        warning_list.extend(warning_set)
    logger.info(f"Warnings found: {warning_list if len(warning_list) else 'None'}")


def get_artifacts(module_path: str) -> list:
    """
    Search for artifacts in the target directory.
    Maven only.

    :param module_path: the module path
    """

    target_path = os.path.join(module_path, "target")
    assert os.path.exists(target_path), "Target directory not found"

    return (
            glob.glob(f"{target_path}/*.jar")
            + glob.glob(f"{target_path}/*.war")
            + glob.glob(f"{target_path}/*.ear")
            + glob.glob(f"{target_path}/*.zip")
    )
