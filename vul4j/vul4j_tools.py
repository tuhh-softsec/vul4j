import csv
import json
import os
import re
import shutil
import subprocess
import xml.etree.ElementTree as ElementTree
from shutil import copytree, ignore_patterns

import git
from loguru import logger

import vul4j.spotbugs as spotbugs
import vul4j.utils as utils
from vul4j.config import JAVA7_HOME, MVN_ARGS, JAVA8_HOME, VUL4J_OUTPUT, LOG_TO_FILE, DATASET_PATH, \
    TEMP_CLONE_DIR, VUL4J_GIT, REPRODUCTION_DIR, VUL4J_COMMITS_URL


class VulnerabilityNotFoundError(Exception):
    def __init__(self, message):
        super().__init__(message)


def load_vulnerabilities() -> dict:
    """
    Loads vulnerability information from the csv dataset into a dictionary of dictionaries.
    The program exits if the dataset is not found.

    :return: dictionary of vulnerabilities
    """

    try:
        assert os.path.exists(DATASET_PATH)
    except AssertionError:
        logger.critical("Vul4j dataset not found!")
        exit(1)

    def get_column(column):
        value = row.get(column)
        return value.strip() if value is not None else value

    vulnerabilities = {}

    with open(DATASET_PATH) as dataset_file:
        reader = csv.DictReader(dataset_file, delimiter=',')
        for row in reader:
            vul_id = get_column('vul_id')
            vulnerabilities[vul_id] = {
                "vul_id": vul_id,
                "cve_id": get_column("cve_id"),
                "cwe_id": get_column("cwe_id"),
                "project": get_column("repo_slug").replace("/", "_"),
                "project_url": f"https://github.com/{get_column('repo_slug')}",
                "build_system": get_column("build_system"),
                "compliance_level": get_column("compliance_level"),
                "compile_cmd": get_column("compile_cmd"),
                "test_all_cmd": get_column("test_all_cmd"),
                "test_cmd": get_column("test_cmd"),
                "cmd_options": get_column("cmd_options"),
                "failing_module": get_column('failing_module'),
                "fixing_commit_hash": get_commit_hash(get_column("human_patch")),
                "human_patch_url": get_column("human_patch"),
                "failing_tests": get_column("failing_tests").split(','),
                "warning": get_column("warning")
            }

    return vulnerabilities


def get_commit_hash(commit_url: str):
    commit_hash = commit_url.split("/")[-1]
    if ".." in commit_hash:
        return commit_hash.split("..")[-1].strip()
    else:
        return commit_hash.strip()


def get_info(vul_id: str) -> None:
    """
    Logs information about the specified vulnerability.

    Raises VulnerabilityNotFoundError if the vulnerability is not found.

    :param vul_id: vulnerability id
    """
    vul = get_vulnerability(vul_id)
    logger.info(json.dumps(vul, indent=4))


def get_vulnerability(vul_id: str) -> dict:
    """
    Loads specified vulnerability from dataset.

    Raises VulnerabilityNotFoundError if the vulnerability is not found.

    :param vul_id: vulnerability id
    :return: dictionary of vulnerability data
    """

    vul = load_vulnerabilities().get(vul_id)
    if vul is None:
        raise VulnerabilityNotFoundError(f"Vulnerability not found: {vul_id}!")

    return vul


def checkout(vul_id: str, project_dir: str) -> None:
    """
    Copies and initializes the specified vulnerability into the output directory.

    If the vulnerability is from the official VUL4J dataset,
    the function checks out the corresponding branch and copies its content to the destination.
    If the vulnerability was newly added to the dataset,
    the function clones the project into a temporary directory and then copies its content to the destination.

    After copying, the function extracts the vulnerable and patched files into separate directories,
    as well as creates a vulnerability.json file which is used by other vul4j functions.

    Finally, a git repo is initialized in the destination directory with two commits:
    one with vulnerable files applies, and another with patched files applied.
    The vulnerable commit is checked out.

    :param vul_id:  vulnerability ID to be checked out
    :param project_dir:  where the checked out project will be copied
    """

    vul = get_vulnerability(vul_id)

    assert not os.path.exists(project_dir), f"Directory '{project_dir}' already exists!"

    # check if vul4j git has a branch for the vulnerability
    repo = git.Repo(VUL4J_GIT)
    clone = vul_id in [branch for branch in repo.heads]

    if clone:
        os.makedirs(TEMP_CLONE_DIR, exist_ok=True)
        project_path = os.path.join(TEMP_CLONE_DIR, vul_id)
        logger.info(f"Cloning project into '{project_path}'")
        git.Repo.clone_from(vul['project_url'], project_path)
        repo = git.Repo(project_path)
        repo.git.checkout(vul['fixing_commit_hash'])
        logger.info("Done cloning!")
    else:
        logger.info("Checking out project...")
        repo = git.Repo(VUL4J_GIT)
        repo.git.reset("--hard")
        repo.git.checkout("--")
        repo.git.clean("-fdx")
        repo.git.checkout("-f", vul_id.upper())

    # copy to working directory
    copytree(TEMP_CLONE_DIR if clone else VUL4J_GIT, project_dir, ignore=ignore_patterns('.git'))

    # extract patched and vulnerable files
    extract_patch_files(vul, project_dir, clone)

    # write vulnerability info into file
    vul_info_file_path = os.path.join(project_dir, VUL4J_OUTPUT, "vulnerability_info.json")
    with open(vul_info_file_path, "w", encoding='utf-8') as f:
        f.write(json.dumps(vul, indent=2))

    assert os.path.exists(vul_info_file_path), "Failed to create vulnerability_info.json file"

    # create vulnerable and human_patch commits
    dest_repo = git.Repo.init(project_dir)
    with dest_repo.config_writer() as git_config:
        git_config.set_value("user", "name", "vul4j")
        git_config.set_value("user", "email", "vul4j@vul4j.org")
    dest_repo.git.add('-A')
    dest_repo.git.commit("-m", "vulnerable")

    apply(project_dir, 'human_patch', True)

    dest_repo.git.add('-A')
    dest_repo.git.commit("-m", "human_patch")
    dest_repo.git.checkout("HEAD~1")

    if clone:
        # remove cloned repo
        project_path = os.path.join(TEMP_CLONE_DIR, vul_id)
        shutil.rmtree(project_path, ignore_errors=True)
        assert not os.path.exists(project_path), "Failed to remove temporary cloned files"
    else:
        # revert to main branch
        repo.git.reset("--hard")
        repo.git.checkout("--")
        repo.git.clean("-fdx")
        repo.git.checkout("-f", "main")


def build(project_dir: str, suffix: str = None, clean: bool = False) -> None:
    """
    Compiles the project found in the provided directory.
    The project must contain a 'vulnerability_info.json' file.

    Important errors can be: VulnerabilityNotFoundError, AssertionError, subprocess.CalledProcessError,
    but might raise other errors too.

    :param project_dir: path to the project to be compiled
    :param suffix: suffix to add to log files if needed (None means no suffix)
    :param clean: clean project before compiling
    """

    vul = read_vul_from_file(project_dir)

    assert (vul.get("compile_cmd") is not None
            and vul.get("compile_cmd") != ""), f"No compile command found for {vul['vul_id']}"

    if clean:
        utils.clean_build(project_dir, vul["build_system"])

    java_home = utils.get_java_home(vul['compliance_level'])
    logger.debug(f"java home: {java_home}")

    env = os.environ.copy()
    env["PATH"] = os.path.join(java_home, "bin") + os.pathsep + env["PATH"]
    env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
    env["MAVEN_OPTS"] = MVN_ARGS

    compile_cmd = vul['compile_cmd'] + " " + vul['cmd_options']
    logger.debug(compile_cmd)

    log_path = os.path.join(project_dir, VUL4J_OUTPUT, utils.suffix_filename("compile.log", suffix))
    log_output = open(log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL

    logger.info("Compiling...")
    subprocess.run(compile_cmd,
                   shell=True,
                   stdout=log_output,
                   stderr=subprocess.STDOUT,
                   cwd=project_dir,
                   env=env,
                   check=True)


def apply(project_dir: str, version: str, quiet: bool = False) -> None:
    """
    Copies the selected file versions into their respective locations.
    The version folder must contain a paths.json file which describes the location of each file in the project.

    Important errors can be: VulnerabilityNotFoundError, but might raise other errors too.

    :param project_dir:  where the project is located
    :param version: the version to apply
    :param quiet:   does not display messages if True
    """

    vul = read_vul_from_file(project_dir)

    if version == "human_patch" and not quiet:
        logger.warning(f"Please check {VUL4J_COMMITS_URL + vul['vul_id']} if build fails.")

    with open(os.path.join(project_dir, VUL4J_OUTPUT, version, "paths.json"), "r") as file:
        paths = json.load(file)

    if not quiet:
        logger.info(f"Applying version: {version}")
    for file, path in paths.items():
        shutil.copy(str(os.path.join(project_dir, VUL4J_OUTPUT, version, file)),
                    str(os.path.join(project_dir, "/".join(path.split("/")[:-1]))))

    if not quiet:
        logger.success(f"Version applied: {version}")


def test(project_dir: str, batch_type: str, suffix: str = None, clean: bool = False) -> dict:
    """
    Runs test cases in the project found in the provided directory.
    The project must contain a 'vulnerability_info.json' file in the vul4j directory defined in the config.

    Batch type defines which tests to run.
    If 'all' is provided, all available test will be run.
    If 'povs' is provided, only the vulnerable tests will be run.

    :param project_dir: path to the project to be tested
    :param batch_type: 'all' for all tests, 'povs' for vulnerable tests only
    :param suffix: suffix to add to log files if needed (None means no suffix)
    :param clean: clean project before running tests
    :return: test results dictionary (number of running, passing, failing, error, skipping)
    """

    vul = read_vul_from_file(project_dir)

    assert vul.get("test_cmd") is not None and vul.get("test_cmd") != "", f"No test command found for {vul['vul_id']}"

    if clean:
        utils.clean_build(project_dir, vul["build_system"])

    java_home = utils.get_java_home(vul['compliance_level'])
    logger.debug(f"java home: {java_home}")

    cmd_type = "test_all_cmd" if batch_type == "all" else "test_cmd"

    env = os.environ.copy()
    env["PATH"] = os.path.join(java_home, "bin") + os.pathsep + env["PATH"]
    env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
    env["MAVEN_OPTS"] = MVN_ARGS

    test_cmd = vul[cmd_type] + " " + vul['cmd_options']
    logger.debug(test_cmd)

    log_path = os.path.join(project_dir, VUL4J_OUTPUT, utils.suffix_filename("testing.log", suffix))
    log_output = open(log_path, "w", encoding="utf-8") if LOG_TO_FILE else subprocess.DEVNULL

    logger.info(f"Running {'' if batch_type == 'all' else 'PoV '}tests...")
    subprocess.run(test_cmd,
                   shell=True,
                   stdout=log_output,
                   stderr=subprocess.STDOUT,
                   cwd=project_dir,
                   env=env)

    test_results = read_test_results(vul, project_dir)

    with (open(os.path.join(project_dir,
                            VUL4J_OUTPUT,
                            utils.suffix_filename("testing_results.json", suffix)), "w")) as f:
        json.dump(test_results, f, indent=2)

    test_stats = test_results["tests"]["overall_metrics"]

    logger.log(("ERROR" if test_stats["number_running"] == 0 else "INFO"),
               "Number of running tests: " + str(test_stats["number_running"]))
    if test_stats["number_passing"] != 0:
        logger.success("Number of passing tests: " + str(test_stats["number_passing"]))
    if test_stats["number_skipping"] != 0:
        logger.warning("Number of skipping tests: " + str(test_stats["number_skipping"]))
    if test_stats["number_failing"] != 0:
        logger.error("Number of failing tests: " + str(test_stats["number_failing"]))
    if test_stats["number_error"] != 0:
        logger.error("Number of errors: " + str(test_stats["number_error"]))

    return test_results


def reproduce(vul_ids):
    """
    Verifies vulnerabilities that are in the official VUL4J dataset or reproduces newly added entries.

    First the vulnerable version is compiled, tested and analyzed with Spotbugs.
    Then the human_patch version is run.

    :param vul_ids: single id or list of ids to reproduce
    :return:
    """

    vulnerabilities = []

    for vul_id in vul_ids:
        try:
            vul = get_vulnerability(vul_id)
            vulnerabilities.append(vul)
        except VulnerabilityNotFoundError as err:
            logger.warning(f"{err} Excluding...")
            continue

    logger.info(f"Reproducing {len(vulnerabilities)} vulnerabilities...")

    os.makedirs(REPRODUCTION_DIR, exist_ok=True)
    assert os.path.exists(REPRODUCTION_DIR), "Failed to create reproduction directory!"

    with open(os.path.join(REPRODUCTION_DIR, 'successful_vulnerabilities.txt'), 'a+') as success_vulnerabilities:
        for vul in vulnerabilities:
            try:
                tests_ran = False
                spotbugs_ok = False

                logger.info(vul['vul_id'].center(60, "-"))
                project_dir = str(os.path.join(REPRODUCTION_DIR, vul['vul_id']))

                # remove existing project directory
                if os.path.exists(project_dir):
                    shutil.rmtree(project_dir)

                checkout(vul['vul_id'], project_dir)

                # vulnerable
                version = "vulnerable"
                try:
                    logger.info(f"Applying version: {version}")
                    apply(project_dir, version, quiet=True)
                except FileNotFoundError:
                    logger.error(f"No such version: {version}")
                    continue

                force_recompile = False
                try:
                    build(project_dir, version, clean=True)
                except subprocess.CalledProcessError:
                    force_recompile = True
                    logger.error("Compile failed! Keep going...")

                try:
                    tests = "povs" if vul["failing_tests"] else "all"
                    test_results = test(project_dir, tests, version)
                    failing_tests = set()
                    failures = test_results['tests']['failures']
                    passing_tests = test_results['tests']['passing_tests']
                    skipping_tests = test_results['tests']['skipping_tests']

                    if len(failures) == len(passing_tests) == len(skipping_tests) == 0:
                        logger.error("Build failed, no tests were run! This is acceptable here.")
                    else:
                        for failure in failures:
                            failing_tests.add(failure['test_class'] + '#' + failure['test_method'])
                        if len(failing_tests) == 0:
                            logger.critical("Vulnerable revision must contain at least 1 failing test!!!")
                            continue
                        else:
                            logger.error(f"Failing tests: {list(failing_tests)}")
                except subprocess.CalledProcessError:
                    logger.error("Tests failed! Keep going...")
                except AssertionError as err:
                    logger.warning(err)

                if force_recompile:
                    logger.info("Compile failed previously. Trying again for Spotbugs...")
                try:
                    spotbugs.run_spotbugs(project_dir, None, force_recompile)
                except subprocess.CalledProcessError:
                    # compile, method getter or spotbugs fails
                    logger.error("Task failed! Keep going...")
                except StopIteration:
                    # the correct jar was not found
                    logger.error("No runnable artifact found! Keep going...")

                # human patch
                version = "human_patch"
                try:
                    logger.info(f"Applying version: {version}")
                    apply(project_dir, version, quiet=True)
                except FileNotFoundError:
                    logger.error(f"No such version: {version}")
                    continue

                force_recompile = False
                try:
                    build(project_dir, version, clean=True)
                except subprocess.CalledProcessError:
                    force_recompile = True
                    logger.error("Compile failed! Keep going...")

                try:
                    tests = "povs" if vul["failing_tests"] else "all"
                    test_results = test(project_dir, tests, version)
                    failing_tests = set()
                    failures = test_results['tests']['failures']
                    passing_tests = test_results['tests']['passing_tests']
                    skipping_tests = test_results['tests']['skipping_tests']

                    if len(failures) == len(passing_tests) == len(skipping_tests) == 0:
                        logger.error("Build failed, no tests were run!")
                    else:
                        tests_ran = True
                        for failure in failures:
                            failing_tests.add(failure['test_class'] + '#' + failure['test_method'])
                        if len(failing_tests) == 0:
                            logger.success(f"No failing tests found!")
                        else:
                            logger.error(f"Failing tests: {list(failing_tests)}")
                            logger.critical("Patched version must contain no failing test!!!")
                            continue
                except subprocess.CalledProcessError:
                    logger.error("Tests failed!")
                except AssertionError as err:
                    logger.warning(err)

                if force_recompile:
                    logger.info("Compile failed previously. Trying again for Spotbugs...")
                try:
                    warnings_human_patch = spotbugs.run_spotbugs(project_dir, None, force_recompile)
                    if vul["warning"] not in warnings_human_patch:
                        spotbugs_ok = True
                except subprocess.CalledProcessError:
                    # compile, method getter or spotbugs fails
                    logger.error("Task failed!")
                except StopIteration:
                    # the correct jar was not found
                    logger.error("No runnable artifact found!")

                if tests_ran:
                    if spotbugs_ok:
                        logger.success(f"{vul['vul_id']} has been reproduced successfully!")
                    else:
                        logger.warning(f"The vulnerabilities in {vul['vul_id']} have been reproduced successfully, "
                                       f"but the Spotbugs analysis did not pass!")
                else:
                    if spotbugs_ok:
                        logger.success(f"Spotbugs check for {vul['vul_id']} has been reproduced successfully!")
                    else:
                        logger.error(f"Spotbugs check for {vul['vul_id']} failed!")

                success_vulnerabilities.write(vul['vul_id'] + '\n')
                success_vulnerabilities.flush()
            except (VulnerabilityNotFoundError, AssertionError) as err:
                logger.error(err)
                continue


# TODO fix
def classpath(output_dir):
    """
    modify from https://github.com/program-repair/RepairThemAll/blob/master/script/info_json_file.py

    :param output_dir:
    :return:
    """

    vul = read_vul_from_file(output_dir)
    # exception can be thrown

    """
    ----------------------------------------
    ONLY for Gradle projects, make sure to put this task into the build.gradle of failing module
    ----------------------------------------
    task copyClasspath {
        def runtimeClasspath = sourceSets.test.runtimeClasspath
        inputs.files( runtimeClasspath )
        doLast {
            new File(projectDir, "classpath.info").text = runtimeClasspath.join( File.pathSeparator )
        }
    }
    ----------------------------------------
    """

    if vul['build_system'] == "Gradle":
        test_all_cmd = vul['test_all_cmd']

        if vul['failing_module'] is None or vul['failing_module'] == 'root':
            cp_cmd = [
                "./gradlew copyClasspath",
                "cat classpath.info"
            ]
        else:
            matched = re.search("(./gradlew :.*:)test$", test_all_cmd)
            if matched is None:
                logger.error("The test all command should follow the regex \"(./gradlew :.*:)test$\"!"
                             f" It is now {test_all_cmd}")
                return

            gradle_classpath_cmd = matched.group(1) + "copyClasspath"
            classpath_info_file = os.path.join(vul['failing_module'], 'classpath.info')
            cat_classpath_info_cmd = "cat " + classpath_info_file
            cp_cmd = [
                gradle_classpath_cmd,
                cat_classpath_info_cmd
            ]

    elif vul['build_system'] == "Maven":
        cmd_options = vul['cmd_options']
        failing_module = vul['failing_module']
        if failing_module != "root" and failing_module != "":
            cp_cmd = [
                f"mvn dependency:build-classpath -Dmdep.outputFile='classpath.info' -pl {failing_module} {cmd_options}",
                f"cat {failing_module}/classpath.info"
            ]
        else:
            cp_cmd = [
                f"mvn dependency:build-classpath -Dmdep.outputFile='classpath.info' {cmd_options}",
                "cat classpath.info"
            ]

    else:
        logger.error(f"Not support for {vul['vul_id']}")
        exit(1)

    java_home = JAVA7_HOME if vul['compliance_level'] <= 7 else JAVA8_HOME

    env = os.environ.copy()
    env["PATH"] = os.path.join(java_home, "bin") + os.pathsep + env["PATH"]
    env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
    env["MAVEN_OPTS"] = MVN_ARGS

    subprocess.run(cp_cmd[0],
                   shell=True,
                   stdout=subprocess.STDOUT,
                   stderr=subprocess.STDOUT,
                   env=env,
                   cwd=output_dir,
                   check=True)

    classpath_result = subprocess.check_output(cp_cmd[1],
                                               shell=True,
                                               cwd=output_dir,
                                               env=env)
    return classpath_result


def read_vul_from_file(output_dir: str) -> dict:
    """
    Reads vulnerability info from vulnerability_info.json found in the project's vul4j work directory.

    :param output_dir: path to the project (which was prepared by the vul4j tool)
    :return: dictionary with vulnerability info or None if the info file is not found or error occurred
    """

    try:
        logger.debug("Reading vulnerability from file...")
        with open(os.path.join(output_dir, VUL4J_OUTPUT, "vulnerability_info.json")) as info_file:
            vul = json.load(info_file)
        assert vul.get("vul_id") is not None, "vul_id not found in dict, info file probably empty or incomplete"
        return vul
    except (OSError, AssertionError) as err:
        logger.debug(err)
        raise VulnerabilityNotFoundError("No vulnerability found in the directory!")


def extract_patch_files(vul: dict, project_dir: str, compare_with_parent: bool = False) -> None:
    """
    Compares the human_patch commit to the vulnerable HEAD commit by default and
    extracts the modified files into separate folders.
    If compare_with_parent is True, it will compare to the parent of the fixing hash.
    It creates a 'human_patch' and a 'vulnerable' directory and places the corresponding files in them.
    A 'paths.json' file is also placed in each directory which points to the files location in the project.

    :param vul: vulnerability dictionary
    :param project_dir: path to the projects directory
    :param compare_with_parent: if True, it will compare the fixing commit hash with
    """
    human_patch_dir = "human_patch"
    vulnerable_dir = "vulnerable"
    paths_filename = "paths.json"

    repo = git.Repo(VUL4J_GIT)
    compare_to = f"{vul['fixing_commit_hash']}~1" if compare_with_parent else repo.head.commit
    logger.debug(f"Comparing fixing commit to {'parent' if compare_with_parent else 'HEAD'}...")
    diff = repo.commit(vul["fixing_commit_hash"]).diff(compare_to)
    changed_java_source_files = []

    for modified_file in diff.iter_change_type("M"):
        file_path = modified_file.a_path
        if file_path.endswith(".java") and ("test/" not in file_path and "tests/" not in file_path):
            changed_java_source_files.append(modified_file)

    # extract vulnerable and patched code into separate folders
    os.makedirs(os.path.join(project_dir, VUL4J_OUTPUT, human_patch_dir))
    os.makedirs(os.path.join(project_dir, VUL4J_OUTPUT, vulnerable_dir))

    logger.debug("Writing file contents...")
    for file in changed_java_source_files:
        human_patch = file.a_blob
        vulnerable = file.b_blob

        # write human_patch file content
        with open(os.path.join(project_dir, VUL4J_OUTPUT, human_patch_dir, human_patch.name), "w",
                  encoding="utf-8") as f:
            f.write(human_patch.data_stream.read().decode("utf-8"))

        # write vulnerable file content
        with open(os.path.join(project_dir, VUL4J_OUTPUT, vulnerable_dir, vulnerable.name), "w",
                  encoding="utf-8") as f:
            f.write(vulnerable.data_stream.read().decode("utf-8"))

    # write paths into file
    logger.debug("Writing paths data...")
    with open(os.path.join(project_dir, VUL4J_OUTPUT, human_patch_dir, paths_filename), "w",
              encoding="utf-8") as f:
        json.dump({entry.a_blob.name: entry.a_blob.path for entry in changed_java_source_files}, f, indent=2)

    with open(os.path.join(project_dir, VUL4J_OUTPUT, vulnerable_dir, paths_filename), "w",
              encoding="utf-8") as f:
        json.dump({entry.b_blob.name: entry.b_blob.path for entry in changed_java_source_files}, f, indent=2)


def read_test_results(vul: dict, project_dir: str) -> dict:
    """
    Reads test results from result files.

    :param vul: vulnerability dictionary
    :param project_dir: where the project is located
    :return:    dictionary of test results
    """

    logger.debug("Reading test results...")

    # find report files
    report_files = []
    for r, dirs, files in os.walk(project_dir):
        for file in files:
            filePath = os.path.join(r, file)
            if (("target/surefire-reports" in filePath
                 or "target/failsafe-reports" in filePath
                 or "build/test-results" in filePath)  # gradle
                    and file.endswith('.xml') and file.startswith('TEST-')):
                report_files.append(filePath)

    failing_tests_count = 0
    error_tests_count = 0
    passing_tests_count = 0
    skipping_tests_count = 0

    failures = []
    passing_test_cases = set()
    skipping_test_cases = set()

    for report_file in report_files:
        with open(report_file, 'r') as file:
            tree = ElementTree.parse(file)
            testsuite_class_name = tree.getroot().attrib['name']
            test_cases = tree.findall('testcase')

            for test_case in test_cases:
                failure_list = {}
                class_name = test_case.attrib['classname'] if 'classname' in test_case.attrib else testsuite_class_name
                method_name = test_case.attrib['name']
                failure_list['test_class'] = class_name
                failure_list['test_method'] = method_name

                # failure
                failure = test_case.findall('failure')
                if len(failure) > 0:
                    failing_tests_count += 1
                    failure_list['failure_name'] = failure[0].attrib['type']
                    if 'message' in failure[0].attrib:
                        failure_list['detail'] = failure[0].attrib['message']
                    failure_list['is_error'] = False
                    failures.append(failure_list)
                    continue

                # error
                error = test_case.findall('error')
                if len(error) > 0:
                    error_tests_count += 1
                    failure_list['failure_name'] = error[0].attrib['type']
                    if 'message' in error[0].attrib:
                        failure_list['detail'] = error[0].attrib['message']
                    failure_list['is_error'] = True
                    failures.append(failure_list)
                    continue

                # skip
                skipTags = test_case.findall("skipped")
                if len(skipTags) > 0:
                    skipping_tests_count += 1
                    skipping_test_cases.add(class_name + '#' + method_name)
                    continue

                # pass
                passing_tests_count += 1
                passing_test_cases.add(class_name + '#' + method_name)

    repository = {'name': vul['project'], 'url': vul['project_url'], 'human_patch_url': vul['human_patch_url']}
    overall_metrics = {'number_running': passing_tests_count + error_tests_count + failing_tests_count,
                       'number_passing': passing_tests_count,
                       'number_error': error_tests_count,
                       'number_failing': failing_tests_count,
                       'number_skipping': skipping_tests_count}
    tests = {'overall_metrics': overall_metrics,
             'failures': failures,
             'passing_tests': list(passing_test_cases),
             'skipping_tests': list(skipping_test_cases)}

    json_data = {'vul_id': vul['vul_id'], 'cve_id': vul['cve_id'], 'repository': repository, 'tests': tests}
    return json_data
