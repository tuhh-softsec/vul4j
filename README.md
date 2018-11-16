# USGS Iridium Short Burst Data (SBD) Decoder Library

Java library for decoding Iridium Short Burst Data packets.

[![Build Status](https://travis-ci.org/usgs/warc-iridium-sbd-decoder.svg?branch=WARC-934-upgrade-to-spring-boot-2.1.0)](https://travis-ci.org/usgs/warc-iridium-sbd-decoder)

## About

This Java software library is used to decode Short Burst Data packets transmitted from the Iridium satellite 
networking using the DirectIP configuration. When paired with a simple TCP socket listener, this library enables
the header and payload data to be extracted from the message and parsed successfully. The user must define the 
data types they are expecting in the payload as well as any scale transformation that should be applied. 

## Developer(s)

 * Jimi Darcey
 * Mark McKelvy
 
## Dependencies

This project uses Lombok, which must be "installed" for your IDE. 
 * Download jar: https://projectlombok.org/download
 * Execute jar (double click) 
 
```
java -jar lombok.jar
```

### Lombok Alternative

If jar "installation" does not work,
 * locate your eclipse.ini or STS.ini file
 * Copy lombok.jar to the same directory
 * Add the following to the bottom of the .ini file  
 
```
-javaagent:lombok.jar
```

Note: on some systems you may need to put the absolute path to this file. 
 
## Build

```
mvn install
```
