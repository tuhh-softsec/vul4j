/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package org.apache.xml.security.utils;



import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;


/**
 * First approach how the validity of certificates can be assured. This would
 * be the way to integrate things like CRL checks etc.
 *
 * @author $Author$
 */
public class X509CertificateValidator {

   /**
    * Method validate
    *
    * @param cert
    * @throws CertificateExpiredException
    * @throws CertificateNotYetValidException
    */
   public static void validate(X509Certificate cert)
           throws CertificateNotYetValidException, CertificateExpiredException {
      cert.checkValidity();
   }
}
