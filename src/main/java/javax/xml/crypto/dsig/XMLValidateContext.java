/*
 * Copyright 2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import javax.xml.crypto.XMLCryptoContext;

/**
 * Contains context information for validating XML Signatures. This interface
 * is primarily intended for type-safety.
 *
 * <p>Note that <code>XMLValidateContext</code> instances can contain 
 * information and state specific to the XML signature structure it is 
 * used with. The results are unpredictable if an 
 * <code>XMLValidateContext</code> is used with different signature structures 
 * (for example, you should not use the same <code>XMLValidateContext</code> 
 * instance to validate two different {@link XMLSignature} objects).
 * <p>
 * <b><a name="Supported Properties"></a>Supported Properties</b>
 * <p>The following properties can be set by an application using the 
 * {@link #setProperty setProperty} method.
 * <ul>
 *   <li><code>javax.xml.crypto.dsig.cacheReference</code>: value must be a
 *	{@link Boolean}. This property controls whether or not the
 *	{@link Reference#validate Reference.validate} method will cache the 
 *	dereferenced content and pre-digested input for subsequent retrieval via
 *      the {@link Reference#getDereferencedData Reference.getDereferencedData}
 *	and {@link Reference#getDigestInputStream 
 *	Reference.getDigestInputStream} methods. The default value if not 
 *	specified is <code>Boolean.FALSE</code>.
 * </ul>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignature#validate(XMLValidateContext)
 * @see Reference#validate(XMLValidateContext)
 */
public interface XMLValidateContext extends XMLCryptoContext {}
