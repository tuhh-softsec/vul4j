The files in this directory are intended as an example of how to use
the Apache Digester's basic functionality via its java interface.

Topics covered:
* how to read xml from a string (instead of a file)
* how to use Digester.getRoot() to retrieve the "root" object
  created when parsing an input file.
* how to use the "factory create" rule to create java objects which
  do not have default (no-argument) constructors.
* how to use ExtendedBaseRules to get trailing-wildcard support
* how to use the BeanPropertySetterRule
* how to use the "set properties" rule (advanced usage) to map xml attributes
  to java bean properties with names different from the xml attribute name.
* how to use the SetPropertyRule.
* how to use the ObjectParamRule to pass a constant string to a method.


If you haven't read the "addressbook" example, it is recommended that
you start there first. This example demonstrates more advanced features
of the digester.
