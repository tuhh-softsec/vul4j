/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


package org.apache.commons.digester.xmlrules;


/**
 * Thrown when an error occurs while parsing XML into Digester rules.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 */
public class XmlLoadException extends RuntimeException {

    private Throwable cause = null;

    /**
     * @param cause underlying exception that caused this to be thrown
     */
    public XmlLoadException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public XmlLoadException(String msg) {
        super(msg);
    }

    public XmlLoadException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    /** 
     * Returns the cause of this throwable or null if the cause is 
     * nonexistent or unknown. 
     */
    public Throwable getCause() {
        return cause;
    }
}
