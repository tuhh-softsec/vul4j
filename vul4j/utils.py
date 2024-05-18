import functools
import os
import subprocess
import urllib.request
import zipfile

from loguru import logger

from vul4j.config import VUL4J_GIT, JAVA7_HOME, JAVA8_HOME, SPOTBUGS_PATH, \
    METHOD_GETTER_PATH, DATASET_PATH, SPOTBUGS_VERSION

SEPARATOR = 60 * "-"
THICK_SEPARATOR = 60 * "="


def suffix_filename(filename: str, suffix: str):
    """
    puts version in the filename if needed
    """
    name_list = filename.split(".")
    return f"{name_list[0]}_{suffix}.{name_list[1]}" if suffix else ".".join(name_list)


def log_frame(title: str):
    def decorator_log_frame(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            start = f" START {title} "
            logger.info(start.center(60, "="))
            func(*args, **kwargs)
            end = f" END {title} "
            logger.info(end.center(60, "="))

        return wrapper

    return decorator_log_frame


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
        java7 = "1.7" in str(subprocess.run("java -version",
                                            shell=True,
                                            stdout=subprocess.PIPE,
                                            stderr=subprocess.STDOUT,
                                            env=env))

    java8 = False
    if JAVA8_HOME:
        env["PATH"] = os.path.join(JAVA8_HOME, "bin") + os.pathsep + env["PATH"]
        java8 = "1.8" in str(subprocess.run("java -version",
                                            shell=True,
                                            stdout=subprocess.PIPE,
                                            stderr=subprocess.STDOUT,
                                            env=env))

    # check maven
    maven = subprocess.run("mvn --version",
                           shell=True,
                           stdout=subprocess.DEVNULL,
                           stderr=subprocess.DEVNULL).returncode == 0

    # check spotbugs
    spotbugs = (bool(SPOTBUGS_PATH) and
                subprocess.run(f"java -jar {SPOTBUGS_PATH} -version",
                               shell=True,
                               stdout=subprocess.DEVNULL,
                               stderr=subprocess.DEVNULL).returncode == 0)

    # check method getter
    method_getter = (bool(METHOD_GETTER_PATH) and
                     subprocess.run(f"java -jar {METHOD_GETTER_PATH} -version",
                                    shell=True,
                                    stdout=subprocess.DEVNULL,
                                    stderr=subprocess.DEVNULL).returncode == 0)

    def log_result(message: str, success: bool):
        logger.log("SUCCESS" if success else "ERROR", f"{message}: {'OK' if success else 'NOT FOUND'}")

    log_result("VUL4J config file", vul4j_config)
    log_result("VUL4J git repository", vul4j_git)
    log_result("VUL4J dataset", vul4j_dataset)
    log_result("Java 7", java7)
    log_result("Java 8", java8)
    log_result("Maven", maven)
    log_result("Spotbugs", spotbugs)
    log_result("Spotbugs method getter", method_getter)


def get_spotbugs():
    """
    Downloads Spotbugs. The version can be specified in the config file. Default is 4.8.5.
    """
    zip_file_path = os.path.expanduser(f"~/spotbugs-{SPOTBUGS_VERSION}.zip")
    urllib.request.urlretrieve(
        f"https://github.com/spotbugs/spotbugs/releases/download/{SPOTBUGS_VERSION}/spotbugs-{SPOTBUGS_VERSION}.zip",
        zip_file_path)

    with zipfile.ZipFile(zip_file_path, 'r') as zip_ref:
        zip_ref.extractall(os.path.expanduser("~"))

    # delete the zip file
    os.remove(zip_file_path)


def clean_build(project_dir: str, build_system: str) -> None:
    """
    Removes build leftovers.

    :param project_dir: path to the project to be cleaned
    :param build_system: maven or gradle
    """

    if build_system == "Maven":
        cmd = "mvn clean"
    elif build_system == "Gradle":
        cmd = "./gradlew clean"
    else:
        logger.error(f"Cleaning not implemented for {build_system}")
        return

    logger.debug(cmd)
    logger.info("Cleaning project...")
    try:
        subprocess.run(cmd,
                       shell=True,
                       stdout=subprocess.DEVNULL,
                       stderr=subprocess.DEVNULL,
                       cwd=project_dir,
                       check=True)
    except subprocess.CalledProcessError:
        logger.error("Clean failed!")
