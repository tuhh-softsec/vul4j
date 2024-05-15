import json
import os
import urllib.request
import subprocess
from xml.etree.ElementTree import parse
import zipfile

import git
from loguru import logger

from vul4j.config import REPRODUCTION_DIR, OUTPUT_DIR, VUL4J_GIT, JAVA7_HOME, JAVA8_HOME, SPOTBUGS_PATH, \
    METHOD_GETTER_PATH, DATASET_PATH

VOID = open(os.devnull, 'w')
SEPARATOR = "---------------------------------------------------------"


def get_commit_hash(commit_url: str):
    commit_hash = commit_url.split("/")[-1]
    if ".." in commit_hash:
        return commit_hash.split("..")[-1].strip()
    else:
        return commit_hash.strip()


def check_status():
    """
    Checks availability of vul4j dependencies.
    """

    # check vul4j.ini
    vul4j_config = os.path.exists(os.path.join(os.path.expanduser("~"), "vul4j.ini"))

    # check vul4j git
    vul4j_git = bool(VUL4J_GIT) and os.path.exists(os.path.join(VUL4J_GIT, ".git"))

    # check vul4j dataset
    vul4j_dataset = bool(DATASET_PATH) and os.path.exists(DATASET_PATH)

    # check java versions
    env = os.environ.copy()

    java7 = False
    if JAVA7_HOME:
        env["PATH"] = os.path.join(JAVA7_HOME, "bin") + os.pathsep + env["PATH"]
        java7 = "1.7" in str(subprocess.check_output("java -version", shell=True, stderr=subprocess.STDOUT, env=env))

    java8 = False
    if JAVA8_HOME:
        env["PATH"] = os.path.join(JAVA8_HOME, "bin") + os.pathsep + env["PATH"]
        java8 = "1.8" in str(subprocess.check_output("java -version", shell=True, stderr=subprocess.STDOUT, env=env))

    # check maven
    maven = subprocess.run("mvn --version", shell=True, stdout=subprocess.DEVNULL,
                           stderr=subprocess.STDOUT).returncode == 0

    # check spotbugs
    try:
        print(SPOTBUGS_PATH)
        spotbugs = (bool(SPOTBUGS_PATH) and
                    os.path.exists(SPOTBUGS_PATH))
                    #subprocess.run(f"java -jar {SPOTBUGS_PATH} -version").returncode == 0)
    except FileNotFoundError:
        spotbugs = False
        print("asd")

    # check method getter
    method_getter = (bool(METHOD_GETTER_PATH) and
                     subprocess.run(f"java -jar {METHOD_GETTER_PATH}", shell=True, stdout=subprocess.DEVNULL,
                                    stderr=subprocess.STDOUT).returncode == 1)

    def log_result(message: str, success: bool):
        logger.log("SUCCESS" if success else "ERROR", f"{message}: {'OK' if success else 'NOT FOUND'}")

    logger.info(SEPARATOR)
    log_result("VUL4J config file", vul4j_config)
    log_result("VUL4J git repository", vul4j_git)
    log_result("VUL4J dataset", vul4j_dataset)
    log_result("Java 7", java7)
    log_result("Java 8", java8)
    log_result("Maven", maven)
    log_result("Spotbugs", spotbugs)
    log_result("Spotbugs method getter", method_getter)
    logger.info(SEPARATOR)


def get_spotbugs():
    spotbugs_directory = os.path.expanduser("~/spotbugs")

    os.makedirs(spotbugs_directory, exist_ok=True)

    zip_file_path = os.path.join(spotbugs_directory, "spotbugs.zip")
    urllib.request.urlretrieve("https://github.com/spotbugs/spotbugs/releases/download/4.8.5/spotbugs-4.8.5.zip",
                               zip_file_path)

    with zipfile.ZipFile(zip_file_path, 'r') as zip_ref:
        zip_ref.extractall(spotbugs_directory)

    # Delete the zip file
    os.remove(zip_file_path)


def extract_failed_tests_from_test_results(test_results):
    failing_tests = set()
    failures = test_results['tests']['failures']
    passing_tests = test_results['tests']['passing_tests']
    skipping_tests = test_results['tests']['skipping_tests']
    if len(failures) == len(passing_tests) == len(skipping_tests) == 0:
        return None  # if all metrics are 0, then the build failed and no tests were run
    for failure in failures:
        failing_tests.add(failure['test_class'] + '#' + failure['test_method'])
    return failing_tests


def write_test_results_to_file(vul, test_results, revision):
    test_output_file = os.path.join(REPRODUCTION_DIR,
                                    '%s_%s_tests_%s.json' % (
                                        vul['project'].replace('-', '_'), vul['vul_id'].replace('-', '_'), revision))
    with (open(test_output_file, 'w', encoding='utf-8')) as f:
        f.write(test_results)


def read_vulnerability_from_output_dir(output_dir):
    with open(os.path.join(output_dir, OUTPUT_DIR, "vulnerability_info.json")) as info_file:
        return json.load(info_file)


def extract_patch_files(vul: dict, output_dir: str) -> None:
    """
    Compares the human_patch commit to the vulnerable HEAD commit and
    extracts the modified files into separate folders.
    It creates a 'human_patch' and a 'vulnerable' directory and places the corresponding files in them.
    A 'paths.json' file is also placed in each directory which points to the files location in the project.

    :param vul: vulnerability dictionary
    :param output_dir: path to the projects directory
    """
    human_patch_dir = "human_patch"
    vulnerable_dir = "vulnerable"
    paths_filename = "paths.json"

    repo = git.Repo(VUL4J_GIT)
    diff = repo.commit(vul["fixing_commit_hash"]).diff(repo.head.commit)
    changed_java_source_files = []

    for modified_file in diff.iter_change_type("M"):
        file_path = modified_file.a_path
        if file_path.endswith(".java") and ("test/" not in file_path and "tests/" not in file_path):
            changed_java_source_files.append(modified_file)

    # extract vulnerable and patched code into separate folders
    os.makedirs(os.path.join(output_dir, OUTPUT_DIR, human_patch_dir))
    os.makedirs(os.path.join(output_dir, OUTPUT_DIR, vulnerable_dir))

    for file in changed_java_source_files:
        human_patch = file.a_blob
        vulnerable = file.b_blob

        # write human_patch file content
        with open(os.path.join(output_dir, OUTPUT_DIR, human_patch_dir, human_patch.name), "w",
                  encoding="utf-8") as f:
            f.write(human_patch.data_stream.read().decode("utf-8"))

        # write vulnerable file content
        with open(os.path.join(output_dir, OUTPUT_DIR, vulnerable_dir, vulnerable.name), "w",
                  encoding="utf-8") as f:
            f.write(vulnerable.data_stream.read().decode("utf-8"))

    # write paths into file
    with open(os.path.join(output_dir, OUTPUT_DIR, human_patch_dir, paths_filename), "w",
              encoding="utf-8") as f:
        json.dump({entry.a_blob.name: entry.a_blob.path for entry in changed_java_source_files}, f, indent=2)

    with open(os.path.join(output_dir, OUTPUT_DIR, vulnerable_dir, paths_filename), "w",
              encoding="utf-8") as f:
        json.dump({entry.b_blob.name: entry.b_blob.path for entry in changed_java_source_files}, f, indent=2)


def remove_test_results(project_dir):
    for r, dirs, files in os.walk(project_dir):
        for file in files:
            filePath = os.path.join(r, file)
            if (("target/surefire-reports" in filePath
                 or "target/failsafe-reports" in filePath
                 or "build/test-results" in filePath)
                    and file.endswith('.xml') and file.startswith('TEST-')):
                os.remove(filePath)


def read_test_results(vul: dict, project_dir: str):
    surefire_report_files = []
    for r, dirs, files in os.walk(project_dir):
        for file in files:
            filePath = os.path.join(r, file)
            if (("target/surefire-reports" in filePath
                 or "target/failsafe-reports" in filePath
                 or "build/test-results" in filePath)  # gradle
                    and file.endswith('.xml') and file.startswith('TEST-')):
                surefire_report_files.append(filePath)

    failing_tests_count = 0
    error_tests_count = 0
    passing_tests_count = 0
    skipping_tests_count = 0

    passingTestCases = set()
    skippingTestCases = set()

    failures = []

    for report_file in surefire_report_files:
        with open(report_file, 'r') as file:
            xml_tree = parse(file)
            testsuite_class_name = xml_tree.getroot().attrib['name']
            test_cases = xml_tree.findall('testcase')
            for test_case in test_cases:
                failure_list = {}
                class_name = test_case.attrib[
                    'classname'] if 'classname' in test_case.attrib else testsuite_class_name
                method_name = test_case.attrib['name']
                failure_list['test_class'] = class_name
                failure_list['test_method'] = method_name

                failure = test_case.findall('failure')
                if len(failure) > 0:
                    failing_tests_count += 1
                    failure_list['failure_name'] = failure[0].attrib['type']
                    if 'message' in failure[0].attrib:
                        failure_list['detail'] = failure[0].attrib['message']
                    failure_list['is_error'] = False
                    failures.append(failure_list)
                else:
                    error = test_case.findall('error')
                    if len(error) > 0:
                        error_tests_count += 1
                        failure_list['failure_name'] = error[0].attrib['type']
                        if 'message' in error[0].attrib:
                            failure_list['detail'] = error[0].attrib['message']
                        failure_list['is_error'] = True
                        failures.append(failure_list)
                    else:
                        skipTags = test_case.findall("skipped")
                        if len(skipTags) > 0:
                            skipping_tests_count += 1
                            skippingTestCases.add(class_name + '#' + method_name)
                        else:
                            passing_tests_count += 1
                            passingTestCases.add(class_name + '#' + method_name)

    repository = {'name': vul['project'], 'url': vul['project_url'], 'human_patch_url': vul['human_patch_url']}
    overall_metrics = {'number_running': passing_tests_count + error_tests_count + failing_tests_count,
                       'number_passing': passing_tests_count, 'number_error': error_tests_count,
                       'number_failing': failing_tests_count, 'number_skipping': skipping_tests_count}
    tests = {'overall_metrics': overall_metrics, 'failures': failures, 'passing_tests': list(passingTestCases),
             'skipping_tests': list(skippingTestCases)}

    json_data = {'vul_id': vul['vul_id'], 'cve_id': vul['cve_id'], 'repository': repository, 'tests': tests}
    return json_data
