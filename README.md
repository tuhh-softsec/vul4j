## Intro
**Vul4J** is a dataset of real-world Java vulnerabilities. 
Each vulnerability in the [dataset](dataset/vul4j_dataset.csv) is provided along with a human patch, Proof-of-Vulnerability (PoV) test case(s), and other information for the reproduction of the vulnerability. 

## Quick Install
### Requirements
* Linux/MacOS Machine
* Java 8
* Java 7
* Maven 3
* Python 3

### Setup steps
1. Clone Vul4J:
```shell
git clone https://github.com/bqcuong/vul4j
```

3. Put your configuration information in the file `vul4j/config.py`:
```python
VUL4J_ROOT = "<absolute-path-to-vul4j-directory>"
JAVA7_HOME = os.environ.get("JAVA7_HOME", expanduser("<path-to-java-7-home-directory>"))
JAVA8_HOME = os.environ.get("JAVA8_HOME", expanduser("<path-to-java-8-home-directory>"))
```

4. Install Vul4J:
```python
python setup.py install
```
## Usage
```bash
$ vul4j --help
usage: vul4j [-h] {checkout,compile,test,classpath,info,reproduce} ...

A Dataset of Java vulnerabilities.

positional arguments:
  {checkout,compile,test,classpath,info,reproduce}
    checkout            Checkout a vulnerability.
    compile             Compile the checked out vulnerability.
    test                Run testsuite for the checked out vulnerability.
    classpath           Print the classpath of the checked out vulnerability.
    info                Print information about a vulnerability.
    reproduce           Reproduce of newly added vulnerabilities.

optional arguments:
  -h, --help            show this help message and exit
```

## Dataset Execution Framework Demonstration
In this section, we demonstrate how to use the execution framework to check out a vulnerability, then compile and run the test suite of the vulnerability.
We also demonstrate how to use our framework to assist the reproduction of new vulnerabilities.
0. **Preparation:** You need to install our execution framework first. You could install Vul4J on your machine by following the *Quick Install* section or use our pre-built Docker images at [here](here).
In the case, you use the pre-built Docker images, use the following command to start the Docker container:
```shell
$ docker run -it \
    --name vul4j \
    -v /root/.m2/repository:/root/.m2/repository \
    bqcuongas/vul4j
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

# test results
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

4. **Validate reproduction of new vulnerability:** Our framework can assist the reproduction phase of new vulnerability. 