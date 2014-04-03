
Generating Java source code after modifing protobuf definition
--------------------------------------------------------------

1. Check to see you have the correct protoc version

    	$ protoc --version

	Confirm that the result version is compatible with the version
    specified in pom.xml.

2. Generate Java source code.

    	$ protoc topology.proto  --java_out=${ONOS_HOME}/src/main/java/

	Replace `--java_out` path to root Java source directory.  
    e.g., If you're in the same directory as this file:

    	$ protoc topology.proto  --java_out=../java/

