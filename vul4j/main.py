import argparse
import csv
import json
import logging
import os
import re
import subprocess
import sys
from shutil import copytree, ignore_patterns
from xml.etree.ElementTree import parse

from unidiff import PatchSet

from vul4j.config import JAVA7_HOME, MVN_OPTS, JAVA8_HOME, OUTPUT_FOLDER_NAME, ENABLE_EXECUTING_LOGS, DATASET_PATH, \
    BENCHMARK_PATH, PROJECT_REPOS_ROOT_PATH

FNULL = open(os.devnull, 'w')
root = logging.getLogger()
root.setLevel(logging.DEBUG)

handler = logging.StreamHandler(sys.stdout)
handler.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
root.addHandler(handler)


class Vul4J:
    def __init__(self):
        self.vulnerabilities = []
        self.load_vulnerabilities()

    def load_vulnerabilities(self):
        # get all the vulnerabilities of the benchmark
        with open(os.path.join(DATASET_PATH)) as f:
            reader = csv.DictReader(f, delimiter=',', )
            for row in reader:
                vul_id = row['vul_id'].strip()
                cve_id = row['cve_id'].strip()
                repo_slug = row['repo_slug'].strip().replace("/", "_")
                build_system = row['build_system'].strip()
                compliance_level = int(row['compliance_level'].strip())
                compile_cmd = row['compile_cmd'].strip()
                test_all_cmd = row['test_all_cmd'].strip()
                cmd_options = row['cmd_options'].strip()
                failing_module = row['failing_module'].strip()
                src_classes_dir = row['src_classes'].strip()
                test_classes_dir = row['test_classes'].strip()
                human_patch_url = row['human_patch'].strip()
                fixing_commit = human_patch_url[human_patch_url.rfind('/') + 1:]

                if failing_module != "root" and failing_module != "":
                    src_classes_dir = failing_module + '/' + src_classes_dir
                    test_classes_dir = failing_module + '/' + test_classes_dir

                self.vulnerabilities += [{
                    "vul_id": vul_id,
                    "cve_id": cve_id,
                    "project": repo_slug,
                    "project_url": "https://github.com/%s" % (repo_slug.replace("_", "/")),
                    "build_system": build_system,
                    "compliance_level": compliance_level,
                    "compile_cmd": compile_cmd,
                    "test_all_cmd": test_all_cmd,
                    "cmd_options": cmd_options,
                    "failing_module": failing_module,
                    "src_classes_dir": src_classes_dir,
                    "test_classes_dir": test_classes_dir,
                    "human_patch_url": human_patch_url,
                    "human_patch": [],
                    "fixing_commit_hash": fixing_commit,
                }]
        return self.vulnerabilities

    def get_vulnerability(self, vul_id):
        for vul in self.vulnerabilities:
            if vul_id.lower() == vul['vul_id'].lower():
                return vul
        return None

    def get_info(self, vul_id):
        vul = self.get_vulnerability(vul_id)
        if vul is None:
            print("Vulnerability not found!")
            exit(1)

        print(json.dumps(vul, indent=4))
        exit(0)

    @staticmethod
    def get_patch(vul):
        cmd = "cd " + vul['project_repo_folder'] + "; git diff %s %s~1" % (
            vul['fixing_commit_hash'], vul['fixing_commit_hash'])
        diff = subprocess.check_output(cmd, shell=True)
        patch = PatchSet(diff.decode('utf-8'))

        changed_java_source_files = []
        for a_file in patch.added_files:
            file_path = a_file.path
            if file_path.endswith('.java') and ('test/' not in file_path and 'tests/' not in file_path):
                changed_java_source_files.append(file_path)

        for m_file in patch.modified_files:
            file_path = m_file.path
            if file_path.endswith('.java') and ('test/' not in file_path and 'tests/' not in file_path):
                changed_java_source_files.append(file_path)

        patch_data = []
        revert_patch_data = []
        for file in changed_java_source_files:
            cmd = "cd " + vul['project_repo_folder'] + "; git show %s:%s" % (vul['fixing_commit_hash'], file)
            content = subprocess.check_output(cmd, shell=True).decode('utf-8')
            patch_data.append({'file_path': file, 'content': content})

            cmd = "cd " + vul['project_repo_folder'] + "; git show %s~1:%s" % (vul['fixing_commit_hash'], file)
            content = subprocess.check_output(cmd, shell=True).decode('utf-8')
            revert_patch_data.append({'file_path': file, 'content': content})
        return patch_data, revert_patch_data

    def checkout(self, vul_id, output_dir):
        vul = self.get_vulnerability(vul_id)

        if vul is None:
            print("No vulnerability found in the dataset with id %s!" % vul_id)
            exit(1)

        if os.path.exists(output_dir):
            print("Directory '%s' has already existed!" % output_dir)
            exit(1)

        cmd = "cd %s; git reset .; git checkout -- .; git clean -x -d --force; git checkout -f %s" % (
            BENCHMARK_PATH, vul["vul_id"])
        ret = subprocess.call(cmd, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)
        if ret != 0:
            exit(1)

        # copy to working directory
        copytree(BENCHMARK_PATH, output_dir, ignore=ignore_patterns('.git'))
        os.makedirs(os.path.join(output_dir, OUTPUT_FOLDER_NAME))
        with open(os.path.join(output_dir, OUTPUT_FOLDER_NAME, "vulnerability_info.json"), "w", encoding='utf-8') as f:
            f.write(json.dumps(vul, indent=2))

        # revert to main branch
        cmd = "cd %s; git reset .; git checkout -- .; git clean -x -d --force; git checkout -f main" % BENCHMARK_PATH
        subprocess.call(cmd, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)

        return 0

    '''
    This checkout() function is used for the reproduction of new vulnerabilities 
    '''

    def checkout_reproduce(self, vul_id, output_dir):
        vul = self.get_vulnerability(vul_id)
        if os.path.exists(output_dir):
            logging.error("Directory '%s' has already existed!" % output_dir)
            exit(1)

        if not os.path.exists(PROJECT_REPOS_ROOT_PATH):
            os.makedirs(PROJECT_REPOS_ROOT_PATH)

        project_repo = os.path.join(PROJECT_REPOS_ROOT_PATH, vul['project'])
        if not os.path.exists(project_repo):
            logging.debug("Cloning new project... " + vul['project'])
            subprocess.call("git clone %s %s" % (vul['project_url'], os.path.abspath(project_repo)), shell=True,
                            stdout=FNULL, stderr=subprocess.STDOUT)
            logging.debug("Done Cloning!")

        cmd = "cd %s; git reset .; git checkout -- .; git clean -x -d --force; git checkout -f %s" % (
            project_repo, vul["fixing_commit_hash"])
        ret = subprocess.call(cmd, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)
        if ret != 0:
            exit(1)

        # extract the original patch
        # some vulns with customized-patch will fail here
        vul['project_repo_folder'] = os.path.abspath(project_repo)
        patch_data, revert_patch_data = self.get_patch(vul)
        vul['human_patch'] = patch_data
        vul['revert_human_patch'] = revert_patch_data

        # copy to working directory
        copytree(project_repo, output_dir, ignore=ignore_patterns('.git'))
        os.makedirs(os.path.join(output_dir, OUTPUT_FOLDER_NAME))
        with open(os.path.join(output_dir, OUTPUT_FOLDER_NAME, "vulnerability_info.json"), "w", encoding='utf-8') as f:
            f.write(json.dumps(vul, indent=2))

        # revert to main branch
        cmd = "cd %s; git reset .; git checkout -- .; git clean -x -d --force; git checkout -f main" % BENCHMARK_PATH
        subprocess.call(cmd, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)

        return 0

    @staticmethod
    def read_vulnerability_from_output_dir(output_dir):
        info_file = os.path.join(output_dir, OUTPUT_FOLDER_NAME, "vulnerability_info.json")
        try:
            vul = json.load(open(info_file))
            return vul
        except IOError:
            logging.error("Not found the info file of vulnerability: '%s'" % info_file)
            exit(1)

    def compile(self, output_dir):
        vul = self.read_vulnerability_from_output_dir(output_dir)

        java_home = JAVA7_HOME if vul['compliance_level'] <= 7 else JAVA8_HOME

        cmd = """cd %s;
export JAVA_HOME="%s";
export _JAVA_OPTIONS=-Djdk.net.URLClassPath.disableClassPathURLCheck=true;
export MAVEN_OPTS="%s";
%s;""" % (output_dir, java_home, MVN_OPTS, vul['compile_cmd'])

        cmd_options = vul['cmd_options']
        if cmd_options:
            cmd = cmd[:-1]  # remove comma
            cmd += " " + cmd_options + ';'

        log_path = os.path.join(output_dir, OUTPUT_FOLDER_NAME, "compile.log")
        stdout = open(log_path, "w", encoding="utf-8") if ENABLE_EXECUTING_LOGS == "1" else FNULL
        ret = subprocess.call(cmd, shell=True, stdout=stdout, stderr=subprocess.STDOUT)
        with (open(os.path.join(output_dir, OUTPUT_FOLDER_NAME, "compile_result.txt"), "w")) as f:
            f.write("1" if ret == 0 else "0")
        return ret

    def test(self, output_dir):
        vul = self.read_vulnerability_from_output_dir(output_dir)

        java_home = JAVA7_HOME if vul['compliance_level'] <= 7 else JAVA8_HOME

        cmd = """cd %s;
export JAVA_HOME="%s";
export _JAVA_OPTIONS=-Djdk.net.URLClassPath.disableClassPathURLCheck=true;
export MAVEN_OPTS="%s";
%s;""" % (output_dir, java_home, MVN_OPTS, vul['test_all_cmd'])

        cmd_options = vul['cmd_options']
        if cmd_options:
            cmd = cmd[:-1]  # remove comma
            cmd += " " + cmd_options + ';'

        log_path = os.path.join(output_dir, OUTPUT_FOLDER_NAME, "testing.log")
        stdout = open(log_path, "w", encoding="utf-8") if ENABLE_EXECUTING_LOGS == "1" else FNULL
        subprocess.call(cmd, shell=True, stdout=stdout, stderr=subprocess.STDOUT)

        test_results = self.read_test_results_maven(vul, output_dir) \
            if vul['build_system'] == "Maven" \
            else self.read_test_results_gradle(vul, output_dir)

        json_str = json.dumps(test_results, indent=2)
        print(json_str)
        with (open(os.path.join(output_dir, OUTPUT_FOLDER_NAME, "testing_results.json"), "w")) as f:
            f.write(json_str)
        return 0

    def classpath(self, output_dir):
        print(self.get_classpath(output_dir))
        return 0

    def get_classpath(self, output_dir):
        vul = self.read_vulnerability_from_output_dir(output_dir)

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
        cp_cmd = ""
        if vul['build_system'] == "Gradle":
            test_all_cmd = vul['test_all_cmd']

            matched = re.search("(./gradlew :.*:)test$", test_all_cmd)
            if matched is None:
                print("The test all command should follow the regex \"(./gradlew :.*:)test$\"!"
                      " It is now %s." % test_all_cmd)
                exit(1)

            gradle_classpath_cmd = matched.group(1) + "copyClasspath"
            classpath_info_file = os.path.join(vul['failing_module'], 'classpath.info')
            cat_classpath_info_cmd = "cat " + classpath_info_file
            cp_cmd = "%s;%s" % (gradle_classpath_cmd, cat_classpath_info_cmd)

        elif vul['build_system'] == "Maven":
            failing_module = vul['failing_module']
            if failing_module != "root" and failing_module != "":
                cp_cmd = "mvn dependency:build-classpath -Dmdep.outputFile='classpath.info' -pl %s; cat %s/classpath.info" \
                         % (failing_module, failing_module)
            else:
                cp_cmd = "mvn dependency:build-classpath -Dmdep.outputFile='classpath.info'; cat classpath.info"

        else:
            print("Not support for %s" % vul['vul_id'])
            exit(1)

        java_home = JAVA7_HOME if vul['compliance_level'] <= 7 else JAVA8_HOME

        cmd = """cd %s;
        export JAVA_HOME="%s";
        export _JAVA_OPTIONS=-Djdk.net.URLClassPath.disableClassPathURLCheck=true;
        export MAVEN_OPTS="%s";
        %s;""" % (output_dir, java_home, MVN_OPTS, cp_cmd.split(';')[0])

        subprocess.call(cmd, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)

        classpath = subprocess.check_output(
            "cd %s;%s;" % (output_dir, cp_cmd.split(';')[1]), shell=True)
        return classpath

    '''
    modify from https://github.com/program-repair/RepairThemAll/blob/master/script/info_json_file.py
    '''

    @staticmethod
    def read_test_results_maven(vul, project_dir):
        surefire_report_files = []
        for r, dirs, files in os.walk(project_dir):
            for file in files:
                filePath = os.path.join(r, file)
                if ("target/surefire-reports" in filePath or "target/failsafe-reports" in filePath) and file.endswith(
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

    @staticmethod
    def read_test_results_gradle(vul, project_dir):
        surefire_report_files = []
        for r, dirs, files in os.walk(project_dir):
            for file in files:
                filePath = os.path.join(r, file)
                if "build/test-results" in filePath and file.endswith('.xml') and file.startswith('TEST-'):
                    surefire_report_files.append(filePath)

        failing_tests_count = 0
        error_tests_count = 0
        passing_tests_count = 0
        skipping_tests_count = 0
        failures = []

        passingTestCases = set()
        skippingTestCases = set()

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

        repository = {'name': vul['project'], 'url': vul['project_url'], 'human_patch': vul['human_patch']}
        overall_metrics = {'number_running': passing_tests_count + error_tests_count + failing_tests_count,
                           'number_passing': passing_tests_count, 'number_error': error_tests_count,
                           'number_failing': failing_tests_count, 'number_skipping': skipping_tests_count}
        tests = {'overall_metrics': overall_metrics, 'failures': failures, 'passing_tests': list(passingTestCases),
                 'skipping_tests': list(skippingTestCases)}

        json_data = {'vul_id': vul['vul_id'], 'cve_id': vul['cve_id'], 'repository': repository, 'tests': tests}
        return json_data


def main_checkout(args):
    vul4j = Vul4J()
    ret = vul4j.checkout(args.id, args.outdir)
    if ret != 0:
        print("Checkout failed!")
    exit(ret)


def main_compile(args):
    vul4j = Vul4J()
    ret = vul4j.compile(args.outdir)
    if ret != 0:
        print("Compile failed!")
    exit(ret)


def main_test(args):
    vul4j = Vul4J()
    ret = vul4j.test(args.outdir)
    exit(ret)


def main_classpath(args):
    vul4j = Vul4J()
    ret = vul4j.classpath(args.outdir)
    exit(ret)


def main_info(args):
    vul4j = Vul4J()
    vul4j.get_info(args.id)


def main(args=None):
    if args is None:
        args = sys.argv[1:]

    parser = argparse.ArgumentParser(prog="vul4j", description="A Benchmark of Java vulnerabilities.")

    sub_parsers = parser.add_subparsers(help="Checkout a vulnerability in the benchmark.")

    checkout_parser = sub_parsers.add_parser('checkout')
    checkout_parser.set_defaults(func=main_checkout)
    checkout_parser.add_argument("-i", "--id", help="Vulnerability Id.", required=True)
    checkout_parser.add_argument("-d", "--outdir", help="The destination directory.", required=True)

    compile_parser = sub_parsers.add_parser('compile')
    compile_parser.set_defaults(func=main_compile)
    compile_parser.add_argument("-i", "--id", help="Vulnerability Id.", required=False)
    compile_parser.add_argument("-d", "--outdir", help="The directory to which the vulnerability was checked out.",
                                required=True)

    test_parser = sub_parsers.add_parser('test')
    test_parser.set_defaults(func=main_test)
    test_parser.add_argument("-i", "--id", help="Vulnerability Id.", required=False)
    test_parser.add_argument("-d", "--outdir", help="The directory to which the vulnerability was checked out.",
                             required=True)

    cp_parser = sub_parsers.add_parser('classpath')
    cp_parser.set_defaults(func=main_classpath)
    cp_parser.add_argument("-i", "--id", help="Vulnerability Id.", required=False)
    cp_parser.add_argument("-d", "--outdir", help="The directory to which the vulnerability was checked out.",
                           required=True)

    info_parser = sub_parsers.add_parser('info')
    info_parser.set_defaults(func=main_info)
    info_parser.add_argument("-i", "--id", help="Vulnerability Id.", required=True)

    options = parser.parse_args(args)
    if not hasattr(options, 'func'):
        parser.print_help()
        exit(1)
    options.func(options)
    return options


if __name__ == "__main__":
    main()
