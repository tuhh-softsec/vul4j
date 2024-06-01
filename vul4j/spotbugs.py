import glob
import json
import os
import subprocess
import sys
import xml.etree.ElementTree as ElementTree

from loguru import logger

import vul4j.utils as utils
import vul4j.vul4j_tools as vul4j
from vul4j.config import VUL4J_OUTPUT, SPOTBUGS_PATH, METHOD_GETTER_PATH, LOG_TO_FILE

original_stdout = sys.stdout


class BugInstance:
    def __init__(self, bug_type: str, name: str, class_name: str, is_method: bool = True):
        self.bug_type = bug_type
        self.name = name
        self.class_name = class_name
        self.content_type = is_method

    @classmethod
    def extract_methods(cls, root):
        methods = []
        for bug_instance in root.findall('.//BugInstance'):
            method = bug_instance.find('.//Method')
            if method is not None:
                methods.append(cls(bug_instance.attrib["type"],
                                   method.attrib['name'],
                                   method.attrib['classname'],
                                   True))
        return methods

    @classmethod
    def extract_attributes(cls, root):
        attributes = []
        for bug_instance in root.findall('.//BugInstance'):
            attribute = bug_instance.find('.//Field')
            if attribute is not None and bug_instance.find('.//Method') is None:
                attributes.append(cls(bug_instance.attrib["type"],
                                      attribute.attrib['name'],
                                      attribute.attrib['classname'],
                                      False))
        return attributes


def run_spotbugs(output_dir: str, version=None, force_compile=False) -> list:
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

    vul = vul4j.Vulnerability.from_json(output_dir)

    assert vul.build_system == "Maven", f"Incompatible build system: {vul.build_system}"

    # create spotbugs directory
    reports_dir = os.path.join(output_dir, VUL4J_OUTPUT, "spotbugs")
    os.makedirs(reports_dir, exist_ok=True)
    assert os.path.exists(reports_dir), "Failed to create spotbugs directory!"
    logger.debug("Spotbugs directory created!")

    # get module path where compiled jars are located
    failing_module = vul.failing_module
    if failing_module == "root":
        module_path = output_dir
    else:
        module_path = os.path.join(output_dir, failing_module)
    logger.debug(f"Module path: {module_path}")

    # check for artifacts, compiling if necessary
    if force_compile:
        logger.debug("Forced compile")
        vul4j.build(output_dir, version, clean=True)

    # select the correct jar from artifacts
    jar_path = get_artifact(module_path)

    # find modified methods and their classes
    method_getter_output = os.path.join(reports_dir, "modifications.json")
    method_getter_command = f"java -jar {METHOD_GETTER_PATH} {output_dir} {method_getter_output}"
    method_getter_log_path = os.path.join(reports_dir, "modifications.log")
    log_to_file = open(method_getter_log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL
    logger.debug(method_getter_command)

    logger.info("Running method getter...")
    subprocess.run(method_getter_command,
                   shell=True,
                   stdout=log_to_file,
                   stderr=subprocess.STDOUT,
                   check=True)
    assert os.path.exists(method_getter_output), "Method getter failed to create output files!"

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
    assert os.path.exists(spotbugs_output), "Spotbugs failed to create output files!"

    # find warnings in modified methods
    warnings = {"attributes": [], "methods": []}
    with open(method_getter_output, 'r') as file:
        modifications = json.load(file)

    tree = ElementTree.parse(spotbugs_output)
    root = tree.getroot()
    methods = BugInstance.extract_methods(root)
    attributes = BugInstance.extract_attributes(root)

    for java_class, mods in modifications.items():
        warnings["attributes"].extend([bug.bug_type for bug in attributes
                                       if bug.class_name == java_class and bug.name in set(mods["attributes"])])
        warnings["methods"].extend([bug.bug_type for bug in methods
                                    if bug.class_name == java_class and bug.name in set(mods["methods"])])

    warnings_output = os.path.join(reports_dir, utils.suffix_filename('warnings.json', version))
    with open(warnings_output, 'w') as file:
        json.dump({key: list(value) for key, value in warnings.items()}, file, indent=2)

    warning_list = []
    for warning_set in warnings.values():
        warning_list.extend(warning_set)
    logger.info(f"Warnings found: {warning_list if len(warning_list) else 'None'}")

    return warning_list


def get_artifact(module_path: str) -> str:
    """
    Search for artifacts in the target directory.
    Maven only.

    :param module_path: the module path
    """

    target_path = os.path.join(module_path, "target")
    assert os.path.exists(target_path), "Project's target directory not found!"

    artifacts = (
            glob.glob(f"{target_path}/*.jar")
            + glob.glob(f"{target_path}/*.war")
            + glob.glob(f"{target_path}/*.ear")
            + glob.glob(f"{target_path}/*.zip")
    )
    logger.debug(f"Found artifacts: {artifacts}")

    try:
        tree = ElementTree.parse(os.path.join(module_path, "pom.xml"))
        root = tree.getroot()

        namespaces = {'m': 'http://maven.apache.org/POM/4.0.0'}

        artifact_id = root.find('m:artifactId', namespaces)
        version = root.find('m:version', namespaces)

        if version is None:
            parent = root.find('m:parent', namespaces)
            if parent is not None:
                version = parent.find('m:version', namespaces)

        jar_filename = f"{artifact_id.text}-{version.text}.jar"

        return next(file for file in artifacts if (jar_filename in file or
                                                   'SNAPSHOT.jar' in file or
                                                   'shaded.jar' in file))

    except ElementTree.ParseError as err:
        logger.debug(err)
        raise AssertionError(f"Could not read pom.xml in {module_path}")
