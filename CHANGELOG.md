# USGS Iridium Short Burst Data (SBD) Decoder Library

## 1.2.1 - 03/27/2019
 * Remove premature byte check for pseudobinary b format in SbdParser
 * Update PseudobinaryBPayloadDecoder to use a queue, be able to decode even if all expected types aren't present, and to be sensitive to "NaN" values

## 1.2.0 - 03/25/2019
 * Add support for decoding Sutron Standard CSV messages
 * Update to Spring Boot 2.1.3
 * Update to Guava 27.1
 * Update to JDT Annotation 2.2.200
 * Update to Commons-CSV 1.6
 * SbdDataType is now Comparable
 * Add SbdDataTypeProvider
 * Add PayloadDecoder interface, along with PseudobinaryBPayloadDecoder and SutronStandardCsvPayloadDecoder
 * Add PayloadType
 * Rename BinaryParser to SbdParser
 * Remove StationDataTypes & test
 * SbdParser now requires a set of SbdDataType (to support CSV decoding)
 
## 1.1.0 - 11/16/2018
 * Update .travis.yml to build OpenJDK11 and all branches
 * Update to Spring Boot 2.1.0
 * Update guava to 27.0-jre
 * Workaround for "Could not find or load main class org.apache.maven.surefire.booter.ForkedBooter" MAVEN error

## 1.0.0 - 05/11/2018
 * Initial release
