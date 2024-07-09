import glob
import json
import os
import subprocess
import sys
import xml.etree.ElementTree as ElementTree

from loguru import logger

import vul4j.utils as utils
import vul4j.vul4j_tools as vul4j
from vul4j.config import VUL4J_OUTPUT, SPOTBUGS_PATH, MODIFICATION_EXTRACTOR_PATH, LOG_TO_FILE

original_stdout = sys.stdout


class BugInstance:
    def __init__(self, bug_type: str, source: str, class_name: str, is_method: bool = True):
        self.bug_type = bug_type
        self.source = source
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
            if attribute is not None:
                attributes.append(cls(bug_instance.attrib["type"],
                                      attribute.attrib['name'],
                                      attribute.attrib['classname'],
                                      False))
        return attributes


def run_spotbugs(project_dir: str, version=None, force_compile=False) -> list:
    """
    Runs Spotbugs check on the project found in the provided directory.
    The project must contain a 'vulnerability_info.json' file.
    Creates a separate spotbugs directory in the project's vul4j work directory.

    If a version is provided (and is not None) the project will be compiled.
    One can manually force recompilation by setting force_compile to True.

    The project's target folder is searched for artifacts.

    The modification extractor extracts the modified classes, class attributes and method names
    into the modifications.json file.
    Then Spotbugs analysis is run.

    The spotbugs_report.xml file is checked for warnings in the modified code parts.
    The results are saved in the warnings.json file or warnings_version.json if a version was provided.

    :param project_dir:  path to the projects directory
    :param version: version name, used for naming output files
    :param force_compile:   recompile project
    """

    vul = vul4j.Vulnerability.from_json(project_dir)

    assert vul.build_system == "Maven", f"Incompatible build system: {vul.build_system}"

    # create spotbugs directory
    reports_dir = os.path.join(project_dir, VUL4J_OUTPUT, "spotbugs")
    os.makedirs(reports_dir, exist_ok=True)
    assert os.path.exists(reports_dir), "Failed to create spotbugs directory!"
    logger.debug("Spotbugs directory created!")

    # get module path where compiled jars are located
    failing_module = vul.failing_module
    if failing_module == "root":
        module_path = project_dir
    else:
        module_path = os.path.join(project_dir, failing_module)
    logger.debug(f"Module path: {module_path}")

    # find modified methods and their classes
    method_getter_output = os.path.join(reports_dir, "modifications.json")
    method_getter_command = f"java -jar {MODIFICATION_EXTRACTOR_PATH} {project_dir} {method_getter_output}"
    method_getter_log_path = os.path.join(reports_dir, "modifications.log")
    log_to_file = open(method_getter_log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL
    logger.debug(method_getter_command)

    logger.info("Extracting modifications...")
    subprocess.run(method_getter_command,
                   shell=True,
                   stdout=log_to_file,
                   stderr=subprocess.STDOUT,
                   env=utils.get_java_home_env("16"),
                   check=True)
    assert os.path.exists(method_getter_output), "Modification extractor failed to create output files!"

    # check for artifacts, compiling if necessary
    if force_compile:
        logger.debug("Forced compile")
        vul4j.build(project_dir, version, clean=True)

    # select the correct jar from artifacts
    jar_path = get_artifact(module_path)

    assert jar_path is not None, "No runnable artifact found!"

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
    warnings = {}
    with open(method_getter_output, 'r') as file:
        modifications = json.load(file)

    tree = ElementTree.parse(spotbugs_output)
    root = tree.getroot()
    method_bugs = BugInstance.extract_methods(root)
    attribute_bugs = BugInstance.extract_attributes(root)

    warning_list = []

    # recursive function to process the class dictionary
    def process_class(cls_dict, classname):
        processed_cls = {}
        for key, value in cls_dict.items():
            if key == "methods" or key == "attributes":
                processed_cls[key] = {name: [] for name in value}
                for bug in (method_bugs if key == "methods" else attribute_bugs):
                    if bug.class_name == classname and bug.source in set(value):
                        processed_cls[key][bug.source].append(bug.bug_type)
                        source_name = bug.source if bug.source != "<init>" else bug.class_name.split(".")[-1]
                        warning_list.append(f"{bug.bug_type}@{bug.class_name}#{source_name}")
            elif key == "classes":
                processed_cls[key] = {}
                for inner_classname, inner_class in value.items():
                    full_inner_classname = f"{classname}${inner_classname}"
                    processed_cls[key][inner_classname] = process_class(inner_class, full_inner_classname)
        return processed_cls

    for class_name, class_info in modifications.items():
        warnings[class_name] = process_class(class_info, class_name)

    warnings_output = os.path.join(reports_dir, utils.suffix_filename('warnings.json', version))
    with open(warnings_output, 'w') as file:
        json.dump(warnings, file, indent=2)

    logger.info(f"Warnings found: {json.dumps(warning_list, indent=2) if len(warning_list) else 'None'}")

    return warning_list


def get_artifact(module_path: str):
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

        jar_filenames = [
            f"{artifact_id.text}-{version.text}.jar" if artifact_id is not None and version is not None else "",
            f"{artifact_id.text}.jar" if artifact_id is not None else "",
            "SNAPSHOT.jar",
            "shaded.jar"
        ]

        for artifact in artifacts:
            for filename in jar_filenames:
                if filename in artifact:
                    return artifact
        return None

    except ElementTree.ParseError as err:
        logger.debug(err)
        raise AssertionError(f"Could not read pom.xml in {module_path}")
