The subdirectories of this directory provide examples of how to use
the Apache Digester's java API.

With the API approach, java code is used to configure the digester with
a set of rules to execute when xml is processed. It is these rules that
determine how the input xml is mapped into a tree of java objects.

An alternative is to use the "xmlrules" digester extension, which allows
the digester rules to be configured via an xml file. This allows the
mapping between input xml and java objects to be modified without
recompilation of any source code.

The examples are graduated in the following order:

1. addressbook
2. catalog