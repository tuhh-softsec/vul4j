/* $Id: DigesterLoadingException.java,v 1.9 2004/05/10 06:30:08 skitching Exp $
 *
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
 * @since 1.2
 */

public class DigesterLoadingException extends Exception {

    private Throwable cause = null;

    /**
     * @param msg a String detailing the reason for the exception
     */
    public DigesterLoadingException(String msg) {
        super(msg);
    }

    /**
     * @param cause underlying exception that caused this to be thrown
     */
    public DigesterLoadingException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    /**
     * @param msg a String detailing the reason for the exception
     * @param cause underlying exception that caused this to be thrown
     */
    public DigesterLoadingException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

}
