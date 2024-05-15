import os
import sys
import glob
import json
import subprocess
import xml.etree.ElementTree as ET
from vul4j.config import OUTPUT_FOLDER_NAME, SPOTBUGS_PATH, METHOD_GETTER_PATH, ENABLE_EXECUTING_LOGS

FNULL = open(os.devnull, 'w')
original_stdout = sys.stdout


def run_spotbugs(output_dir: str, artifacts: dict, vul: dict, version=None):
    # create spotbugs directory
    reports_dir = os.path.join(os.path.join(output_dir, OUTPUT_FOLDER_NAME, "spotbugs"))
    if not os.path.exists(reports_dir):
        os.mkdir(reports_dir)

    # get module path where compiled jars are located
    failing_module = vul["failing_module"]
    if failing_module == "root":
        module_path = output_dir
    else:
        module_path = os.path.join(output_dir, failing_module)

    generated_files = get_generated_files(module_path, artifacts)

    # find modified methods and their classes
    method_getter_output = os.path.join(reports_dir, 'modifications.json')
    method_getter_command = f"java -jar {METHOD_GETTER_PATH} {output_dir} {method_getter_output}"
    method_getter_log_path = os.path.join(reports_dir, "modifications.log")
    log_to_file = open(method_getter_log_path, "w", encoding="utf-8") if ENABLE_EXECUTING_LOGS == "1" else FNULL
    res = subprocess.call(method_getter_command, shell=True, stdout=FNULL, stderr=log_to_file)

    if res != 0 or not os.path.exists(method_getter_output):
        print("Method getter failed!")
        return res

    # get actual compiled jar path
    jar_path = os.path.join(module_path, 'target',
                            [file for file in generated_files['module_build_file_list'] if 'SNAPSHOT.jar' in file][0])

    # run spotbugs
    spotbugs_output = os.path.join(reports_dir, "spotbugs_report.xml")
    spotbugs_command = f"java -jar {SPOTBUGS_PATH} -textui -low -xml={spotbugs_output} {jar_path}"
    spotbugs_log_path = os.path.join(reports_dir, "spotbugs.log")
    log_to_file = open(spotbugs_log_path, "w", encoding="utf-8") if ENABLE_EXECUTING_LOGS == "1" else FNULL
    res = subprocess.call(spotbugs_command, shell=True, stdout=FNULL, stderr=log_to_file)

    if res != 0 or not os.path.exists(spotbugs_output):
        print("Spotbugs failed!")
        return res

        # find warnings in modified methods
    warnings = {}

    with open(method_getter_output, 'r') as file:
        modifications = json.load(file)

    tree = ET.parse(spotbugs_output)
    root = tree.getroot()
    for java_class, java_methods in modifications.items():
        for java_method in java_methods:
            warnings[java_method] = set()
            for bug_instance in root.findall('.//BugInstance'):
                method = bug_instance.find('.//Method')
                if method and method.attrib['classname'] == java_class and method.attrib['name'] == java_method:
                    warnings[java_method].add(bug_instance.attrib['type'])

    warnings_output = os.path.join(reports_dir, 'warnings.json' if version is None else f'warnings_{version}.json')

    with open(warnings_output, 'w') as file:
        json.dump({key: list(value) for key, value in warnings.items()}, file, indent=2)

    return res


def restore_pom(output_dir: str):
    restore_pom_command = f"cd {output_dir}; git checkout -- pom.xml"
    res = subprocess.call(restore_pom_command, shell=True, stdout=FNULL, stderr=subprocess.STDOUT)

    if res != 0:
        print("Failed to revert pom.xml changes")
        return res


def get_generated_files(module_path, file_name):
    """
    Search for the generated files we need
    :param module_path: The module path
    :param file_name: Name of file
    """
    targetDirs = []
    jar_war_ear_zips_in_all_target = []
    for root, dirs, files in os.walk(module_path):
        for dir in dirs:
            if dir.split("/")[-1] == "target":
                jar_war_ear_zips = (
                        glob.glob(root + "/target/*.jar")
                        + glob.glob(root + "/target/*.war")
                        + glob.glob(root + "/target/*.ear")
                        + glob.glob(root + "/target/*.zip")
                )
                if len(jar_war_ear_zips) != 0:
                    targetDirs.append(root + "/target")
                    for jwez in jar_war_ear_zips:
                        jar_war_ear_zips_in_all_target.append(jwez)

    if len(targetDirs) == 0:
        raise Exception("Missing target folder!")

    if len(targetDirs) == 1 and targetDirs[0] == module_path + "/target":
        jar_war_ear_zips = (
                glob.glob(module_path + os.path.sep + "target/*.jar")
                + glob.glob(module_path + "/target/*.war")
                + glob.glob(module_path + "/target/*.ear")
                + glob.glob(module_path + "/target/*.zip")
        )
        print(f"Generated files in {module_path}: \n{jar_war_ear_zips}", "")

        artifact_id = file_name["artifactId"]
        version = file_name["version"]
        fileList = []
        jar_war_ear_zip_name_without_version = artifact_id
        jar_war_ear_zip_name_with_version = artifact_id + "-" + version
        for filePath in jar_war_ear_zips:
            fileList.append(str(filePath).split(os.path.sep)[-1])
        for jar_war_ear_zip in jar_war_ear_zips:
            build_file_name = str(jar_war_ear_zip).split(
                os.path.sep)[-1].rsplit(".", 1)[0]
            if str(build_file_name) == str(jar_war_ear_zip_name_with_version):
                return {
                    "default_build_file_path": jar_war_ear_zip,
                    "module_build_file_list": fileList,
                }
            if str(build_file_name) == str(jar_war_ear_zip_name_without_version):
                return {
                    "default_build_file_path": jar_war_ear_zip,
                    "module_build_file_list": fileList,
                }
        return {"default_build_file_path": "", "module_build_file_list": fileList}


def edit_pom(pom_path, java_version):
    """
    Editing pom if it is necessary
    :param pom_path: The path where pom.xml file is
    """
    namespace = ""
    parser = ET.XMLParser(target=ET.TreeBuilder(insert_comments=True))
    tree = ET.parse(pom_path, parser)
    root = tree.getroot()
    ns = root.tag.split("}")[0].strip("{")
    if ns != "project":
        ET.register_namespace("", ns)
        namespace = "{" + ns + "}"

    artifact_id = ""
    version = ""
    artifactId = root.find(namespace + "artifactId")
    if artifactId is not None:
        artifact_id = artifactId.text
    module_version = root.find(namespace + "version")
    if module_version is not None:
        version = module_version.text

    no_version = True

    # build = root.find(namespace + "build")
    # if build is None:
    #     pass
    # else:
    #     print("-----CHECKING BUILD CONFIGURATION-----", "")
    #     edit_build_configuration(build, namespace, no_version)

    properties = root.find(namespace + "properties")
    if (properties is None) & no_version:
        child = ET.Element(namespace + "properties")
        root.append(child)
        properties = root.find(namespace + "properties")

    if properties is not None:
        print("-----CHECKING PROPERTIES-----", "")
        no_version = edit_properties(properties, namespace)

    tree.write(open(pom_path, "w"), encoding="unicode")

    print("Pom edited", "")
    return {"artifactId": artifact_id, "version": version}


def edit_build_configuration(build, namespace, version=None):
    """
    Editing build configuration if it is necessary
    :param build: The build tag
    :param namespace: The namespace
    :param no_version: java version
    """
    plugins = build.find(namespace + "plugins")
    if plugins is None:
        print("-----PLUGINS ARE MISSING FROM BUILD----", "")
    else:
        for plugin in plugins:
            executions = plugin.find(namespace + "executions")
            if executions is not None:
                for execution in executions:
                    phase = execution.find(namespace + "phase")
                    if phase is not None and phase.text == "never":
                        plugins.remove(plugin)
            configuration = plugin.find(namespace + "configuration")
            if configuration is not None:
                print("-----CONFIGURATION FOUND WITHIN BUILD----", "")
                source = configuration.find(namespace + "source")
                if source is None:
                    print(
                        "-----NO SOURCE ELEMENT FOUND IN CONFIGURATION-----", "")
                else:
                    no_version = False
                    print(
                        "-----SOURCE ELEMENT FOUND IN CONFIGURATION-----", "")
                    try:
                        if float(source.text) <= float(version):
                            source.text = version
                    except:
                        source.text = version

                target = configuration.find(namespace + "target")
                if target is None:
                    print(
                        "-----NO TARGET ELEMENT FOUND IN CONFIGURATION-----", "")
                else:
                    no_version = False
                    print(
                        "-----SOURCE ELEMENT FOUND IN CONFIGURATION-----", "")
                    try:
                        if float(target.text) <= float(version):
                            target.text = version
                    except:
                        target.text = version
    return no_version


def edit_properties(properties, namespace):
    """
    Editing properties if it is necessary
    :param properties: The properties tag
    :param namespace: The namespace
    """
    # source_element = properties.find(namespace + "maven.compiler.source")
    # if source_element is None:
    #     source_element = ET.Element("maven.compiler.source")
    #     source_element.text = PREFERRED_JAVA_VERSION
    #     properties.insert(0, source_element)
    #     print("-----SOURCE ELEMENT IS MISSING FROM POM, ADDING 1.8----", "")
    # else:
    #     print("-----SOURCE ELEMENT FOUND IN POM-----", "")

    # target_element = properties.find(namespace + "maven.compiler.target")
    # if target_element is None:
    #     target_element = ET.Element("maven.compiler.target")
    #     target_element.text = PREFERRED_JAVA_VERSION
    #     properties.insert(0, target_element)
    #     print("-----TARGET ELEMENT IS MISSING FROM POM, ADDING 1.8----", "")
    # else:
    #     print("-----TARGET ELEMENT FOUND IN POM----", "")

    encoding_element = properties.find(
        namespace + "project.build.sourceEncoding")
    if encoding_element is None:
        encoding_element = ET.Element("project.build.sourceEncoding")
        encoding_element.text = "UTF-8"
        properties.insert(0, encoding_element)
        print("-----ENCODING ELEMENT IS MISSING FROM POM, ADDING UTF-8----", "")
    else:
        print("-----ENCODING FOUND IN POM----", "")
