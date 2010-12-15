**************************************************************
This file contains a description of sample sigantures produced
with the IAIK Signature Library (IXSIL).

Author: Gregor Karlinger
Date: 15. 11. 2001
IXSIL Build: 1010
**************************************************************

signatureAlgorithms/dSASignature.xml
------------------------------------

  Simple signature using DSA as signature algorithm.
  DSA public key is encoded as KeyValue.
  
  The input used for the digest computation can be found in file
  "../digestInputs/dSASignature.firstReference.txt".
  
signatureAlgorithms/rSASignature.xml
------------------------------------

  Simple signature using RSA as signature algorithm.
  RSA public key is encoded as KeyValue.

  The input used for the digest computation can be found in file
  "../digestInputs/rSASignature.firstReference.txt".
  
signatureAlgorithms/hMACSignature.xml
-------------------------------------

  Simple signature using HMAC as authentication algorithm.
  The secret key is "secret".getBytes("ASCII").
  
  The input used for the digest computation can be found in file
  "../digestInputs/hMACSignature.firstReference.txt".

signatureAlgorithms/hMACShortSignature.xml
------------------------------------------

  Simple signature using HMAC as authentication algorithm.
  HMAC output length is limited to 40 bits.
  The secret key is "secret".getBytes("ASCII").
  
  The input used for the digest computation can be found in file
  "../digestInputs/hMACShortSignature.firstReference.txt".

coreFeatures/signatureTypesSignature.xml
----------------------------------------

  Signature containing several References to demonstrate
  IXSIL's ability to work with enveloping, enveloped and
  detached signatures.
  
  * The first Reference refers to an external source 
    (http) - detached signature.
  
  * The second Reference refers to an external source
    (file system) - detached signature. The external file
    is "../samples/sampleTextData.txt"
    
  * The third Reference refers to XML data that is a 
    sibling of the XML Signature, but within the same
    document - detached signature
    
    The input used for the digest computation can be found in file
    "../digestInputs/signatureTypesSignature.thirdReference.txt".
    
  * The fourth Reference refers to an external XML file
    and uses an XPath transform to select parts of the
    document for signing - detached signature. The external
    file is "../samples/sampleXMLData.xml"
    
    The input used for the digest computation can be found in file
    "../digestInputs/signatureTypesSignature.fourthReference.txt".
    
  * The fifth Reference refers to data in an Object of the
    signature via an ID reference - enveloping signature.
    
    The input used for the digest computation can be found in file
    "../digestInputs/signatureTypesSignature.fifthReference.txt".
    
  * The sixth Reference refers to data in an Object of the
    signature via an empty URI (URI="") and using an XPath
    transform to select the data for signing - enveloping
    signature.
    
    The input used for the digest computation can be found in file
    "../digestInputs/signatureTypesSignature.sixthReference.txt".
    
  * The seventh Reference refers to the signature document
    itself (URI="") and uses an enveloped signature
    transform - enveloped signature
  
    The input used for the digest computation can be found in file
    "../digestInputs/signatureTypesSignature.seventhReference.txt".
    
coreFeatures/manifestSignature.xml
----------------------------------

  Signature containing a Reference to a Manifest element that
  is stored in an Object container within the Signature.
  
  The input used for the digest computation can be found in file
  "../digestInputs/manifestSignature.firstReference.txt".

  * The first Reference in the Manifest refers to the whole
    signature document and uses an enveloped signature trans-
    form to cut out the Signature element.
    
    The input used for the digest computation can be found in file
    "../digestInputs/manifestSignature.manifest.firstReference.txt".

  * The second Reference in the Manifest refers an external
    resource (file system). The external file is
    "../samples/sampleXMLData.xml".
    
    The input used for the digest computation can be found in file
    "../digestInputs/manifestSignature.manifest.secondReference.txt".

coreFeatures/anonymousReferenceSignature.xml
--------------------------------------------

  Signature containing an anonymous Reference, i.e. the URI
  attribute of the Reference is missing. In such a case, the
  application must provide the hint where to find the corres-
  ponding data. In this case, the data can be found in file
  "../samples/anonymousReferenceContent.xml".
  
  The input used for the digest computation can be found in file
  "../digestInputs/anonymousReferenceSignature.firstReference.txt".

transforms/base64DecodeSignature.xml
------------------------------------

  Signature containing a Reference to a base64 encoded file.
  The Reference contains a Base64 transform, i.e. the file
  will be decoded prior to digest computation. The encoded
  file is "../samples/sampleBase64EncodedData.txt".
  
  The input used for the digest computation can be found in file
  "../digestInputs/base64Signature.firstReference.txt".

transforms/c14nSignature.xml
----------------------------

  Signature containing two References to an XML file. The 
  XML file is "../samples/sampleXMLData.xml".
  
  * The first Reference contains a canonical XML transform,
    that is, comments in the XML file will be removed at
    transformation.
    
    The input used for the digest computation can be found in file
    "../digestInputs/c14nSignature.firstReference.txt".

  * The second Reference contains a canonical XML transform
    with comments, that is, comments in the XML file will be
    preserved at transformation.
    
    The input used for the digest computation can be found in file
    "../digestInputs/c14nSignature.secondReference.txt".

transforms/envelopedSignatureSignature.xml
------------------------------------------

  Signature containing a single Reference to the signature 
  file itself (URI=""). The Reference contains an enveloped
  signature transform to cut out the Signature structure
  from the XML.
  
  The input used for the digest computation can be found in file
  "../digestInputs/envelopedSignatureSignature.firstReference.txt".

transforms/xPathSignature.xml
-----------------------------

  Signature containing three internal References to demonstrate
  document subset selection.
  
  * The first Reference contains a reference-only URI ("#objectId").
    This means that the XML element with its ID attribute set to the
    value "objectId" is selected; comments will be suppressed.
    Finally a canonical XML transfrom will be performed prior to
    digest computation.
    
    The input used for the digest computation can be found in file
    "../digestInputs/xPathSignature.firstReference.txt".

  * The second Reference also contains a reference-only URI 
    ("#xpointer(id('objectId'))"). This also means that the XML 
    element with its ID attribute set to the value "objectId"
    is selected; but contrary to the first Reference, this time
    comments are preserved. Finally a canonical XML transfrom
    (preserving comments option chosen) will be performed prior to
    digest computation.

    The input used for the digest computation can be found in file
    "../digestInputs/xPathSignature.secondReference.txt".

    
    