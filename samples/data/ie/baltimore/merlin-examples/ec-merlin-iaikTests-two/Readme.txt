Gregor Karlinger <gregor.karlinger@iaik.at>'s exclusive
c14n[1] examples[2] dumped in an XML Signature[3], thereby
undoing their usefulness as standalone exclusive c14n
examples, but simplifying their testing. All errors are
my own. Version 2.

. iaikTests.example?.xml - Gregor's examples (*)
. signature.xml - Signature representing the examples
. signature.tmpl - Signature template
. c14n-?.txt - Intermediate c14n output

(*) I ran perl -pi -e 's/foo.com/example.org/g' on the files.

[1] http://www.w3.org/TR/2002/CR-xml-exc-c14n-20020212
[2] http://lists.w3.org/Archives/Public/w3c-ietf-xmldsig/2002JanMar/0259.html
[3] http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/

Merlin Hughes <merlin@baltimore.ie>
Baltimore Technologies, Ltd.
http://www.baltimore.com/

Thursday, April 18, 2002
