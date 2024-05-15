import json
import os
from xml.etree.ElementTree import parse

import git

from config import REPRODUCTION_DIR, OUTPUT_DIR, VUL4J_ROOT


def get_commit_hash(commit_url: str):
    commit_hash = commit_url.split("/")[-1]
    if ".." in commit_hash:
        return commit_hash.split("..")[-1].strip()
    else:
        return commit_hash.strip()


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

    repo = git.Repo(VUL4J_ROOT)
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


def read_test_results_maven(vul, project_dir):
    surefire_report_files = []
    for r, dirs, files in os.walk(project_dir):
        for file in files:
            filePath = os.path.join(r, file)
            if (os.path.normpath("target/surefire-reports") in os.path.normpath(filePath) or os.path.normpath("target/failsafe-reports") in os.path.normpath(filePath)) and file.endswith(
                    '.xml') and file.startswith('TEST-'):
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