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
package org.apache.xml.security.exceptions;



import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.I18n;


/**
 * The mother of all Exceptions in this bundle. It allows exceptions to have
 * their messages translated to the different locales.
 *
 * The <code>xmlsecurity_en.properties</code> file contains this line:
 * <pre>
 * xml.WrongElement = Can't create a {0} from a {1} element
 * </pre>
 *
 * Usage in the Java source is:
 * <pre>
 * {
 *    Object exArgs[] = { Constants._TAG_TRANSFORMS, "BadElement" };
 *
 *    throw new XMLSecurityException("xml.WrongElement", exArgs);
 * }
 * </pre>
 *
 * Additionally, if another Exception has been caught, we can supply it, too>
 * <pre>
 * try {
 *    ...
 * } catch (Exception oldEx) {
 *    Object exArgs[] = { Constants._TAG_TRANSFORMS, "BadElement" };
 *
 *    throw new XMLSecurityException("xml.WrongElement", exArgs, oldEx);
 * }
 * </pre>
 *
 *
 * @author Christian Geuer-Pollmann
 */
public class XMLSecurityException extends Exception {

   /** Field originalException */
   protected Exception originalException = null;

   /** Field msgID */
   protected String msgID;

   /**
    * Constructor XMLSecurityException
    *
    */
   public XMLSecurityException() {

      super("Missing message string");

      this.msgID = null;
      this.originalException = null;
   }

   /**
    * Constructor XMLSecurityException
    *
    * @param msgID
    */
   public XMLSecurityException(String msgID) {

      super(I18n.getExceptionMessage(msgID));

      this.msgID = msgID;
      this.originalException = null;
   }

   /**
    * Constructor XMLSecurityException
    *
    * @param msgID
    * @param exArgs
    */
   public XMLSecurityException(String msgID, Object exArgs[]) {

      super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));

      this.msgID = msgID;
      this.originalException = null;
   }

   /**
    * Constructor XMLSecurityException
    *
    * @param originalException
    */
   public XMLSecurityException(Exception originalException) {

      super("Missing message ID to locate message string in resource bundle \""
            + Constants.exceptionMessagesResourceBundleBase
            + "\". Original Exception was a "
            + originalException.getClass().getName() + " and message "
            + originalException.getMessage());

      this.originalException = originalException;
   }

   /**
    * Constructor XMLSecurityException
    *
    * @param msgID
    * @param originalException
    */
   public XMLSecurityException(String msgID, Exception originalException) {

      super(I18n.getExceptionMessage(msgID, originalException));

      this.msgID = msgID;
      this.originalException = originalException;
   }

   /**
    * Constructor XMLSecurityException
    *
    * @param msgID
    * @param exArgs
    * @param originalException
    */
   public XMLSecurityException(String msgID, Object exArgs[],
                               Exception originalException) {

      super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));

      this.msgID = msgID;
      this.originalException = originalException;
   }

   /**
    * Method getMsgID
    *
    * @return
    */
   public String getMsgID() {

      if (msgID == null) {
         return "Missing message ID";
      } else {
         return msgID;
      }
   }

   /**
    * Method toString
    *
    * @return
    */
   public String toString() {

      String s = this.getClass().getName();
      String message = super.getLocalizedMessage();

      if (message != null) {
         message = s + ": " + message;
      } else {
         message = s;
      }

      if (originalException != null) {
         message = message + "\nOriginal Exception was "
                   + originalException.toString();
      }

      return message;
   }

   /**
    * Method printStackTrace
    *
    */
   public void printStackTrace() {

      synchronized (System.err) {
         super.printStackTrace(System.err);

         if (this.originalException != null) {
            this.originalException.printStackTrace(System.err);
         }
      }
   }

   /**
    * Method printStackTrace
    *
    * @param printwriter
    */
   public void printStackTrace(PrintWriter printwriter) {

      super.printStackTrace(printwriter);

      if (this.originalException != null) {
         this.originalException.printStackTrace(printwriter);
      }
   }

   /**
    * Method printStackTrace
    *
    * @param printstream
    */
   public void printStackTrace(PrintStream printstream) {

      super.printStackTrace(printstream);

      if (this.originalException != null) {
         this.originalException.printStackTrace(printstream);
      }
   }

   /**
    * Method getOriginalException
    *
    * @return
    */
   public Exception getOriginalException() {
      return originalException;
   }
}
