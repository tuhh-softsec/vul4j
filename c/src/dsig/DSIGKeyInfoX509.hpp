/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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
 * DSIGKeyInfoX509 := A "Super" key that defines a certificate with a sub-key that defines
 *                the signing key
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$ *
 */

#ifndef DSIGKEYINFOX509_INCLUDE
#define DSIGKEYINFOX509_INCLUDE

#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/enc/XSECCryptoX509.hpp>

#include <vector>

XSEC_USING_XERCES(DOMElement);

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief The class for \<X509Data\> nodes in a KeyInfo list.
 *
 * Class for holding information on a X509Data node as well as setting
 * such a node in a signature.
 *
 */


class DSIG_EXPORT DSIGKeyInfoX509 : public DSIGKeyInfo {

public:


	struct X509Holder {

		const XMLCh			* mp_encodedX509;		// Base64 encoding
		XSECCryptoX509		* mp_cryptoX509;		// The certificate

	};

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<X509Holder *>			X509ListType;
#else
	typedef std::vector<X509Holder *>		X509ListType;
#endif

#if defined(XSEC_SIZE_T_IN_NAMESPACE_STD)
	typedef std::size_t		size_type;
#else
	typedef size_t			size_type;
#endif

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor used when XML structures exist.
	 *
	 * Constructor called by DSIGSignature class when loading an
	 * X509Data element from DOM nodes.
	 *
	 * @param sig Calling signature
	 * @param X509Data DOMNode at start of data
	 */

	DSIGKeyInfoX509(DSIGSignature * sig, DOMNode *X509Data);

	/**
	 * \brief Constructor called when building XML structures
	 *
	 * Constructor called by DSIGSignature class when an XML
	 * structure is being built by a user calling the API
	 * 
	 * @param sig Calling signature
	 */

	DSIGKeyInfoX509(DSIGSignature * sig);
	
	/**
	 * \brief Destructor
	 */

	virtual ~DSIGKeyInfoX509();

	//@}

	/** @name Load function and get methods */
	//@{

	/**
	 * \brief Function called to load an XML structure
	 *
	 * Function called by DSIGSignature to load an X509Data structure
	 * from DOMNodes.
	 */

	virtual void load();

	/**
	 * \brief Get the name of the certificate
	 *
	 * Get the name stored in the X509SubjectName element (if it
	 * exists).
	 *
	 * @returns A pointer to a buffer containing the name
	 * (NULL if not set.)
	 */

	const XMLCh * getX509SubjectName(void);

	/**
	 * \brief Get the name of the certificate (interface function)
	 *
	 * Get the name stored in the X509SubjectName element (if it
	 * exists).
	 *
	 * @returns A pointer to the buffer containing the name (or NULL if not set).
	 * The decoded string is returned.
	 * @see setX509SubjectName(void)
	 */

	const XMLCh * getKeyName(void);
	
	/**
	 * \brief Get the IssuerSerialName
	 *
	 * Get the name of the Issuer (stored in the X509IssuerSerial element).
	 *
	 * @returns A pointer to the buffer containing the issuer name.
	 * (0 if not set.)
	 */

	const XMLCh * getX509IssuerName(void);

	/**
	 * \brief Get the IsserSerialNumber
	 *
	 * Get the serial number of the certificate of the issuer of the
	 * signing certificate.
	 * 
	 * @returns A pointer to the string containing the IssuerSerialNumber.
	 * (0 if not set.)  This is the decoded string.
	 * @see setX509IssuerSerial
	 */

	const XMLCh * getX509IssuerSerialNumber(void);

	/**
	 * \brief Get any associated CRL
	 *
	 * Return the string containing the base64 encoded CRL that was held in
	 * the X509CRL node
	 *
	 * @returns A pointer to the string containing the CRL
	 * (0 if not set)
	 */

	const XMLCh * getX509CRL(void);

	/**
	 * \brief Get the SKI value (if set)
	 *
	 * The SKI (Subject Key Identifier) can be used to reference a
	 * required certificate.  If this was set in the KeyInfo element,
	 * this function will return the value
	 *
	 * @returns the base64 encoded (plan - not DER) encoded SKI value
	 */

	const XMLCh * getX509SKI(void);

	/**
	 * \brief Return the raw Retrieval method to find this certificate
	 *
	 * In some cases, the KeyInfo RetrievalMethod references a raw certificate
	 * In such cases, the library simply creates a KeyInfoX509 object and sets
	 * this string to allow others to find the certificate
	 */

	const XMLCh * getRawRetrievalURI(void);

	/**
	 * \brief Find the number of certificates held
	 *
	 * Find the number of certificates held in the X509Data structures.
	 *
	 * @returns The number of certificates
	 */

	int getCertificateListSize(void);

	/**
	 * \brief Get the DER encoded certificate pointed to in the list.
	 *
	 * Use the index to find the required certificate and return a pointer
	 * to the buffer containing the encoded certificate.
	 *
	 * @returns A pointer to the buffer containing the certificate or 0 if
	 * no certificate exists at that point in the list.
	 */

	const XMLCh * getCertificateItem(int item);

	/**
	 * \brief Interface function to find the type of this KeyInfo
	 */
	
	virtual keyInfoType getKeyInfoType(void) {return DSIGKeyInfo::KEYINFO_X509;}

	//@}

	/** @name Create and Set functions */
	//@{

	/**
	 * \brief Create a new X509 data element.
	 *
	 * Create a blank (empty) X509Data element that can then be used
	 * by the application to add X509Data elements.
	 *
	 * @returns A pointer to the new X509Data element.
	 */

	DOMElement * createBlankX509Data(void);

	/**
	 * \brief Set the X509SubjectName element in the KeyInfo element.
	 *
	 * If a X509SubjectName element exists, replace the text with the
	 * provided text.  Otherwise create the element and set the text.
	 *
	 * @note XMLDSIG requires Distinguished Names be encoded in a defined
	 * manner (escaping '<' characters etc.).  This method will perform
	 * this encoding prior to creating the DOM nodes.
	 *
	 * @param name The name to set.
	 */

	void setX509SubjectName(const XMLCh * name);

	/**
	 * \brief Set the IssuerSerial element
	 *
	 * If an X509IssuerSerial exists, replace the values with those provided,
	 * otherwise create a new element and set the values appropriately.
	 *
	 * @note XMLDSIG requires Distinguished Names be encoded in a defined
	 * manner (escaping '<' characters etc.).  This method will perform
	 * this encoding prior to creating the DOM nodes.
	 *
	 * @param name The name of the issuer.
	 * @param serial The serial number of the issuer's certificate
	 */

	void setX509IssuerSerial(const XMLCh * name, const XMLCh * serial);

	/**
	 * \brief Set the CRL element
	 *
	 * If an X509CRL exists, replace the value with that provided,
	 * otherwise create a new element and set the value appropriately.
	 *
	 * @param crl The base64 encoded string containing the CRL
	 */

	void setX509CRL(const XMLCh * crl);
	
	/**
	 * \brief Set the SKI element
	 *
	 * The SKI node provides a reference to the Subject Key Identifier of
	 * a certificate.
	 *
	 * This function takes a base64 encoded ski and sets it into the 
	 * appropriate node
	 *
	 * @param ski The base64 plain (non-DER) encoded SKI value
	 */

	void setX509SKI(const XMLCh * ski);

	/**
	 * \brief set the retrieval URI
	 *
	 * Generally to be used by internal library processes only.
	 * This sets the retrieval URI - but does _not_ manipulate the DOM
	 * in any way.
	 *
	 * @param uri The URI string to use
	 */

	void setRawRetrievalURI(const XMLCh * uri);

	/**
	 * \brief Add a certificate.
	 *
	 * Append an X509Certificate element to the list of certificates
	 * stored at the end of this KeyInfo element.
	 *
	 * @param base64Certificate A pointer to the base64 encoded certificate,
	 * exactly as it will appear in the XML structure.
	 */

	void appendX509Certificate(const XMLCh * base64Certificate);

	//@}


private:

	DSIGKeyInfoX509();

	X509ListType		m_X509List;				// The X509 structures
	XMLCh 				* mp_X509IssuerName;	// Parameters from KeyInfo (not cert)
	const XMLCh 		* mp_X509SerialNumber;
	XMLCh 				* mp_X509SubjectName;
	const XMLCh			* mp_X509CRL;
	const XMLCh			* mp_X509SKI;
	XMLCh				* mp_rawRetrievalURI;

	// Text nodes holding information

	DOMNode				* mp_X509SubjectNameTextNode;
	DOMNode				* mp_X509IssuerNameTextNode;
	DOMNode				* mp_X509SerialNumberTextNode;
	DOMNode				* mp_X509CRLTextNode;
	DOMNode				* mp_X509SKITextNode;

};



#endif /* #define DSIGKEYX509_INCLUDE */
