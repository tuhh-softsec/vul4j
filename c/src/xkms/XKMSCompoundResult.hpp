/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XKMSCompoundResult := Interface for CompoundResult Messages
 *
 * $Id$
 *
 */

#ifndef XKMSCOMPOUNDRESULT_INCLUDE
#define XKMSCOMPOUNDRESULT_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSResultType.hpp>

class XKMSLocateResult;
class XKMSLocateRequest;
class XKMSValidateResult;
class XKMSStatusResult;
class XKMSStatusRequest;
class XKMSValidateRequest;
class XKMSRegisterRequest;
class XKMSRegisterResult;
class XKMSResult;
class XKMSRequestAbstractType;

/**
 * @ingroup xkms
 * @{
 */

/**
 * @brief Interface definition for the CompoundResult elements
 *
 * The \<CompoundResult\> is used to merge a number of separate requests
 * into a single message.  It is a very simple type - simply a holding
 * Result that has all the sub-Results held within it.
 *
 * The schema definition for CompoundResult is as follows :
 *
 * \verbatim
   <!-- CompoundResult -->
   <element name="CompoundResult" type="xkms:CompoundResultType"/>
   <complexType name="CompoundResultType">
      <complexContent>
         <extension base="xkms:ResultType">
            <choice minOccurs="0" maxOccurs="unbounded">
               <element ref="xkms:LocateResult"/>
               <element ref="xkms:ValidateResult"/>
               <element ref="xkms:RegisterResult"/>
               <element ref="xkms:ReissueResult"/>
               <element ref="xkms:RecoverResult"/>
               <element ref="xkms:RevokeResult"/>
            </choice>
         </extension>
      </complexContent>
   </complexType>
   <!-- /CompoundResult -->
\endverbatim
 */

class XKMSCompoundResult : public XKMSResultType {

	/** @name Constructors and Destructors */
	//@{

protected:

	XKMSCompoundResult() {};

public:

	virtual ~XKMSCompoundResult() {};

	/** @name Getter Interface Methods */
	//@{

	/**
	 * \brief Return the element at the base of the message
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const = 0;

	/**
	 * \brief Obtain the number of results within the compound result
	 *
	 * This class holds a list of results that can be accessed using the
	 * getResultListItem method.  This method allows an application to determine
	 * how many result items there are.
	 *
	 * @returns The size of the Result list
	 */

	virtual int getResultListSize(void) = 0;

	/**
	 * \brief Obtain a Result item
	 * 
	 * Obtain a particular Result from the list of Results held in this
	 * compound object
	 *
	 * @returns The nominated item
	 */
	 
	 virtual XKMSResultType * getResultListItem(int item) = 0;

	//@}

	/** @name Setter Interface Methods */
	//@{

	/**
	 * \brief Create a new \<LocateResult\> message.
	 * 
	 * Generates a new LocateResult message from scratch, building the DOM
	 * as it goes.  The response will be based on a input LocateRequest message
	 * which is used to provide Id etc.
	 *
	 * @param request Request to base response on
	 * @param id Value to set in the Id field.  If NULL, the library will
	 * generate a new Unique Id value.
	 * @returns the new XKMSLocateResponse structure
	 */

	virtual XKMSLocateResult * createLocateResult(
		XKMSLocateRequest * request,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin = XKMSResultType::NoneMinor,
		const XMLCh * id = NULL) = 0;

	/**
	 * \brief Create a new \<ValidateResult\> message.
	 * 
	 * Generates a new ValidateResult message from scratch, building the DOM
	 * as it goes.  The response will be based on a input ValidateRequest message
	 * which is used to provide Id etc.
	 *
	 * @param request Request to base response on
	 * @param id Value to set in the Id field.  If NULL, the library will
	 * generate a new Unique Id value.
	 * @returns the new XKMSLocateResponse structure
	 */

	virtual XKMSValidateResult * createValidateResult(
		XKMSValidateRequest * request,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin = XKMSResultType::NoneMinor,
		const XMLCh * id = NULL) = 0;

	/**
	 * \brief Create a new \<StatusResult\> message.
	 * 
	 * Generates a new StatusResult message from scratch, building the DOM
	 * as it goes.  The response will be based on a input StatusRequest message
	 * which is used to provide Id etc.
	 *
	 * @param request Request to base response on
	 * @param id Value to set in the Id field.  If NULL, the library will
	 * generate a new Unique Id value.
	 * @returns the new XKMSStatusResponse structure
	 */

	virtual XKMSStatusResult * createStatusResult(
		XKMSStatusRequest * request,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin = XKMSResultType::NoneMinor,
		const XMLCh * id = NULL) = 0;
		

	/**
	 * \brief Create a new \<RegisterResult\> message.
	 * 
	 * Generates a new RegisterResult message from scratch, building the DOM
	 * as it goes.  The response will be based on a input RegisterRequest message
	 * which is used to provide Id etc.
	 *
	 * @param request Request to base response on
	 * @param id Value to set in the Id field.  If NULL, the library will
	 * generate a new Unique Id value.
	 * @returns the new XKMSRegisterResult structure
	 */

	virtual XKMSRegisterResult * createRegisterResult(
		XKMSRegisterRequest * request,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin = XKMSResultType::NoneMinor,
		const XMLCh * id = NULL) = 0;

	/**
	 * \brief Create a new \<Result\> message.
	 * 
	 * Generates a new Result message from scratch, building the DOM
	 * as it goes.  The response will be based on an input Request message
	 * which is used to provide Id etc.
	 *
	 * @param request Request to base response on
	 * @param id Value to set in the Id field.  If NULL, the library will
	 * generate a new Unique Id value.
	 * @returns the new XKMSResult structure
	 */

	virtual XKMSResult * createResult(
		XKMSRequestAbstractType * request,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin = XKMSResultType::NoneMinor,
		const XMLCh * id = NULL) = 0;
	 
	 //@}

private:

	// Unimplemented
	XKMSCompoundResult(const XKMSCompoundResult &);
	XKMSCompoundResult & operator = (const XKMSCompoundResult &);

};

#endif /* XKMSCOMPOUNDRESULT_INCLUDE */
