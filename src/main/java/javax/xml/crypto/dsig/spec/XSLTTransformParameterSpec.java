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
package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.XMLStructure;

/**
 * Parameters for the <a href="http://www.w3.org/TR/1999/REC-xslt-19991116">
 * XSLT Transform Algorithm</a>.
 * The parameters include a namespace-qualified stylesheet element.
 *
 * <p>An <code>XSLTTransformParameterSpec</code> is instantiated with a
 * mechanism-dependent (ex: DOM) stylesheet element. For example:
 * <pre>
 *   DOMStructure stylesheet = new DOMStructure(element)
 *   XSLTTransformParameterSpec spec = new XSLTransformParameterSpec(stylesheet);
 * </pre>
 * where <code>element</code> is an {@link org.w3c.dom.Element} containing
 * the namespace-qualified stylesheet element.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see Transform
 */
public final class XSLTTransformParameterSpec implements TransformParameterSpec{
    private XMLStructure stylesheet;

    /**
     * Creates an <code>XSLTTransformParameterSpec</code> with the specified 
     * stylesheet.
     *
     * @param stylesheet the XSLT stylesheet to be used
     * @throws NullPointerException if <code>stylesheet</code> is 
     *    <code>null</code>
     */
    public XSLTTransformParameterSpec(XMLStructure stylesheet) {
        if (stylesheet == null) {
            throw new NullPointerException();
        }
        this.stylesheet = stylesheet;
    }

    /**
     * Returns the stylesheet.
     *
     * @return the stylesheet
     */
    public XMLStructure getStylesheet() {
        return stylesheet;
    }
}
