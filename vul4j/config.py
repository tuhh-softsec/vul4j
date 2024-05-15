import configparser
import os

config = configparser.ConfigParser()
config.read(os.environ.get("VUL4J_CONFIG", os.path.expanduser("~/vul4j.ini")))


def get_config(section: str, config_name: str, default=None):
    value = config.get(section, config_name)
    if value is None or value == "":
        value = os.environ.get(config_name)

    return value if value else default


def config_check():
    missing_values = []
    for section in config.sections():
        for option in config.options(section):
            value = config.get(section, option)
            if value == '':
                value = os.getenv(option.upper())
            if not value:
                missing_values.append(option)

    return missing_values


# VUl4J
VUL4J_GIT = get_config("VUL4J", "VUL4J_GIT")
VUL4J_COMMITS_URL = get_config("VUL4J", "VUL4J_COMMITS_URL")
DATASET_PATH = get_config("VUL4J", "DATASET_PATH",
                          os.path.join(VUL4J_GIT, "dataset", "vul4j_dataset.csv"))
LOG_TO_FILE = get_config("VUL4J", "LOG_TO_FILE") == "1"

# DIRS
OUTPUT_DIR = get_config("DIRS", "OUTPUT_DIR")
REPRODUCTION_DIR = get_config("DIRS", "REPRODUCTION_DIR",
                              os.path.expanduser("~/vul4j_reproduction"))
TEMP_CLONE_DIR = get_config("DIRS", "TEMP_CLONE_DIR",
                            os.path.expanduser("~/vul4j_temp_clone_dir"))

# JAVA
JAVA_ARGS = get_config("JAVA", "JAVA_ARGS")
MVN_ARGS = get_config("JAVA", "MVN_ARGS")
JAVA7_HOME = get_config("JAVA", "JAVA7_HOME")
JAVA8_HOME = get_config("JAVA", "JAVA8_HOME")

# SPOTBUGS
SPOTBUGS_PATH = get_config("SPOTBUGS", "SPOTBUGS_PATH",
                           os.path.expanduser("~/spotbugs/spotbugs-4.8.5/lib/spotbugs.jar"))
METHOD_GETTER_PATH = get_config("SPOTBUGS", "METHOD_GETTER_PATH",
                                os.path.join(VUL4J_GIT, "method_getter", "method-getter.jar"))
