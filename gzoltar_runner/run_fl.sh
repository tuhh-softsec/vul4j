#!/usr/bin/env bash
#
# ------------------------------------------------------------------------------
# This script performs fault-localization on a Java project using the GZoltar
# command line interface either using instrumentation 'at runtime' or 'offline'.
#
# Usage:
# ./run.sh
#     --instrumentation <online|offline>
#     [--help]
#
# Requirements:
# - `java` and `javac` needs to be set and must point to the Java installation.
#
# ------------------------------------------------------------------------------

SCRIPT_DIR=$(pwd)

#
# Print error message and exit
#
die() {
  echo "$@" >&2
  exit 1
}

# ------------------------------------------------------------------ Envs & Args

GZOLTAR_VERSION="1.7.3-SNAPSHOT"

# Check whether GZOLTAR_CLI_JAR is set
export GZOLTAR_CLI_JAR="$SCRIPT_DIR/lib-gzoltar/com.gzoltar.cli-$GZOLTAR_VERSION-jar-with-dependencies.jar"
[ "$GZOLTAR_CLI_JAR" != "" ] || die "GZOLTAR_CLI is not set!"
[ -s "$GZOLTAR_CLI_JAR" ] || die "$GZOLTAR_CLI_JAR does not exist or it is empty! Please go to '$SCRIPT_DIR/..' and run 'mvn clean install'."

# Check whether GZOLTAR_AGENT_RT_JAR is set
export GZOLTAR_AGENT_RT_JAR="$SCRIPT_DIR/lib-gzoltar/com.gzoltar.agent.rt-$GZOLTAR_VERSION-all.jar"
[ "$GZOLTAR_AGENT_RT_JAR" != "" ] || die "GZOLTAR_AGENT_RT_JAR is not set!"
[ -s "$GZOLTAR_AGENT_RT_JAR" ] || die "$GZOLTAR_AGENT_RT_JAR does not exist or it is empty! Please go to '$SCRIPT_DIR/..' and run 'mvn clean install'."

USAGE="Usage: ${BASH_SOURCE[0]} -t <test classes dir> -c <classpath> -o <output folder>"
#if [ ! "$#" -eq "1" ]; then
#  die "$USAGE"
#fi

INSTRUMENTATION="online"
OUTPUT_DIR=""
SRC_CLASSES_DIR=""
TEST_CLASSES_DIR=""
CLASSPATH=""

for i in "$@"; do
  case $i in
    -o=*|--output-dir=*)
      OUTPUT_DIR="${i#*=}"
      shift # past argument=value
      ;;
    -s=*|--source-classes-dir=*)
      SRC_CLASSES_DIR="${i#*=}"
      shift # past argument=value
      ;;
    -t=*|--test-classes-dir=*)
      TEST_CLASSES_DIR="${i#*=}"
      shift # past argument=value
      ;;
    -c=*|--classpath=*)
      CLASSPATH="${i#*=}"
      shift # past argument=value
      ;;
    -i=*|--instrumentation=*)
      INSTRUMENTATION="${i#*=}"
      shift # past argument=value
      ;;
    *)
      # unknown option
      ;;
  esac
done

#echo "Output directory: $OUTPUT_DIR"
#echo "Instrumentation mode: $INSTRUMENTATION"
#echo "Source classes directory: $SRC_CLASSES_DIR"
#echo "Test classes directory: $TEST_CLASSES_DIR"
#echo "Other classpath: $CLASSPATH"

if [[ -e $OUTPUT_DIR ]]
then
  rm -rf $OUTPUT_DIR
fi

mkdir -p $OUTPUT_DIR

#
# Prepare runtime dependencies
#
#LIB_DIR="$SCRIPT_DIR/lib-gzoltar"
#mkdir -p "$LIB_DIR" || die "Failed to create $LIB_DIR!"
#[ -d "$LIB_DIR" ] || die "$LIB_DIR does not exist!"
#
#JUNIT_JAR="$LIB_DIR/junit.jar"
#if [ ! -s "$JUNIT_JAR" ]; then
#  wget "https://repo1.maven.org/maven2/junit/junit/4.12/junit-4.12.jar" -O "$JUNIT_JAR" || die "Failed to get junit-4.12.jar from https://repo1.maven.org!"
#fi
#[ -s "$JUNIT_JAR" ] || die "$JUNIT_JAR does not exist or it is empty!"
#
#HAMCREST_JAR="$LIB_DIR/hamcrest-core.jar"
#if [ ! -s "$HAMCREST_JAR" ]; then
#  wget -np -nv "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" -O "$HAMCREST_JAR" || die "Failed to get hamcrest-core-1.3.jar from https://repo1.maven.org!"
#fi
#[ -s "$HAMCREST_JAR" ] || die "$HAMCREST_JAR does not exist or it is empty!"

#
# Collect list of unit test cases to run
#

echo "Collect list of unit test cases to run ..."

UNIT_TESTS_FILE="$OUTPUT_DIR/tests.txt"

java -cp $SRC_CLASSES_DIR:$TEST_CLASSES_DIR:$CLASSPATH:$GZOLTAR_CLI_JAR \
  com.gzoltar.cli.Main listTestMethods $TEST_CLASSES_DIR \
    --outputFile "$UNIT_TESTS_FILE" \
    --includes "*" || die "Collection of unit test cases has failed!"
[ -s "$UNIT_TESTS_FILE" ] || die "$UNIT_TESTS_FILE does not exist or it is empty!"

##
## Collect coverage
##

SER_FILE="$OUTPUT_DIR/gzoltar.ser"

if [[ "$INSTRUMENTATION" == "online" ]]; then
  echo "Perform instrumentation at runtime and run each unit test case in isolation ..."

  # Perform instrumentation at runtime and run each unit test case in isolation
  java -javaagent:$GZOLTAR_AGENT_RT_JAR=destfile=$SER_FILE,buildlocation=$SRC_CLASSES_DIR,includes="*",excludes="",inclnolocationclasses=false,output="file" \
      -cp $SRC_CLASSES_DIR:$TEST_CLASSES_DIR:$CLASSPATH:$GZOLTAR_CLI_JAR \
      com.gzoltar.cli.Main runTestMethods \
        --testMethods "$UNIT_TESTS_FILE" \
        --collectCoverage || die "Coverage collection has failed!"

elif [[ "$INSTRUMENTATION" == "offline" ]]; then #offline mode not work yet
  echo "Perform offline instrumentation ..."

  # Backup original classes
  BUILD_BACKUP_DIR="$SCRIPT_DIR/.src_classes_backup"
  mv "$SRC_CLASSES_DIR" "$BUILD_BACKUP_DIR" || die "Backup of original classes has failed!"
  mkdir -p "$SRC_CLASSES_DIR"

  # Perform offline instrumentation
  java -cp $BUILD_BACKUP_DIR:$GZOLTAR_AGENT_RT_JAR:$GZOLTAR_CLI_JAR \
    com.gzoltar.cli.Main instrument \
    --outputDirectory "$SRC_CLASSES_DIR" \
    $BUILD_BACKUP_DIR || die "Offline instrumentation has failed!"

  echo "Run each unit test case in isolation ..."

  # Run each unit test case in isolation
  java -cp $SRC_CLASSES_DIR:$TEST_CLASSES_DIR:$CLASSPATH:$GZOLTAR_AGENT_RT_JAR:$GZOLTAR_CLI_JAR \
    -Dgzoltar-agent.destfile=$SER_FILE \
    -Dgzoltar-agent.output="file" \
    com.gzoltar.cli.Main runTestMethods \
      --testMethods "$UNIT_TESTS_FILE" \
      --offline \
      --collectCoverage || die "Coverage collection has failed!"

  # Restore original classes
  cp -R $BUILD_BACKUP_DIR/* "$SRC_CLASSES_DIR" || die "Restore of original classes has failed!"
  rm -rf "$BUILD_BACKUP_DIR"
fi

[ -s "$SER_FILE" ] || die "$SER_FILE does not exist or it is empty!"

##
## Create fault localization report
##

echo "Create fault localization report ..."

SPECTRA_FILE="$OUTPUT_DIR/sfl/txt/spectra.csv"
MATRIX_FILE="$OUTPUT_DIR/sfl/txt/matrix.txt"
TESTS_FILE="$OUTPUT_DIR/sfl/txt/tests.csv"

java -cp $SRC_CLASSES_DIR:$TEST_CLASSES_DIR:$CLASSPATH:$GZOLTAR_CLI_JAR \
  com.gzoltar.cli.Main faultLocalizationReport \
    --buildLocation "$SRC_CLASSES_DIR" \
    --granularity "line" \
    --inclPublicMethods \
    --inclStaticConstructors \
    --inclDeprecatedMethods \
    --dataFile "$SER_FILE" \
    --outputDirectory "$OUTPUT_DIR" \
    --family "sfl" \
    --formula "ochiai" \
    --metric "entropy" \
    --formatter "txt" || die "Generation of fault-localization report has failed!"

[ -s "$SPECTRA_FILE" ] || die "$SPECTRA_FILE does not exist or it is empty!"
[ -s "$MATRIX_FILE" ] || die "$MATRIX_FILE does not exist or it is empty!"
[ -s "$TESTS_FILE" ] || die "$TESTS_FILE does not exist or it is empty!"

echo "DONE!"
exit 0