import configparser
import os
from os.path import normpath, expanduser


def get_config(section: str, config_name: str, default: str = "") -> str:
    """
    Returns the value of the given config entry.
    First the vul4j.ini file is checked,
    if the value or the config file is not found,
    the environment variables are checked for the provided name,
    and if none of the two previous checks have a value, the default value is used.

    :param section: section name in the config file
    :param config_name: config entry name in the config file and/or in the env vars
    :param default: default value if no config value is found
    :return:    string value of the config entry
    """
    config = configparser.ConfigParser()
    config_path = os.path.join(VUL4J_DATA, "vul4j.ini")
    config.read(config_path, encoding="utf-8")

    value = config.get(section, config_name, fallback=None)
    if value is None or value == "":
        value = os.environ.get(config_name)
    return value if value else default


# VUl4J
VUL4J_DATA = normpath(os.environ.get("VUL4J_DATA", expanduser("~/vul4j_data")))
VUL4J_GIT = normpath(get_config("VUL4J", "VUL4J_GIT"))
DATASET_PATH = normpath(get_config("VUL4J", "DATASET_PATH",
                                   os.path.join(VUL4J_GIT, "dataset", "vul4j_dataset.csv")))
VUL4J_COMMITS_URL = get_config("VUL4J", "VUL4J_COMMITS_URL")
LOG_TO_FILE = get_config("VUL4J", "LOG_TO_FILE", "1") == "1"
FILE_LOG_LEVEL = get_config("VUL4J", "FILE_LOG_LEVEL", "INFO").upper()

# DIRS
VUL4J_OUTPUT = normpath(get_config("DIRS", "VUL4J_WORKDIR", "VUL4J"))
REPRODUCTION_DIR = normpath(get_config("DIRS", "REPRODUCTION_DIR",
                                       os.path.join(VUL4J_DATA, "reproduction")))
TEMP_CLONE_DIR = normpath(get_config("DIRS", "TEMP_CLONE_DIR",
                                     os.path.join(VUL4J_DATA, "clone")))

# JAVA
JAVA_ARGS = get_config("JAVA", "JAVA_ARGS")
MVN_ARGS = get_config("JAVA", "MVN_ARGS")
JAVA7_HOME = normpath(get_config("JAVA", "JAVA7_HOME"))
JAVA8_HOME = normpath(get_config("JAVA", "JAVA8_HOME"))
JAVA11_HOME = normpath(get_config("JAVA", "JAVA11_HOME"))
JAVA16_HOME = normpath(get_config("JAVA", "JAVA16_HOME"))

# SPOTBUGS
SPOTBUGS_VERSION = get_config("SPOTBUGS", "SPOTBUGS_VERSION", "4.8.5")
SPOTBUGS_PATH = normpath(get_config("SPOTBUGS", "SPOTBUGS_PATH",
                                    os.path.join(VUL4J_DATA, f"spotbugs-{SPOTBUGS_VERSION}", "lib", "spotbugs.jar")))
MODIFICATION_EXTRACTOR_PATH = normpath(get_config("SPOTBUGS", "MODIFICATION_EXTRACTOR_PATH",
                                                  os.path.join(VUL4J_GIT,
                                                               "modification-extractor",
                                                               "modification-extractor.jar")))
