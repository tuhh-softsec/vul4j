/*
 * Copyright 2005-2009 The Apache Software Foundation.
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
package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;

/**
 * This exception is thrown when a particular XML mechanism is requested but
 * is not available in the environment.
 *
 * <p>A <code>NoSuchMechanismException</code> can contain a cause: another 
 * throwable that caused this <code>NoSuchMechanismException</code> to get 
 * thrown. 
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#getInstance XMLSignatureFactory.getInstance
 * @see KeyInfoFactory#getInstance KeyInfoFactory.getInstance
 */
public class NoSuchMechanismException extends RuntimeException {

    private static final long serialVersionUID = 4189669069570660166L;

    /**
     * The throwable that caused this exception to get thrown, or null if this
     * exception was not caused by another throwable or if the causative
     * throwable is unknown. 
     *
     * @serial
     */
    private Throwable cause;

    /**
     * Constructs a new <code>NoSuchMechanismException</code> with 
     * <code>null</code> as its detail message.
     */
    public NoSuchMechanismException() {
        super();
    }

    /**
     * Constructs a new <code>NoSuchMechanismException</code> with the 
     * specified detail message. 
     *
     * @param message the detail message
     */
    public NoSuchMechanismException(String message) {
        super(message);
    }

    /**
     * Constructs a new <code>NoSuchMechanismException</code> with the 
     * specified detail message and cause.  
     * <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message 
     * @param cause the cause (A <tt>null</tt> value is permitted, and 
     *	      indicates that the cause is nonexistent or unknown.)
     */
    public NoSuchMechanismException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructs a new <code>NoSuchMechanismException</code> with the 
     * specified cause and a detail message of 
     * <code>(cause==null ? null : cause.toString())</code> (which typically 
     * contains the class and detail message of <code>cause</code>).
     *
     * @param cause the cause (A <tt>null</tt> value is permitted, and 
     *        indicates that the cause is nonexistent or unknown.)
     */
    public NoSuchMechanismException(Throwable cause) {
        super(cause==null ? null : cause.toString());
        this.cause = cause;
    }

    /**
     * Returns the cause of this <code>NoSuchMechanismException</code> or 
     * <code>null</code> if the cause is nonexistent or unknown.  (The 
     * cause is the throwable that caused this 
     * <code>NoSuchMechanismException</code> to get thrown.)
     *
     * @return the cause of this <code>NoSuchMechanismException</code> or 
     *         <code>null</code> if the cause is nonexistent or unknown.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Prints this <code>NoSuchMechanismException</code>, its backtrace and
     * the cause's backtrace to the standard error stream.
     */
    public void printStackTrace() {
        super.printStackTrace();
        //XXX print backtrace of cause
    }

    /**
     * Prints this <code>NoSuchMechanismException</code>, its backtrace and
     * the cause's backtrace to the specified print stream.
     *
     * @param s <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        //XXX print backtrace of cause
    }

    /**
     * Prints this <code>NoSuchMechanismException</code>, its backtrace and
     * the cause's backtrace to the specified print writer.
     *
     * @param s <code>PrintWriter</code> to use for output
     */
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        //XXX print backtrace of cause
    }
}
