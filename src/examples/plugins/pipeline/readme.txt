== overview

The files in this directory are intended as an example of how to use
the Apache Digester's "plugins" functionality.

Topics covered:
* how to declare "plugin points" using PluginCreateRule.
* how to write plugin classes.

If you're just starting with Digester, try the "api" examples first.
This example demonstrates more advanced features of the digester.

== compiling and running


First rename the build.properties.sample file in the parent directory
to build.properties and edit it to suit your environment. Then in this
directory:

* to compile:
  ant compile

* to run the examples:
  ant run-uppercase
  ant run-substitute
  ant run-compound

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly. See the build.properties and build.xml
files for details.
