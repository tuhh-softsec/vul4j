# Vul4J
Vul4J: A benchmark of Java vulnerabilities to enable controlled research studies for testing and debugging.

# Setup
1. Clone Vul4J:
```shell
git clone https://github.com/bqcuong/vul4j
```

2. Setup the database repository:
```shell
git submodule update --init 
```

3. Configuration in `vul4j.py`:
```python
JAVA7_HOME = os.environ.get("JAVA7_HOME", expanduser("/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home"))
JAVA8_HOME = os.environ.get("JAVA8_HOME", expanduser("/Library/Java/JavaVirtualMachines/jdk1.8.0_281.jdk/Contents/Home"))
```
# Usage
```bash
$ python vul4j.py 
usage: vul4j [-h] {checkout,compile,test,classpath,fl} ...

A Benchmark of Java vulnerabilities.

positional arguments:
  {checkout,compile,test,classpath,fl}
                        Checkout a vulnerability in the benchmark.

optional arguments:
  -h, --help            show this help message and exit
```