/* $Id: VariableExpander.java,v 1.6 2004/05/10 06:46:31 skitching Exp $
 *
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.digester.substitution;

/**
 * <p>An Interface describing a class capable of expanding strings which
 * may contain variable references. The exact syntax of the "reference",
 * and the mechanism for determining the corresponding value to be used
 * is up to the concrete implementation.</p>
 *
 * @since 1.6
 */
public interface VariableExpander {
    /**
     * Return the input string with any variables replaced by their
     * corresponding value. If there are no variables in the string,
     * then the input parameter is returned unaltered.
     */
    public String expand(String param);
}

