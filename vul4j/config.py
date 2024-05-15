import configparser
import os

config = configparser.ConfigParser()
config.read(os.environ.get("VUL4J_CONFIG", os.path.expanduser("~/vul4j.ini")))


def get_config(section: str, config_name: str):
    value = config.get(section, config_name)
    return value if (value is not None and value != "") else os.environ.get(config_name)


# VUl4J
VUL4J_ROOT = get_config("VUL4J", "VUL4J_ROOT")
VUL4J_COMMITS_URL = get_config("VUL4J", "VUL4J_COMMITS_URL")
DATASET_PATH = get_config("VUL4J", "CUSTOM_DATASET_PATH")
LOG_TO_FILE = get_config("VUL4J", "LOG_TO_FILE") == "1"

# DIRS
OUTPUT_DIR = get_config("DIRS", "OUTPUT_DIR")
REPRODUCTION_DIR = get_config("DIRS", "REPRODUCTION_DIR")
TEMP_CLONE_DIR = get_config("DIRS", "TEMP_CLONE_DIR")

# JAVA
JAVA_ARGS = get_config("JAVA", "JAVA_ARGS")
MVN_ARGS = get_config("JAVA", "MVN_ARGS")
JAVA7_HOME = get_config("JAVA", "JAVA7_HOME")
JAVA8_HOME = get_config("JAVA", "JAVA8_HOME")

# SPOTBUGS
SPOTBUGS_PATH = get_config("SPOTBUGS", "SPOTBUGS_PATH")
METHOD_GETTER_PATH = get_config("SPOTBUGS", "METHOD_GETTER_PATH")
