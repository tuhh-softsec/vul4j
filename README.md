## Introduction
**Vul4J** is a dataset of real-world Java vulnerabilities. 
Each vulnerability in the [dataset](dataset/vul4j_dataset.csv) is provided along with a human patch, Proof-of-Vulnerability (PoV) test case(s), and other information for the reproduction of the vulnerability.

In this repository, we host the Vul4J dataset, the support framework that allows performing several common tasks required by APR tools on the dataset, and the scripts for Patch Filtering.

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6383527.svg)](https://doi.org/10.5281/zenodo.6383527)

- License for the dataset: CC-BY-4.0
- License for code: GPL-3.0

If you use Vul4J in academic context, please cite:
```bibtex
@inproceedings{vul4j2022,
  title={Vul4J: A Dataset of Reproducible Java Vulnerabilities Geared Towards the Study of Program Repair Techniques},  
  author={Bui, Quang-Cuong and Scandariato, Riccardo and Ferreyra, Nicol{\'a}s E. D{\'\i}az},  
  booktitle={2022 IEEE/ACM 19th International Conference on Mining Software Repositories (MSR)},   
  year={2022},
  pages={464-468},
  doi={10.1145/3524842.3528482}
}
```

* **Reproduction status**: Due to the deprecated library dependencies, a number of vulnerabilities in Vul4J are no longer reproducible, especially [those belonging to Spring projects](https://spring.io/blog/2022/12/14/notice-of-permissions-changes-to-repo-spring-io-january-2023#upcoming-changes). Please see the detailed information in [STATUS.md](STATUS.md). A temporary workaround was implemented to address this issue. For the remaining vulnerabilities, all their dependencies are being collected and packed into this [pre-built Docker image (`alldeps`)](https://hub.docker.com/layers/bqcuongas/vul4j/alldeps/images/sha256-04ad7977adb1031ef3841537f82860f2f05c611bc2faf63d6c2fc7cb53a01423), so that they are reproducible.

## Quick Install
### Requirements
* Linux/macOS Machine
* Java 7
* Java 8
* Java 11
* Java 16
* Maven 3
* Python 3

### Setup steps
1. Clone Vul4J:
```shell
git clone https://github.com/bqcuong/vul4j
```

2. Install Vul4J:
```python
python setup.py install
```

This will create a new `vul4j_data` folder in the home directory.
If it already exists, you will need to manually delete it first.
You can find the `vul4j.ini` and log files there.
By default, the reproduction, temporary cloning and spotbugs directories are placed there as well.
You can change these in the `vul4j.ini`.

3. Put your configuration information in the file `~/vul4j_data/vul4j.ini`:
```ini
JAVA7_HOME = <path-to-java-7-home-directory>
JAVA8_HOME = <path-to-java-8-home-directory>
JAVA11_HOME = <path-to-java-11-home-directory>
JAVA16_HOME = <path-to-java-16-home-directory>
```
Other configuration values are optional,
if left empty, the environment variables will be checked or a default value will be used.

The `vul4j.ini` within the **vul4j git repository** is just a sample
and will get overridden by certain operations. Make sure to edit the one in your home directory.

4. You can check if everything is installed correctly:
```shell
vul4j status
```

## Usage
```bash
$ vul4j --help

usage: vul4j [-h] [-l LOG] {status,checkout,compile,test,apply,sast,reproduce,verify,info,classpath,get-spotbugs} ...

A Dataset of Java vulnerabilities.

positional arguments:
  {status,checkout,compile,test,apply,sast,reproduce,verify,info,classpath,get-spotbugs}
    status              Lists vul4j requirements and their availability.
    checkout            Checkout a vulnerability into the specified directory.
    compile             Compile the checked out vulnerability.
    test                Run testsuite for the checked out vulnerability.
    apply               Apply the specified file versions.
    sast                Run Spotbugs analysis.
    reproduce (verify)  Verify the reproducibility of vulnerabilities in the dataset.
    info                Print information about a vulnerability.
    classpath           Print the classpath of the checked out vulnerability.
    get-spotbugs        Download Spotbugs into the user directory.

optional arguments:
  -h, --help            show this help message and exit
  -l LOG, --log LOG     Specify displayed log level for this command.
```

## Dataset Execution Framework Demonstration
In this section, we demonstrate how to use the execution framework to check out a vulnerability, then compile and run the test suite and SAST analysis of the vulnerability.
We also demonstrate how to use our framework to validate the reproduction of new vulnerabilities.

0. **Preparation:** You need to install our execution framework first. You could install Vul4J on your machine by following the *Quick Install* section or use our [pre-built Docker image](https://hub.docker.com/r/bqcuongas/vul4j).
In the case, you want to use the pre-built Docker image, use the following command to start the Docker container:
```shell
$ docker run -it --name vul4j bqcuongas/vul4j
```

1. **Checkout a vulnerability:** We will check out the vulnerability with ID *VUL4J-10*, 
which had the CVE identifier CVE-2013-2186 and made the Apache Commons FileUpload vulnerable to Null Byte Injection.
Our desired destination is the directory `/tmp/vul4j/VUL4J-10`.
```shell
$ vul4j checkout --id VUL4J-10 -d /tmp/vul4j/VUL4J-10
```

2. **Compile:** Now we can compile the vulnerability.
```shell
$ vul4j compile -d /tmp/vul4j/VUL4J-10
```

3. **Run Testsuite:** And run the test suite with the presence of the vulnerability in source code.
```shell
$ vul4j test -d /tmp/vul4j/VUL4J-10

# test results found in /tmp/vul4j/VUL4J-10/VUL4J/test_results.json
{
  "vul_id": "VUL4J-10",
  "cve_id": "CVE-2013-2186",
  "repository": {
    "name": "apache_commons-fileupload",
    "url": "https://github.com/apache/commons-fileupload",
    "human_patch_url": "https://github.com/apache/commons-fileupload/commit/163a6061fbc077d4b6e4787d26857c2baba495d1"
  },
  "tests": {
    "overall_metrics": {
      "number_running": 69,
      "number_passing": 67,
      "number_error": 0,
      "number_failing": 2,
      "number_skipping": 0
    },
    "failures": [
      {
        "test_class": "org.apache.commons.fileupload.DiskFileItemSerializeTest",
        "test_method": "testInvalidRepositoryWithNullChar",
        "failure_name": "java.lang.AssertionError",
        "detail": "Expected exception: java.io.IOException",
        "is_error": false
      },
      {
        "test_class": "org.apache.commons.fileupload.DiskFileItemSerializeTest",
        "test_method": "testInvalidRepository",
        "failure_name": "java.lang.AssertionError",
        "detail": "Expected exception: java.io.IOException",
        "is_error": false
      }
    ],
    "passing_tests": [
      "org.apache.commons.fileupload.util.mime.QuotedPrintableDecoderTestCase#invalidQuotedPrintableEncoding",
      "org.apache.commons.fileupload.util.mime.QuotedPrintableDecoderTestCase#unsafeDecodeLowerCase",
      ... 
      "org.apache.commons.fileupload.DefaultFileItemTest#testBelowThreshold"
    ],
    "skipping_tests": []
  }
}
```

4. **Run SAST analysis:** Run Spotbugs analysis on the compiled jar file.
```shell
$ vul4j sast -d /tmp/vul4j/VUL4J-10

# SAST warnings in the vulnerable files
[
  "MC_OVERRIDABLE_METHOD_CALL_IN_READ_OBJECT@org.apache.commons.fileupload.disk.DiskFileItem#readObject",
  "MC_OVERRIDABLE_METHOD_CALL_IN_READ_OBJECT@org.apache.commons.fileupload.disk.DiskFileItem#readObject",
  "MC_OVERRIDABLE_METHOD_CALL_IN_READ_OBJECT@org.apache.commons.fileupload.disk.DiskFileItem#readObject",
  "MC_OVERRIDABLE_METHOD_CALL_IN_READ_OBJECT@org.apache.commons.fileupload.disk.DiskFileItem#readObject",
  "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE@org.apache.commons.fileupload.disk.DiskFileItem#readObject"
]
```
If Spotbugs fails to run, make sure it is installed and the correct path is set in the `~/vul4j/vul4j.ini` file.
You can also install it automatically to a default location with `vul4j get-spotbugs`, which will install it inside the `~/vul4j` directory.

5. **Validate reproduction of new vulnerability:** Our framework can validate the reproduction of new vulnerability.
First, you need to provide the essential information about the new vulnerability in the [csv dataset file](dataset/vul4j_dataset.csv) including: `vul_id`, `human_patch_url`, `build_system`, `compliance_level`, `compile_cmd`, `test_all_cmd`.
Then, you can run the following command to check the new vulnerability is reproducible or not. We demonstrate with an existing vulnerability we used in the previous task. 
```shell
$ vul4j reproduce --id VUL4J-10

2024-06-13 20:35:31 | ===================== START REPRODUCE ======================
2024-06-13 20:35:31 | Reproducing 1 vulnerabilities...
2024-06-13 20:35:31 | --------------------------VUL4J-10--------------------------
2024-06-13 20:35:31 | Checking out project...
2024-06-13 20:35:31 | --> Applying version: vulnerable
2024-06-13 20:35:31 | Cleaning project...
2024-06-13 20:35:33 | Compiling...
2024-06-13 20:35:41 | Running PoV tests...
2024-06-13 20:35:46 | Number of running tests: 1
2024-06-13 20:35:46 | Failing tests: [
  "org.apache.commons.fileupload.DiskFileItemSerializeTesttestInvalidRepositoryWithNullChar"
]
2024-06-13 20:35:46 | No fixed warnings found in the dataset for VUL4J-10. Skipping Spotbugs...
2024-06-13 20:35:46 | --> Applying version: human_patch
2024-06-13 20:35:46 | Cleaning project...
2024-06-13 20:35:48 | Compiling...
2024-06-13 20:35:56 | Running PoV tests...
2024-06-13 20:36:01 | Number of running tests: 1
2024-06-13 20:36:01 | Number of passing tests: 1
2024-06-13 20:36:01 | No fixed warnings found in the dataset for VUL4J-10. Skipping Spotbugs...
2024-06-13 20:36:01 | Vulnerabilities: PASS, Spotbugs: SKIP!
2024-06-13 20:36:01 | ====================== END REPRODUCE =======================
```
