== overview

The files in this directory are intended as an example of how to use
the Apache Digester's basic functionality via its xmlrules interface.

Topics covered:
* how to create a digester instance initialised with
  rules specified in an external file.
* how to parse a file
* how to use the "object create" rule to create java objects
* how to use the "set properties" rule (basic usage) to map xml attributes
  to java bean properties.
* how to use the "set next" rule to build trees of java objects.
* how to use the "call method rule" (basic usage)
* how to use the "call parameter rule" to process the text contained
  in a tag's body
* how to use the "call parameter rule" to process the contents of an 
  xml attribute.

== compiling and running

First rename the build.properties.sample file in the parent directory
to build.properties and edit it to suit your environment. Then in this
directory:

* to compile:
  ant compile

* to run:
  ant run

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly. See the build.properties and build.xml
files for details.
