# JMH benchmark sample

Uses the [Java Microbenchmark Harness](http://openjdk.java.net/projects/code-tools/jmh/) (JMH)
for tests.

Tests taken from Java Magazine March/April 2015 ["The Quantum Physics of Java".](http://www.oraclejavamagazine-digital.com/javamagazine/march_april_2015?sub_id=COwlfbZ7bu2Af#pg41)  
Compares running two loops over the array with 67,000 integer elements.

1st loop: changes every element  
2nd loop: changes every sixteenth element

### Building the benchmarks

    $ mvn clean install

### Running the benchmarks

    $ java -jar target/benchmarks.jar

### Measurements

####Intel® Core™ i7-2600 Processor (8M Cache, up to 3.80 GHz)
Benchmark             | Mode | Cnt  | Score  | Error   | Units
----------------------|------|------|--------|---------|------
MyBenchmark.testLoop1 | avgt | 25   | 30.257 | ± 0.457 | ms/op  
MyBenchmark.testLoop2 | avgt | 25   | 29.946 | ± 0.264 | ms/op


####Intel® Core™ i7-4770S Processor (8M Cache, up to 3.90 GHz)
Benchmark             | Mode | Cnt  | Score  | Error   | Units
----------------------|------|------|--------|---------|------
MyBenchmark.testLoop1 | avgt | 25   | 26.909 | ± 0.278 | ms/op  
MyBenchmark.testLoop2 | avgt | 25   | 26.021 | ± 0.297 | ms/op
