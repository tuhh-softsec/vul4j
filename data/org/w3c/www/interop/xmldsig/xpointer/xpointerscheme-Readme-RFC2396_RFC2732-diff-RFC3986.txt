Discussion on RFC 2396 + RFC 2732 vs. RFC 3986.

======================================================================
XMLDSIG 2002  4.3.3.1
======================================================================
> The URI attribute identifies a data object using a URI-Reference, as
> specified by RFC2396 [URI]. The set of allowed characters for URI
> attributes is the same as for XML, namely [Unicode]. However, some
> Unicode characters are disallowed from URI references including all
> non-ASCII characters and the excluded characters listed in RFC2396
> [URI, section 2.4]. However, the number sign (#), percent sign (%),
> and square bracket characters re-allowed in RFC 2732 [URI-Literal]
> are permitted.


RFC 2396
========

fragment      = *uric
uric          = reserved | unreserved | escaped
reserved      = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
                "$" | ","
unreserved    = alphanum | mark
mark          = "-" | "_" | "." | "!" | "~" | "*" | "'" |
                "(" | ")"


--> 

fragment      = *( 
                ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
                "$" | ","
                 alphanum |
                "-" | "_" | "." | "!" | "~" | "*" | "'" |
                "(" | ")"
                 )

--> 

a..zA..Z0..9-._~!$&'()*+,;=/?:@


XMLDSIG 2002 allowed square brackets([]) as in RFC 2732.


RFC 2732
========

> This document incudes an update to the generic syntax for Uniform
> Resource Identifiers defined in RFC 2396 [URL].  It defines a syntax
> for IPv6 addresses and allows the use of "[" and "]" within a URI
> explicitly for this reserved purpose.

      reserved    = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
                    "$" | "," | "[" | "]"

-->

fragment      = *( 
                ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
                "$" | "," | "[" | "]"
                 alphanum |
                "-" | "_" | "." | "!" | "~" | "*" | "'" |
                "(" | ")"
                 )

--> 

a..zA..Z0..9-._~!$&'()*+,;=/?:@[]


Although the grammar was changed in RFC 2732 in a way that allowed 
"[" | "]" in the fragment the prose in RFC 2732 is saying:

> It defines a syntax
> for IPv6 addresses and allows the use of "[" and "]" within a URI
> explicitly for this reserved purpose.


That indicates that this overrules the grammar wich is also consistent
with the current RFC 3986 grammar.


XMLDSIG 2002 allowed (#), percent sign (%)
===========================================
Here the only valid interpretation is is that (#), percent sign (%)
are allowed (in their non-percent encoded form) to sperate the fragment
and to initiate a percent encoding respectively 
because RFC 2396
 says 
the following:

> The character "#" is excluded
> because it is used to delimit a URI from a fragment identifier in URI
> references (Section 4). The percent character "%" is excluded because
> it is used for the encoding of escaped characters.

Wich is also consistent with RFC 3986 and the latest draft XMLDSIG 2007.


+========+ The interpretation above makes the mention of number sign (#)
|        | and percent sign (%) in 4.3.3.1 redundant.
| BEWARE | Some implementations may have wrongly interpreted 4.3.3.1
|        | to allow number sign (#) and percent sign (%) in in their 
|        | non-percent encoded form in the fragment, wich however
|        | contradicts the grammar in RFC 2396 and the prose in 
+========+ RFC 2732 and is inconsistent with RFC 3986.

If such a misinterpretation caused the production of signatures 
containing an xpointer like the following 

#xpointer(//*[@authenticate='true']) (cf. EBICS-Standard in Germany)

it does not comply to the grammar in RFC 3986 and the interpretation 
of RFC 2732 above does not allow square brackets in the fragment. 

Correct would be the following

#xpointer(//*%5B@authenticate='true'%5D)


As however square brackets wrongly appear to be allowed in fragments
according to RFC 2732 grammar, but prohibited to the prose in RFC 2732
we may want to allow implementations to verify such signatures and 
advocate against the creation of new signatures that fail to escape the
gen-delims characters in RFC 3986 (unless they really delimit the 
components of the URI).


The text in the current draft correctly follows RFC 3986, but maybe we
would like to add a note pointing to this mail.

======================================================================
XMLDSIG 2007 4.3.3.1 
======================================================================

RFC 3986

fragment      = *( pchar / "/" / "?" )
pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
                 / "*" / "+" / "," / ";" / "="

--> 

fragment      = *( pct-encoded / ALPHA / DIGIT / "-" / "." / "_" / "~" 
                 / "!" / "$" / "&" / "'" / "(" / ")"
                 / "*" / "+" / "," / ";" / "=" 
                 / "/" / "?" )

--> 

a..zA..Z0..9-._~!$&'()*+,;=/?:@


==>

The allowed characters are equal usinf the interpretation in this mail.

RFC 2396 fragment chars are : a..zA..Z0..9-._~!$&'()*+,;=/?:@
RFC 3986 fragment chars are : a..zA..Z0..9-._~!$&'()*+,;=/?:@


regards

Konrad Lanz

P.S: Non percent encoded unicode caracters that can live in URI
references inside XML are disjoint from the set of characters in 
RFC 2396 and RFC 3986 grammar and hence do not need to be discussed
here further.

-- 
Konrad Lanz, IAIK/SIC - Graz University of Technology
Inffeldgasse 16a, 8010 Graz, Austria
Tel: +43 316 873 5547
Fax: +43 316 873 5520
https://www.iaik.tugraz.at/aboutus/people/lanz
http://jce.iaik.tugraz.at

Certificate chain (including the EuroPKI root certificate):
https://europki.iaik.at/ca/europki-at/cert_download.htm


