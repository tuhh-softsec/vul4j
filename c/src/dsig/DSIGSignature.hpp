/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European 
 * Commission in the <WebSig> project in the ISIS Programme. 
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 * XSEC
 *
 * DSIGSignature := Class for checking and setting up signature nodes in a DSIG signature
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGSIGNATURE_INCLUDE
#define DSIGSIGNATURE_INCLUDE

// XSEC Includes
#include <xsec/utils/XSECSafeBufferFormatter.hpp>
#include <xsec/dsig/DSIGTransform.hpp>
#include <xsec/dsig/DSIGKeyInfoList.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/dsig/DSIGSignedInfo.hpp>

// Xerces Includes

#include <xercesc/dom/DOM.hpp>

class XSECBinTXFMInputStream;
class XSECURIResolver;
class XSECKeyInfoResolver;
class DSIGKeyInfoValue;
class DSIGKeyInfoX509;
class DSIGKeyInfoName;

/**
 * @ingroup pubsig
 */
/*\@{*/

/**
 * @brief The main class used for manipulating XML Digital Signatures.
 *
 * <p>The DSIGSignature class is used to manipulate and verify
 * <signature> blocks.  It should only ever be created via the 
 * XSECProvider class.</p>
 *
 */

class DSIG_EXPORT DSIGSignature {

protected:

    /** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor for use with existing XML signatures or templates.
	 *
	 * <p>Create a DSIGSignature object based on an already existing
	 * DSIG Signature XML node.  It is assumed that the underlying
	 * DOM structure is in place and works correctly.</p>
	 *
	 * <p>It is required that the caller pass in the signature DOM Node
	 * as there may be more than one signature in a document.  The caller
	 * needs to specify which signature tree is to be used.</p>
	 *
	 * @param doc The DOM document node in which the signature is embedded.
	 * @param sigNode The DOM node (within doc) that is to be used as the base of the signature.
	 * @see #load
	 */

	DSIGSignature(DOMDocument *doc, DOMNode *sigNode);
	~DSIGSignature();
    //@}
	
public:

	/** @name Load and Setup Functions */
	//@{

	/**
	  * \brief Load the signature information from the DOM source.
	  *
	  * Used to tell the DSIGSignature object to read from the DOM tree
	  * into local structures.  Will throw various exceptions if it finds that
	  * the DOM structure is not in line with the XML Signature standard.
	  *
	  */

	void load(void);

	/**
	  * \brief Externally set the signing/verification key
	  *
	  * Used prior to a verify or sign operation to set the signature key (public or
	  * private respectively) to be used for the operation.
	  *
	  * @note Once passed in via this call, the key is owned by the Signature.  It will
	  * deleted when a new key is loaded or the signature is released.
	  *
	  * @see #verify
	  * @see #sign
	  */

	void setSigningKey(XSECCryptoKey *k);

	//@}

	/** @name Signature Operations */
	//@{

	/**
	  * \brief Verify that a signature is valid.
	  *
	  * <p>The <I>verify</I> function will validate the signature of an XML document
	  * previously loaded into the DSIGSignature structure via a <I>load</I>.</p>
	  *
	  * <p>It performs the following operations : </p>
	  * <ul>
	  *		<li>Iterate through each reference and validate the hash;
	  *		<li>Iterate through references contained in <manifest> elements;
	  *		<li>Calculate the hash of the <SignedInfo> element; and
	  *		<li>Validate the signature of the hash previously calculated.
	  * </ul>
	  * 
	  * @returns true/false
	  *		<ul>
	  *        <li><b>true</b> = Signature (and all references) validated correctly.
	  *		   <li><b>false</b> = Signature validation failed.  An error list can be found via a
	  *          call to #getErrMsgs.
	  *		</ul>
	  * @see #load
	  * @see #getErrMsgs
	  */

	bool verify(void);

	/**
	  * \brief Verify a signature is valid (skip references).
	  *
	  * <p>This function is almost the same as #verify except it will skip the
	  * reference checks.
	  *
	  * @see #load
	  * @see #verify
	  */

	bool verifySignatureOnly(void);

	/**
	  * \brief Sign a DSIGSignature DOM structure.
	  *
	  * <p>The #sign function will create the reference hash values and signature
	  * value in a DOM structure previously created via a #load or #createBlankSignature
	  * call </p>
	  *
	  * <p>It performs the following operations : </p>
	  * <ul>
	  *		<li>Iterate through each reference, calculate and set the hash value;
	  *		<li>Iterate through references contained in <manifest> elements and set their values;
	  *		<li>Calculate the hash of the <SignedInfo> element; and
	  *		<li>Calculate (and set) the signature value given the hash previously calculated.
	  * </ul>
	  * 
	  * @note The key to use for signing must have been set prior to call to sign using #setSigningKey
	  *
	  * @throws XSECException (for errors during the XML formatting and loading)
	  * @throws XSECCryptoException (for errors during the cryptographic operations)
	  *
	  * @see #setSigningKey
	  * @see #load
	  * @see #getErrMsgs
	  */

	void sign(void);
	//@}

	/** @name Functions to create and manipulate signature elements. */
	//@{
	
	/**
	  * \brief Set the prefix be used for the DSIG namespace.
	  *
	  * <p>When the XSEC library creates XML Element nodes, it uses the prefix here
	  * for all nodes created.  By default, the library assumes that the default
	  * namespace is used.</p>
	  *
	  * <p>The #createBlankSignature function will use this prefix to setup the
	  * dsig namespace.  E.g. (assuming a call has been made to set the prefix to "ds")
	  * the <Signature> element will have a namespace attribute added of</p>
	  *
	  * <p>xmlns:ds="http://www.w3.org/2000/09/xmldsig#"</p>
	  *
	  * <p>If no prefix has been set, this attribute will be set as the default namespace</p>
	  *
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Digital Signature nodes
	  */

	void setDSIGNSPrefix(const XMLCh * prefix);

	/**
	  * \brief Set the prefix be used for the Exclusive Canonicalisation namespace.
	  *
	  * The Exclusive Canonicalisation specification defines a new namespace for the
	  * InclusiveNamespaces node.  This function can be used to set the prefix
	  * that the library will use when creating nodes within this namespace.
	  *
	  * <p>xmlns:ds="http://www.w3.org/2001/10/xml-exc-c14n#"</p>
	  *
	  * If no prefix is set, the default namespace will be used
	  *
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Exclusive Canonicalisation nodes
	  */

	void setECNSPrefix(const XMLCh * prefix);

	/**
	  * \brief Set the prefix be used for the XPath-Filter2 namespace.
	  *
	  * The XPathFilter definition uses its own namespace.  This
	  * method can be used to set the prefix that the library will use
	  * when creating elements in this namespace
	  *
	  * <p>xmlns:ds="http://www.w3.org/2002/06/xmldsig-filter2"</p>
	  *
	  * If no prefix is set, the default namespace will be used
	  *
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XPath
	  * filter nodes
	  */

	void setXPFNSPrefix(const XMLCh * prefix);

	/**
	 * \brief Create a <Signature> DOM structure.
	 *
	 * <p>The DOM structure created is still divorced from the document.  The callee
	 * needs to take the returned <Signature> Element node and insert it at the
	 * appropriate place in their document.</p>
	 *
	 * <p>The signature is a skeleton only.  There are no references or KeyInfo
	 * elements inserted.  However the DSIGSignature structures are set up with the
	 * new information, so once an element has been created and a signing key has been
	 * set, a call to #sign will sign appropriately.
	 *
	 * @note The digest method (hash method) set here is for the signing function only.
	 * Different hash methods can be used for reference elements.
	 *
	 * @param doc The document the Signature DOM structure will be inserted into.
	 * @param cm The canonicalisation method to use.
	 * @param sm The signature algorithm to be used.
	 * @param hm The Digest function to be used for the actual signatures.
	 * @returns The newly created <Signature> element that the caller should insert in
	 * the document.
	 */

	DOMElement *createBlankSignature(DOMDocument *doc,
		canonicalizationMethod cm = CANON_C14N_NOC,
		signatureMethod	sm = SIGNATURE_DSA,
		hashMethod hm = HASH_SHA1);

	/**
	 * \brief Add a new reference to the end of the list of <Reference> nodes.
	 *
	 * <p>Creates a new DSIGReference, adds it to the list of references handled
	 * by the owning DSIGSignature and also creates the skeleton DOM structure into
	 * the document.</p>
	 *
	 * @note The XSEC Library currently makes very little use of <em>type</em>
	 * attributes in <Reference> Elements.  However this may of use to calling
	 * applications.
	 *
	 * @see DSIGReference
	 * @param URI The Data that this Reference node refers to.
	 * @param hm The hashing (digest) method to be used for this reference
	 * @param type A "type" string (as defined in XML Signature).
	 * @returns The newly created DSIGReference element.
	 *
	 */

	
	DSIGReference * createReference(const XMLCh * URI,
		hashMethod hm = HASH_SHA1, char * type = NULL);

	//@}

	/** @name General and Information functions. */
	//@{

	/**
	 * \brief Get the hash of the Signed Value
	 * 
	 * Function to calculate and return the hash of the <SignedInfo>
	 * structures (after the canonicalization defined by
	 * <CanonicalizationMethod> has been performed).
	 *
	 * @param hashBuf Buffer to place the raw hash in.
	 * @param hashBufLen The length of the buffer
	 * @returns The length of the hash that was placed in hashBuf
	 *
	 */

	unsigned int calculateSignedInfoHash(unsigned char * hashBuf, 
										unsigned int hashBufLen);

	/**
	 * \brief Return the reference list for outside use.
	 *
	 * Returns a pointer to the list of references which can
	 * then be read by the caller.
	 *
	 * @returns The referenceList
	 */

	DSIGReferenceList * getReferenceList(void);

	/**
	 * \brief Create an input stream from SignedInfo.
	 *
	 * This method allows applications to read the fully canonicalised
	 * byte stream that is hashed and signed.
	 *
	 * All transforms are performed up to the point where they would
	 * normally be fed into the Digest function.
	 *
	 * @returns A BinInputSource of the canonicalised SignedInfo
	 */


	XSECBinTXFMInputStream * makeBinInputStream(void) const;


	/**
	 * \brief Get the Error messages from the last #verify.
	 *
	 * Returns a list of text error messages from the last Signature
	 * operation.  Each error that occurred is in the buffer, separated
	 * by new-lines.
	 *
	 * @note The buffer is owned by the DSIGSignature object - do not delete it
	 *
	 * @returns A pointer to the buffer containing the error strings.
	 *
	 */

	const XMLCh * getErrMsgs(void);

	/**
	 * \brief Get the NS Prefix being used for DSIG elements.
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setDSIGNSPrefix
	 *
	 */

	const XMLCh * getDSIGNSPrefix() {return mp_prefixNS;}

	/**
	 * \brief Get the NS being used for EC nodes
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setECNSPrefix
	 */

	const XMLCh * getECNSPrefix() {return mp_ecPrefixNS;}

	/**
	 * \brief Get the NS being used for XPath Filter2 nodes
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setXPFNSPrefix
	 */

	const XMLCh * getXPFNSPrefix() {return mp_xpfPrefixNS;}

	/**
	 * \brief
	 *
	 * Get the DOM_Document that this Signature is operating within.
	 *
	 * Mainly used by the library itself.
	 *
	 * @returns The DOM_Document node.
	 */

	DOMDocument * getParentDocument() {return mp_doc;}

	/**
	 * \brief Get canonicalisation algorithm
	 *
	 * Returns the canonicalisation algorithm that will be/is used
	 * to canonicalise the <SignedInfo> element prior to hash/sign
	 *
	 * @returns The canonicalisation method
	 */

	canonicalizationMethod getCanonicalizationMethod(void) 
	{return (mp_signedInfo != NULL ? 
			 mp_signedInfo->getCanonicalizationMethod() : CANON_NONE);}


	/**
	 * \brief Get the hash method
	 *
	 * Obtain the hash (digest) algorithm that is used to generate a hash
	 * of the canonicalised <SignedInfo> element.
	 *
	 * @returns the Hash (digest) Method
	 */

	hashMethod getHashMethod(void)
	{return (mp_signedInfo != NULL ? 
			 mp_signedInfo->getHashMethod() : HASH_NONE);}

	/**
	 * \brief Get the signature method
	 *
	 * Obtain the algorithm that will be used to generate/check the signature
	 * of the canonicalised and hashed <SignedInfo> element.
	 *
	 * @returns the Signature method
	 */

	signatureMethod getSignatureMethod(void)
	{return (mp_signedInfo != NULL ? 
			 mp_signedInfo->getSignatureMethod() : SIGNATURE_NONE);}
		 
	/**
	 * \brief Helper function for sub Classes.
	 *
	 * Returns the pointer to the formatter being used within the Signature
	 *
	 */

	XSECSafeBufferFormatter * getSBFormatter(void) {return mp_formatter;}

	//@}

	/** @name Resolver manipulation */
	//@{

	/**
	 * \brief Register a URIResolver 
	 *
	 * Registers a URIResolver to be used by the Signature when dereferencing
	 * a URI in a Reference element
	 *
	 */

	void setURIResolver(XSECURIResolver * resolver);

	/**
	 * \brief Return a pointer to the resolver being used
	 *
	 * @returns A pointer to the URIResolver registered in this signature
	 */

	XSECURIResolver * getURIResolver(void);

	/**
	 * \brief Register a KeyInfoResolver 
	 *
	 * Registers a KeyInfoResolver to be used by the Signature when 
	 * it needs to find a key to be used to validate a signature
	 *
	 */

	void setKeyInfoResolver(XSECKeyInfoResolver * resolver);

	/**
	 * \brief Return a pointer to the resolver being used
	 *
	 * @returns A pointer to the KeyInfoResolver registered in this signature
	 */

	XSECKeyInfoResolver * getKeyInfoResolver(void);

	//@}

	/** @name KeyInfo Element Manipulation */
	
	//@{

	/**
	 * \brief Get the list of <KeyInfo> elements.
	 *
	 * <p>This function recovers list that contains the KeyInfo elements
	 * read in from the DOM document.</p>
	 *
	 * <p>This list should be used by calling applications to determine what key
	 * is appropriate for validating (or even signing) the Signature.</p>
	 *
	 * @todo The KeyInfo process is very primitive.  An interface needs to be
	 * created to allow application developers to install an object into the Signature
	 * that the Signature can call on to translate KeyInfo lists into a Key.
	 * @returns A pointer to the DSIGKeyInfoList object held by the DSIGSignature
	 */
	
	DSIGKeyInfoList * getKeyInfoList() {return &m_keyInfoList;}

	/**
	 * \brief Clear out all KeyInfo elements in the signature.
	 *
	 * This function will delete all KeyInfo elements from both the DSIGSignature
	 * object <em>and the associated DOM</em>.
	 *
	 */

	void clearKeyInfo(void);

	/**
	 * \brief Append a DSA KeyValue element 
	 *
	 * Add a new KeyInfo element for a DSA Value
	 *
	 * @param P Base64 encoded value of P
	 * @param Q Base64 encoded value of Q
	 * @param G Base64 encoded value of G
	 * @param Y Base64 encoded value of Y
	 * @returns A pointer to the created object.
	 */

	DSIGKeyInfoValue * appendDSAKeyValue(const XMLCh * P, 
						   const XMLCh * Q, 
						   const XMLCh * G, 
						   const XMLCh * Y);

	/**
	 * \brief Append a RSA KeyValue element 
	 *
	 * Add a new KeyInfo element for a RSA Value
	 *
	 * @param modulus Base64 encoded value of the modulus
	 * @param exponent Base64 encoded value of exponent
	 * @returns A pointer to the created object.
	 */

	DSIGKeyInfoValue * appendRSAKeyValue(const XMLCh * modulus, 
						   const XMLCh * exponent);

	/**
	 * \brief Append a X509Data element.
	 *
	 * Add a new KeyInfo element for X509 data.
	 *
	 * @note The added element is empty.  The caller must make use of the
	 * returned object to set the required values.
	 *
	 * @returns A pointer to the created object.
	 */

	DSIGKeyInfoX509 * appendX509Data(void);

	/**
	 * \brief Append a KeyName element.
	 *
	 * Add a new KeyInfo element for a key name.
	 *
	 * @param name The name of the key to set in the XML
	 * @returns A pointer to the created object
	 */

	DSIGKeyInfoName * appendKeyName(const XMLCh * name);

	//@}

	friend class XSECProvider;

private:

	// Internal functions
	void createKeyInfoElement(void);
	bool verifySignatureOnlyInternal(void);

	XSECSafeBufferFormatter		* mp_formatter;
	bool						m_loaded;				// Have we already loaded?
	DOMDocument					* mp_doc;
	DOMNode						* mp_sigNode;
	DSIGSignedInfo				* mp_signedInfo;
	DOMNode						* mp_signatureValueNode;
	safeBuffer					m_signatureValueSB;
	DSIGKeyInfoList				m_keyInfoList;
	DOMNode						* mp_KeyInfoNode;
	safeBuffer					m_errStr;

	// For creating functions
	XMLCh 						* mp_prefixNS;
	XMLCh						* mp_ecPrefixNS;
	XMLCh						* mp_xpfPrefixNS;

	// The signing/verifying key
	XSECCryptoKey				* mp_signingKey;

	// Resolvers
	XSECURIResolver				* mp_URIResolver;
	XSECKeyInfoResolver			* mp_KeyInfoResolver;

	// Not implemented constructors

	DSIGSignature();

	/*\@}*/
};

#endif /* DSIGSIGNATURE_INCLUDE */
