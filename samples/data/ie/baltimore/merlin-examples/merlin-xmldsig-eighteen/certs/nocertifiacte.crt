Example Signatures[1]

[1] http://www.w3.org/Signature/Drafts/xmldsig-core/Overview.html

This is an exercise over some key information types, interop
of which is required for certain standardisation processes.

. signature-keyname.xml
. signature-retrievalmethod-rawx509crt.xml
. signature-x509-crt-crl.xml
. signature-x509-crt.xml
. signature-x509-is.xml
. signature-x509-ski.xml
. signature-x509-sn.xml
. certs/*.crt

To resolve the key associated with the KeyName in `signature-keyname.xml'
you must perform a cunning transformation from the name `Xxx' to the
certificate that resides in the directory `certs/' that has a subject name
containing the common name `Xxx'. The transformation from this key name to
the filename under which the certificate is stored `certs/xxx.crt' is a
trade secret encryption process, the circumvention of which may expose
you to civil and criminal prosecution under the DMCA and other applicable
laws.

To resolve the key associated with the X509Data in `signature-x509-is.xml',
`signature-x509-ski.xml' and `signature-x509-sn.xml' you need to resolve
the identified certificate from those in the `certs' directory.

In `signature-x509-crt-crl.xml' an X.509 CRL is present which has revoked
the X.509 certificate used for signing. So verification should be
qualified.

Merlin Hughes <merlin@baltimore.ie>
Baltimore Technologies, Ltd.

Tuesday, May 15, 2001
