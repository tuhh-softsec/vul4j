# USGS Iridium Short Burst Data (SBD) Decoder Library

## About

Java library for decoding Iridium Short Burst Data packets.

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
