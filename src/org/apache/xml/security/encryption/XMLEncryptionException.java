/*
 * Copyright  2003-2004 The Apache Software Foundation.
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
package org.apache.xml.security.encryption;

import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 * 
 */
public class XMLEncryptionException extends XMLSecurityException {
	/**
     * 
	 *
	 */
   public XMLEncryptionException() {
      super();
   }
   /**
    * 
    * @param _msgID
    */
   public XMLEncryptionException(String _msgID) {
      super(_msgID);
   }
   /**
    * 
    * @param _msgID
    * @param exArgs
    */
   public XMLEncryptionException(String _msgID, Object exArgs[]) {
      super(_msgID, exArgs);
   }
   /**
    * 
    * @param _msgID
    * @param _originalException
    */
   public XMLEncryptionException(String _msgID,
                                              Exception _originalException) {
      super(_msgID, _originalException);
   }
   /**
    * 
    * @param _msgID
    * @param exArgs
    * @param _originalException
    */
   public XMLEncryptionException(String _msgID, Object exArgs[],
                                              Exception _originalException) {
      super(_msgID, exArgs, _originalException);
   }
}
