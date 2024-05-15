import csv
import json
import os
import re
import shutil
import subprocess
import sys
from shutil import copytree, ignore_patterns

import git
from loguru import logger

import vul4j.utils as utils
from vul4j.config import JAVA7_HOME, MVN_ARGS, JAVA8_HOME, OUTPUT_DIR, LOG_TO_FILE, DATASET_PATH, VUL4J_COMMITS_URL, \
    TEMP_CLONE_DIR, VUL4J_GIT

VOID = open(os.devnull, 'w')


class Vul4J:

    def __init__(self):
        self.vulnerabilities = {}
        self._load_vulnerabilities()

    def _load_vulnerabilities(self):
        """
        Loads vulnerability information from the csv dataset into a dictionary of dictionaries.

        :return: dictionary of vulnerabilities
        """

        def get_column(column):
            return row[column].strip()

        with open(DATASET_PATH) as dataset_file:
            reader = csv.DictReader(dataset_file, delimiter=',')
            for row in reader:
                vul_id = get_column('vul_id')
                self.vulnerabilities[vul_id] = {
                    "vul_id": vul_id,
                    "cve_id": get_column("cve_id"),
                    "cwe_id": get_column("cwe_id"),
                    "project": get_column("repo_slug").replace("/", "_"),
                    "project_url": f"https://github.com/{get_column('repo_slug')}",
                    "build_system": get_column("build_system"),
                    "compliance_level": int(get_column("compliance_level")),
                    "compile_cmd": get_column("compile_cmd"),
                    "test_all_cmd": get_column("test_all_cmd"),
                    "test_cmd": get_column("test_cmd"),
                    "cmd_options": get_column("cmd_options"),
                    "failing_module": get_column('failing_module'),
                    "fixing_commit_hash": utils.get_commit_hash(get_column("human_patch")),
                    "human_patch_url": get_column("human_patch"),
                    "failing_tests": get_column("failing_tests").split(','),
                }

    # TODO needed?
    def get_info(self, vul_id):
        vul = self.vulnerabilities.get(vul_id)
        if vul is None:
            print("Vulnerability not found!")
            exit(1)

        print(json.dumps(vul, indent=4))
        exit(0)

    def get_vulnerability(self, vul_id):
        return self.vulnerabilities.get(vul_id)

    def checkout(self, vul_id: str, output_dir: str, clone: bool = False) -> None:
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
        :param output_dir:  where the checked out project will be copied
        :param clone:   non vul4j projects that need to be cloned instead of checked out from the vul4j git
        """
        vul = self.vulnerabilities.get(vul_id.upper())

        if vul is None:
            logger.error(f"No vulnerability found in the dataset with ID {vul_id}!")
            exit(1)

        if os.path.exists(output_dir):
            logger.error(f"Directory '{output_dir}' already exists!")
            exit(1)

        if clone:
            if not os.path.exists(TEMP_CLONE_DIR):
                os.makedirs(TEMP_CLONE_DIR)
            project_path = os.path.join(TEMP_CLONE_DIR, vul_id)
            logger.info(f"Cloning project into '{project_path}'")
            git.Repo.clone_from(vul['project_url'], project_path)
            repo = git.Repo(project_path)
            logger.info("Done cloning!")
            repo.git.checkout(vul['fixing_commit_hash'])
        else:
            repo = git.Repo(VUL4J_GIT)
            repo.git.reset("--hard")
            repo.git.checkout("--")
            repo.git.clean("-fdx")
            repo.git.checkout("-f", vul_id.upper())

        # copy to working directory
        copytree(TEMP_CLONE_DIR if clone else VUL4J_GIT, output_dir, ignore=ignore_patterns('.git'))

        # extract patched and vulnerable files
        # TODO does not work for non-vul4j projects, compare with parent
        utils.extract_patch_files(vul, output_dir)

        # write vulnerability info into file
        with open(os.path.join(output_dir, OUTPUT_DIR, "vulnerability_info.json"), "w", encoding='utf-8') as f:
            f.write(json.dumps(vul, indent=2))

        dest_repo = git.Repo.init(output_dir)
        with dest_repo.config_writer() as git_config:
            git_config.set_value("user", "name", "vul4j")
            git_config.set_value("user", "email", "vul4j@vul4j.org")
        dest_repo.git.add('-A')
        dest_repo.git.commit("-m", "vulnerable")

        original_stdout = sys.stdout
        sys.stdout = VOID
        self.apply(output_dir, 'human_patch')
        sys.stdout = original_stdout

        dest_repo.git.add('-A')
        dest_repo.git.commit("-m", "human_patch")
        dest_repo.git.checkout("HEAD~1")

        if not clone:
            # revert to main branch
            repo.git.reset("--hard")
            repo.git.checkout("--")
            repo.git.clean("-fdx")
            repo.git.checkout("-f", "main")
        else:
            # TODO remove cloned project
            pass

        logger.info("----- Checkout complete! -----")

    @staticmethod
    def compile(output_dir) -> None:
        """
        Compiles the project found in the provided directory.
        The project must contain a 'vulnerability_info.json' file.

        :param output_dir: path to the project to be compiled
        """
        vul = utils.read_vulnerability_from_output_dir(output_dir)

        if vul is None:
            raise FileNotFoundError("No vulnerability found!")

        java_home = JAVA7_HOME if vul['compliance_level'] <= 7 else JAVA8_HOME

        env = os.environ.copy()
        env["JAVA_HOME"] = java_home
        env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
        env["MAVEN_OPTS"] = MVN_ARGS

        # TODO check maven and gradle install on start
        env["PATH"] = env["PATH"] + ";" + os.path.normpath("D:/Downloads/apache-maven-3.9.6/bin")

        compile_cmd = vul['compile_cmd'] + " " + vul['cmd_options']

        log_path = os.path.join(output_dir, OUTPUT_DIR, "compile.log")
        log_output = open(log_path, "w", encoding="utf-8") if LOG_TO_FILE else VOID

        subprocess.run(compile_cmd,
                       shell=True,
                       stdout=log_output,
                       stderr=subprocess.STDOUT,
                       cwd=output_dir,
                       env=env,
                       check=True)

    @staticmethod
    def apply(output_dir, version, quiet=False):
        """
        Copies the selected file versions into their respective locations.
        The version folder must contain a paths.json file which describes the location of each file in the project.

        :param output_dir:  where the project is located
        :param version: the version to apply
        :param quiet:   displays warning message if False
        """
        vul = utils.read_vulnerability_from_output_dir(output_dir)

        if vul is None:
            raise FileExistsError("No vulnerability found!")

        if version == "human_patch" and not quiet:
            logger.warning(
                f"Please check {VUL4J_COMMITS_URL + vul['vul_id']} if build fails.")

        with open(os.path.join(output_dir, OUTPUT_DIR, version, "paths.json"), "r") as file:
            paths = json.load(file)

        for file, path in paths.items():
            shutil.copy(str(os.path.join(output_dir, OUTPUT_DIR, version, file)),
                        str(os.path.join(output_dir, "/".join(path.split("/")[:-1]))))

    @staticmethod
    def test(output_dir, batch_type) -> str:
        """
        Runs test cases in the project found in the provided directory.
        The project must contain a 'vulnerability_info.json' file in the vul4j directory defined in the config.

        Batch type defines which tests to run.
        If 'all' is provided, all available test will be run.
        If 'povs' is provided, only the vulnerable tests will be run.

        :param output_dir: path to the project to be compiled
        :param batch_type: 'all' for all tests, 'povs' for vulnerable tests only
        """
        vul = utils.read_vulnerability_from_output_dir(output_dir)
        utils.remove_test_results(output_dir)

        java_home = JAVA7_HOME if vul["compliance_level"] <= 7 else JAVA8_HOME
        cmd_type = "test_all_cmd" if batch_type == "all" else "test_cmd"

        env = os.environ.copy()
        env["JAVA_HOME"] = java_home
        env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
        env["MAVEN_OPTS"] = MVN_ARGS

        # TODO check maven and gradle install on start
        env["PATH"] = env["PATH"] + ";" + os.path.normpath("D:/Downloads/apache-maven-3.9.6/bin")

        test_cmd = vul[cmd_type] + " " + vul['cmd_options']

        log_path = os.path.join(output_dir, OUTPUT_DIR, "testing.log")
        log_output = open(log_path, "w", encoding="utf-8") if LOG_TO_FILE else VOID

        subprocess.run(test_cmd,
                       shell=True,
                       stdout=log_output,
                       stderr=subprocess.STDOUT,
                       cwd=output_dir,
                       env=env)

        test_results = utils.read_test_results(vul, output_dir)

        with (open(os.path.join(output_dir, OUTPUT_DIR, "testing_results.json"), "w")) as f:
            json.dump(test_results, f, indent=2)

        test_stats = test_results["tests"]["overall_metrics"]

        logger.info("---------------------------------------------------------")
        logger.log(("CRITICAL" if test_stats["number_running"] == 0 else "INFO"),
                   "Number of running tests: " + str(test_stats["number_running"]))
        if test_stats["number_passing"] != 0:
            logger.success("Number of passing tests: " + str(test_stats["number_passing"]))
        if test_stats["number_skipping"] != 0:
            logger.warning("Number of skipping tests: " + str(test_stats["number_skipping"]))
        if test_stats["number_failing"] != 0:
            logger.error("Number of failing tests: " + str(test_stats["number_failing"]))
        if test_stats["number_error"] != 0:
            logger.critical("Number of errors: " + str(test_stats["number_error"]))
        logger.info("---------------------------------------------------------")

        return json.dumps(test_results, indent=2)

    def classpath(self, output_dir, print_out=True):
        """

        :param output_dir:
        :param print_out:
        :return:
        """
        cp = self.get_classpath(output_dir)
        if print_out:
            logger.info(cp)
        exit(0)

    # TODO FIX
    @staticmethod
    def get_classpath(output_dir):
        """
        modify from https://github.com/program-repair/RepairThemAll/blob/master/script/info_json_file.py

        :param output_dir:
        :return:
        """
        vul = utils.read_vulnerability_from_output_dir(output_dir)

        # TODO check vul

        '''
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
        '''
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
                                 " It is now %s." % test_all_cmd)
                    exit(1)

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
        env["JAVA_HOME"] = java_home
        env["JAVA_OPTIONS"] = "-Djdk.net.URLClassPath.disableClassPathURLCheck=true"
        env["MAVEN_OPTS"] = MVN_ARGS

        subprocess.run(cp_cmd[0],
                       shell=True,
                       stdout=subprocess.STDOUT,
                       stderr=subprocess.STDOUT,
                       env=env,
                       cwd=output_dir,
                       check=True)

        classpath = subprocess.check_output(cp_cmd[1],
                                            shell=True,
                                            cwd=output_dir,
                                            env=env)
        return classpath
