import os
from os.path import expanduser

DATASET_PATH = os.environ.get("DATASET_PATH",
                              expanduser("/Users/cuong/PycharmProjects/vul4j/dataset/vul4j_dataset.csv"))
BENCHMARK_PATH = os.environ.get("BENCHMARK_PATH",
                                expanduser("/Users/cuong/Research/securethemall/benchmarks/sapkb"))
GZOLTAR_RUNNER_PATH = os.environ.get("GZOLTAR_RUNNER_PATH",
                                     expanduser("/Users/cuong/PycharmProjects/vul4j/gzoltar_runner"))

JAVA7_HOME = os.environ.get("JAVA7_HOME",
                            expanduser("/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home"))
JAVA8_HOME = os.environ.get("JAVA8_HOME",
                            expanduser("/Library/Java/JavaVirtualMachines/jdk1.8.0_281.jdk/Contents/Home"))

JAVA_ARGS = os.environ.get("JAVA_ARGS", "-Xmx4g -Xms1g -XX:MaxPermSize=512m")
MVN_OPTS = os.environ.get("MVN_OPTS", "-Xmx4g -Xms1g -XX:MaxPermSize=512m")

OUTPUT_FOLDER_NAME = "VUL4J"
ENABLE_EXECUTING_LOGS = os.environ.get("ENABLE_EXECUTING_LOGS", "1")