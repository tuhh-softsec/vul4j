JMH benchmark sample
====================

Uses the [Java Microbenchmark Harness](http://openjdk.java.net/projects/code-tools/jmh/) (JMH)
for tests.

Tests taken from Java Magazine March/April 2015 ["The Quantum Physics of Java".](http://www.oraclejavamagazine-digital.com/javamagazine/march_april_2015?sub_id=COwlfbZ7bu2Af#pg41)  
Compares running two loops over the array with 67,000 integer elements.

1st loop: changes every element  
2nd loop: changes every sixteenth element
  
**Building the benchmarks**

    $ mvn clean install

**Running the benchmarks**

    $ java -jar target/benchmarks.jar

